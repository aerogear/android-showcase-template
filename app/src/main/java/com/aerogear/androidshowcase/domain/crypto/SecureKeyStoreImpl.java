package com.aerogear.androidshowcase.domain.crypto;

import java.io.IOException;
import java.security.GeneralSecurityException;
import java.security.KeyStore;

/**
 * Base class that implements the SecureKeystore interface.
 */

public abstract class SecureKeyStoreImpl implements SecureKeyStore {

    protected static final String ANDROID_KEY_STORE = "AndroidKeyStore";

    protected static final int AES_KEYSIZE_128 = 128;
    protected static final int RSA_KEY_SIZE = 2048;

    protected static final String ALG_AES_GCM_NOPADDING = "AES/GCM/NoPadding";
    protected static final String ALG_RSA_ECB_PCKS1Padding = "RSA/ECB/PKCS1Padding";
    protected static final String ALG_RSA_ECB_OAEPPadding = "RSA/ECB/OAEPWithSHA-256AndMGF1Padding";

    protected KeyStore loadKeyStore() throws GeneralSecurityException, IOException {
        KeyStore keyStore = KeyStore.getInstance(ANDROID_KEY_STORE);
        keyStore.load(null);
        return keyStore;
    }

    @Override
    public KeyStore.Entry getKeyPairEntry(String keyAlias) throws GeneralSecurityException, IOException {
        KeyStore ks = loadKeyStore();
        return ks.getEntry(keyAlias, null);
    }
}
