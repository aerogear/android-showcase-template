package com.feedhenry.securenativeandroidtemplate;

import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;

import butterknife.OnClick;

/**
 * Created by weili on 12/09/2017.
 */

public class BaseActivity extends AppCompatActivity {
    private String infoText;


    @OnClick(R.id.helpButton)
    public void showHelp() {
        AlertDialog.Builder builder = new AlertDialog.Builder(BaseActivity.this);
        builder.setTitle(getString(R.string.popup_title));
        builder.setCancelable(true);
        builder.setMessage(infoText);
        builder.show();
    }

    /**
     * Set the current text for the information dialog
     */
    public void setInformationTextResourceId(int resourceId) {
        infoText = getString(resourceId);
    }
}
