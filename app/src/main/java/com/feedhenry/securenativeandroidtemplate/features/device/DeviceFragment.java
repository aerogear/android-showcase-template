package com.feedhenry.securenativeandroidtemplate.features.device;

import android.app.Activity;
import android.app.KeyguardManager;
import android.app.admin.DevicePolicyManager;
import android.content.Context;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.os.Debug;
import android.provider.Settings;
import android.support.design.widget.Snackbar;
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
import com.feedhenry.securenativeandroidtemplate.mvp.views.BaseFragment;
import com.scottyab.rootbeer.RootBeer;

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

    @Inject
    DevicePresenter devicePresenter;

    @Inject
    Context context;

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
        setTrustScore();
    }

    /**
     * Detect if the device is rooted.
     */
    public void detectRoot() {
        totalTests++;
        RootBeer rootBeer = new RootBeer(context);
        if (rootBeer.isRooted()) {
            setDetected(rootAccess, R.string.root_detected_positive);
        }
    }

    /**
     * Detect if the device has a lock screen setup (pin, password etc).
     */
    public void detectDeviceLock() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            totalTests++;
            KeyguardManager keyguardManager = (KeyguardManager) context.getSystemService(KEYGUARD_SERVICE);
            if (!keyguardManager.isDeviceSecure()) {
                setDetected(lockScreenSetup, R.string.device_lock_detected_negative);
            }
        } else {
            lockScreenSetup.setVisibility(View.GONE);
        }
    }

    /**
     * Detect if a debugger is attached to the application.
     */
    public void debuggerDetected() {
        totalTests++;
        if (Debug.isDebuggerConnected()) {
            setDetected(debuggerAccess, R.string.debugger_detected_positive);
        }
    }

    /**
     * Detect if the application is being run in an emulator.
     */
    public void detectEmulator() {
        totalTests++;
        if (isEmulator()) {
            setDetected(emulatorAccess, R.string.emulator_detected_positive);
        }
    }

    /**
     * Helper function to detect if the host is an emulator
     *
     * @return boolean
     */
    private boolean isEmulator() {
        return Build.FINGERPRINT.startsWith("generic")
                || Build.FINGERPRINT.startsWith("unknown")
                || Build.MODEL.contains("google_sdk")
                || Build.MODEL.contains("Emulator")
                || Build.MODEL.contains("Android SDK built for x86")
                || Build.SERIAL == null
                || Build.MANUFACTURER.contains("Genymotion")
                || (Build.BRAND.startsWith("generic") && Build.DEVICE.startsWith("generic"))
                || "google_sdk".equals(Build.PRODUCT);
    }

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

    /**
     * Function to check if the backup flag is enabled in the application manifest file
     */
    public void detectBackupEnabled() {
        totalTests++;
        try {
            PackageInfo packageInfo;
            packageInfo = context.getPackageManager().getPackageInfo(context.getPackageName(), 0);
            if ((packageInfo.applicationInfo.flags & ApplicationInfo.FLAG_ALLOW_BACKUP) != 0) {
                setDetected(allowBackup, R.string.allow_backup_detected_positive);
            }
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        }
    }

    /**
     * Function to check if the devices filesystem is encrypted
     */
    public void detectDeviceEncryptionStatus() {
        if (Build.VERSION.SDK_INT >= 11) {
            totalTests++;
            final DevicePolicyManager policyManager = (DevicePolicyManager) context.getSystemService(Context.DEVICE_POLICY_SERVICE);
            if (policyManager != null) {
                int isEncrypted = policyManager.getStorageEncryptionStatus();
                if (isEncrypted != DevicePolicyManager.ENCRYPTION_STATUS_ACTIVE) {
                    setDetected(deviceEncrypted, R.string.device_encrypted_negative);
                }
            }
        }
    }

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

    public void detectDeveloperOptions() {
        totalTests++;
        int devOptionsEnabled = Settings.Secure.getInt(context.getContentResolver(),
                Settings.Global.DEVELOPMENT_SETTINGS_ENABLED,
                0);

        if (devOptionsEnabled > 0) {
            setDetected(developerOptions, R.string.developer_options_positive);
        }
    }

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

    /**
     * Set the trust score colouring as an indicator
     */
    public void setTrustScore() {
        int score = 100 - Math.round(((totalTestFailures / totalTests) * 100));
        trustScore.setProgress(score);
        trustScoreText.setText(score + "%");
        trustScoreHeader.setText(getText(R.string.trust_score_header_title) + "\n(" + Math.round(totalTests) + " Tests)");

        // change the score percentage colour depending on the trust score
        if (trustScore.getProgress() == 100) {
            trustScoreHeader.setBackgroundColor(getResources().getColor(R.color.green));
            trustScoreText.setBackgroundColor(getResources().getColor(R.color.green));
        }
    }
}
