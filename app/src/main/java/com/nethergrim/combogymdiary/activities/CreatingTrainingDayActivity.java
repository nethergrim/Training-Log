package com.nethergrim.combogymdiary.activities;

import android.content.Context;
import android.content.pm.ActivityInfo;
import android.content.res.Configuration;
import android.graphics.Color;
import android.os.Bundle;
import android.util.SparseBooleanArray;
import android.view.ActionMode;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.Toast;

import com.nethergrim.combogymdiary.model.DayOfWeek;
import com.nethergrim.combogymdiary.model.TrainingDay;
import com.nethergrim.combogymdiary.storage.DB;
import com.nethergrim.combogymdiary.R;
import com.nethergrim.combogymdiary.dialogs.DialogAddExercises;
import com.nethergrim.combogymdiary.dialogs.DialogInfo;
import com.nethergrim.combogymdiary.fragments.CreateTrainingDayFragment;
import com.nethergrim.combogymdiary.model.Exercise;
import com.nethergrim.combogymdiary.model.ExerciseTrainingObject;
import com.nethergrim.combogymdiary.tools.Prefs;
import com.nethergrim.combogymdiary.tools.SwipeDismissListViewTouchListener;
import com.nethergrim.combogymdiary.view.DraggableListView;
import com.nethergrim.combogymdiary.view.FAB;
import com.nethergrim.combogymdiary.view.TextViewLight;
import com.shamanland.fab.ShowHideOnScroll;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class CreatingTrainingDayActivity extends AnalyticsActivity implements DialogAddExercises.OnExerciseAddCallback, DraggableListView.OnListItemSwapListener, CreateTrainingDayFragment.OnFragmentInteractionListener {


    public static final String BUNDLE_ID_KEY = "com.nethergrim.combogymdiary.ID";
    private DraggableListView list;
    private TextViewLight textNoExe;
    private EditText etName;
    private DB db;
    private TrainingDayAdapter adapter;
    private View.OnTouchListener listener1;
    private View.OnTouchListener listener2;
    private View.OnTouchListener listener3;
    private SwipeDismissListViewTouchListener swipeListener;
    private boolean isInActionMode = false;
    private ActionMode.Callback supersetCallback = new ActionMode.Callback() {
        @Override
        public boolean onCreateActionMode(ActionMode mode, Menu menu) {
            isInActionMode = true;
            etName.setVisibility(View.GONE);
            getMenuInflater().inflate(R.menu.add_superset, menu);
            clearSelection();
            return true;
        }

        @Override
        public boolean onPrepareActionMode(ActionMode mode, Menu menu) {
            return false;
        }

        @Override
        public boolean onActionItemClicked(ActionMode mode, MenuItem item) {
            if (list.getCheckedItemCount() > 1) {
                SparseBooleanArray sparseBooleanArray = list.getCheckedItemPositions();
                int count = list.getCount();
                Random random = new Random();
                int position = list.getCheckedItemCount() - 1;
                int color = Color.argb(255, random.nextInt(256), random.nextInt(256), random.nextInt(256));
                int supsersetId = random.nextInt();
                for (int i = 0; i < 10; i++){                                                                   // REALLY RANDOM ID =)))
                    if (db.isAlreadyThisIdInSupersets(supsersetId) || adapter.alreadyHasThisId(supsersetId)){
                        supsersetId = random.nextInt();
                    } else {
                        break;
                    }
                }
                for (int i = count - 1; i >= 0; i--) {
                    if (sparseBooleanArray.get(i)) {
                        adapter.addSuperset(i, position--, supsersetId, color);
                    }
                }
            }
            clearSelection();
            mode.finish();
            return true;
        }

        @Override
        public void onDestroyActionMode(ActionMode mode) {
            clearSelection();
            etName.setVisibility(View.VISIBLE);
            isInActionMode = false;
            fabAdd.setVisibility(View.VISIBLE);
            fabSave.setVisibility(View.VISIBLE);
            fabSs.setVisibility(View.VISIBLE);
        }
    };
    private boolean editing = false;
    private int oldId;
    private FAB fabSave;
    private FAB fabSs;
    private FAB fabAdd;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if(getResources().getConfiguration().orientation == Configuration.ORIENTATION_PORTRAIT) {
            setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
        } else setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);
        setContentView(R.layout.activity_new_creating_training_day);
        if (getIntent().getIntExtra(BUNDLE_ID_KEY, -1) >= 0) {
            oldId = getIntent().getIntExtra(BUNDLE_ID_KEY, 0);
            editing = true;
        } else {
            oldId = (int) getIntent().getLongExtra(BUNDLE_ID_KEY, -1);
            if (oldId >= 0){
                editing = true;
            }
        }
        db = DB.get();
        etName = (EditText) findViewById(R.id.etTrainingName);
        if (!editing) {
            setTitle(R.string.creating_program);
        } else {
            etName.setText(db.fetchTrainingDay(oldId).getTrainingName());
            setTitle(R.string.editing_program);
        }
        getActionBar().setDisplayHomeAsUpEnabled(true);
        getActionBar().setDisplayShowHomeEnabled(false);
        initButtons();
        initList();

        setTypeFaceLight(etName);
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
    public void onExerciseAddedCallback(List<Long> idList) {
        final List<Exercise> newExercises = new ArrayList<Exercise>();
        if (idList != null && idList.size() > 0) {
            textNoExe.setVisibility(View.GONE);
            for (Long anIdList : idList) {
                newExercises.add(db.fetchtExercise(anIdList));
            }
            adapter.addNewData(newExercises);
            clearSelection();
        }
    }

    private void initList() {
        textNoExe = (TextViewLight) findViewById(R.id.text_add_exersices);
        list = (DraggableListView) findViewById(R.id.listView);
        list.setListener(this);
        list.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                if (isInActionMode) {
                    list.setItemChecked(position, !list.isItemChecked(position));
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
            }
        });
        adapter = new TrainingDayAdapter(this);
        list.setAdapter(adapter);
        list.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                if (list != null && list.getCount() > 0) {
                    swipeListener.onTouch(v, event);
                }
                if (list.getCount() > 5) {
                    listener1.onTouch(v, event);

                    listener2.onTouch(v, event);
                    listener3.onTouch(v, event);
                    return false;
                }
                return false;
            }
        });

        list.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(AdapterView<?> parent, View view, int position, long id) {
                list.startSwapping();
                return false;
            }
        });

        list.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                if (!isInActionMode) {
                    clearSelection();
                }
            }
        });

        swipeListener = new SwipeDismissListViewTouchListener(list, new SwipeDismissListViewTouchListener.DismissCallbacks() {
            @Override
            public boolean canDismiss(int position) {
                return true;
            }

            @Override
            public void onDismiss(ListView listView, int[] reverseSortedPositions) {
                for (int i = 0; i < reverseSortedPositions.length; i++) {
                    adapter.removeItem(reverseSortedPositions[0]);
                }
            }
        });

        if (editing) {
            loadTrainingsFromDbAndAdd();
        }
    }

    private void loadTrainingsFromDbAndAdd() {
        List<ExerciseTrainingObject> trainingObjects = db.fetchExerciseTrainingObjects(oldId);
        List<Row> rows = new ArrayList<Row>();
        for (ExerciseTrainingObject trainingObject : trainingObjects) {
            Row row = new Row(db.fetchtExercise(trainingObject.getExerciseId()));
            row.setSupersetPosition(trainingObject.getPositionAtSuperset());
            row.setSupersetColor(trainingObject.getSupersetColor());
            row.setInSuperset(trainingObject.getSuperset());
            row.setSupersetId(trainingObject.getSupersetId());
            rows.add(row);
        }
        adapter.addRows(rows);
    }

    private void initButtons() {
        fabSave = (FAB) findViewById(R.id.fabSave);
        fabSs = (FAB) findViewById(R.id.fabSs);
        fabAdd = (FAB) findViewById(R.id.btnAdd);

        listener1 = new ShowHideOnScroll(fabAdd);
        listener2 = new ShowHideOnScroll(fabSave);
        listener3 = new ShowHideOnScroll(fabSs);


        fabAdd.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!isInActionMode) {
                    DialogAddExercises dialogAddExercises = new DialogAddExercises();
                    dialogAddExercises.show(getFragmentManager(), DialogAddExercises.class.getName());
                    dialogAddExercises.setListener(CreatingTrainingDayActivity.this);
                }
            }
        });
        fabSave.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!isInActionMode) {
                    save();
                }
            }
        });
        fabSs.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!isInActionMode && list.getCount() > 1) {
                    CreatingTrainingDayActivity.this.startActionMode(supersetCallback);
                    fabAdd.setVisibility(View.GONE);
                    fabSave.setVisibility(View.GONE);
                    fabSs.setVisibility(View.GONE);
                }
            }
        });
    }

    private void save() {
        if (etName.getText().toString() == null || etName.getText().toString().equals("")) {
            Toast.makeText(this, R.string.enter_training_name, Toast.LENGTH_SHORT).show();
        } else if (list.getCount() == 0) {
            Toast.makeText(this, R.string.add_exercises_to_workout, Toast.LENGTH_SHORT).show();
        } else {
            if (editing) db.deleteTrainingDay((long) oldId, false);
            List<Row> rows = adapter.getRows();
            TrainingDay trainingDay = new TrainingDay();
            trainingDay.setTrainingName(etName.getText().toString());
            trainingDay.setDayOfWeek(DayOfWeek.MONDAY);
            long trainingId = DB.get().persistTrainingDay(trainingDay);
            for (int i = 0; i < rows.size(); i++) {
                ExerciseTrainingObject exerciseTrainingObject = new ExerciseTrainingObject();
                Row row = rows.get(i);

                exerciseTrainingObject.setTrainingProgramId(trainingId);
                exerciseTrainingObject.setExerciseId(row.getExercise().getId());
                exerciseTrainingObject.setPositionAtTraining(i);

                if (row.isInSuperset()) {
                    exerciseTrainingObject.setSuperset(true);
                    exerciseTrainingObject.setSupersetColor(row.getSupersetColor());
                    exerciseTrainingObject.setPositionAtSuperset(row.getSupersetPosition());
                    exerciseTrainingObject.setSupersetId(row.getSupersetId());
                } else {
                    exerciseTrainingObject.setSupersetId(0L);
                    exerciseTrainingObject.setSupersetColor(0);
                    exerciseTrainingObject.setSuperset(false);
                    exerciseTrainingObject.setPositionAtSuperset(0);
                }

                DB.get().persistExerciseTrainingObject(exerciseTrainingObject);

            }
            findViewById(R.id.content_first).setVisibility(View.GONE);
            getSupportFragmentManager().beginTransaction().replace(R.id.content, CreateTrainingDayFragment.newInstance(trainingId)).commit();
        }
    }

    private void showSuperSetDialog() {
        if (Prefs.get().getSuperSetInfoShowed() <= 3) {
            DialogInfo dialogInfo = new DialogInfo();
            Bundle args = new Bundle();
            args.putBoolean(DialogInfo.KEY_INFO_ABOUT_SUPERSET, true);
            dialogInfo.setArguments(args);
            dialogInfo.show(getFragmentManager(), DialogInfo.class.getName());
            Prefs.get().setSuperSetInfoShowed(Prefs.get().getSuperSetInfoShowed() + 1);
        }
    }

    private void clearSelection() {
        for (int i = 0; i < list.getCount(); i++) {
            list.setItemChecked(i, false);
        }
    }

    @Override
    public void onListItemSwapped(int i1, int i2) {
        clearSelection();
        adapter.swapItems(i1, i2);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_SENSOR);
    }

    @Override
    public void onTrainingDaySaved(Long id) {
        finish();
    }

    private class TrainingDayAdapter extends BaseAdapter {

        final int INVALID_ID = -1;
        private ArrayList<Row> rows = new ArrayList<Row>();
        private LayoutInflater inflater;

        public TrainingDayAdapter(Context context) {
            this.inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        }

        @Override
        public void notifyDataSetChanged() {
            super.notifyDataSetChanged();
            if (getCount() == 0) {
                CreatingTrainingDayActivity.this.textNoExe.setVisibility(View.VISIBLE);
            } else if (getCount() > 0) {
                CreatingTrainingDayActivity.this.textNoExe.setVisibility(View.GONE);
            }
        }

        public void addNewData(List<Exercise> newData) {
            for (Exercise aNewData : newData) {
                if (!containsExercise(aNewData.getId())) {
                    rows.add(new Row(aNewData));
                }
            }
            notifyDataSetChanged();
        }

        public boolean containsExercise(long id) {
            for (Row row : rows) {
                if (row.getExercise().getId() == id)
                    return true;
            }
            return false;
        }

        public void addRows(List<Row> rows) {
            this.rows.addAll(rows);
            notifyDataSetChanged();
        }

        public boolean alreadyHasThisId(int supersetId){
            for (int i = 0; i < rows.size(); i++){
                if (rows.get(i).getSupersetId() == supersetId){
                    return true;
                }
            }
            return false;
        }

        public void swapItems(int i1, int i2) {
            Row tmp = rows.get(i1);
            rows.set(i1, rows.get(i2));
            rows.set(i2, tmp);
            notifyDataSetChanged();
        }

        public List<Row> getRows() {
            return this.rows;
        }

        public void addSuperset(int position, int positionInsuperset, long supersetId, int supersetColor) {
            rows.get(position).setInSuperset(true);
            rows.get(position).setSupersetPosition(positionInsuperset);
            rows.get(position).setSupersetId(supersetId);
            rows.get(position).setSupersetColor(supersetColor);
            this.notifyDataSetChanged();
        }

        public void removeAllSupersets(long id) {
            for (Row row : rows) {
                if (row.getSupersetId() == id){
                    row.setInSuperset(false);
                    row.setSupersetPosition(0);
                    row.setSupersetId(0L);
                    row.setSupersetColor(0);
                }
            }
        }

        public void removeItem(int position) {
            try {
                if (rows.get(position).isInSuperset()) removeAllSupersets(rows.get(position).getSupersetId());
                rows.remove(position);
                this.notifyDataSetChanged();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        @Override
        public int getCount() {
            return rows.size();
        }

        @Override
        public String getItem(int position) {
            return rows.get(position).getExercise().getName();
        }

        @Override
        public long getItemId(int position) {
            if (position < 0 || position >= rows.size()) {
                return INVALID_ID;
            }
            return rows.get(position).getExercise().getId();
        }

        @Override
        public View getView(final int position, View convertView, ViewGroup parent) {
            View view = convertView;
            if (view == null) {
                view = inflater.inflate(R.layout.list_item_creating_programm, parent, false);
                ViewHolder viewHolder = new ViewHolder();
                viewHolder.textViewLightExerciseName = (TextViewLight) view.findViewById(R.id.text_exercise);
                viewHolder.textViewLightSupersetNumber = (TextViewLight) view.findViewById(R.id.text_number_of_superset);
                viewHolder.color = view.findViewById(R.id.color_superset);
                viewHolder.imageViewSuperset = (ImageView) view.findViewById(R.id.imageSuperset);
                view.setTag(viewHolder);
            }
            ViewHolder holder = (ViewHolder) view.getTag();
            holder.textViewLightExerciseName.setText(rows.get(position).getExercise().getName());
            if (rows.get(position).isInSuperset()) {
                holder.color.setVisibility(View.VISIBLE);
                holder.color.setBackgroundColor(rows.get(position).getSupersetColor());
                holder.textViewLightSupersetNumber.setVisibility(View.VISIBLE);
                holder.textViewLightSupersetNumber.setText(String.valueOf(rows.get(position).getSupersetPosition() + 1));
                holder.imageViewSuperset.setVisibility(View.VISIBLE);
            } else {
                holder.color.setVisibility(View.GONE);
                holder.textViewLightSupersetNumber.setVisibility(View.GONE);
                holder.imageViewSuperset.setVisibility(View.GONE);
            }
            return view;
        }

        private class ViewHolder {
            TextViewLight textViewLightExerciseName;
            ImageView imageViewSuperset;
            View color;
            TextViewLight textViewLightSupersetNumber;
        }
    }

    private class Row implements Serializable {

        private Exercise exercise;
        private boolean isInSuperset;
        private int supersetPosition;
        private long supersetId;
        private int supersetColor;

        public Row(Exercise exercise) {
            this.exercise = exercise;
            isInSuperset = false;
            supersetPosition = -1;
        }

        public long getSupersetId() {
            return supersetId;
        }

        public void setSupersetId(Long supersetId) {
            this.supersetId = supersetId;
        }

        public int getSupersetColor() {
            return supersetColor;
        }

        public void setSupersetColor(int supersetColor) {
            this.supersetColor = supersetColor;
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
