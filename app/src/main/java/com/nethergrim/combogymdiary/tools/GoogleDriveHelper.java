package com.nethergrim.combogymdiary.tools;

import android.content.Context;
import android.os.Bundle;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.ResultCallback;
import com.google.android.gms.drive.Drive;
import com.google.android.gms.drive.DriveApi;
import com.google.android.gms.drive.DriveFolder;
import com.google.android.gms.drive.DriveId;
import com.google.android.gms.drive.MetadataBuffer;
import com.google.android.gms.drive.MetadataChangeSet;
import com.google.android.gms.drive.query.Filters;
import com.google.android.gms.drive.query.Query;
import com.google.android.gms.drive.query.SearchableField;
import com.yandex.metrica.Counter;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.OutputStream;
import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * Created by Andrey Drobyazko on 25.08.2014.
 */
public class GoogleDriveHelper implements
        GoogleApiClient.ConnectionCallbacks,
        GoogleApiClient.OnConnectionFailedListener {

    private Context context;
    static final String DRIVE_FOLDER_NAME = "Workout Diary Backups";
    static DriveId FOLDER_DRIVE_ID;
    private String fileTitle;
    private GoogleApiClient mGoogleApiClient;

    public GoogleDriveHelper(Context context) {
        this.context = context;
    }

    public void autoBackup() {
        SimpleDateFormat sdf = new SimpleDateFormat("dd.MM.yyyy");
        String date = sdf.format(new Date(System.currentTimeMillis()));
        fileTitle = "Trainings backup " + date + " .db";
        if (mGoogleApiClient == null) {
            mGoogleApiClient = new GoogleApiClient.Builder(context)
                    .addApi(Drive.API).addScope(Drive.SCOPE_FILE)
                    .addScope(Drive.SCOPE_APPFOLDER)
                            // required for App Folder sample
                    .addConnectionCallbacks(this)
                    .addOnConnectionFailedListener(this).build();
        }
        mGoogleApiClient.connect();
    }

    private GoogleApiClient getGoogleApiClient() {
        return this.mGoogleApiClient;
    }


    @Override
    public void onConnected(Bundle bundle) {
        Query query = new Query.Builder()
                .addFilter(
                        Filters.eq(SearchableField.MIME_TYPE,
                                "application/vnd.google-apps.folder")
                )
                .addFilter(Filters.eq(SearchableField.TITLE, DRIVE_FOLDER_NAME))
                .addFilter(Filters.eq(SearchableField.TRASHED, false)).build();

        // search for a folder named "Workout Diary Backups" in Google Drive
        Drive.DriveApi.query(getGoogleApiClient(), query).setResultCallback(
                folderQueryCallback);
    }

    @Override
    public void onConnectionSuspended(int i) {

    }

    @Override
    public void onConnectionFailed(ConnectionResult connectionResult) {

    }

    final private ResultCallback<DriveApi.MetadataBufferResult> folderQueryCallback = new ResultCallback<DriveApi.MetadataBufferResult>() {
        @Override
        public void onResult(DriveApi.MetadataBufferResult result) {
            if (!result.getStatus().isSuccess()) {
                return;
            }
            MetadataBuffer mdb = result.getMetadataBuffer();
            if (mdb.getCount() == 0) {
                // create a new folder
                MetadataChangeSet changeSet = new MetadataChangeSet.Builder()
                        .setTitle(DRIVE_FOLDER_NAME).build();
                Drive.DriveApi.getRootFolder(getGoogleApiClient())
                        .createFolder(getGoogleApiClient(), changeSet)
                        .setResultCallback(folderCreatedCallback);
            } else {
                // get folder DriveID and proceed
                FOLDER_DRIVE_ID = mdb.get(0).getDriveId();
                goIntoFolder();
            }
        }
    };

    final private ResultCallback<DriveFolder.DriveFolderResult> folderCreatedCallback = new ResultCallback<DriveFolder.DriveFolderResult>() {
        @Override
        public void onResult(DriveFolder.DriveFolderResult result) {
            if (!result.getStatus().isSuccess()) {
                return;
            }
            // new folder DriveID
            FOLDER_DRIVE_ID = result.getDriveFolder().getDriveId();
            goIntoFolder();
        }
    };

    private void goIntoFolder() {
        // here we already have DriveId for a folder
        // next step is backup DB file to a folder


        // automatic backup
        Drive.DriveApi.newContents(getGoogleApiClient()).setResultCallback(newContentsResultOnCreatingFile);

    }

    final private ResultCallback<DriveApi.ContentsResult> newContentsResultOnCreatingFile = new ResultCallback<DriveApi.ContentsResult>() {
        @Override
        public void onResult(DriveApi.ContentsResult result) {
            if (!result.getStatus().isSuccess()) {
                return;
            }

            OutputStream outputStream = result.getContents().getOutputStream();
            Backuper back = new Backuper();
            File db = back.getDbFile();

            byte[] b = new byte[(int) db.length()];
            try {
                FileInputStream fileInputStream = new FileInputStream(db);
                fileInputStream.read(b);
                for (byte aB : b) {
                    System.out.print((char) aB);
                }
                fileInputStream.close();
            } catch (FileNotFoundException e) {
                Counter.sharedInstance().reportError("", e);
            } catch (IOException e1) {
                Counter.sharedInstance().reportError("", e1);
            }

            try {
                outputStream.write(b);
            } catch (IOException e1) {
                Counter.sharedInstance().reportError("", e1);
            }

            DriveFolder folder = Drive.DriveApi.getFolder(getGoogleApiClient(),
                    FOLDER_DRIVE_ID);
            MetadataChangeSet changeSet = new MetadataChangeSet.Builder()
                    .setTitle(fileTitle).setMimeType("text/plain")
                    .setStarred(true).build();
            folder.createFile(getGoogleApiClient(), changeSet,
                    result.getContents())
                    .setResultCallback(fileCreatedCallback);
        }
    };

    final private ResultCallback<DriveFolder.DriveFileResult> fileCreatedCallback = new ResultCallback<DriveFolder.DriveFileResult>() {
        @Override
        public void onResult(DriveFolder.DriveFileResult result) {
            mGoogleApiClient.disconnect();
        }
    };
}
