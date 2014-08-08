package com.nethergrim.combogymdiary.fragments;

import android.app.Activity;
import android.app.Fragment;
import android.content.Context;
import android.os.Bundle;
import android.view.ContextMenu;
import android.view.ContextMenu.ContextMenuInfo;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView.AdapterContextMenuInfo;
import android.widget.BaseExpandableListAdapter;
import android.widget.ExpandableListView;
import android.widget.TextView;
import android.widget.Toast;

import com.nethergrim.combogymdiary.DB;
import com.nethergrim.combogymdiary.R;
import com.nethergrim.combogymdiary.dialogs.DialogAddExercise;
import com.nethergrim.combogymdiary.model.Exercise;
import com.nethergrim.combogymdiary.tools.Prefs;
import com.nethergrim.combogymdiary.view.FloatingActionButton;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class ExerciseListFragment extends Fragment {

    private static final int CM_DELETE_ID = 1;
    private DB db;
//    private int LOADER_ID = 1;
    private OnExerciseEditPressed mListener;
    private FloatingActionButton fab;
    private ExpandableListView elv;
    private ExercisesAdapter adapter;

    public static interface OnExerciseEditPressed {
        public void onExerciseEdit(long id);
    }

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
        adapter = new ExercisesAdapter(getActivity(), db.getExercises());

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

    private void goToEditExe(long ID) {
        mListener.onExerciseEdit(ID);
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        registerForContextMenu(elv);
    }

    public void onResume() {
        super.onResume();
        fab.show();
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
        menu.add(0, CM_DELETE_ID, 0, R.string.delete_record);
    }

    public boolean onContextItemSelected(MenuItem item) {
        AdapterContextMenuInfo acmi = (AdapterContextMenuInfo) item
                .getMenuInfo();
        if (item.getItemId() == CM_DELETE_ID) {

            TextView tvTmp = (TextView) acmi.targetView;
            String exeName = tvTmp.getText().toString();

            if (Prefs.getPreferences().getTrainingAtProgress()) {
                Toast.makeText(getActivity(), R.string.error_deleting_exe, Toast.LENGTH_SHORT).show();
            } else {
                db.delRec_Exe(acmi.id);
                db.deleteExersice(exeName);
                Toast.makeText(getActivity(), R.string.deleted, Toast.LENGTH_SHORT).show();
                // FIXME update fragment
//                ((FragmentActivity) getActivity()).getSupportLoaderManager().getLoader(LOADER_ID).forceLoad();
            }

            return true;
        }
        return super.onContextItemSelected(item);
    }

    private class ExercisesAdapter extends BaseExpandableListAdapter{

        private Context context;
        private ArrayList<Exercise> data;
        private int groupsCount = 0;
        private String[] groupsArray;

        public ExercisesAdapter(Context context, List<Exercise> data){
            this.context = context;
            this.data = (ArrayList<Exercise>) data;
            groupsCount = getGroupsCount(this.data);
        }

        private int getGroupsCount(ArrayList<Exercise> list){
            Set<String> groups = new HashSet<String>();
            for (Exercise aData : list) {
                groups.add(aData.getPartOfBody());
            }
            groupsArray = groups.toArray(groupsArray);
            return groups.size();
        }

        public void update(ArrayList<Exercise> data){
            this.data = data;
            groupsCount = getGroupsCount(this.data);
            notifyDataSetChanged();
        }

        @Override
        public int getGroupCount() {
            return groupsCount;
        }

        @Override
        public int getChildrenCount(int groupPosition) {
            return data.size();
        }

        @Override
        public String getGroup(int groupPosition) {
            return groupsArray[groupPosition];
        }

        @Override
        public Exercise getChild(int groupPosition, int childPosition) {
            int childCounter = 0;

            for (Exercise aData : data) {
                if (aData.getPartOfBody().equals(groupsArray[groupPosition])) {
                    if (childPosition > childCounter) {
                        childCounter++;
                    } else {
                        return aData;
                    }
                }
            } // FIXME test me!!

            return null;
        }

        @Override
        public long getGroupId(int groupPosition) {
            return groupPosition;
        }

        @Override
        public long getChildId(int groupPosition, int childPosition) {

            int childCounter = 0;
            for (Exercise aData : data) {
                if (aData.getPartOfBody().equals(groupsArray[groupPosition])) {
                    if (childPosition > childCounter) {
                        childCounter++;
                    } else {
                        return aData.getId();
                    }
                }
            } // FIXME test me!!
            return 0;
        }

        @Override
        public boolean hasStableIds() {
            return false;
        }

        @Override
        public View getGroupView(int groupPosition, boolean isExpanded, View convertView, ViewGroup parent) {
            View v = convertView;

            if (v == null) {

                LayoutInflater inflater = (LayoutInflater)context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);

//                v = inflater.inflate(R.layout.item_layout, parent, false);

            }
            return v;
        }

        @Override
        public View getChildView(int groupPosition, int childPosition, boolean isLastChild, View convertView, ViewGroup parent) {
            View v = convertView;

            if (v == null) {

                LayoutInflater inflater = (LayoutInflater)context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);

//                v = inflater.inflate(R.layout.item_layout, parent, false);

            }



            return v;
        }

        @Override
        public boolean isChildSelectable(int groupPosition, int childPosition) {
            return true;
        }
    }
}
