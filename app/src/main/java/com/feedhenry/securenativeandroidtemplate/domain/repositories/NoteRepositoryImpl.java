package com.feedhenry.securenativeandroidtemplate.domain.repositories;

import com.feedhenry.securenativeandroidtemplate.domain.callbacks.Callback;
import com.feedhenry.securenativeandroidtemplate.domain.models.Note;
import com.feedhenry.securenativeandroidtemplate.domain.store.NoteDataStore;
import com.feedhenry.securenativeandroidtemplate.domain.store.NoteDataStoreFactory;

import java.util.List;

import javax.inject.Inject;
import javax.inject.Singleton;

/**
 * Implement the CRUDL operations for Notes.
 */
@Singleton
public class NoteRepositoryImpl implements NoteRepository {


    NoteDataStoreFactory noteStoreFactory;
    NoteDataStore noteDataStore;

    @Inject
    public NoteRepositoryImpl(NoteDataStoreFactory noteStoreFactory) {
        this.noteStoreFactory = noteStoreFactory;
        this.noteDataStore = this.noteStoreFactory.getDataStore();
    }

    @Override
    public void listNotes(Callback<List<Note>> cb) {
        this.noteDataStore.listNotes(cb);
    }

    @Override
    public void readNote(String noteId, Callback<Note> cb) {
        this.noteDataStore.readNote(noteId, cb);
    }

    @Override
    public void createNote(Note noteModel, Callback<Note> cb) {
        this.noteDataStore.createNote(noteModel, cb);
    }

    @Override
    public void updateNote(Note noteModel, Callback<Note> cb) {
        this.noteDataStore.updateNote(noteModel, cb);
    }

    @Override
    public void deleteNote(Note noteModel, Callback<Note> cb) {
        this.noteDataStore.deleteNote(noteModel, cb);
    }
}
