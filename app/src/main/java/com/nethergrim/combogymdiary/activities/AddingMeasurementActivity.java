package com.nethergrim.combogymdiary.activities;

import android.app.Activity;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.view.Gravity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.nethergrim.combogymdiary.DB;
import com.nethergrim.combogymdiary.R;
import com.nethergrim.combogymdiary.view.FloatingActionButton;

import java.text.SimpleDateFormat;
import java.util.Date;


public class AddingMeasurementActivity extends AnalyticsActivity {

    private EditText etWeight, etTall, etChest, etWaist, etHip, etLeg, etCalf,
            etArm;
    private String date;
    private DB db;
    private FloatingActionButton fab;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_adding_measurement);
        getActionBar().setTitle(R.string.adding_measurements);
        getActionBar().setDisplayHomeAsUpEnabled(true);
        getActionBar().setDisplayShowHomeEnabled(false);
        etWeight = (EditText) findViewById(R.id.etMeasureWeight);
        etTall = (EditText) findViewById(R.id.etMeasureTall);
        etChest = (EditText) findViewById(R.id.etMeasureChest);
        etWaist = (EditText) findViewById(R.id.etMeasureWaist);
        etHip = (EditText) findViewById(R.id.etMeasureHip);
        etLeg = (EditText) findViewById(R.id.etMeasureLeg);
        etCalf = (EditText) findViewById(R.id.etMeasureCalf);
        etArm = (EditText) findViewById(R.id.etMeasureArm);
        SimpleDateFormat sdf = new SimpleDateFormat("dd.MM.yyyy");
        date = sdf.format(new Date(System.currentTimeMillis()));
        db = new DB(this);
        db.open();
        TextView tvTmp = (TextView) findViewById(R.id.textView1weight);
        SharedPreferences sp = PreferenceManager
                .getDefaultSharedPreferences(this);
        String item = sp.getString(BaseActivity.MEASURE_ITEM, "1");
        String measureItem = "";
        if (item.equals("1")) {
            measureItem = " ("
                    + getResources().getStringArray(R.array.measure_items)[0]
                    + ") ";
        } else if (item.equals("2")) {
            measureItem = " ("
                    + getResources().getStringArray(R.array.measure_items)[1]
                    + ") ";
        }
        tvTmp.setText(getResources().getString(R.string.weight)
                + measureItem);
        fab = new FloatingActionButton.Builder(this)
                .withDrawable(getResources().getDrawable(R.drawable.ic_action_save))
                .withButtonColor(getResources().getColor(R.color.material_cyan_a400))
                .withGravity(Gravity.BOTTOM | Gravity.CENTER_HORIZONTAL)
                .withMargins(0, 0, 0, 16)
                .create();
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                saveResults();
            }
        });
    }

    protected void onDestroy() {
        super.onDestroy();
        db.close();
    }

//    @Override
//    public boolean onCreateOptionsMenu(Menu menu) {
//        getMenuInflater().inflate(R.menu.adding_measurement, menu);
//        return true;
//    }

    private void saveResults() {
        String weight_m = etWeight.getText().toString();
        String tall_m = etTall.getText().toString();
        String chest_m = etChest.getText().toString();
        String waist_m = etWaist.getText().toString();
        String hip_m = etHip.getText().toString();
        String leg_m = etLeg.getText().toString();
        String calf_m = etCalf.getText().toString();
        String arm_m = etArm.getText().toString();
        boolean areEmpty = true;
        if (!weight_m.isEmpty()) {
            db.addRecMeasure(date, getResources().getString(R.string.weight),
                    weight_m);
            areEmpty = false;
        }
        if (!tall_m.isEmpty()) {
            db.addRecMeasure(date, getResources().getString(R.string.tall),
                    tall_m);
            areEmpty = false;
        }
        if (!chest_m.isEmpty()) {
            db.addRecMeasure(date, getResources().getString(R.string.chest),
                    chest_m);
            areEmpty = false;
        }
        if (!waist_m.isEmpty()) {
            db.addRecMeasure(date, getResources().getString(R.string.waist),
                    waist_m);
            areEmpty = false;
        }
        if (!hip_m.isEmpty()) {
            db.addRecMeasure(date, getResources().getString(R.string.hip),
                    hip_m);
            areEmpty = false;
        }
        if (!leg_m.isEmpty()) {
            db.addRecMeasure(date, getResources().getString(R.string.leg),
                    leg_m);
            areEmpty = false;
        }
        if (!calf_m.isEmpty()) {
            db.addRecMeasure(date, getResources().getString(R.string.calf),
                    calf_m);
            areEmpty = false;
        }
        if (!arm_m.isEmpty()) {
            db.addRecMeasure(date, getResources().getString(R.string.arm),
                    arm_m);
            areEmpty = false;
        }
        if (areEmpty) {
            Toast.makeText(this, R.string.input_data, Toast.LENGTH_SHORT)
                    .show();
        } else {
            finish();
        }
    }



    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        /*if (id == R.id.itemSaveMeasure) {
            saveResults();
            return true;
        } else*/ if (id == android.R.id.home) {
            finish();
            return true;
        }
        return false;
    }

}
