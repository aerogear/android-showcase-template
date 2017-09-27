package com.feedhenry.securenativeandroidtemplate;

import android.support.test.InstrumentationRegistry;

import com.feedhenry.securenativeandroidtemplate.di.SecureTestApplication;
import com.feedhenry.securenativeandroidtemplate.domain.crypto.AesGcmCrypto;
import com.feedhenry.securenativeandroidtemplate.domain.utils.StreamUtils;

import org.junit.Before;
import org.junit.Test;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.security.GeneralSecurityException;

import javax.crypto.CipherInputStream;
import javax.inject.Inject;

import static junit.framework.Assert.assertEquals;

/**
 * Created by weili on 22/09/2017.
 */

public class AesGcmCryptoTest {

    @Inject
    AesGcmCrypto cryptoToTest;

    @Before
    public void setUp() {
        SecureTestApplication application = (SecureTestApplication) InstrumentationRegistry.getTargetContext().getApplicationContext();
        application.getComponent().inject(this);
    }

    @Test
    public void testCryptoOperations() throws GeneralSecurityException, IOException {
        String testKeyAlias = "AesGcmCryptoTestKey";
        String textToEncrypt = "this is a test text";
        String encryptedText = cryptoToTest.encryptString(testKeyAlias, textToEncrypt, "utf-8");
        String decryptedText = cryptoToTest.decryptString(testKeyAlias, encryptedText, "utf-8");
        assertEquals(textToEncrypt, decryptedText);

        ByteArrayOutputStream bos = new ByteArrayOutputStream();
        OutputStream cipherStream = cryptoToTest.encryptStream(testKeyAlias, bos);
        cipherStream.write(textToEncrypt.getBytes());
        cipherStream.flush();
        cipherStream.close();
        byte[] encryptedBytes = bos.toByteArray();
        ByteArrayInputStream bis = new ByteArrayInputStream(encryptedBytes);
        InputStream decryptedStream = cryptoToTest.decryptStream(testKeyAlias, bis);
        String decryptedTextFromStream = StreamUtils.readStream(decryptedStream);
        assertEquals(textToEncrypt, decryptedTextFromStream);
    }


}
