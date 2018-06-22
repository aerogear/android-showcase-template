package com.aerogear.androidshowcase.navigation;

import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.content.Context;
import android.support.annotation.ArrayRes;
import android.support.annotation.LayoutRes;
import android.support.annotation.Nullable;
import android.support.annotation.StringRes;
import android.support.design.widget.Snackbar;

import com.aerogear.androidshowcase.BaseActivity;
import com.aerogear.androidshowcase.MainActivity;
import com.aerogear.androidshowcase.R;
import com.aerogear.androidshowcase.domain.Constants;
import com.aerogear.androidshowcase.domain.models.Note;
import com.aerogear.androidshowcase.features.authentication.AuthenticationDetailsFragment;
import com.aerogear.androidshowcase.features.authentication.AuthenticationFragment;
import com.aerogear.androidshowcase.features.device.DeviceFragment;
import com.aerogear.androidshowcase.features.documentation.DocumentUrl;
import com.aerogear.androidshowcase.features.documentation.DocumentationFragment;
import com.aerogear.androidshowcase.features.home.HomeFragment;
import com.aerogear.androidshowcase.features.landing.LandingFragment;
import com.aerogear.androidshowcase.features.network.NetworkFragment;
import com.aerogear.androidshowcase.features.push.PushFragment;
import com.aerogear.androidshowcase.features.storage.NotesDetailFragment;
import com.aerogear.androidshowcase.features.storage.NotesListFragment;
import com.aerogear.androidshowcase.features.underconstruction.UnderConstructionFragment;
import com.aerogear.androidshowcase.mvp.views.BaseFragment;

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
    public Navigator() {

    }

    public void navigateToHomeView(BaseActivity activity) {
        HomeFragment homeView = new HomeFragment();
        loadFragment(activity, homeView, HomeFragment.TAG);
    }

    public void navigateToAuthenticationView(final BaseActivity activity) {
        if (!isConfigured("keycloak")) {
            showNotConfiguredDialog(activity, "identity management", DocumentUrl.IDENTITY_MANAGEMENT);
            return;
        }

        AuthenticationFragment authFragment = new AuthenticationFragment();
        UserPrincipal user = authService.currentUser();

        if (user != null) {
            navigateToAuthenticateDetailsView(activity, user);
        } else {
            loadFragment(activity, authFragment, AuthenticationFragment.TAG);
        }
    }

    public void navigateToAuthenticateDetailsView(final BaseActivity activity, final UserPrincipal user) {
        if (!isConfigured("keycloak")) {
            showNotConfiguredDialog(activity, "identity management", DocumentUrl.IDENTITY_MANAGEMENT);
            return;
        }

        AuthenticationDetailsFragment authDetailsView = AuthenticationDetailsFragment.forIdentityData(user);
        loadFragment(activity, authDetailsView, AuthenticationDetailsFragment.TAG);
    }

    public void navigateToStorageView(BaseActivity activity) {
        if (!isConfigured("notes-service")) {
            showNotConfiguredDialog(activity, "notes service", DocumentUrl.NOTES_SERVICE);
            return;
        }

        if (!isConfigured("keycloak")) {
            showNotConfiguredDialog(activity, "identity management", DocumentUrl.IDENTITY_MANAGEMENT);
            return;
        }


        NotesListFragment notesListView = new NotesListFragment();
        loadFragment(activity, notesListView, NotesListFragment.TAG);
    }

    public void navigateToSingleNoteView(BaseActivity activity, Note note) {
        if (!isConfigured("notes-service")) {
            showNotConfiguredDialog(activity, "notes service", DocumentUrl.NOTES_SERVICE);
            return;
        }

        if (!isConfigured("keycloak")) {
            showNotConfiguredDialog(activity, "identity management", DocumentUrl.IDENTITY_MANAGEMENT);
            return;
        }

        NotesDetailFragment noteDetails = NotesDetailFragment.forNote(note);
        loadFragment(activity, noteDetails, NotesDetailFragment.TAG);
    }

    public void navigateToDeviceView(MainActivity activity) {
        DeviceFragment deviceFragment = new DeviceFragment();
        loadFragment(activity, deviceFragment, DeviceFragment.TAG);
    }

    public void navigateToPushView(MainActivity activity) {
        if (!isConfigured("push")) {
            showNotConfiguredDialog(activity, "push", DocumentUrl.PUSH);
            return;
        }

        PushFragment pushFragment = new PushFragment();
        loadFragment(activity, pushFragment, PushFragment.TAG);
    }

    public void navigateToNetworkView(MainActivity activity) {
        if (!isConfigured("keycloak")) {
            showNotConfiguredDialog(activity, "identity management", DocumentUrl.IDENTITY_MANAGEMENT);
            return;
        }

        UserPrincipal currentUser = authService.currentUser();
        if (currentUser != null && currentUser.hasRealmRole(Constants.ACCESS_CONTROL_ROLES.ROLE_API_ACCESS)) {
            NetworkFragment networkFragment = new NetworkFragment();
            loadFragment(activity, networkFragment, NetworkFragment.TAG);
        } else {
            Snackbar.make(activity.findViewById(android.R.id.content), R.string.not_authenticated_api_access, Snackbar.LENGTH_LONG).show();
            navigateToAuthenticationView(activity);
        }
    }

    public void navigateToUnderConstructorView(MainActivity activity) {
        UnderConstructionFragment fragment = new UnderConstructionFragment();
        loadFragment(activity, fragment, UnderConstructionFragment.TAG);
    }

    public void navigateToIdentityManagementDocumentation(MainActivity activity) {
        navigateToDocumentation(activity, DocumentUrl.IDENTITY_MANAGEMENT);
    }

    public void navigateToSecurityDocumentation(MainActivity activity) {
        navigateToDocumentation(activity, DocumentUrl.DEVICE_SECURITY);
    }

    public void navigateToMetricsDocumentation(MainActivity activity) {
        navigateToDocumentation(activity, DocumentUrl.METRICS);
    }

    public void navigateToPushDocumentation(MainActivity activity) {
        navigateToDocumentation(activity, DocumentUrl.PUSH);
    }

    private void navigateToDocumentation(MainActivity activity, DocumentUrl documentUrl) {
        DocumentationFragment documentationFragment = DocumentationFragment.newInstance(documentUrl);
        loadFragment(activity, documentationFragment, documentUrl.getUrl());
    }

    public void navigateToLandingIdentityManagement(MainActivity activity) {
        navigateToLanding(
                activity,
                R.string.identity_management_landing_title,
                R.array.identity_management_landing_description
        );
    }

    public void navigateToLandingSecurity(MainActivity activity) {
        navigateToLanding(
                activity,
                R.string.security_landing_title,
                R.array.security_landing_description
        );
    }

    public void navigateToLandingPush(MainActivity activity) {
        navigateToLanding(
                activity,
                R.string.push_landing_title,
                R.array.push_landing_description
        );
    }

    public void navigateToLandingMetrics(MainActivity activity) {
        navigateToLanding(
                activity,
                R.string.metrics_landing_title,
                R.array.metrics_landing_description
        );
    }

    private void navigateToLanding(MainActivity activity, @StringRes int titleResId,
                                   @ArrayRes int descriptionResId) {
        LandingFragment landingFragment = LandingFragment.newInstance(titleResId, descriptionResId);
        loadFragment(activity, landingFragment, LandingFragment.TAG);
    }

    public void loadFragment(BaseActivity activity, BaseFragment fragment, String fragmentTag) {
        FragmentManager fm = activity.getFragmentManager();
        FragmentTransaction transaction = fm.beginTransaction();
        // create a FragmentTransaction to begin the transaction and replace the Fragment
        transaction
                .addToBackStack(null)
                .replace(R.id.frameLayout, fragment, fragmentTag)
                .commit();
    }

    public boolean canGoBack(BaseActivity activity) {
        FragmentManager fm = activity.getFragmentManager();
        return fm.getBackStackEntryCount() > 0;
    }

    public void goBack(BaseActivity activity) {
        FragmentManager fm = activity.getFragmentManager();
        fm.popBackStack();
    }


    private void showNotConfiguredDialog(BaseActivity activity, String friendlyServiceName, DocumentUrl docUrl) {
        NotAvailableDialogFragment dialog = NotAvailableDialogFragment.newInstance(friendlyServiceName);
        dialog.setGotoDocsCallback(() -> {
            gotoDocs(activity, docUrl);
            dialog.dismiss();
        });
        android.support.v4.app.FragmentManager fm = activity.getSupportFragmentManager();
        dialog.show(fm, friendlyServiceName);

    }

    private void gotoDocs(BaseActivity activity, DocumentUrl docUrl) {
        DocumentationFragment fragment = DocumentationFragment.newInstance(docUrl);
        loadFragment(activity, fragment, docUrl.getUrl());
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

    public void navigateToSSODocumentation(MainActivity mainActivity) {
        gotoDocs(mainActivity, DocumentUrl.IDENTITY_MANAGEMENT_SSO);
    }
}
