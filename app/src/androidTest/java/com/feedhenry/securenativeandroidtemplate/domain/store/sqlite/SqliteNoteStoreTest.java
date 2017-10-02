package com.feedhenry.securenativeandroidtemplate.domain.store.sqlite;

import android.content.Context;
import android.support.test.InstrumentationRegistry;

import com.feedhenry.securenativeandroidtemplate.di.SecureTestApplication;
import com.feedhenry.securenativeandroidtemplate.domain.models.Note;
import com.feedhenry.securenativeandroidtemplate.domain.store.NoteDataStore;
import com.feedhenry.securenativeandroidtemplate.domain.store.NoteStoreTestBase;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import java.util.List;

import javax.inject.Inject;

import static junit.framework.Assert.assertEquals;
import static junit.framework.Assert.assertNotNull;

public class SqliteNoteStoreTest extends NoteStoreTestBase {

    @Inject
    Context context;

    @Inject
    SqliteNoteStore sqliteStore;

    @Before
    public void setup() {
        SecureTestApplication application = (SecureTestApplication) InstrumentationRegistry.getTargetContext().getApplicationContext();
        application.getComponent().inject(this);
        cleardb();
    }

    @After
    public void teardown() {
        cleardb();
    }

    @Test
    public void testSqliteNoteStore() throws Exception {
        noteCRUDL(this.sqliteStore);
    }

    private void cleardb() {
        this.context.deleteDatabase(NoteDbHelper.DATABASE_NAME);
    }

}
