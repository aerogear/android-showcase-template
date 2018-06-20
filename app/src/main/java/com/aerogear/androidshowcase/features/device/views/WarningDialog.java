package com.aerogear.androidshowcase.features.device.views;

import android.app.AlertDialog;
import android.app.Dialog;
import android.app.DialogFragment;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.aerogear.androidshowcase.R;

public class WarningDialog extends DialogFragment {
    int SCORE_THRESHOLD;
    int trustScore;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, Bundle savedInstanceState) {
        View view = super.onCreateView(inflater, container, savedInstanceState);
        getDialog().setCanceledOnTouchOutside(false);
        return view;
    }

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        // Use the Builder class for convenient dialog construction
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        SCORE_THRESHOLD = getArguments().getInt("SCORE_THRESHOLD");
        trustScore = getArguments().getInt("trustScore");
        builder.setTitle("Warning");
        builder.setMessage("Your current device trust score " + trustScore + "% is below the specified target of " + SCORE_THRESHOLD + "%, do you want to continue or exit the app?")
                .setPositiveButton(R.string.device_exit, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        android.os.Process.killProcess(android.os.Process.myPid());
                    }
                })
                .setNegativeButton(R.string.device_continue, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        dismiss();
                    }
                });
        return builder.create();
    }
}
