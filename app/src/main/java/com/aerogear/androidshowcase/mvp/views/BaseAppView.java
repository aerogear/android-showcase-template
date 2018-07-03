package com.aerogear.androidshowcase.mvp.views;

import android.app.Fragment;
import android.content.Context;
import android.support.design.widget.Snackbar;

import com.aerogear.androidshowcase.R;
import com.aerogear.androidshowcase.mvp.components.ProgressDialogHelper;

/**
 * A base implementation for the AppView interface.
 */

public abstract class BaseAppView implements AppView {

    private Fragment fragment;
    private ProgressDialogHelper progressDialogHelper;

    public BaseAppView(Fragment fragment) {
        this.fragment = fragment;
        this.progressDialogHelper = new ProgressDialogHelper();

    }

    @Override
    public void showLoading() {
        Context ctx = getContext();
        if (ctx != null) {
            this.progressDialogHelper.showProgress(ctx);
        }
    }

    @Override
    public void hideLoading() {
        this.progressDialogHelper.hideProgress();
    }

    @Override
    public void showMessage(String message) {
        Context ctx = getContext();
        if (ctx != null) {
            Snackbar snackbar = Snackbar.make(this.fragment.getActivity().getCurrentFocus(), message, Snackbar.LENGTH_SHORT)
                    .setAction("Action", null);
            snackbar.getView().setBackgroundResource(R.color.white);
            snackbar.show();
        }
    }

    @Override
    public void showMessage(int messageResId) {
        Context ctx = getContext();
        showMessage(ctx.getString(messageResId));
    }

    @Override
    public void showMessage(int messageResourceId, Object... formatArgs) {
        Context ctx = getContext();
        showMessage(ctx.getString(messageResourceId, formatArgs));
    }

    private Context getContext() {
        return this.fragment.getActivity();
    }
}
