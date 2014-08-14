package com.nethergrim.combogymdiary.activities;

import android.os.Bundle;
import android.util.Log;
import android.view.Gravity;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;

import com.nethergrim.combogymdiary.DB;
import com.nethergrim.combogymdiary.R;
import com.nethergrim.combogymdiary.dialogs.DialogAddExercises;
import com.nethergrim.combogymdiary.dialogs.DialogInfo;
import com.nethergrim.combogymdiary.model.ExerciseGroup;
import com.nethergrim.combogymdiary.tools.Prefs;
import com.nethergrim.combogymdiary.view.FloatingActionButton;

import java.util.ArrayList;
import java.util.List;

public class NewCreatingTrainingDayActivity extends AnalyticsActivity implements DialogAddExercises.OnExerciseAddCallback {

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
        textNoExe = (TextView) findViewById(R.id.text_add_exersices);
        setTypeFaceLight(textNoExe);
        etName = (EditText) findViewById(R.id.etTrainingName);
        setTypeFaceLight(etName);
    }


    private void initList() {
        list = (ListView) findViewById(R.id.listView);
    }

    private void initButtons() {
        fabAdd = new FloatingActionButton.Builder(this)
                .withDrawable(getResources().getDrawable(R.drawable.ic_plus_small))
                .withButtonColor(getResources().getColor(R.color.material_cyan_a400))
                .withGravity(Gravity.BOTTOM | Gravity.LEFT)
                .withMargins(16, 0, 0, 16)
                .create();

        fabSave = new FloatingActionButton.Builder(this)
                .withDrawable(getResources().getDrawable(R.drawable.ic_action_save))
                .withButtonColor(getResources().getColor(R.color.material_cyan_a400))
                .withGravity(Gravity.BOTTOM | Gravity.CENTER_HORIZONTAL)
                .withMargins(0, 0, 0, 16)
                .create();

        fabSuperSet = new FloatingActionButton.Builder(this)
                .withDrawable(getResources().getDrawable(R.drawable.ic_action_ss))
                .withButtonColor(getResources().getColor(R.color.material_cyan_a400))
                .withGravity(Gravity.BOTTOM | Gravity.RIGHT)
                .withMargins(0, 0, 16, 16)
                .create();

        fabAdd.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                DialogAddExercises dialogAddExercises = new DialogAddExercises();
                dialogAddExercises.show(getFragmentManager(), DialogAddExercises.class.getName());
            }
        });

        fabSave.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // TODO save
            }
        });

        fabSuperSet.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // TODO create superset
            }
        });
    }

    @Override
    public void onStart() {
        super.onStart();
        showSuperSetDialog();
    }

    @Override
    protected void onResume() {
        super.onResume();
        fabSuperSet.show();
        fabSave.show();
        fabAdd.show();
        if (list.getCount() > 0) {
            textNoExe.setVisibility(View.GONE);
        } else {
            textNoExe.setVisibility(View.VISIBLE);
        }
    }

    private void showSuperSetDialog() {
        if (Prefs.getPreferences().getSuperSetInfoShowed() <= 3) {
            DialogInfo dialogInfo = new DialogInfo();
            Bundle args = new Bundle();
            args.putBoolean(DialogInfo.KEY_INFO_ABOUT_SUPERSET, true);
            dialogInfo.setArguments(args);
            dialogInfo.show(getFragmentManager(), DialogInfo.class.getName());
            Prefs.getPreferences().setSuperSetInfoShowed(Prefs.getPreferences().getSuperSetInfoShowed() + 1);
        }
    }

    @Override
    public void onStop() {
        super.onStop();
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

    @Override
    public void onExerciseAddedCallback(List<Integer> idList) {
        Log.e("log", "added size: " + idList.size());
        for (Integer anIdList : idList) {
            Log.e("log", "added id: " + anIdList);
        }
        // TODO adding exercises to list by there id`s (only unique)
    }
}
