package com.feedhenry.securenativeandroidtemplate.domain.crypto;

import android.annotation.TargetApi;
import android.os.Build;
import android.security.keystore.KeyGenParameterSpec;
import android.security.keystore.KeyProperties;
import android.util.Base64;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.ByteBuffer;
import java.security.GeneralSecurityException;
import java.security.Key;
import java.security.KeyStore;

import javax.crypto.Cipher;
import javax.crypto.CipherInputStream;
import javax.crypto.CipherOutputStream;
import javax.crypto.KeyGenerator;
import javax.crypto.SecretKey;
import javax.crypto.spec.GCMParameterSpec;
import javax.inject.Inject;

/**
 * Perform data encryption and decryption using AES/GCM/NoPadding alg. At the moment, it requires API level 23.
 * TODO: add support for older API versions.
 */

public class AesGcmCrypto {

    private static final String ANDROID_KEY_STORE = "AndroidKeyStore";
    private static final int AES_KEYSIZE_128 = 128;
    private static final String ALG_AES_GCM_NOPADDING = "AES/GCM/NoPadding";
    private static final int BASE64_FLAG = Base64.NO_WRAP;

    private String keyStoreType;

    public AesGcmCrypto(String keyStoreType) {
        this.keyStoreType = keyStoreType;
    }

    @Inject
    public AesGcmCrypto() {
        this.keyStoreType = ANDROID_KEY_STORE;
    }

    /**
     * Generate a secret key and save it in the keystore with the given alias. The key will be generated using AES/GCM/NoPadding, and it's size is 128.
     * Note: This can only be used on Android M (API level 23) and later versions.
     * @param keyAlias the alias of the key entry. Can be used to retrieve the key from the keystore later on.
     * @throws GeneralSecurityException
     */
    @TargetApi(Build.VERSION_CODES.M)
    public void generateAESKey(String keyAlias) throws GeneralSecurityException, IOException {
        KeyStore keyStore = KeyStore.getInstance(this.keyStoreType);
        keyStore.load(null);
        KeyGenerator keyGenerator = KeyGenerator.getInstance(KeyProperties.KEY_ALGORITHM_AES, this.keyStoreType);
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

    /**
     * Load the secret key from the keystore using the given key alias if it already exists, or generate a new one if it doesn't exist.
     * @param keyAlias the alias of the key
     * @return the SecretKey instance.
     * @throws GeneralSecurityException
     * @throws IOException
     */
    private SecretKey loadOrGenerateSecretKey(String keyAlias, boolean doGenerate) throws GeneralSecurityException, IOException {
        KeyStore keyStore = KeyStore.getInstance(this.keyStoreType);
        keyStore.load(null);
        if (!keyStore.containsAlias(keyAlias)) {
            if (doGenerate) {
                generateAESKey(keyAlias);
            } else {
                throw new GeneralSecurityException("missing alias " + keyAlias);
            }
        }
        SecretKey secretKey = (SecretKey) keyStore.getKey(keyAlias, null);
        return secretKey;

    }

    /**
     * Encrypt the given text.
     * @param keyAlias The alias of the key in the keystore that will be used for the encryption.
     * @param plainText the text to encrypt
     * @return the encrypted data. The first 12 bytes will be the IV (initial vector) used for the encryption.
     * @throws GeneralSecurityException
     * @throws IOException
     */
    public byte[] encrypt(String keyAlias, byte[] plainText) throws GeneralSecurityException, IOException {
        SecretKey secretKey = loadOrGenerateSecretKey(keyAlias, true);
        Cipher cipher = Cipher.getInstance(ALG_AES_GCM_NOPADDING);
        cipher.init(Cipher.ENCRYPT_MODE, secretKey);
        //get the iv that is being used
        byte[] iv = cipher.getIV();
        byte[] encrypted = cipher.doFinal(plainText);
        GCMEncrypted encryptedData = new GCMEncrypted(iv, encrypted);
        return encryptedData.toByteArray();
    }

    /**
     * Decrypt the given encrypted data
     * @param keyAlias The alias of the key in the keystore that will be used for the decryption.
     * @param encryptedText the text to decrypt. The first 12 bytes should be the IV used for encryption.
     * @return the plain text data
     * @throws GeneralSecurityException
     * @throws IOException
     */
    public byte[] decrypt(String keyAlias, byte[] encryptedText) throws GeneralSecurityException, IOException {
        GCMEncrypted encryptedData = GCMEncrypted.parse(encryptedText);
        SecretKey secretKey = loadOrGenerateSecretKey(keyAlias, false);
        Cipher cipher = Cipher.getInstance(ALG_AES_GCM_NOPADDING);
        cipher.init(Cipher.DECRYPT_MODE, secretKey, new GCMParameterSpec(GCMEncrypted.GCM_TAG_LENGTH, encryptedData.iv));
        byte[] plainText = cipher.doFinal(encryptedData.encryptedData);
        return plainText;
    }

    /**
     * Encrypt the given string. The encrypted data will be returned as a base64-encoded string.
     * @param keyAlias The alias of the key in the keystore that will be used for the encryption.
     * @param plainText the string to encrypt
     * @param encoding the encode of the plain text
     * @return encrypted data in base64-encoded string
     * @throws GeneralSecurityException
     * @throws IOException
     */
    public String encryptString(String keyAlias, String plainText, String encoding) throws GeneralSecurityException, IOException {
        return Base64.encodeToString(encrypt(keyAlias, plainText.getBytes(encoding)), BASE64_FLAG);
    }

    /**
     * Returns an OutputStream that will automatically encrypt data while writing. The IV will be first written to the original stream without encryption.
     * NOTE: don't write to the original stream directly in the calling method.
     * @param keyAlias the alias of the secret key to use
     * @param outputStream the original output stream.
     * @return The output stream that will encrypt the data
     * @throws GeneralSecurityException
     * @throws IOException
     */
    public OutputStream encryptStream(String keyAlias, OutputStream outputStream) throws GeneralSecurityException, IOException {
        SecretKey secretKey = loadOrGenerateSecretKey(keyAlias, true);
        Cipher cipher = Cipher.getInstance(ALG_AES_GCM_NOPADDING);
        cipher.init(Cipher.ENCRYPT_MODE, secretKey);
        //get the iv that is being used
        byte[] iv = cipher.getIV();
        outputStream.write(iv);
        CipherOutputStream cipherStream = new CipherOutputStream(outputStream, cipher);
        return cipherStream;
    }

    /**
     * Decrypted the encrypted input stream, and return the plain text input stream.
     * @param keyAlias the alias of the secret key to use
     * @param inputStream the encrypted input stream. The first 12 bytes should be the IV.
     * @return the plain text input stream
     * @throws GeneralSecurityException
     * @throws IOException
     */
    public InputStream decryptStream(String keyAlias, InputStream inputStream) throws GeneralSecurityException, IOException {
        SecretKey secretKey = loadOrGenerateSecretKey(keyAlias, false);
        byte[] iv = new byte[12];
        inputStream.read(iv);
        Cipher cipher = Cipher.getInstance(ALG_AES_GCM_NOPADDING);
        cipher.init(Cipher.DECRYPT_MODE, secretKey, new GCMParameterSpec(GCMEncrypted.GCM_TAG_LENGTH, iv));
        CipherInputStream cipherStream = new CipherInputStream(inputStream, cipher);
        return cipherStream;
    }

    /**
     * Decrypt the given string. The encrypted data should be base64-encoded, and no line separators (it's in one line).
     * @param keyAlias The alias of the key in the keystore that will be used for the decryption.
     * @param encryptedText The text to decrypt. It should be base64-encoded, and has no line separators.
     * @param encoding the encoding of the decrypted data.
     * @return the decrypted string data
     * @throws GeneralSecurityException
     * @throws IOException
     */
    public String decryptString(String keyAlias, String encryptedText, String encoding) throws GeneralSecurityException, IOException {
        return new String(decrypt(keyAlias, Base64.decode(encryptedText, BASE64_FLAG)), encoding);
    }

    /**
     * Remove the key entry from the keystore that has the given keyAlias
     * @param keyAlias the alias of the key
     * @throws GeneralSecurityException
     * @throws IOException
     */
    public void deleteSecretKey(String keyAlias) throws GeneralSecurityException, IOException {
        KeyStore keyStore = KeyStore.getInstance(this.keyStoreType);
        keyStore.load(null);
        keyStore.deleteEntry(keyAlias);
    }

    static class GCMEncrypted {
        private static final int GCM_TAG_LENGTH = 128;
        private byte[] iv;
        private byte[] encryptedData;

        private GCMEncrypted() {

        }

        GCMEncrypted(byte[] iv, byte[] data) {
            this.iv = iv;
            this.encryptedData = data;
        }

        public byte[] toByteArray() {
            ByteBuffer bb = ByteBuffer.allocate(this.iv.length + this.encryptedData.length);
            bb.put(this.iv);
            bb.put(this.encryptedData);
            return bb.array();
        }

        public static GCMEncrypted parse(byte[] encrypted) {
            ByteBuffer bb = ByteBuffer.wrap(encrypted);
            GCMEncrypted gcmData = new GCMEncrypted();
            byte[] ivArr = new byte[12];
            byte[] dataArr = new byte[encrypted.length - 12];
            bb.get(ivArr);
            bb.get(dataArr);
            gcmData.iv = ivArr;
            gcmData.encryptedData = dataArr;
            return gcmData;
        }
    }
}
