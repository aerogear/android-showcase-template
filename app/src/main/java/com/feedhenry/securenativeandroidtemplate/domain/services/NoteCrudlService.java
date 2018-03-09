package com.feedhenry.securenativeandroidtemplate.domain.services;

import android.os.AsyncTask;
import android.util.Log;

import com.feedhenry.securenativeandroidtemplate.domain.callbacks.CallbackHandler;
import com.feedhenry.securenativeandroidtemplate.domain.models.Note;
import com.feedhenry.securenativeandroidtemplate.domain.repositories.NoteRepository;

import java.util.List;

import javax.inject.Inject;

/**
 * Perform CRUDL operations for the notes.
 */

public class NoteCrudlService {

    private static final int THREAD_NUMBER = 1;

    private static final String TAG = "NoteCrudlService";

    private abstract class NoteTask<Param, Progress, Result> extends AsyncTask<Param, Progress, Result> {
        CallbackHandler<Result> callback;
        NoteRepository noteRepository;
        Exception error;

        NoteTask(NoteRepository noteRepo, CallbackHandler<Result> callback) {
            this.noteRepository = noteRepo;
            this.callback = callback;
        }

        @Override
        protected Result doInBackground(Param... params) {
            Result r = null;
            try {
                r = performOperation(params);
            } catch (Exception e) {
                error = e;
            }
            return r;
        }

        @Override
        protected void onPostExecute(Result result) {
            if (error != null) {
                Log.e(TAG, error.getMessage(), error);
                callback.onError(error);
            } else {
                callback.onSuccess(result);
            }
        }

        protected abstract Result performOperation(Param... params) throws Exception;
    }

    private class CreateNoteTask extends NoteTask<Object, Void, Note> {

        CreateNoteTask(NoteRepository noteRepo, CallbackHandler<Note> callback) {
            super(noteRepo, callback);
        }

        @Override
        protected Note performOperation(Object... params) throws Exception {
            Note noteToCreate = (Note) params[0];
            int storeType = (int) params[1];
            return noteRepository.createNote(noteToCreate, storeType);
        }
    }

    private class UpdateNoteTask extends NoteTask<Note, Void, Note> {

        UpdateNoteTask(NoteRepository noteRepo, CallbackHandler<Note> callback) {
            super(noteRepo, callback);
        }

        @Override
        protected Note performOperation(Note... notes) throws Exception {
            return noteRepository.updateNote(notes[0]);
        }
    }

    private class ReadNoteTask extends NoteTask<Object, Void, Note> {

        ReadNoteTask(NoteRepository noteRepo, CallbackHandler<Note> callback) {
            super(noteRepo, callback);
        }

        @Override
        protected Note performOperation(Object... params) throws Exception {
            String noteId = (String) params[0];
            int storeType = (int) params[1];
            return noteRepository.readNote(noteId, storeType);
        }
    }

    private class DeleteNoteTask extends NoteTask<Note, Void, Note> {

        DeleteNoteTask(NoteRepository noteRepo, CallbackHandler<Note> callback) {
            super(noteRepo, callback);
        }

        @Override
        protected Note performOperation(Note... notes) throws Exception {
            Note deleted = noteRepository.deleteNote(notes[0]);
            return deleted;
        }
    }

    private class ListNoteTask extends NoteTask<Void, Void, List<Note>> {

        ListNoteTask(NoteRepository noteRepo, CallbackHandler<List<Note>> callback) {
            super(noteRepo, callback);
        }

        @Override
        protected List<Note> performOperation(Void... aVoid) throws Exception {
            return noteRepository.listNotes();
        }
    }


    NoteRepository noteRepo;

    @Inject
    public NoteCrudlService(NoteRepository noteRepoImpl) {
        this.noteRepo = noteRepoImpl;
    }

    /**
     * List the notes.
     * @param callback the function to be executed when the operation is finished. The callback will be executed on the main UI thread.
     */
    public void listNotes(CallbackHandler<List<Note>> callback) {
        new ListNoteTask(this.noteRepo, callback).execute();
    }

    /**
     * Create the given note.
     * @param noteToCreate the note to be created.
     * @param storeType the storage type of the note
     * @param callback the function to be executed when the operation is finished. The callback will be executed on the main UI thread.
     */
    public void createNote(Note noteToCreate, int storeType, CallbackHandler<Note> callback) {
        new CreateNoteTask(this.noteRepo, callback).execute(noteToCreate, storeType);
    }

    /**
     * Update the given note
     * @param noteToUpdate the note to be updated.
     * @param callback the function to be executed when the operation is finished. The callback will be executed on the main UI thread.
     */
    public void updateNote(Note noteToUpdate, CallbackHandler<Note> callback) {
        new UpdateNoteTask(this.noteRepo, callback).execute(noteToUpdate);
    }

    /**
     * Delete the given note.
     * @param noteToDelete the note to be deleted.
     * @param callback the function to be executed when the operation is finished. The callback will be executed on the main UI thread.
     */
    public void deleteNote(Note noteToDelete, CallbackHandler<Note> callback) {
        new DeleteNoteTask(this.noteRepo, callback).execute(noteToDelete);
    }

    /**
     * Read the note details
     * @param noteId the id of the note
     * @param storeType the storage type of the note
     * @param callback the function to be executed when the operation is finished. The callback will be executed on the main UI thread.
     */
    public void readNote(String noteId, int storeType, CallbackHandler<Note> callback) {
        new ReadNoteTask(this.noteRepo, callback).execute(noteId, storeType);
    }
}
