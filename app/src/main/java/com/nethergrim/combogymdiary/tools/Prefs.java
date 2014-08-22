package com.nethergrim.combogymdiary.tools;

import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;

import com.nethergrim.combogymdiary.R;
import com.nethergrim.combogymdiary.activities.BaseActivity;


public class Prefs {
    private static final String KEY_ADS_REMOVED = "ads_removed";
    private static final String KEY_SUPERSET_INFO_SHOWED = "superset_info_showed";
    private final static String KEY_TRAINING_AT_PROGRESS = "training_at_progress";
    private final static String KEY_DATABASE_FILLED = "database_filled";
    private static final String KEY_DATABASE_UPDATED_TOV5 = "db_updated_to5";
    private static final String KEY_MEASURE_ITEM = "measureItem";
    private static final String KEY_CURRENT_TRAINING_ID = "current_training_id";
    private static final String KEY_TRAININGS_COUNT = "trainings_count";
    private static final String KEY_MARKET_ALREADY_LEAVED_FEEDBACK = "market_already_leaved_feedback";
    private static final String KEY_START_TIME = "start_time";
    private static final String KEY_CHECKED_POSITION = "checked_position";
    private static final String KEY_TURN_SCREEN_OFF = "toTurnOff";
    private static final String KEY_VIBRATE_ON = "vibrateOn";
    private static final String KEY_VIBRATE_LENGHT = "vibtateLenght";
    private static final String KEY_TO_NOTIFY_WITH_ALARM = "toNotifyWithSound";
    private static final String KEY_PROGRESS = "progress";
    private static final String KEY_TIMER_ON = "timerIsOn";
    private static final String KEY_RINGTONE_ALARM = "ringtone";

    private static SharedPreferences prefs;
    private static Prefs pref;

    private Prefs(Context context) {
        prefs = PreferenceManager.getDefaultSharedPreferences(context);
    }

    public static Prefs init(Context context) {
        if (pref == null) {
            pref = new Prefs(context);
        }
        return pref;
    }

    public static Prefs get() {
        return pref;
    }

    public void setAdsRemoved(boolean removed) {
        prefs.edit().putBoolean(KEY_ADS_REMOVED, removed).apply();
    }

    public boolean getAdsRemoved() {
        return prefs.getBoolean(KEY_ADS_REMOVED, false);
    }

    public void setSuperSetInfoShowed(int times) {
        prefs.edit().putInt(KEY_SUPERSET_INFO_SHOWED, times).apply();
    }

    public int getSuperSetInfoShowed() {
        return prefs.getInt(KEY_SUPERSET_INFO_SHOWED, 0);
    }

    public void setTrainingAtProgress(boolean atProgress) {
        prefs.edit().putBoolean(KEY_TRAINING_AT_PROGRESS, atProgress).apply();
    }

    public boolean getTrainingAtProgress() {
        return prefs.getBoolean(KEY_TRAINING_AT_PROGRESS, false);
    }

    public void setDatabaseFilled(boolean filled){
        prefs.edit().putBoolean(KEY_DATABASE_FILLED, filled).apply();
    }

    public boolean getDatabaseFilled(){
        return prefs.getBoolean(KEY_DATABASE_FILLED, false);
    }

    public boolean getDbUpdatedToV5(){
        return prefs.getBoolean(KEY_DATABASE_UPDATED_TOV5, false);
    }

    public void setDbUpdatedToV5(boolean updatedToV5){
        prefs.edit().putBoolean(KEY_DATABASE_UPDATED_TOV5, updatedToV5).apply();
    }

    public String getWeightMeasureType(Context context) {
        String item = prefs.getString(KEY_MEASURE_ITEM, "1");
        if (item.equals("1")) {
            return context.getResources().getStringArray(R.array.measure_items)[0];
        } else if (item.equals("2")) {
            return context.getResources().getStringArray(R.array.measure_items)[1];
        }
        return "";
    }

    public void setCurrentTrainingId(int id){
        prefs.edit().putInt(KEY_CURRENT_TRAINING_ID, id).apply();
    }

    public int getCurrentTrainingId(){
        return prefs.getInt(KEY_CURRENT_TRAINING_ID, 0);
    }

    public void setTrainingsCount(int count){
        prefs.edit().putInt(KEY_TRAININGS_COUNT, count).apply();
    }

    public int getTrainingsCount(){
        return prefs.getInt(KEY_TRAININGS_COUNT, 0);
    }

    public void setMarketAlreadyLeavedFeedback(boolean already){
        prefs.edit().putBoolean(KEY_MARKET_ALREADY_LEAVED_FEEDBACK, already).apply();
    }

    public boolean getMarketAlreadyLeavedFeedback(){
        return prefs.getBoolean(KEY_MARKET_ALREADY_LEAVED_FEEDBACK, false);
    }

    public void setStartTime(long time){
        prefs.edit().putLong(KEY_START_TIME, time).apply();
    }

    public long getStartTime(){
        return prefs.getLong(KEY_START_TIME, 0);
    }

    public void setCheckedPosition(int position){
        prefs.edit().putInt(KEY_CHECKED_POSITION, position).apply();
    }

    public int getCheckedPosition(){
        return prefs.getInt(KEY_CHECKED_POSITION, 0);
    }

    public void setTurnScreenOff(boolean turnOff){
        prefs.edit().putBoolean(KEY_TURN_SCREEN_OFF, turnOff).apply();
    }

    public boolean getTurnScreenOff(){
        return prefs.getBoolean(KEY_TURN_SCREEN_OFF, false);
    }

    public void setVibrateOn(boolean vibrate){
        prefs.edit().putBoolean(KEY_VIBRATE_ON, vibrate).apply();
    }

    public boolean getVibrateOn(){
        return prefs.getBoolean(KEY_VIBRATE_ON, true);
    }

    public void setVibrateLenght(String value){
        prefs.edit().putString(KEY_VIBRATE_LENGHT, value).apply();
    }

    public String getVibrateLenght(){
        return prefs.getString(KEY_VIBRATE_LENGHT, "2");
    }

    public void setNotifyWithSound(boolean notify){
        prefs.edit().putBoolean(KEY_TO_NOTIFY_WITH_ALARM, notify).apply();
    }

    public boolean getNotifyWithSound(){
        return prefs.getBoolean(KEY_TO_NOTIFY_WITH_ALARM, true);
    }

    public void setProgress(int progress){
        prefs.edit().putInt(KEY_PROGRESS, progress).apply();
    }

    public int getProgress(){
        return prefs.getInt(KEY_PROGRESS, 0);
    }

    public void setTimerOn(boolean timerOn){
        prefs.edit().putBoolean(KEY_TIMER_ON, timerOn).apply();
    }

    public boolean getTimerOn(){
        return prefs.getBoolean(KEY_TIMER_ON, false);
    }

    public void setRingtone(String ringtone){
        prefs.edit().putString(KEY_RINGTONE_ALARM, ringtone).apply();
    }

    public String getRingtone(){
        return prefs.getString(KEY_RINGTONE_ALARM, null);
    }
}
