package com.nethergrim.combogymdiary;

import android.app.Application;

import com.google.analytics.tracking.android.EasyTracker;
import com.yandex.metrica.Counter;

public class MyApp extends Application {

    @Override
    public void onCreate() {
        super.onCreate();
        Counter.initialize(getApplicationContext());
        Counter.sharedInstance().setTrackLocationEnabled(false);
    }





//    @Override
//    public void onStart() {
//        super.onStart();
//        super.o
//        EasyTracker.getInstance(this).activityStart(this);  // Add this method.
//    }

//    @Override
//    public void onStop() {
//        super.onStop();
//        EasyTracker.getInstance(this).activityStop(this);  // Add this method.
//    }

}
