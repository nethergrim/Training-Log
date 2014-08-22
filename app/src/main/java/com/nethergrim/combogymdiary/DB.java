package com.nethergrim.combogymdiary;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteDatabase.CursorFactory;
import android.database.sqlite.SQLiteOpenHelper;

import com.nethergrim.combogymdiary.model.Exercise;
import com.nethergrim.combogymdiary.model.ExerciseGroup;
import com.nethergrim.combogymdiary.model.ExerciseTrainingObject;
import com.nethergrim.combogymdiary.model.TrainingRow;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;


public class DB {

    public static final String LOG_TAG = "myLogs";
    public static final String DB_NAME = "mydb";
    public static final String _ID = "_id";
    public static final String EXERCISE_NAME = "exercise_name";
    public static final String TRAINING_NAME = "training_name";
    public static final String TIMER_VALUE = "timer_value";
    public static final String COMMENT_TO_TRAINING = "comment_to_training";
    public static final String DB_TABLE_MAIN = "main_tab";
    public static final String DB_TABLE_TRAINING_EXERCISE = "training_exercise";
    public static final String DATE = "Date";
    public static final String WEIGHT = "Weight";
    public static final String REPS = "Reps";
    public static final String SET = "SetsN";
    public static final String TOTAL_TIME_OF_TRAINING = "time_of_training";
    public static final String DB_TABLE_COMMENT = "comment_table";
    public static final String PART_OF_BODY = "part_of_body";
    public static final String TOTAL_WEIGHT_OF_TRAINING = "total_weight";
    public static final String DB_MEASURE_TABLE = "measurements_tab";
    public static final String PART_OF_BODY_FOR_MEASURING = "part_of_body";
    public static final String MEASURE_VALUE = "measure_value";
    public static final String DB_TRAININGS_TABLE = "trainings_tab";
    public static final String strSeparator = "__,__";
    public static final String SIMPLE_DATE_FORMAT = "dd.MM.yyyy";
    public static final String SUPERSET_EXISTS = "superset";
    public static final String SUPERSET_POSITION = "superset_position";
    public static final String SUPERSET_ID = "superset_id";
    public static final String SUPERSET_COLOR = "superset_color";
    public static final String POSITION_AT_TRAINING = "position_at_training";
    public static final String EXERCISE_ID = "training_exercise_id";
    public static final String TRAINING_PROGRAM_ID = "training_program_id";
    private static final int DB_VERSION = 5;
    private static final String DB_EXE_TABLE = "exe_tab";
    private Context mCtx;
    private DBHelper mDBHelper;
    private SQLiteDatabase mDB;


    private static final String DB_MAIN_CREATE = "create table " + DB_TABLE_MAIN + "("
            + _ID + " integer primary key autoincrement, "
            + TRAINING_NAME + " text, "
            + EXERCISE_NAME + " text, "
            + DATE + " text, "
            + WEIGHT + " integer, "
            + REPS + " integer, "
            + SET + " integer" + ");";

    private static final String DB_COMMENT_CREATE = "create table "
            + DB_TABLE_COMMENT + "("
            + _ID + " integer primary key autoincrement, "
            + DATE + " text, "
            + COMMENT_TO_TRAINING + " text, "
            + TOTAL_TIME_OF_TRAINING + " text, "
            + TOTAL_WEIGHT_OF_TRAINING + " integer" + ");";

    private static final String DB_MEASURE_CREATE = "create table " +   DB_MEASURE_TABLE + "(" +
            _ID + " integer primary key autoincrement, " +
            DATE + " text, " +
            PART_OF_BODY_FOR_MEASURING + " text, " +
            MEASURE_VALUE + " text" + ");";

    private static final String DB_TRAININGS_CREATE = "create table "  + DB_TRAININGS_TABLE + "("
            + _ID + " integer primary key autoincrement, "
            + TRAINING_NAME + " text, "
            + EXERCISE_NAME + " text"             // UNUSED, DEPRECATED!
            + ");";

    private static final String DB_TRAINING_EXERCISE_CREATE = "create table "  + DB_TABLE_TRAINING_EXERCISE + "("
            + _ID + " integer primary key autoincrement, "
            + TRAINING_PROGRAM_ID + " integer, "
            + EXERCISE_ID + " integer, "
            + POSITION_AT_TRAINING + " integer, "
            + SUPERSET_EXISTS + " boolean, "
            + SUPERSET_POSITION + " integer, "
            + SUPERSET_ID + " integer, "
            + SUPERSET_COLOR + " integer"
            + ");";

    private static final String DB_EXE_CREATE = "create table " + DB_EXE_TABLE + "("
            + _ID + " integer primary key autoincrement, "
            + TRAINING_NAME + " text, "           // UNUSED, DEPRECATED!
            + EXERCISE_NAME + " text, "
            + TIMER_VALUE + " text, "
            + PART_OF_BODY + " text"
            + ");";


    public DB(Context ctx) {
        mCtx = ctx;
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

    public String convertArrayToString(String[] array) {
        String str = "";
        for (int i = 0; i < array.length; i++) {
            str = str + array[i];
            if (i < array.length - 1) {
                str = str + strSeparator;
            }
        }
        return str;
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

    public String getExerciseByID(int id) {
        String[] args = {"" + id};
        Cursor c = mDB.query(DB.DB_EXE_TABLE, null, DB._ID + "=?", args, null, null, null);
        if (c.moveToFirst()) {
            return c.getString(2);
        } else
            return null;
    }

    public int getExeIdByName(String name) {
        String[] args = {name};
        String[] cols = {DB._ID};
        Cursor c = mDB.query(DB_EXE_TABLE, cols, DB.EXERCISE_NAME + "=?", args,  null, null,  null);
        if (c.moveToFirst()) {
            return c.getInt(0);
        } else {
            return 0;
        }
    }

    public Cursor getDataMain(String groupBy) {
        return mDB.query(DB_TABLE_MAIN, null, null, null, groupBy, null, null);
    }

    public boolean delRecordMeasurement(String date) {
        String[] args = {date};
        int tmp = mDB.delete(DB_MEASURE_TABLE, DATE + "=?", args);
        return tmp > 0;
    }

    public void deleteTrainingProgram(int id){
        mDB.delete(DB_TRAININGS_TABLE, _ID + " = " + id, null);
        mDB.delete(DB_TABLE_TRAINING_EXERCISE, TRAINING_PROGRAM_ID + " = " + id, null);
    }

    public String getTrainingList(int _id) {
        Cursor c = mDB.query(DB_TRAININGS_TABLE, null, null, null, null, null, null);
        if (c.moveToFirst()) {
            do {
                if (c.getInt(0) == _id) {
                    return c.getString(2);
                }
            } while ((c.moveToNext()));
        }
        return null;
    }

    public int getLastWeightOrReps(String _exeName, int _set, boolean ifWeight) {
        String[] cols = {DB.WEIGHT, DB.SET};
        if (ifWeight) {
            cols[0] = DB.WEIGHT;
        } else {
            cols[0] = DB.REPS;
        }

        String[] tags = {_exeName};
        Cursor c = mDB.query(DB_TABLE_MAIN, cols, DB.EXERCISE_NAME + "=?", tags,
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

    public String getTimerValueByExerciseName(String exeName) {
        String result = "60";
        String[] cols = {DB.TIMER_VALUE};
        String[] tags = {exeName};
        Cursor c1 = mDB.query(DB_EXE_TABLE, cols, DB.EXERCISE_NAME + "=?", tags, null, null, null, null);
        if (c1.moveToFirst()) {
            result = c1.getString(0);
        }
        return result;
    }

    public int getThisWeight(int currentSet, String exeName) {
        int result = 0;
        String[] args = {exeName};
        Cursor c = mDB.query(DB_TABLE_MAIN, null, EXERCISE_NAME + "=?", args, null, null, null);
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
        Cursor c = mDB.query(DB_TABLE_MAIN, null, EXERCISE_NAME + "=?", args, null, null, null);
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
        Cursor c = mDB.query(DB_TABLE_MAIN, null, EXERCISE_NAME + "=?", args, null,
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
        return mDB.query(DB_TABLE_MAIN, column, selection, selectionArgs, groupBy, having, orderedBy);
    }

    public Cursor getDataExe(String[] column, String selection, String[] selectionArgs, String groupBy, String having, String orderedBy) {
        return mDB.query(DB_EXE_TABLE, column, selection, selectionArgs,  groupBy, having, orderedBy);
    }

    public Cursor getDataTrainings(String[] column, String selection, String[] selectionArgs, String groupBy, String having, String orderedBy) {
        return mDB.query(DB_TRAININGS_TABLE, column, selection, selectionArgs, groupBy, having, orderedBy);
    }

    public Cursor getCommentData(String date) {
        String[] args = {date};
        Cursor c = mDB.query(DB_TABLE_COMMENT, null, DATE + "=?", args, null, null, null);
        return c;
    }

    public Cursor getDataMeasures(String[] column, String selection, String[] selectionArgs, String groupBy, String having, String orderedBy) {
        return mDB.query(DB_MEASURE_TABLE, column, selection, selectionArgs, groupBy, having, orderedBy);
    }

    public long addExercise(String exeName, String timer, String partOfBody) {
        ContentValues cv = new ContentValues();
        cv.put(EXERCISE_NAME, exeName);
        cv.put(TIMER_VALUE, timer);
        cv.put(PART_OF_BODY, partOfBody);
        return mDB.insert(DB_EXE_TABLE, null, cv);
    }

    public List<Exercise> getExercises() {
        Cursor c = mDB.query(DB_EXE_TABLE, null, null, null, null, null, null);
        List<Exercise> exercises = new ArrayList<Exercise>();
        if (c.moveToFirst()) {
            do {
                exercises.add(new Exercise(c.getInt(0), c.getString(2), c.getString(3), c.getString(4)));
            } while (c.moveToNext());
        }
        return exercises;
    }

    public List<ExerciseGroup> getExerciseGroups() {  // returns groups of exercises, sorted by part_of_body
        List<ExerciseGroup> result = new ArrayList<ExerciseGroup>();
        for (int i = 0; i < Constants.PARTS_OF_BODY.length; i++) {
            ExerciseGroup exerciseGroup = new ExerciseGroup();
            exerciseGroup.setPositionInGlobalArray(i);
            exerciseGroup.setName(Constants.PARTS_OF_BODY[i]);
            String[] args = {Constants.PARTS_OF_BODY[i]};
            Cursor c = mDB.query(DB_EXE_TABLE, null, PART_OF_BODY + "=?", args, null, null, EXERCISE_NAME);
            List<Exercise> exercises = new ArrayList<Exercise>();
            if (c.moveToFirst()) {
                do {
                    exercises.add(new Exercise(c.getInt(0), c.getString(2), c.getString(3), c.getString(4)));
                } while (c.moveToNext());
                exerciseGroup.setList(exercises);
                result.add(exerciseGroup);
            }
        }
        return result;
    }

    public Exercise getExercise(long id) {
        String[] args = {id + ""};
        Cursor c = mDB.query(DB_EXE_TABLE, null, _ID + "=?", args, null, null, null);
        if (c.moveToFirst()) {
            return new Exercise(c.getInt(0), c.getString(2), c.getString(3), c.getString(4));
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

    public List<TrainingRow> getTrainingRows(int trainingID){
        String[] args = {String.valueOf(trainingID)};
        Cursor c = mDB.query(DB_TABLE_TRAINING_EXERCISE, null, TRAINING_PROGRAM_ID + "=?", args,null,null,POSITION_AT_TRAINING);
        List<TrainingRow> result = new ArrayList<TrainingRow>();
        if (c.moveToFirst()){
            do {
                TrainingRow trainingRow = new TrainingRow();
                trainingRow.setId(c.getInt(0));
                trainingRow.setTrainingProgramId(trainingID);
                trainingRow.setExerciseId(c.getInt(2));
                trainingRow.setPositionAtTraining(c.getInt(3));
                trainingRow.setSuperset(c.getInt(4) == 1 ? true : false);
                trainingRow.setPositionAtSuperset(c.getInt(5));
                trainingRow.setSupersetId(c.getInt(6));
                trainingRow.setSupersetColor(c.getInt(7));
                trainingRow.setExerciseName(this.getExercise(trainingRow.getExerciseId()).getName());
                result.add(trainingRow);
            } while (c.moveToNext());
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

    public int getExerciseId(String exerciseName){
        String[] args = {exerciseName};
        Cursor c = mDB.query(DB_EXE_TABLE, null, EXERCISE_NAME + "=?", args, null, null, null);
        if (c.moveToFirst()){
            return c.getInt(0);
        }
        return 0;
    }

    public String getTrainingName(int id) {
        String[] args = {id + ""};
        Cursor c = mDB.query(DB_TRAININGS_TABLE, null, _ID + "=?", args, null, null, null);
        if (c.moveToFirst()) {
            return c.getString(1);
        } else
            return "";
    }

    public long addTrainings(String traName) {
        ContentValues cv = new ContentValues();
        cv.put(TRAINING_NAME, traName);
        return mDB.insert(DB_TRAININGS_TABLE, null, cv);
    }

    public void addRecMeasure(String date, String part_of_body, String value) {
        ContentValues cv = new ContentValues();
        cv.put(DATE, date);
        cv.put(PART_OF_BODY_FOR_MEASURING, part_of_body);
        cv.put(MEASURE_VALUE, value);
        mDB.insert(DB_MEASURE_TABLE, null, cv);
    }

    public void addRecMainTable(String traName, String exeName, String date, int weight, int reps, int set) {
        ContentValues cv = new ContentValues();
        cv.put(EXERCISE_NAME, exeName);
        cv.put(TRAINING_NAME, traName);
        cv.put(DATE, date);
        cv.put(WEIGHT, weight);
        cv.put(REPS, reps);
        cv.put(SET, set);
        mDB.insert(DB_TABLE_MAIN, null, cv);
    }

    public void addRecComment(String date, String comment, int totalWeight, String time) {
        ContentValues cv = new ContentValues();
        cv.put(DATE, date);
        cv.put(COMMENT_TO_TRAINING, comment);
        cv.put(TOTAL_WEIGHT_OF_TRAINING, totalWeight);
        cv.put(TOTAL_TIME_OF_TRAINING, time);
        mDB.insert(DB_TABLE_COMMENT, null, cv);
    }

    public Cursor getDataComment(String[] cols, String selection, String[] args, String groupby, String having, String orderBy) {
        return mDB.query(DB_TABLE_COMMENT, cols, selection, args, groupby, having, orderBy);
    }

    public void updateExercise(int Id, String column, String data) {
        ContentValues cv1 = new ContentValues();
        cv1.put(column, data);
        mDB.update(DB_EXE_TABLE, cv1, _ID + " = " + Id, null);
    }

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
        mDB.update(DB_TABLE_MAIN, cv, _ID + " = " + Id, null);
    }

    public void updateRec_Training(int Id, int colId, String data_str) {
        ContentValues cv = new ContentValues();
        if (colId == 1) {
            cv.put(TRAINING_NAME, data_str);
        } else if (colId == 2) {
            cv.put(EXERCISE_NAME, data_str);
        }
        mDB.update(DB_TRAININGS_TABLE, cv, _ID + " = " + Id, null);
    }

    public void deleteExercise(long id) {
        mDB.delete(DB_TABLE_TRAINING_EXERCISE, _ID + " = " + id, null);
        mDB.delete(DB_EXE_TABLE, _ID + " = " + id, null);
    }

    public void delRec_Main(long id) {
        mDB.delete(DB_TABLE_MAIN, _ID + " = " + id, null);
    }

    private class DBHelper extends SQLiteOpenHelper {

        public DBHelper(Context context, String name, CursorFactory factory,
                        int version) {
            super(context, name, factory, version);
        }

        @Override
        public void onCreate(SQLiteDatabase db) {
            db.execSQL(DB_EXE_CREATE);
            db.execSQL(DB_MAIN_CREATE);
            db.execSQL(DB_MEASURE_CREATE);
            db.execSQL(DB_TRAININGS_CREATE);
            db.execSQL(DB_COMMENT_CREATE);
            db.execSQL(DB_TRAINING_EXERCISE_CREATE);
        }

        @Override
        public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
            if (oldVersion == 1 && newVersion == 2) {
                db.execSQL(DB_MEASURE_CREATE);
            }
            if (oldVersion == 2 && newVersion == 3) {
                db.execSQL(DB_TRAININGS_CREATE);
            }
            if (oldVersion == 3 && newVersion == 4) {
                db.execSQL(DB_COMMENT_CREATE);
            }
            if (oldVersion == 4 && newVersion == 5) {
                db.execSQL(DB_TRAINING_EXERCISE_CREATE);
                db.execSQL("ALTER TABLE " + DB_EXE_TABLE + " ADD COLUMN " + PART_OF_BODY + " TEXT");
            }
        }
    }
}
