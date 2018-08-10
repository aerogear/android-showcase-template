package com.aerogear.androidshowcase.features.device;

import android.content.Context;
import android.widget.RadioButton;

import org.aerogear.mobile.security.DeviceCheck;
import org.aerogear.mobile.security.DeviceCheckResult;
import org.aerogear.mobile.security.DeviceCheckType;

class SecurityCheckDecorator implements DeviceCheck {

    private final DeviceCheck delegate;
    private final boolean secureWhenFalse;
    private final int unsecureMessageResouceId;
    private final RadioButton button;

    private SecurityCheckDecorator(final DeviceCheck delegate, final int unsecureMessageResouceId, RadioButton button, final boolean secureWhenFalse) {
        this.delegate = delegate;
        this.unsecureMessageResouceId = unsecureMessageResouceId;
        this.button = button;
        this.secureWhenFalse = secureWhenFalse;
    }

    @Override
    public DeviceCheckResult test(Context context) {
        return new CheckResultSecurityDecorator(this, delegate.test(context), secureWhenFalse);
    }

    @Override
    public String getName() {
        return delegate.getName();
    }

    @Override
    public String getId() {
        return delegate.getId();
    }

    static class CheckResultSecurityDecorator implements DeviceCheckResult {

        private final boolean negate;
        private final DeviceCheckResult delegate;
        private final SecurityCheckDecorator deviceCheck;

        public CheckResultSecurityDecorator(final SecurityCheckDecorator deviceCheck, final DeviceCheckResult delegate, final boolean negate) {
            this.negate = negate;
            this.delegate = delegate;
            this.deviceCheck = deviceCheck;
        }

        @Override
        public String getName() {
            return delegate.getName();
        }

        @Override
        public String getId() {
            return delegate.getId();
        }

        public int getUnsecureMessageResouceId() {
            return deviceCheck.unsecureMessageResouceId;
        }

        public RadioButton getRadioButton() {
            return deviceCheck.button;
        }

        @Override
        public boolean passed() {
            boolean retVal = delegate.passed();

            if (negate) {
                return !retVal;
            }

            return retVal;
        }
    }

    public static Builder forCheck(DeviceCheckType deviceCheckType) {
        return new Builder(deviceCheckType);
    }

    public static Builder forCheck(DeviceCheck deviceCheck) {
        return new Builder(deviceCheck);
    }

    static class Builder {
        private final DeviceCheck deviceCheck;
        private int unsecureMessageResouceId;
        private boolean secureWhenFalse = false;
        private RadioButton radioButton;

        private Builder(DeviceCheckType deviceCheckType) {
            this.deviceCheck = deviceCheckType.getSecurityCheck();
        }

        private Builder(DeviceCheck deviceCheck) {
            this.deviceCheck = deviceCheck;
        }

        public Builder withUnsecureMessage(final int unsecureMessageResouceId) {
            this.unsecureMessageResouceId = unsecureMessageResouceId;
            return this;
        }

        public Builder withRadioButton(final RadioButton radioButton) {
            this.radioButton = radioButton;
            return this;
        }

        public Builder secureWhenFalse(boolean secureWhenFalse) {
            this.secureWhenFalse = secureWhenFalse;
            return this;
        }

        public DeviceCheck build() {
            return new SecurityCheckDecorator(deviceCheck, unsecureMessageResouceId, radioButton, secureWhenFalse);
        }
    }

}
