package com.nethergrim.combogymdiary.activities;

import android.content.Context;
import android.graphics.Color;
import android.os.Bundle;
import android.util.Log;
import android.util.SparseBooleanArray;
import android.view.ActionMode;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.Toast;

import com.nethergrim.combogymdiary.DB;
import com.nethergrim.combogymdiary.R;
import com.nethergrim.combogymdiary.dialogs.DialogAddExercises;
import com.nethergrim.combogymdiary.dialogs.DialogInfo;
import com.nethergrim.combogymdiary.model.Exercise;
import com.nethergrim.combogymdiary.model.ExerciseTrainingObject;
import com.nethergrim.combogymdiary.tools.Prefs;
import com.nethergrim.combogymdiary.view.FAB;
import com.nethergrim.combogymdiary.view.TextViewLight;
import com.shamanland.fab.ShowHideOnScroll;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

public class NewCreatingTrainingDayActivity extends AnalyticsActivity implements DialogAddExercises.OnExerciseAddCallback, ActionMode.Callback {

    private ListView list;
    private TextViewLight textNoExe;
    private EditText etName;
    private DB db;
    private TrainingDayAdapter adapter;
    private View.OnTouchListener listener1;
    private View.OnTouchListener listener2;
    private View.OnTouchListener listener3;
    private boolean isInActionMode = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_new_creating_training_day);
        db = new DB(this);
        db.open();
        setTitle(R.string.creating_program);
        getActionBar().setDisplayHomeAsUpEnabled(true);
        getActionBar().setDisplayShowHomeEnabled(false);
        initButtons();
        initList();
        etName = (EditText) findViewById(R.id.etTrainingName);
        setTypeFaceLight(etName);
    }

    private void initList() {
        textNoExe = (TextViewLight) findViewById(R.id.text_add_exersices);
        list = (ListView) findViewById(R.id.listView);
        adapter = new TrainingDayAdapter(this);
        list.setAdapter(adapter);
        list.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                if (list.getCount() > 5) {
                    listener1.onTouch(v, event);
                    listener2.onTouch(v, event);
                    listener3.onTouch(v, event);
                }
                return true;
            }
        });
        list.setChoiceMode(AbsListView.CHOICE_MODE_MULTIPLE);
    }

    private void initButtons() {
        FAB fabSave = (FAB) findViewById(R.id.fabSave);
        FAB fabSs = (FAB) findViewById(R.id.fabSs);
        FAB fabAdd = (FAB) findViewById(R.id.btnAdd);

        listener1 = new ShowHideOnScroll(fabAdd);
        listener2 = new ShowHideOnScroll(fabSave);
        listener3 = new ShowHideOnScroll(fabSs);

        fabAdd.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                DialogAddExercises dialogAddExercises = new DialogAddExercises();
                dialogAddExercises.show(getFragmentManager(), DialogAddExercises.class.getName());
            }
        });
        fabSave.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                save();
            }
        });
        fabSs.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                addSuperset();
            }
        });
    }

    private void addSuperset() {                                                    // TODO add supersets

    }

    private void save() {
        if (etName.getText().toString() == null || etName.getText().toString().equals("")){
            Toast.makeText(this, R.string.enter_training_name, Toast.LENGTH_SHORT).show();
            return;
        } else if (list.getCount() == 0){
            Toast.makeText(this, R.string.add_exercises_to_workout, Toast.LENGTH_SHORT).show();
            return;
        } else {
            List<Row> rows = adapter.getRows();
            int trainingId = (int) db.addRecTrainings(etName.getText().toString());
            int firstSupersetPosition = 0;
            boolean lastWasSuperset = false;

            for (int i = 0; i < rows.size(); i++){
                ExerciseTrainingObject exerciseTrainingObject = new ExerciseTrainingObject();
                Row row = rows.get(i);

                exerciseTrainingObject.setTrainingProgramId(trainingId);
                exerciseTrainingObject.setExerciseId((int) row.getExercise().getId());
                exerciseTrainingObject.setPositionAtTraining(i);

                if (row.isInSuperset()){
                    exerciseTrainingObject.setSuperset(true);
                    exerciseTrainingObject.setPositionAtSuperset(row.getSupersetPosition());
                } else {
                    exerciseTrainingObject.setSuperset(false);
                    exerciseTrainingObject.setPositionAtSuperset(0);
                }

                if (row.isInSuperset() && !lastWasSuperset){
                    exerciseTrainingObject.setSupersetFirstItemId(-1);
                    firstSupersetPosition = (int) db.addExerciseTrainingObject(exerciseTrainingObject);
                } else if (row.isInSuperset() && lastWasSuperset) {
                    exerciseTrainingObject.setSupersetFirstItemId(firstSupersetPosition);
                    db.addExerciseTrainingObject(exerciseTrainingObject);
                } else {
                    exerciseTrainingObject.setSupersetFirstItemId(0);
                    db.addExerciseTrainingObject(exerciseTrainingObject);
                }

                lastWasSuperset = row.isInSuperset();
            }
            Toast.makeText(this, R.string.saved, Toast.LENGTH_SHORT).show();
            finish();
        }
    }

    @Override
    public void onStart() {
        super.onStart();
        showSuperSetDialog();
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (list.getCount() > 0) {
            textNoExe.setVisibility(View.GONE);
        } else {
            textNoExe.setVisibility(View.VISIBLE);
        }
    }

    private void showSuperSetDialog() {
        if (Prefs.getPreferences().getSuperSetInfoShowed() <= 3) {
            DialogInfo dialogInfo = new DialogInfo();
            Bundle args = new Bundle();
            args.putBoolean(DialogInfo.KEY_INFO_ABOUT_SUPERSET, true);
            dialogInfo.setArguments(args);
            dialogInfo.show(getFragmentManager(), DialogInfo.class.getName());
            Prefs.getPreferences().setSuperSetInfoShowed(Prefs.getPreferences().getSuperSetInfoShowed() + 1);
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                super.onBackPressed();
                return true;
        }
        return false;
    }

    @Override
    public void onExerciseAddedCallback(List<Integer> idList) {
        final List<Exercise> newExercises = new ArrayList<Exercise>();
        if (idList != null && idList.size() > 0){
            textNoExe.setVisibility(View.GONE);
            for (Integer anIdList : idList) {
                newExercises.add(db.getExercise(anIdList));
            }
            adapter.addData(newExercises);
            clearSelection();
        }
    }

    private void clearSelection(){
        for (int i = 0; i < list.getCount(); i++){
            list.setItemChecked(i, false);
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        db.close();
    }


    @Override
    public boolean onCreateActionMode(ActionMode mode, Menu menu) {
        isInActionMode = true;
        getMenuInflater().inflate(R.menu.menu_creating_training_day,menu);
        return true;
    }

    @Override
    public boolean onPrepareActionMode(ActionMode mode, Menu menu) {

        return false;
    }

    @Override
    public boolean onActionItemClicked(ActionMode mode, MenuItem item) {
        if (list.getCheckedItemCount() > 0) {

            SparseBooleanArray arrayToDelete = list.getCheckedItemPositions();
            int count = list.getCount();
            for (int i = count - 1; i >= 0; i--) {
                if (arrayToDelete.get(i)) {
                    adapter.removeItem(i);
                }
            }
        }
        clearSelection();
        mode.finish();
        return false;
    }

    @Override
    public void onDestroyActionMode(ActionMode mode) {
        isInActionMode = false;
        clearSelection();
    }

    private class TrainingDayAdapter extends BaseAdapter{

        private ArrayList<Row> rows = new ArrayList<Row>();
        private LayoutInflater inflater;

        public TrainingDayAdapter(Context context){
            this.inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        }

        @Override
        public void notifyDataSetChanged() {
            super.notifyDataSetChanged();
            if (getCount() < 1){
                NewCreatingTrainingDayActivity.this.textNoExe.setVisibility(View.VISIBLE);
            }
        }

        public void addData(List<Exercise> newData){
            for (Exercise aNewData : newData) {
                rows.add(new Row(aNewData));
            }
            notifyDataSetChanged();
        }

        public List<Row>getRows(){
            return this.rows;
        }

        public void removeItem(int position){
            rows.remove(position);
            this.notifyDataSetChanged();
        }

        @Override
        public int getCount() {
            return rows.size();
        }

        @Override
        public Row getItem(int position) {
            return rows.get(position);
        }

        @Override
        public long getItemId(int position) {
            return rows.get(position).getExercise().getId();
        }

        @Override
        public View getView(final int position, View convertView, ViewGroup parent) {
            View v = convertView;
            if (v == null){
                v = inflater.inflate(R.layout.list_item_creating_programm, parent, false);
                ViewHolder viewHolder = new ViewHolder();
                viewHolder.textViewLightExerciseName = (TextViewLight) v.findViewById(R.id.text_exercise);
                viewHolder.textViewLightSupersetNumber = (TextViewLight) v.findViewById(R.id.text_number_of_superset);
                viewHolder.imageViewSuperset = (ImageView)v.findViewById(R.id.imageSuperset);
                v.setTag(viewHolder);
            }
            ViewHolder holder = (ViewHolder) v.getTag();
            holder.textViewLightExerciseName.setText(rows.get(position).getExercise().getName());
            if (rows.get(position).isInSuperset()){
                holder.textViewLightSupersetNumber.setVisibility(View.VISIBLE);
                holder.textViewLightSupersetNumber.setText(String.valueOf(rows.get(position).getSupersetPosition()));
            } else {
                holder.textViewLightSupersetNumber.setVisibility(View.GONE);
                holder.imageViewSuperset.setVisibility(View.GONE);
            }
            v.setOnLongClickListener(new View.OnLongClickListener() {
                @Override
                public boolean onLongClick(View v) {
                    list.setChoiceMode(AbsListView.CHOICE_MODE_MULTIPLE);
                    startActionMode(NewCreatingTrainingDayActivity.this);
                    list.setItemChecked(position, true);
                    return false;
                }
            });
            v.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (isInActionMode){
                        list.setItemChecked(position, !list.isItemChecked(position));
                    }
                }
            });
            return v;
        }

        private class ViewHolder {
            TextViewLight textViewLightExerciseName;
            ImageView imageViewSuperset;
            TextViewLight textViewLightSupersetNumber;
        }
    }

    private class Row implements Serializable {

        private Exercise exercise;
        private boolean isInSuperset;
        private int supersetPosition;

        public Row (Exercise exercise){
            this.exercise = exercise;
            isInSuperset = false;
            supersetPosition = -1;
        }

        public Exercise getExercise() {
            return exercise;
        }

        public void setExercise(Exercise exercise) {
            this.exercise = exercise;
        }

        public boolean isInSuperset() {
            return isInSuperset;
        }

        public void setInSuperset(boolean isInSuperset) {
            this.isInSuperset = isInSuperset;
        }

        public int getSupersetPosition() {
            return supersetPosition;
        }

        public void setSupersetPosition(int supersetPosition) {
            this.supersetPosition = supersetPosition;
        }
    }
}
