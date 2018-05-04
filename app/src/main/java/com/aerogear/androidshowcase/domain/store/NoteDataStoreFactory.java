package com.aerogear.androidshowcase.domain.store;

import android.content.Context;
import android.support.annotation.NonNull;

import com.aerogear.androidshowcase.domain.models.Note;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.inject.Inject;
import javax.inject.Singleton;

/**
 * Factory class to decide which storage should be used when store notes.
 */
@Singleton
public class NoteDataStoreFactory {

    private Context context;
    private Map<Integer, NoteDataStore> allStores;

    @Inject
    public NoteDataStoreFactory(@NonNull Context context, List<NoteDataStore> stores) {
        this.context = context;
        allStores = new HashMap<Integer, NoteDataStore>();
        for (NoteDataStore store: stores) {
            allStores.put(store.getType(), store);
        }
    }

    public List<NoteDataStore> getAllStores() {
        List<NoteDataStore> dataStores = new ArrayList<NoteDataStore>();
        dataStores.addAll(allStores.values());
        return dataStores;
    }

    public NoteDataStore getDataStoreForNote(Note note) {
        return getDataStoreByType(note.getStoreType());
    }

    public NoteDataStore getDataStoreByType(int storeType) {
        NoteDataStore store = allStores.get(storeType);
        return store;
    }
}
