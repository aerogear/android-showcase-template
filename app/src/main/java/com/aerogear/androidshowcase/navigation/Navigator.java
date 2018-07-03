package com.aerogear.androidshowcase.navigation;

import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.content.Context;
import android.support.annotation.ArrayRes;
import android.support.annotation.Nullable;
import android.support.annotation.StringRes;
import android.support.v7.widget.Toolbar;

import com.aerogear.androidshowcase.BaseActivity;
import com.aerogear.androidshowcase.MainActivity;
import com.aerogear.androidshowcase.R;
import com.aerogear.androidshowcase.features.authentication.AuthenticationDetailsFragment;
import com.aerogear.androidshowcase.features.authentication.AuthenticationFragment;
import com.aerogear.androidshowcase.features.device.DeviceFragment;
import com.aerogear.androidshowcase.features.documentation.DocumentUrl;
import com.aerogear.androidshowcase.features.documentation.DocumentationFragment;
import com.aerogear.androidshowcase.features.home.HomeFragment;
import com.aerogear.androidshowcase.features.landing.LandingFragment;
import com.aerogear.androidshowcase.features.push.PushFragment;
import com.aerogear.androidshowcase.features.underconstruction.UnderConstructionFragment;
import com.aerogear.androidshowcase.mvp.views.BaseFragment;
import com.aerogear.androidshowcase.providers.PushServiceProvider;

import org.aerogear.mobile.auth.AuthService;
import org.aerogear.mobile.auth.user.UserPrincipal;
import org.aerogear.mobile.core.MobileCore;
import org.aerogear.mobile.core.configuration.ServiceConfiguration;

import javax.inject.Inject;

/**
 * A class to control the navigation of the app.
 */
public class Navigator {

    @Inject
    Context context;

    @Inject
    @Nullable
    AuthService authService;

    @Inject
    @Nullable
    PushServiceProvider pushServiceProvider;

    @Inject
    public Navigator() {

    }

    public void navigateToHomeView(BaseActivity activity, String title) {
        HomeFragment homeView = new HomeFragment();
        loadFragment(activity, homeView, HomeFragment.TAG, title);
    }

    public void navigateToAuthenticationView(final BaseActivity activity, String authViewTitle, String authDetailsTitle) {
        if (!isConfigured("keycloak")) {
            showNotConfiguredDialog(activity, "identity management", DocumentUrl.IDENTITY_MANAGEMENT, authViewTitle);
            return;
        }

        AuthenticationFragment authFragment = new AuthenticationFragment();
        UserPrincipal user = authService.currentUser();

        if (user != null) {
            navigateToAuthenticateDetailsView(activity, user, authDetailsTitle);
        } else {
            loadFragment(activity, authFragment, AuthenticationFragment.TAG, authViewTitle);
        }
    }

    public void navigateToAuthenticateDetailsView(final BaseActivity activity, final UserPrincipal user, String title) {
        if (!isConfigured("keycloak")) {
            showNotConfiguredDialog(activity, "identity management", DocumentUrl.IDENTITY_MANAGEMENT, title);
            return;
        }

        AuthenticationDetailsFragment authDetailsView = AuthenticationDetailsFragment.forIdentityData(user);
        loadFragment(activity, authDetailsView, AuthenticationDetailsFragment.TAG, title);
    }

    public void navigateToDeviceView(MainActivity activity, String title) {
        DeviceFragment deviceFragment = new DeviceFragment();
        loadFragment(activity, deviceFragment, DeviceFragment.TAG, title);
    }

    public void navigateToPushView(MainActivity activity, String title) {
        if (!isConfigured("push")) {
            showNotConfiguredDialog(activity, "push", DocumentUrl.PUSH, title);
            return;
        }
        if (pushServiceProvider != null && pushServiceProvider.getRegistrationException() != null) {
            showPushRegistrationFailedDialog(activity, "push", pushServiceProvider.getRegistrationException());
            return;
        }

        PushFragment pushFragment = new PushFragment();
        loadFragment(activity, pushFragment, PushFragment.TAG, title);
    }

    public void navigateToUnderConstructorView(MainActivity activity, String title) {
        UnderConstructionFragment fragment = new UnderConstructionFragment();
        loadFragment(activity, fragment, UnderConstructionFragment.TAG, title);
    }

    public void navigateToIdentityManagementDocumentation(MainActivity activity, String title) {
        navigateToDocumentation(activity, DocumentUrl.IDENTITY_MANAGEMENT, title);
    }

    public void navigateToSecurityDocumentation(MainActivity activity, String title) {
        navigateToDocumentation(activity, DocumentUrl.DEVICE_SECURITY, title);
    }

    public void navigateToMetricsDocumentation(MainActivity activity, String title) {
        navigateToDocumentation(activity, DocumentUrl.METRICS, title);
    }

    public void navigateToPushDocumentation(MainActivity activity, String title) {
        navigateToDocumentation(activity, DocumentUrl.PUSH, title);
    }

    private void navigateToDocumentation(MainActivity activity, DocumentUrl documentUrl, String title) {
        DocumentationFragment documentationFragment = DocumentationFragment.newInstance(documentUrl);
        loadFragment(activity, documentationFragment, documentUrl.getUrl(),
                title);
    }

    public void navigateToLandingIdentityManagement(MainActivity activity, String title) {
        navigateToLanding(
                activity,
                R.string.identity_management_landing_title,
                R.array.identity_management_landing_description,
                title
        );
    }

    public void navigateToLandingSecurity(MainActivity activity, String title) {
        navigateToLanding(
                activity,
                R.string.security_landing_title,
                R.array.security_landing_description,
                title
        );
    }

    public void navigateToLandingPush(MainActivity activity, String title) {
        navigateToLanding(
                activity,
                R.string.push_landing_title,
                R.array.push_landing_description,
                title
        );
    }

    public void navigateToLandingMetrics(MainActivity activity, String title) {
        navigateToLanding(
                activity,
                R.string.metrics_landing_title,
                R.array.metrics_landing_description
                ,
                title
        );
    }

    private void navigateToLanding(MainActivity activity, @StringRes int titleResId,
                                   @ArrayRes int descriptionResId, String title) {
        LandingFragment landingFragment = LandingFragment.newInstance(titleResId, descriptionResId);
        loadFragment(activity, landingFragment, LandingFragment.TAG, title);
    }

    public void loadFragment(BaseActivity activity, BaseFragment fragment, String fragmentTag, String title) {
        FragmentManager fm = activity.getFragmentManager();
        FragmentTransaction transaction = fm.beginTransaction();
        // create a FragmentTransaction to begin the transaction and replace the Fragment
        transaction
                .addToBackStack(null)
                .replace(R.id.frameLayout, fragment, fragmentTag)
                .commit();
        ((Toolbar)activity.findViewById(R.id.toolbar)).setTitle(title);
    }

    public boolean canGoBack(BaseActivity activity) {
        FragmentManager fm = activity.getFragmentManager();
        return fm.getBackStackEntryCount() > 0;
    }

    public void goBack(BaseActivity activity) {
        FragmentManager fm = activity.getFragmentManager();
        fm.popBackStack();
    }


    private void showNotConfiguredDialog(BaseActivity activity, String friendlyServiceName, DocumentUrl docUrl, String title) {
        NotAvailableDialogFragment dialog = NotAvailableDialogFragment.newInstance(friendlyServiceName);
        dialog.setGotoDocsCallback(() -> {
            gotoDocs(activity, docUrl, title);
            dialog.dismiss();
        });
        android.support.v4.app.FragmentManager fm = activity.getSupportFragmentManager();
        dialog.show(fm, friendlyServiceName);

    }

    private void showPushRegistrationFailedDialog(BaseActivity activity, String friendlyServiceName, Exception registrationException) {
        PushRegistrationFailedDialogFragment dialog = PushRegistrationFailedDialogFragment.newInstance(registrationException);
        android.support.v4.app.FragmentManager fm = activity.getSupportFragmentManager();
        dialog.show(fm, friendlyServiceName);

    }

    private void gotoDocs(BaseActivity activity, DocumentUrl docUrl, String title) {
        DocumentationFragment fragment = DocumentationFragment.newInstance(docUrl);
        loadFragment(activity, fragment, docUrl.getUrl(), title);
    }

    private boolean isConfigured(String serviceId) {
        MobileCore core = MobileCore.getInstance();
        ServiceConfiguration configuration = core
                .getServiceConfigurationByType(serviceId);

        //Some services (keycloak, push) are singleton and by type
        //Custom Service connectors are looked up by id.
        //So we check both for null.
        if (configuration == null) {
            configuration = core.getServiceConfigurationById(serviceId);
        }

        return configuration != null;
    }

    public void navigateToSSODocumentation(MainActivity mainActivity, String title) {
        gotoDocs(mainActivity, DocumentUrl.IDENTITY_MANAGEMENT_SSO, title);
    }

    public void navigateToSelfSignedCertificateDocumentation(MainActivity mainActivity, String title) {
        gotoDocs(mainActivity, DocumentUrl.SELF_SIGNED_DOCS, title);
    }
}
