package com.feedhenry.securenativeandroidtemplate.domain.crypto;

import android.os.Build;
import android.security.keystore.KeyGenParameterSpec;
import android.security.keystore.KeyProperties;
import android.support.annotation.RequiresApi;

import java.io.IOException;
import java.security.GeneralSecurityException;
import java.security.Key;
import java.security.KeyStore;

import javax.crypto.KeyGenerator;

/**
 * Implement the SecureKeyStore using the Android native KeyStore APIs. This requires Android M and later versions, as from this version, the Android keyStore supports generating AES keys.
 */
@RequiresApi(Build.VERSION_CODES.M)
public class AndroidMSecureKeyStore extends SecureKeyStoreImpl implements SecureKeyStore {

    @Override
    public boolean hasKeyAlias(String keyAlias) throws GeneralSecurityException, IOException {
        KeyStore ks = loadKeyStore();
        return ks.containsAlias(keyAlias);
    }

    @Override
    public Key getKey(String keyAlias) throws GeneralSecurityException, IOException {
        KeyStore ks = loadKeyStore();
        return ks.getKey(keyAlias, null);
    }

    @Override
    public void generateAESKey(String keyAlias) throws GeneralSecurityException, IOException {
        KeyGenerator keyGenerator = KeyGenerator.getInstance(KeyProperties.KEY_ALGORITHM_AES, ANDROID_KEY_STORE);
        //TODO: further control if user authentication is required for accessing the keys
        KeyGenParameterSpec keyGenerationParameters = new KeyGenParameterSpec.Builder(keyAlias, KeyProperties.PURPOSE_ENCRYPT | KeyProperties.PURPOSE_DECRYPT)
                .setKeySize(AES_KEYSIZE_128)
                .setBlockModes(KeyProperties.BLOCK_MODE_GCM)
                .setEncryptionPaddings(KeyProperties.ENCRYPTION_PADDING_NONE)
                .setRandomizedEncryptionRequired(true)
                .build();
        keyGenerator.init(keyGenerationParameters);
        keyGenerator.generateKey();
    }

    @Override
    public void deleteKey(String keyAlias) throws GeneralSecurityException, IOException {
        if (hasKeyAlias(keyAlias)) {
            KeyStore ks = loadKeyStore();
            ks.deleteEntry(keyAlias);
        }
    }
}
