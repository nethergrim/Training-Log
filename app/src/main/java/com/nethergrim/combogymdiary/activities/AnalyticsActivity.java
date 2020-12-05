package com.nethergrim.combogymdiary.activities;

import android.graphics.Typeface;
import android.os.Bundle;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.google.analytics.tracking.android.EasyTracker;
import com.nethergrim.combogymdiary.Constants;


public abstract class AnalyticsActivity extends AppCompatActivity {


    @Override
    protected void onCreate(Bundle savedInstanceState) {
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
