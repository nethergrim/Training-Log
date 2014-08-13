package com.nethergrim.combogymdiary.activities;

import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.os.AsyncTask;
import android.os.Bundle;
import android.preference.PreferenceManager;

import com.nethergrim.combogymdiary.Constants;
import com.nethergrim.combogymdiary.DB;
import com.nethergrim.combogymdiary.R;
import com.yandex.metrica.Counter;

public class StartActivity extends AnalyticsActivity {

    private final static String DATABASE_FILLED = "database_filled";
    private SharedPreferences sp;
    private DB db;
    String[] exeLegs;
    String[] exeChest;
    String[] exeBack;
    String[] exeShoulders;
    String[] exeBiceps;
    String[] exeTriceps;
    String[] exeAbs;

    private void goNext() {
        Intent gotoStartTraining = new Intent(this, BaseActivity.class);
        startActivity(gotoStartTraining);
        finish();
    }

    private void initialize() {
        sp = PreferenceManager.getDefaultSharedPreferences(this);
        db = new DB(this);
        db.open();
        Cursor tmp = db.getDataExe(null, null, null, null, null, null);
        if (tmp.getCount() < 3) {
            sp.edit().putBoolean(DATABASE_FILLED, false).apply();
        } else {
            sp.edit().putBoolean(DATABASE_FILLED, true).apply();
        }
        tmp.close();
        exeLegs = getResources().getStringArray(R.array.exercisesArrayLegs);
        exeChest = getResources().getStringArray(R.array.exercisesArrayChest);
        exeBack = getResources().getStringArray(R.array.exercisesArrayBack);
        exeShoulders = getResources().getStringArray(R.array.exercisesArrayShoulders);
        exeBiceps = getResources().getStringArray(R.array.exercisesArrayBiceps);
        exeTriceps = getResources().getStringArray(R.array.exercisesArrayTriceps);
        exeAbs = getResources().getStringArray(R.array.exercisesArrayAbs);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        getActionBar().hide();
    }

    @Override
    protected void onPostCreate(Bundle savedInstanceState) {
        super.onPostCreate(savedInstanceState);
        initialize();
        if (!sp.getBoolean(DATABASE_FILLED, false)) {
            InitTask task = new InitTask();
            task.execute();
        } else {
            UpdateTask task = new UpdateTask();
            task.execute();
        }
    }

    private void initTableForFirstTime() {
        db.addRecTrainings(getString(R.string.traLegs), db.convertArrayToString(exeLegs));
        db.addRecTrainings(getString(R.string.traChest), db.convertArrayToString(exeChest));
        db.addRecTrainings(getString(R.string.traBack), db.convertArrayToString(exeBack));
        db.addRecTrainings(getString(R.string.traShoulders), db.convertArrayToString(exeShoulders));
        db.addRecTrainings(getString(R.string.traBiceps), db.convertArrayToString(exeBiceps));
        db.addRecTrainings(getString(R.string.traTriceps), db.convertArrayToString(exeTriceps));
        db.addRecTrainings(getString(R.string.traAbs), db.convertArrayToString(exeAbs));

        for (String exeLeg : exeLegs) db.addExercise(exeLeg, "90", Constants.PART_OF_BODY_LEGS);
        for (String anExeChest : exeChest)
            db.addExercise(anExeChest, "60", Constants.PART_OF_BODY_CHEST);
        for (String exeBicep : exeBiceps)
            db.addExercise(exeBicep, "60", Constants.PART_OF_BODY_BICEPS);
        for (String exeTricep : exeTriceps)
            db.addExercise(exeTricep, "60", Constants.PART_OF_BODY_TRICEPS);
        for (String anExeBack : exeBack)
            db.addExercise(anExeBack, "60", Constants.PART_OF_BODY_BACK);
        for (String exeShoulder : exeShoulders)
            db.addExercise(exeShoulder, "60", Constants.PART_OF_BODY_SHOULDERS);
        for (String exeAb : exeAbs) db.addExercise(exeAb, "60", Constants.PART_OF_BODY_ABS);
    }

    private void updateTableForVersion5() {
        Cursor c = db.getDataExe(null, null, null, null, null, DB.COLUMN_ID);
        if (c.moveToFirst()) {
            do {
                if (c.getString(4) == null || c.getString(4).equals("")) {
                    boolean fixed = false;
                    for (String exe : exeAbs) {
                        if (c.getString(2).equals(exe)) {
                            db.updateExercise(c.getInt(0), DB.PART_OF_BODY, Constants.PART_OF_BODY_ABS);
                            fixed = true;
                            break;
                        }
                    }
                    if (!fixed)
                        for (String exe : exeShoulders) {
                            if (c.getString(2).equals(exe)) {
                                db.updateExercise(c.getInt(0), DB.PART_OF_BODY, Constants.PART_OF_BODY_SHOULDERS);
                                fixed = true;
                                break;
                            }
                        }
                    if (!fixed)
                        for (String exe : exeBack) {
                            if (c.getString(2).equals(exe)) {
                                db.updateExercise(c.getInt(0), DB.PART_OF_BODY, Constants.PART_OF_BODY_BACK);
                                fixed = true;
                                break;
                            }
                        }
                    if (!fixed)
                        for (String exe : exeTriceps) {
                            if (c.getString(2).equals(exe)) {
                                db.updateExercise(c.getInt(0), DB.PART_OF_BODY, Constants.PART_OF_BODY_TRICEPS);
                                fixed = true;
                                break;
                            }
                        }
                    if (!fixed)
                        for (String exe : exeBiceps) {
                            if (c.getString(2).equals(exe)) {
                                db.updateExercise(c.getInt(0), DB.PART_OF_BODY, Constants.PART_OF_BODY_BICEPS);
                                fixed = true;
                                break;
                            }
                        }
                    if (!fixed)
                        for (String exe : exeChest) {
                            if (c.getString(2).equals(exe)) {
                                db.updateExercise(c.getInt(0), DB.PART_OF_BODY, Constants.PART_OF_BODY_CHEST);
                                fixed = true;
                                break;
                            }
                        }
                    if (!fixed)
                        for (String exe : exeLegs) {
                            if (c.getString(2).equals(exe)) {
                                db.updateExercise(c.getInt(0), DB.PART_OF_BODY, Constants.PART_OF_BODY_LEGS);
                                fixed = true;
                                break;
                            }
                        }
                    if (!fixed) {
                        db.updateExercise(c.getInt(0), DB.PART_OF_BODY, Constants.PART_OF_BODY_NONE);
                    }
                }
            } while (c.moveToNext());
        }
        c.close();
    }

    protected void onDestroy() {
        super.onDestroy();
        db.close();
    }

    class InitTask extends AsyncTask<Void, Void, Void> {

        @Override
        protected void onPreExecute() {
            sp.edit().putBoolean(DATABASE_FILLED, true).apply();
            super.onPreExecute();
        }

        @Override
        protected Void doInBackground(Void... params) {

            try {
                initTableForFirstTime();
            } catch (Exception e) {
                Counter.sharedInstance().reportError("", e);
            }
            return null;
        }

        @Override
        protected void onPostExecute(Void result) {
            super.onPostExecute(result);
            goNext();
        }
    }

    class UpdateTask extends AsyncTask<Void, Void, Void> {

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
        }

        @Override
        protected Void doInBackground(Void... params) {

            updateTableForVersion5();

            return null;
        }

        @Override
        protected void onPostExecute(Void result) {
            super.onPostExecute(result);
            goNext();
        }
    }


}
