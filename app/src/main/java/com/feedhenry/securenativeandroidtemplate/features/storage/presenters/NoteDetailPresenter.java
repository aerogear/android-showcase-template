package com.feedhenry.securenativeandroidtemplate.features.storage.presenters;

import com.feedhenry.securenativeandroidtemplate.R;
import com.feedhenry.securenativeandroidtemplate.domain.callbacks.Callback;
import com.feedhenry.securenativeandroidtemplate.domain.models.Note;
import com.feedhenry.securenativeandroidtemplate.domain.repositories.NoteRepository;
import com.feedhenry.securenativeandroidtemplate.features.storage.views.NoteDetailAppView;
import com.feedhenry.securenativeandroidtemplate.mvp.presenters.BasePresenter;

import javax.inject.Inject;

/**
 * Implement the presenter for the note details view.
 * It will be responsible for read/update/save the note in the repository, and update the view accordingly.
 */

public class NoteDetailPresenter extends BasePresenter<NoteDetailAppView> {

    private NoteRepository noteRepository;

    @Inject
    NoteDetailPresenter(NoteRepository noteRepository) {
        this.noteRepository = noteRepository;
    }

    public void createNote(String noteTitle, String noteContent) {
        this.view.showLoading();
        Note note = new Note(noteTitle, noteContent);
        this.noteRepository.createNote(note, new Callback<Note>() {
            @Override
            public void onSuccess(Note note) {
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

    public void loadNoteWithId(String noteId) {
        this.view.showLoading();
        this.noteRepository.readNote(noteId, new Callback<Note>() {
            @Override
            public void onSuccess(Note note) {
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
        this.noteRepository.updateNote(note, new Callback<Note>() {
            @Override
            public void onSuccess(Note updatedNote) {
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
}
