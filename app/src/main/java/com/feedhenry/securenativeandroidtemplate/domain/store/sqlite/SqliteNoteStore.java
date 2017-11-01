package com.feedhenry.securenativeandroidtemplate.domain.store.sqlite;

import android.content.ContentValues;
import android.content.Context;
import android.content.SharedPreferences;
import android.util.Base64;

import com.feedhenry.securenativeandroidtemplate.domain.crypto.RsaCrypto;
import com.feedhenry.securenativeandroidtemplate.domain.models.Note;
import com.feedhenry.securenativeandroidtemplate.domain.store.NoteDataStore;
import com.feedhenry.securenativeandroidtemplate.domain.store.NoteStoreException;

import net.sqlcipher.Cursor;
import net.sqlcipher.DatabaseUtils;
import net.sqlcipher.database.SQLiteDatabase;

import java.io.IOException;
import java.security.GeneralSecurityException;
import java.security.SecureRandom;
import java.util.ArrayList;
import java.util.List;

import javax.inject.Inject;

/**
 * Created by weili on 25/09/2017.
 */

public class SqliteNoteStore implements NoteDataStore {

    NoteDbHelper dbHelper;
    SQLiteDatabase writableDb;
    SQLiteDatabase readableDb;

    private static final String DB_KEY_PREFS = "dbprefs";
    private static final String DB_KEY_PREF_NAME = "dbkey";
    private static final String ENCRYPT_KEY_ALIAS = "database_key";

    private static final int PASSWORD_BYTES = 24;

    RsaCrypto rsaCrypto;
    SharedPreferences sharedPreferences;

    @Inject
    public SqliteNoteStore(Context context, RsaCrypto rsaCrypto) {
        this.dbHelper = new NoteDbHelper(context);
        this.rsaCrypto = rsaCrypto;
        this.sharedPreferences = context.getSharedPreferences(DB_KEY_PREFS, Context.MODE_PRIVATE);
    }

    private String randomPassword() {
        byte[] passwordBytes = new byte[PASSWORD_BYTES];
        SecureRandom secureRandom = new SecureRandom();
        secureRandom.nextBytes(passwordBytes);
        String password = Base64.encodeToString(passwordBytes, Base64.NO_WRAP);
        return password;
    }

    // tag::getDbPassword[]
    /**
     * Get the password to protect the database
     * @return the password
     * @throws GeneralSecurityException
     * @throws IOException
     */
    private String getDbPassword() throws GeneralSecurityException, IOException {
        String encryptedDbPass = this.sharedPreferences.getString(DB_KEY_PREF_NAME, null);
        if (encryptedDbPass == null) {
            String passwordToEncrypt = randomPassword();
            encryptedDbPass = Base64.encodeToString(rsaCrypto.encrypt(ENCRYPT_KEY_ALIAS, passwordToEncrypt.getBytes("utf-8")), Base64.NO_WRAP);
            SharedPreferences.Editor editor = this.sharedPreferences.edit();
            editor.putString(DB_KEY_PREF_NAME, encryptedDbPass).commit();
        }
        String password = new String(rsaCrypto.decrypt(ENCRYPT_KEY_ALIAS, Base64.decode(encryptedDbPass, Base64.NO_WRAP)), "utf-8");
        return password;
    }
    // end::getDbPassword[]

    private SQLiteDatabase getWritableDatabase() throws GeneralSecurityException, IOException {
        if (this.writableDb == null) {
            String password = getDbPassword();
            this.writableDb = this.dbHelper.getWritableDatabase(password);
        }
        return this.writableDb;
    }

    private SQLiteDatabase getReadableDb() throws GeneralSecurityException, IOException {
        if (this.readableDb == null) {
            String password = getDbPassword();
            this.readableDb = this.dbHelper.getReadableDatabase(password);
        }
        return this.readableDb;
    }

    @Override
    public Note createNote(Note note) throws Exception {
        SQLiteDatabase db = getWritableDatabase();

        ContentValues values = new ContentValues();
        values.put(NoteContract.NoteEntry.COLUMN_UUID, note.getId());
        values.put(NoteContract.NoteEntry.COLUMN_NAME_TITLE, note.getTitle());
        values.put(NoteContract.NoteEntry.COLUMN_NAME_CONTENT, note.getContent());
        values.put(NoteContract.NoteEntry.COLUMN_CREATED_AT, note.getCreatedAt().getTime());

        long id = db.insert(NoteContract.NoteEntry.TABLE_NAME, null, values);
        if (id >= 0) {
            return note;
        } else {
            throw new NoteStoreException("Failed to create note using sqlite");
        }
    }

    @Override
    public Note updateNote(Note note) throws Exception {
        SQLiteDatabase db = getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(NoteContract.NoteEntry.COLUMN_NAME_TITLE, note.getTitle());
        values.put(NoteContract.NoteEntry.COLUMN_NAME_CONTENT, note.getContent());
        String selection = NoteContract.NoteEntry.COLUMN_UUID + " is ?";
        String[] selectionArgs = { note.getId() };

        int count = db.update(
                NoteContract.NoteEntry.TABLE_NAME,
                values,
                selection,
                selectionArgs
        );
        if (count == 1) {
            return note;
        } else {
            throw new NoteStoreException("Failed to update note using sqlite");
        }
    }

    @Override
    public Note deleteNote(Note note) throws Exception {
        SQLiteDatabase db = getWritableDatabase();
        String selection = NoteContract.NoteEntry.COLUMN_UUID + " is ?";
        String[] selectionArgs = { note.getId() };
        db.delete(NoteContract.NoteEntry.TABLE_NAME, selection, selectionArgs);
        return note;
    }

    @Override
    public Note readNote(String noteId) throws Exception {
        SQLiteDatabase db = getReadableDb();

        String[] projections = {
                NoteContract.NoteEntry.COLUMN_UUID,
                NoteContract.NoteEntry.COLUMN_CREATED_AT,
                NoteContract.NoteEntry.COLUMN_NAME_TITLE,
                NoteContract.NoteEntry.COLUMN_NAME_CONTENT
        };

        Note readNote = null;
        String selection = NoteContract.NoteEntry.COLUMN_UUID + " is ?";
        String[] selectionArgs = {noteId};
        Cursor cursor = db.query(NoteContract.NoteEntry.TABLE_NAME, projections, selection, selectionArgs, null, null, null);
        if (cursor.moveToNext()) {
            String uuid = cursor.getString(cursor.getColumnIndexOrThrow(NoteContract.NoteEntry.COLUMN_UUID));
            int createdAt = cursor.getInt(cursor.getColumnIndexOrThrow(NoteContract.NoteEntry.COLUMN_CREATED_AT));
            String title = cursor.getString(cursor.getColumnIndexOrThrow(NoteContract.NoteEntry.COLUMN_NAME_TITLE));
            String content = cursor.getString(cursor.getColumnIndexOrThrow(NoteContract.NoteEntry.COLUMN_NAME_CONTENT));
            readNote = new Note(uuid, title, content, (long) createdAt);
            readNote.setStoreType(getType());
        }
        cursor.close();
        return readNote;
    }

    @Override
    public List<Note> listNotes() throws Exception {
        SQLiteDatabase db = getReadableDb();
        String[] projections = {
                NoteContract.NoteEntry.COLUMN_UUID,
                NoteContract.NoteEntry.COLUMN_CREATED_AT,
                NoteContract.NoteEntry.COLUMN_NAME_TITLE
        };

        String sortOrder = NoteContract.NoteEntry.COLUMN_CREATED_AT + " DESC";
        Cursor cursor = db.query(NoteContract.NoteEntry.TABLE_NAME, projections, null, null, null, null, sortOrder);
        List<Note> notes = new ArrayList<Note>();
        while(cursor.moveToNext()) {
            String uuid = cursor.getString(cursor.getColumnIndexOrThrow(NoteContract.NoteEntry.COLUMN_UUID));
            int createdAt = cursor.getInt(cursor.getColumnIndexOrThrow(NoteContract.NoteEntry.COLUMN_CREATED_AT));
            String title = cursor.getString(cursor.getColumnIndexOrThrow(NoteContract.NoteEntry.COLUMN_NAME_TITLE));
            Note note = new Note(uuid, title, null, createdAt);
            note.setStoreType(getType());
            notes.add(note);
        }
        cursor.close();
        return notes;
    }

    @Override
    public int getType() {
        return STORE_TYPE_SQL;
    }

    @Override
    public long count() throws Exception {
        SQLiteDatabase db = getReadableDb();
        long count = DatabaseUtils.queryNumEntries(db, NoteContract.NoteEntry.TABLE_NAME);
        return count;
    }
}
