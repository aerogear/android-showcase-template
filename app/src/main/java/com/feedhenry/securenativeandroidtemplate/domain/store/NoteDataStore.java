package com.feedhenry.securenativeandroidtemplate.domain.store;

import com.feedhenry.securenativeandroidtemplate.domain.callbacks.Callback;
import com.feedhenry.securenativeandroidtemplate.domain.models.Note;

import java.util.List;

/**
 * Define how the notes should be stored.
 */

public interface NoteDataStore {

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
}
