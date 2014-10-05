package com.nethergrim.combogymdiary.dialogs;

import android.app.DialogFragment;
import android.content.SharedPreferences;
import android.graphics.Typeface;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.nethergrim.combogymdiary.Constants;
import com.nethergrim.combogymdiary.storage.DB;
import com.nethergrim.combogymdiary.R;
import com.nethergrim.combogymdiary.activities.BaseActivity;
import com.nethergrim.combogymdiary.model.Exercise;
import com.yandex.metrica.Counter;

import java.util.ArrayList;

public class DialogEditExercise extends DialogFragment implements OnClickListener {

    private EditText etName, etTimer;
    private Boolean editing = false;
    private DB db;
    private SharedPreferences sp;
    private ArrayList<String> partsOfBody = new ArrayList<String>();
    private ArrayAdapter<String> adapter;
    private boolean partOfBodySelected = false;
    private Exercise exercise;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Bundle args = getArguments();
        initList();
        if (args != null) {
            editing = true;
            exercise = (Exercise) args.getSerializable(Constants.BUNDLE_EXERCISE);
        } else {
            exercise = new Exercise();
            exercise.setPartOfBody("");
            exercise.setName("");
        }
    }

    private void initList() {
        partsOfBody.add(getString(R.string.choose_muscle_part));
        String[] muscleGroups = getResources().getStringArray(R.array.MuscleGroupsArray);
        partsOfBody.add(muscleGroups[0]);
        partsOfBody.add(muscleGroups[3]);
        partsOfBody.add(muscleGroups[4]);
        partsOfBody.add(muscleGroups[5]);
        partsOfBody.add(muscleGroups[2]);
        partsOfBody.add(muscleGroups[6]);
        partsOfBody.add(muscleGroups[1]);
        adapter = new ArrayAdapter<String>(getActivity(), android.R.layout.simple_spinner_item, partsOfBody);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
    }

    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        if (editing) {
            getDialog().setTitle(R.string.create_new_exercise);
        } else {
            getDialog().setTitle(R.string.add_an_exercise);
        }
        View v = inflater.inflate(R.layout.adding_exersise, null);
        Button btnCreate = (Button) v.findViewById(R.id.btnSave);
        btnCreate.setTypeface(Typeface.createFromAsset(getActivity().getAssets(), Constants.TYPEFACE_LIGHT));
        btnCreate.setOnClickListener(this);
        Spinner spinner = (Spinner) v.findViewById(R.id.spinnerPartOfBody);
        spinner.setAdapter(adapter);
        spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                partOfBodySelected = position > 0;
                if (partOfBodySelected) {
                    switch (position) {
                        case 1:
                            exercise.setPartOfBody(Constants.PART_OF_BODY_CHEST);
                            break;
                        case 2:
                            exercise.setPartOfBody(Constants.PART_OF_BODY_SHOULDERS);
                            break;
                        case 3:
                            exercise.setPartOfBody(Constants.PART_OF_BODY_BICEPS);
                            break;
                        case 4:
                            exercise.setPartOfBody(Constants.PART_OF_BODY_TRICEPS);
                            break;
                        case 5:
                            exercise.setPartOfBody(Constants.PART_OF_BODY_BACK);
                            break;
                        case 6:
                            exercise.setPartOfBody(Constants.PART_OF_BODY_ABS);
                            break;
                        case 7:
                            exercise.setPartOfBody(Constants.PART_OF_BODY_LEGS);
                            break;
                    }
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
            }
        });
        TextView textView = (TextView) v.findViewById(R.id.tvWidget2);
        textView.setTypeface(Typeface.createFromAsset(getActivity().getAssets(), Constants.TYPEFACE_LIGHT));
        etName = (EditText) v.findViewById(R.id.etTimerValue);
        etName.setTypeface(Typeface.createFromAsset(getActivity().getAssets(), Constants.TYPEFACE_LIGHT));
        etTimer = (EditText) v.findViewById(R.id.editText2);
        etTimer.setTypeface(Typeface.createFromAsset(getActivity().getAssets(), Constants.TYPEFACE_LIGHT));
        db = new DB(getActivity());
        db.open();
        if (editing) {
            etName.setText(exercise.getName());
            etTimer.setText(exercise.getTimer());
        }
        sp = PreferenceManager.getDefaultSharedPreferences(getActivity());
        if (exercise.getPartOfBody() == null || exercise.getPartOfBody().equals("")) {
            spinner.setSelection(0);
        }

        if (exercise.getPartOfBody() != null && !exercise.getPartOfBody().equals("")) {
            if (exercise.getPartOfBody().equals(Constants.PART_OF_BODY_CHEST))
                spinner.setSelection(1);
            if (exercise.getPartOfBody().equals(Constants.PART_OF_BODY_SHOULDERS))
                spinner.setSelection(2);
            if (exercise.getPartOfBody().equals(Constants.PART_OF_BODY_BICEPS))
                spinner.setSelection(3);
            if (exercise.getPartOfBody().equals(Constants.PART_OF_BODY_TRICEPS))
                spinner.setSelection(4);
            if (exercise.getPartOfBody().equals(Constants.PART_OF_BODY_BACK))
                spinner.setSelection(5);
            if (exercise.getPartOfBody().equals(Constants.PART_OF_BODY_ABS))
                spinner.setSelection(6);
            if (exercise.getPartOfBody().equals(Constants.PART_OF_BODY_LEGS))
                spinner.setSelection(7);
            if (exercise.getPartOfBody().equals(Constants.PART_OF_BODY_NONE))
                spinner.setSelection(0);
        }
        return v;
    }

    public void onClick(View v) {
        if (!partOfBodySelected) {
            Toast.makeText(getActivity(), R.string.choose_muscle_part, Toast.LENGTH_SHORT).show();
            return;
        }
        if (v.getId() == R.id.btnSave && !editing) {  // creating
            if (!etName.getText().toString().isEmpty() && !etTimer.getText().toString().isEmpty()) {
                dismiss();
                db.persistExercise(etName.getText().toString(), etTimer.getText().toString(), exercise.getPartOfBody());
                Toast.makeText(getActivity(), R.string.saved, Toast.LENGTH_SHORT).show();
            }
        } else if (v.getId() == R.id.btnSave && editing) {  // editing
            if (!etName.getText().toString().isEmpty() && !etTimer.getText().toString().isEmpty()) {
                dismiss();
                db.updateExercise((int) exercise.getId(), DB.EXERCISE_NAME, etName.getText().toString());
                db.updateExercise((int) exercise.getId(), DB.TIMER_VALUE, etTimer.getText().toString());
                db.updateExercise((int) exercise.getId(), DB.PART_OF_BODY, exercise.getPartOfBody());
                Toast.makeText(getActivity(), R.string.saved, Toast.LENGTH_SHORT).show();
            }
        }
        try {
            if (getActivity() instanceof BaseActivity) {
                BaseActivity baseActivity = (BaseActivity) getActivity();
                baseActivity.getExerciseListFragment().updateList(getActivity());
            }
        } catch (Exception e) {
            Counter.sharedInstance().reportError("", e);
            e.printStackTrace();
        }
    }

    public void onResume() {
        super.onResume();
        String defaultTimer = sp.getString("etDefault", "60");
        if (editing) {
            etTimer.setText(exercise.getTimer());
        } else {
            etTimer.setText(defaultTimer);
        }
    }

}
