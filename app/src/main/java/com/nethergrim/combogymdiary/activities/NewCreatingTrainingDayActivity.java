package com.nethergrim.combogymdiary.activities;

import android.content.Context;
import android.os.Bundle;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.BaseAdapter;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import com.nethergrim.combogymdiary.DB;
import com.nethergrim.combogymdiary.R;
import com.nethergrim.combogymdiary.dialogs.DialogAddExercises;
import com.nethergrim.combogymdiary.dialogs.DialogInfo;
import com.nethergrim.combogymdiary.model.Exercise;
import com.nethergrim.combogymdiary.model.ExerciseGroup;
import com.nethergrim.combogymdiary.tools.Prefs;
import com.nethergrim.combogymdiary.view.DynamicListView;
import com.nethergrim.combogymdiary.view.FloatingActionButton;
import com.nethergrim.combogymdiary.view.TextViewLight;

import java.util.ArrayList;
import java.util.List;

public class NewCreatingTrainingDayActivity extends AnalyticsActivity implements DialogAddExercises.OnExerciseAddCallback {

    private ListView list;
    private TextViewLight textNoExe;
    private EditText etName;
    private FloatingActionButton fabAdd, fabSave, fabSuperSet;
    private DB db;
    private TrainingDayAdapter adapter;
    private boolean btnsHiding = false;
    private int lastPos = 0;

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
        list.setOnScrollListener(new AbsListView.OnScrollListener() {
            @Override
            public void onScrollStateChanged(AbsListView view, int scrollState) {
                switch (scrollState){
                    case SCROLL_STATE_FLING:
                        break;
                    case SCROLL_STATE_IDLE:
                        break;
                    case SCROLL_STATE_TOUCH_SCROLL:
                        if (list.getCount() - 1 == list.getLastVisiblePosition() && list.getCount() > 0){
                            hideButtons(200, 3000);
                        }
                        break;
                }
            }

            @Override
            public void onScroll(AbsListView view, int firstVisibleItem, int visibleItemCount, int totalItemCount) {
                if (firstVisibleItem < lastPos){
                    fabSuperSet.show();
                    fabSave.show();
                    fabAdd.show();
                } else {
                    hideButtons(50, 5000);
                }
                lastPos = firstVisibleItem;
            }
        });
    }

    private void hideButtons(final int offset, final int time){
        if (!btnsHiding && list != null && list.getCount() > 4){
            Thread thread = new Thread(new Runnable() {
                @Override
                public void run() {
                    try {
                        btnsHiding = true;
                        Thread.sleep(offset);
                        NewCreatingTrainingDayActivity.this.runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                fabSuperSet.hide();
                                fabSave.hide();
                                fabAdd.hide();
                            }
                        });
                        Thread.sleep(time);
                        NewCreatingTrainingDayActivity.this.runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                fabAdd.show();
                                fabSave.show();
                                fabSuperSet.show();
                                btnsHiding = false;
                            }
                        });
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }
            });
            thread.start();
        }
    }

    private void initButtons() {
        fabAdd = new FloatingActionButton.Builder(this)
                .withDrawable(getResources().getDrawable(R.drawable.ic_plus_small))
                .withButtonColor(getResources().getColor(R.color.material_cyan_a400))
                .withGravity(Gravity.BOTTOM | Gravity.LEFT)
                .withMargins(16, 0, 0, 16)
                .create();

        fabSave = new FloatingActionButton.Builder(this)
                .withDrawable(getResources().getDrawable(R.drawable.ic_action_save))
                .withButtonColor(getResources().getColor(R.color.material_cyan_a400))
                .withGravity(Gravity.BOTTOM | Gravity.CENTER_HORIZONTAL)
                .withMargins(0, 0, 0, 16)
                .create();

        fabSuperSet = new FloatingActionButton.Builder(this)
                .withDrawable(getResources().getDrawable(R.drawable.ic_action_ss))
                .withButtonColor(getResources().getColor(R.color.material_cyan_a400))
                .withGravity(Gravity.BOTTOM | Gravity.RIGHT)
                .withMargins(0, 0, 16, 16)
                .create();

        fabAdd.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!fabAdd.isHidden()){
                    DialogAddExercises dialogAddExercises = new DialogAddExercises();
                    dialogAddExercises.show(getFragmentManager(), DialogAddExercises.class.getName());
                }
            }
        });

        fabSave.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!fabSave.isHidden()){

                }
                // TODO save
            }
        });

        fabSuperSet.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!fabSuperSet.isHidden()){

                }
                // TODO create superset
            }
        });
    }

    @Override
    public void onStart() {
        super.onStart();
        showSuperSetDialog();
    }

    @Override
    protected void onResume() {
        super.onResume();
        fabSuperSet.show();
        fabSave.show();
        fabAdd.show();
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
    public void onStop() {
        super.onStop();
        fabAdd.hide();
        fabSave.hide();
        fabSuperSet.hide();
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
            Thread thread = new Thread(new Runnable() {
                @Override
                public void run() {
                    try {
                        Thread.sleep(1500);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                    btnsHiding = false;
                }
            });
            thread.start();
            btnsHiding = true;
            adapter.addData(newExercises);

        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        db.close();
    }

    private class TrainingDayAdapter extends BaseAdapter{

        private List<Exercise> data = new ArrayList<Exercise>();
        private List<Boolean> supersetBoolean;
        private List<Integer> supersetPosition;
        private LayoutInflater inflater;

        public TrainingDayAdapter(Context context){
            supersetBoolean = new ArrayList<Boolean>();
            supersetPosition = new ArrayList<Integer>();
            this.inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        }

        public void addData(List<Exercise> newData){
            this.data.addAll(newData);
            notifyDataSetChanged();
        }

        @Override
        public int getCount() {
            return data.size();
        }

        @Override
        public Exercise getItem(int position) {
            return data.get(position);
        }

        @Override
        public long getItemId(int position) {
            return data.get(position).getId();
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            View v = convertView;
            if (v == null){
                v = inflater.inflate(R.layout.list_item_creating_programm, parent, false);
                ViewHolder viewHolder = new ViewHolder();
                viewHolder.textViewLightExerciseName = (TextViewLight) v.findViewById(R.id.text_exercise);
                viewHolder.textViewLightSupersetNumber = (TextViewLight) v.findViewById(R.id.text_number_of_superset);
                viewHolder.btnUp = (ImageButton)v.findViewById(R.id.btnUp);
                viewHolder.btnDown = (ImageButton)v.findViewById(R.id.btnDown);
                viewHolder.imageViewSuperset = (ImageView)v.findViewById(R.id.imageSuperset);
                v.setTag(viewHolder);
            }

            ViewHolder holder = (ViewHolder) v.getTag();

            holder.textViewLightExerciseName.setText(data.get(position).getName());
            holder.btnUp.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                     // TODO SWAP UP
                }
            });
            holder.btnDown.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    // TODO SWAP DOWN
                }
            });

            holder.textViewLightSupersetNumber.setVisibility(View.GONE);
            holder.imageViewSuperset.setVisibility(View.GONE);
            // TODO SUPERSET



            return v;
        }

        private class ViewHolder {
            TextViewLight textViewLightExerciseName;
            ImageButton btnUp;
            ImageButton btnDown;
            ImageView imageViewSuperset;
            TextViewLight textViewLightSupersetNumber;
        }
    }
}
