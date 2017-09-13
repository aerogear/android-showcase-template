package com.feedhenry.securenativeandroidtemplate.features.storage.presenters;

import com.feedhenry.securenativeandroidtemplate.domain.callbacks.Callback;
import com.feedhenry.securenativeandroidtemplate.domain.models.Note;
import com.feedhenry.securenativeandroidtemplate.domain.repositories.NoteRepository;
import com.feedhenry.securenativeandroidtemplate.mvp.presenters.BasePresenter;
import com.feedhenry.securenativeandroidtemplate.features.storage.views.NoteListAppView;

import java.util.List;

import javax.inject.Inject;

/**
 * Implement the presenter for the notes list view.
 * The presenter will have access to the NoteRepository to load the notes, and control the views to render the notes once the notes are loaded.
 */

public class NoteListPresenter extends BasePresenter<NoteListAppView> {

    private NoteRepository noteRepository;

    @Inject
    public NoteListPresenter(NoteRepository noteRepo) {
        this.noteRepository = noteRepo;
    }

    @Override
    protected void onViewAttached() {
        super.onViewAttached();
        loadData();
    }

    private void loadData() {
        this.view.showLoading();
        this.noteRepository.listNotes(new Callback<List<Note>>() {
            @Override
            public void onSuccess(List<Note> models) {
                if (view != null) {
                    view.hideLoading();
                    view.renderNotes(models);
                }

            }

            @Override
            public void onError(Throwable error) {
                if (view != null) {
                    view.hideLoading();
                    view.showMessage(error.getMessage());
                }
            }
        });
    }

    public void onNoteClicked(Note note) {
        this.view.viewNote(note);
    }

}
