package com.nethergrim.combogymdiary.activities;

import android.app.Fragment;
import android.app.NotificationManager;
import android.appwidget.AppWidgetManager;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.database.Cursor;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.os.RemoteException;
import android.preference.PreferenceManager;
import android.support.v4.app.ActionBarDrawerToggle;
import android.support.v4.app.FragmentActivity;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.Toast;

import com.android.vending.billing.IInAppBillingService;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.InterstitialAd;
import com.nethergrim.combogymdiary.Constants;
import com.nethergrim.combogymdiary.DB;
import com.nethergrim.combogymdiary.R;
import com.nethergrim.combogymdiary.WidgetStatistics;
import com.nethergrim.combogymdiary.dialogs.DialogAddExercise;
import com.nethergrim.combogymdiary.dialogs.DialogExitFromTraining.MyInterface;
import com.nethergrim.combogymdiary.dialogs.DialogInfo;
import com.nethergrim.combogymdiary.dialogs.DialogUniversalApprove;
import com.nethergrim.combogymdiary.dialogs.DialogUniversalApprove.OnEditExerciseAccept;
import com.nethergrim.combogymdiary.dialogs.DialogUniversalApprove.OnStartTrainingAccept;
import com.nethergrim.combogymdiary.fragments.CatalogFragment;
import com.nethergrim.combogymdiary.fragments.ExerciseListFragment;
import com.nethergrim.combogymdiary.fragments.ExerciseListFragment.OnExerciseEdit;
import com.nethergrim.combogymdiary.fragments.HistoryFragment;
import com.nethergrim.combogymdiary.fragments.MeasurementsFragment;
import com.nethergrim.combogymdiary.fragments.StartTrainingFragment;
import com.nethergrim.combogymdiary.fragments.StartTrainingFragment.OnSelectedListener;
import com.nethergrim.combogymdiary.fragments.TrainingFragment;
import com.nethergrim.combogymdiary.googledrive.BaseDriveActivity;
import com.nethergrim.combogymdiary.googledrive.DriveBackupActivity;
import com.nethergrim.combogymdiary.service.TrainingService;
import com.nethergrim.combogymdiary.tools.AdChecker;
import com.nethergrim.combogymdiary.tools.Backuper;
import com.yandex.metrica.Counter;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

public class BaseActivity extends FragmentActivity implements
        OnSelectedListener, MyInterface, OnStartTrainingAccept, OnExerciseEdit,
        OnEditExerciseAccept {
    public final static String TOTAL_WEIGHT = "total_weight";
    public final static String TRAINING_AT_PROGRESS = "training_at_progress";
    public final static String COMMENT_TO_TRAINING = "comment_to_training";
    public final static String START_TIME = "start_time";
    public final static String MEASURE_ITEM = "measureItem";
    public final static String LIST_OF_SETS = "list_of_sets";
    public final static String TRAINING_ID = "training_id";
    public final static String TRAINING_NAME = "training_name";
    public final static String TRA_ID = "tra_id";
    public final static String TRAINING_LIST = "training_list";
    public final static String TIMER_IS_ON = "timerIsOn";
    public final static String MY_AD_UNIT_ID = "ca-app-pub-5652589022154086/4102541457";
    public final static String MY_ACCOUNT_NAME = "account_name";
    public final static String DRIVE_FOLDER_ID_ENCODED_TO_STRING = "drive_folder_id";
    public final static String DRIVE_EXISTS = "drive_exists";
    public final static String MARKET_LEAVED_FEEDBACK = "market_leaved_feedback";
    public final static String DATABASE_FILLED = "database_filled";
    public final static String AUTO_BACKUP_TO_DRIVE = "settingAutoBackup";
    public final static String PROGRESS = "progress";
    public final static String TRAININGS_DONE_NUM = "trainings_done_num";
    public final static String USER_CLICKED_POSITION = "user_clicked_position";
    public final static String APPLICAITON_ID = "52ebc42807089e0f00000000";
    public final static String MINUTES = "minutes";
    public final static String SECONDS = "seconds";
    public final static String TYPE_OF_DIALOG = "type_of_dialog";
    public final static String ID = "id";
    public final static String POSITION = "position";
    private final static String FRAGMENT_ID = "fragment_id";
    private static boolean IF_TRAINING_STARTED = false;
    protected final String LOG_TAG = "myLogs";
    protected SharedPreferences sPref;
    private DrawerLayout mDrawerLayout;
    private ListView mDrawerList;
    private ActionBarDrawerToggle mDrawerToggle;
    private String[] listButtons;
    private int FRAGMENT_NUMBER = 0;
    private SharedPreferences sp;
    private ArrayAdapter<String> adapter;
    private int previouslyChecked = 0;
    private DB db;
    //    private AdView adView;
    private InterstitialAd interstitial;
    private static int adStepCounter = 0;
    private static int adStepCounterLimit = 4;
    private boolean doubleBackToExitPressedOnce = false;
    private boolean resumed = false;
    private AdRequest adRequest;

    private IInAppBillingService mService;

    private ServiceConnection mServiceConn = new ServiceConnection() {
        @Override
        public void onServiceDisconnected(ComponentName name) {
            mService = null;
        }

        @Override
        public void onServiceConnected(ComponentName name, IBinder service) {
            mService = IInAppBillingService.Stub.asInterface(service);
            checkAd();
        }
    };

    private CatalogFragment catalogFragment = new CatalogFragment();
    private ExerciseListFragment exerciseListFragment = new ExerciseListFragment();
    private HistoryFragment historyFragment = new HistoryFragment();
    private MeasurementsFragment measurementsFragment = new MeasurementsFragment();
    private StartTrainingFragment startTrainingFragment = new StartTrainingFragment();
    private TrainingFragment trainingFragment = new TrainingFragment();
    private Fragment currentFragment;

    public static boolean get_TRAINING_STARTED() {
        return IF_TRAINING_STARTED;
    }

    public static void set_TRAINING_STARTED(boolean iF_TRAINING_STARTED) {
        IF_TRAINING_STARTED = iF_TRAINING_STARTED;
    }

    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
        mDrawerToggle.onConfigurationChanged(newConfig);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        db = new DB(this);
        db.open();
        bindService(new Intent(
                        "com.android.vending.billing.InAppBillingService.BIND"),
                mServiceConn, Context.BIND_AUTO_CREATE
        );
        setContentView(R.layout.menu);
        initStrings();
        mDrawerLayout = (DrawerLayout) findViewById(R.id.drawer_layout);
        mDrawerList = (ListView) findViewById(R.id.left_drawer);
        mDrawerLayout.setDrawerShadow(R.drawable.drawer_shadow,
                GravityCompat.START);
        adapter = new ArrayAdapter<String>(this, R.layout.menu_list_item,
                listButtons);
        mDrawerList.setAdapter(adapter);
        mDrawerList.setOnItemClickListener(new DrawerItemClickListener());
        getActionBar().setDisplayHomeAsUpEnabled(true);
        getActionBar().setDisplayShowHomeEnabled(false);
        getActionBar().setHomeButtonEnabled(true);
        mDrawerToggle = new ActionBarDrawerToggle(this, /* host Activity */
                mDrawerLayout, /* DrawerLayout object */
                R.drawable.ic_drawer, /* nav drawer image to replace 'Up' caret */
                R.string.drawer_open, /* "open drawer" description for accessibility */
                R.string.drawer_close /* "close drawer" description for accessibility */
        ) {
            public void onDrawerClosed(View view) {
                invalidateOptionsMenu();
            }

            public void onDrawerOpened(View drawerView) {
                invalidateOptionsMenu();
            }
        };
        mDrawerLayout.setDrawerListener(mDrawerToggle);
        sp = PreferenceManager.getDefaultSharedPreferences(this);
        if (sp.getBoolean(TRAINING_AT_PROGRESS, false)) {
            currentFragment = trainingFragment;
            Bundle args = new Bundle();
            args.putInt(TRAINING_ID, sp.getInt(TRA_ID, 0));
            currentFragment.setArguments(args);
            listButtons[0] = getResources().getString(
                    R.string.continue_training);
            adapter.notifyDataSetChanged();
            set_TRAINING_STARTED(true);
        } else {
            currentFragment = startTrainingFragment;
        }
        if (currentFragment != null)
            getFragmentManager().beginTransaction()
                    .add(R.id.content, currentFragment).commit();
        mDrawerList.setItemChecked(0, true);
        if (savedInstanceState == null) {
            selectItem(0);
        }
        if (sp.getBoolean("ad", false)) {
            AdChecker.setPaid(true);
        }
        interstitial = new InterstitialAd(this);
        interstitial.setAdUnitId(Constants.AD_INTERTISTIAL_BLOCK_ID);
        loadAd();
    }

    private boolean checkAd() {
        try {
            Bundle ownedItems = mService.getPurchases(3, getPackageName(),
                    "inapp", null);

            int response = ownedItems.getInt("RESPONSE_CODE");
            if (response == 0) {
                ArrayList<String> purchaseDataList = ownedItems
                        .getStringArrayList("INAPP_PURCHASE_DATA_LIST");
                Log.d("myLogs", "purchaseDataList.size() == "
                        + purchaseDataList.size());

                if (purchaseDataList.size() > 0) {
                    AdChecker.setPaid(true);
                    PreferenceManager.getDefaultSharedPreferences(this).edit()
                            .putBoolean("ad", true).apply();
                    Counter.sharedInstance()
                            .reportEvent("checkAd() true, paid");
                    return true;
                } else if (purchaseDataList.size() == 0) {
                    AdChecker.setPaid(false);

                    PreferenceManager.getDefaultSharedPreferences(this).edit()
                            .putBoolean("ad", false).apply();
                    Counter.sharedInstance().reportEvent(
                            "checkAd() false, not paid");
                    return false;
                }

                if (AdChecker.IsPaid()
                        && !PreferenceManager.getDefaultSharedPreferences(this)
                        .getBoolean("ad", false)) {
                    Counter.sharedInstance()
                            .reportEvent(
                                    "AdChecker.getIsPaid() && !PreferenceManager.getDefaultSharedPreferences(this).getBoolean(\"ad\", false)");
                }

            }

        } catch (RemoteException e) {
            Counter.sharedInstance().reportError("", e);
        }
        return false;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (mDrawerToggle.onOptionsItemSelected(item)) {
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    private void initStrings() {
        listButtons = new String[8];
        listButtons[0] = getResources().getString(
                R.string.startTrainingButtonString);
        listButtons[1] = getResources().getString(
                R.string.excersisiesListButtonString);
        listButtons[2] = getResources().getString(R.string.training_history);
        listButtons[3] = getResources().getString(R.string.measurements);
        listButtons[4] = getResources().getString(R.string.exe_catalog);
        listButtons[5] = getResources().getString(R.string.statistics);
        listButtons[6] = getResources()
                .getString(R.string.settingsButtonString);
        listButtons[7] = getResources().getString(R.string.faq);
    }

    public void selectItem(int position) {
        mDrawerLayout.closeDrawer(mDrawerList);
        adStepCounter++;
        if (adStepCounter >= adStepCounterLimit ){
            adStepCounter = 0;
            showAd();
        } else if (adStepCounter == (adStepCounterLimit -1)){
            loadAd();
        }

        if (previouslyChecked == position) {
            return;
        }
        switch (position) {
            case 0:
                FRAGMENT_NUMBER = 0;
                if (get_TRAINING_STARTED()) {
                    currentFragment = trainingFragment;
                    Bundle args = new Bundle();
                    if (!currentFragment.isVisible())
                        args.putInt(TRAINING_ID, sp.getInt(TRA_ID, 0));
                    else
                        return;
                    currentFragment.setArguments(args);
                    listButtons[0] = getResources().getString(
                            R.string.continue_training);
                    adapter.notifyDataSetChanged();
                } else {
                    currentFragment = startTrainingFragment;
                }
                previouslyChecked = 0;
                break;
            case 1:
                FRAGMENT_NUMBER = 1;
                currentFragment = exerciseListFragment;
                previouslyChecked = 1;
                break;
            case 6:
                currentFragment = null;
                Intent intent = new Intent(this, SettingsActivity.class);
                startActivity(intent);
                mDrawerList.setItemChecked(previouslyChecked, true);
                break;
            case 7:
                currentFragment = null;
                DialogInfo dialog = new DialogInfo();
                dialog.show(getFragmentManager(), "info");
                mDrawerList.setItemChecked(previouslyChecked, true);
                break;
            case 2:
                FRAGMENT_NUMBER = 2;
                currentFragment = historyFragment;
                previouslyChecked = 2;
                break;
            case 3:
                FRAGMENT_NUMBER = 3;
                currentFragment = measurementsFragment;
                previouslyChecked = 3;
                break;
            case 4:
                FRAGMENT_NUMBER = 4;
                currentFragment = catalogFragment;
                previouslyChecked = 4;
                break;
            case 5:
                currentFragment = null;
                Intent intentStats = new Intent(this, StatisticsActivity.class);
                startActivity(intentStats);
                mDrawerList.setItemChecked(previouslyChecked, true);
                break;
        }
        if (currentFragment != null) {
            mDrawerList.setItemChecked(position, true);
            getFragmentManager().beginTransaction()
                    .replace(R.id.content, currentFragment).commit();
        }
        invalidateOptionsMenu();
    }

    @Override
    protected void onPostCreate(Bundle savedInstanceState) {
        super.onPostCreate(savedInstanceState);
        mDrawerToggle.syncState();
    }

    protected boolean isNetworkAvailable() {
        ConnectivityManager connectivityManager = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetworkInfo = connectivityManager
                .getActiveNetworkInfo();
        return activeNetworkInfo != null && activeNetworkInfo.isConnected();
    }

    @Override
    protected void onResume() {
        super.onResume();
        AdChecker.setPaid(PreferenceManager.getDefaultSharedPreferences(this)
                .getBoolean("ad", false));
        Counter.sharedInstance().onResumeActivity(this);
        resumed = true;
    }

    @Override
    protected void onPause() {
        super.onPause();
        Counter.sharedInstance().onPauseActivity(this);
        resumed = false;
    }

    @Override
    public void onSaveInstanceState(Bundle save) {
        save.putInt(FRAGMENT_ID, FRAGMENT_NUMBER);
        super.onSaveInstanceState(save);
    }

    public void onRestoreInstanceState(Bundle restore) {
        selectItem(restore.getInt(FRAGMENT_ID));
        super.onRestoreInstanceState(restore);
    }

    @Override
    public void onTrainingSelected(int id) {
        DialogUniversalApprove approve = new DialogUniversalApprove();
        Bundle args = new Bundle();
        args.putInt(TYPE_OF_DIALOG, 0);
        args.putInt(ID, id);
        approve.setArguments(args);
        approve.show(getFragmentManager(), "");
        loadAd();
    }

    private void showAd(){
        if (interstitial != null && /*interstitial.isLoaded() &&*/ !AdChecker.IsPaid() && resumed){
            interstitial.show();
            loadAd();
        } else if (interstitial == null){
            interstitial = new InterstitialAd(this);
            interstitial.setAdUnitId(Constants.AD_INTERTISTIAL_BLOCK_ID);
            loadAd();
        }
    }

    private void loadAd(){
        if (interstitial != null){
            adRequest = new AdRequest.Builder().setGender(AdRequest.GENDER_MALE).build();
            interstitial.loadAd(adRequest);
        }
    }

    @Override
    public void onChoose() {
        showAd();
        DB db = new DB(this);
        db.open();
        Cursor tmpCursor = db.getDataMain(null, null, null, null, null, null);
        if (tmpCursor.getCount() > 10) {
            Backuper backUP = new Backuper();
            backUP.backupToSd();
        }
        tmpCursor.close();

        sp.edit().putBoolean(TRAINING_AT_PROGRESS, false).apply();
        sp.edit().putInt(USER_CLICKED_POSITION, 0).apply();
        sp.edit().putInt(TrainingFragment.CHECKED_POSITION, 0).apply();
        int total = sp.getInt(TOTAL_WEIGHT, 0);
        SimpleDateFormat sdf = new SimpleDateFormat("dd.MM.yyyy");
        String date = sdf.format(new Date(System.currentTimeMillis()));
        int tmpSec = sp.getInt(SECONDS, 0);
        int tmpMin = tmpSec / 60;
        tmpSec = tmpSec - (tmpMin * 60);
        String time = tmpMin + ":" + tmpSec;
        if (!sp.getString(COMMENT_TO_TRAINING, "").equals("")) {
            db.addRecComment(date, sp.getString(COMMENT_TO_TRAINING, ""),
                    total, time);
        } else {
            db.addRecComment(date, null, total, time);
        }
        sp.edit().putString(COMMENT_TO_TRAINING, "").apply();
        sp.edit().putInt(TOTAL_WEIGHT, 0).apply();
        sp.edit().putLong(START_TIME, 0);

        stopService(new Intent(this, TrainingService.class));

        NotificationManager notificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
        notificationManager.cancelAll();

        if (PreferenceManager.getDefaultSharedPreferences(this).getBoolean(
                AUTO_BACKUP_TO_DRIVE, true)) {

            Intent backup = new Intent(this, DriveBackupActivity.class);
            backup.putExtra(BaseDriveActivity.KEY_AUTOBACKUP, true);
            startActivity(backup);
        }

        if (sp.contains(TRAININGS_DONE_NUM)) {
            int tmp = sp.getInt(TRAININGS_DONE_NUM, 0);
            tmp++;
            sp.edit().putInt(TRAININGS_DONE_NUM, tmp).apply();
        } else {
            sp.edit().putInt(TRAININGS_DONE_NUM, 1).apply();
        }
        set_TRAINING_STARTED(false);
        getFragmentManager().beginTransaction()
                .replace(R.id.content, new StartTrainingFragment()).commit();
        getActionBar().setSubtitle(" ");
        listButtons[0] = getResources().getString(
                R.string.startTrainingButtonString);
        adapter.notifyDataSetChanged();
        db.close();
    }

    @Override
    public void onAccept(int id) {
        TrainingFragment newFragment = new TrainingFragment();
        Bundle args = new Bundle();
        args.putInt(TRAINING_ID, id);
        newFragment.setArguments(args);
        getFragmentManager().beginTransaction()
                .replace(R.id.content, newFragment).commit();
        set_TRAINING_STARTED(true);
        listButtons[0] = getResources().getString(R.string.continue_training);
        adapter.notifyDataSetChanged();

    }

    @Override
    public void onExerciseEdit(int pos, long id) {
        DialogUniversalApprove approve = new DialogUniversalApprove();
        Bundle args = new Bundle();
        args.putInt(TYPE_OF_DIALOG, 1);
        args.putLong(ID, id);
        args.putInt(POSITION, pos);
        approve.setArguments(args);
        approve.show(getFragmentManager(), "");
    }

    @Override
    public void onAcceptEditExercise(long id, int pos) {

        if (sp.getBoolean(TRAINING_AT_PROGRESS, false)) {
            Toast.makeText(this, R.string.error_editing_exe, Toast.LENGTH_SHORT)
                    .show();
        } else {
            String[] cols = {DB.COLUMN_ID, DB.EXE_NAME, DB.TIMER_VALUE};
            Cursor cursor_exe = db.getDataExe(cols, null, null, null, null,
                    DB.EXE_NAME);
            cursor_exe.moveToFirst();
            while (cursor_exe.getPosition() < pos) {
                cursor_exe.moveToNext();
            }
            String name = cursor_exe.getString(1);
            String timV = cursor_exe.getString(2);
            cursor_exe.close();
            Bundle args = new Bundle();
            args.putString("exeName", name);
            args.putString("timerValue", timV);
            args.putInt("exePosition", pos);
            args.putLong("exeID", id);
            DialogAddExercise dialog = new DialogAddExercise();
            dialog.setArguments(args);
            dialog.show(getFragmentManager(), "tag");
        }
    }

    @Override
    public void onBackPressed() {
        if (doubleBackToExitPressedOnce) {
            super.onBackPressed();
            return;
        }

        this.doubleBackToExitPressedOnce = true;
        Toast.makeText(this, R.string.press_back_to_exit, Toast.LENGTH_SHORT)
                .show();

        new Handler().postDelayed(new Runnable() {

            @Override
            public void run() {
                doubleBackToExitPressedOnce = false;
            }
        }, 2000);
    }

    @Override
    public void onDestroy() {
        if (mService != null) {
            unbindService(mServiceConn);
        }
        super.onDestroy();
    }

    private class DrawerItemClickListener implements
            ListView.OnItemClickListener {
        @Override
        public void onItemClick(AdapterView<?> parent, View view, int position,
                                long id) {
            selectItem(position);
        }
    }

}