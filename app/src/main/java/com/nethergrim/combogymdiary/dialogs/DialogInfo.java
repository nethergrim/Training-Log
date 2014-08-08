package com.nethergrim.combogymdiary.dialogs;

import android.app.DialogFragment;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;

import com.nethergrim.combogymdiary.R;

public class DialogInfo extends DialogFragment {


    public static final String KEY_INFO_ABOUT_APP = "5";
    public static final String KEY_INFO_ABOUT_SUPERSET = "6";

    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View v = null;
        getDialog().setTitle(R.string.info);
        if (getArguments() != null){
            if (getArguments().getBoolean(KEY_INFO_ABOUT_APP)){
                v = inflater.inflate(R.layout.dialog_info, null);
            } else if (getArguments().getBoolean(KEY_INFO_ABOUT_SUPERSET)){
                v = inflater.inflate(R.layout.dialog_info_superset, null);
                getDialog().setCancelable(true);
                getDialog().requestWindowFeature(Window.FEATURE_NO_TITLE);
            }
        }

        return v;
    }


}
