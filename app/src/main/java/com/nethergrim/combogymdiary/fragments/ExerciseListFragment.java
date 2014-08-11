package com.nethergrim.combogymdiary.fragments;

import android.app.Activity;
import android.app.Fragment;
import android.content.Context;
import android.graphics.Typeface;
import android.os.Bundle;
import android.util.Log;
import android.util.TypedValue;
import android.view.ContextMenu;
import android.view.ContextMenu.ContextMenuInfo;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseExpandableListAdapter;
import android.widget.ExpandableListView;
import android.widget.TextView;
import android.widget.Toast;

import com.nethergrim.combogymdiary.Constants;
import com.nethergrim.combogymdiary.DB;
import com.nethergrim.combogymdiary.R;
import com.nethergrim.combogymdiary.dialogs.DialogAddExercise;
import com.nethergrim.combogymdiary.model.Exercise;
import com.nethergrim.combogymdiary.model.ExerciseGroup;
import com.nethergrim.combogymdiary.tools.Prefs;
import com.nethergrim.combogymdiary.view.FloatingActionButton;

import java.util.List;

public class ExerciseListFragment extends Fragment {

    private static final int CM_DELETE_ID = 1;
    private OnExerciseEditPressed mListener;
    private FloatingActionButton fab;
    private ExpandableListView elv;
    private ExercisesAdapter adapter;
    private DB db;

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        try {
            mListener = (OnExerciseEditPressed) activity;
        } catch (ClassCastException e) {
            throw new ClassCastException(activity.toString() + " must implement OnHeadlineSelectedListener");
        }
    }

    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);
        setRetainInstance(true);
        db = new DB(getActivity());
        db.open();
    }

    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.exersises_list, null);
        getActivity().getActionBar().setTitle(R.string.excersisiesListButtonString);
        elv = (ExpandableListView) v.findViewById(R.id.elvExercises);
        adapter = new ExercisesAdapter(getActivity());
        elv.setAdapter(adapter);
        fab = new FloatingActionButton.Builder(getActivity())
                .withDrawable(getResources().getDrawable(R.drawable.ic_action_new))
                .withButtonColor(getResources().getColor(R.color.holo_blue_light))
                .withGravity(Gravity.BOTTOM | Gravity.RIGHT)
                .withMargins(0, 0, 16, 16)
                .create();
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                DialogAddExercise dialog = new DialogAddExercise();
                dialog.show(getFragmentManager(), "tag");
            }
        });
        fab.hide();
        return v;
    }

    public void updateList(Context context) {
        if (adapter != null) {
            adapter.update(context);
        }
    }

    private void goToEditExe(long ID) {
        mListener.onExerciseEdit(ID);
    }

    public void onResume() {
        super.onResume();
        fab.show();
        registerForContextMenu(elv);
    }

    public void onPause() {
        super.onPause();
        fab.hide();
        unregisterForContextMenu(elv);
    }

    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        menu.clear();
        super.onCreateOptionsMenu(menu, inflater);
    }

    public void onCreateContextMenu(ContextMenu menu, View v, ContextMenuInfo menuInfo) {
        super.onCreateContextMenu(menu, v, menuInfo);

        ExpandableListView.ExpandableListContextMenuInfo info =
                (ExpandableListView.ExpandableListContextMenuInfo) menuInfo;

        int type = ExpandableListView.getPackedPositionType(info.packedPosition);

        int group = ExpandableListView.getPackedPositionGroup(info.packedPosition);

        int child = ExpandableListView.getPackedPositionChild(info.packedPosition);

        // Only create a context menu for child items
        if (type == ExpandableListView.PACKED_POSITION_TYPE_CHILD) {
            menu.add(0, CM_DELETE_ID, 0, R.string.delete_record);

        }

    }

    public boolean onContextItemSelected(MenuItem item) {
        ExpandableListView.ExpandableListContextMenuInfo info = (ExpandableListView.ExpandableListContextMenuInfo) item.getMenuInfo();
        int groupPos = 0, childPos = 0;
        int type = ExpandableListView.getPackedPositionType(info.packedPosition);
        if (type == ExpandableListView.PACKED_POSITION_TYPE_CHILD) {
            groupPos = ExpandableListView.getPackedPositionGroup(info.packedPosition);
            childPos = ExpandableListView.getPackedPositionChild(info.packedPosition);
            if (item.getItemId() == CM_DELETE_ID) {
                if (Prefs.getPreferences().getTrainingAtProgress()) {
                    Toast.makeText(getActivity(), R.string.error_deleting_exe, Toast.LENGTH_SHORT).show();
                    return false;
                }
                Exercise exercise = db.getExerciseGroups().get(groupPos).getExercisesList().get(childPos);
                db.deleteExercise(exercise.getId());
                db.deleteExersice(exercise.getName());
                Toast.makeText(getActivity(), R.string.deleted, Toast.LENGTH_SHORT).show();
                updateList(getActivity());
                return true;
            }
        }
        return super.onContextItemSelected(item);
    }

    public static interface OnExerciseEditPressed {
        public void onExerciseEdit(long id);
    }

    private class ExercisesAdapter extends BaseExpandableListAdapter {

        private Context context;
        private List<ExerciseGroup> data;

        public ExercisesAdapter(Context context) {
            this.context = context;
            DB db = new DB(context);
            db.open();
            this.data = db.getExerciseGroups();
            db.close();
        }

        public void update(Context context) {
            this.context = context;
            DB db = new DB(context);
            db.open();
            this.data = db.getExerciseGroups();
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
            View v = inflater.inflate(android.R.layout.simple_expandable_list_item_1, parent, false);
            TextView text1 = (TextView) v.findViewById(android.R.id.text1);
            String realNames[] = Constants.getPartsOfBodyRealNames(getActivity());
            text1.setText(realNames[groupPosition]);
            text1.setTypeface(Typeface.createFromAsset(getActivity().getAssets(), Constants.TYPEFACE_LIGHT));
            text1.setTextSize(TypedValue.COMPLEX_UNIT_SP, 18);
            v.setTag(data.get(groupPosition));
            return v;
        }

        @Override
        public View getChildView(int groupPosition, int childPosition, boolean isLastChild, View convertView, ViewGroup parent) {
            LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            View v = inflater.inflate(android.R.layout.simple_list_item_1, parent, false);
            v.setTag(data.get(groupPosition).getExercisesList().get(childPosition));
            TextView text1 = (TextView) v.findViewById(android.R.id.text1);
            text1.setText(data.get(groupPosition).getExercisesList().get(childPosition).getName());
            text1.setTypeface(Typeface.createFromAsset(getActivity().getAssets(), Constants.TYPEFACE_LIGHT));
            text1.setTextSize(TypedValue.COMPLEX_UNIT_SP, 16);
            return v;
        }

        @Override
        public boolean isChildSelectable(int groupPosition, int childPosition) {
            return true;
        }
    }
}
