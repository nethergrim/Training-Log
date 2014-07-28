package com.nethergrim.combogymdiary.activities;


import android.app.Activity;
import android.support.v4.app.FragmentActivity;

import com.google.analytics.tracking.android.EasyTracker;

public abstract class AnalyticsActivity extends FragmentActivity {
    @Override
    public void onStart() {
        super.onStart();
        EasyTracker.getInstance(this).activityStart(this);  // Add this method.
    }

    @Override
    public void onStop() {
        super.onStop();
        EasyTracker.getInstance(this).activityStop(this);  // Add this method.
    }

}
