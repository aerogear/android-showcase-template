package com.aerogear.androidshowcase.features.device;

import android.content.Context;
import android.widget.RadioButton;

import org.aerogear.mobile.security.DeviceCheck;
import org.aerogear.mobile.security.DeviceCheckResult;
import org.aerogear.mobile.security.DeviceCheckType;

/**
 * This class is a decorator used to interpretate the result of a device check as a security check.
 * While the device checks simple tests if a feature is enabled or not, returning <code>true</code> if the feature is enabled,
 * this decorator tests if the device is in the most secure situation.
 * That means, for example, that while the DebuggerEnabledCheck test returns <code>true</code> if the debugger is enabled, the decorator,
 * in the same situation, returns <code>false</code>, because having debugger enabled is considered less secure.
 */
class SecurityCheckDecorator implements DeviceCheck {

    private final DeviceCheck delegate;
    private boolean secureWhenFalse = false;
    private int unsecureMessageResouceId;
    private RadioButton button;

    /**
     * Builds the decorator.
     *
     * @param delegate The original check to be decorated
     */
    private SecurityCheckDecorator(final DeviceCheck delegate) {
        this.delegate = delegate;
    }

    /**
     * Delegates the execution to the {@link #delegate} object and embeds the results into a decorated result (#CheckResultSecurityDecorator)
     * @param context the context
     * @return a decorated check result (#CheckResultSecurityDecorator)
     */
    @Override
    public DeviceCheckResult test(final Context context) {
        return new CheckResultSecurityDecorator(this, delegate.test(context));
    }

    /**
     * Returns {@link #delegate#getName()}
     * @return {@link #delegate#getName()}
     */
    @Override
    public String getName() {
        return delegate.getName();
    }

    /**
     * Returns {@link #delegate#getId()}
     * @return {@link #delegate#getId()}
     */
    @Override
    public String getId() {
        return delegate.getId();
    }

    /**
     * Create an instance of the decorator for the passed int deviceCheckType
     * @param deviceCheckType check type to be decorated
     * @return a new instance of the decorator, decorating the passed in check
     */
    public static SecurityCheckDecorator forCheck(DeviceCheckType deviceCheckType) {
        return forCheck((deviceCheckType.getDeviceCheck()));
    }

    /**
     * Create an instance of the decorator for the passed int deviceCheck
     * @param deviceCheck check to be decorated
     * @return a new instance of the decorator, decorating the passed in check
     */
    public static SecurityCheckDecorator forCheck(DeviceCheck deviceCheck) {
        return new SecurityCheckDecorator(deviceCheck);
    }

    /**
     * Set the message to be visualised into the GUI if the check results unsecure.
     * The API is fluent: other calls to <code>with</code> methods can be chained.
     *
     * @param unsecureMessageResouceId the message resource id to be used to show the message
     * @return this
     */
    public SecurityCheckDecorator withUnsecureMessage(final int unsecureMessageResouceId) {
        this.unsecureMessageResouceId = unsecureMessageResouceId;
        return this;
    }

    /**
     * Set the radio button associated with this check.
     * The API is fluent: other calls to <code>with</code> methods can be chained.
     *
     * @param radioButton the radio button associated with this check
     * @return this
     */
    public SecurityCheckDecorator withRadioButton(final RadioButton radioButton) {
        this.button = radioButton;
        return this;
    }

    /**
     * Instruct the decorator whether to return <code>true</code> when the delegate test passes or not
     * @param secureWhenFalse whether to return <code>true</code> when the delegate test passes or not
     * @return this
     */
    public SecurityCheckDecorator secureWhenFalse(boolean secureWhenFalse) {
        this.secureWhenFalse = secureWhenFalse;
        return this;
    }

    /**
     * Decorator class for check results.
     */
    static class CheckResultSecurityDecorator implements DeviceCheckResult {

        private final DeviceCheckResult delegate;
        private final SecurityCheckDecorator deviceCheck;

        /**
         * Constructor.
         *
         * @param deviceCheck the check that generated this result
         * @param delegate the not yet decorated result
         */
        private CheckResultSecurityDecorator(final SecurityCheckDecorator deviceCheck, final DeviceCheckResult delegate) {
            this.delegate = delegate;
            this.deviceCheck = deviceCheck;
        }

        /**
         * Returns {@link #delegate#getName()}
         * @return {@link #delegate#getName()}
         */
        @Override
        public String getName() {
            return delegate.getName();
        }

        /**
         * Returns {@link #delegate#getId()}
         * @return {@link #delegate#getId()}
         */
        @Override
        public String getId() {
            return delegate.getId();
        }

        /**
         * Returns the resource id to be used to show the message in case of an unsecure result.
         * @return the resource id to be used to show the message in case of an unsecure result
         */
        public int getUnsecureMessageResouceId() {
            return deviceCheck.unsecureMessageResouceId;
        }

        /**
         * Returns the radio button to be updated with the result of this check.
         * @return the radio button to be updated with the result of this check.
         */
        public RadioButton getRadioButton() {
            return deviceCheck.button;
        }

        /**
         * Returns whether the result is considered secure or not.
         * @return whether the result is considered secure or not
         */
        @Override
        public boolean passed() {
            boolean retVal = delegate.passed();

            if (deviceCheck.secureWhenFalse) {
                return !retVal;
            }

            return retVal;
        }
    }
}
