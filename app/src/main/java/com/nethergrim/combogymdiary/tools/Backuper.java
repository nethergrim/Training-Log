package com.nethergrim.combogymdiary.tools;

import android.os.Environment;
import android.util.Log;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.channels.FileChannel;

public class Backuper {


    @SuppressWarnings("resource")
    public boolean backupToSd() {
        try {
            File sd = Environment.getExternalStorageDirectory();
            File data = Environment.getDataDirectory();
            if (sd.canWrite()) {
                String currentDBPath = "//data//com.nethergrim.combogymdiary//databases//mydb";
                String backupDBPath = "Workout_diary_backup.db";

                File currentDB = new File(data, currentDBPath);
                File backupDB = new File(sd, backupDBPath);
                if (currentDB.exists()) {
                    FileChannel src = new FileInputStream(currentDB)
                            .getChannel();
                    FileChannel dst = new FileOutputStream(backupDB)
                            .getChannel();
                    dst.transferFrom(src, 0, src.size());
                    src.close();
                    dst.close();
                    return true;
                } else
                    return false;
            } else {
                return false;
            }
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    @SuppressWarnings("resource")
    public boolean restoreBackup(String path) {
        try {
            File sd1 = Environment.getExternalStorageDirectory();
            File data = Environment.getDataDirectory();
            if (sd1.canWrite()) {
                String restroredDBPath = "//data//com.nethergrim.combogymdiary//databases//mydb";
                File restoredDB = new File(data, restroredDBPath);
                File backupedDB = new File(path);
                if (backupedDB.exists()) {
                    FileChannel src = new FileInputStream(backupedDB)
                            .getChannel();
                    FileChannel dst = new FileOutputStream(restoredDB)
                            .getChannel();
                    dst.transferFrom(src, 0, src.size());
                    src.close();
                    dst.close();
                    return true;
                } else {
                    return false;
                }
            } else
            return false;
        } catch (Exception e) {
            return false;
        }

    }

    public static File getDbFile() {
        File data = Environment.getDataDirectory();
        String currentDBPath = "//data//com.nethergrim.combogymdiary//databases//mydb";

        File dataFolder = new File(data,"//data//");
        if (!dataFolder.exists()){
            try {
                Log.e("log","creating data for DB");
                dataFolder.createNewFile();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        File packageFolder = new File(data,"//data//com.nethergrim.combogymdiary//");
        if (!packageFolder.exists()){
            try {
                Log.e("log","creating packageFolder for DB");
                packageFolder.createNewFile();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        File dbFolder = new File(data, "//data//com.nethergrim.combogymdiary//databases//");
        if (!dbFolder.exists()){
            try {
                Log.e("log","creating dbFolder for DB");
                dbFolder.createNewFile();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        File result = new File(data, currentDBPath);
        if (!result.exists()){
            try {
                result.createNewFile();
                Log.e("log", "db file created");
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return result;
    }

    public static File getPreferencesFile(){
        File data = Environment.getDataDirectory();
        String currentPrefsPath = "//data//com.nethergrim.combogymdiary//shared_prefs//com.nethergrim.combogymdiary_preferences.xml";
        File result = new File(data, currentPrefsPath);
        if (!result.exists()){
            try {
                result.createNewFile();
                Log.e("log", "prefs file created");
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return result;
    }
}
