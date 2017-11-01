package com.feedhenry.securenativeandroidtemplate.mvp.views;

/**
 * Interface to represent a View that will load data.
 * The data loading should happen in a backgroun thread.
 * The View will show/hide the progress indicator accordingly.
 */

public interface AppView {

    /**
     * Show the progress bar to indicate the loading is in progress
     */
    void showLoading();

    /**
     * Hide the progress bar
     */
    void hideLoading();

    /**
     * Show a message using snackbar
     * @param message the message to show
     */
    void showMessage(String message);

    /**
     * Show a message using snackbar
     * @param messageResourceId the resource id of the message
     */
    void showMessage(int messageResourceId);

    /**
     * Show a message using snackbar
     * @param messageResourceId the resource id of the message
     * @param formatArgs format arguments
     */
    void showMessage(int messageResourceId, Object... formatArgs);
}
