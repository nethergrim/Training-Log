package com.nethergrim.combogymdiary.tools;

import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;


public class Prefs {
    private static final String KEY_ADS_REMOVED = "ads_removed";
    private static final String KEY_SUPERSET_INFO_SHOWED = "superset_info_showed";
    public final static String KEY_TRAINING_AT_PROGRESS = "training_at_progress";
    public final static String KEY_DATABASE_FILLED = "database_filled";
    private static final String KEY_DATABASE_UPDATED_TOV5 = "db_updated_to5";

    private static SharedPreferences prefs;
    private static Prefs pref;

    private Prefs(Context context) {
        prefs = PreferenceManager.getDefaultSharedPreferences(context);
    }

    public static Prefs init(Context context) {
        if (pref == null) {
            pref = new Prefs(context);
        }
        return pref;
    }

    public static Prefs getPreferences() {
        return pref;
    }

    public void setAdsRemoved(boolean removed) {
        prefs.edit().putBoolean(KEY_ADS_REMOVED, removed).apply();
    }

    public boolean getAdsRemoved() {
        return prefs.getBoolean(KEY_ADS_REMOVED, false);
    }

    public void setSuperSetInfoShowed(int times) {
        prefs.edit().putInt(KEY_SUPERSET_INFO_SHOWED, times).apply();
    }

    public int getSuperSetInfoShowed() {
        return prefs.getInt(KEY_SUPERSET_INFO_SHOWED, 0);
    }

    public void setTrainingAtProgress(boolean atProgress) {
        prefs.edit().putBoolean(KEY_TRAINING_AT_PROGRESS, atProgress).apply();
    }

    public boolean getTrainingAtProgress() {
        return prefs.getBoolean(KEY_TRAINING_AT_PROGRESS, false);
    }

    public void setDatabaseFilled(boolean filled){
        prefs.edit().putBoolean(KEY_DATABASE_FILLED, filled).apply();
    }

    public boolean getDatabaseFilled(){
        return prefs.getBoolean(KEY_DATABASE_FILLED, false);
    }

    public boolean getDbUpdatedToV5(){
        return prefs.getBoolean(KEY_DATABASE_UPDATED_TOV5, false);
    }

    public void setDbUpdatedToV5(boolean updatedToV5){
        prefs.edit().putBoolean(KEY_DATABASE_UPDATED_TOV5, updatedToV5).apply();
    }


}
