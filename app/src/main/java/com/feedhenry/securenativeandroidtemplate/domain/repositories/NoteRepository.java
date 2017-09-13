package com.feedhenry.securenativeandroidtemplate.domain.repositories;

import com.feedhenry.securenativeandroidtemplate.domain.callbacks.Callback;
import com.feedhenry.securenativeandroidtemplate.domain.models.Note;

import java.util.List;

/**
 * Define a repository that can be used to perform CRUDL operations on the notes.
 */

public interface NoteRepository {

    /**
     * Read the notes from a data store
     * @param cb the function to be executed when the notes are loaded
     */
    void listNotes(Callback<List<Note>> cb);

    /**
     * Read the details about the a note with the given noteId
     * @param noteId the id of the node
     * @param cb the function to be executed when the note is read
     */
    void readNote(String noteId, Callback<Note> cb);

    /**
     * Create a new note
     * @param noteModel the note to be created
     * @param cb the function to be executed when the note is created
     */
    void createNote(Note noteModel, Callback<Note> cb);

    /**
     * Update the given note
     * @param noteModel the note to be updated
     * @param cb the function to be executed when the note is updated
     */
    void updateNote(Note noteModel, Callback<Note> cb);

    /**
     * Delete the given note
     * @param noteModel the model to be deleted
     * @param cb the function to be executed when the note is deleted
     */
    void deleteNote(Note noteModel, Callback<Note> cb);

}
