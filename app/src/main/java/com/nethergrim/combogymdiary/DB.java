package com.nethergrim.combogymdiary;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteDatabase.CursorFactory;
import android.database.sqlite.SQLiteOpenHelper;

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
    public static final String COMMENT_TO_TRAINING = "comment_to_training";
    public static final String DB_TABLE_SET = "main_tab";
    public static final String DB_TABLE_TRAINING_EXERCISE = "training_exercise";
    public static final String DATE = "Date";
    public static final String WEIGHT = "Weight";
    public static final String REPS = "Reps";
    public static final String SET = "SetsN";
    public static final String TOTAL_TIME_OF_TRAINING = "time_of_training";
    public static final String DB_TABLE_TRAINING = "comment_table";
    public static final String PART_OF_BODY = "part_of_body";
    public static final String TOTAL_WEIGHT_OF_TRAINING = "total_weight";
    public static final String DB_MEASURE_TABLE = "measurements_tab";
    public static final String PART_OF_BODY_FOR_MEASURING = "part_of_body";
    public static final String MEASURE_VALUE = "measure_value";
    public static final String TRAINING_DAYS = "trainings_tab";
    public static final String strSeparator = "__,__";
    public static final String SIMPLE_DATE_FORMAT = "dd.MM.yyyy";
    public static final String SUPERSET_EXISTS = "superset";
    public static final String SUPERSET_POSITION = "superset_position";
    public static final String SUPERSET_ID = "superset_id";
    public static final String SUPERSET_COLOR = "superset_color";
    public static final String POSITION_AT_TRAINING = "position_at_training";
    public static final String EXERCISE_ID = "training_exercise_id";
    public static final String TRAINING_PROGRAM_ID = "training_program_id";

    private static final int DB_VERSION = 6;
    private static final String DB_TABLE_EXERCISES = "exe_tab";
    private Context mCtx;
    private DBHelper mDBHelper;
    private SQLiteDatabase mDB;


    private static final String TABLE_SET_CREATE = "create table " + DB_TABLE_SET + "("
            + _ID + " integer primary key autoincrement, "
            + TRAINING_NAME + " text, "
            + EXERCISE_NAME + " text, "
            + DATE + " text, "
            + WEIGHT + " integer, "
            + REPS + " integer, "
            + SET + " integer, "
            + SUPERSET_EXISTS + " boolean, "
            + SUPERSET_ID + " integer, "
            + SUPERSET_COLOR + " integer, "
            + TRAINING_PROGRAM_ID + " integer, "
            + EXERCISE_ID + " integer"
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

    private static final String TABLE_MEASURE_CREATE = "create table " +   DB_MEASURE_TABLE + "(" +
            _ID + " integer primary key autoincrement, " +
            DATE + " text, " +
            PART_OF_BODY_FOR_MEASURING + " text, " +
            MEASURE_VALUE + " text" + ");";

    private static final String TABLE_TRAINING_DAY_CREATE = "create table "  + TRAINING_DAYS + "("
            + TrainingDay.Columns.FIELD_ID + " integer primary key autoincrement, "
            + TrainingDay.Columns.FIELD_TRAINING_NAME + " text, "
            + TrainingDay.Columns.FIELD_DAY_OF_WEEK + " integer, "
            + TrainingDay.Columns.FIELD_IMAGE_URL + " text, "
            + TrainingDay.Columns.FIELD_COLOR + " integer, "
            + TrainingDay.Columns.FIELD_CREATED_AT + " integer, "
            + TrainingDay.Columns.FIELD_TRAINING_PROGRAM_ID + " integer"
            + ");";

    private static final String TABLE_TRAINING_EXERCISE_CREATE = "create table "  + DB_TABLE_TRAINING_EXERCISE + "("
            + _ID + " integer primary key autoincrement, "
            + TRAINING_PROGRAM_ID + " integer, "
            + EXERCISE_ID + " integer, "
            + POSITION_AT_TRAINING + " integer, "
            + SUPERSET_EXISTS + " boolean, "
            + SUPERSET_POSITION + " integer, "
            + SUPERSET_ID + " integer, "
            + SUPERSET_COLOR + " integer"
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


    public DB(Context ctx) {
        mCtx = ctx;
        open();
    }

    public long addExerciseTrainingObject(ExerciseTrainingObject object){
        ContentValues cv = new ContentValues();
        cv.put(TRAINING_PROGRAM_ID, object.getTrainingProgramId());
        cv.put(EXERCISE_ID, object.getExerciseId());
        cv.put(POSITION_AT_TRAINING, object.getPositionAtTraining());
        cv.put(SUPERSET_EXISTS, object.isSuperset());
        cv.put(SUPERSET_POSITION, object.getPositionAtSuperset());
        cv.put(SUPERSET_ID, object.getSupersetId());
        cv.put(SUPERSET_COLOR, object.getSupersetColor());
        return mDB.insert(DB_TABLE_TRAINING_EXERCISE, null, cv);
    }

    public boolean hasExerciseTrainingObjects(){
        Cursor c = mDB.query(DB_TABLE_TRAINING_EXERCISE,null,null,null,null,null,null);
        return c.moveToFirst();
    }

    public boolean hasTrainingPrograms(){
        Cursor c = mDB.query(TRAINING_DAYS,null,null,null,null,null,null);
        return c.moveToFirst();
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

//    public int getExeIdByName(String name) {
//        String[] args = {name};
//        String[] cols = {DB._ID};
//        Cursor c = mDB.query(DB_TABLE_EXERCISES, cols, DB.EXERCISE_NAME + "=?", args,  null, null,  null);
//        if (c.moveToFirst()) {
//            return c.getInt(0);
//        } else {
//            return 0;
//        }
//    }

    public Cursor getDataMain(String groupBy) {
        return mDB.query(DB_TABLE_SET, null, null, null, groupBy, null, null);
    }

    public boolean delRecordMeasurement(String date) {
        String[] args = {date};
        int tmp = mDB.delete(DB_MEASURE_TABLE, DATE + "=?", args);
        return tmp > 0;
    }

    public void deleteTrainingDay(Long id, boolean onlyFromExerciseTable){
        if (!onlyFromExerciseTable){
            mDB.delete(TRAINING_DAYS, _ID + " = " + id, null);
        }
        mDB.delete(DB_TABLE_TRAINING_EXERCISE, TRAINING_PROGRAM_ID + " = " + id, null);
        close();
    }

    public void updateTrainingDay(TrainingDay trainingDay){
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

    public int getLastWeightOrReps(String _exeName, int _set, boolean ifWeight) {
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

//    public String getTimerValueByExerciseName(String exeName) {
//        String result = "60";
//        String[] cols = {DB.TIMER_VALUE};
//        String[] tags = {exeName};
//        Cursor c1 = mDB.query(DB_TABLE_EXERCISES, cols, DB.EXERCISE_NAME + "=?", tags, null, null, null, null);
//        if (c1.moveToFirst()) {
//            result = c1.getString(0);
//        }
//        return result;
//    }

    public int getThisWeight(int currentSet, String exeName) {
        int result = 0;
        String[] args = {exeName};
        Cursor c = mDB.query(DB_TABLE_SET, null, EXERCISE_NAME + "=?", args, null, null, null);
        if (c.moveToLast()) {
            do {
                if (c.getInt(6) == currentSet) {
                    result = c.getInt(4);
                    break;
                }
            } while (c.moveToPrevious());
        }
        return result;
    }

    public int getThisReps(int currentSet, String exeName) {
        int result = 0;
        String[] args = {exeName};
        Cursor c = mDB.query(DB_TABLE_SET, null, EXERCISE_NAME + "=?", args, null, null, null);
        if (c.moveToLast()) {
            do {
                if (c.getInt(6) == currentSet) {
                    result = c.getInt(5);
                    break;
                }
            } while (c.moveToPrevious());
        }
        return result;
    }

    public int getThisId(int currentSet, String exeName) {
        int result = 0;
        String[] args = {exeName};
        Cursor c = mDB.query(DB_TABLE_SET, null, EXERCISE_NAME + "=?", args, null,
                null, null);
        if (c.moveToLast()) {
            do {
                if (c.getInt(6) == currentSet) {
                    result = c.getInt(0);
                    break;
                }
            } while (c.moveToPrevious());
        }
        return result;
    }

    public Cursor getDataMain(String[] column, String selection, String[] selectionArgs, String groupBy, String having, String orderedBy) {
        return mDB.query(DB_TABLE_SET, column, selection, selectionArgs, groupBy, having, orderedBy);
    }

//    public Cursor getDataExe(String[] column, String selection, String[] selectionArgs, String groupBy, String having, String orderedBy) {
//        return mDB.query(DB_TABLE_EXERCISES, column, selection, selectionArgs,  groupBy, having, orderedBy);
//    }

    public Cursor getDataTrainings(String[] column, String selection, String[] selectionArgs, String groupBy, String having, String orderedBy) {
        return mDB.query(TRAINING_DAYS, column, selection, selectionArgs, groupBy, having, orderedBy);
    }

    public Cursor getCommentData(String date) {
        String[] args = {date};
        Cursor c = mDB.query(DB_TABLE_TRAINING, null, DATE + "=?", args, null, null, null);
        return c;
    }

    public Cursor getDataMeasures(String[] column, String selection, String[] selectionArgs, String groupBy, String having, String orderedBy) {
        return mDB.query(DB_MEASURE_TABLE, column, selection, selectionArgs, groupBy, having, orderedBy);
    }

    public long addExercise(Exercise exercise) {
        ContentValues cv = new ContentValues();
        cv.put(Exercise.Columns.FIELD_EXERCISE_NAME, exercise.getName());
        cv.put(Exercise.Columns.FIELD_TIMER_VALUE, exercise.getTimer());
        cv.put(Exercise.Columns.FIELD_PART_OF_BODY, exercise.getPartOfBody());
        cv.put(Exercise.Columns.FIELD_CREATED_AT, exercise.getCreatedAt());
        return mDB.insert(Exercise.Columns.TABLE, null, cv);
    }

    public List<ExerciseGroup> getExerciseGroups() {  // returns groups of exercises, sorted by part_of_body
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

    public Exercise getExercise(long id) {
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

    public List<ExerciseTrainingObject> getExerciseTrainingObjects(int trainingId){
        String[] args = {String.valueOf(trainingId)};
        Cursor c = mDB.query(DB_TABLE_TRAINING_EXERCISE, null, TRAINING_PROGRAM_ID + "=?", args,null,null,POSITION_AT_TRAINING);
        List<ExerciseTrainingObject> result = new ArrayList<ExerciseTrainingObject>();
        if (c.moveToFirst()){
            do {
                ExerciseTrainingObject exerciseTrainingObject = new ExerciseTrainingObject();
                exerciseTrainingObject.setId(c.getInt(0));
                exerciseTrainingObject.setTrainingProgramId(trainingId);
                exerciseTrainingObject.setExerciseId(c.getInt(2));
                exerciseTrainingObject.setPositionAtTraining(c.getInt(3));
                exerciseTrainingObject.setSuperset(c.getInt(4) == 1 ? true : false);
                exerciseTrainingObject.setPositionAtSuperset(c.getInt(5));
                exerciseTrainingObject.setSupersetId(c.getInt(6));
                exerciseTrainingObject.setSupersetColor(c.getInt(7));
                result.add(exerciseTrainingObject);
            } while (c.moveToNext());
        }
        return result;
    }

    public List<Set> getSets(int trainingID){
        String[] args = {String.valueOf(trainingID)};
        Cursor c = mDB.query(DB_TABLE_TRAINING_EXERCISE, null, TRAINING_PROGRAM_ID + "=?", args,null,null,POSITION_AT_TRAINING);
        List<Set> result = new ArrayList<Set>();
        try {
            if (c.moveToFirst()){
                do {
                    Set set = new Set();
                    set.setId(c.getInt(0));
                    set.setTrainingProgramId(trainingID);
                    set.setExerciseId(c.getInt(2));
                    set.setPositionAtTraining(c.getInt(3));
                    set.setSuperset(c.getInt(4) == 1 ? true : false);
                    set.setPositionAtSuperset(c.getInt(5));
                    set.setSupersetId(c.getInt(6));
                    set.setSupersetColor(c.getInt(7));
                    set.setExerciseName(this.getExercise(c.getInt(2)).getName());
                    result.add(set);
                } while (c.moveToNext());
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return result;
    }

    public boolean isAlreadyThisIdInSupersets(int supersetId){
        String[] args = {String.valueOf(supersetId)};
        Cursor c = mDB.query(DB_TABLE_TRAINING_EXERCISE,null, SUPERSET_ID + "=?",args,null,null,null);
        if (c.moveToFirst() && c.getCount() > 0){
            return true;
        }
        return false;
    }

//    public int getExerciseId(String exerciseName){
//        String[] args = {exerciseName};
//        Cursor c = mDB.query(DB_TABLE_EXERCISES, null, EXERCISE_NAME + "=?", args, null, null, null);
//        if (c.moveToFirst()){
//            return c.getInt(0);
//        }
//        return 0;
//    }

    public List<TrainingDay> getTrainingDays(){
        List<TrainingDay> trainingDays = new ArrayList<TrainingDay>();
        Cursor c = mDB.query(TrainingDay.Columns.TABLE, null,null,null,null,null,TrainingDay.Columns.FIELD_DAY_OF_WEEK);
        if (c.moveToFirst()){
            do {
                TrainingDay trainingDay = new TrainingDay();
                trainingDay.setId(c.getLong(0));
                trainingDay.setCreatedAt(c.getLong(c.getColumnIndex(TrainingDay.Columns.FIELD_CREATED_AT)));
                trainingDay.setDayOfWeek(DayOfWeek.getByCode(c.getInt(c.getColumnIndex(TrainingDay.Columns.FIELD_DAY_OF_WEEK))));
                trainingDay.setTrainingName(c.getString(c.getColumnIndex(TrainingDay.Columns.FIELD_TRAINING_NAME)));
                trainingDay.setImageUrl(c.getString(c.getColumnIndex(TrainingDay.Columns.FIELD_IMAGE_URL)));
                trainingDay.setColor(c.getInt(c.getColumnIndex(TrainingDay.Columns.FIELD_COLOR)));
                trainingDays.add(trainingDay);
            } while(c.moveToNext());
        }
        c.close();
        return  trainingDays;
    }

    public TrainingDay getTrainingDay(long id){
        TrainingDay trainingDay = new TrainingDay();
        Cursor c = mDB.query(TrainingDay.Columns.TABLE, null,TrainingDay.Columns.FIELD_ID + "=" + String.valueOf(id),null,null,null,TrainingDay.Columns.FIELD_ID);
        if (c.moveToFirst()){
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

    public void persistTrainingDay(TrainingDay trainingDay){
        ContentValues cv = new ContentValues();
        cv.put(TrainingDay.Columns.FIELD_CREATED_AT, System.currentTimeMillis());
        cv.put(TrainingDay.Columns.FIELD_TRAINING_NAME, trainingDay.getTrainingName());
        cv.put(TrainingDay.Columns.FIELD_COLOR, trainingDay.getColor());
        cv.put(TrainingDay.Columns.FIELD_DAY_OF_WEEK, trainingDay.getDayOfWeek().getCode());
        cv.put(TrainingDay.Columns.FIELD_IMAGE_URL, trainingDay.getImageUrl());
        mDB.insert(TrainingDay.Columns.TABLE, null, cv);
    }

    public String getTrainingName(int id) {
        String[] args = {id + ""};
        Cursor c = mDB.query(TRAINING_DAYS, null, _ID + "=?", args, null, null, null);
        if (c.moveToFirst()) {
            return c.getString(1);
        } else
            return "";
    }

    public long persistTrainings(String traName) {
        ContentValues cv = new ContentValues();
        cv.put(TRAINING_NAME, traName);
        return mDB.insert(TRAINING_DAYS, null, cv);
    }

    public void persistMeasure(String date, String part_of_body, String value) {
        ContentValues cv = new ContentValues();
        cv.put(DATE, date);
        cv.put(PART_OF_BODY_FOR_MEASURING, part_of_body);
        cv.put(MEASURE_VALUE, value);
        mDB.insert(DB_MEASURE_TABLE, null, cv);
    }

    public void persistSet(String traName, String exeName, String date, int weight, int reps, int set, boolean isInSuperset, int supersetId, int supersetColor, int trainingId, int exerciseID) {
        ContentValues cv = new ContentValues();
        cv.put(EXERCISE_NAME, exeName);
        cv.put(TRAINING_NAME, traName);
        cv.put(DATE, date);
        cv.put(WEIGHT, weight);
        cv.put(REPS, reps);
        cv.put(SET, set);
        cv.put(SUPERSET_EXISTS, isInSuperset);
        cv.put(SUPERSET_ID, supersetId);
        cv.put(SUPERSET_COLOR, supersetColor);
        cv.put(TRAINING_PROGRAM_ID, trainingId);
        cv.put(EXERCISE_ID, exerciseID);
        mDB.insert(DB_TABLE_SET, null, cv);
    }

    public void addRecComment(String date, String comment, int totalWeight, String time) {
        ContentValues cv = new ContentValues();
        cv.put(DATE, date);
        cv.put(COMMENT_TO_TRAINING, comment);
        cv.put(TOTAL_WEIGHT_OF_TRAINING, totalWeight);
        cv.put(TOTAL_TIME_OF_TRAINING, time);
        mDB.insert(DB_TABLE_TRAINING, null, cv);
    }

    public Cursor getDataComment(String[] cols, String selection, String[] args, String groupby, String having, String orderBy) {
        return mDB.query(DB_TABLE_TRAINING, cols, selection, args, groupby, having, orderBy);
    }

//    public void updateExercise(int Id, String column, String data) {
//        ContentValues cv1 = new ContentValues();
//        cv1.put(column, data);
//        mDB.update(Exercise.Columns.TABLE, cv1, Exercise.Columns.FIELD_ID + " = " + Id, null);
//    }

    public void updateRec_Main(int Id, int colId, String data_str, int data_int) {
        ContentValues cv = new ContentValues();
        if (colId == 1) {
            cv.put(TRAINING_NAME, data_str);
        } else if (colId == 2) {
            cv.put(EXERCISE_NAME, data_str);
        } else if (colId == 3) {
            cv.put(DATE, data_str);
        } else if (colId == 4) {
            cv.put(WEIGHT, data_int);
        } else if (colId == 5) {
            cv.put(REPS, data_int);
        } else if (colId == 6) {
            cv.put(SET, data_int);
        }
        mDB.update(DB_TABLE_SET, cv, _ID + " = " + Id, null);
    }

    public void deleteExercise(long id) {
        mDB.delete(DB_TABLE_TRAINING_EXERCISE, _ID + " = " + id, null);
        mDB.delete(DB_TABLE_EXERCISES, _ID + " = " + id, null);
    }

    public void delRec_Main(long id) {
        mDB.delete(DB_TABLE_SET, _ID + " = " + id, null);
    }

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
            db.execSQL(TABLE_TRAINING_EXERCISE_CREATE);
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
                db.execSQL(TABLE_TRAINING_EXERCISE_CREATE);
                db.execSQL("ALTER TABLE " + DB_TABLE_EXERCISES + " ADD COLUMN " + PART_OF_BODY + " TEXT");
                db.execSQL("ALTER TABLE " + DB_TABLE_SET + " ADD COLUMN " + SUPERSET_EXISTS + " TEXT");
                db.execSQL("ALTER TABLE " + DB_TABLE_SET + " ADD COLUMN " + SUPERSET_ID + " TEXT");
                db.execSQL("ALTER TABLE " + DB_TABLE_SET + " ADD COLUMN " + SUPERSET_COLOR + " TEXT");
                db.execSQL("ALTER TABLE " + DB_TABLE_SET + " ADD COLUMN " + TRAINING_PROGRAM_ID + " TEXT");
                db.execSQL("ALTER TABLE " + DB_TABLE_SET + " ADD COLUMN " + EXERCISE_ID + " TEXT");
            }
            if (oldVersion == 5 && newVersion == 6){
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


                /*  Training Days moving to new version */
                Cursor c = db.query(TrainingDay.Columns.TABLE, null,null, null,null,null,null);
                if (c.moveToFirst()){
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
                        if (dayOkWeek > 7){
                            dayOkWeek = 1;
                        }
                    } while(c.moveToNext());
                }
            }
        }
    }
}
