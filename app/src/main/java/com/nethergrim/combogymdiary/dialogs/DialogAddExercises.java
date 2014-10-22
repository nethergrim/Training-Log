package com.nethergrim.combogymdiary.dialogs;

import android.app.AlertDialog;
import android.app.Dialog;
import android.app.DialogFragment;
import android.content.Context;
import android.content.DialogInterface;
import android.graphics.Typeface;
import android.os.Bundle;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseExpandableListAdapter;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.ExpandableListView;
import android.widget.TextView;

import com.nethergrim.combogymdiary.Constants;
import com.nethergrim.combogymdiary.storage.DB;
import com.nethergrim.combogymdiary.R;
import com.nethergrim.combogymdiary.model.Exercise;
import com.nethergrim.combogymdiary.model.ExerciseGroup;
import com.nethergrim.combogymdiary.view.TextViewLight;

import java.util.ArrayList;
import java.util.List;

public class DialogAddExercises extends DialogFragment implements DialogInterface.OnClickListener {

    private OnExerciseAddCallback listener;
    private ExercisesAdapter adapter;

    public void setListener (OnExerciseAddCallback callback){
        this.listener = callback;
    }

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

    public Dialog onCreateDialog(Bundle savedInstanceState) {
        LayoutInflater inflater = (LayoutInflater) getActivity().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View v = inflater.inflate(R.layout.dialog_add_exercise, null, false);
        ExpandableListView elv = (ExpandableListView) v.findViewById(R.id.elvExercisesToAdd);
        elv.setOnGroupClickListener(new ExpandableListView.OnGroupClickListener() {
            @Override
            public boolean onGroupClick(ExpandableListView parent, View v, int groupPosition, long id) {
                adapter.onGroupExpanded(groupPosition);
                return true;
            }
        });
        adapter = new ExercisesAdapter(getActivity());
        elv.setAdapter(adapter);
        elv.setGroupIndicator(null);
        elv.setChoiceMode(ExpandableListView.CHOICE_MODE_MULTIPLE);
        AlertDialog.Builder adb = new AlertDialog.Builder(getActivity())
                .setTitle(R.string.add_an_exercise)
                .setPositiveButton(R.string.yes, this)
                .setNegativeButton(R.string.no, this)
                .setView(v);
        return adb.create();
    }


    public interface OnExerciseAddCallback {
        public void onExerciseAddedCallback(List<Long> idList);
    }

    private class ExercisesAdapter extends BaseExpandableListAdapter {

        private LayoutInflater inflater;
        private List<ExerciseGroup> data;

        public ExercisesAdapter(Context context) {
            this.inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            DB db = new DB(context);
            db.open();
            this.data = db.fetchExerciseGroups();
            db.close();
        }

        public List<Long> getChecked(){
            List<Long> result = new ArrayList<Long>();
            for (ExerciseGroup aData : data) {
                for (int j = 0; j < aData.getExercisesList().size(); j++) {
                    if (aData.getExercisesList().get(j).isChecked()) {
                        result.add(aData.getExercisesList().get(j).getId());
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
                v = inflater.inflate(R.layout.group_item_layout, parent, false);
            }
            TextView text1 = (TextView) v.findViewById(R.id.text_group_name);
            text1.setText(Constants.getRealPartOfBodyName(data.get(groupPosition).getName()));
            text1.setTypeface(Typeface.createFromAsset(getActivity().getAssets(), Constants.TYPEFACE_LIGHT));
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
            checkBox.setChecked(data.get(groupPosition).getExercisesList().get(childPosition).isChecked());
            textView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    data.get(groupPosition).getExercisesList().get(childPosition).setChecked(!data.get(groupPosition).getExercisesList().get(childPosition).isChecked());
                    checkBox.setChecked(data.get(groupPosition).getExercisesList().get(childPosition).isChecked());
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
