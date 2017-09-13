package com.feedhenry.securenativeandroidtemplate;

import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;

import butterknife.OnClick;

/**
 * Created by weili on 12/09/2017.
 */

public class BaseActivity extends AppCompatActivity {
    private String infoText;


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu_actions, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            // action with ID action_refresh was selected
            case R.id.action_help:
                showHelp();
                break;
            default:
                break;
        }

        return true;
    }

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
