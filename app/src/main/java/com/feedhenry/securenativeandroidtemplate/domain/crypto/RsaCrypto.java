package com.feedhenry.securenativeandroidtemplate.domain.crypto;

import java.io.IOException;
import java.security.GeneralSecurityException;
import java.security.KeyStore;

import javax.inject.Inject;

/**
 * Perform data encryption/decryption using RSA key pairs. It can be used to encrypt/decrypt small amount of data.
 */
public class RsaCrypto {


    SecureKeyStore secureKeyStore;

    @Inject
    public RsaCrypto(SecureKeyStore secureKeyStore) {
        this.secureKeyStore = secureKeyStore;
    }

    public byte[] encrypt(String keyAlias, byte[] textToEncrypt) throws GeneralSecurityException, IOException {
        if (!secureKeyStore.hasKeyPair(keyAlias)) {
            secureKeyStore.generatePrivateKeyPair(keyAlias);
        }
        KeyStore.PrivateKeyEntry privateKeyEntry = (KeyStore.PrivateKeyEntry) secureKeyStore.getKeyPairEntry(keyAlias);
        return RsaHelper.encrypt(secureKeyStore.getSupportedRSAMode(), privateKeyEntry, textToEncrypt);
    }

    public byte[] decrypt(String keyAlias, byte[] dataToDecrypt) throws GeneralSecurityException, IOException {
        if (!secureKeyStore.hasKeyPair(keyAlias)) {
            throw new GeneralSecurityException("missing key " + keyAlias);
        }
        KeyStore.PrivateKeyEntry privateKeyEntry = (KeyStore.PrivateKeyEntry) secureKeyStore.getKeyPairEntry(keyAlias);
        return RsaHelper.decrypt(secureKeyStore.getSupportedRSAMode(), privateKeyEntry, dataToDecrypt);
    }
}
