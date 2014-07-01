package com.nethergrim.combogymdiary;

import java.util.ArrayList;

import android.content.Context;
import android.database.Cursor;
import android.util.Log;

public class Statistics {

	private DB db;
	private Context context;
	private static final String LOG_TAG = "myLogs";

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
			Log.d(LOG_TAG, "  == " + result
					+ "  == " + max);
		}

		return result;
	}

	public void close() {
		db.close();
	}

}
