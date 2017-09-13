package com.feedhenry.securenativeandroidtemplate.features.home;


import android.app.Activity;
import android.os.Bundle;
import android.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.feedhenry.securenativeandroidtemplate.R;
import com.feedhenry.securenativeandroidtemplate.features.home.presenters.HomeViewPresenter;
import com.feedhenry.securenativeandroidtemplate.features.home.views.HomeView;
import com.feedhenry.securenativeandroidtemplate.features.home.views.HomeViewImpl;
import com.feedhenry.securenativeandroidtemplate.mvp.presenters.BasePresenter;
import com.feedhenry.securenativeandroidtemplate.mvp.presenters.Presenter;
import com.feedhenry.securenativeandroidtemplate.mvp.views.AppView;
import com.feedhenry.securenativeandroidtemplate.mvp.views.BaseAppView;
import com.feedhenry.securenativeandroidtemplate.mvp.views.BaseFragment;

import javax.inject.Inject;

import dagger.android.AndroidInjection;

/**
 * A simple {@link Fragment} subclass.
 */
public class HomeFragment extends BaseFragment<HomeViewPresenter, HomeView> {

    @Inject
    HomeViewPresenter homeViewPresenter;

    public HomeFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_home, container, false);
    }

    @Override
    public void onAttach(Activity activity) {
        AndroidInjection.inject(this);
        super.onAttach(activity);
    }

    @Override
    public void onDetach() {
        super.onDetach();
        this.homeViewPresenter = null;
    }

    @Override
    protected HomeViewPresenter initPresenter() {
        return homeViewPresenter;
    }

    @Override
    protected HomeView initView() {
        return new HomeViewImpl(this) {};
    }

    @Override
    public int getHelpMessageResourceId() {
        return R.string.popup_home_fragment;
    }
}
