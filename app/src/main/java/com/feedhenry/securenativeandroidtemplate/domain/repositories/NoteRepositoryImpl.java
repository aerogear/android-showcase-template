package com.feedhenry.securenativeandroidtemplate.domain.repositories;

import com.feedhenry.securenativeandroidtemplate.domain.callbacks.Callback;
import com.feedhenry.securenativeandroidtemplate.domain.models.Note;
import com.feedhenry.securenativeandroidtemplate.domain.store.NoteDataStore;
import com.feedhenry.securenativeandroidtemplate.domain.store.NoteDataStoreFactory;
import com.feedhenry.securenativeandroidtemplate.domain.store.NoteStoreException;

import java.util.ArrayList;
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
    }

    @Override
    public List<Note> listNotes() throws Exception {
        List<Note> notes = new ArrayList<Note>();
        List<NoteDataStore> stores = this.noteStoreFactory.getAllStores();
        for (NoteDataStore store: stores) {
            notes.addAll(store.listNotes());
        }
        return notes;
    }

    @Override
    public Note readNote(String noteId) throws Exception {
        Note readNote = null;
        List<NoteDataStore> stores = this.noteStoreFactory.getAllStores();
        for (NoteDataStore store:stores) {
            readNote = store.readNote(noteId);
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
            return dataStore.readNote(noteId);
        } else {
            throw new NoteStoreException("invalid store type " + storeType);
        }
    }

    @Override
    public Note createNote(Note noteModel, int storeType) throws Exception {
        noteModel.setStoreType(storeType);
        NoteDataStore dataStore = this.noteStoreFactory.getDataStoreByType(storeType);
        if (dataStore != null) {
            return dataStore.createNote(noteModel);
        } else {
            throw new NoteStoreException("invalid store type " + storeType);
        }
    }

    @Override
    public Note updateNote(Note noteModel) throws Exception {
        int storeType = noteModel.getStoreType();
        NoteDataStore dataStore = this.noteStoreFactory.getDataStoreByType(storeType);
        if (dataStore != null) {
            return dataStore.updateNote(noteModel);
        } else {
            throw new NoteStoreException("invalid store type " + storeType);
        }
    }

    @Override
    public Note deleteNote(Note noteModel) throws Exception {
        int storeType = noteModel.getStoreType();
        NoteDataStore dataStore = this.noteStoreFactory.getDataStoreByType(storeType);
        if (dataStore != null) {
            return dataStore.deleteNote(noteModel);
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
