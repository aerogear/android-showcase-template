package com.aerogear.androidshowcase.features.storage.presenters;

import com.aerogear.androidshowcase.R;
import com.aerogear.androidshowcase.domain.callbacks.CallbackHandler;
import com.aerogear.androidshowcase.domain.models.Note;
import com.aerogear.androidshowcase.domain.services.NoteCrudlService;
import com.aerogear.androidshowcase.domain.store.NoteDataStore;
import com.aerogear.androidshowcase.features.storage.views.NoteDetailAppView;
import com.aerogear.androidshowcase.mvp.presenters.BasePresenter;

import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;

import javax.inject.Inject;

/**
 * Implement the presenter for the note details view.
 * It will be responsible for read/update/save the note in the repository, and update the view accordingly.
 */

public class NoteDetailPresenter extends BasePresenter<NoteDetailAppView> {

    private NoteCrudlService noteCrudlService;

    @Inject
    NoteDetailPresenter(NoteCrudlService noteCrudlService) {
        this.noteCrudlService = noteCrudlService;
    }

    public void createNote(String noteTitle, String noteContent, int storeType) {
        this.view.showLoading();
        Note note = new Note(noteTitle, noteContent);
        this.noteCrudlService.createNote(note, storeType, new CallbackHandler<Note>() {
            @Override
            public void onSuccess(Note note) throws ExecutionException, InterruptedException {
                view.hideLoading();
                view.showMessage(R.string.info_note_saved);
                view.onNoteSaved(note);
            }

            @Override
            public void onError(Throwable error) {
                view.hideLoading();
                view.showMessage(R.string.error_failed_save_note);
            }
        });
    }

    public void loadNoteWithId(String noteId, int storeType) {
        this.view.showLoading();
        this.noteCrudlService.readNote(noteId, storeType, new CallbackHandler<Note>() {
            @Override
            public void onSuccess(Note note) throws ExecutionException, InterruptedException {
                view.hideLoading();
                view.loadNote(note);
            }

            @Override
            public void onError(Throwable error) {
                view.hideLoading();
                view.showMessage(R.string.error_failed_read_note);
            }
        });
    }

    public void updateNote(Note note) {
        this.view.showLoading();
        this.noteCrudlService.updateNote(note, new CallbackHandler<Note>() {
            @Override
            public void onSuccess(Note updatedNote) throws ExecutionException, InterruptedException {
                view.hideLoading();
                view.showMessage(R.string.info_note_saved);
                view.onNoteSaved(updatedNote);
            }

            @Override
            public void onError(Throwable error) {
                view.showMessage(R.string.error_failed_save_note);
            }
        });
    }

    public void deleteNote(final Note note) {
        this.view.showLoading();
        this.noteCrudlService.deleteNote(note, new CallbackHandler<Note>() {
            @Override
            public void onSuccess(Note note) throws ExecutionException, InterruptedException {
                view.hideLoading();
                view.showMessage(R.string.info_note_deleted);
                view.onNoteSaved(note);
            }

            @Override
            public void onError(Throwable error) {
                view.showMessage(R.string.error_failed_delete_note);
            }
        });
    }
}
