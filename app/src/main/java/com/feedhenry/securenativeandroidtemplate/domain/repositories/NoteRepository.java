package com.feedhenry.securenativeandroidtemplate.domain.repositories;

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
     * Read the details about a note with the given noteId from the given storage.
     * @param noteId the id of the note
     * @param storeType the type of the storage.
     * @return the note instance
     * @throws Exception
     */
    Note readNote(String noteId, int storeType) throws Exception;

    /**
     * Create a new note
     * @param noteModel the note to be created
     * @param storeType the stype of storage for the note
     */
    Note createNote(Note noteModel, int storeType) throws Exception;

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

    /**
     * Return the total counts of notes
     * @return the total number of notes
     * @throws Exception
     */
    long count() throws Exception;

}
