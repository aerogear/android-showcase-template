package com.feedhenry.securenativeandroidtemplate.domain.crypto;

import java.io.IOException;
import java.security.GeneralSecurityException;
import java.security.Key;

/**
 * A implementation of the SecureKeyStore interface that will only throw exceptions.
 * This should only be used for devices with Android that is lower than 18 (4.3).
 */

public class NullAndroidSecureKeyStore extends SecureKeyStoreImpl implements SecureKeyStore {
    @Override
    public boolean hasKeyAlias(String keyAlias) throws GeneralSecurityException, IOException {
        throw new GeneralSecurityException("This version of Android is not supported");
    }

    @Override
    public Key getKey(String keyAlias) throws GeneralSecurityException, IOException {
        throw new GeneralSecurityException("This version of Android is not supported");
    }

    @Override
    public void generateAESKey(String keyAlias) throws GeneralSecurityException, IOException {
        throw new GeneralSecurityException("This version of Android is not supported");
    }

    @Override
    public void deleteKey(String keyAlias) throws GeneralSecurityException, IOException {
        throw new GeneralSecurityException("This version of Android is not supported");
    }
}
