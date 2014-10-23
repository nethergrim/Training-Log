package com.nethergrim.combogymdiary.storage;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteDatabase.CursorFactory;
import android.database.sqlite.SQLiteOpenHelper;

import com.nethergrim.combogymdiary.Constants;
import com.nethergrim.combogymdiary.model.DayOfWeek;
import com.nethergrim.combogymdiary.model.Exercise;
import com.nethergrim.combogymdiary.model.ExerciseGroup;
import com.nethergrim.combogymdiary.model.ExerciseTrainingObject;
import com.nethergrim.combogymdiary.model.Set;
import com.nethergrim.combogymdiary.model.Training;
import com.nethergrim.combogymdiary.model.TrainingDay;
import com.nethergrim.combogymdiary.model.TrainingProgram;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;


public class DB {

    public static final String DB_NAME = "mydb";
    public static final String _ID = "_id";
    public static final String EXERCISE_NAME = "exercise_name";
    public static final String TRAINING_NAME = "training_name";
    public static final String TIMER_VALUE = "timer_value";
    public static final String DB_TABLE_SET = "main_tab";
    public static final String DB_TABLE_TRAINING_EXERCISE = "training_exercise";
    public static final String DATE = "Date";
    public static final String WEIGHT = "Weight";
    public static final String REPS = "Reps";
    public static final String SET = "SetsN";
    public static final String PART_OF_BODY = "part_of_body";
    public static final String DB_MEASURE_TABLE = "measurements_tab";
    public static final String PART_OF_BODY_FOR_MEASURING = "part_of_body";
    public static final String MEASURE_VALUE = "measure_value";
    public static final String TRAINING_DAYS = "trainings_tab";
    public static final String strSeparator = "__,__";
    public static final String SIMPLE_DATE_FORMAT = "dd.MM.yyyy";
    public static final String SUPERSET_EXISTS = "superset";
    public static final String SUPERSET_ID = "superset_id";
    public static final String SUPERSET_COLOR = "superset_color";
    public static final String POSITION_AT_TRAINING = "position_at_training";
    public static final String EXERCISE_ID = "training_exercise_id";
    public static final String TRAINING_PROGRAM_ID = "training_program_id";
    private static final int DB_VERSION = 6;
    private static final String DB_TABLE_EXERCISES = "exe_tab";
    private static Context mCtx;
    private static DB db;
    private DBHelper mDBHelper;
    private SQLiteDatabase mDB;

    private static final String TABLE_MEASURE_CREATE = "create table " + DB_MEASURE_TABLE + "(" +
            _ID + " integer primary key autoincrement, " +
            DATE + " text, " +
            PART_OF_BODY_FOR_MEASURING + " text, " +
            MEASURE_VALUE + " text" + ");";

    private static final String TABLE_SET_CREATE = "create table " + Set.Columns.TABLE + "("
            + Set.Columns.FIELD_ID + " integer primary key autoincrement, "
            + Set.Columns.FIELD_TRAINING_NAME + " text, "
            + Set.Columns.FIELD_EXERCISE_NAME + " text, "
            + Set.Columns.FIELD_DATE + " text, "
            + Set.Columns.FIELD_WEIGHT + " integer, "
            + Set.Columns.FIELD_REPS + " integer, "
            + Set.Columns.FIELD_SET_COUNT + " integer, "
            + Set.Columns.FIELD_SUPERSET_PRESENTS + " boolean, "
            + Set.Columns.FIELD_SUPERSET_ID + " integer, "
            + Set.Columns.FIELD_SUPERSET_COLOR + " integer, "
            + Set.Columns.FIELD_TRAINING_DAY_ID + " integer, "
            + Set.Columns.FIELD_EXERCISE_ID + " integer. "
            + Set.Columns.FIELD_CREATED_AT + " INTEGER, "
            + Set.Columns.FIELD_POSITION_AT_TRAINING + " INTEGER, "
            + Set.Columns.FIELD_SUPERSET_POSITION + " INTEGER"
            + ");";
    private static final String TABLE_TRAINING_CREATE = "create table " + Training.Columns.TABLE + "("
            + Training.Columns.FIELD_ID + " integer primary key autoincrement, "
            + Training.Columns.FIELD_DATE + " text, "
            + Training.Columns.FIELD_COMMENT + " text, "
            + Training.Columns.FIELD_TOTAL_TIME + " text, "
            + Training.Columns.FIELD_TOTAL_WEIGHT + " integer, "
            + Training.Columns.FIELD_CREATED_AT + " INTEGER, "
            + Training.Columns.FIELD_TRAINING_DAY_NAME + " TEXT, "
            + Training.Columns.FIELD_TRAINING_DAY_ID + " INTEGER, "
            + Training.Columns.FIELD_TIME_OF_END + " INTEGER"
            + ");";
    private static final String TABLE_TRAINING_DAY_CREATE = "create table " + TrainingDay.Columns.TABLE + "("
            + TrainingDay.Columns.FIELD_ID + " integer primary key autoincrement, "
            + TrainingDay.Columns.FIELD_TRAINING_NAME + " text, "
            + TrainingDay.Columns.FIELD_DAY_OF_WEEK + " integer, "
            + TrainingDay.Columns.FIELD_IMAGE_URL + " text, "
            + TrainingDay.Columns.FIELD_COLOR + " integer, "
            + TrainingDay.Columns.FIELD_CREATED_AT + " integer, "
            + TrainingDay.Columns.FIELD_TRAINING_PROGRAM_ID + " integer"
            + ");";
    private static final String TABLE_EXERCISE_TRAINING_CREATE = "create table " + ExerciseTrainingObject.Columns.TABLE + "("
            + ExerciseTrainingObject.Columns.FIELD_ID + " integer primary key autoincrement, "
            + ExerciseTrainingObject.Columns.FIELD_TRAINING_DAY_ID + " integer, "
            + ExerciseTrainingObject.Columns.FIELD_EXERCISE_ID + " integer, "
            + ExerciseTrainingObject.Columns.FIELD_POSITION_AT_TRAINING + " integer, "
            + ExerciseTrainingObject.Columns.FIELD_SUPERSET_PRESENTS + " boolean, "
            + ExerciseTrainingObject.Columns.FIELD_SUPERSET_POSITION + " integer, "
            + ExerciseTrainingObject.Columns.FIELD_SUPERSET_ID + " integer, "
            + ExerciseTrainingObject.Columns.FIELD_SUPERSET_COLOR + " integer, "
            + ExerciseTrainingObject.Columns.FIELD_CREATED_AT + " INTEGER"
            + ");";
    private static final String TABLE_EXERCISE_CREATE = "create table " + Exercise.Columns.TABLE + "("
            + Exercise.Columns.FIELD_ID + " integer primary key autoincrement, "
            + Exercise.Columns.FIELD_EXERCISE_NAME + " text, "
            + Exercise.Columns.FIELD_TIMER_VALUE + " text, "
            + Exercise.Columns.FIELD_PART_OF_BODY + " text, "
            + Exercise.Columns.FIELD_CREATED_AT + " INTEGER"
            + ");";
    private static final String TABLE_TRAINING_PROGRAM_CREATE = "create table " + TrainingProgram.Columns.TABLE + "("
            + TrainingProgram.Columns.FIELD_ID + " integer primary key autoincrement, "
            + TrainingProgram.Columns.FIELD_CREATED_AT + " integer, "
            + TrainingProgram.Columns.FIELD_NAME + " text, "
            + TrainingProgram.Columns.FIELD_PAID + " integer"
            + ");";

    public static void init(Context context){
        db = new DB(context);
    }

    public static DB get(){
        return db;
    }

    public DB(Context ctx) {
        mCtx = ctx;
        open();
    }

    public void open() {
        mDBHelper = new DBHelper(mCtx, DB_NAME, null, DB_VERSION);
        mDB = mDBHelper.getWritableDatabase();
    }

    public void close() {
        try {
            if (mDBHelper != null)
                mDBHelper.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public String convertDateToString(Date date) {
        try {
            SimpleDateFormat dateformat = new SimpleDateFormat(SIMPLE_DATE_FORMAT);
            return dateformat.format(date);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return "";
    }

    public Date convertStringToDate(String string) {
        SimpleDateFormat format = new SimpleDateFormat(SIMPLE_DATE_FORMAT);
        try {
            return format.parse(string);
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return null;
    }

    public String[] convertStringToArray(String str) {
        String[] arr = str.split(strSeparator);
        return arr;
    }

    public boolean delRecordMeasurement(String date) {
        String[] args = {date};
        int tmp = mDB.delete(DB_MEASURE_TABLE, DATE + "=?", args);
        return tmp > 0;
    }

    public Cursor getDataMeasures(String[] column, String selection, String[] selectionArgs, String groupBy, String having, String orderedBy) {
        return mDB.query(DB_MEASURE_TABLE, column, selection, selectionArgs, groupBy, having, orderedBy);
    }


    /*-------------------------- Exercise ------------------------------------*/

    public long persistExercise(Exercise exercise) {
        ContentValues cv = new ContentValues();
        cv.put(Exercise.Columns.FIELD_EXERCISE_NAME, exercise.getName());
        cv.put(Exercise.Columns.FIELD_TIMER_VALUE, exercise.getTimer());
        cv.put(Exercise.Columns.FIELD_PART_OF_BODY, exercise.getPartOfBody());
        cv.put(Exercise.Columns.FIELD_CREATED_AT, exercise.getCreatedAt());
        return mDB.insert(Exercise.Columns.TABLE, null, cv);
    }

    public void deleteExercise(long id) {
        mDB.delete(DB_TABLE_TRAINING_EXERCISE, _ID + " = " + id, null);
        mDB.delete(DB_TABLE_EXERCISES, _ID + " = " + id, null);
    }

    public Exercise fetchtExercise(long id) {
        String[] args = {id + ""};
        Cursor c = mDB.query(DB_TABLE_EXERCISES, null, _ID + "=?", args, null, null, null);
        if (c.moveToFirst()) {
            Exercise exercise = new Exercise();
            exercise.setName(c.getString(c.getColumnIndex(Exercise.Columns.FIELD_EXERCISE_NAME)));
            exercise.setPartOfBody(c.getString(c.getColumnIndex(Exercise.Columns.FIELD_PART_OF_BODY)));
            exercise.setId(c.getLong(0));
            exercise.setTimer(c.getString(c.getColumnIndex(Exercise.Columns.FIELD_TIMER_VALUE)));
            exercise.setCreatedAt(c.getLong(c.getColumnIndex(Exercise.Columns.FIELD_CREATED_AT)));
            return exercise;
        }
        return null;
    }

    public List<ExerciseGroup> fetchExerciseGroups() {  // returns groups of exercises, sorted by part_of_body
        List<ExerciseGroup> result = new ArrayList<ExerciseGroup>();
        for (int i = 0; i < Constants.PARTS_OF_BODY.length; i++) {
            ExerciseGroup exerciseGroup = new ExerciseGroup();
            exerciseGroup.setPositionInGlobalArray(i);
            exerciseGroup.setName(Constants.PARTS_OF_BODY[i]);
            String[] args = {Constants.PARTS_OF_BODY[i]};
            Cursor c = mDB.query(Exercise.Columns.TABLE, null, Exercise.Columns.FIELD_PART_OF_BODY + "=?", args, null, null, Exercise.Columns.FIELD_EXERCISE_NAME);
            List<Exercise> exercises = new ArrayList<Exercise>();
            if (c.moveToFirst()) {
                do {
                    Exercise exercise = new Exercise();
                    exercise.setName(c.getString(c.getColumnIndex(Exercise.Columns.FIELD_EXERCISE_NAME)));
                    exercise.setPartOfBody(c.getString(c.getColumnIndex(Exercise.Columns.FIELD_PART_OF_BODY)));
                    exercise.setId(c.getLong(0));
                    exercise.setTimer(c.getString(c.getColumnIndex(Exercise.Columns.FIELD_TIMER_VALUE)));
                    exercise.setCreatedAt(c.getLong(c.getColumnIndex(Exercise.Columns.FIELD_CREATED_AT)));
                    exercises.add(exercise);
                } while (c.moveToNext());
                exerciseGroup.setList(exercises);
                result.add(exerciseGroup);
            }
        }
        return result;
    }

    /*------------------------------------------------------------------------*/

    /*---------------------------  Set  -------------------------------------*/
    public void persistSet(Set set) {
        ContentValues cv = new ContentValues();
        cv.put(Set.Columns.FIELD_CREATED_AT, System.currentTimeMillis());
        cv.put(Set.Columns.FIELD_DATE, set.getDate());
        cv.put(Set.Columns.FIELD_EXERCISE_ID, set.getExerciseId());
        cv.put(Set.Columns.FIELD_EXERCISE_NAME, set.getExerciseName());
        cv.put(Set.Columns.FIELD_POSITION_AT_TRAINING, set.getPositionAtTraining());
        cv.put(Set.Columns.FIELD_EXERCISE_ID, set.getExerciseId());
        cv.put(Set.Columns.FIELD_REPS, set.getReps());
        cv.put(Set.Columns.FIELD_SET_COUNT, set.getSetCount());
        cv.put(Set.Columns.FIELD_SUPERSET_COLOR, set.getSupersetColor());
        cv.put(Set.Columns.FIELD_SUPERSET_ID, set.getSupersetId());
        cv.put(Set.Columns.FIELD_SUPERSET_POSITION, set.getPositionAtSuperset());
        cv.put(Set.Columns.FIELD_SUPERSET_PRESENTS, set.getSuperset());
        cv.put(Set.Columns.FIELD_TRAINING_NAME, set.getTrainingName());
        cv.put(Set.Columns.FIELD_TRAINING_DAY_ID, set.getTrainingProgramId());
        cv.put(Set.Columns.FIELD_WEIGHT, set.getWeight());
        mDB.insert(Set.Columns.TABLE, null, cv);
    }

    public void updateSet(Set set) {
        ContentValues cv = new ContentValues();
        cv.put(Set.Columns.FIELD_CREATED_AT, System.currentTimeMillis());
        cv.put(Set.Columns.FIELD_DATE, set.getDate());
        cv.put(Set.Columns.FIELD_EXERCISE_ID, set.getExerciseId());
        cv.put(Set.Columns.FIELD_EXERCISE_NAME, set.getExerciseName());
        cv.put(Set.Columns.FIELD_POSITION_AT_TRAINING, set.getPositionAtTraining());
        cv.put(Set.Columns.FIELD_EXERCISE_ID, set.getExerciseId());
        cv.put(Set.Columns.FIELD_REPS, set.getReps());
        cv.put(Set.Columns.FIELD_SET_COUNT, set.getSetCount());
        cv.put(Set.Columns.FIELD_SUPERSET_COLOR, set.getSupersetColor());
        cv.put(Set.Columns.FIELD_SUPERSET_ID, set.getSupersetId());
        cv.put(Set.Columns.FIELD_SUPERSET_POSITION, set.getPositionAtSuperset());
        cv.put(Set.Columns.FIELD_SUPERSET_PRESENTS, set.getSuperset());
        cv.put(Set.Columns.FIELD_TRAINING_NAME, set.getTrainingName());
        cv.put(Set.Columns.FIELD_TRAINING_DAY_ID, set.getTrainingProgramId());
        cv.put(Set.Columns.FIELD_WEIGHT, set.getWeight());
        if (set.getId() > 0) {
            mDB.update(Set.Columns.TABLE, cv, Set.Columns.FIELD_ID + "=" + String.valueOf(set.getId()), null);
        } else {
            mDB.update(Set.Columns.TABLE, cv, Set.Columns.FIELD_DATE + "=" + set.getDate()
                    + " AND " + Set.Columns.FIELD_TRAINING_NAME + "=" + set.getTrainingName()
                    + " AND " + Set.Columns.FIELD_EXERCISE_NAME + "=" + set.getExerciseName()
                    + " AND " + Set.Columns.FIELD_SET_COUNT + "=" + set.getSetCount()
                    , null);
        }
    }

    public void persistSet(List<Set> sets) {
        for (Set set : sets) {
            persistSet(set);
        }
    }

    public void deleteSet(Set set) {
        if (set.getId() > 0) {
            mDB.delete(Set.Columns.TABLE, Set.Columns.FIELD_ID + "=" + String.valueOf(set.getId()), null);
        } else {
            mDB.delete(Set.Columns.TABLE, Set.Columns.FIELD_DATE + "=" + set.getDate()
                    + " AND " + Set.Columns.FIELD_TRAINING_NAME + "=" + set.getTrainingName()
                    + " AND " + Set.Columns.FIELD_EXERCISE_NAME + "=" + set.getExerciseName()
                    + " AND " + Set.Columns.FIELD_SET_COUNT + "=" + set.getSetCount()
                    , null);
        }
    }

    public Set fetchSet(Long id) {
        Cursor c = mDB.query(Set.Columns.TABLE, null, Set.Columns.FIELD_ID + "=" + String.valueOf(id), null, null, null, null);
        if (c.moveToFirst()) {
            Set set = new Set();
            set.setId(c.getLong(0));
            set.setCreatedAt(c.getLong(c.getColumnIndex(Set.Columns.FIELD_CREATED_AT)));
            set.setDate(c.getString(c.getColumnIndex(Set.Columns.FIELD_DATE)));
            set.setExerciseName(c.getString(c.getColumnIndex(Set.Columns.FIELD_EXERCISE_NAME)));
            set.setReps(c.getInt(c.getColumnIndex(Set.Columns.FIELD_REPS)));
            set.setSetCount(c.getInt(c.getColumnIndex(Set.Columns.FIELD_SET_COUNT)));
            set.setTrainingName(c.getString(c.getColumnIndex(Set.Columns.FIELD_TRAINING_NAME)));
            set.setWeight(c.getInt(c.getColumnIndex(Set.Columns.FIELD_WEIGHT)));
            set.setExerciseId(c.getLong(c.getColumnIndex(Set.Columns.FIELD_EXERCISE_ID)));
            set.setPositionAtSuperset(c.getInt(c.getColumnIndex(Set.Columns.FIELD_SUPERSET_POSITION)));
            set.setSuperset(c.getInt(c.getColumnIndex(Set.Columns.FIELD_SUPERSET_PRESENTS)) == 1);
            set.setSupersetColor(c.getInt(c.getColumnIndex(Set.Columns.FIELD_SUPERSET_COLOR)));
            set.setSupersetId(c.getLong(c.getColumnIndex(Set.Columns.FIELD_SUPERSET_ID)));
            set.setExerciseName(c.getString(c.getColumnIndex(Set.Columns.FIELD_EXERCISE_NAME)));
            set.setDate(c.getString(c.getColumnIndex(Set.Columns.FIELD_DATE)));
            set.setPositionAtTraining(c.getInt(c.getColumnIndex(Set.Columns.FIELD_POSITION_AT_TRAINING)));
            set.setTrainingProgramId(c.getLong(c.getColumnIndex(Set.Columns.FIELD_TRAINING_DAY_ID)));
            c.close();
            return set;
        } else {
            return null;
        }
    }

    public int getLastWeightOrReps(String _exeName, int _set, boolean ifWeight) { // FIXME fix exercise name - > exercise id
        String[] cols = {DB.WEIGHT, DB.SET};
        if (ifWeight) {
            cols[0] = DB.WEIGHT;
        } else {
            cols[0] = DB.REPS;
        }

        String[] tags = {_exeName};
        Cursor c = mDB.query(DB_TABLE_SET, cols, DB.EXERCISE_NAME + "=?", tags,
                null, null, null);

        int size = c.getCount();
        if (size > 1) {
            if (c.moveToLast() && (size > (_set + 1))) {

                if (_set > 0) {
                    for (int i = 0; i < _set; i++) {
                        c.moveToPrevious();
                        size--;
                    }
                }

                int setNumberAtLastTraining = c.getInt(1);
                int delta = setNumberAtLastTraining - _set - 1;
                if (size > delta) {
                    if (setNumberAtLastTraining > _set) {
                        for (int j = 0; j < delta; j++) {
                            c.moveToPrevious();
                        }
                        return c.getInt(0);
                    } else if (setNumberAtLastTraining == _set) {
                        return 0;
                    } else
                        return 0;
                } else
                    return 0;
            } else
                return 0;
        } else
            return 0;
    }

    /*-----------------------------------------------------------------------*/

    /*-------------------------Exercise Training Object----------------------*/

    public List<ExerciseTrainingObject> fetchExerciseTrainingObjects(long trainingDayId) {
        String[] args = {String.valueOf(trainingDayId)};
        Cursor c = mDB.query(ExerciseTrainingObject.Columns.TABLE, null, ExerciseTrainingObject.Columns.FIELD_TRAINING_DAY_ID + "=?", args, null, null, POSITION_AT_TRAINING);
        List<ExerciseTrainingObject> result = new ArrayList<ExerciseTrainingObject>();
        if (c.moveToFirst()) {
            do {
                ExerciseTrainingObject exerciseTrainingObject = new ExerciseTrainingObject();
                exerciseTrainingObject.setId(c.getLong(c.getColumnIndex(ExerciseTrainingObject.Columns.FIELD_ID)));
                exerciseTrainingObject.setTrainingProgramId(trainingDayId);
                exerciseTrainingObject.setExerciseId(c.getLong(c.getColumnIndex(ExerciseTrainingObject.Columns.FIELD_EXERCISE_ID)));
                exerciseTrainingObject.setPositionAtTraining(c.getInt(c.getColumnIndex(ExerciseTrainingObject.Columns.FIELD_POSITION_AT_TRAINING)));
                exerciseTrainingObject.setSuperset(c.getInt(c.getColumnIndex(ExerciseTrainingObject.Columns.FIELD_SUPERSET_PRESENTS)) == 1);
                exerciseTrainingObject.setPositionAtSuperset(c.getInt(c.getColumnIndex(ExerciseTrainingObject.Columns.FIELD_SUPERSET_POSITION)));
                exerciseTrainingObject.setSupersetId(c.getLong(c.getColumnIndex(ExerciseTrainingObject.Columns.FIELD_SUPERSET_ID)));
                exerciseTrainingObject.setSupersetColor(c.getInt(c.getColumnIndex(ExerciseTrainingObject.Columns.FIELD_SUPERSET_COLOR)));
                result.add(exerciseTrainingObject);
            } while (c.moveToNext());
        }
        return result;
    }

    public void persistExerciseTrainingObject(ExerciseTrainingObject exerciseTrainingObject) {
        ContentValues cv = new ContentValues();
        cv.put(ExerciseTrainingObject.Columns.FIELD_CREATED_AT, System.currentTimeMillis());
        cv.put(ExerciseTrainingObject.Columns.FIELD_SUPERSET_COLOR, exerciseTrainingObject.getSupersetColor());
        cv.put(ExerciseTrainingObject.Columns.FIELD_POSITION_AT_TRAINING, exerciseTrainingObject.getPositionAtTraining());
        cv.put(ExerciseTrainingObject.Columns.FIELD_TRAINING_DAY_ID, exerciseTrainingObject.getTrainingProgramId());
        cv.put(ExerciseTrainingObject.Columns.FIELD_EXERCISE_ID, exerciseTrainingObject.getExerciseId());
        cv.put(ExerciseTrainingObject.Columns.FIELD_SUPERSET_PRESENTS, exerciseTrainingObject.getSuperset());
        cv.put(ExerciseTrainingObject.Columns.FIELD_SUPERSET_ID, exerciseTrainingObject.getSupersetId());
        cv.put(ExerciseTrainingObject.Columns.FIELD_SUPERSET_POSITION, exerciseTrainingObject.getPositionAtSuperset());
        mDB.insert(ExerciseTrainingObject.Columns.TABLE, null, cv);
    }

    public void deleteExerciseTrainingObject(long id) {
        mDB.delete(ExerciseTrainingObject.Columns.TABLE, ExerciseTrainingObject.Columns.FIELD_ID + " = " + id, null);
    }

    public void updateExerciseTrainingObject(ExerciseTrainingObject exerciseTrainingObject) {
        long id = exerciseTrainingObject.getId();
        ContentValues cv = new ContentValues();
        cv.put(ExerciseTrainingObject.Columns.FIELD_CREATED_AT, System.currentTimeMillis());
        cv.put(ExerciseTrainingObject.Columns.FIELD_SUPERSET_COLOR, exerciseTrainingObject.getSupersetColor());
        cv.put(ExerciseTrainingObject.Columns.FIELD_POSITION_AT_TRAINING, exerciseTrainingObject.getPositionAtTraining());
        cv.put(ExerciseTrainingObject.Columns.FIELD_TRAINING_DAY_ID, exerciseTrainingObject.getTrainingProgramId());
        cv.put(ExerciseTrainingObject.Columns.FIELD_EXERCISE_ID, exerciseTrainingObject.getExerciseId());
        cv.put(ExerciseTrainingObject.Columns.FIELD_SUPERSET_PRESENTS, exerciseTrainingObject.getSuperset());
        cv.put(ExerciseTrainingObject.Columns.FIELD_SUPERSET_ID, exerciseTrainingObject.getSupersetId());
        cv.put(ExerciseTrainingObject.Columns.FIELD_SUPERSET_POSITION, exerciseTrainingObject.getPositionAtSuperset());
        mDB.update(ExerciseTrainingObject.Columns.TABLE, cv, ExerciseTrainingObject.Columns.FIELD_ID + " = " + id, null);
    }

    public boolean isAlreadyThisIdInSupersets(int supersetId) {
        String[] args = {String.valueOf(supersetId)};
        Cursor c = mDB.query(DB_TABLE_TRAINING_EXERCISE, null, SUPERSET_ID + "=?", args, null, null, null);
        return c.moveToFirst() && c.getCount() > 0;
    }

    public boolean hasExerciseTrainingObjects() {
        Cursor c = mDB.query(DB_TABLE_TRAINING_EXERCISE, null, null, null, null, null, null);
        return c.moveToFirst();
    }


    /*-----------------------------------------------------------------------*/

    /*-------------------------Training Days---------------------------------*/

    public List<TrainingDay> fetchTrainingDays() {
        List<TrainingDay> trainingDays = new ArrayList<TrainingDay>();
        Cursor c = mDB.query(TrainingDay.Columns.TABLE, null, null, null, null, null, TrainingDay.Columns.FIELD_DAY_OF_WEEK);
        if (c.moveToFirst()) {
            do {
                TrainingDay trainingDay = new TrainingDay();
                trainingDay.setId(c.getLong(0));
                trainingDay.setCreatedAt(c.getLong(c.getColumnIndex(TrainingDay.Columns.FIELD_CREATED_AT)));
                trainingDay.setDayOfWeek(DayOfWeek.getByCode(c.getInt(c.getColumnIndex(TrainingDay.Columns.FIELD_DAY_OF_WEEK))));
                trainingDay.setTrainingName(c.getString(c.getColumnIndex(TrainingDay.Columns.FIELD_TRAINING_NAME)));
                trainingDay.setImageUrl(c.getString(c.getColumnIndex(TrainingDay.Columns.FIELD_IMAGE_URL)));
                trainingDay.setColor(c.getInt(c.getColumnIndex(TrainingDay.Columns.FIELD_COLOR)));
                trainingDays.add(trainingDay);
            } while (c.moveToNext());
        }
        c.close();
        return trainingDays;
    }

    public TrainingDay fetchTrainingDay(long id) {
        TrainingDay trainingDay = new TrainingDay();
        Cursor c = mDB.query(TrainingDay.Columns.TABLE, null, TrainingDay.Columns.FIELD_ID + "=" + String.valueOf(id), null, null, null, TrainingDay.Columns.FIELD_ID);
        if (c.moveToFirst()) {
            trainingDay.setId(c.getLong(0));
            trainingDay.setCreatedAt(c.getLong(c.getColumnIndex(TrainingDay.Columns.FIELD_CREATED_AT)));
            trainingDay.setDayOfWeek(DayOfWeek.getByCode(c.getInt(c.getColumnIndex(TrainingDay.Columns.FIELD_DAY_OF_WEEK))));
            trainingDay.setTrainingName(c.getString(c.getColumnIndex(TrainingDay.Columns.FIELD_TRAINING_NAME)));
            trainingDay.setImageUrl(c.getString(c.getColumnIndex(TrainingDay.Columns.FIELD_IMAGE_URL)));
            trainingDay.setColor(c.getInt(c.getColumnIndex(TrainingDay.Columns.FIELD_COLOR)));
        }
        c.close();
        return trainingDay;
    }

    public long persistTrainingDay(TrainingDay trainingDay) {
        ContentValues cv = new ContentValues();
        cv.put(TrainingDay.Columns.FIELD_CREATED_AT, System.currentTimeMillis());
        cv.put(TrainingDay.Columns.FIELD_TRAINING_NAME, trainingDay.getTrainingName());
        cv.put(TrainingDay.Columns.FIELD_COLOR, trainingDay.getColor());
        cv.put(TrainingDay.Columns.FIELD_DAY_OF_WEEK, trainingDay.getDayOfWeek().getCode());
        cv.put(TrainingDay.Columns.FIELD_IMAGE_URL, trainingDay.getImageUrl());
        return mDB.insert(TrainingDay.Columns.TABLE, null, cv);
    }

    public void deleteTrainingDay(Long id, boolean onlyFromExerciseTable) {
        if (!onlyFromExerciseTable) {
            mDB.delete(TrainingDay.Columns.TABLE, TrainingDay.Columns.FIELD_ID + " = " + id, null);
        }
        mDB.delete(ExerciseTrainingObject.Columns.TABLE, ExerciseTrainingObject.Columns.FIELD_TRAINING_DAY_ID + " = " + id, null);
        close();
    }

    public void updateTrainingDay(TrainingDay trainingDay) {
        long id = trainingDay.getId();
        ContentValues cv = new ContentValues();
        cv.put(TrainingDay.Columns.FIELD_CREATED_AT, System.currentTimeMillis());
        cv.put(TrainingDay.Columns.FIELD_COLOR, trainingDay.getColor());
        cv.put(TrainingDay.Columns.FIELD_IMAGE_URL, trainingDay.getImageUrl());
        cv.put(TrainingDay.Columns.FIELD_DAY_OF_WEEK, trainingDay.getDayOfWeek().getCode());
        cv.put(TrainingDay.Columns.FIELD_TRAINING_NAME, trainingDay.getTrainingName());
        cv.put(TrainingDay.Columns.FIELD_TRAINING_PROGRAM_ID, trainingDay.getTrainingProgramId());
        mDB.update(TrainingDay.Columns.TABLE, cv, TrainingDay.Columns.FIELD_ID + " = " + String.valueOf(id), null);
    }

    public boolean hasTrainingDays() {
        Cursor c = mDB.query(TRAINING_DAYS, null, null, null, null, null, null);
        return c.moveToFirst();
    }

    /*-----------------------------------------------------------------------*/
    /*------------------------Training Programs------------------------------*/

    public void persistTrainingPrograms(TrainingProgram trainingProgram){
        ContentValues cv = new ContentValues();
        // TODO
        mDB.insert(TrainingProgram.Columns.TABLE, null, cv);
    }
    /*-----------------------------------------------------------------------*/
    /*----------------------------Training-----------------------------------*/


    // TODO

    /*-----------------------------------------------------------------------*/


    private class DBHelper extends SQLiteOpenHelper {

        public DBHelper(Context context, String name, CursorFactory factory,
                        int version) {
            super(context, name, factory, version);
        }

        @Override
        public void onCreate(SQLiteDatabase db) {
            db.execSQL(TABLE_EXERCISE_CREATE);
            db.execSQL(TABLE_SET_CREATE);
            db.execSQL(TABLE_MEASURE_CREATE);
            db.execSQL(TABLE_TRAINING_DAY_CREATE);
            db.execSQL(TABLE_TRAINING_CREATE);
            db.execSQL(TABLE_EXERCISE_TRAINING_CREATE);
            db.execSQL(TABLE_TRAINING_PROGRAM_CREATE);
        }

        @Override
        public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
            if (oldVersion == 1 && newVersion == 2) {
                db.execSQL(TABLE_MEASURE_CREATE);
            }
            if (oldVersion == 2 && newVersion == 3) {
                db.execSQL(TABLE_TRAINING_DAY_CREATE);
            }
            if (oldVersion == 3 && newVersion == 4) {
                db.execSQL(TABLE_TRAINING_CREATE);
            }
            if (oldVersion == 4 && newVersion == 5) {
                db.execSQL(TABLE_EXERCISE_TRAINING_CREATE);
                db.execSQL("ALTER TABLE " + DB_TABLE_EXERCISES + " ADD COLUMN " + PART_OF_BODY + " TEXT");
                db.execSQL("ALTER TABLE " + DB_TABLE_SET + " ADD COLUMN " + SUPERSET_EXISTS + " TEXT");
                db.execSQL("ALTER TABLE " + DB_TABLE_SET + " ADD COLUMN " + SUPERSET_ID + " TEXT");
                db.execSQL("ALTER TABLE " + DB_TABLE_SET + " ADD COLUMN " + SUPERSET_COLOR + " TEXT");
                db.execSQL("ALTER TABLE " + DB_TABLE_SET + " ADD COLUMN " + TRAINING_PROGRAM_ID + " TEXT");
                db.execSQL("ALTER TABLE " + DB_TABLE_SET + " ADD COLUMN " + EXERCISE_ID + " TEXT");
            }
            if (oldVersion == 5 && newVersion == 6) {
                db.execSQL(TABLE_TRAINING_PROGRAM_CREATE);
                db.execSQL("ALTER TABLE " + TrainingDay.Columns.TABLE + " ADD COLUMN " + TrainingDay.Columns.FIELD_DAY_OF_WEEK + " INTEGER");
                db.execSQL("ALTER TABLE " + TrainingDay.Columns.TABLE + " ADD COLUMN " + TrainingDay.Columns.FIELD_IMAGE_URL + " TEXT");
                db.execSQL("ALTER TABLE " + TrainingDay.Columns.TABLE + " ADD COLUMN " + TrainingDay.Columns.FIELD_COLOR + " INTEGER");
                db.execSQL("ALTER TABLE " + TrainingDay.Columns.TABLE + " ADD COLUMN " + TrainingDay.Columns.FIELD_CREATED_AT + " INTEGER");
                db.execSQL("ALTER TABLE " + TrainingDay.Columns.TABLE + " ADD COLUMN " + TrainingDay.Columns.FIELD_TRAINING_PROGRAM_ID + " INTEGER");
                db.execSQL("ALTER TABLE " + Training.Columns.TABLE + " ADD COLUMN " + Training.Columns.FIELD_CREATED_AT + " INTEGER");
                db.execSQL("ALTER TABLE " + Training.Columns.TABLE + " ADD COLUMN " + Training.Columns.FIELD_TIME_OF_END + " INTEGER");
                db.execSQL("ALTER TABLE " + Training.Columns.TABLE + " ADD COLUMN " + Training.Columns.FIELD_TRAINING_DAY_ID + " INTEGER");
                db.execSQL("ALTER TABLE " + Training.Columns.TABLE + " ADD COLUMN " + Training.Columns.FIELD_TRAINING_DAY_NAME + " TEXT");
                db.execSQL("ALTER TABLE " + Exercise.Columns.TABLE + " ADD COLUMN " + Exercise.Columns.FIELD_CREATED_AT + " INTEGER");
                db.execSQL("ALTER TABLE " + ExerciseTrainingObject.Columns.TABLE + " ADD COLUMN " + ExerciseTrainingObject.Columns.FIELD_CREATED_AT + " INTEGER");
                db.execSQL("ALTER TABLE " + Set.Columns.TABLE + " ADD COLUMN " + Set.Columns.FIELD_SUPERSET_PRESENTS + " BOOLEAN");
                db.execSQL("ALTER TABLE " + Set.Columns.TABLE + " ADD COLUMN " + Set.Columns.FIELD_SUPERSET_ID + " INTEGER");
                db.execSQL("ALTER TABLE " + Set.Columns.TABLE + " ADD COLUMN " + Set.Columns.FIELD_SUPERSET_COLOR + " INTEGER");
                db.execSQL("ALTER TABLE " + Set.Columns.TABLE + " ADD COLUMN " + Set.Columns.FIELD_TRAINING_DAY_ID + " INTEGER");
                db.execSQL("ALTER TABLE " + Set.Columns.TABLE + " ADD COLUMN " + Set.Columns.FIELD_EXERCISE_ID + " INTEGER");
                db.execSQL("ALTER TABLE " + Set.Columns.TABLE + " ADD COLUMN " + Set.Columns.FIELD_CREATED_AT + " INTEGER");
                db.execSQL("ALTER TABLE " + Set.Columns.TABLE + " ADD COLUMN " + Set.Columns.FIELD_POSITION_AT_TRAINING + " INTEGER");
                db.execSQL("ALTER TABLE " + Set.Columns.TABLE + " ADD COLUMN " + Set.Columns.FIELD_SUPERSET_POSITION + " INTEGER");


                /*  Training Days moving to new version */
                Cursor c = db.query(TrainingDay.Columns.TABLE, null, null, null, null, null, null);
                if (c.moveToFirst()) {
                    int dayOkWeek = 1;
                    do {
                        TrainingDay trainingDay = new TrainingDay();
                        trainingDay.setId(c.getLong(0));
                        trainingDay.setCreatedAt(System.currentTimeMillis());
                        trainingDay.setDayOfWeek(DayOfWeek.getByCode(dayOkWeek));
                        trainingDay.setTrainingName(c.getString(c.getColumnIndex(TrainingDay.Columns.FIELD_TRAINING_NAME)));
                        trainingDay.setImageUrl("");
                        trainingDay.setColor(c.getInt(c.getColumnIndex(TrainingDay.Columns.FIELD_COLOR)));

                        ContentValues cv = new ContentValues();
                        cv.put(TrainingDay.Columns.FIELD_CREATED_AT, System.currentTimeMillis());
                        cv.put(TrainingDay.Columns.FIELD_COLOR, trainingDay.getColor());
                        cv.put(TrainingDay.Columns.FIELD_IMAGE_URL, trainingDay.getImageUrl());
                        cv.put(TrainingDay.Columns.FIELD_DAY_OF_WEEK, trainingDay.getDayOfWeek().getCode());
                        cv.put(TrainingDay.Columns.FIELD_TRAINING_NAME, trainingDay.getTrainingName());
                        cv.put(TrainingDay.Columns.FIELD_TRAINING_PROGRAM_ID, trainingDay.getTrainingProgramId());
                        db.update(TrainingDay.Columns.TABLE, cv, TrainingDay.Columns.FIELD_ID + " = " + String.valueOf(trainingDay.getId()), null);
                        dayOkWeek++;
                        if (dayOkWeek > 7) {
                            dayOkWeek = 1;
                        }
                    } while (c.moveToNext());
                }
            }
        }
    }
}
