package com.aerogear.androidshowcase.domain.store;

import android.content.Context;
import android.support.test.InstrumentationRegistry;

import com.aerogear.androidshowcase.di.SecureTestApplication;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import java.io.File;

import javax.inject.Inject;

/**
 * Created by weili on 25/09/2017.
 */

public class SecureFileNoteStoreTest extends NoteStoreTestBase {
    @Inject
    Context context;

    @Inject
    SecureFileNoteStore secureFileNoteStore;

    @Before
    public void setup() {
        SecureTestApplication application = (SecureTestApplication) InstrumentationRegistry.getTargetContext().getApplicationContext();
        application.getComponent().inject(this);
        removeFiles(this.context);
    }

    @After
    public void teardown() {
        removeFiles(this.context);
    }

    @Test
    public void testSecureFileNoteStore() throws Exception {
        noteCRUDL(this.secureFileNoteStore);
    }

    public static void removeFiles(Context context) {
        File testDir = context.getFilesDir();
        if (testDir.exists()) {
            File[] files = testDir.listFiles();
            for (File f : files) {
                f.delete();
            }
            testDir.delete();
        }
    }
}
