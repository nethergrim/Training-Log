package com.nethergrim.combogymdiary.activities;

import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.view.MenuItem;

import com.nethergrim.combogymdiary.DB;
import com.nethergrim.combogymdiary.R;
import com.nethergrim.combogymdiary.tools.Prefs;
import com.nethergrim.combogymdiary.view.TextViewLight;

public class MeasurementsDetailedActivity extends AnalyticsActivity {

    private DB db;
    private TextViewLight tv1, tv2, tv3, tv4, tv5, tv6, tv7, tv8;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_measurements_detailed);
        Intent intent = getIntent();
        String date = intent.getStringExtra("date");
        db = new DB(this);
        db.open();
        String longValue = getResources().getString(R.string.sm);

        getActionBar().setTitle(
                getResources().getString(R.string.measurements) + " - " + date);
        getActionBar().setDisplayHomeAsUpEnabled(true);
        getActionBar().setDisplayShowHomeEnabled(false);
        initTv();
        String weight = getResources().getString(R.string.weight);
        String tall = getResources().getString(R.string.tall);
        String chest = getResources().getString(R.string.chest);
        String waist = getResources().getString(R.string.waist);
        String hip = getResources().getString(R.string.hip);
        String leg = getResources().getString(R.string.leg);
        String calf = getResources().getString(R.string.calf);
        String arm = getResources().getString(R.string.arm);

        String[] cols = {DB.DATE, DB.PART_OF_BODY_FOR_MEASURING,
                DB.MEASURE_VALUE};
        String[] args = {date};
        Cursor cursor = db.getDataMeasures(cols, DB.DATE + "=?", args, null, null, DB.DATE);

        String measureItem = Prefs.get().getWeightMeasureType(this);

        if (cursor.moveToFirst()) {
            do {
                String tmp_type = cursor.getString(1);
                if (tmp_type.equals(weight)) {
                    tv1.setText(tmp_type + " - " + cursor.getString(2) + measureItem);
                } else if (tmp_type.equals(tall)) {
                    tv2.setText(tmp_type + " - " + cursor.getString(2) + longValue);
                } else if (tmp_type.equals(chest)) {
                    tv3.setText(tmp_type + " - " + cursor.getString(2) + longValue);
                } else if (tmp_type.equals(waist)) {
                    tv4.setText(tmp_type + " - " + cursor.getString(2) + longValue);
                } else if (tmp_type.equals(hip)) {
                    tv5.setText(tmp_type + " - " + cursor.getString(2) + longValue);
                } else if (tmp_type.equals(leg)) {
                    tv6.setText(tmp_type + " - " + cursor.getString(2) + longValue);
                } else if (tmp_type.equals(calf)) {
                    tv7.setText(tmp_type + " - " + cursor.getString(2) + longValue);
                } else if (tmp_type.equals(arm)) {
                    tv8.setText(tmp_type + " - " + cursor.getString(2) + longValue);
                }
            } while (cursor.moveToNext());
        }
        cursor.close();

    }

    private void initTv() {
        tv1 = (TextViewLight) findViewById(R.id.textView1_);
        tv2 = (TextViewLight) findViewById(R.id.textView2_);
        tv3 = (TextViewLight) findViewById(R.id.textView3_);
        tv4 = (TextViewLight) findViewById(R.id.textView4_);
        tv5 = (TextViewLight) findViewById(R.id.textView5_);
        tv6 = (TextViewLight) findViewById(R.id.textView6_);
        tv7 = (TextViewLight) findViewById(R.id.textView7_);
        tv8 = (TextViewLight) findViewById(R.id.textView8_);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                super.onBackPressed();
                return true;
        }
        return false;
    }

    protected void onDestroy() {
        super.onDestroy();
        db.close();
    }
}
