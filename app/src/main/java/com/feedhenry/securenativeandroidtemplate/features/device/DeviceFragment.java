package com.feedhenry.securenativeandroidtemplate.features.device;

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

import com.feedhenry.securenativeandroidtemplate.R;
import com.feedhenry.securenativeandroidtemplate.features.device.presenters.DevicePresenter;
import com.feedhenry.securenativeandroidtemplate.features.device.views.DeviceView;
import com.feedhenry.securenativeandroidtemplate.features.device.views.DeviceViewImpl;
import com.feedhenry.securenativeandroidtemplate.features.device.views.WarningDialog;
import com.feedhenry.securenativeandroidtemplate.mvp.views.BaseFragment;

import org.aerogear.mobile.security.SecurityCheckResult;
import org.aerogear.mobile.security.SecurityCheckType;
import org.aerogear.mobile.security.SecurityService;

import javax.inject.Inject;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import dagger.android.AndroidInjection;

import static android.content.Context.KEYGUARD_SERVICE;

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

    @BindView(R.id.hookingDetected)
    RadioButton hookingDetected;

    @BindView(R.id.allowBackup)
    RadioButton allowBackup;

    @BindView(R.id.deviceEncrypted)
    RadioButton deviceEncrypted;

    @BindView(R.id.deviceOS)
    RadioButton deviceOS;

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

        // perform detections
        detectRoot();
        detectDeviceLock();
        detectEmulator();
        debuggerDetected();
        detectHookingFramework();
        detectBackupEnabled();
        detectDeviceEncryptionStatus();
        detectLatestOS();
        detectDeveloperOptions();

        // get trust score
        int score = getTrustScore();
        setTrustScore(score);
        checkTrustScore(score);
    }

    // tag::detectRoot[]
    /**
     * Detect if the device is rooted.
     */
    public void detectRoot() {
        totalTests++;
        SecurityCheckResult result = securityService.check(SecurityCheckType.IS_ROOTED);
        if (result.passed()) {
            setDetected(rootAccess, R.string.root_detected_positive);
        }
    }
    // end::detectRoot[]

    // tag::detectDeviceLock[]
    /**
     * Detect if the device has a lock screen setup (pin, password etc).
     */
    public void detectDeviceLock() {
        totalTests++;
        SecurityCheckResult result = securityService.check(SecurityCheckType.SCREEN_LOCK_ENABLED);
        if (!result.passed()) {
            setDetected(lockScreenSetup, R.string.device_lock_detected_negative);
        }
    }
    // end::detectDeviceLock[]

    // tag::debuggerDetected[]
    /**
     * Detect if a debugger is attached to the application.
     */
    public void debuggerDetected() {
        totalTests++;
        SecurityCheckResult result = securityService.check(SecurityCheckType.IS_DEBUGGER);
        if (result.passed()) {
            setDetected(debuggerAccess, R.string.debugger_detected_positive);
        }
    }
    // end::debuggerDetected[]

    // tag::detectEmulator[]
    /**
     * Detect if the application is being run in an emulator.
     */
    public void detectEmulator() {
        totalTests++;
        SecurityCheckResult result = securityService.check(SecurityCheckType.IS_EMULATOR);
        if (result.passed()) {
            setDetected(emulatorAccess, R.string.emulator_detected_positive);
        }
    }
    // end::detectEmulator[]

    // tag::detectHookingFramework[]
    /**
     * Detect if a hooking framework application is installed on the device
     */
    public void detectHookingFramework() {
        totalTests++;
        String xposedPackageName = "de.robv.android.xposed.installer";
        String substratePackageName = "com.saurik.substrate";

        if (checkAppInstalled(xposedPackageName) || checkAppInstalled(substratePackageName)) {
            setDetected(hookingDetected, R.string.hooking_detected_positive);
        }
    }
    // end::detectHookingFramework[]

    /**
     * Function to check if an app is installed on the device based on a package name.
     *
     * @param packageName - the package name to check
     * @return boolean
     */
    public boolean checkAppInstalled(String packageName) {
        try {
            PackageManager packageManager = context.getPackageManager();
            packageManager.getPackageInfo(packageName, 0);
            return true;
        } catch (PackageManager.NameNotFoundException e) {
            return false;
        }
    }

    // tag::detectBackupEnabled[]
    /**
     * Function to check if the backup flag is enabled in the application manifest file
     */
    public void detectBackupEnabled() {
        totalTests++;
        SecurityCheckResult result = securityService.check(SecurityCheckType.ALLOW_BACKUP_ENABLED);
        if (result.passed()) {
            setDetected(allowBackup, R.string.allow_backup_detected_positive);
        }
    }
    // end::detectBackupEnabled[]

    // tag::detectDeviceEncryptionStatus[]
    /**
     * Function to check if the devices filesystem is encrypted
     */
    public void detectDeviceEncryptionStatus() {
        totalTests++;
        SecurityCheckResult result = securityService.check(SecurityCheckType.HAS_ENCRYPTION_ENABLED);
        if (!result.passed()) {
            setDetected(deviceEncrypted, R.string.device_encrypted_negative);
        }
    }
    // end::detectDeviceEncryptionStatus[]

    // tag::detectLatestOS[]
    /**
     * Function to check if the device is running the latest Android OS
     */
    public void detectLatestOS() {
        // todo: find if there is a better way to define what the latest android version is
        int latestOsApiLevel = Build.VERSION_CODES.M;
        totalTests++;

        if (Build.VERSION.SDK_INT < latestOsApiLevel) {
            setDetected(deviceOS, R.string.device_os_latest_negative);
        }
    }
    // end::detectLatestOS[]

    // tag::detectDeveloperOptions[]
    /**
     * Detect if the developer options mode is enabled on the device
     */
    public void detectDeveloperOptions() {
        totalTests++;
        SecurityCheckResult result = securityService.check(SecurityCheckType.IS_DEVELOPER_MODE);
        if (result.passed()) {
            setDetected(developerOptions, R.string.developer_options_positive);
        }
    }
    // end::detectDeveloperOptions[]

    /**
     * Function to allow updates to the radio buttons UI
     *
     * @param uiElement    - the UI element to update
     * @param textResource - the text resource to set the updates text for
     */
    public void setDetected(RadioButton uiElement, int textResource) {
        totalTestFailures++;
        uiElement.setText(textResource);
        uiElement.setTextColor(getResources().getColor(R.color.colorPrimary));
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
