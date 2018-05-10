package com.aerogear.androidshowcase;

import android.support.test.InstrumentationRegistry;

import com.aerogear.androidshowcase.di.SecureTestApplication;
import com.aerogear.androidshowcase.domain.crypto.RsaCrypto;

import org.junit.Before;
import org.junit.Test;

import java.io.IOException;
import java.security.GeneralSecurityException;

import javax.inject.Inject;

import static junit.framework.Assert.assertEquals;

/**
 * Created by weili on 27/09/2017.
 */

public class RsaCryptoTest {
    @Inject
    RsaCrypto rsaCrytoTest;

    @Before
    public void setUp() {
        SecureTestApplication application = (SecureTestApplication) InstrumentationRegistry.getTargetContext().getApplicationContext();
        application.getComponent().inject(this);
    }

    @Test
    public void testCryptoOperations() throws GeneralSecurityException, IOException {
        String testKeyAlias = "RSACryptoTestKey";
        String textToEncrypt = "this is a test text";
        byte[] encrypted = rsaCrytoTest.encrypt(testKeyAlias,textToEncrypt.getBytes("utf-8"));
        byte[] decrypted = rsaCrytoTest.decrypt(testKeyAlias, encrypted);
        assertEquals(textToEncrypt, new String(decrypted, "utf-8"));
    }
}
