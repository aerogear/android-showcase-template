package com.feedhenry.securenativeandroidtemplate.domain.crypto;

import java.io.IOException;
import java.security.GeneralSecurityException;
import java.security.KeyStore;

/**
 * Base class that implements the SecureKeystore interface.
 */

public abstract class SecureKeyStoreImpl implements SecureKeyStore {

    protected static final String ANDROID_KEY_STORE = "AndroidKeyStore";

    protected static final int AES_KEYSIZE_128 = 128;

    protected KeyStore loadKeyStore() throws GeneralSecurityException, IOException {
        KeyStore keyStore = KeyStore.getInstance(ANDROID_KEY_STORE);
        keyStore.load(null);
        return keyStore;
    }
}
