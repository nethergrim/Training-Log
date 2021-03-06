package com.nethergrim.combogymdiary.dialogs;

import android.app.DialogFragment;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;

import com.nethergrim.combogymdiary.R;
import com.nethergrim.combogymdiary.activities.BaseActivity;
import com.nethergrim.combogymdiary.tools.Prefs;

public class DialogAddCommentToTraining extends DialogFragment implements
        OnClickListener {

    private EditText et;

    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        getDialog().setTitle(R.string.add_comment_to_training);
        View v = inflater
                .inflate(R.layout.dialog_add_comment_to_training, null);
        et = (EditText) v.findViewById(R.id.editText1AddComment);
        Button btn = (Button) v.findViewById(R.id.buttonSave);
        btn.setOnClickListener(this);
        if (Prefs.get().getCommentToTraining().length() > 0) {
            et.setText(Prefs.get().getCommentToTraining());
        }
        return v;
    }

    @Override
    public void onClick(View v) {
        int id = v.getId();
        if (id == R.id.buttonSave) {
            Prefs.get().setCommentToTraining(et.getText().toString());
            dismiss();
        }
    }

    public void onDismiss(DialogInterface dialog) {
        super.onDismiss(dialog);
    }

    public void onCancel(DialogInterface dialog) {
        super.onCancel(dialog);
    }
}
