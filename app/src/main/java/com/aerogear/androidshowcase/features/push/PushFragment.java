package com.aerogear.androidshowcase.features.push;

import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import com.aerogear.androidshowcase.R;
import com.aerogear.androidshowcase.features.push.presenters.PushPresenter;
import com.aerogear.androidshowcase.features.push.views.PushView;
import com.aerogear.androidshowcase.features.push.views.PushViewImpl;
import com.aerogear.androidshowcase.handler.NotificationBarMessageHandler;
import com.aerogear.androidshowcase.mvp.views.BaseFragment;

import org.aerogear.mobile.push.MessageHandler;
import org.aerogear.mobile.push.PushService;
import org.aerogear.mobile.push.UnifiedPushMessage;

import java.util.ArrayList;
import java.util.Map;

import javax.inject.Inject;

import butterknife.BindView;
import butterknife.ButterKnife;
import dagger.android.AndroidInjection;

/**
 * Created by tjackman on 02/05/18.
 */

public class PushFragment extends BaseFragment implements MessageHandler {

    @Inject
    PushPresenter pushPresenter;

    @Inject
    Context context;

    @BindView(R.id.messages)
    ListView messageList;

    public static final String TAG = "Push";

    private ArrayAdapter<String> adapter;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_push, container, false);

        ButterKnife.bind(this, view);

        adapter = new ArrayAdapter<>(context, android.R.layout.simple_list_item_1,
                new ArrayList<>());
        messageList.setAdapter(adapter);

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
        this.pushPresenter = null;
    }

    @Override
    protected PushPresenter initPresenter() {
        return pushPresenter;
    }

    @Override
    protected PushView initView() {
        return new PushViewImpl(this) {
        };
    }


    @Override
    public void onStart() {
        super.onStart();
        PushService.registerMainThreadHandler(this);
        PushService.unregisterBackgroundThreadHandler(NotificationBarMessageHandler.getInstance());
    }

    @Override
    public void onStop() {
        super.onStop();
        PushService.unregisterMainThreadHandler(this);
        PushService.registerBackgroundThreadHandler(NotificationBarMessageHandler.getInstance());
    }

    @Override
    public void onMessage(Context context, Map<String, String> message) {
        adapter.add(message.get(UnifiedPushMessage.MESSAGE));
    }

}