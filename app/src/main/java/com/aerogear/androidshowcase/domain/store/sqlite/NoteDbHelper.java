package com.aerogear.androidshowcase.domain.store.sqlite;

import android.content.Context;

import net.sqlcipher.database.SQLiteDatabase;
import net.sqlcipher.database.SQLiteOpenHelper;

/**
 * Created by weili on 25/09/2017.
 */

public class NoteDbHelper extends SQLiteOpenHelper {

    public static final int DATABASE_VERSION = 1;
    public static final String DATABASE_NAME = "notes.db";

    private static final String SQL_CREATE_STATEMENT = String.format("CREATE TABLE %s (%s INTEGER PRIMARY KEY, %s INTEGER, %s TEXT, %s TEXT, %s TEXT)",
            NoteContract.NoteEntry.TABLE_NAME, NoteContract.NoteEntry._ID, NoteContract.NoteEntry.COLUMN_CREATED_AT, NoteContract.NoteEntry.COLUMN_UUID, NoteContract.NoteEntry.COLUMN_NAME_TITLE, NoteContract.NoteEntry.COLUMN_NAME_CONTENT);

    public NoteDbHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL(SQL_CREATE_STATEMENT);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

    }
}
