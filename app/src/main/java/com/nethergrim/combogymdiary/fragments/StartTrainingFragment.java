package com.nethergrim.combogymdiary.fragments;

import android.app.Activity;
import android.app.Fragment;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.ContextMenu;
import android.view.ContextMenu.ContextMenuInfo;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.AdapterContextMenuInfo;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ListView;
import android.widget.Toast;

import com.nethergrim.combogymdiary.DB;
import com.nethergrim.combogymdiary.R;
import com.nethergrim.combogymdiary.activities.CreatingTrainingDayActivity;
import com.nethergrim.combogymdiary.adapter.ListViewAdapter;
import com.nethergrim.combogymdiary.dialogs.DialogGoToMarket;
import com.nethergrim.combogymdiary.model.DayOfWeek;
import com.nethergrim.combogymdiary.model.TrainingDay;
import com.nethergrim.combogymdiary.row.ExpandableRow;
import com.nethergrim.combogymdiary.row.TrainingDayRow;
import com.nethergrim.combogymdiary.tools.BaseActivityInterface;
import com.nethergrim.combogymdiary.tools.Prefs;
import com.nethergrim.combogymdiary.view.FAB;
import com.nhaarman.listviewanimations.appearance.simple.SwingBottomInAnimationAdapter;
import com.shamanland.fab.ShowHideOnScroll;

import java.util.List;

public class StartTrainingFragment extends Fragment implements TrainingDayRow.OnTrainingDayRowPressed {

    private ListView lvMain;
    private DB db;
    private BaseActivityInterface baseActivityInterface;
    private ListViewAdapter adapter;

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        try {
            baseActivityInterface = (BaseActivityInterface) activity;
        } catch (ClassCastException e) {
            throw new ClassCastException(activity.toString() + " must implement OnHeadlineSelectedListener");
        }
    }

    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setRetainInstance(true);
        setHasOptionsMenu(true);
        db = new DB(getActivity());
    }

    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.start_training, null);
        lvMain = (ListView) v.findViewById(R.id.lvStartTraining);
        getActivity().getActionBar().setTitle(R.string.startTrainingButtonString);
        adapter = new ListViewAdapter(getActivity());
        SwingBottomInAnimationAdapter animationAdapter = new SwingBottomInAnimationAdapter(adapter);
        animationAdapter.setAbsListView(lvMain);
        lvMain.setAdapter(animationAdapter);
        lvMain.setOnItemClickListener(new OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                if (adapter.getItem(i) instanceof ExpandableRow) {
                    ExpandableRow expandableRow = (ExpandableRow) adapter.getItem(i);
                    expandableRow.toggle();
                }
            }
        });
        FAB fabAdd = (FAB) v.findViewById(R.id.fabAddTrainings);
        fabAdd.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent gotoAddingProgramActivity = new Intent(getActivity(), CreatingTrainingDayActivity.class);
                startActivity(gotoAddingProgramActivity);
            }
        });
        lvMain.setOnTouchListener(new ShowHideOnScroll(fabAdd));
        new GetTrainingDaysTask().execute();
        return v;
    }

    private void showData(List<TrainingDay> trainingDays){
        for (TrainingDay trainingDay : trainingDays){
            adapter.addRow(new TrainingDayRow(trainingDay, this));
        }
        adapter.notifyDataSetChanged();
    }

    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        menu.clear();
        super.onCreateOptionsMenu(menu, inflater);
    }

    @Override
    public void onTrainingDayPressed(TrainingDay trainingDay1) {

    }

    private class GetTrainingDaysTask extends AsyncTask<Void,Void,List<TrainingDay>>{

        @Override
        protected List<TrainingDay> doInBackground(Void... voids) {
            return db.getTrainingDays();
        }

        @Override
        protected void onPostExecute(List<TrainingDay> trainingDays) {
            super.onPostExecute(trainingDays);
            showData(trainingDays);
        }
    }
}
