package com.feedhenry.securenativeandroidtemplate.mvp.presenters;


import com.feedhenry.securenativeandroidtemplate.mvp.views.AppView;

/**
 * Interface representing a Presenter in a model-view-presenter (MVP) pattern.
 * In most cases, the presenter is bound with a view. Given that the view will have its life cycles, the presenter needs to be aware of the view's life cycle.
 */

public interface Presenter<VIEW extends AppView> {

    /**
     * Attach the view to the presenter
     * @param view the view to attach
     */
    void attachView(VIEW view);

    /**
     * detach the view from the presenter
     */
    void detachView();

    /**
     * Method that control the lifecycle of the view. It should be called in the view's
     * (Activity or Fragment) onResume() method.
     */
    void resume();

    /**
     * Method that control the lifecycle of the view. It should be called in the view's
     * (Activity or Fragment) onPause() method.
     */
    void pause();

}
