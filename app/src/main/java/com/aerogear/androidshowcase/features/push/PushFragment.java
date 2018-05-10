package com.aerogear.androidshowcase.features.push;

import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.Toast;
import com.aerogear.androidshowcase.R;
import com.aerogear.androidshowcase.features.push.presenters.PushPresenter;
import com.aerogear.androidshowcase.features.push.views.PushView;
import com.aerogear.androidshowcase.features.push.views.PushViewImpl;
import com.aerogear.androidshowcase.handler.NotificationBarMessageHandler;
import com.aerogear.androidshowcase.mvp.views.BaseFragment;
import org.aerogear.mobile.core.MobileCore;
import org.aerogear.mobile.core.executor.AppExecutors;
import org.aerogear.mobile.push.MessageHandler;
import org.aerogear.mobile.push.PushService;
import org.aerogear.mobile.push.UnifiedPushMessage;
import java.util.ArrayList;
import java.util.Map;
import javax.inject.Inject;
import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
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

    @BindView(R.id.refreshToken)
    Button refreshToken;

    @BindView(R.id.register)
    Button register;

    @BindView(R.id.unregister)
    Button unregister;

    public static final String TAG = "Push";
    private ArrayAdapter<String> adapter;
    View view;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        // Inflate the layout for this fragment
        view = inflater.inflate(R.layout.fragment_push, container, false);
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

            public void unregisterSuccess() {
                new AppExecutors().mainThread().execute(() -> registered(false));
            }

            public void unregisterError(Throwable error) {
                refreshToken.setEnabled(false);
                register.setEnabled(false);
                MobileCore.getLogger().error(error.getMessage(), error);
                new AppExecutors().mainThread().execute(() -> {
                    registered(true);
                    Toast.makeText(context, R.string.device_register_error, Toast.LENGTH_LONG)
                            .show();
                });
            }

            public void registerSuccess() {
                new AppExecutors().mainThread().execute(() -> registered(true));
            }

            public void registerError(Throwable error) {
                new AppExecutors().mainThread().execute(() -> {
                    register.setEnabled(true);
                    MobileCore.getLogger().error(TAG, error.getMessage(), error);
                    registered(false);
                    Toast.makeText(context, R.string.device_register_error, Toast.LENGTH_LONG)
                            .show();
                });
            }

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

    @OnClick(R.id.refreshToken)
    void refreshToken() {
        pushPresenter.refreshToken();
    }

    @OnClick(R.id.register)
    void register() {
        register.setEnabled(false);
        pushPresenter.register();
    }

    @OnClick(R.id.unregister)
    void unregister() {
        refreshToken.setEnabled(false);
        unregister.setEnabled(false);
        pushPresenter.unregister();
    }

    private void registered(boolean registered) {
        register.setEnabled(!registered);
        refreshToken.setEnabled(registered);
        unregister.setEnabled(registered);

        if (registered) {
            register.setTextColor(getResources().getColor(R.color.colorPrimaryDark));
            unregister.setTextColor(getResources().getColor(R.color.white));
            refreshToken.setTextColor(getResources().getColor(R.color.white));
        } else {
            register.setTextColor(getResources().getColor(R.color.white));
            unregister.setTextColor(getResources().getColor(R.color.grey));
            refreshToken.setTextColor(getResources().getColor(R.color.colorPrimaryDark));
        }
    }


    @Override
    public int getHelpMessageResourceId() {
        return R.string.popup_push_fragment;
    }
}