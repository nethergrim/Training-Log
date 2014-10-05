package com.nethergrim.combogymdiary.dialogs;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.DialogFragment;
import android.content.DialogInterface;
import android.content.DialogInterface.OnClickListener;
import android.os.Bundle;

import com.nethergrim.combogymdiary.Constants;
import com.nethergrim.combogymdiary.storage.DB;
import com.nethergrim.combogymdiary.R;
import com.nethergrim.combogymdiary.model.Exercise;

public class DialogUniversalApprove extends DialogFragment implements
        OnClickListener {

    private int type_of_dialog = 0;
    private int id = 0;
    private OnDeleteExerciseCallback listener;
    public static final int TYPE_DELETE_EXERCISE = 1;
    public static final int TYPE_START_WORKOUT = 0;
    private Exercise exercise;

    public Dialog onCreateDialog(Bundle savedInstanceState) {
        DB db = new DB(getActivity());
        db.open();
        AlertDialog.Builder adb = null;
        Bundle args = getArguments();
        if (args != null) {
            type_of_dialog = args.getInt(Constants.TYPE_OF_DIALOG);
        }
        if (type_of_dialog == TYPE_START_WORKOUT) {
            id = args.getInt(Constants._ID);
            String tra_name = db.getTrainingName(id);
            adb = new AlertDialog.Builder(getActivity())
                    .setTitle(R.string.start_training)
                    .setPositiveButton(R.string.yes, this)
                    .setNegativeButton(R.string.no, this)
                    .setMessage(
                            getResources().getString(R.string.start_training)
                                    + ": " + tra_name + " ?"
                    );
        } else if (type_of_dialog == TYPE_DELETE_EXERCISE) {
            id = args.getInt(Constants._ID);
            exercise = db.fetchtExercise(id);
            adb = new AlertDialog.Builder(getActivity())
                    .setTitle(R.string.delete_exe)
                    .setPositiveButton(R.string.yes, this)
                    .setNegativeButton(R.string.no, this)
                    .setMessage(
                            getResources().getString(R.string.delete_exe)
                                    + ": " + exercise.getName() + " ?"
                    );

        } else
            dismiss();

        return adb.create();
    }

    public void onClick(DialogInterface dialog, int which) {
        switch (which) {
            case Dialog.BUTTON_POSITIVE:
                if (type_of_dialog == TYPE_START_WORKOUT) {
//                    mListener.onStartTrainingAccepted(id);
                } else if (type_of_dialog == TYPE_DELETE_EXERCISE) {
                    listener.onExerciseDeleteAccepted(exercise);
                }
                dismiss();
                break;
            case Dialog.BUTTON_NEGATIVE:
                dismiss();
                break;
        }
    }

    public void onCancel(DialogInterface dialog) {
        super.onCancel(dialog);
    }

    @Override
    public void onAttach(Activity activity) {
        listener = (OnDeleteExerciseCallback) activity;
        super.onAttach(activity);
    }

    @Override
    public void onDetach() {
//        mListener = null;
        listener = null;
        super.onDetach();
    }

    public static interface OnDeleteExerciseCallback {
        public void onExerciseDeleteAccepted(Exercise exercise);
    }

    public static interface OnStartTrainingAccept {
        public void onStartTrainingAccepted(int trainingId);
    }

}
