package com.aerogear.androidshowcase;

import android.app.AlertDialog;
import android.app.Dialog;
import android.app.DialogFragment;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

public class CertificateErrorDialog extends DialogFragment {


    private Runnable gotoDocs;

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
        builder.setTitle("Certificate Error");
        builder.setMessage("You may be using self signed certificates that will prevent the showcase from running properly.  Please review the documentation and configure your certificates.")
                .setPositiveButton("Show Documentation", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        if (gotoDocs != null) {
                            gotoDocs.run();
                        } else {
                            dismiss();
                        }
                    }
                })
                .setNegativeButton("Close", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        dismiss();
                    }
                });
        return builder.create();
    }

    public void setGotoDocs(Runnable runnable) {
        this.gotoDocs = runnable;
    }
}
