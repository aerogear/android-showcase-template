package com.feedhenry.securenativeandroidtemplate.domain.crypto;

import java.io.IOException;
import java.security.GeneralSecurityException;
import java.security.Key;
import java.security.KeyStore;

/**
 * A implementation of the SecureKeyStore interface that will only throw exceptions.
 * This should only be used for devices with Android that is lower than 18 (4.3).
 */

public class NullAndroidSecureKeyStore extends SecureKeyStoreImpl implements SecureKeyStore {
    @Override
    public String getSupportedAESMode() {
        return null;
    }

    @Override
    public String getSupportedRSAMode() {
        return null;
    }

    @Override
    public boolean hasSecretKey(String keyAlias) throws GeneralSecurityException, IOException {
        throw new GeneralSecurityException("This version of Android is not supported");
    }

    @Override
    public Key getSecretKey(String keyAlias) throws GeneralSecurityException, IOException {
        throw new GeneralSecurityException("This version of Android is not supported");
    }

    @Override
    public void generateAESKey(String keyAlias) throws GeneralSecurityException, IOException {
        throw new GeneralSecurityException("This version of Android is not supported");
    }

    @Override
    public void generatePrivateKeyPair(String keyAlias) throws GeneralSecurityException, IOException {
        throw new GeneralSecurityException("This version of Android is not supported");
    }

    @Override
    public KeyStore.Entry getKeyPairEntry(String keyAlias) throws GeneralSecurityException, IOException {
        throw new GeneralSecurityException("This version of Android is not supported");
    }

    @Override
    public boolean hasKeyPair(String keyAlias) throws GeneralSecurityException, IOException {
        throw new GeneralSecurityException("This version of Android is not supported");
    }

    @Override
    public void deleteKey(String keyAlias) throws GeneralSecurityException, IOException {
        throw new GeneralSecurityException("This version of Android is not supported");
    }
}
