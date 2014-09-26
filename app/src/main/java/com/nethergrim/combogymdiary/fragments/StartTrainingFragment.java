package com.nethergrim.combogymdiary.fragments;

import android.app.Activity;
import android.app.Fragment;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ListView;

import com.nethergrim.combogymdiary.DB;
import com.nethergrim.combogymdiary.R;
import com.nethergrim.combogymdiary.activities.CreatingTrainingDayActivity;
import com.nethergrim.combogymdiary.adapter.ExpandableListViewAdapter;
import com.nethergrim.combogymdiary.model.TrainingDay;
import com.nethergrim.combogymdiary.row.TrainingDayRow;
import com.nethergrim.combogymdiary.row.interfaces.OnTrainingDayRowPressed;
import com.nethergrim.combogymdiary.tools.BaseActivityInterface;
import com.nethergrim.combogymdiary.view.FAB;
import com.nhaarman.listviewanimations.appearance.simple.SwingBottomInAnimationAdapter;
import com.shamanland.fab.ShowHideOnScroll;

import java.util.List;

public class StartTrainingFragment extends AbstractFragment implements OnTrainingDayRowPressed, OnItemClickListener, View.OnClickListener {

    private ListView lvMain;
    private DB db;
    private BaseActivityInterface baseActivityInterface;
    private ExpandableListViewAdapter adapter;

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
        adapter = new ExpandableListViewAdapter(getActivity());
//        SwingBottomInAnimationAdapter animationAdapter = new SwingBottomInAnimationAdapter(adapter);
//        animationAdapter.setAbsListView(lvMain);
        lvMain.setAdapter(adapter);
        lvMain.setOnItemClickListener(this);
        FAB fabAdd = (FAB) v.findViewById(R.id.fabAddTrainings);
        fabAdd.setOnClickListener(this);
        lvMain.setOnTouchListener(new ShowHideOnScroll(fabAdd));
        new GetTrainingDaysTask().execute();
        return v;
    }

    private void showData(List<TrainingDay> trainingDays) {
        for (TrainingDay trainingDay : trainingDays) {
            adapter.addRow(new TrainingDayRow(trainingDay, this));
        }
        adapter.notifyDataSetChanged();
    }

    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        menu.clear();
        super.onCreateOptionsMenu(menu, inflater);
    }

    @Override
    public void onTrainingStartPressed(TrainingDay trainingDay1) {

    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        adapter.toggle(position);
        if (!adapter.getRows().get(position).isOpened())
        lvMain.smoothScrollToPosition(position);
    }

    @Override
    public void onClick(View v) {
        Intent gotoAddingProgramActivity = new Intent(getActivity(), CreatingTrainingDayActivity.class);
        startActivity(gotoAddingProgramActivity);
    }

    private class GetTrainingDaysTask extends AsyncTask<Void, Void, List<TrainingDay>> {

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
