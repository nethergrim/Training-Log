package com.nethergrim.combogymdiary.fragments;

import android.app.Fragment;

import com.nethergrim.combogymdiary.view.FloatingActionButton;

import java.util.ArrayList;


public abstract class FabFragment extends Fragment {

    private ArrayList<FloatingActionButton> fabs = new ArrayList<FloatingActionButton>();

    public void onDrawerEvent(boolean closed){
        for (FloatingActionButton fab : fabs) {
            try {
                if (closed) fab.show();
                else fab.hide();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    public void registerFab(FloatingActionButton fab){
        fabs.add(fab);
    }
}
