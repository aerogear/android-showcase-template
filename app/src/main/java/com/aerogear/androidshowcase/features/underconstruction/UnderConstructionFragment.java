package com.aerogear.androidshowcase.features.underconstruction;

import android.app.Activity;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.aerogear.androidshowcase.R;
import com.aerogear.androidshowcase.features.push.presenters.PushPresenter;
import com.aerogear.androidshowcase.features.underconstruction.presenters.UnderConstructionPresenter;
import com.aerogear.androidshowcase.features.underconstruction.views.UnderConstructionViewImpl;
import com.aerogear.androidshowcase.mvp.presenters.Presenter;
import com.aerogear.androidshowcase.mvp.views.AppView;
import com.aerogear.androidshowcase.mvp.views.BaseFragment;

import javax.inject.Inject;

import butterknife.ButterKnife;
import dagger.android.AndroidInjection;

public class UnderConstructionFragment extends BaseFragment {

    public static final String TAG = "UnderConstruction";

    @Inject
    UnderConstructionPresenter underConstructionPresenter;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_under_construction, container, false);

        ButterKnife.bind(this, view);

        return view;
    }

    @Override
    public void onAttach(Activity activity) {
        AndroidInjection.inject(this);
        super.onAttach(activity);
    }

    @Override
    public void onDetach() {
        super.onDetach();
        this.underConstructionPresenter = null;
    }

    @Override
    protected Presenter initPresenter() {
        return underConstructionPresenter;
    }

    @Override
    protected AppView initView() {
        return new UnderConstructionViewImpl(this) {
        };
    }

}
