package com.nethergrim.combogymdiary.fragments;

import android.content.Context;
import android.graphics.Typeface;
import android.os.Bundle;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseExpandableListAdapter;
import android.widget.ExpandableListView;
import android.widget.TextView;

import com.nethergrim.combogymdiary.Constants;
import com.nethergrim.combogymdiary.storage.DB;
import com.nethergrim.combogymdiary.R;
import com.nethergrim.combogymdiary.dialogs.DialogEditExercise;
import com.nethergrim.combogymdiary.dialogs.DialogUniversalApprove;
import com.nethergrim.combogymdiary.model.Exercise;
import com.nethergrim.combogymdiary.model.ExerciseGroup;
import com.nethergrim.combogymdiary.view.FAB;
import com.nethergrim.combogymdiary.view.TextViewLight;
import com.shamanland.fab.ShowHideOnScroll;

import java.util.List;

public class ExerciseListFragment extends AbstractFragment {


    private ExpandableListView elv;
    private ExercisesAdapter adapter;

    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);
        setRetainInstance(true);
    }

    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.exersises_list, null);
        getActivity().getActionBar().setTitle(R.string.excersisiesListButtonString);
        elv = (ExpandableListView) v.findViewById(R.id.elvExercises);
        adapter = new ExercisesAdapter(getActivity());
        elv.setAdapter(adapter);
        FAB fab = (FAB) v.findViewById(R.id.fabAddExercises);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                DialogEditExercise dialog = new DialogEditExercise();
                dialog.show(getFragmentManager(), "tag");
            }
        });
        elv.setOnTouchListener(new ShowHideOnScroll(fab));
        return v;
    }

    public void updateList(Context context) {
        if (adapter != null) {
            adapter.update(context);
        } else {
            adapter = new ExercisesAdapter(getActivity());
            elv.setAdapter(adapter);
        }
    }

    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        menu.clear();
        super.onCreateOptionsMenu(menu, inflater);
    }

    private class ExercisesAdapter extends BaseExpandableListAdapter {

        private Context context;
        private List<ExerciseGroup> data;

        public ExercisesAdapter(Context context) {
            this.context = context;
            DB db = new DB(context);
            db.open();
            this.data = db.fetchExerciseGroups();
            db.close();
        }

        public void update(Context context) {
            this.context = context;
            DB db = new DB(context);
            db.open();
            this.data = db.fetchExerciseGroups();
            db.close();
            notifyDataSetChanged();
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
            LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            View v = inflater.inflate(R.layout.simple_expandable_list_item_1, parent, false);
            TextViewLight text1 = (TextViewLight) v.findViewById(R.id.tvl1);
            text1.setText(Constants.getRealPartOfBodyName(data.get(groupPosition).getName()));
            text1.setTypeface(Typeface.createFromAsset(getActivity().getAssets(), Constants.TYPEFACE_LIGHT));
            text1.setTextSize(TypedValue.COMPLEX_UNIT_SP, 18);
            v.setTag(data.get(groupPosition));
            return v;
        }

        @Override
        public View getChildView(final int groupPosition, final int childPosition, boolean isLastChild, View convertView, ViewGroup parent) {
            LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            View v = inflater.inflate(R.layout.item_list_with_delete_btn, parent, false);
            v.setTag(data.get(groupPosition).getExercisesList().get(childPosition));
            TextView text1 = (TextView) v.findViewById(R.id.text_item_name);
            text1.setText(data.get(groupPosition).getExercisesList().get(childPosition).getName());
            text1.setTypeface(Typeface.createFromAsset(getActivity().getAssets(), Constants.TYPEFACE_LIGHT));
            text1.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    DialogEditExercise dialogEditExercise = new DialogEditExercise();
                    Bundle args = new Bundle();
                    args.putSerializable(Constants.BUNDLE_EXERCISE, data.get(groupPosition).getExercisesList().get(childPosition));
                    dialogEditExercise.setArguments(args);
                    dialogEditExercise.show(getFragmentManager(), DialogEditExercise.class.getName());
                }
            });
            v.findViewById(R.id.btn_delete).setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    DialogUniversalApprove dialogUniversalApprove = new DialogUniversalApprove();
                    Bundle args = new Bundle();
                    args.putInt(Constants.TYPE_OF_DIALOG, DialogUniversalApprove.TYPE_DELETE_EXERCISE);
                    args.putInt(Constants._ID, (int) data.get(groupPosition).getExercisesList().get(childPosition).getId());
                    dialogUniversalApprove.setArguments(args);
                    dialogUniversalApprove.show(getFragmentManager(), DialogUniversalApprove.class.getName());
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
