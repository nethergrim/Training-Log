package com.nethergrim.combogymdiary;

import android.content.Context;
import android.database.Cursor;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;

public class Statistics {

    private static final String LOG_TAG = "myLogs";
    private static final long millisecInDay = 24 * 60 * 60 * 1000;
    private DB db;
    private Context context;

    public Statistics(Context _context) {
        this.context = _context;
        this.db = new DB(context);
        db.open();
    }

    public String getMainExercise() {
        String result = context.getResources().getString(R.string.none);
        Cursor exercises = db.getDataMain(DB.EXE_NAME);
        ArrayList<String> alExercises = new ArrayList<String>();
        if (exercises.moveToFirst()) {
            do {
                alExercises.add(exercises.getString(2));
            } while (exercises.moveToNext());
        }
        ArrayList<Integer> alCount = new ArrayList<Integer>();
        for (int i = 0; i < alExercises.size(); i++) {
            alCount.add(0);
        }
        Cursor allCursor = db.getDataMain(null, null, null, null, null,
                DB.EXE_NAME);
        if (allCursor.moveToFirst()) {
            do {
                for (int i = 0; i < alExercises.size(); i++) {
                    if (allCursor.getString(2).equals(alExercises.get(i))) {
                        alCount.set(i, (alCount.get(i) + 1));
                    }
                }
            } while (allCursor.moveToNext());
        }

        int max = 0;
        int maxIndex = 0;
        for (int i = 0; i < alCount.size(); i++) {
            if (alCount.get(i) > max) {
                max = alCount.get(i);
                maxIndex = i;
            }
        }
        if (max > 0) {
            result = alExercises.get(maxIndex);
        }

        return result;
    }

    public String getBodyWeightDelta(int days) {
        String result = context.getResources().getString(R.string.none);
        String[] args = {context.getResources().getString(R.string.weight)};
        Cursor c = db.getDataMeasures(null, DB.PART_OF_BODY_FOR_MEASURING + "=?", args, null, null, DB.COLUMN_ID);
        if (c.moveToLast()) {
            Date lastDate = db.convertStringToDate(c.getString(1));
            Calendar lastDay = Calendar.getInstance();
            lastDay.setTime(lastDate);
            Double lastWeight = Double.parseDouble(c.getString(3));
            do {
                if (c.moveToPrevious()) {
                    Date date = db.convertStringToDate(c.getString(1));
                    Calendar tmp = Calendar.getInstance();
                    tmp.setTime(date);
                    long diff = lastDay.getTimeInMillis() - tmp.getTimeInMillis();
                    if (diff > (millisecInDay * days)) {
                        Double firstWeight = Double.parseDouble(c.getString(3));
                        result = lastWeight > firstWeight ? "+" + (lastWeight - firstWeight) : "-" + (firstWeight - lastWeight);
                        return result + " " + db.getWeightMeasureType(context);
                    }
                }
            } while (true);
        }
        return result;
    }

    public void close() {
        if (db != null)
            db.close();
    }

}
