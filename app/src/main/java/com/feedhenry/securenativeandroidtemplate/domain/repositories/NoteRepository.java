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
     */
    List<Note> listNotes() throws Exception;

    /**
     * Read the details about the a note with the given noteId
     * @param noteId the id of the node
     */
    Note readNote(String noteId) throws Exception;

    /**
     * Create a new note
     * @param noteModel the note to be created
     */
    Note createNote(Note noteModel) throws Exception;

    /**
     * Update the given note
     * @param noteModel the note to be updated
     */
    Note updateNote(Note noteModel) throws Exception;

    /**
     * Delete the given note
     * @param noteModel the model to be deleted
     */
    Note deleteNote(Note noteModel) throws Exception;

}
