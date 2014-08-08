package com.nethergrim.combogymdiary.activities;

import android.graphics.Typeface;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.view.View;
import android.widget.TextView;

import com.google.analytics.tracking.android.EasyTracker;
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

    public TextView setTypeFaceLight(TextView v){
        v.setTypeface(Typeface.createFromAsset(getAssets(), "fonts/Roboto-Light.ttf"));
        return  v;
    }

    public TextView setTypeFaceThin(TextView v){
        v.setTypeface(Typeface.createFromAsset(getAssets(), "fonts/Roboto-Thin.ttf"));
        return v;
    }

}
