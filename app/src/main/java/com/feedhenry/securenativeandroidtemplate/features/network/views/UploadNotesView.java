package com.feedhenry.securenativeandroidtemplate.features.network.views;

import com.feedhenry.securenativeandroidtemplate.mvp.views.AppView;

/**
 * Created by weili on 27/10/2017.
 */

public interface UploadNotesView extends AppView {

    public void renderNotesMessage(long numberOfNotes);

    public void renderPermissionMessage(boolean hasPerm, String requiredRole);

    public void showProgressBar();

    public void updateProgress(long completed, long total);

    public void updateProgressDesc(String progressDesc);

    public void hideProgressBar();
}
