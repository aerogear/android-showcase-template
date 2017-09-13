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
     * @param callback
     */
    void createNote(Note note, Callback<Note> callback);

    /**
     * Update the note in the data store
     * @param note
     * @param callback
     */
    void updateNote(Note note, Callback<Note> callback);


    /**
     * Delete the note in the data store
     * @param note
     * @param callback
     */
    void deleteNote(Note note, Callback<Note> callback);


    /**
     *  Read the note from the data store
     * @param noteId the id of the note
     * @param callback
     */
    void readNote(String noteId, Callback<Note> callback);

    /**
     *  List the notes from the data store
     * @param callback
     */
    void listNotes(Callback<List<Note>> callback);
}
