package com.nethergrim.combogymdiary.util;

import android.app.backup.BackupAgentHelper;
import android.app.backup.FileBackupHelper;
import android.app.backup.SharedPreferencesBackupHelper;
import android.os.Environment;
import android.util.Log;

import java.io.File;

/**
 * Created by andrey_drobyazko on 22.08.14.
 */
public class MyBackupAgent extends BackupAgentHelper {


    public static String SETTINGS = "settings";
    private static final String DB_NAME = "mydb";
    private static final String PREFS_NAME = "com.nethergrim.combogymdiary_preferences";

    // /data/data/com.nethergrim.combogymdiary/databases



    @Override
    public void onCreate() {
        File source = new File(getFilesDir().getParentFile().toString() + "/shared_prefs/" + PREFS_NAME + ".xml") ;
        Log.e("log", "source: " + source.toString());


        File destination = new File(getFilesDir().toString() + "settings");
        Log.e("log", "destination: " + destination.toString());






//        FileBackupHelper helper = new FileBackupHelper(this, DB_NAME , PREFS_NAME);
//        addHelper(SETTINGS, helper);
    }

    @Override
    public File getFilesDir(){
        File path = getDatabasePath(DB_NAME);
        return path.getParentFile();
    }



}
