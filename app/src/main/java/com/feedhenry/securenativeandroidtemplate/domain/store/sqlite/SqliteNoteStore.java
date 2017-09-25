package com.feedhenry.securenativeandroidtemplate.domain.store.sqlite;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import com.feedhenry.securenativeandroidtemplate.domain.models.Note;
import com.feedhenry.securenativeandroidtemplate.domain.store.NoteDataStore;
import com.feedhenry.securenativeandroidtemplate.domain.store.NoteStoreException;

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

    @Inject
    public SqliteNoteStore(Context context) {
        this.dbHelper = new NoteDbHelper(context);
    }

    private SQLiteDatabase getWritableDatabase() {
        if (this.writableDb == null) {
            this.writableDb = this.dbHelper.getWritableDatabase();
        }
        return this.writableDb;
    }

    private SQLiteDatabase getReadableDb() {
        if (this.readableDb == null) {
            this.readableDb = this.dbHelper.getReadableDatabase();
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
        return notes;
    }

    @Override
    public int getType() {
        return STORE_TYPE_SQL;
    }
}
