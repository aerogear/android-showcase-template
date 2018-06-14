package com.aerogear.androidshowcase.domain.repositories;

import com.aerogear.androidshowcase.domain.models.Note;
import com.aerogear.androidshowcase.domain.store.NoteDataStore;
import com.aerogear.androidshowcase.domain.store.NoteDataStoreFactory;
import com.aerogear.androidshowcase.domain.store.NoteStoreException;
import com.aerogear.androidshowcase.features.storage.NotesDetailFragment;
import com.aerogear.androidshowcase.features.storage.NotesListFragment;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Future;

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
    }

    @Override
    public List<Note> listNotes() throws Exception {
        List<Note> notes = new ArrayList<Note>();
        List<NoteDataStore> stores = this.noteStoreFactory.getAllStores();
        for (NoteDataStore store: stores) {
            notes.addAll(store.listNotes().get());
        }
        return notes;
    }

    @Override
    public Note readNote(String noteId) throws Exception {
        Note readNote = null;
        List<NoteDataStore> stores = this.noteStoreFactory.getAllStores();
        for (NoteDataStore store:stores) {
            readNote = store.readNote(noteId).get();
            if (readNote != null) {
                break;
            }
        }
        return readNote;
    }

    @Override
    public Note readNote(String noteId, int storeType) throws Exception {
        NoteDataStore dataStore = this.noteStoreFactory.getDataStoreByType(storeType);
        if (dataStore != null) {
            return dataStore.readNote(noteId).get();
        } else {
            throw new NoteStoreException("invalid store type " + storeType);
        }
    }

    @Override
    public Note createNote(Note noteModel, int storeType) throws Exception {
        noteModel.setStoreType(storeType);
        NoteDataStore dataStore = this.noteStoreFactory.getDataStoreByType(storeType);
        if (dataStore != null) {
            return dataStore.createNote(noteModel).get();
        } else {
            throw new NoteStoreException("invalid store type " + storeType);
        }
    }

    @Override
    public Note updateNote(Note noteModel) throws Exception {
        int storeType = noteModel.getStoreType();
        NoteDataStore dataStore = this.noteStoreFactory.getDataStoreByType(storeType);
        if (dataStore != null) {
            return dataStore.updateNote(noteModel).get();
        } else {
            throw new NoteStoreException("invalid store type " + storeType);
        }
    }

    @Override
    public Note deleteNote(Note noteModel) throws Exception {
        int storeType = noteModel.getStoreType();
        NoteDataStore dataStore = this.noteStoreFactory.getDataStoreByType(storeType);
        if (dataStore != null) {
            return dataStore.deleteNote(noteModel).get();
        } else {
            throw new NoteStoreException("invalid store type " + storeType);
        }
    }

    @Override
    public void noteCreated(int storeType, NotesDetailFragment.SaveNoteListener listener) throws Exception {
        NoteDataStore dataStore = this.noteStoreFactory.getDataStoreByType(storeType);
        if (dataStore != null) {
            dataStore.noteCreated(listener);
        } else {
            throw new NoteStoreException("invalid store type " + storeType);
        }

    }

    @Override
    public long count() throws Exception {
        List<Note> notes = new ArrayList<Note>();
        List<NoteDataStore> stores = this.noteStoreFactory.getAllStores();
        int total = 0;
        for (NoteDataStore store : stores) {
            total += store.count();
        }
        return total;
    }


}
