package com.nethergrim.combogymdiary.fragments;

import android.app.ActionBar;
import android.app.DialogFragment;
import android.app.Fragment;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.res.Resources;
import android.database.Cursor;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.os.Vibrator;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.SparseBooleanArray;
import android.view.ActionMode;
import android.view.Display;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.CompoundButton;
import android.widget.CompoundButton.OnCheckedChangeListener;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.PopupWindow;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.ToggleButton;

import com.nethergrim.combogymdiary.DB;
import com.nethergrim.combogymdiary.R;
import com.nethergrim.combogymdiary.activities.EditingProgramAtTrainingActivity;
import com.nethergrim.combogymdiary.activities.HistoryDetailedActivity;
import com.nethergrim.combogymdiary.dialogs.DialogAddCommentToTraining;
import com.nethergrim.combogymdiary.dialogs.DialogExitFromTraining;
import com.nethergrim.combogymdiary.model.ExerciseTrainingObject;
import com.nethergrim.combogymdiary.model.TrainingRow;
import com.nethergrim.combogymdiary.service.TrainingService;
import com.nethergrim.combogymdiary.tools.Prefs;
import com.nethergrim.combogymdiary.view.DraggableListView;
import com.nethergrim.combogymdiary.view.FAB;
import com.nethergrim.combogymdiary.view.TextViewLight;
import com.yandex.metrica.Counter;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import kankan.wheel.widget.WheelView;
import kankan.wheel.widget.adapters.AbstractWheelTextAdapter;

public class TrainingFragment extends Fragment implements
        OnCheckedChangeListener, OnClickListener, DraggableListView.OnListItemSwapListener {

    public static final String BUNDLE_KEY_TRAINING_ID = "com.nethergrim.combogymdiary.TRAINING_ID";
    private ActionBar actionBar;
    private Boolean tglChecked = true, vibrate = false;
    private EditText etTimer;
    private DB db;
    private String trainingName = "", currentExerciseName = "", date = "";
    private int currentCheckedPosition = 0, set = 0, currentSet = 0, oldReps = 0,
            oldWeight = 0, timerValue = 0, vibrateLenght = 0, currentId = 0;
    private long startTime = 0;
    private Runnable timerRunnable = new Runnable() {
        @Override
        public void run() {
            long millis = System.currentTimeMillis() - startTime;
            int seconds = (int) (millis / 1000);
            int minutes = (seconds / 60);
            seconds = (seconds % 60);
            actionBar.setSubtitle((String.format("%d:%02d", minutes, seconds)) + " "
                    + " " + " ["
                    + ((set == currentSet ? set : currentSet) + 1) + " "
                    + getResources().getString(R.string.set) + "] ");
            timerHandler.postDelayed(this, 500);
        }
    };
    private Handler handler;
    private WheelView repsWheel, weightWheel;
    private TextView tvInfoText;
    private boolean btnBlocked = false;
    private PopupWindow popupWindow;
    private Handler timerHandler = new Handler();
    private LinearLayout llBottom;
    private Animation anim = null;
    private boolean isTrainingAtProgress = false, toPlaySound = false;
    private ProgressDialog pd;
    private boolean isActiveDialog = false, blocked = false, blockedSelection = false;
    private DraggableListView listView;
    private TrainingAdapter adapter;
    private FAB fabSave, fabBack, fabForward;
    private boolean isInActionMode = false;
    private ActionMode.Callback mActionModeCallback = new ActionMode.Callback() {

        @Override
        public boolean onCreateActionMode(ActionMode mode, Menu menu) {
            isInActionMode = true;
            MenuInflater inflater = mode.getMenuInflater();
            inflater.inflate(R.menu.cab_training, menu);
            fabSave.setVisibility(View.GONE);
            fabBack.setVisibility(View.GONE);
            fabForward.setVisibility(View.GONE);
            return true;
        }

        @Override
        public boolean onPrepareActionMode(ActionMode mode, Menu menu) {
            blockedSelection = true;
            listView.setChoiceMode(ListView.CHOICE_MODE_MULTIPLE);
            for (int i = 0; i < listView.getCount(); ++i) {
                listView.setItemChecked(i, false);
            }
            llBottom.setVisibility(View.GONE);
            return false;
        }

        @Override
        public boolean onActionItemClicked(ActionMode mode, MenuItem item) {
            if (item.getItemId() == R.id.cab_delete) {
                SparseBooleanArray deletingPositions = listView.getCheckedItemPositions();
                for (int i = deletingPositions.size() - 1; i >= 0; --i) {
                    if (deletingPositions.get(i)) {
                        adapter.deleteItem(i);
                    }
                }
                for (int i = 0; i < listView.getCount(); ++i) {
                    listView.setItemChecked(i, false);
                }
                blockedSelection = false;
                listView.setChoiceMode(ListView.CHOICE_MODE_SINGLE);

                if (listView.getCount() > 0) {
                    onSelected(0);
                    listView.setItemChecked(0, true);
                    llBottom.setVisibility(View.VISIBLE);
                }

                mode.finish();
                return true;
            }
            return false;
        }

        @Override
        public void onDestroyActionMode(ActionMode mode) {
            for (int i = 0; i < listView.getCount(); ++i) {
                listView.setItemChecked(i, false);
            }
            blockedSelection = false;
            listView.setChoiceMode(ListView.CHOICE_MODE_SINGLE);
            if (listView.getCount() > 0) {
                onSelected(0);
                listView.setItemChecked(0, true);
                llBottom.setVisibility(View.VISIBLE);
            }
            fabSave.setVisibility(View.VISIBLE);
            initSetButtons();
            isInActionMode = false;
        }
    };
    private int trainingId;

    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);
        setRetainInstance(true);
        db = new DB(getActivity());
        db.open();
        trainingId = getArguments().getInt(BUNDLE_KEY_TRAINING_ID);
        adapter = new TrainingAdapter(getActivity());
        adapter.addData(db.getTrainingRows(trainingId));
        isTrainingAtProgress = Prefs.get().getTrainingAtProgress();
        Prefs.get().setTrainingAtProgress(true);
        Prefs.get().setCurrentTrainingId(trainingId);
        trainingName = db.getTrainingName(trainingId);
        actionBar = getActivity().getActionBar();
        actionBar.setTitle(trainingName);
        getActivity().startService(new Intent(getActivity(), TrainingService.class));
        startTime = System.currentTimeMillis();
        Prefs.get().setStartTime(startTime);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.training_at_progress_new_wheel_new_list, null);
        fabBack = (FAB) v.findViewById(R.id.fabBack);
        fabBack.setOnClickListener(this);
        fabSave = (FAB) v.findViewById(R.id.fabSaveSet);
        fabSave.setOnClickListener(this);
        fabForward = (FAB) v.findViewById(R.id.fabForward);
        fabForward.setOnClickListener(this);
        llBottom = (LinearLayout) v.findViewById(R.id.LLBottom);
        anim = AnimationUtils.loadAnimation(getActivity(), R.anim.setfortraining);
        repsWheel = (WheelView) v.findViewById(R.id.wheelReps);
        repsWheel.setVisibleItems(7);
        repsWheel.setWheelBackground(R.drawable.card);
        repsWheel.setWheelForeground(R.drawable.wheel_foreground);
        repsWheel.setShadowColor(0xFFFFFF, 0xFFFFFF, 0xFFFFFF);
        repsWheel.setViewAdapter(new RepsAdapter(getActivity()));
        weightWheel = (WheelView) v.findViewById(R.id.wheelWeight);
        weightWheel.setVisibleItems(7);
        weightWheel.setWheelBackground(R.drawable.card);
        weightWheel.setWheelForeground(R.drawable.wheel_foreground);
        weightWheel.setShadowColor(0xFFFFFF, 0xFFFFFF, 0xFFFFFF);
        weightWheel.setViewAdapter(new WeightsAdapter(getActivity()));
        ToggleButton tglTimerOn = (ToggleButton) v.findViewById(R.id.tglTurnOff);
        tglTimerOn.setOnCheckedChangeListener(this);
        etTimer = (EditText) v.findViewById(R.id.etTimerValueAtTraining);
        etTimer.setOnClickListener(this);
        etTimer.addTextChangedListener(new TextWatcher() {
            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
            }

            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void afterTextChanged(Editable s) {
                updateTimer(s.toString());
            }
        });
        tvInfoText = (TextView) v.findViewById(R.id.infoText);
        listView = (DraggableListView) v.findViewById(R.id.listViewExerciseList);
        listView.setListener(this);
        listView.setAdapter(adapter);
        listView.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(AdapterView<?> parent, View view, int position, long id) {
                if (!isInActionMode) {
                    listView.startSwapping();
                    return true;
                }
                return false;
            }
        });
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View itemClicked, int position, long id) {
                onSelected(position);
            }
        });
        listView.setItemChecked(Prefs.get().getCheckedPosition(), true);
        onSelected(Prefs.get().getCheckedPosition());
        SimpleDateFormat sdf = new SimpleDateFormat("dd.MM.yyyy");
        date = sdf.format(new Date(System.currentTimeMillis()));
        boolean isTimerOn = Prefs.get().getTimerOn();
        if (isTimerOn) {
            tglTimerOn.setChecked(true);
            tglChecked = true;
            etTimer.setEnabled(true);
        } else {
            tglTimerOn.setChecked(false);
            tglChecked = false;
            etTimer.setEnabled(false);
        }
        return v;
    }

    private void initSetButtons() {
        try {
            if (set > 0 && currentSet > 0) {
                fabBack.setVisibility(View.VISIBLE);
            } else {
                fabBack.setVisibility(View.GONE);
            }
            if (currentSet < set) {
                fabForward.setVisibility(View.VISIBLE);
            } else {
                fabForward.setVisibility(View.GONE);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void onSelected(int position) {
        if (blockedSelection)
            return;
        if (adapter.getData().size() == 0) {
            llBottom.setVisibility(View.GONE);
            Toast.makeText(getActivity(), R.string.please_add_an_exe, Toast.LENGTH_LONG).show();
            return;
        } else {
            llBottom.setVisibility(View.VISIBLE);
        }
        Prefs.get().setCheckedPosition(position);
        currentCheckedPosition = position;
        currentExerciseName = adapter.getData().get(position).getExerciseName();
        set = adapter.getData().get(position).getSetsCount();
        try {
            timerValue = Integer.parseInt(db
                    .getTimerValueByExerciseName(currentExerciseName));
        } catch (NumberFormatException e) {
            Toast.makeText(getActivity(), R.string.parsing_error, Toast.LENGTH_LONG).show();
            timerValue = 60;
            Counter.sharedInstance().reportError("", e);
        }
        currentSet = set;
        etTimer.setText(String.valueOf(timerValue));
        initSetButtons();
        oldReps = db.getLastWeightOrReps(currentExerciseName, set, false);
        oldWeight = db.getLastWeightOrReps(currentExerciseName, set, true);
        if (oldReps > 0 && oldWeight > 0) {
            tvInfoText.setText(getResources().getString(R.string.previous_result_was) + " " + oldWeight + "x" + oldReps);
            weightWheel.setCurrentItem(oldWeight - 1);
            repsWheel.setCurrentItem(oldReps - 1);
        } else {
            tvInfoText.setText(getResources().getString(R.string.new_set) + " (" + (set + 1) + ")");
        }
        blocked = false;
    }

    @Override
    public void onResume() {
        super.onResume();
        actionBar.setTitle(trainingName);
        listView.setKeepScreenOn(!Prefs.get().getTurnScreenOff());
        vibrate = Prefs.get().getVibrateOn();
        String vl = Prefs.get().getVibrateLenght();
        try {
            vibrateLenght = Integer.parseInt(vl) * 1000;
        } catch (Exception e) {
            vibrateLenght = 2000;
        }
        toPlaySound = Prefs.get().getNotifyWithSound();
        if (isTrainingAtProgress) {
            startTime = Prefs.get().getStartTime();
            restoreSetsFromPreferences();
            try {
                listView.setItemChecked(Prefs.get().getCheckedPosition(), true);
                onSelected(Prefs.get().getCheckedPosition());
            } catch (Exception e) {
                listView.setItemChecked(0, true);
                onSelected(0);
            }
        }
        timerHandler.postDelayed(timerRunnable, 0);
        fabSave.setVisibility(View.VISIBLE);
        initSetButtons();
        handler = new Handler() {
            public void handleMessage(Message msg) {
                if (msg.what == 0) {
                    this.removeMessages(0);
                } else {
                    pd.setIndeterminate(false);
                    if (pd.getProgress() < pd.getMax()) {
                        pd.incrementProgressBy(1);
                        handler.sendEmptyMessageDelayed(1, 1000);
                    } else {
                        if (toPlaySound) {
                            String sound = Prefs.get().getRingtone();
                            if (sound == null) {
                                playSound(getActivity(), RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION)
                                );
                            } else {
                                Uri uri = Uri.parse(sound);
                                playSound(getActivity(), uri);
                            }
                        }
                        if (vibrate) {
                            try {
                                Vibrator v = (Vibrator) getActivity()
                                        .getSystemService(
                                                Context.VIBRATOR_SERVICE);
                                v.vibrate(vibrateLenght);
                            } catch (Exception e) {
                                Counter.sharedInstance().reportError("error vibrating", e);
                            }
                        }
                        pd.dismiss();
                    }
                }
            }
        };
    }

    private void showProgressDialog() {
        pd = new ProgressDialog(getActivity());
        pd.setTitle(R.string.resting);
        pd.setProgressStyle(ProgressDialog.STYLE_HORIZONTAL);
        pd.setMax(timerValue);
        pd.setCancelable(false);
        pd.setButton(DialogInterface.BUTTON_NEGATIVE,
                getResources().getString(R.string.cancel),
                new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                        handler.removeCallbacksAndMessages(null);
                    }
                }
        );
        pd.show();
        isActiveDialog = true;
        handler.sendEmptyMessageDelayed(1, 100);
    }

    @Override
    public void onPause() {
        super.onPause();
        timerHandler.removeCallbacks(timerRunnable);
        saveSetsToPreferences();
        isTrainingAtProgress = true;
        saveExercicesToDatabase();
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        menu.clear();
        inflater.inflate(R.menu.main, menu);
        super.onCreateOptionsMenu(menu, inflater);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int itemId = item.getItemId();
        if (itemId == R.id.itemExit) {
            if (popupWindow != null && popupWindow.isShowing()) {
                return false;
            }
            DialogFragment dlg1 = new DialogExitFromTraining();
            dlg1.setCancelable(false);
            if (!dlg1.isAdded())
                dlg1.show(getFragmentManager(), "dlg1");
        } else if (itemId == R.id.itemEditTrainings) {
            Intent intent = new Intent(getActivity(),
                    EditingProgramAtTrainingActivity.class);
            intent.putExtra("trName", trainingName);
            intent.putExtra("ifAddingExe", true);
            startActivityForResult(intent, 1);
        } else if (itemId == R.id.itemSeePreviousTraining) {
            String[] args = {trainingName};
            Cursor tmpCursor = db.getDataMain(null, DB.TRAINING_NAME + "=?", args,
                    DB.DATE, null, null);
            if (tmpCursor.moveToLast()
                    && (tmpCursor.getCount() > 1 || !tmpCursor.getString(3)
                    .equals(date))) {

                if (tmpCursor.getString(3).equals(date)) {
                    tmpCursor.moveToPrevious();
                }

                Intent intent_history_detailed = new Intent(getActivity(),
                        HistoryDetailedActivity.class);
                intent_history_detailed
                        .putExtra("date", tmpCursor.getString(3));
                intent_history_detailed.putExtra("trName",
                        tmpCursor.getString(1));
                startActivity(intent_history_detailed);
                tmpCursor.close();

            } else {
                Toast.makeText(
                        getActivity(),
                        getResources().getString(R.string.no_history) + trainingName,
                        Toast.LENGTH_SHORT).show();
            }
        } else if (itemId == R.id.itemAddCommentToTraining) {

            DialogAddCommentToTraining dialog = new DialogAddCommentToTraining();
            dialog.show(getFragmentManager(), "");
        } else if (itemId == R.id.itemDeleteExercise) {
            getActivity().startActionMode(mActionModeCallback);
        }
        return false;
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) { // FIXME add data to adapter
        if (data == null) {
            return;
        }


//        long[] itemsChecked = data
//                .getLongArrayExtra("return_array_of_exersices");
//        for (long anItemsChecked : itemsChecked) {
//            exerciseList.add(db.getExerciseByID((int) anItemsChecked));
//            setsList.add(0);
//        }
//        for (int j = 0; j < 100; j++) {
//            setsList.add(0);
//        }
//
//        String[] tmp = new String[exerciseList.size()];
//
//        for (int i = 0; i < exerciseList.size(); i++) {
//            tmp[i] = exerciseList.get(i);
//        }
//        db.updateRec_Training(trainingId, 2, db.convertArrayToString(tmp));
//        updateAdapter();
    }

    private void updateTimer(String tmp) {
        String timerv;
        if (tmp != null) {
            timerv = tmp;
        } else {
            timerv = etTimer.getText().toString();
        }
        String tmpStr = db.getTimerValueByExerciseName(currentExerciseName);

        if (tmpStr != null && timerv != null && !tmpStr.equals(timerv)) {
            int exe_id = db.getExeIdByName(currentExerciseName);
            db.updateExercise(exe_id, DB.TIMER_VALUE, timerv);
        }
        try {
            timerValue = Integer.parseInt(timerv);
        } catch (Exception e) {
            timerValue = 60;
        }

    }

    @Override
    public void onClick(View arg0) {
        int id = arg0.getId();
        if (blocked) {
            Toast.makeText(getActivity(),
                    getResources().getString(R.string.select_an_exercise),
                    Toast.LENGTH_LONG).show();
            return;
        }
        if (id == R.id.fabSaveSet && currentSet == set && !btnBlocked) {
            int wei = (weightWheel.getCurrentItem() + 1);
            int rep_s = (repsWheel.getCurrentItem() + 1);
            adapter.getData().get(currentCheckedPosition).incrementSetsCount();
            set = adapter.getData().get(currentCheckedPosition).getSetsCount();
            db.addRecMainTable(trainingName, currentExerciseName, date, wei, rep_s, set);
            currentSet = set;
            initSetButtons();
            try {
                Toast.makeText(getActivity(), R.string.saved, Toast.LENGTH_SHORT).show();
            } catch (Resources.NotFoundException e) {
                e.printStackTrace();
            }
            if (isActiveDialog) {
                handler.sendEmptyMessage(0);
            }
            showPopup();
            oldReps = db.getLastWeightOrReps(currentExerciseName, set, false);
            oldWeight = db.getLastWeightOrReps(currentExerciseName, set, true);
            if (oldReps > 0 && oldWeight > 0) {
                tvInfoText.setText(getResources().getString(
                        R.string.previous_result_was)
                        + " " + oldWeight + "x" + oldReps);
                weightWheel.setCurrentItem(oldWeight - 1);
                repsWheel.setCurrentItem(oldReps - 1);
            } else {
                tvInfoText.setText(getResources().getString(R.string.new_set)
                        + " (" + (set + 1) + ")");
            }
        } else if (id == R.id.fabSaveSet && currentSet < set) {
            int wei = (weightWheel.getCurrentItem() + 1);
            int rep_s = (repsWheel.getCurrentItem() + 1);
            db.updateRec_Main(currentId, 4, null, wei);
            db.updateRec_Main(currentId, 5, null, rep_s);
            Toast.makeText(getActivity(), R.string.resaved, Toast.LENGTH_SHORT)
                    .show();
            currentSet = set;
            onSelected(currentCheckedPosition);
        } else if (id == R.id.fabBack) {
            if (currentSet > 0) {
                llBottom.startAnimation(anim);
                currentSet--;
                int weitghsS = db.getThisWeight(currentSet + 1, currentExerciseName) - 1;
                int repsS = db.getThisReps(currentSet + 1, currentExerciseName) - 1;
                currentId = db.getThisId(currentSet + 1, currentExerciseName);
                weightWheel.setCurrentItem(weitghsS);
                repsWheel.setCurrentItem(repsS);
                tvInfoText.setText(getResources().getString(
                        R.string.resaved_text)
                        + " "
                        + (weitghsS + 1)
                        + "x"
                        + (repsS + 1)
                        + " ("
                        + (currentSet + 1) + ")");
            }

        } else if (id == R.id.fabForward) {
            if (currentSet < set - 1) {
                llBottom.startAnimation(anim);
                currentSet++;
                int weitghsS = db.getThisWeight(currentSet + 1, currentExerciseName) - 1;
                int repsS = db.getThisReps(currentSet + 1, currentExerciseName) - 1;
                weightWheel.setCurrentItem(weitghsS);
                repsWheel.setCurrentItem(repsS);
                tvInfoText.setText(getResources().getString(
                        R.string.resaved_text)
                        + " "
                        + (weitghsS + 1)
                        + "x"
                        + (repsS + 1)
                        + " ("
                        + (currentSet + 1) + ")");
            } else if (currentSet == set - 1) {
                llBottom.startAnimation(anim);
                onSelected(currentCheckedPosition);
            }
        }
        initSetButtons();
    }

    private void showPopup() {
        if (oldReps > 0 && oldWeight > 0) {
            int wei = (weightWheel.getCurrentItem() + 1);
            int rep_s = (repsWheel.getCurrentItem() + 1);

            int weightDelta = wei - oldWeight;
            int repsDelta = rep_s - oldReps;

            LayoutInflater inflater = (LayoutInflater) getActivity().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            View v = inflater.inflate(R.layout.popup_training_layout, null);


            TextView textViewWeightDelta = (TextView) v.findViewById(R.id.text_weight_delta);
            TextView text1 = (TextView) v.findViewById(R.id.text_1);
            TextView textViewRepsDelta = (TextView) v.findViewById(R.id.text_reps_delta);
            TextView text2 = (TextView) v.findViewById(R.id.text2);

            int green = getActivity().getResources().getColor(R.color.material_green_a400);
            int red = getActivity().getResources().getColor(R.color.material_pink_a400);

            if (weightDelta >= 0) {
                textViewWeightDelta.setTextColor(green);
                text1.setTextColor(green);
                textViewWeightDelta.setText("+" + String.valueOf(weightDelta));
            } else {
                textViewWeightDelta.setTextColor(red);
                text1.setTextColor(red);
                textViewWeightDelta.setText(String.valueOf(weightDelta));
            }

            if (repsDelta >= 0) {
                textViewRepsDelta.setTextColor(green);
                text2.setTextColor(green);
                textViewRepsDelta.setText("+" + String.valueOf(weightDelta));
            } else {
                textViewRepsDelta.setText(String.valueOf(repsDelta));
                textViewRepsDelta.setTextColor(red);
                text2.setTextColor(red);
            }

            WindowManager wm = (WindowManager) getActivity().getSystemService(Context.WINDOW_SERVICE);
            Display display = wm.getDefaultDisplay();
            int widthDp = dpFromPx(display.getWidth());

            popupWindow = new PopupWindow(v, pxFromDp(widthDp - 24), pxFromDp(120));
            popupWindow.setAnimationStyle(R.style.PopupAnimation);
            popupWindow.showAsDropDown(listView, pxFromDp(8), pxFromDp(20));
            btnBlocked = true;

            Thread thread = new Thread(new Runnable() {
                @Override
                public void run() {
                    try {
                        Thread.sleep(2500);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                    getActivity().runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            hidePopup();
                        }
                    });

                    try {
                        Thread.sleep(100);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }

                    getActivity().runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            if (tglChecked) {
                                Prefs.get().setProgress(0);
                                showProgressDialog();
                            }
                        }
                    });
                }
            });
            thread.start();
        } else {
            if (tglChecked) {
                Prefs.get().setProgress(0);
                showProgressDialog();
            }
        }


    }

    private void hidePopup() {
        btnBlocked = false;
        if (popupWindow != null && popupWindow.isShowing()) {
            popupWindow.dismiss();
        }
    }

    @Override
    public void onCheckedChanged(CompoundButton tglTimerOn, boolean isChecked) {
        if (isChecked) {
            Prefs.get().setTimerOn(true);
            tglChecked = true;
            etTimer.setEnabled(true);
        } else {
            Prefs.get().setTimerOn(false);
            tglChecked = false;
            etTimer.setEnabled(false);
        }
    }

    public void saveSetsToPreferences() {// FIXME make at background

        List<TrainingRow> currentData = adapter.getData();
        JSONArray jsonArray = new JSONArray();
        for (TrainingRow aCurrentData : currentData) {
            JSONObject jsonObject = new JSONObject();
            try {
                jsonObject.put("setCount", aCurrentData.getSetsCount());
                jsonArray.put(jsonObject);
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
        Prefs.get().saveSets(jsonArray.toString());
    }

    public void restoreSetsFromPreferences() {// FIXME make at background
        try {
            JSONArray jsonArray = new JSONArray(Prefs.get().getSavedSets());
            for (int i = 0; i < jsonArray.length(); i++) {
                JSONObject tmp = (JSONObject) jsonArray.get(i);
                adapter.getData().get(i).setSetsCount(tmp.getInt("setCount"));
            }
            adapter.notifyDataSetChanged();
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    private void saveExercicesToDatabase() { // FIXME make at background
        List<TrainingRow> currentData = adapter.getData();
        db.deleteTrainingProgram(trainingId, true);
        for (int i = 0; i < currentData.size(); i++) {
            ExerciseTrainingObject exerciseTrainingObject = new ExerciseTrainingObject();
            TrainingRow row = currentData.get(i);
            exerciseTrainingObject.setTrainingProgramId(trainingId);
            exerciseTrainingObject.setExerciseId(row.getExerciseId());
            exerciseTrainingObject.setPositionAtTraining(i);
            if (row.isSuperset()) {
                exerciseTrainingObject.setSuperset(true);
                exerciseTrainingObject.setSupersetColor(row.getSupersetColor());
                exerciseTrainingObject.setPositionAtSuperset(row.getPositionAtSuperset());
                exerciseTrainingObject.setSupersetId(row.getSupersetId());
            } else {
                exerciseTrainingObject.setSupersetId(0);
                exerciseTrainingObject.setSupersetColor(0);
                exerciseTrainingObject.setSuperset(false);
                exerciseTrainingObject.setPositionAtSuperset(0);
            }
            db.addExerciseTrainingObject(exerciseTrainingObject);
        }
    }

    private void playSound(Context context, Uri sound) {
        MediaPlayer mMediaPlayer = new MediaPlayer();
        try {
            mMediaPlayer.setDataSource(context, sound);
            final AudioManager audioManager = (AudioManager) context
                    .getSystemService(Context.AUDIO_SERVICE);
            if (audioManager.getStreamVolume(AudioManager.STREAM_ALARM) != 0) {
                mMediaPlayer.setAudioStreamType(AudioManager.STREAM_ALARM);
                mMediaPlayer.prepare();
                mMediaPlayer.start();
            }
        } catch (Exception e) {
            System.out.println(e.getMessage());
        }
    }

    private int dpFromPx(float px) {
        return (int) (px / getActivity().getResources().getDisplayMetrics().density);
    }

    private int pxFromDp(float dp) {
        return (int) (dp * getActivity().getResources().getDisplayMetrics().density);
    }

    @Override
    public void onListItemSwapped(int i1, int i2) {
        swapElements(adapter.getData(), i1, i2);
        if (currentCheckedPosition == i1) {
            currentCheckedPosition = i2;
        } else if (currentCheckedPosition == i2) {
            currentCheckedPosition = i1;
        }
        onSelected(currentCheckedPosition);
        adapter.notifyDataSetChanged();
    }

    private void swapElements(List list, int indexOne, int indexTwo) {
        Object temp = list.get(indexOne);
        list.set(indexOne, list.get(indexTwo));
        list.set(indexTwo, temp);
    }

    private class RepsAdapter extends AbstractWheelTextAdapter {
        ArrayList<String> reps = new ArrayList<String>();

        protected RepsAdapter(Context context) {
            super(context, R.layout.city_holo_layout, NO_RESOURCE);

            for (int i = 0; i < 300; i++) {
                reps.add("" + (i + 1));
            }

            setItemTextResource(R.id.city_name);
        }

        @Override
        public View getItem(int index, View cachedView, ViewGroup parent) {
            return super.getItem(index, cachedView, parent);
        }

        @Override
        public int getItemsCount() {
            return reps.size();
        }

        @Override
        protected CharSequence getItemText(int index) {
            return reps.get(index);
        }
    }

    private class WeightsAdapter extends AbstractWheelTextAdapter {
        ArrayList<String> weights = new ArrayList<String>();

        protected WeightsAdapter(Context context) {
            super(context, R.layout.city_holo_layout, NO_RESOURCE);

            for (int i = 0; i < 1000; i++) {
                weights.add("" + (i + 1));
            }
            setItemTextResource(R.id.city_name);
        }

        @Override
        public View getItem(int index, View cachedView, ViewGroup parent) {
            return super.getItem(index, cachedView, parent);
        }

        @Override
        public int getItemsCount() {
            return weights.size();
        }

        @Override
        protected CharSequence getItemText(int index) {
            return weights.get(index);
        }
    }

    private class TrainingAdapter extends BaseAdapter {

        private final static int INVALID_ID = -1;
        private LayoutInflater inflater;
        private List<TrainingRow> data;

        public TrainingAdapter(Context context) {
            this.inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            data = new ArrayList<TrainingRow>();
        }

        public void addData(List<TrainingRow> newData) {
            data.addAll(newData);
            notifyDataSetChanged();
        }

        public void deleteItem(int position) {
            data.remove(position);
            notifyDataSetChanged();
        }

        public List<TrainingRow> getData() {
            return this.data;
        }

        @Override
        public int getCount() {
            return data.size();
        }

        @Override
        public TrainingRow getItem(int position) {
            return data.get(position);
        }

        @Override
        public long getItemId(int position) {
            if (position >= 0 && position < data.size()) {
                return data.get(position).getId();
            } else {
                return INVALID_ID;
            }
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            View view = convertView;
            if (view == null) {
                view = inflater.inflate(R.layout.list_item_creating_programm, parent, false);
                ViewHolder viewHolder = new ViewHolder();
                viewHolder.tvExerciseName = (TextViewLight) view.findViewById(R.id.text_exercise);
                viewHolder.tvSupersetNumber = (TextViewLight) view.findViewById(R.id.text_number_of_superset);
                viewHolder.color = view.findViewById(R.id.color_superset);
                viewHolder.imageViewSuperset = (ImageView) view.findViewById(R.id.imageSuperset);
                view.setTag(viewHolder);
            }
            ViewHolder holder = (ViewHolder) view.getTag();
            holder.tvExerciseName.setText(data.get(position).getExerciseName());
            if (data.get(position).isSuperset()) {
                holder.color.setVisibility(View.VISIBLE);
                holder.color.setBackgroundColor(data.get(position).getSupersetColor());
                holder.tvSupersetNumber.setVisibility(View.VISIBLE);
                holder.tvSupersetNumber.setText(String.valueOf(data.get(position).getPositionAtSuperset() + 1));
                holder.imageViewSuperset.setVisibility(View.VISIBLE);
            } else {
                holder.color.setVisibility(View.GONE);
                holder.tvSupersetNumber.setVisibility(View.GONE);
                holder.imageViewSuperset.setVisibility(View.GONE);
            }
            return view;
        }

        private class ViewHolder {
            TextViewLight tvExerciseName;
            ImageView imageViewSuperset;
            View color;
            TextViewLight tvSupersetNumber;
        }
    }


}