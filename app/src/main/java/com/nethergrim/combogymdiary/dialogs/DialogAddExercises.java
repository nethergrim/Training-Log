package com.nethergrim.combogymdiary.dialogs;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.DialogFragment;
import android.content.Context;
import android.content.DialogInterface;
import android.graphics.Typeface;
import android.os.Bundle;
import android.util.Log;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseExpandableListAdapter;
import android.widget.CheckedTextView;
import android.widget.ExpandableListView;
import android.widget.TextView;

import com.nethergrim.combogymdiary.Constants;
import com.nethergrim.combogymdiary.R;
import com.nethergrim.combogymdiary.model.Exercise;
import com.nethergrim.combogymdiary.model.ExerciseGroup;

import java.util.ArrayList;
import java.util.List;

public class DialogAddExercises extends DialogFragment implements DialogInterface.OnClickListener {

    public static final String BUNDLE_KEY_DATA = "data";

    private OnExerciseAddCallback listener;
    private Context context;
    private List<Integer> checkedIds = new ArrayList<Integer>();
    private ExpandableListView elv;
    private ExercisesAdapter adapter;
    private ArrayList<ExerciseGroup> data;

    @Override
    public void onClick(DialogInterface dialog, int which) {
        switch (which) {
            case Dialog.BUTTON_POSITIVE:
                listener.onExerciseAddedCallback(checkedIds);
                dismiss();
                break;
            case Dialog.BUTTON_NEGATIVE:
                dismiss();
                break;
        }
    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        listener = (OnExerciseAddCallback) activity;
        this.context = activity;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            this.data = (ArrayList<ExerciseGroup>) getArguments().getSerializable(BUNDLE_KEY_DATA);
        }
    }

    public Dialog onCreateDialog(Bundle savedInstanceState) {
        LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View v = inflater.inflate(R.layout.dialog_add_exercise, null, false);
        elv = (ExpandableListView) v.findViewById(R.id.elvExercises);
        elv.setChoiceMode(ExpandableListView.CHOICE_MODE_MULTIPLE);
        adapter = new ExercisesAdapter(context, data);
        AlertDialog.Builder adb = new AlertDialog.Builder(getActivity())
                .setTitle(R.string.add_an_exercise)
                .setPositiveButton(R.string.yes, this)
                .setNegativeButton(R.string.no, this)
                .setView(v);
        return adb.create();
    }

    @Override
    public void onResume() {
        super.onResume();
        elv.setAdapter(adapter);
    }

    public interface OnExerciseAddCallback {
        public void onExerciseAddedCallback(List<Integer> idList);
    }

    private class ExercisesAdapter extends BaseExpandableListAdapter {

        private Context context;
        private List<ExerciseGroup> data;
        private String realNames[];

        public ExercisesAdapter(Context context, List<ExerciseGroup> data) {
            this.context = context;
            this.data = data;
            realNames = Constants.getPartsOfBodyRealNames(context);
        }

        @Override
        public int getGroupCount() {
            return data.size();
        }

        @Override
        public int getChildrenCount(int groupPosition) {
            return data.get(groupPosition).getExercisesList().size();
        }

        @Override
        public ExerciseGroup getGroup(int groupPosition) {
            return data.get(groupPosition);
        }

        @Override
        public Exercise getChild(int groupPosition, int childPosition) {
            return data.get(groupPosition).getExercisesList().get(childPosition);
        }

        @Override
        public long getGroupId(int groupPosition) {
            return groupPosition;
        }

        @Override
        public long getChildId(int groupPosition, int childPosition) {
            return data.get(groupPosition).getExercisesList().get(childPosition).getId();
        }

        @Override
        public boolean hasStableIds() {
            return false;
        }

        @Override
        public View getGroupView(int groupPosition, boolean isExpanded, View convertView, ViewGroup parent) {
            View v = convertView;
            if (v == null) {
                LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
                v = inflater.inflate(android.R.layout.simple_expandable_list_item_1, parent, false);
            }
            TextView text1 = (TextView) v.findViewById(android.R.id.text1);
            text1.setText(realNames[groupPosition]);
            text1.setTypeface(Typeface.createFromAsset(getActivity().getAssets(), Constants.TYPEFACE_LIGHT));
            text1.setTextSize(TypedValue.COMPLEX_UNIT_SP, 18);

            return v;
        }

        @Override
        public View getChildView(final int groupPosition, final int childPosition, boolean isLastChild, View convertView, ViewGroup parent) {
            View v = convertView;
            if (v == null) {
                LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
                v = inflater.inflate(android.R.layout.simple_list_item_multiple_choice, parent, false);
            }
            final Exercise exercise = data.get(groupPosition).getExercisesList().get(childPosition);
            final CheckedTextView text1 = (CheckedTextView) v.findViewById(android.R.id.text1);
            text1.setText(exercise.getName());
            text1.setTypeface(Typeface.createFromAsset(getActivity().getAssets(), Constants.TYPEFACE_LIGHT));
            text1.setChecked(exercise.isChecked());
            text1.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    text1.toggle();
                    Log.e("log", exercise.getName() + " become checked: " + text1.isChecked());
                    exercise.setChecked(text1.isChecked());
                    if (text1.isChecked()) {
                        if (!checkedIds.contains(Integer.valueOf((int) exercise.getId())))
                            checkedIds.add((int) exercise.getId());
                    } else {
                        checkedIds.remove(Integer.valueOf((int) exercise.getId()));
                    }
                    data.get(groupPosition).getExercisesList().set(childPosition, exercise);
                    v.setTag(exercise);
                }
            });
            v.setTag(exercise);
            return v;
        }

        @Override
        public boolean isChildSelectable(int groupPosition, int childPosition) {
            return true;
        }
    }
}
