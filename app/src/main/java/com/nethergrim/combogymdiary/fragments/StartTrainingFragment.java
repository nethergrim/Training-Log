package com.nethergrim.combogymdiary.fragments;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
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

import com.nethergrim.combogymdiary.Constants;
import com.nethergrim.combogymdiary.DB;
import com.nethergrim.combogymdiary.MyApp;
import com.nethergrim.combogymdiary.R;
import com.nethergrim.combogymdiary.activities.CreatingTrainingDayActivity;
import com.nethergrim.combogymdiary.adapter.ExpandableListViewAdapter;
import com.nethergrim.combogymdiary.model.TrainingDay;
import com.nethergrim.combogymdiary.row.TrainingDayRow;
import com.nethergrim.combogymdiary.row.interfaces.TrainingDayRowInterface;
import com.nethergrim.combogymdiary.tools.BaseActivityInterface;
import com.nethergrim.combogymdiary.view.FAB;
import com.nethergrim.combogymdiary.view.HeaderFooterListVIew;
import com.shamanland.fab.ShowHideOnScroll;

import java.util.List;

public class StartTrainingFragment extends AbstractFragment implements TrainingDayRowInterface, OnItemClickListener, View.OnClickListener {

    private HeaderFooterListVIew list;
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
        list = (HeaderFooterListVIew) v.findViewById(R.id.lvStartTraining);
        getActivity().getActionBar().setTitle(R.string.startTrainingButtonString);
        adapter = new ExpandableListViewAdapter(getActivity());
        list.setAdapter(adapter);
        list.setOnItemClickListener(this);
        FAB fabAdd = (FAB) v.findViewById(R.id.fabAddTrainings);
        fabAdd.setOnClickListener(this);
        list.setOnTouchListener(new ShowHideOnScroll(fabAdd));
        return v;
    }

    @Override
    public void onStart() {
        super.onStart();
        loadData();
    }

    public void loadData() {
        new GetTrainingDaysTask().execute();
    }

    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        menu.clear();
        super.onCreateOptionsMenu(menu, inflater);
    }

    @Override
    public void onTrainingStartPressed(TrainingDay trainingDay) {
        baseActivityInterface.onStartTrainingPressed(trainingDay.getId());
    }

    @Override
    public void onDeletePressed(final TrainingDay trainingDay) {
        AlertDialog.Builder customBuilder = new AlertDialog.Builder(getActivity());
        customBuilder.setTitle(getString(R.string.delete));
        customBuilder.setMessage(getString(R.string.delete) + " " + trainingDay.getTrainingName() + " ?")
                .setPositiveButton(getString(R.string.yes), new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        db.deleteTrainingDay(trainingDay.getId(), false);
                        loadData();
                        dialog.cancel();
                    }
                })
                .setNegativeButton(getString(R.string.no), new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        dialog.cancel();
                    }
                });
        Dialog dialog = customBuilder.create();
        dialog.show();
    }

    @Override
    public void onEditPressed(TrainingDay trainingDay) {
        Intent intent = new Intent(getActivity(), CreatingTrainingDayActivity.class);
        intent.putExtra(CreatingTrainingDayActivity.BUNDLE_ID_KEY, trainingDay.getId());
        startActivity(intent);
    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        position--;
        if (!adapter.getRows().get(position).isOpened())        {
            list.smoothScrollToPosition(position);
            final int finalPosition = position;
            list.postDelayed(new Runnable() {
                @Override
                public void run() {
                    if (finalPosition >= list.getLastVisiblePosition() - 1){
                        try {
                            list.smoothScrollToPosition(finalPosition + 1);
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }
                }
            }, getResources().getInteger(R.integer.animation_expand_time));
        }

        adapter.toggle(position);
    }

    @Override
    public void onClick(View v) {
        Intent gotoAddingProgramActivity = new Intent(getActivity(), CreatingTrainingDayActivity.class);
        startActivity(gotoAddingProgramActivity);
    }

    private class GetTrainingDaysTask extends AsyncTask<Void, Void, List<TrainingDay>> {

        @Override
        protected List<TrainingDay> doInBackground(Void... voids) {
            try {
                adapter.clearAdapter();

            } catch (Exception e) {
                e.printStackTrace();
            }
            return db.getTrainingDays();
        }

        @Override
        protected void onPostExecute(List<TrainingDay> aVoid) {
            super.onPostExecute(aVoid);
            for (TrainingDay trainingDay : aVoid) {
                adapter.addRow(new TrainingDayRow(trainingDay, StartTrainingFragment.this));
            }
            adapter.notifyDataSetChanged();
        }
    }
}
