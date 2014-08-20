package com.nethergrim.combogymdiary;

import android.app.Application;

import com.nethergrim.combogymdiary.tools.Prefs;
import com.yandex.metrica.Counter;

public class MyApp extends Application {

    @Override
    public void onCreate() {
        super.onCreate();
        Counter.initialize(getApplicationContext());
        Counter.sharedInstance().setTrackLocationEnabled(false);
        Prefs.init(getApplicationContext());
        Constants.getPartsOfBodyRealNames(this);
    }
}
