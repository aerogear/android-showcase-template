package com.feedhenry.securenativeandroidtemplate.domain.store;

import android.content.Context;

import com.feedhenry.securenativeandroidtemplate.domain.crypto.AesGcmCrypto;
import com.feedhenry.securenativeandroidtemplate.domain.models.Note;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.invocation.InvocationOnMock;
import org.mockito.junit.MockitoJUnitRunner;
import org.mockito.stubbing.Answer;

import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.List;

import static junit.framework.Assert.assertEquals;
import static junit.framework.Assert.assertNotNull;
import static junit.framework.Assert.assertNull;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.when;

/**
 * Created by weili on 19/09/2017.
 */

@RunWith(MockitoJUnitRunner.class)
public class SecureFileNoteStoreTest {

    @Mock
    Context testContext;

    @Mock
    AesGcmCrypto testCrypto;

    @InjectMocks SecureFileNoteStore storeToTest;

    final String tmpDirPath = System.getProperty("java.io.tmpdir");
    private static final String TEST_DIR_NAME = "secureNotesTest";

    @Before
    public void setUp() {
        File testDir = new File(tmpDirPath, TEST_DIR_NAME);
        if (testDir.exists()) {
            File[] files = testDir.listFiles();
            for (File f : files) {
                f.delete();
            }
            testDir.delete();
        }
    }

    @Test
    public void testFileNoteStore() throws Exception {
        final File testDir = new File(tmpDirPath, TEST_DIR_NAME);
        testDir.mkdirs();
        when(testContext.getFilesDir()).thenReturn(testDir);
        when(testContext.openFileInput(anyString())).then(new Answer<InputStream>() {
            @Override
            public InputStream answer(InvocationOnMock invocation) throws Throwable {
                String fileName = invocation.getArgument(0);
                return new FileInputStream(new File(testDir, fileName));
            }
        });
        when(testCrypto.encryptStream(anyString(), any(OutputStream.class))).then(new Answer<OutputStream>() {
            @Override
            public OutputStream answer(InvocationOnMock invocation) throws Throwable {
                return invocation.getArgument(1);
            }
        });
        when(testCrypto.decryptStream(anyString(), any(InputStream.class))).then(new Answer<InputStream>() {
            @Override
            public InputStream answer(InvocationOnMock invocation) throws Throwable {
                return invocation.getArgument(1);
            }
        });

        final String testTitle = "testNote";
        final String testContent = "testNoteContent";
        Note note = new Note(testTitle, testContent);
        Note created = storeToTest.createNote(note);
        assertNotNull(created);

        List<Note> listed = storeToTest.listNotes();
        assertEquals(1, listed.size());
        Note read =  storeToTest.readNote(note.getId());
        assertEquals(read.getTitle(), testTitle);
        assertEquals(read.getContent(), testContent);

        Note removed = storeToTest.deleteNote(note);
        assertNotNull(removed);

        listed = storeToTest.listNotes();
        assertEquals(0, listed.size());
    }

}
