package com.nethergrim.combogymdiary;

import android.app.Application;
import android.content.Context;

import com.nethergrim.combogymdiary.storage.DB;
import com.nethergrim.combogymdiary.tools.Prefs;
import com.yandex.metrica.Counter;

public class MyApp extends Application {

    public static int density;
    public static Context context;

    @Override
    public void onCreate() {
        super.onCreate();
        context = this;
        DB.init(this);
        Counter.initialize(getApplicationContext());
        Counter.sharedInstance().setTrackLocationEnabled(false);
        Prefs.init(getApplicationContext());
        Constants.getPartsOfBodyRealNames(this);
        density = (int) getResources().getDisplayMetrics().density;
    }

    public static void reopenDB(){
        DB.get().close();
        DB.init(context);
    }
}
