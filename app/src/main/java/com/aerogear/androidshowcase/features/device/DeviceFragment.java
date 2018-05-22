package com.aerogear.androidshowcase.features.device;

import android.app.Activity;
import android.content.Context;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;
import android.widget.RadioButton;
import android.widget.TextView;

import com.aerogear.androidshowcase.R;
import com.aerogear.androidshowcase.features.device.presenters.DevicePresenter;
import com.aerogear.androidshowcase.features.device.views.DeviceView;
import com.aerogear.androidshowcase.features.device.views.DeviceViewImpl;
import com.aerogear.androidshowcase.features.device.views.WarningDialog;
import com.aerogear.androidshowcase.mvp.views.BaseFragment;

import org.aerogear.mobile.core.MobileCore;
import org.aerogear.mobile.core.metrics.MetricsService;
import org.aerogear.mobile.security.SecurityCheckExecutor;
import org.aerogear.mobile.security.SecurityCheckResult;
import org.aerogear.mobile.security.SecurityCheckType;
import org.aerogear.mobile.security.SecurityService;

import java.util.Map;

import javax.inject.Inject;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import dagger.android.AndroidInjection;

/**
 * The fragment for the Device related functionality.
 */
public class DeviceFragment extends BaseFragment<DevicePresenter, DeviceView> {


    public static final String TAG = "device";

    private static final int SCORE_THRESHOLD = 70;

    @Inject
    DevicePresenter devicePresenter;

    @Inject
    Context context;

    @Inject
    SecurityService securityService;

    @BindView(R.id.rootAccess)
    RadioButton rootAccess;

    @BindView(R.id.lockScreenSetup)
    RadioButton lockScreenSetup;

    @BindView(R.id.emulatorAccess)
    RadioButton emulatorAccess;

    @BindView(R.id.debuggerAccess)
    RadioButton debuggerAccess;

    @BindView(R.id.allowBackup)
    RadioButton allowBackup;

    @BindView(R.id.deviceEncrypted)
    RadioButton deviceEncrypted;

    @BindView(R.id.developerOptions)
    RadioButton developerOptions;

    @BindView(R.id.trustScore)
    ProgressBar trustScore;

    @BindView(R.id.trustScoreText)
    TextView trustScoreText;

    @BindView(R.id.trustScoreHeader)
    TextView trustScoreHeader;

    View view;

    // Used to calculate trust store percentage
    private float totalTests = 0;
    private float totalTestFailures = 0;

    public DeviceFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        // Inflate the layout for this fragment
        view = inflater.inflate(R.layout.fragment_device, container, false);
        ButterKnife.bind(this, view);

        securityService = MobileCore.getInstance().getService(SecurityService.class);

        // run the detection tests on load
        runTests();

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
        this.devicePresenter = null;
    }

    @Override
    protected DevicePresenter initPresenter() {
        return devicePresenter;
    }


    @Override
    protected DeviceView initView() {
        return new DeviceViewImpl(this) {
        };
    }

    @Override
    public int getHelpMessageResourceId() {
        return R.string.popup_device_fragment;
    }

    public void runTests() {
        totalTests = 0;
        totalTestFailures = 0;

        Map<String, SecurityCheckResult> results = SecurityCheckExecutor.Builder
                .newSyncExecutor(context)
                .withSecurityCheck(SecurityCheckType.NOT_ROOTED)
                .withSecurityCheck(SecurityCheckType.SCREEN_LOCK_ENABLED)
                .withSecurityCheck(SecurityCheckType.NOT_IN_EMULATOR)
                .withSecurityCheck(SecurityCheckType.NO_DEBUGGER)
                .withSecurityCheck(SecurityCheckType.NO_DEVELOPER_MODE)
                .withMetricsService(
                        MobileCore.getInstance().getService(MetricsService.class))
                .build().execute();

        // perform detections
        detectRoot(results);
        detectDeviceLock(results);
        detectEmulator(results);
        debuggerDetected(results);
        detectBackupEnabled(results);
        detectDeviceEncryptionStatus(results);
        detectDeveloperOptions(results);

        // get trust score
        int score = getTrustScore();
        setTrustScore(score);
        checkTrustScore(score);
    }

    // tag::detectRoot[]
    /**
     * Detect if the device is rooted.
     */
    public void detectRoot(Map<String, SecurityCheckResult> results) {
        totalTests++;
        SecurityCheckResult result = results.get(SecurityCheckType.NOT_ROOTED.getType());
        if (result != null && !result.passed()) {
            setCheckFailed(rootAccess, R.string.root_detected_positive);
        }
    }
    // end::detectRoot[]

    // tag::detectDeviceLock[]
    /**
     * Detect if the device has a lock screen setup (pin, password etc).
     */
    public void detectDeviceLock(Map<String, SecurityCheckResult> results) {
        totalTests++;
        SecurityCheckResult result = results.get(SecurityCheckType.SCREEN_LOCK_ENABLED.getType());
        if (result != null && !result.passed()) {
            setCheckFailed(lockScreenSetup, R.string.device_lock_detected_negative);
        }
    }
    // end::detectDeviceLock[]

    // tag::debuggerDetected[]
    /**
     * Detect if a debugger is attached to the application.
     */
    public void debuggerDetected(Map<String, SecurityCheckResult> results) {
        totalTests++;
        SecurityCheckResult result = results.get(SecurityCheckType.NO_DEBUGGER.getType());
        if (result != null && !result.passed()) {
            setCheckFailed(debuggerAccess, R.string.debugger_detected_positive);
        }
    }
    // end::debuggerDetected[]

    // tag::detectEmulator[]
    /**
     * Detect if the application is being run in an emulator.
     */
    public void detectEmulator(Map<String, SecurityCheckResult> results) {
        totalTests++;
        SecurityCheckResult result = results.get(SecurityCheckType.NOT_IN_EMULATOR.getType());
        if (result != null && !result.passed()) {
            setCheckFailed(emulatorAccess, R.string.emulator_detected_positive);
        }
    }
    // end::detectEmulator[]

    // tag::detectBackupEnabled[]
    /**
     * Function to check if the backup flag is enabled in the application manifest file
     */
    public void detectBackupEnabled(Map<String, SecurityCheckResult> results) {
        totalTests++;
        SecurityCheckResult result = securityService.check(SecurityCheckType.ALLOW_BACKUP_DISABLED);
        if (result != null && !result.passed()) {
            setCheckFailed(allowBackup, R.string.allow_backup_detected_positive);
        }
    }
    // end::detectBackupEnabled[]

    // tag::detectDeviceEncryptionStatus[]

    /**
     * Function to check if the devices filesystem is encrypted
     */
    public void detectDeviceEncryptionStatus(Map<String, SecurityCheckResult> results) {
        totalTests++;
        SecurityCheckResult result =
                securityService.check(SecurityCheckType.HAS_ENCRYPTION_ENABLED);
        if (result != null && !result.passed()) {
            setCheckFailed(deviceEncrypted, R.string.device_encrypted_negative);
        }
    }
    // end::detectDeviceEncryptionStatus[]

    // tag::detectDeveloperOptions[]
    /**
     * Detect if the developer options mode is enabled on the device
     */
    public void detectDeveloperOptions(Map<String, SecurityCheckResult> results) {
        totalTests++;
        SecurityCheckResult result = results.get(SecurityCheckType.NO_DEVELOPER_MODE.getType());
        if (result != null && !result.passed()) {
            setCheckFailed(developerOptions, R.string.developer_options_positive);
        }
    }
    // end::detectDeveloperOptions[]


    /**
     * Function to allow updates to the radio buttons UI when a security check has failed Passed
     * tests do not need updating due to being the default UI state
     *
     * @param uiElement - the UI element to update
     * @param textResource - the text resource to set the updates text for
     */
    public void setCheckFailed(RadioButton uiElement, int textResource) {
        totalTestFailures++;
        uiElement.setText(textResource);
        uiElement.setTextColor(getResources().getColor(R.color.orange));
    }


    public int getTrustScore() {
        int score = 100 - Math.round(((totalTestFailures / totalTests) * 100));
        return  score;
    }

    /**
     * Set the trust score colouring as an indicator
     */
    public void setTrustScore(int score) {
        trustScore.setProgress(score);
        trustScoreText.setText(score + "%");
        trustScoreHeader.setText(getText(R.string.trust_score_header_title) + "\n(" + Math.round(totalTests) + " Tests)");

        // change the score percentage colour depending on the trust score
        if (trustScore.getProgress() == 100) {
            trustScoreHeader.setBackgroundColor(getResources().getColor(R.color.green));
            trustScoreText.setBackgroundColor(getResources().getColor(R.color.green));
        }
    }

    private void checkTrustScore(int score) {
        if (score < SCORE_THRESHOLD) {
            WarningDialog warning = new WarningDialog();
            warning.show(getFragmentManager(), "device_warning");
        }
    }

    @OnClick(R.id.refresh_score_btn)
    public void refreshScore() {
        runTests();
    }
}
