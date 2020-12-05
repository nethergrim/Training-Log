package com.nethergrim.combogymdiary;

import android.app.Application;

//import com.crashlytics.android.Crashlytics;
//import com.crashlytics.android.answers.Answers;
import com.nethergrim.combogymdiary.tools.Prefs;
import com.yandex.metrica.Counter;

//import io.fabric.sdk.android.Fabric;

public class MyApp extends Application {

    @Override
    public void onCreate() {
        super.onCreate();
//        Fabric.with(this, new Crashlytics(), new Answers());
        Counter.initialize(getApplicationContext());
        Counter.sharedInstance().setTrackLocationEnabled(false);
        Prefs.init(getApplicationContext());
        Constants.getPartsOfBodyRealNames(this);
    }
}
