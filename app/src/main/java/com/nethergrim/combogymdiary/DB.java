package com.nethergrim.combogymdiary;

import android.content.ContentValues;
import android.content.Context;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteDatabase.CursorFactory;
import android.database.sqlite.SQLiteOpenHelper;
import android.preference.PreferenceManager;
import android.util.Log;

import com.nethergrim.combogymdiary.activities.BasicMenuActivityNew;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;


public class DB {

    public static final String LOG_TAG = "myLogs";
    public static final String DB_NAME = "mydb";
    public static final String COLUMN_ID = "_id";
    public static final String EXE_NAME = "exercise_name";
    public static final String TRA_NAME = "training_name";
    public static final String TIMER_VALUE = "timer_value";
    public static final String COMMENT_TO_TRAINING = "comment_to_training";
    public static final String DB_MAIN_TABLE = "main_tab";
    public static final String DATE = "Date";
    public static final String WEIGHT = "Weight";
    public static final String REPS = "Reps";
    public static final String SET = "SetsN";
    private static final String DB_MAIN_CREATE = "create table "
            + DB_MAIN_TABLE + "(" + COLUMN_ID
            + " integer primary key autoincrement, " + TRA_NAME + " text, "
            + EXE_NAME + " text, " + DATE + " text, " + WEIGHT + " integer, "
            + REPS + " integer, " + SET + " integer" + ");";
    public static final String TOTAL_TIME_OF_TRAINING = "time_of_training";
    public static final String DB_COMMENT_TABLE = "comment_table";
    public static final String TOTAL_WEIGHT_OF_TRAINING = "total_weight";
    private static final String DB_COMMENT_CREATE = "create table "
            + DB_COMMENT_TABLE + "(" + COLUMN_ID
            + " integer primary key autoincrement, " + DATE + " text, "
            + COMMENT_TO_TRAINING + " text, " + TOTAL_TIME_OF_TRAINING
            + " text, " + TOTAL_WEIGHT_OF_TRAINING + " integer" + ");";
    public static final String DB_MEASURE_TABLE = "measurements_tab";
    public static final String PART_OF_BODY_FOR_MEASURING = "part_of_body";
    public static final String MEASURE_VALUE = "measure_value";
    private static final String DB_MEASURE_CREATE = "create table " + DB_MEASURE_TABLE + "(" +
            COLUMN_ID + " integer primary key autoincrement, " +
            DATE + " text, " +
            PART_OF_BODY_FOR_MEASURING + " text, " +
            MEASURE_VALUE + " text" + ");";
    public static final String DB_TRAININGS_TABLE = "trainings_tab";
    private static final String DB_TRAININGS_CREATE = "create table "
            + DB_TRAININGS_TABLE + "(" + COLUMN_ID
            + " integer primary key autoincrement, " + TRA_NAME + " text, "
            + EXE_NAME + " text" + ");";
    public static final String strSeparator = "__,__";
    public static final String SIMPLE_DATE_FORMAT = "dd.MM.yyyy";
    private static final int DB_VERSION = 4;
    private static final String DB_EXE_TABLE = "exe_tab";
    private static final String DB_EXE_CREATE = "create table " + DB_EXE_TABLE
            + "(" + COLUMN_ID + " integer primary key autoincrement, "
            + TRA_NAME + " text, " + EXE_NAME + " text, " + TIMER_VALUE
            + " text" + ");";
    private Context mCtx;
    private DBHelper mDBHelper;
    private SQLiteDatabase mDB;

    public DB(Context ctx) {
        mCtx = ctx;
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

    public String getWeightMeasureType(Context context){
        SharedPreferences sp = PreferenceManager
                .getDefaultSharedPreferences(context);
        String item = sp.getString(BasicMenuActivityNew.MEASURE_ITEM, "1");
        if (item.equals("1")) {
            return context.getResources().getStringArray(R.array.measure_items)[0];
        } else if (item.equals("2")) {
            return context.getResources().getStringArray(R.array.measure_items)[1];
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

    public String getExerciseByID(int id) {
        String[] args = {"" + id};
        Cursor c = mDB.query(DB.DB_EXE_TABLE, null, DB.COLUMN_ID + "=?", args,
                null, null, null);
        if (c.moveToFirst()) {
            return c.getString(2);
        } else
            return null;
    }

    public void close() {
        if (mDBHelper != null)
            mDBHelper.close();
    }

    public Cursor getAllData_Exe() {

        return mDB.query(DB_EXE_TABLE, null, null, null, null, null, null);
    }

    public Cursor getDataExercises(String groupBy) {

        return mDB.query(DB_EXE_TABLE, null, null, null, groupBy, null, null);
    }

    public int getExeIdByName(String name) {

        String[] args = {name};
        String[] cols = {DB.COLUMN_ID};
        Cursor c = mDB.query(DB_EXE_TABLE, cols, DB.EXE_NAME + "=?", args,
                (String) null, (String) null, (String) null);
        if (c.moveToFirst()) {
            return c.getInt(0);
        } else {
            return 0;
        }
    }

    public Cursor getDataMain(String groupBy) {
        return mDB.query(DB_MAIN_TABLE, null, null, null, groupBy, null, null);
    }

    public boolean delRecordMeasurement(String date) {
        String[] args = {date};
        int tmp = mDB.delete(DB_MEASURE_TABLE, DATE + "=?", args);
        if (tmp > 0) {
            return true;
        } else
            return false;
    }

    public String getTrainingList(int _id) {
        Cursor c = mDB.query(DB_TRAININGS_TABLE, null, null, null, null, null,
                null);
        if (c.moveToFirst()) {
            do {
                if (c.getInt(0) == _id) {
                    return c.getString(2);
                }
            } while ((c.moveToNext()));
        }
        return null;
    }

    public void deleteExersice(String name) {
        Cursor c = mDB.query(DB_TRAININGS_TABLE, null, null, null, null, null,
                null);
        if (c.moveToFirst()) {
            do {
                int delta = 0;
                String tmp = c.getString(2);
                String[] tmpArray = convertStringToArray(tmp);
                int[] intArray = new int[tmpArray.length];
                for (int i = 0; i < intArray.length; i++)
                    intArray[i] = 0;

                for (int i = 0; i < tmpArray.length; i++) {
                    if (tmpArray[i].equals(name)) {
                        intArray[i] = 1;
                        delta++;
                    }
                }

                String[] newArray = new String[tmpArray.length - delta];
                for (int i = 0, j = 0; i < tmpArray.length; i++) {
                    if (intArray[i] == 0) {
                        newArray[j] = tmpArray[i];
                        j++;
                    } else if (intArray[i] == 1) {
                        continue;
                    }
                }

                String newString = convertArrayToString(newArray);

                updateRec_Training(c.getInt(0), 2, newString);
            } while (c.moveToNext());
        }
    }

    public void deleteExersice(String name, String trainingName) {
        String[] args = {trainingName};
        Cursor c = mDB.query(DB_TRAININGS_TABLE, null, TRA_NAME + "=?", args,
                (String) null, (String) null, (String) null);
        if (c.moveToFirst()) {
            int delta = 0;
            String tmp = c.getString(2);
            String[] tmpArray = convertStringToArray(tmp);
            int[] intArray = new int[tmpArray.length];
            for (int i = 0; i < intArray.length; i++)
                intArray[i] = 0;
            for (int i = 0; i < tmpArray.length; i++) {
                if (tmpArray[i].equals(name)) {
                    intArray[i] = 1;
                    delta++;
                }
            }
            String[] newArray = new String[tmpArray.length - delta];
            for (int i = 0, j = 0; i < tmpArray.length; i++) {
                if (intArray[i] == 0) {
                    newArray[j] = tmpArray[i];
                    j++;
                } else if (intArray[i] == 1) {
                    continue;
                }
            }

            String newString = convertArrayToString(newArray);

            updateRec_Training(c.getInt(0), 2, newString);
        }
    }

    public void deleteComment(String date) {
        mDB.delete(DB_COMMENT_TABLE, DATE + " = " + date, null);
    }

    public int getLastWeightOrReps(String _exeName, int _set, boolean ifWeight) {
        String[] cols = {DB.WEIGHT, DB.SET};
        if (ifWeight) {
            cols[0] = DB.WEIGHT;
        } else {
            cols[0] = DB.REPS;
        }

        String[] tags = {_exeName};
        Cursor c = mDB.query(DB_MAIN_TABLE, cols, DB.EXE_NAME + "=?", tags,
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
        Cursor c1 = mDB.query(DB_EXE_TABLE, cols, DB.EXE_NAME + "=?", tags,
                null, null, null, null);
        if (c1.moveToFirst()) {
            result = c1.getString(0);
        } else
            Log.d(LOG_TAG, "ERROR There is no such exe:" + exeName);
        return result;
    }

    public int getIdValueByExerciseName(String exeName) {
        int result;
        String[] cols = {DB.COLUMN_ID};
        String[] tags = {exeName};
        Cursor c = mDB.query(DB_EXE_TABLE, cols, DB.EXE_NAME + "=?", tags,
                null, null, null, null);
        c.moveToFirst();
        result = c.getInt(0);
        return result;
    }

    public int getThisWeight(int currentSet, String exeName) {
        int result = 0;
        String[] args = {exeName};
        Cursor c = mDB.query(DB_MAIN_TABLE, null, EXE_NAME + "=?", args, null,
                null, null);
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
        Cursor c = mDB.query(DB_MAIN_TABLE, null, EXE_NAME + "=?", args, null,
                null, null);
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
        Cursor c = mDB.query(DB_MAIN_TABLE, null, EXE_NAME + "=?", args, null,
                null, null);
        if (c.moveToLast()) {
            do {
                if (c.getInt(6) == currentSet) {
                    result = c.getInt(0);
                    break;
                }
            } while (c.moveToPrevious());
        }
        Log.d(LOG_TAG, "returned ID == " + result);
        return result;
    }

    public Cursor getAllData_Main() {
        return mDB.query(DB_MAIN_TABLE, null, null, null, null, null, null);
    }

    public Cursor getDataMain(String[] column, // The columns to return
                              String selection, // The columns for the WHERE clause
                              String[] selectionArgs, // The values for the WHERE clause
                              String groupBy, // group the rows
                              String having, // filter by row groups
                              String orderedBy // The sort order
    ) {

        return mDB.query(DB_MAIN_TABLE, column, selection, selectionArgs,
                groupBy, having, orderedBy);
    }

    public Cursor getDataExe(String[] column, // The columns to return
                             String selection, // The columns for the WHERE clause
                             String[] selectionArgs, // The values for the WHERE clause
                             String groupBy, // group the rows
                             String having, // filter by row groups
                             String orderedBy // The sort order
    ) {
        return mDB.query(DB_EXE_TABLE, column, selection, selectionArgs,
                groupBy, having, orderedBy);
    }

    public Cursor getDataTrainings(String[] column, String selection,
                                   String[] selectionArgs, String groupBy, String having,
                                   String orderedBy) {
        return mDB.query(DB_TRAININGS_TABLE, column, selection, selectionArgs,
                groupBy, having, orderedBy);
    }

    public Cursor getCommentData(String date) {
        String[] args = {date};
        Cursor c = mDB.query(DB_COMMENT_TABLE, null, DATE + "=?", args, null,
                null, null);
        return c;
    }

    public Cursor getDataMeasures(String[] column, // The columns to return
                                  String selection, // The columns for the WHERE clause
                                  String[] selectionArgs, // The values for the WHERE clause
                                  String groupBy, // group the rows
                                  String having, // filter by row groups
                                  String orderedBy // The sort order
    ) {
        return mDB.query(DB_MEASURE_TABLE, column, selection, selectionArgs,
                groupBy, having, orderedBy);
    }

    public void addRecExe(String exeName, String timer) {
        ContentValues cv = new ContentValues();
        cv.put(EXE_NAME, exeName);
        cv.put(TIMER_VALUE, timer);
        mDB.insert(DB_EXE_TABLE, null, cv);
    }

    public String getTrainingName(int _id) {
        String[] args = {_id + ""};
        Cursor c = mDB.query(DB_TRAININGS_TABLE, null, COLUMN_ID + "=?", args,
                null, null, null);
        if (c.moveToFirst()) {
            return c.getString(1);
        } else
            return "";
    }

    public void addRecTrainings(String traName, String exeName) {
        ContentValues cv = new ContentValues();
        cv.put(EXE_NAME, exeName);
        cv.put(TRA_NAME, traName);
        long id = mDB.insert(DB_TRAININGS_TABLE, null, cv);
        Log.d(LOG_TAG, "added record to trainigns: id == " + id);
    }

    public void addRecMeasure(String date, String part_of_body, String value) {
        ContentValues cv = new ContentValues();
        cv.put(DATE, date);
        cv.put(PART_OF_BODY_FOR_MEASURING, part_of_body);
        cv.put(MEASURE_VALUE, value);
        mDB.insert(DB_MEASURE_TABLE, null, cv);
        Log.d(LOG_TAG, "Added row: date = " + date + " part_of_body = "
                + part_of_body + " value = " + value);
    }

    public void addRecMainTable(String traName, String exeName, String date,
                                int weight, int reps, int set) {
        ContentValues cv = new ContentValues();
        cv.put(EXE_NAME, exeName);
        cv.put(TRA_NAME, traName);
        cv.put(DATE, date);
        cv.put(WEIGHT, weight);
        cv.put(REPS, reps);
        cv.put(SET, set);
        mDB.insert(DB_MAIN_TABLE, null, cv);
    }

    public void addRecComment(String date, String comment, int totalWeight,
                              String time) {
        ContentValues cv = new ContentValues();
        cv.put(DATE, date);
        cv.put(COMMENT_TO_TRAINING, comment);
        cv.put(TOTAL_WEIGHT_OF_TRAINING, totalWeight);
        cv.put(TOTAL_TIME_OF_TRAINING, time);

        mDB.insert(DB_COMMENT_TABLE, null, cv);
    }

    public Cursor getDataComment(String[] cols, String selection,
                                 String[] args, String groupby, String having, String orderBy) {
        return mDB.query(DB_COMMENT_TABLE, cols, selection, args, groupby,
                having, orderBy);
    }

    public void updateRec_Exe(int Id, String column, String data) {
        ContentValues cv1 = new ContentValues();
        cv1.put(column, data);
        Log.d(LOG_TAG, "===DB_exe  Editing column " + column + " at ID = " + Id
                + " with data: " + data);
        mDB.update(DB_EXE_TABLE, cv1, "_id = " + Id, null);
    }

    public void delDB() {
        mCtx.deleteDatabase(DB.DB_NAME);
    }

    public void updateRec_Main(int Id, int colId, String data_str, int data_int) {
        ContentValues cv = new ContentValues();
        if (colId == 1) {
            cv.put(TRA_NAME, data_str);
        } else if (colId == 2) {
            cv.put(EXE_NAME, data_str);
        } else if (colId == 3) {
            cv.put(DATE, data_str);
        } else if (colId == 4) {
            cv.put(WEIGHT, data_int);
        } else if (colId == 5) {
            cv.put(REPS, data_int);
        } else if (colId == 6) {
            cv.put(SET, data_int);
        }
        Log.d(LOG_TAG, "Updating main DB:\n" + "id == " + Id + " colID == "
                + colId + " data == " + data_int);
        mDB.update(DB_MAIN_TABLE, cv, "_id = " + Id, null);
    }

    public void updateRec_Training(int Id, int colId, String data_str) {
        ContentValues cv = new ContentValues();
        if (colId == 1) {
            cv.put(TRA_NAME, data_str);
        } else if (colId == 2) {
            cv.put(EXE_NAME, data_str);
        }
        mDB.update(DB_TRAININGS_TABLE, cv, "_id = " + Id, null);
    }

    public void delRec_Exe(long id) {
        mDB.delete(DB_EXE_TABLE, COLUMN_ID + " = " + id, null);
    }

    public void delRec_Trainings(long id) {
        mDB.delete(DB_TRAININGS_TABLE, COLUMN_ID + " = " + id, null);
        Log.d(LOG_TAG, "deleting id == " + id);
    }

    public void delRec_Main(long id) {
        mDB.delete(DB_MAIN_TABLE, COLUMN_ID + " = " + id, null);
    }

    private class DBHelper extends SQLiteOpenHelper {

        public DBHelper(Context context, String name, CursorFactory factory,
                        int version) {
            super(context, name, factory, version);
        }

        @Override
        public void onCreate(SQLiteDatabase db) {
            Log.d(LOG_TAG, "DB created");
            db.execSQL(DB_EXE_CREATE);
            db.execSQL(DB_MAIN_CREATE);
            db.execSQL(DB_MEASURE_CREATE);
            db.execSQL(DB_TRAININGS_CREATE);
            db.execSQL(DB_COMMENT_CREATE);

        }

        @Override
        public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
            if (oldVersion == 1 && newVersion == 2) {
                Log.d(LOG_TAG, "DB updated from v1 to v2");
                db.execSQL(DB_MEASURE_CREATE);
            }
            if (oldVersion == 2 && newVersion == 3) {
                Log.d(LOG_TAG, "DB updating from v2 to v3");
                db.execSQL(DB_TRAININGS_CREATE);
            }
            if (oldVersion == 3 && newVersion == 4) {
                db.execSQL(DB_COMMENT_CREATE);
            }
        }
    }
}
