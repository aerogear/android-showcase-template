package com.aerogear.androidshowcase.features.landing;

import android.app.Activity;
import android.databinding.ObservableArrayList;
import android.databinding.ObservableList;
import android.os.Bundle;
import android.support.annotation.ArrayRes;
import android.support.annotation.LayoutRes;
import android.support.annotation.Nullable;
import android.support.annotation.StringRes;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.Html;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.aerogear.androidshowcase.BR;
import com.aerogear.androidshowcase.R;
import com.aerogear.androidshowcase.features.landing.presenters.LandingPresenter;
import com.aerogear.androidshowcase.features.landing.views.LandingViewImpl;
import com.aerogear.androidshowcase.mvp.presenters.Presenter;
import com.aerogear.androidshowcase.mvp.views.AppView;
import com.aerogear.androidshowcase.mvp.views.BaseFragment;
import com.github.nitrico.lastadapter.LastAdapter;

import java.util.Arrays;

import javax.inject.Inject;

import butterknife.BindView;
import butterknife.ButterKnife;
import dagger.android.AndroidInjection;

public class LandingFragment extends BaseFragment {

    private static final String TITLE = "title";
    private static final String DESCRIPTION = "description";

    public static final String TAG = "Landing";

    private ObservableList<String> description = new ObservableArrayList<>();

    @Inject
    LandingPresenter landingPresenter;

    @BindView(R.id.title)
    TextView mTitle;
    
    @BindView(R.id.description)
    RecyclerView mDescription;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container,
                             Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_landing, container, false);

        ButterKnife.bind(this, view);

        mDescription.setLayoutManager(new LinearLayoutManager(getActivity()));

        new LastAdapter(description, BR.description)
                .map(String.class, R.layout.item_landing)
                .into(mDescription);

        return view;
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        
        @StringRes int titleResId = getArguments().getInt(TITLE);
        mTitle.setText(getString(titleResId));
        
        @ArrayRes int descriptionResId = getArguments().getInt(DESCRIPTION);
        String[] descriptionArray = getResources().getStringArray(descriptionResId);
        description.addAll(Arrays.asList(descriptionArray));
    }

    @Override
    public void onAttach(Activity activity) {
        AndroidInjection.inject(this);
        super.onAttach(activity);
    }

    @Override
    public void onDetach() {
        super.onDetach();
        this.landingPresenter = null;
    }

    @Override
    protected Presenter initPresenter() {
        return landingPresenter;
    }

    @Override
    protected AppView initView() {
        return new LandingViewImpl(this) {
        };
    }

    public static LandingFragment newInstance(@StringRes int titleResId, 
                                              @ArrayRes int descriptionResId) {
        LandingFragment fragment = new LandingFragment();

        Bundle args = new Bundle();
        args.putInt(TITLE, titleResId);
        args.putInt(DESCRIPTION, descriptionResId);

        fragment.setArguments(args);

        return fragment;
    }

}
