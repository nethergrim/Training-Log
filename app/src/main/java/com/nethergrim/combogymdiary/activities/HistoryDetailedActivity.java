package com.nethergrim.combogymdiary.activities;

import android.app.Activity;
import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.util.Log;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup.LayoutParams;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.ScrollView;

import com.nethergrim.combogymdiary.DB;
import com.nethergrim.combogymdiary.R;
import com.nethergrim.combogymdiary.tools.Prefs;
import com.nethergrim.combogymdiary.view.TextViewLight;

public class HistoryDetailedActivity extends Activity {

    public static final String BUNDLE_KEY_TRAINING_NAME = "com.nethergrim.combogymdiary.TRAININGNAME";
    public static final String BUNDLE_KEY_TRAINING_ID = "com.nethergrim.combogymdiary.TRAININGID";
    public static final String BUNDLE_KEY_DATE = "com.nethergrim.combogymdiary.DATE";

    private DB db;
    private Cursor cursor;
    private String trName = null;
    private String trDate = null;
    private FrameLayout content_frame;
    private String measureItem;
    private int total = 0;

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                super.onBackPressed();
                return true;
        }
        return false;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_history_detailed);
        TextViewLight tvComment = (TextViewLight) findViewById(R.id.tvComment);
        TextViewLight tvWeight = (TextViewLight) findViewById(R.id.textViewWeightTOtal);
        content_frame = (FrameLayout) findViewById(R.id.content_frame);
        db = new DB(this);
        db.open();
        Intent intent = getIntent();
        trName = intent.getStringExtra(BUNDLE_KEY_TRAINING_NAME);
        trDate = intent.getStringExtra(BUNDLE_KEY_DATE);
        setupActionBar();

        Cursor c = db.getCommentData(trDate);
        if (c.moveToFirst()) {
            total = c.getInt(4);
            if (c.getString(2) != null) {
                tvComment.setText(getResources().getString(R.string.comment) + " " + c.getString(2));
                tvComment.setVisibility(View.VISIBLE);
            } else {
                tvComment.setVisibility(View.GONE);
            }
        }

        c.close();
        setupCursor();
        setupLayout();

        tvWeight.setText(getResources().getString(
                R.string.total_weight_of_training) + " " + total + measureItem);

    }

    private void setupActionBar() {
        getActionBar().setTitle(trName + " (" + trDate + ")");
        getActionBar().setDisplayHomeAsUpEnabled(true);
        getActionBar().setDisplayShowHomeEnabled(false);
    }

    private void setupCursor() {
        String[] cols = {DB.DATE, DB.TRAINING_NAME, DB.EXERCISE_NAME, DB.WEIGHT,  DB.REPS, DB.SET, DB.SUPERSET_EXISTS, DB.SUPERSET_COLOR};
        String[] args = {trDate};
        cursor = db.getDataMain(cols, DB.DATE + "=?", args, null, null, DB.EXERCISE_NAME);
    }

    private void setupLayout() {
        ScrollView scrollView = new ScrollView(this);
        LinearLayout.LayoutParams lpView = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT);
        lpView.gravity = Gravity.CENTER;

        LinearLayout.LayoutParams lpData = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT);
        lpData.gravity = Gravity.CENTER;

        LinearLayout llMain = new LinearLayout(this);

        llMain.setOrientation(LinearLayout.VERTICAL);
        LayoutParams linLayoutParam = new LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT);

        content_frame.addView(scrollView, linLayoutParam);

        int px = (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 8, getResources().getDisplayMetrics());
        measureItem = Prefs.get().getWeightMeasureType(this);
        scrollView.addView(llMain, linLayoutParam);
        llMain.setGravity(Gravity.CENTER);

        int color = getResources().getColor(R.color.gray_dark);
        if (cursor.moveToFirst()) {
            do {
                LayoutInflater inflater = getLayoutInflater();
                View card = inflater.inflate(R.layout.item_detailed_history, null, false);
                TextViewLight tvName = (TextViewLight) card.findViewById(R.id.textViewExerciseName);
                if (cursor.getInt(6) == 1){
                    View view = card.findViewById(R.id.view_color_for_superset);
                    view.setBackgroundColor(cursor.getInt(7));
                }
                LinearLayout llData = (LinearLayout) card.findViewById(R.id.linearLayoutForConent);
                llData.setGravity(Gravity.CENTER);
                tvName.setText(cursor.getString(2));
                tvName.setTextColor(color);
                lpView.setMargins(0, px, 0, 0);
                llMain.addView(card, lpView);
                do {
                    TextViewLight tvNewSet = new TextViewLight(this);
                    tvNewSet.setGravity(Gravity.CENTER);
                    tvNewSet.setText("" + cursor.getInt(3) + measureItem + "/" + cursor.getInt(4));
                    tvNewSet.setTextColor(color);
                    total += cursor.getInt(3) * cursor.getInt(4);

                    lpData.gravity = Gravity.CENTER;
                    llData.addView(tvNewSet, lpData);
                } while (cursor.moveToNext() && cursor.getInt(5) != 1);
                cursor.moveToPrevious();

            } while (cursor.moveToNext());
        }
        cursor.close();
    }

    protected void onDestroy() {
        super.onDestroy();
        db.close();
    }
}
