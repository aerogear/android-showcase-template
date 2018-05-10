package com.aerogear.androidshowcase.features.push.presenters;

import com.aerogear.androidshowcase.features.push.views.PushView;
import com.aerogear.androidshowcase.mvp.presenters.BasePresenter;
import org.aerogear.mobile.core.Callback;
import org.aerogear.mobile.core.MobileCore;
import org.aerogear.mobile.push.PushService;
import org.aerogear.mobile.push.UnifiedPushConfig;
import java.util.Arrays;

import javax.inject.Inject;

/**
 * Created by tjackman on 02/05/18.
 */

public class PushPresenter extends BasePresenter<PushView> {

    @Inject
    public PushPresenter() {

    }

    public void unregister() {
        PushService pushService = MobileCore.getInstance().getService(PushService.class);
        pushService.unregisterDevice(new Callback() {
            @Override
            public void onSuccess() {
                view.unregisterSuccess();
            }

            @Override
            public void onError(Throwable error) {
                view.unregisterError(error);
            }
        });
    }

    public void register() {
        UnifiedPushConfig unifiedPushConfig = new UnifiedPushConfig();
        unifiedPushConfig.setAlias("AeroGear");
        unifiedPushConfig.setCategories(Arrays.asList("Android", "Example"));

        PushService pushService = MobileCore.getInstance().getService(PushService.class);
        pushService.registerDevice(unifiedPushConfig, new Callback() {
            @Override
            public void onSuccess() {
                view.registerSuccess();
            }

            @Override
            public void onError(Throwable error) {
                view.registerError(error);
            }
        });
    }

    public void refreshToken() {
        PushService pushService = MobileCore.getInstance().getService(PushService.class);
        pushService.refreshToken();
    }
}
