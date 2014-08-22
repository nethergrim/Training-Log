package com.nethergrim.combogymdiary.dialogs;

import android.app.AlertDialog;
import android.app.Dialog;
import android.app.DialogFragment;
import android.content.DialogInterface;
import android.content.DialogInterface.OnClickListener;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;

import com.nethergrim.combogymdiary.Constants;
import com.nethergrim.combogymdiary.R;
import com.nethergrim.combogymdiary.tools.Prefs;
import com.yandex.metrica.Counter;

public class DialogGoToMarket extends DialogFragment implements OnClickListener {


    @Override
    public void onClick(DialogInterface dialog, int which) {

        switch (which) {
            case Dialog.BUTTON_POSITIVE:
                Prefs.get().setMarketAlreadyLeavedFeedback(true);
                try {
                    startActivity(new Intent(
                            Intent.ACTION_VIEW,
                            Uri.parse(Constants.MARKET_LINK)));
                } catch (android.content.ActivityNotFoundException anfe) {
                    startActivity(new Intent(
                            Intent.ACTION_VIEW,
                            Uri.parse(Constants.MARKET_LINK_HTTP)));
                }
                Counter.sharedInstance().reportEvent("Go to martet: YES");
                break;
            case Dialog.BUTTON_NEGATIVE:
                Prefs.get().setMarketAlreadyLeavedFeedback(true);
                break;
            case Dialog.BUTTON_NEUTRAL:
                Prefs.get().setTrainingsCount(1);
                break;
        }
    }

    public Dialog onCreateDialog(Bundle savedInstanceState) {
        AlertDialog.Builder adb = new AlertDialog.Builder(getActivity())
                .setTitle("Google Play Market")
                .setPositiveButton(R.string.yes, this)
                .setNegativeButton(R.string.no, this)
                .setNeutralButton(R.string.later, this)
                .setMessage(R.string.leave_feedback_market)
                .setIcon(android.R.drawable.btn_star_big_on);
        return adb.create();
    }

}
