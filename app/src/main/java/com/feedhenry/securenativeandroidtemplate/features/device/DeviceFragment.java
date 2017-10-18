package com.feedhenry.securenativeandroidtemplate.features.device;

import android.app.Activity;
import android.app.ActivityManager;
import android.app.KeyguardManager;
import android.content.Context;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.os.Debug;
import android.support.v4.content.ContextCompat;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RadioButton;

import com.feedhenry.securenativeandroidtemplate.R;
import com.feedhenry.securenativeandroidtemplate.features.device.presenters.DevicePresenter;
import com.feedhenry.securenativeandroidtemplate.features.device.views.DeviceView;
import com.feedhenry.securenativeandroidtemplate.features.device.views.DeviceViewImpl;
import com.feedhenry.securenativeandroidtemplate.mvp.views.BaseFragment;
import com.scottyab.rootbeer.RootBeer;

import java.io.IOException;
import java.util.List;
import java.util.zip.ZipEntry;
import java.util.zip.ZipFile;

import javax.inject.Inject;

import butterknife.BindView;
import butterknife.ButterKnife;
import dagger.android.AndroidInjection;

import static android.content.Context.ACTIVITY_SERVICE;
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

    View view;

    public DeviceFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        // Inflate the layout for this fragment
        view = inflater.inflate(R.layout.fragment_device, container, false);
        ButterKnife.bind(this, view);

        // perform detections
        detectRoot();
        detectDeviceLock();
        detectEmulator();
        debuggerDetected();
        detectHookingFramework();
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

    /**
     * Detect if the device is rooted.
     */
    public void detectRoot() {
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
        if (Debug.isDebuggerConnected()) {
            setDetected(debuggerAccess, R.string.debugger_detected_positive);
        }
    }

    /**
     * Detect if the application is being run in an emulator.
     */
    public void detectEmulator() {
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
     *
     * @param uiElement - the UI element to update
     * @param textResource - the text resource to set the updates text for
     */
    public void setDetected(RadioButton uiElement, int textResource) {
        uiElement.setText(textResource);
        uiElement.setTextColor(getResources().getColor(R.color.colorPrimary));
    }
}
