package com.nethergrim.combogymdiary.tools;

import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;


public class Prefs {
    private static final String KEY_ADS_REMOVED = "ads_removed";

    private static SharedPreferences prefs;
    private static Prefs pref;

    private Prefs(Context context){
        prefs = PreferenceManager.getDefaultSharedPreferences(context);
    }

    public static Prefs init(Context context){
        if (pref == null){
            pref = new Prefs(context);
        }
        return pref;
    }

    public static Prefs getPreferences(){
        return pref;
    }

    public void setAdsRemoved(boolean removed){
        prefs.edit().putBoolean(KEY_ADS_REMOVED, removed).apply();
    }

    public boolean getAdsRemoved(){
        return prefs.getBoolean(KEY_ADS_REMOVED, false);
    }



}
