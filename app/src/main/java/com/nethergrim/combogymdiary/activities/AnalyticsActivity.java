package com.nethergrim.combogymdiary.activities;

import android.graphics.Typeface;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.widget.TextView;

import com.google.analytics.tracking.android.EasyTracker;
import com.nethergrim.combogymdiary.Constants;
import com.startapp.android.publish.SDKAdPreferences;
import com.startapp.android.publish.StartAppSDK;

public abstract class AnalyticsActivity extends FragmentActivity {


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        StartAppSDK.init(this, " 108133674", "208084744", new SDKAdPreferences().setAge(22).setGender(SDKAdPreferences.Gender.MALE));
        super.onCreate(savedInstanceState);
    }

    @Override
    public void onStart() {
        super.onStart();
        EasyTracker.getInstance(this).activityStart(this);
    }

    @Override
    public void onStop() {
        super.onStop();
        EasyTracker.getInstance(this).activityStop(this);
    }

    public TextView setTypeFaceLight(TextView v) {
        v.setTypeface(Typeface.createFromAsset(getAssets(), Constants.TYPEFACE_LIGHT));
        return v;
    }

    public TextView setTypeFaceThin(TextView v) {
        v.setTypeface(Typeface.createFromAsset(getAssets(), Constants.TYPEFACE_THIN));
        return v;
    }

    public TextView setTypeFace(TextView v, String font) {
        v.setTypeface(Typeface.createFromAsset(getAssets(), font));
        return v;
    }

}
