package com.nethergrim.combogymdiary.activities;

import android.app.Fragment;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.backup.BackupManager;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.IntentSender;
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
import com.inmobi.commons.InMobi;
import com.inmobi.monetization.IMInterstitial;
import com.nethergrim.combogymdiary.Constants;
import com.nethergrim.combogymdiary.DB;
import com.nethergrim.combogymdiary.R;
import com.nethergrim.combogymdiary.dialogs.DialogExitFromTraining.MyInterface;
import com.nethergrim.combogymdiary.dialogs.DialogInfo;
import com.nethergrim.combogymdiary.dialogs.DialogUniversalApprove;
import com.nethergrim.combogymdiary.dialogs.DialogUniversalApprove.OnStartTrainingAccept;
import com.nethergrim.combogymdiary.fragments.CatalogFragment;
import com.nethergrim.combogymdiary.fragments.ExerciseListFragment;
import com.nethergrim.combogymdiary.fragments.HistoryFragment;
import com.nethergrim.combogymdiary.fragments.MeasurementsFragment;
import com.nethergrim.combogymdiary.fragments.StartTrainingFragment;
import com.nethergrim.combogymdiary.fragments.StartTrainingFragment.OnSelectedListener;
import com.nethergrim.combogymdiary.fragments.TrainingFragment;
import com.nethergrim.combogymdiary.googledrive.BaseDriveActivity;
import com.nethergrim.combogymdiary.googledrive.DriveBackupActivity;
import com.nethergrim.combogymdiary.model.Exercise;
import com.nethergrim.combogymdiary.service.TrainingService;
import com.nethergrim.combogymdiary.tools.Backuper;
import com.nethergrim.combogymdiary.tools.GoogleDriveHelper;
import com.nethergrim.combogymdiary.tools.Prefs;
import com.yandex.metrica.Counter;

import org.json.JSONException;
import org.json.JSONObject;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Random;

public class BaseActivity extends AnalyticsActivity implements
        OnSelectedListener, MyInterface, OnStartTrainingAccept, DialogUniversalApprove.OnDeleteExerciseCallback, AdapterView.OnItemClickListener {

    public final static String SECONDS = "seconds";
    private final static String FRAGMENT_ID = "fragment_id";
    private static final char[] SYMBOLS = new char[36];
    private static boolean startedTraining = false;
    private DrawerLayout mDrawerLayout;
    private ListView mDrawerList;
    private ActionBarDrawerToggle mDrawerToggle;
    private String[] listButtons;
    private int FRAGMENT_NUMBER = 0;
    private SharedPreferences sp;
    private ArrayAdapter<String> adapter;
    private int previouslyChecked = 0;
    private DB db;
    private boolean doubleBackToExitPressedOnce = false;
    private IInAppBillingService mService;
    private CatalogFragment catalogFragment = new CatalogFragment();

    public ExerciseListFragment getExerciseListFragment() {
        return exerciseListFragment;
    }

    private ExerciseListFragment exerciseListFragment = new ExerciseListFragment();
    private HistoryFragment historyFragment = new HistoryFragment();
    private MeasurementsFragment measurementsFragment = new MeasurementsFragment();
    private StartTrainingFragment startTrainingFragment = new StartTrainingFragment();
    private TrainingFragment trainingFragment = new TrainingFragment();
    private Fragment currentFragment;
    private ServiceConnection mServiceConn;
   private int adCounter = 0;
    private IMInterstitial interstitial;

    static {
        for (int idx = 0; idx < 10; ++idx)
            SYMBOLS[idx] = (char) ('0' + idx);
        for (int idx = 10; idx < 36; ++idx)
            SYMBOLS[idx] = (char) ('a' + idx - 10);
    }

    public static boolean isTrainingAlreadyStarted() {
        return startedTraining;
    }

    public static void setTrainingAlreadyStarted(boolean started) {
        startedTraining = started;
    }

    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
        mDrawerToggle.onConfigurationChanged(newConfig);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_base);
        interstitial = new IMInterstitial(this, Constants.INMOBI_PROPERTY_ID);
        db = new DB(this);
        db.open();
        mServiceConn = new ServiceConnection() {
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
        bindService(new Intent("com.android.vending.billing.InAppBillingService.BIND"), mServiceConn, Context.BIND_AUTO_CREATE);

        mDrawerLayout = (DrawerLayout) findViewById(R.id.drawer_layout);
        mDrawerList = (ListView) findViewById(R.id.left_drawer);
        mDrawerLayout.setDrawerShadow(R.drawable.drawer_shadow, GravityCompat.START);
        initStrings();
        adapter = new ArrayAdapter<String>(this, R.layout.menu_list_item, listButtons);
        mDrawerList.setAdapter(adapter);
        mDrawerList.setOnItemClickListener(this);
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
        getActionBar().setDisplayHomeAsUpEnabled(true);
        getActionBar().setHomeButtonEnabled(true);
        sp = PreferenceManager.getDefaultSharedPreferences(this);
        if (Prefs.get().getTrainingAtProgress()) {
            currentFragment = trainingFragment;
            Bundle args = new Bundle();
            args.putInt(TrainingFragment.BUNDLE_KEY_TRAINING_ID, Prefs.get().getCurrentTrainingId());
            currentFragment.setArguments(args);
            listButtons[0] = getResources().getString(
                    R.string.continue_training);
            adapter.notifyDataSetChanged();
            setTrainingAlreadyStarted(true);
        } else {
            currentFragment = startTrainingFragment;
        }
        if (currentFragment != null)
            getFragmentManager().beginTransaction()
                    .replace(R.id.content, currentFragment).commit();
        mDrawerList.setItemChecked(0, true);
        if (savedInstanceState == null) {
            onItemSelected(0);
        }
    }

    private boolean checkAd() {
        try {
            Bundle ownedItems = mService.getPurchases(3, getPackageName(), "inapp", null);
            int response = ownedItems.getInt("RESPONSE_CODE");
            if (response == 0) {
                ArrayList<String> purchaseDataList = ownedItems.getStringArrayList("INAPP_PURCHASE_DATA_LIST");
                if (purchaseDataList.size() > 0) {
                    Prefs.get().setAdsRemoved(true);
                    Counter.sharedInstance().reportEvent("checkAd() true, paid");
                    return true;
                } else if (purchaseDataList.size() == 0) {
                    Prefs.get().setAdsRemoved(false);
                    return false;
                }

            } else {
                Prefs.get().setAdsRemoved(false);
            }

        } catch (RemoteException e) {
            Counter.sharedInstance().reportError("", e);
            Prefs.get().setAdsRemoved(false);
        }
        return false;
    }

    private void removeAds() {
        try {
            RandomString randomString = new RandomString(36);
            String payload = randomString.nextString();
            Bundle buyIntentBundle = mService.getBuyIntent(3, getPackageName(), "remove_ad", "inapp", payload);
            int response = buyIntentBundle.getInt("BILLING_RESPONSE_RESULT_OK");
            if (response == 0) {
                PendingIntent pendingIntent = buyIntentBundle.getParcelable("BUY_INTENT");
                startIntentSenderForResult(pendingIntent.getIntentSender(), 1001, new Intent(), 0, 0, 0);
            }
        } catch (RemoteException e) {
            Counter.sharedInstance().reportError("", e);
            e.printStackTrace();
        } catch (IntentSender.SendIntentException e) {
            Counter.sharedInstance().reportError("", e);
            e.printStackTrace();
        }

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == 1001 && resultCode == RESULT_OK) {
            String purchaseData = data.getStringExtra("INAPP_PURCHASE_DATA");
            try {
                JSONObject jo = new JSONObject(purchaseData);
                String sku = jo.getString("productId");
                Counter.sharedInstance().reportEvent(
                        "bought the " + sku + ".");
                Prefs.get().setAdsRemoved(true);
                initStrings();
                adapter.notifyDataSetChanged();
            } catch (JSONException e) {
                Counter.sharedInstance().reportError("", e);
            }

        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (mDrawerToggle.onOptionsItemSelected(item)) {
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    private void initStrings() {
        if (!Prefs.get().getAdsRemoved()) {
            listButtons = new String[9];
            listButtons[8] = getString(R.string.remove_ads);
        } else {
            listButtons = new String[8];
        }
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
        try {
            adapter.notifyDataSetChanged();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void onItemSelected(int position) {
        mDrawerLayout.closeDrawer(mDrawerList);
        if (!Prefs.get().getAdsRemoved()) {
            adCounter++;
            if (adCounter >= 3) {
                adCounter = 0;
                showAd();
            }
        }

        if (position == 8) {
            removeAds();
            mDrawerList.setItemChecked(previouslyChecked, true);
            return;
        }

        if (previouslyChecked == position) {
            return;
        }
        switch (position) {
            case 0:
                FRAGMENT_NUMBER = 0;
                if (isTrainingAlreadyStarted()) {
                    currentFragment = trainingFragment;
                    Bundle args = new Bundle();
                    if (!currentFragment.isVisible())
                        args.putInt(TrainingFragment.BUNDLE_KEY_TRAINING_ID, Prefs.get().getCurrentTrainingId());
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
                Bundle args = new Bundle();
                args.putBoolean(DialogInfo.KEY_INFO_ABOUT_APP, true);
                dialog.setArguments(args);
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

    @Override
    protected void onResume() {
        super.onResume();
        Counter.sharedInstance().onResumeActivity(this);
        initStrings();
        try {
            if (interstitial != null){
                interstitial.loadInterstitial();
            } else {
                interstitial = new IMInterstitial(this, Constants.INMOBI_PROPERTY_ID);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    @Override
    protected void onPause() {
        super.onPause();
        Counter.sharedInstance().onPauseActivity(this);
    }

    @Override
    public void onSaveInstanceState(Bundle save) {
        save.putInt(FRAGMENT_ID, FRAGMENT_NUMBER);
        super.onSaveInstanceState(save);
    }

    public void onRestoreInstanceState(Bundle restore) {
        onItemSelected(restore.getInt(FRAGMENT_ID));
        super.onRestoreInstanceState(restore);
    }

    @Override
    public void onTrainingSelected(int id) {
        DialogUniversalApprove approve = new DialogUniversalApprove();
        Bundle args = new Bundle();
        args.putInt(Constants.TYPE_OF_DIALOG, DialogUniversalApprove.TYPE_START_WORKOUT);
        args.putInt(Constants._ID, id);
        approve.setArguments(args);
        approve.show(getFragmentManager(), "");
    }

    @Override
    public void onChoose() {
        getActionBar().setSubtitle(null);
        setTrainingAlreadyStarted(false);
        DB db = new DB(this);
        db.open();
        Cursor tmpCursor = db.getDataMain(null, null, null, null, null, null);
        if (tmpCursor.getCount() > 10) {
            Backuper backUP = new Backuper();
            backUP.backupToSd();
        }
        tmpCursor.close();
        Prefs.get().setTrainingAtProgress(false);
        Prefs.get().setCheckedPosition(0);
        SimpleDateFormat sdf = new SimpleDateFormat("dd.MM.yyyy");
        String date = sdf.format(new Date(System.currentTimeMillis()));
        int tmpSec = sp.getInt(SECONDS, 0);
        int tmpMin = tmpSec / 60;
        tmpSec = tmpSec - (tmpMin * 60);
        String time = tmpMin + ":" + tmpSec;
        db.addRecComment(date, Prefs.get().getCommentToTraining(), 0, time);

        Prefs.get().setCommentToTraining("");

        Prefs.get().setStartTime(0);

        stopService(new Intent(this, TrainingService.class));
        NotificationManager notificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
        notificationManager.cancelAll();

        if (Prefs.get().getAutoBackupToDrive()) {
            Thread thread = new Thread(new Runnable() {
                @Override
                public void run() {
                    GoogleDriveHelper googleDriveHelper = new GoogleDriveHelper(BaseActivity.this);
                    googleDriveHelper.autoBackup();
                }
            });
            thread.start();
        }

        Prefs.get().setTrainingsCount(Prefs.get().getTrainingsCount() + 1);
        getFragmentManager().beginTransaction().replace(R.id.content, new StartTrainingFragment()).commit();
        db.close();
        listButtons[0] = getResources().getString(
                R.string.startTrainingButtonString);
        adapter.notifyDataSetChanged();
        getActionBar().setSubtitle(null);
        BackupManager bm = new BackupManager(this);
        bm.dataChanged();
        showAd();
    }

    private void showAd() { //TODO AD
        Log.e("log", "state: " + interstitial.getState().toString());
        if (!Prefs.get().getAdsRemoved()){
            if (interstitial.getState() ==IMInterstitial.State.READY){
                interstitial.show();
                interstitial.loadInterstitial();
            } else if (interstitial.getState() != IMInterstitial.State.LOADING) {
                interstitial.loadInterstitial();
            }

        }
    }

    @Override
    public void onStartTrainingAccepted(int id) {
        trainingFragment = new TrainingFragment();
        currentFragment = trainingFragment;
        Bundle args = new Bundle();
        args.putInt(TrainingFragment.BUNDLE_KEY_TRAINING_ID, id);
        currentFragment.setArguments(args);
        getFragmentManager().beginTransaction().replace(R.id.content, currentFragment).commit();
        setTrainingAlreadyStarted(true);
        listButtons[0] = getResources().getString(R.string.continue_training);
        adapter.notifyDataSetChanged();
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

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        onItemSelected(position);
    }

    @Override
    public void onExerciseDeleteAccepted(Exercise exercise) {
        db.deleteExercise(exercise.getId());
        exerciseListFragment.updateList(this);
        Toast.makeText(this, R.string.deleted, Toast.LENGTH_SHORT).show();
    }

    public class RandomString {

        private final Random random = new Random();

        private final char[] buf;

        public RandomString(int length) {
            if (length < 1)
                throw new IllegalArgumentException("length < 1: " + length);
            buf = new char[length];
        }

        public String nextString() {
            for (int idx = 0; idx < buf.length; ++idx)
                buf[idx] = SYMBOLS[random.nextInt(SYMBOLS.length)];
            return new String(buf);
        }

    }
}