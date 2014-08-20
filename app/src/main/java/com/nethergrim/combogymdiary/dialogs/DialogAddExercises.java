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
import android.widget.CheckBox;
import android.widget.CheckedTextView;
import android.widget.CompoundButton;
import android.widget.ExpandableListView;
import android.widget.TextView;

import com.nethergrim.combogymdiary.Constants;
import com.nethergrim.combogymdiary.DB;
import com.nethergrim.combogymdiary.R;
import com.nethergrim.combogymdiary.model.Exercise;
import com.nethergrim.combogymdiary.model.ExerciseGroup;
import com.nethergrim.combogymdiary.view.TextViewLight;

import java.util.ArrayList;
import java.util.List;

public class DialogAddExercises extends DialogFragment implements DialogInterface.OnClickListener {

    private OnExerciseAddCallback listener;
    private ExercisesAdapter adapter;
    private ExpandableListView elv;

    @Override
    public void onClick(DialogInterface dialog, int which) {
        switch (which) {
            case Dialog.BUTTON_POSITIVE:
                listener.onExerciseAddedCallback(adapter.getChecked());
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
        this.listener = (OnExerciseAddCallback) activity;
    }

    public Dialog onCreateDialog(Bundle savedInstanceState) {
        LayoutInflater inflater = (LayoutInflater) getActivity().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View v = inflater.inflate(R.layout.dialog_add_exercise, null, false);
        elv = (ExpandableListView) v.findViewById(R.id.elvExercisesToAdd);
        elv.setOnGroupClickListener(new ExpandableListView.OnGroupClickListener() {
            @Override
            public boolean onGroupClick(ExpandableListView parent, View v, int groupPosition, long id) {
                return true;
            }
        });
        adapter = new ExercisesAdapter(getActivity());
        elv.setAdapter(adapter);
        elv.setChoiceMode(ExpandableListView.CHOICE_MODE_MULTIPLE);
        AlertDialog.Builder adb = new AlertDialog.Builder(getActivity())
                .setTitle(R.string.add_an_exercise)
                .setPositiveButton(R.string.yes, this)
                .setNegativeButton(R.string.no, this)
                .setView(v);
        return adb.create();
    }


    public interface OnExerciseAddCallback {
        public void onExerciseAddedCallback(List<Integer> idList);
    }

    private class ExercisesAdapter extends BaseExpandableListAdapter {

        private LayoutInflater inflater;
        private List<ExerciseGroup> data;
        private boolean blocked = false;

        public ExercisesAdapter(Context context) {
            this.realNames = Constants.partsOfBody;
            this.inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            DB db = new DB(context);
            db.open();
            this.data = db.getExerciseGroups();
            db.close();
        }

        public List<Integer> getChecked(){
            List<Integer> result = new ArrayList<Integer>();
            for (ExerciseGroup aData : data) {
                for (int j = 0; j < aData.getExercisesList().size(); j++) {
                    if (aData.getExercisesList().get(j).isChecked()) {
                        result.add((int) aData.getExercisesList().get(j).getId());
                    }
                }
            }
            return result;
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
            return getChild(groupPosition,childPosition).getId();
        }

        @Override
        public boolean hasStableIds() {
            return true;
        }

        @Override
        public View getGroupView(int groupPosition, boolean isExpanded, View convertView, ViewGroup parent) {
            View v = convertView;
            if (v == null) {
                v = inflater.inflate(android.R.layout.simple_expandable_list_item_1, parent, false);
            }
            TextView text1 = (TextView) v.findViewById(android.R.id.text1);
            text1.setText(Constants.getRealPartOfBodyName(data.get(groupPosition).getName()));
            text1.setTypeface(Typeface.createFromAsset(getActivity().getAssets(), Constants.TYPEFACE_LIGHT));
            text1.setTextSize(TypedValue.COMPLEX_UNIT_SP, 18);
            elv.expandGroup(groupPosition);
            return v;
        }

        @Override
        public View getChildView(final int groupPosition, final int childPosition, boolean isLastChild, View convertView, ViewGroup parent) {
            View v = convertView;
            if (v == null){
                v = inflater.inflate(R.layout.list_item_checkbox, parent, false);
            }
            TextViewLight textView = (TextViewLight)v.findViewById(R.id.label);
            textView.setText(data.get(groupPosition).getExercisesList().get(childPosition).getName());
            final CheckBox checkBox = (CheckBox)v.findViewById(R.id.check);
            blocked = true;
            checkBox.setChecked(data.get(groupPosition).getExercisesList().get(childPosition).isChecked());
            blocked = false;
            checkBox.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                @Override
                public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                    if (!blocked){
                        data.get(groupPosition).getExercisesList().get(childPosition).setChecked(isChecked);
                    }
                }
            });
            textView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    checkBox.setChecked(!checkBox.isChecked());
                }
            });
            return v;
        }

        @Override
        public boolean isChildSelectable(int groupPosition, int childPosition) {
            return true;
        }
    }
}
