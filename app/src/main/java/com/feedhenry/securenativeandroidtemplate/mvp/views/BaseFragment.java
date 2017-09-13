package com.feedhenry.securenativeandroidtemplate.mvp.views;

import android.app.Fragment;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.View;

import com.feedhenry.securenativeandroidtemplate.mvp.presenters.Presenter;

/**
 * Base fragment. It implements the methods to make sure the presenter and view are initialised correctly, make sure the presenter will get notified about various life cycle events of the fragment.
 */

public abstract class BaseFragment<PRESENTER extends Presenter<VIEW>, VIEW extends AppView> extends Fragment {
    protected PRESENTER presenter;

    protected VIEW view;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        presenter = initPresenter();
        view = initView();
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        presenter.attachView(this.view);
    }

    @Override
    public void onResume() {
        super.onResume();
        presenter.resume();
    }

    @Override
    public void onPause() {
        super.onPause();
        presenter.pause();
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        presenter.detachView();
    }

    protected abstract PRESENTER initPresenter();

    protected abstract VIEW initView();

    public abstract int getHelpMessageResourceId();
}
