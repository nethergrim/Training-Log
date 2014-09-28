package com.nethergrim.combogymdiary.tools;

import android.app.backup.BackupAgentHelper;
import android.app.backup.BackupManager;
import android.app.backup.FileBackupHelper;
import android.app.backup.SharedPreferencesBackupHelper;
import android.content.Context;

import com.nethergrim.combogymdiary.DB;

/**
 * Created by andrey_drobyazko on 22.08.14.
 */
public class MyBackupAgent extends BackupAgentHelper {

//    public static final String HEADER_SETTINGS = "settings_header";
//    public static final String HEADER_DB = "db_header";

//    @Override
//    public void onBackup(ParcelFileDescriptor oldState, BackupDataOutput data, ParcelFileDescriptor newState) throws IOException {
//
//        //*************************** DB BACKUP ***********************************
//        File db = Backuper.getDbFile();
//        data.writeEntityHeader(HEADER_DB, (int) db.length());
//
//        int size = (int) db.length();
//        byte[] bytes = new byte[size];
//        try {
//            BufferedInputStream buf = new BufferedInputStream(new FileInputStream(db));
//            buf.read(bytes, 0, bytes.length);
//            buf.close();
//        } catch (FileNotFoundException e) {
//            e.printStackTrace();
//        } catch (IOException e) {
//            e.printStackTrace();
//        }
//
//        data.writeEntityData(bytes,size);
//
//        //************************* PREFS BACKUP *****************************************************
//
//
//        File prefs = Backuper.getPreferencesFile();
//        data.writeEntityHeader(HEADER_SETTINGS, (int) prefs.length());
//
//        int prefsSize = (int) prefs.length();
//        byte[] prefsBytes = new byte[prefsSize];
//        try {
//            BufferedInputStream buf = new BufferedInputStream(new FileInputStream(prefs));
//            buf.read(prefsBytes, 0, prefsBytes.length);
//            buf.close();
//        } catch (FileNotFoundException e) {
//            e.printStackTrace();
//        } catch (IOException e) {
//            e.printStackTrace();
//        }
//
//        data.writeEntityData(prefsBytes,prefsSize);
//
//    }
//
//    @Override
//    public void onRestore(BackupDataInput data, int appVersionCode, ParcelFileDescriptor newState) throws IOException {
//        DB db = new DB(getApplicationContext());
//        db.open();
//        while (data.readNextHeader()) {
//            String key = data.getKey();
//            int dataSize = data.getDataSize();
//            // If the key is ours (for saving top score). Note this key was used when
//            // we wrote the backup entity header
//            if (HEADER_DB.equals(key)) {
//                Log.e("log", "restoring DB file " + dataSize);
//                byte[] dataBuf = new byte[dataSize];
//                data.readEntityData(dataBuf, 0, dataSize);
//                BufferedOutputStream bos = new BufferedOutputStream(new FileOutputStream(Backuper.getDbFile()));
//                bos.write(dataBuf);
//                bos.flush();
//                bos.close();
//            } else if (HEADER_SETTINGS.equals(key)){
//                Log.e("log", "restoring SETTINGS file " + dataSize);
//                byte[] dataBuf = new byte[dataSize];
//                data.readEntityData(dataBuf, 0, dataSize);
//                BufferedOutputStream bos = new BufferedOutputStream(new FileOutputStream(Backuper.getPreferencesFile()));
//                bos.write(dataBuf);
//                bos.flush();
//                bos.close();
//            } else {
//                // We don't know this entity key. Skip it. (Shouldn't happen.)
//                data.skipEntityData();
//            }
//        }
//        db.close();
//    }


    //метод для запроса бэкапа. Согласно документации следует вызывать этот метод всякий раз, когда данные изменились.
    public static void requestBackup(Context context) {
        BackupManager bm = new BackupManager(context);
        bm.dataChanged();
    }

    @Override
    public void onCreate() {
        // DatabaseHelper.DATABASE_NAME – константа с названием базы данных.
        FileBackupHelper fileBackupHelper = new FileBackupHelper(this, "../databases/" + DB.DB_NAME);
        addHelper(DB.DB_NAME, fileBackupHelper);
        //здесь мы просто, через запятую указываем названия файлов, в которых хранятся ваши настройки. Обратите внимание на последний параметр, в файле под таким названием хранит данные дефолтный PreferenceManager(PreferenceManager.getDefaultSharedPreferences(context))
        SharedPreferencesBackupHelper helper = new SharedPreferencesBackupHelper(this, getPackageName() + "_preferences");
        addHelper("prefs", helper);
    }


}
