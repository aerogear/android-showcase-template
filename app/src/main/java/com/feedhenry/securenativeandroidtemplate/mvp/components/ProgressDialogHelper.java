package com.feedhenry.securenativeandroidtemplate.mvp.components;

import android.app.ProgressDialog;
import android.content.Context;

/**
 * A helper class to hide/show the progress dialog
 */

public class ProgressDialogHelper {
    private ProgressDialog dialog;

    private volatile int progressesCount = 0;

    public void showProgress(Context context) {
        showProgress(context, null);
    }

    public void showProgress(Context context, String message) {
        showProgress(context, message, null);
    }

    public void showProgress(Context context, String message, String title) {
        if (context == null) {
            return;
        }

        if (!inProgress()) {
            dialog = new ProgressDialog(context);
            if (message != null) dialog.setMessage(message);
            if (title != null) dialog.setTitle(title);
            dialog.setIndeterminate(true);
            dialog.setCanceledOnTouchOutside(false);
            dialog.show();
        }

        progressesCount++;
    }

    public void hideProgress() {
        progressesCount--;
        if (progressesCount <= 0) {
            if (dialog != null && dialog.isShowing()) {
                dialog.dismiss();
            }
            progressesCount = 0;
        }

    }

    private boolean inProgress() {
        return dialog != null && dialog.isShowing();
    }
}
