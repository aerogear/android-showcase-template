package com.aerogear.androidshowcase.features.push.presenters;

import com.aerogear.androidshowcase.features.push.views.PushView;
import com.aerogear.androidshowcase.mvp.presenters.BasePresenter;
import org.aerogear.mobile.core.Callback;
import org.aerogear.mobile.core.MobileCore;
import org.aerogear.mobile.core.executor.AppExecutors;
import org.aerogear.mobile.core.reactive.Responder;
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
        pushService.unregisterDevice().respondOn(new AppExecutors().mainThread())
            .respondWith(new Responder<Boolean>() {
                @Override
                public void onResult(Boolean value) {
                    view.unregisterSuccess();
                }

                @Override
                public void onException(Exception exception) {
                    view.unregisterError(exception);
                }
            });
    }

    public void register() {
        UnifiedPushConfig unifiedPushConfig = new UnifiedPushConfig();
        unifiedPushConfig.setAlias("AeroGear");
        unifiedPushConfig.setCategories(Arrays.asList("Android", "Example"));

        PushService pushService = MobileCore.getInstance().getService(PushService.class);
        pushService.registerDevice().respondOn(new AppExecutors().mainThread())
            .respondWith(new Responder<Boolean>() {
                @Override
                public void onResult(Boolean value) {
                    view.registerSuccess();
                }

                @Override
                public void onException(Exception exception) {
                    view.registerError(exception);
                }
            });
    }

    public void refreshToken() {
        PushService pushService = MobileCore.getInstance().getService(PushService.class);
        pushService.refreshToken();
    }
}
