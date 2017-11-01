package com.feedhenry.securenativeandroidtemplate.domain.store;

import com.feedhenry.securenativeandroidtemplate.domain.callbacks.Callback;
import com.feedhenry.securenativeandroidtemplate.domain.models.Note;

import java.util.List;

/**
 * Define how the notes should be stored.
 */

public interface NoteDataStore {

    public static final int STORE_TYPE_INMEMORY = 0;
    public static final int STORE_TYPE_FILE = 1;
    public static final int STORE_TYPE_SQL = 2;

    /**
     * Save the note in the data store
     * @param note
     */
    Note createNote(Note note) throws Exception;

    /**
     * Update the note in the data store
     * @param note
     */
    Note updateNote(Note note) throws Exception;


    /**
     * Delete the note in the data store
     * @param note
     */
    Note deleteNote(Note note) throws Exception;


    /**
     * Read the note from the data store
     * @param noteId the id of the note
     */
    Note readNote(String noteId) throws Exception;

    /**
     *  List the notes from the data store
     */
    List<Note> listNotes() throws Exception;

    /**
     * Return the type of the store
     * @return
     */
    public int getType();

    /**
     * Return the total number of notes in the store
     * @return the total number of notes
     */
    public long count() throws Exception;
}
