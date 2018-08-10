package com.aerogear.androidshowcase.features.device;

import android.app.Activity;
import android.content.Context;
import android.content.res.ColorStateList;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RadioButton;
import android.widget.TextView;
import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import com.aerogear.androidshowcase.R;
import com.aerogear.androidshowcase.features.device.presenters.DevicePresenter;
import com.aerogear.androidshowcase.features.device.views.DeviceView;
import com.aerogear.androidshowcase.features.device.views.DeviceViewImpl;
import com.aerogear.androidshowcase.features.device.views.WarningDialog;
import com.aerogear.androidshowcase.mvp.views.BaseFragment;
import dagger.android.AndroidInjection;
import java.util.Map;
import javax.inject.Inject;
import org.aerogear.mobile.core.MobileCore;
import org.aerogear.mobile.security.DeviceCheckExecutor;
import org.aerogear.mobile.security.DeviceCheckResult;
import org.aerogear.mobile.security.DeviceCheckType;
import org.aerogear.mobile.security.SecurityService;
import static org.aerogear.mobile.security.SyncDeviceCheckExecutor.Builder;

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

        securityService = new SecurityService();

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

    public void runTests() {
        totalTests = 0;
        totalTestFailures = 0;

        Map<String, DeviceCheckResult> results = buildResults();

        // perform detections

        for (DeviceCheckResult res : results.values()) {
            SecurityCheckDecorator.CheckResultSecurityDecorator decorator = (SecurityCheckDecorator.CheckResultSecurityDecorator) res;
            if (!decorator.isSecure()) {
                setCheckFailed(decorator.getRadioButton(), decorator.getUnsecureMessageResouceId());
            }
        }

        totalTests = results.size();

        // get trust score
        int score = getTrustScore();
        setTrustScore(score);
        checkTrustScore(score);
    }

    private Map<String, DeviceCheckResult> buildResults() {
        MobileCore core = MobileCore.getInstance();

        Builder builder = DeviceCheckExecutor.Builder
            .newSyncExecutor(context)
            .withSecurityCheck(SecurityCheckDecorator.forCheck(DeviceCheckType.ROOT_ENABLED).withUnsecureMessage(R.string.root_detected_positive).withRadioButton(rootAccess).secureWhenFalse(true))
            .withSecurityCheck(SecurityCheckDecorator.forCheck(DeviceCheckType.SCREEN_LOCK_ENABLED).withUnsecureMessage(R.string.device_lock_detected_negative).withRadioButton(lockScreenSetup))
            .withSecurityCheck(SecurityCheckDecorator.forCheck(DeviceCheckType.IS_EMULATOR).withUnsecureMessage(R.string.emulator_detected_positive).withRadioButton(emulatorAccess).secureWhenFalse(true))
            .withSecurityCheck(SecurityCheckDecorator.forCheck(DeviceCheckType.DEBUGGER_ENABLED).withUnsecureMessage(R.string.debugger_detected_positive).withRadioButton(debuggerAccess).secureWhenFalse(true))
            .withSecurityCheck(SecurityCheckDecorator.forCheck(DeviceCheckType.DEVELOPER_MODE_ENABLED).withUnsecureMessage(R.string.developer_options_positive).withRadioButton(developerOptions).secureWhenFalse(true))
            .withSecurityCheck(SecurityCheckDecorator.forCheck(DeviceCheckType.ENCRYPTION_ENABLED).withUnsecureMessage(R.string.device_encrypted_negative).withRadioButton(deviceEncrypted))
            .withSecurityCheck(SecurityCheckDecorator.forCheck(DeviceCheckType.BACKUP_ENABLED).withUnsecureMessage(R.string.allow_backup_detected_positive).withRadioButton(allowBackup).secureWhenFalse(true));

        if (core.getServiceConfigurationByType("metrics") != null) {
            builder.withMetricsService(core.getMetricsService());
        }

        return builder.build().execute();
    }

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
        uiElement.setTextColor(getResources().getColor(R.color.red));
        uiElement.setButtonDrawable(R.drawable.baseline_warning);
        uiElement.setButtonTintList(ColorStateList.valueOf(getResources().getColor(R.color.red)));

    }


    public int getTrustScore() {
        int score = 100 - Math.round(((totalTestFailures / totalTests) * 100));
        return score;
    }

    /**
     * Set the trust score colouring as an indicator
     */
    public void setTrustScore(int score) {
        trustScoreText.setText(score + "%");
        trustScoreHeader.setText(
            getText(R.string.trust_score_header_title) + "\n(" + Math.round(totalTests - totalTestFailures) +" out of "  + Math.round(totalTests)
                + " checks passing)");
    }

    private void checkTrustScore(int score) {
        if (score < SCORE_THRESHOLD) {
            WarningDialog warning = new WarningDialog();
            Bundle warningBundle = new Bundle();

            warningBundle.putInt("SCORE_THRESHOLD", SCORE_THRESHOLD);
            warningBundle.putInt("trustScore", score);

            warning.setArguments(warningBundle);
            warning.show(getFragmentManager(), "device_warning");
        }
    }

    @OnClick(R.id.refresh_score_btn)
    public void refreshScore() {
        runTests();
    }
}
