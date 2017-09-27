package com.feedhenry.securenativeandroidtemplate.domain.crypto;

import java.io.IOException;
import java.security.GeneralSecurityException;
import java.security.Key;

/**
 * A keystore interface. It will allow us to support different types of keystores on different versions of Android.
 */

public interface SecureKeyStore {

    public boolean hasKeyAlias(String keyAlias) throws GeneralSecurityException, IOException;

    public Key getKey(String keyAlias) throws GeneralSecurityException, IOException;

    public void generateAESKey(String keyAlias) throws GeneralSecurityException, IOException;

    public void deleteKey(String keyAlias) throws GeneralSecurityException, IOException;
}
