package com.aerogear.androidshowcase.features.http;

import java.util.List;

import com.aerogear.androidshowcase.BR;
import com.aerogear.androidshowcase.domain.models.User;
import com.aerogear.androidshowcase.features.http.presenters.HttpViewPresenter;
import com.aerogear.androidshowcase.features.http.views.HttpView;
import com.aerogear.androidshowcase.features.http.views.HttpViewImpl;
import com.aerogear.androidshowcase.mvp.views.BaseFragment;
import com.github.nitrico.lastadapter.LastAdapter;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import android.app.Activity;
import android.content.Context;
import android.databinding.ObservableArrayList;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import org.aerogear.mobile.core.MobileCore;
import org.aerogear.mobile.core.executor.AppExecutors;
import org.aerogear.mobile.core.http.HttpRequest;
import org.aerogear.mobile.core.reactive.Responder;
import com.aerogear.androidshowcase.R;


import javax.inject.Inject;

import butterknife.BindView;
import butterknife.ButterKnife;
import dagger.android.AndroidInjection;

public class HttpFragment extends BaseFragment <HttpViewPresenter, HttpView>{

    public static final String TAG = "HttpFragment";

    @Inject
    HttpViewPresenter httpViewPresenter;

    @BindView(R.id.userList)
    RecyclerView userList;

    @Inject
    Context context;

    View view;

    private final String exampleJsonEndpoint = "https://jsonplaceholder.typicode.com/users";
    private ObservableArrayList<User> users = new ObservableArrayList<>();

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        // Inflate the layout for this fragment
        view = inflater.inflate(R.layout.fragment_http, container, false);
        ButterKnife.bind(this, view);

        sendRequest();

        // Inflate the layout for this fragment
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
        this.httpViewPresenter = null;
    }

    @Override
    protected HttpViewPresenter initPresenter() {
        return httpViewPresenter;
    }

    @Override
    protected HttpView initView() {
        return new HttpViewImpl(this) {};
    }


    @Override
    public int getHelpMessageResourceId() {
        return R.string.popup_http_fragment;
    }

    public void sendRequest() {
        userList.setLayoutManager(new LinearLayoutManager(context));

        new LastAdapter(users, BR.user).map(User.class, R.layout.item_http).into(userList);

        HttpRequest httpRequest = MobileCore.getInstance().getHttpLayer().newRequest();

        httpRequest.get(exampleJsonEndpoint).map((response) -> {
            String stringBody = response.stringBody();
            List<User> retrievedUsers = new Gson().fromJson(stringBody,
                    new TypeToken<List<User>>() {}.getType());
            return retrievedUsers;
        }).respondOn(new AppExecutors().mainThread()).respondWith(new Responder<List<User>>() {
            @Override
            public void onResult(List<User> retrievedUsers) {
                MobileCore.getLogger().info("Users: " + retrievedUsers.size());
                users.addAll(retrievedUsers);
            }

            @Override
            public void onException(Exception exception) {
                Log.e(TAG, exception.toString());
            }

        });
    }

}
