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
    public List<Note> listNotes() throws Exception {
        return this.noteDataStore.listNotes();
    }

    @Override
    public Note readNote(String noteId) throws Exception {
        return this.noteDataStore.readNote(noteId);
    }

    @Override
    public Note createNote(Note noteModel) throws Exception {
        return this.noteDataStore.createNote(noteModel);
    }

    @Override
    public Note updateNote(Note noteModel) throws Exception {
        return this.noteDataStore.updateNote(noteModel);
    }

    @Override
    public Note deleteNote(Note noteModel) throws Exception {
        return this.noteDataStore.deleteNote(noteModel);
    }
}
