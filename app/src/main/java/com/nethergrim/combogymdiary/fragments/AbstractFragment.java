package com.nethergrim.combogymdiary.fragments;

import android.app.Fragment;
import android.os.Bundle;

/**
 * Created by andrey_drobyazko on 26.09.14.
 */
public abstract class AbstractFragment extends Fragment {

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setRetainInstance(true);
    }
}
