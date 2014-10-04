package com.nethergrim.combogymdiary.activities;

import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.content.res.Configuration;
import android.database.Cursor;
import android.os.AsyncTask;
import android.os.Bundle;

import com.nethergrim.combogymdiary.Constants;
import com.nethergrim.combogymdiary.storage.DB;
import com.nethergrim.combogymdiary.R;
import com.nethergrim.combogymdiary.model.DayOfWeek;
import com.nethergrim.combogymdiary.model.ExerciseTrainingObject;
import com.nethergrim.combogymdiary.model.TrainingDay;
import com.nethergrim.combogymdiary.tools.Prefs;
import com.yandex.metrica.Counter;

public class StartActivity extends AnalyticsActivity {

    private DB db;
    private String[] exeLegs;
    private String[] exeChest;
    private String[] exeBack;
    private String[] exeShoulders;
    private String[] exeBiceps;
    private String[] exeTriceps;
    private String[] exeAbs;

    private void goNext() {
        Intent gotoStartTraining = new Intent(this, BaseActivity.class);
        gotoStartTraining.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(gotoStartTraining);
        finish();
    }

    private void initialize() {
        db = new DB(this);
        db.open();
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
        if(getResources().getConfiguration().orientation == Configuration.ORIENTATION_PORTRAIT) {
            setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
        } else setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);
        setContentView(R.layout.activity_main);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_SENSOR);
    }

    @Override
    protected void onPostCreate(Bundle savedInstanceState) {
        super.onPostCreate(savedInstanceState);
        if (!Prefs.get().getDatabaseFilled()) {
            InitTask task = new InitTask();
            task.execute();
        } else if (!Prefs.get().getDbUpdatedToV5()) {
            UpdateTask task = new UpdateTask();
            task.execute();
        } else goNext();
    }

    private void initTableForFirstTime(String partOfBody, String trainingName, String[] exerciseList, DayOfWeek dayOfWeek, String url) {
        int trainingId = (int) db.persistTrainings(trainingName);
        TrainingDay trainingDay = db.getTrainingDay(trainingId);
        trainingDay.setDayOfWeek(dayOfWeek);
        trainingDay.setImageUrl(url);
        db.updateTrainingDay(trainingDay);
        for (int i = 0; i < exerciseList.length; i++) {
            int exeId = (int) db.addExercise(exerciseList[i], "60", partOfBody);
            ExerciseTrainingObject exerciseTrainingObject = new ExerciseTrainingObject();
            exerciseTrainingObject.setTrainingProgramId(trainingId);
            exerciseTrainingObject.setExerciseId(exeId);
            exerciseTrainingObject.setSuperset(false);
            exerciseTrainingObject.setSupersetId(0);
            exerciseTrainingObject.setPositionAtTraining(i);
            exerciseTrainingObject.setPositionAtSuperset(0);
            db.addExerciseTrainingObject(exerciseTrainingObject);
        }
    }

    private void initTableForFirstTime() {
        if (!db.hasTrainingPrograms()) {
            initTableForFirstTime(Constants.PART_OF_BODY_LEGS, getString(R.string.traLegs), exeLegs, DayOfWeek.MONDAY, Constants.partsOfBodyURLs.get(12));
            initTableForFirstTime(Constants.PART_OF_BODY_CHEST, getString(R.string.traChest), exeChest, DayOfWeek.TUESDAY, Constants.partsOfBodyURLs.get(0));
            initTableForFirstTime(Constants.PART_OF_BODY_BICEPS, getString(R.string.traBiceps), exeBiceps, DayOfWeek.WEDNESDAY, Constants.partsOfBodyURLs.get(1));
            initTableForFirstTime(Constants.PART_OF_BODY_TRICEPS, getString(R.string.traTriceps), exeTriceps, DayOfWeek.THURSDAY, Constants.partsOfBodyURLs.get(2));
            initTableForFirstTime(Constants.PART_OF_BODY_BACK, getString(R.string.traBack), exeBack, DayOfWeek.FRIDAY, Constants.partsOfBodyURLs.get(9));
            initTableForFirstTime(Constants.PART_OF_BODY_SHOULDERS, getString(R.string.traShoulders), exeShoulders, DayOfWeek.SATURDAY, Constants.partsOfBodyURLs.get(3));
            initTableForFirstTime(Constants.PART_OF_BODY_ABS, getString(R.string.traAbs), exeAbs, DayOfWeek.SUNDAY, Constants.partsOfBodyURLs.get(5));
        }
    }

    private void updateTableForVersion5() {
        if (!db.hasExerciseTrainingObjects()) {
            Cursor c = db.getDataExe(null, null, null, null, null, DB._ID);
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
            c = db.getDataTrainings(null, null, null, null, null, null);
            if (c.moveToFirst()) {
                do {
                    String[] exercises = db.convertStringToArray(c.getString(2));
                    for (int i = 0; i < exercises.length; i++) {
                        ExerciseTrainingObject exerciseTrainingObject = new ExerciseTrainingObject();
                        exerciseTrainingObject.setTrainingProgramId(c.getInt(0));
                        exerciseTrainingObject.setExerciseId(db.getExerciseId(exercises[i]));
                        exerciseTrainingObject.setPositionAtSuperset(0);
                        exerciseTrainingObject.setPositionAtTraining(i);
                        exerciseTrainingObject.setSuperset(false);
                        exerciseTrainingObject.setSupersetId(0);
                        db.addExerciseTrainingObject(exerciseTrainingObject);
                    }
                } while (c.moveToNext());
            }
            c.close();
        }
    }

    class InitTask extends AsyncTask<Void, Void, Void> {

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
        }

        @Override
        protected Void doInBackground(Void... params) {
            try {
                initialize();
                initTableForFirstTime();
                Prefs.get().setDbUpdatedToV5(true);
                Prefs.get().setDatabaseFilled(true);
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
            initialize();
            updateTableForVersion5();
            Prefs.get().setDbUpdatedToV5(true);
            return null;
        }

        @Override
        protected void onPostExecute(Void result) {
            super.onPostExecute(result);
            goNext();
        }
    }


}
