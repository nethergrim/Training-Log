package com.nethergrim.combogymdiary.fragments;

import android.app.Fragment;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.nethergrim.combogymdiary.storage.DB;


/**
 * Created by andrey_drobyazko on 26.09.14.
 */
public abstract class AbstractFragment extends Fragment {

    protected DB db;
    protected Handler handler;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setRetainInstance(true);
        db = DB.get();
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        handler = new Handler();
        return super.onCreateView(inflater, container, savedInstanceState);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        db = null;
        handler = null;
    }

    protected void postInUiThread(Runnable runnable){
        if (handler != null){
            handler.post(runnable);
        }
    }
}
