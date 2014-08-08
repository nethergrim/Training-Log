package com.nethergrim.combogymdiary.activities;

import android.os.Bundle;
import android.view.Gravity;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;

import com.nethergrim.combogymdiary.R;
import com.nethergrim.combogymdiary.view.FloatingActionButton;

public class NewCreatingTrainingDayActivity extends AnalyticsActivity {

    private ListView list;
    private TextView textNoExe;
    private EditText etName;
    private FloatingActionButton fabAdd, fabSave, fabSuperSet;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_new_creating_training_day);
        setTitle(R.string.creating_program);
        getActionBar().setDisplayHomeAsUpEnabled(true);
        getActionBar().setDisplayShowHomeEnabled(false);
        initButtons();
        initList();
        textNoExe = (TextView)findViewById(R.id.text_add_exersices);
        setTypeFaceLight(textNoExe);
        etName = (EditText)findViewById(R.id.etTrainingName);
        setTypeFaceLight(etName);
    }

    private void initList() {
        list = (ListView)findViewById(R.id.listView);
    }

    private void initButtons() {
        fabAdd = new FloatingActionButton.Builder(this)
                .withDrawable(getResources().getDrawable(R.drawable.ic_action_new))
                .withButtonColor(getResources().getColor(R.color.holo_blue_light))
                .withGravity(Gravity.BOTTOM | Gravity.LEFT)
                .withMargins(16, 0, 0, 16)
                .create();

        fabSave = new FloatingActionButton.Builder(this)
                .withDrawable(getResources().getDrawable(R.drawable.ic_action_save))
                .withButtonColor(getResources().getColor(R.color.holo_blue_light))
                .withGravity(Gravity.BOTTOM | Gravity.CENTER_HORIZONTAL)
                .withMargins(0, 0, 0, 16)
                .create();

        fabSuperSet = new FloatingActionButton.Builder(this)
                .withDrawable(getResources().getDrawable(R.drawable.ic_action_ss))
                .withButtonColor(getResources().getColor(R.color.holo_blue_light))
                .withGravity(Gravity.BOTTOM | Gravity.RIGHT)
                .withMargins(0, 0, 16, 16)
                .create();

        fabAdd.hide();
        fabSave.hide();
        fabSuperSet.hide();
    }

    @Override
    protected void onResume() {
        super.onResume();
        fabSuperSet.show();
        fabSave.show();
        fabAdd.show();
        if (list.getCount() > 0){
            textNoExe.setVisibility(View.GONE);
        } else {
            textNoExe.setVisibility(View.VISIBLE);
        }
    }

    @Override
    protected void onPause() {
        super.onPause();
        fabAdd.hide();
        fabSave.hide();
        fabSuperSet.hide();
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                super.onBackPressed();
                return true;
        }
        return false;
    }
}
