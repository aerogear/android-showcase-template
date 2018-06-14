package com.aerogear.androidshowcase.features.storage.presenters;

import com.aerogear.androidshowcase.domain.callbacks.CallbackHandler;
import com.aerogear.androidshowcase.domain.models.Note;
import com.aerogear.androidshowcase.domain.services.NoteCrudlService;
import com.aerogear.androidshowcase.domain.store.NoteDataStore;
import com.aerogear.androidshowcase.features.storage.NotesDetailFragment;
import com.aerogear.androidshowcase.features.storage.NotesListFragment;
import com.aerogear.androidshowcase.mvp.presenters.BasePresenter;
import com.aerogear.androidshowcase.features.storage.views.NoteListAppView;

import java.util.List;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;

import javax.inject.Inject;

/**
 * Implement the presenter for the notes list view.
 * The presenter will have access to the NoteRepository to load the notes, and control the views to render the notes once the notes are loaded.
 */

public class NoteListPresenter extends BasePresenter<NoteListAppView> {

    private NoteCrudlService noteCrudlService;

    @Inject
    public NoteListPresenter(NoteCrudlService noteCrudlService) {
        this.noteCrudlService = noteCrudlService;
    }

    @Override
    protected void onViewAttached() {
        super.onViewAttached();
        loadData();
    }

    private void loadData() {
        this.view.showLoading();
        this.noteCrudlService.listNotes(new CallbackHandler<List<Note>>() {
            @Override
            public void onSuccess(List<Note> models) throws ExecutionException, InterruptedException {
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

    public void onCreateNote() {
        this.view.createNote();
    }

    public void noteCreated(int storeType, NotesDetailFragment.SaveNoteListener listener) {
        try {
            this.noteCrudlService.noteCreated(storeType, listener);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

}
