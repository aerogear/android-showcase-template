package com.feedhenry.securenativeandroidtemplate.domain.crypto;

import java.io.IOException;
import java.security.GeneralSecurityException;
import java.security.Key;
import java.security.KeyStore;

/**
 * A keystore interface. It will allow us to support different types of keystores on different versions of Android.
 */

public interface SecureKeyStore {

    /**
     * Return the supported AES mode.
     * @return the supported AES mode, like AES/GCM/NoPadding
     */
    public String getSupportedAESMode();

    /**
     * Return the supported RSA mode
     * @return the supported RSA mode, like RSA/ECB/PKCS1Padding
     */
    public String getSupportedRSAMode();

    /**
     * Check if the keystore has the given secret key alias
     * @param keyAlias the key alias of the secret key to check
     * @return if the key alias exists
     * @throws GeneralSecurityException
     * @throws IOException
     */
    public boolean hasSecretKey(String keyAlias) throws GeneralSecurityException, IOException;

    /**
     * Get the secret key from the key store
     * @param keyAlias the alias of the secret key
     * @return the secret key
     * @throws GeneralSecurityException
     * @throws IOException
     */
    public Key getSecretKey(String keyAlias) throws GeneralSecurityException, IOException;

    /**
     * Generate a AES secret key and save in the key store
     * @param keyAlias the alias of the secret key
     * @throws GeneralSecurityException
     * @throws IOException
     */
    public void generateAESKey(String keyAlias) throws GeneralSecurityException, IOException;

    /**
     * Generate a private key pair in the key store
     * @param keyAlias the alias of the key pair
     * @throws GeneralSecurityException
     * @throws IOException
     */
    public void generatePrivateKeyPair(String keyAlias) throws GeneralSecurityException, IOException;

    /**
     * Get the key pair from the key store
     * @param keyAlias the alias of the key pair
     * @return the key pair
     * @throws GeneralSecurityException
     * @throws IOException
     */
    public KeyStore.Entry getKeyPairEntry(String keyAlias) throws GeneralSecurityException, IOException;

    /**
     * Check if the key store has the given ken alias
     * @param keyAlias the alias of the key
     * @return
     * @throws GeneralSecurityException
     * @throws IOException
     */
    public boolean hasKeyPair(String keyAlias) throws GeneralSecurityException, IOException;

    /**
     * Delete the key from the key store with the given key alias
     * @param keyAlias
     * @throws GeneralSecurityException
     * @throws IOException
     */
    public void deleteKey(String keyAlias) throws GeneralSecurityException, IOException;
}
