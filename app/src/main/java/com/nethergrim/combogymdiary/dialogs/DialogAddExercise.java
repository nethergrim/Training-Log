package com.nethergrim.combogymdiary.dialogs;

import android.app.DialogFragment;
import android.content.SharedPreferences;
import android.graphics.Typeface;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v4.app.FragmentActivity;
import android.util.Log;
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
import com.nethergrim.combogymdiary.DB;
import com.nethergrim.combogymdiary.R;

import java.util.ArrayList;

public class DialogAddExercise extends DialogFragment implements OnClickListener {

    private EditText etName, etTimer;
    private String exeName = "", timerV = "";
    private long exeID = 0;
    private Boolean editing = false;
    private DB db;
    private SharedPreferences sp;
    private String partOfBody = "";
    private ArrayList<String> partsOfBody = new ArrayList<String>();
    private ArrayAdapter<String> adapter;
    private boolean partOfBodySelected = false;
    private String[] muscleGroups;
    public static final String KEY_PART_OF_BODY  = "part_of_body";

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Bundle args = getArguments();
        initList();
        if (args != null) {
            editing = true;
            exeName = args.getString("exeName");
            timerV = args.getString("timerValue");
            exeID = args.getLong("exeID");
            partOfBody = args.getString(KEY_PART_OF_BODY);
        }
        Log.e("log", "partofbody: " + partOfBody);
    }

    private void initList() {
        partsOfBody.add(getString(R.string.choose_muscle_part));
        muscleGroups =  getResources().getStringArray(R.array.MuscleGroupsArray);
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
        getDialog().setTitle(R.string.add_an_exercise);
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
                            partOfBody = Constants.PART_OF_BODY_CHEST;
                            break;
                        case 2:
                            partOfBody = Constants.PART_OF_BODY_SHOULDERS;
                            break;
                        case 3:
                            partOfBody = Constants.PART_OF_BODY_BICEPS;
                            break;
                        case 4:
                            partOfBody = Constants.PART_OF_BODY_TRICEPS;
                            break;
                        case 5:
                            partOfBody = Constants.PART_OF_BODY_BACK;
                            break;
                        case 6:
                            partOfBody = Constants.PART_OF_BODY_ABS;
                            break;
                        case 7:
                            partOfBody = Constants.PART_OF_BODY_LEGS;
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
            etName.setText(exeName);
            etTimer.setText(timerV);
        }
        sp = PreferenceManager.getDefaultSharedPreferences(getActivity());
        if (editing && partOfBody == null || partOfBody.equals("")){
            spinner.setSelection(0);
        }

        if (partOfBody != null && !partOfBody.equals("")){
            if (partOfBody.equals(Constants.PART_OF_BODY_CHEST)) spinner.setSelection(1);
            if (partOfBody.equals(Constants.PART_OF_BODY_SHOULDERS)) spinner.setSelection(2);
            if (partOfBody.equals(Constants.PART_OF_BODY_BICEPS)) spinner.setSelection(3);
            if (partOfBody.equals(Constants.PART_OF_BODY_TRICEPS)) spinner.setSelection(4);
            if (partOfBody.equals(Constants.PART_OF_BODY_BACK)) spinner.setSelection(5);
            if (partOfBody.equals(Constants.PART_OF_BODY_ABS)) spinner.setSelection(6);
            if (partOfBody.equals(Constants.PART_OF_BODY_LEGS)) spinner.setSelection(7);

        }
        return v;
    }

    public void onClick(View v) {
        if (!partOfBodySelected){
            Toast.makeText(getActivity(),R.string.choose_muscle_part, Toast.LENGTH_SHORT).show();
            return;
        }
        if (v.getId() == R.id.btnSave && !editing) {  // creating
            if (!etName.getText().toString().isEmpty() && !etTimer.getText().toString().isEmpty()) {
                dismiss();
                db.addExercise(etName.getText().toString(), etTimer.getText().toString(), partOfBody);
                Toast.makeText(getActivity(), R.string.saved, Toast.LENGTH_SHORT).show();
            }
        } else if (v.getId() == R.id.btnSave && editing) {  // editing
            if (!etName.getText().toString().isEmpty() && !etTimer.getText().toString().isEmpty()) {
                dismiss();
                db.updateExercise((int) exeID, DB.EXE_NAME, etName.getText().toString());
                db.updateExercise((int) exeID, DB.TIMER_VALUE, etTimer.getText().toString());
                db.updateExercise((int) exeID, DB.PART_OF_BODY, partOfBody);
                Toast.makeText(getActivity(), R.string.saved, Toast.LENGTH_SHORT).show();
            }
        }
        ((FragmentActivity) getActivity()).getSupportLoaderManager().getLoader(1).forceLoad();
    }

    public void onResume() {
        super.onResume();
        String defaultTimer = sp.getString("etDefault", "60");
        etTimer.setText(defaultTimer);
    }

}
