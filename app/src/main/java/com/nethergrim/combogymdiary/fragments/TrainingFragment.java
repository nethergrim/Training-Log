package com.nethergrim.combogymdiary.fragments;

import android.app.ActionBar;
import android.app.DialogFragment;
import android.app.Fragment;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
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
import android.preference.PreferenceManager;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.SparseBooleanArray;
import android.view.ActionMode;
import android.view.Display;
import android.view.Gravity;
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
import android.widget.CompoundButton;
import android.widget.CompoundButton.OnCheckedChangeListener;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.PopupWindow;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.ToggleButton;

import com.nethergrim.combogymdiary.DB;
import com.nethergrim.combogymdiary.R;
import com.nethergrim.combogymdiary.activities.BaseActivity;
import com.nethergrim.combogymdiary.activities.EditingProgramAtTrainingActivity;
import com.nethergrim.combogymdiary.activities.HistoryDetailedActivity;
import com.nethergrim.combogymdiary.dialogs.DialogAddCommentToTraining;
import com.nethergrim.combogymdiary.dialogs.DialogExitFromTraining;
import com.nethergrim.combogymdiary.service.TrainingService;
import com.nethergrim.combogymdiary.tools.StableArrayAdapter;
import com.nethergrim.combogymdiary.view.DynamicListView;
import com.nethergrim.combogymdiary.view.DynamicListView.onElementsSwapped;
import com.nethergrim.combogymdiary.view.FloatingActionButton;
import com.yandex.metrica.Counter;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.StringTokenizer;
import java.util.concurrent.atomic.AtomicInteger;

import kankan.wheel.widget.WheelView;
import kankan.wheel.widget.adapters.AbstractWheelTextAdapter;

public class TrainingFragment extends Fragment implements
        OnCheckedChangeListener, OnClickListener, onElementsSwapped, BaseActivity.OnDrawerEvent {

    public final static String TRAINING_AT_PROGRESS = "training_at_progress";
    public final static String TRA_ID = "tra_id";
    public final static String CHECKED_POSITION = "checked_pos";
    private final static String START_TIME = "start_time";
    private final static String LIST_OF_SETS = "list_of_sets";
    private final static String PROGRESS = "progress";
    private final static String TIMER_IS_ON = "timerIsOn";
    public String RINGTONE = "ringtone";
    private ActionBar bar;
    private static final AtomicInteger sNextGeneratedId = new AtomicInteger(1);
    private Boolean tglChecked = true, vibrate = false;
    private EditText etTimer;
    private DB db;
    private String traName = "", exeName = "", date = "";
    private SharedPreferences sp;
    private int checkedPosition = 0, set = 0, currentSet = 0, oldReps = 0,
            oldWeight = 0, timerValue = 0, vibrateLenght = 0, currentId = 0;
    private long startTime = 0;
    private Handler h;
    private WheelView repsWheel, weightWheel;
    private TextView tvInfoText, tvWeight;
    private ArrayList<String> alExersicesList = new ArrayList<String>();
    private ArrayList<Integer> alSetList = new ArrayList<Integer>();
    private int trainingId = 0;
    private boolean btnBlocked = false;
    private PopupWindow popupWindow;
    Runnable timerRunnable = new Runnable() {
        @Override
        public void run() {
            long millis = System.currentTimeMillis() - startTime;
            int seconds = (int) (millis / 1000);
            int minutes = (seconds / 60);
            seconds = (seconds % 60);
            bar.setSubtitle((String.format("%d:%02d", minutes, seconds)) + " "
                    + " " + " ["
                    + ((set == currentSet ? set : currentSet) + 1) + " "
                    + getResources().getString(R.string.set) + "] ");
            timerHandler.postDelayed(this, 500);

        }
    };
    private Handler timerHandler = new Handler();
    private LinearLayout llBottom;
    private Animation anim = null;
    private boolean isTrainingAtProgress = false, toPlaySound = false;
    private ProgressDialog pd;
    private boolean isActiveDialog = false, blocked = false, blockedSelection = false;
    private DynamicListView listView;
    private StableArrayAdapter adapter;
    private FloatingActionButton fabLeft, fabRight, fabCenter;
    private ActionMode.Callback mActionModeCallback = new ActionMode.Callback() {

        @Override
        public boolean onCreateActionMode(ActionMode mode, Menu menu) {
            MenuInflater inflater = mode.getMenuInflater();
            inflater.inflate(R.menu.cab_training, menu);
            fabLeft.hide();
            fabRight.hide();
            fabCenter.hide();
            return true;
        }

        @Override
        public boolean onPrepareActionMode(ActionMode mode, Menu menu) {
            blockedSelection = true;
            listView.setChoiceMode(ListView.CHOICE_MODE_MULTIPLE);
            for (int i = 0; i < listView.getCount(); ++i) {
                listView.setItemChecked(i, false);
            }
            listView.setupLongClickListener(0);
            llBottom.setVisibility(View.GONE);
            return false;
        }

        @Override
        public boolean onActionItemClicked(ActionMode mode, MenuItem item) {
            if (item.getItemId() == R.id.cab_delete) {

                long[] itemsChecked = listView.getCheckedItemIds();
                SparseBooleanArray deletingPositions = listView
                        .getCheckedItemPositions();

                if (itemsChecked.length >= listView.getCount()) {
                    Toast.makeText(getActivity(),
                            R.string.cannot_delete_all_exe, Toast.LENGTH_LONG)
                            .show();
                    return false;
                }

                if (itemsChecked.length > 0) {

                    for (int i = listView.getCount(); i > 0; i--) {
                        if (deletingPositions.get(i - 1)) {
                            alExersicesList.remove(i - 1);
                            alSetList.remove(i - 1);
                        }
                    }
                    updateAdapter();
                }

                for (int i = 0; i < listView.getCount(); ++i) {
                    listView.setItemChecked(i, false);
                }
                blockedSelection = false;
                listView.setChoiceMode(ListView.CHOICE_MODE_SINGLE);
                listView.setupLongClickListener();

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
            listView.setupLongClickListener();
            if (listView.getCount() > 0) {
                onSelected(0);
                listView.setItemChecked(0, true);
                llBottom.setVisibility(View.VISIBLE);
            }
            fabCenter.show();
            initSetButtons();
        }
    };
    private int btnSaveId, btnBackId, btnForwardId;
    private boolean isResumed = false;

    @SuppressWarnings("rawtypes")
    @Override
    public void onSwapped(ArrayList arrayList, int indexOne, int indexTwo) {
        swapElements(alSetList, indexOne, indexTwo);
        swapElements(alExersicesList, indexOne, indexTwo);
        if (checkedPosition == indexOne) {
            checkedPosition = indexTwo;
        } else if (checkedPosition == indexTwo) {
            checkedPosition = indexOne;
        }
        onSelected(checkedPosition);
    }

    @SuppressWarnings({"rawtypes", "unchecked"})
    private void swapElements(ArrayList arrayList, int indexOne, int indexTwo) {
        Object temp = arrayList.get(indexOne);
        arrayList.set(indexOne, arrayList.get(indexTwo));
        arrayList.set(indexTwo, temp);
    }

    private void loadExercisesFromDbAndUpdateList() {
        alExersicesList = new ArrayList<String>();
        try {
            if (db.getTrainingList(trainingId) != null) {
                String[] exersices = db.convertStringToArray(db
                        .getTrainingList(trainingId));
                for (int i = 0; i < exersices.length; i++) {
                    alExersicesList.add(exersices[i]);
                }
            } else {
                alExersicesList.add(" none ");
            }
        } catch (Exception e2) {
            Counter.sharedInstance().reportError("", e2);
        }
    }

    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);
        setRetainInstance(true);
        db = new DB(getActivity());
        db.open();
        sp = PreferenceManager.getDefaultSharedPreferences(getActivity());
        trainingId = getArguments().getInt(BaseActivity.TRAINING_ID);
        isTrainingAtProgress = sp.getBoolean(TRAINING_AT_PROGRESS, false);
        sp.edit().putInt(TRA_ID, trainingId).apply();
        sp.edit().putBoolean(TRAINING_AT_PROGRESS, true).apply();
        traName = db.getTrainingName(trainingId);
        bar = getActivity().getActionBar();
        bar.setTitle(traName);
        loadExercisesFromDbAndUpdateList();

        for (int i = 0; i < 150; i++) {
            alSetList.add(0);
        }
        getActivity().startService(
                new Intent(getActivity(), TrainingService.class));
        startTime = System.currentTimeMillis();
        sp.edit().putLong(START_TIME, startTime);
        createButtons();
    }

    private void createButtons() {
        fabLeft = new FloatingActionButton.Builder(getActivity())
                .withDrawable(getResources().getDrawable(R.drawable.ic_action_back))
                .withButtonColor(getResources().getColor(R.color.holo_blue_light))
                .withGravity(Gravity.BOTTOM | Gravity.LEFT)
                .withMargins(16, 0, 0, 16)
                .create();
        fabCenter = new FloatingActionButton.Builder(getActivity())
                .withDrawable(getResources().getDrawable(R.drawable.ic_action_save))
                .withButtonColor(getResources().getColor(R.color.holo_blue_light))
                .withGravity(Gravity.BOTTOM | Gravity.CENTER_HORIZONTAL)
                .withMargins(0, 0, 0, 16)
                .create();
        fabRight = new FloatingActionButton.Builder(getActivity())
                .withDrawable(getResources().getDrawable(R.drawable.ic_action_forward))
                .withButtonColor(getResources().getColor(R.color.holo_blue_light))
                .withGravity(Gravity.BOTTOM | Gravity.RIGHT)
                .withMargins(0, 0, 16, 16)
                .create();

        fabCenter.hide();
        fabRight.hide();
        fabLeft.hide();
        fabRight.setOnClickListener(this);
        fabLeft.setOnClickListener(this);
        fabCenter.setOnClickListener(this);
        fabCenter.setId(generateViewId());
        fabLeft.setId(generateViewId());
        fabRight.setId(generateViewId());
        btnSaveId = fabCenter.getId();
        btnBackId = fabLeft.getId();
        btnForwardId = fabRight.getId();
    }

    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View v = inflater.inflate(
                R.layout.training_at_progress_new_wheel_new_list, null);
        LinearLayout llTimerProgress = (LinearLayout) v.findViewById(R.id.llProgressShow);


        llTimerProgress.setVisibility(View.GONE);
        llBottom = (LinearLayout) v.findViewById(R.id.LLBottom);
        anim = AnimationUtils.loadAnimation(getActivity(),   R.anim.setfortraining);
        tvWeight = (TextView) v.findViewById(R.id.textView4__);
        repsWheel = (WheelView) v.findViewById(R.id.wheelReps);
        repsWheel.setVisibleItems(7);
        repsWheel.setWheelBackground(R.drawable.wheel_bg_holo);
        repsWheel.setWheelForeground(R.drawable.wheel_val_holo);
        repsWheel.setShadowColor(0xFFFFFF, 0xFFFFFF, 0xFFFFFF);
        repsWheel.setViewAdapter(new RepsAdapter(getActivity()));
        weightWheel = (WheelView) v.findViewById(R.id.wheelWeight);
        weightWheel.setVisibleItems(7);
        weightWheel.setWheelBackground(R.drawable.wheel_bg_holo);
        weightWheel.setWheelForeground(R.drawable.wheel_val_holo);
        weightWheel.setShadowColor(0xFFFFFF, 0xFFFFFF, 0xFFFFFF);
        weightWheel.setViewAdapter(new WeightsAdapter(getActivity()));
        ToggleButton tglTimerOn = (ToggleButton) v.findViewById(R.id.tglTurnOff);
        tglTimerOn.setOnCheckedChangeListener(this);
        etTimer = (EditText) v.findViewById(R.id.etTimerValueAtTraining);
        etTimer.setOnClickListener(this);
        etTimer.addTextChangedListener(new TextWatcher() {

            @Override
            public void onTextChanged(CharSequence s, int start, int before,
                                      int count) {

            }

            @Override
            public void beforeTextChanged(CharSequence s, int start, int count,
                                          int after) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                updateTimer(s.toString());
            }
        });

        tvInfoText = (TextView) v.findViewById(R.id.infoText);
        listView = (DynamicListView) v.findViewById(R.id.listViewExerciseList);

        listView.setList(alExersicesList);
        listView.setFragment(this);
        adapter = new StableArrayAdapter(getActivity(),
                R.layout.my_training_list_item, alExersicesList);
        listView.setAdapter(adapter);
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View itemClicked,
                                    int position, long id) {
                onSelected(position);
            }
        });
        listView.setItemChecked(sp.getInt(CHECKED_POSITION, 0), true);
        onSelected(sp.getInt(CHECKED_POSITION, 0));
        SimpleDateFormat sdf = new SimpleDateFormat("dd.MM.yyyy");
        date = sdf.format(new Date(System.currentTimeMillis()));
        tvInfoText.setTextColor(getResources().getColor(R.color.holo_orange_dark));
        boolean isTimerOn = sp.getBoolean(TIMER_IS_ON, false);
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
                fabLeft.show();
            } else {
                fabLeft.hide();
            }
            if (currentSet < set) {
                fabRight.show();
            } else {
                fabRight.hide();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void onSelected(int position) {
        if (blockedSelection)
            return;
        if (alExersicesList.size() == 0) {
            llBottom.setVisibility(View.GONE);
            Toast.makeText(getActivity(), R.string.please_add_an_exe,
                    Toast.LENGTH_LONG).show();
            return;
        } else {
            llBottom.setVisibility(View.VISIBLE);
        }

        sp.edit().putInt(CHECKED_POSITION, position).apply();
        checkedPosition = position;
        exeName = alExersicesList.get(position);
        set = alSetList.get(position);
        try {
            timerValue = Integer.parseInt(db
                    .getTimerValueByExerciseName(exeName));
        } catch (NumberFormatException e) {
            Toast.makeText(getActivity(), R.string.parsing_error,
                    Toast.LENGTH_LONG).show();
            timerValue = 60;
            Counter.sharedInstance().reportError("", e);
        }
        currentSet = set;
        etTimer.setText(String.valueOf(timerValue));
        initSetButtons();
        oldReps = db.getLastWeightOrReps(exeName, set, false);
        oldWeight = db.getLastWeightOrReps(exeName, set, true);
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
        blocked = false;
    }

    public void onResume() {
        super.onResume();
        isResumed = true;
        listView.setKeepScreenOn(!(sp.getBoolean("toTurnOff", false)));
        vibrate = sp.getBoolean("vibrateOn", true);
        String vl = sp.getString("vibtateLenght", "2");
        try {
            vibrateLenght = Integer.parseInt(vl);
        } catch (Exception e) {
            vibrateLenght = 2;
        }
        vibrateLenght *= 1000;
        toPlaySound = sp.getBoolean("toNotifyWithSound", true);
        if (isTrainingAtProgress) {
            startTime = sp.getLong(START_TIME, 0);
            restoreSetsFromPreferences();
            try {
                listView.setItemChecked(sp.getInt(CHECKED_POSITION, 0), true);
                onSelected(sp.getInt(CHECKED_POSITION, 0));
            } catch (Exception e) {
                listView.setItemChecked(0, true);
                onSelected(0);
            }
        }
        timerHandler.postDelayed(timerRunnable, 0);
        fabCenter.show();

        h = new Handler() {
            public void handleMessage(Message msg) {
                if (msg.what == 0) {
                    this.removeMessages(0);
                } else {
                    pd.setIndeterminate(false);
                    if (pd.getProgress() < pd.getMax()) {
                        pd.incrementProgressBy(1);
                        h.sendEmptyMessageDelayed(1, 1000);
                    } else {
                        if (toPlaySound) {
                            String sound = sp.getString(RINGTONE, null);

                            if (sound == null) {
                                playSound(
                                        getActivity(),
                                        RingtoneManager
                                                .getDefaultUri(RingtoneManager.TYPE_NOTIFICATION)
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

    private void goDialogProgress() {
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
                        h.removeCallbacksAndMessages(null);
                    }
                }
        );
        pd.show();
        isActiveDialog = true;
        h.sendEmptyMessageDelayed(1, 100);
    }

    public void onPause() {
        super.onPause();
        sp.edit().putLong(START_TIME, startTime).apply();
        timerHandler.removeCallbacks(timerRunnable);
        saveSetsToPreferences();
        isTrainingAtProgress = true;
        saveExercicesToDatabase();
        fabRight.hide();
        fabLeft.hide();
        fabCenter.hide();
        isResumed = false;
    }

    @Override
    public void onStop() {
        super.onStop();
        fabRight.hide();
        fabLeft.hide();
        fabCenter.hide();
    }

    private void saveExercicesToDatabase() {
        String[] e = new String[alExersicesList.size()];
        for (int i = 0; i < e.length; ++i) {
            e[i] = alExersicesList.get(i);
        }
        db.updateRec_Training(trainingId, 2, db.convertArrayToString(e));
    }

    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        menu.clear();
        inflater.inflate(R.menu.main, menu);
        super.onCreateOptionsMenu(menu, inflater);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int itemId = item.getItemId();
        if (itemId == R.id.itemExit) {
            DialogFragment dlg1 = new DialogExitFromTraining();
            dlg1.setCancelable(false);
            if (!dlg1.isAdded())
                dlg1.show(getFragmentManager(), "dlg1");
        } else if (itemId == R.id.itemEditTrainings) {
            Intent intent = new Intent(getActivity(),
                    EditingProgramAtTrainingActivity.class);
            intent.putExtra("trName", traName);
            intent.putExtra("ifAddingExe", true);
            startActivityForResult(intent, 1);
        } else if (itemId == R.id.itemSeePreviousTraining) {
            String[] args = {traName};
            Cursor tmpCursor = db.getDataMain(null, DB.TRA_NAME + "=?", args,
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
                        getResources().getString(R.string.no_history) + traName,
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
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (data == null) {
            return;
        }

        long[] itemsChecked = data
                .getLongArrayExtra("return_array_of_exersices");
        for (int i = 0; i < itemsChecked.length; i++) {

            alExersicesList.add(db.getExerciseByID((int) itemsChecked[i]));
            alSetList.add(0);
        }
        for (int j = 0; j < 100; j++) {
            alSetList.add(0);
        }
        adapter.notifyDataSetChanged();
        String[] tmp = new String[alExersicesList.size()];

        for (int i = 0; i < alExersicesList.size(); i++) {
            tmp[i] = alExersicesList.get(i);
        }
        db.updateRec_Training(trainingId, 2, db.convertArrayToString(tmp));
        updateAdapter();
    }

    private void updateTimer(String tmp) {
        String timerv;
        if (tmp != null) {
            timerv = tmp;
        } else {
            timerv = etTimer.getText().toString();
        }
        String tmpStr = db.getTimerValueByExerciseName(exeName);

        if (tmpStr != null && timerv != null && !tmpStr.equals(timerv)) {
            int exe_id = db.getExeIdByName(exeName);
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
        if (id == btnSaveId && currentSet == set && !btnBlocked) {
            int wei = (weightWheel.getCurrentItem() + 1);
            int rep_s = (repsWheel.getCurrentItem() + 1);
            int tmp = alSetList.get(checkedPosition);
            tmp++;
            alSetList.set(checkedPosition, tmp);
            set = alSetList.get(checkedPosition);
            db.addRecMainTable(traName, exeName, date, wei, rep_s, set);
            currentSet = set;
            initSetButtons();
            try {
                Toast.makeText(getActivity(), R.string.saved, Toast.LENGTH_SHORT).show();
            } catch (Resources.NotFoundException e) {
                e.printStackTrace();
            }
            if (isActiveDialog) {
                h.sendEmptyMessage(0);
            }
            showPopup();
            oldReps = db.getLastWeightOrReps(exeName, set, false);
            oldWeight = db.getLastWeightOrReps(exeName, set, true);
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
        } else if (id == btnSaveId && currentSet < set) {
            int wei = (weightWheel.getCurrentItem() + 1);
            int rep_s = (repsWheel.getCurrentItem() + 1);
            db.updateRec_Main(currentId, 4, null, wei);
            db.updateRec_Main(currentId, 5, null, rep_s);
            Toast.makeText(getActivity(), R.string.resaved, Toast.LENGTH_SHORT)
                    .show();
            currentSet = set;
            onSelected(checkedPosition);
        } else if (id == btnBackId) {
            if (currentSet > 0) {
                llBottom.startAnimation(anim);
                currentSet--;
                int weitghsS = db.getThisWeight(currentSet + 1, exeName) - 1;
                int repsS = db.getThisReps(currentSet + 1, exeName) - 1;
                currentId = db.getThisId(currentSet + 1, exeName);
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

        } else if (id == btnForwardId) {
            if (currentSet < set - 1) {
                llBottom.startAnimation(anim);
                currentSet++;
                int weitghsS = db.getThisWeight(currentSet + 1, exeName) - 1;
                int repsS = db.getThisReps(currentSet + 1, exeName) - 1;
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
                onSelected(checkedPosition);
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
            TextView text1 = (TextView) v.findViewById(R.id.text1);
            TextView textViewRepsDelta = (TextView) v.findViewById(R.id.text_reps_delta);
            TextView text2 = (TextView) v.findViewById(R.id.text2);

            if (weightDelta > 0) {
                textViewWeightDelta.setTextColor(getActivity().getResources().getColor(R.color.holo_green_light));
                text1.setTextColor(getActivity().getResources().getColor(R.color.holo_green_light));
                textViewWeightDelta.setText("+" + String.valueOf(weightDelta));
            } else {
                textViewWeightDelta.setTextColor(getActivity().getResources().getColor(R.color.holo_red_light));
                text1.setTextColor(getActivity().getResources().getColor(R.color.holo_red_light));
                textViewWeightDelta.setText(String.valueOf(weightDelta));
            }

            if (repsDelta > 0) {
                textViewRepsDelta.setTextColor(getActivity().getResources().getColor(R.color.holo_green_light));
                text2.setTextColor(getActivity().getResources().getColor(R.color.holo_green_light));
                textViewRepsDelta.setText("+" + String.valueOf(weightDelta));
            } else {
                textViewRepsDelta.setText(String.valueOf(repsDelta));
                textViewRepsDelta.setTextColor(getActivity().getResources().getColor(R.color.holo_red_light));
                text2.setTextColor(getActivity().getResources().getColor(R.color.holo_red_light));
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

                    }

                    getActivity().runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            if (tglChecked) {
                                sp.edit().putInt(PROGRESS, 0).apply();
                                goDialogProgress();
                            }
                        }
                    });
                }
            });
            thread.start();
        } else {
            if (tglChecked) {
                sp.edit().putInt(PROGRESS, 0).apply();
                goDialogProgress();
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
            sp.edit().putBoolean(TIMER_IS_ON, true).apply();
            tglChecked = true;
            etTimer.setEnabled(true);
        } else {
            sp.edit().putBoolean(TIMER_IS_ON, false).apply();
            tglChecked = false;
            etTimer.setEnabled(false);
        }
    }

    public void saveSetsToPreferences() {

        StringBuilder str = new StringBuilder();
        for (int i = 0; i < alSetList.size(); i++) {
            str.append(alSetList.get(i)).append(",");
        }
        sp.edit().putString(LIST_OF_SETS, str.toString()).apply();
    }

    public void restoreSetsFromPreferences() {
        if (sp.contains(LIST_OF_SETS)) {
            String savedString = sp.getString(LIST_OF_SETS, "");
            StringTokenizer st = new StringTokenizer(savedString, ",");
            ArrayList<Integer> array = new ArrayList<Integer>();
            int size = st.countTokens();
            for (int i = 0; i < size; i++) {
                try {
                    array.add(Integer.parseInt(st.nextToken()));
                } catch (Exception e) {
                    array.add(0);
                }
            }
            alSetList = array;
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

    protected void updateAdapter() {
        adapter = new StableArrayAdapter(getActivity(),
                R.layout.my_training_list_item, alExersicesList);
        listView.setAdapter(adapter);
    }

    private int dpFromPx(float px) {
        return (int) (px / getActivity().getResources().getDisplayMetrics().density);
    }

    private int pxFromDp(float dp) {
        return (int) (dp * getActivity().getResources().getDisplayMetrics().density);
    }

    @Override
    public void onDrawerClosed() {
        if (isResumed){
            try {
                fabCenter.show();
                initSetButtons();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

    }

    @Override
    public void onDrawerOpened() {
            try {
                fabCenter.hide();
                fabRight.hide();
                fabLeft.hide();
            } catch (Exception e) {
                e.printStackTrace();
            }
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

    public static int generateViewId() {
        for (; ; ) {
            final int result = sNextGeneratedId.get();
            // aapt-generated IDs have the high byte nonzero; clamp to the range under that.
            int newValue = result + 1;
            if (newValue > 0x00FFFFFF) newValue = 1; // Roll over to 1, not 0.
            if (sNextGeneratedId.compareAndSet(result, newValue)) {
                return result;
            }
        }
    }
}