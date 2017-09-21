package com.feedhenry.securenativeandroidtemplate.domain.services;

import android.os.AsyncTask;
import android.util.Log;

import com.feedhenry.securenativeandroidtemplate.domain.callbacks.Callback;
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
        Callback<Result> callback;
        NoteRepository noteRepository;
        Exception error;

        NoteTask(NoteRepository noteRepo, Callback<Result> callback) {
            this.noteRepository = noteRepo;
            this.callback = callback;
        }

        @Override
        protected Result doInBackground(Param... params) {
            Result r = null;
            try {
                Param p = null;
                if (params.length > 0) {
                    p = params[0];
                }
                r = performOperation(p);
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

        protected abstract Result performOperation(Param param) throws Exception;
    }

    private class CreateNoteTask extends NoteTask<Note, Void, Note> {

        CreateNoteTask(NoteRepository noteRepo, Callback<Note> callback) {
            super(noteRepo, callback);
        }

        @Override
        protected Note performOperation(Note note) throws Exception {
            return noteRepository.createNote(note);
        }
    }

    private class UpdateNoteTask extends NoteTask<Note, Void, Note> {

        UpdateNoteTask(NoteRepository noteRepo, Callback<Note> callback) {
            super(noteRepo, callback);
        }

        @Override
        protected Note performOperation(Note note) throws Exception {
            return noteRepository.updateNote(note);
        }
    }

    private class ReadNoteTask extends NoteTask<String, Void, Note> {

        ReadNoteTask(NoteRepository noteRepo, Callback<Note> callback) {
            super(noteRepo, callback);
        }

        @Override
        protected Note performOperation(String noteId) throws Exception {
            return noteRepository.readNote(noteId);
        }
    }

    private class DeleteNoteTask extends NoteTask<Note, Void, Note> {

        DeleteNoteTask(NoteRepository noteRepo, Callback<Note> callback) {
            super(noteRepo, callback);
        }

        @Override
        protected Note performOperation(Note note) throws Exception {
            Note deleted = noteRepository.deleteNote(note);
            return deleted;
        }
    }

    private class ListNoteTask extends NoteTask<Void, Void, List<Note>> {

        ListNoteTask(NoteRepository noteRepo, Callback<List<Note>> callback) {
            super(noteRepo, callback);
        }

        @Override
        protected List<Note> performOperation(Void aVoid) throws Exception {
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
    public void listNotes(Callback<List<Note>> callback) {
        new ListNoteTask(this.noteRepo, callback).execute();
    }

    /**
     * Create the given note.
     * @param noteToCreate the note to be created.
     * @param callback the function to be executed when the operation is finished. The callback will be executed on the main UI thread.
     */
    public void createNote(Note noteToCreate, Callback<Note> callback) {
        new CreateNoteTask(this.noteRepo, callback).execute(noteToCreate);
    }

    /**
     * Update the given note
     * @param noteToUpdate the note to be updated.
     * @param callback the function to be executed when the operation is finished. The callback will be executed on the main UI thread.
     */
    public void updateNote(Note noteToUpdate, Callback<Note> callback) {
        new UpdateNoteTask(this.noteRepo, callback).execute(noteToUpdate);
    }

    /**
     * Delete the given note.
     * @param noteToDelete the note to be deleted.
     * @param callback the function to be executed when the operation is finished. The callback will be executed on the main UI thread.
     */
    public void deleteNote(Note noteToDelete, Callback<Note> callback) {
        new DeleteNoteTask(this.noteRepo, callback).execute(noteToDelete);
    }

    /**
     * Read the note details
     * @param noteId the id of the note
     * @param callback the function to be executed when the operation is finished. The callback will be executed on the main UI thread.
     */
    public void readNote(String noteId, Callback<Note> callback) {
        new ReadNoteTask(this.noteRepo, callback).execute(noteId);
    }
}
