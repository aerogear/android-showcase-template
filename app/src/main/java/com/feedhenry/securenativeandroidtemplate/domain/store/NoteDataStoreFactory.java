package com.feedhenry.securenativeandroidtemplate.domain.store;

import android.content.Context;
import android.support.annotation.NonNull;

import javax.inject.Inject;
import javax.inject.Singleton;

/**
 * Factory class to decide which storage should be used when store notes.
 */
@Singleton
public class NoteDataStoreFactory {

    private Context context;
    private NoteDataStore dataStore;

    @Inject
    public NoteDataStoreFactory(@NonNull Context context, NoteDataStore dataStore) {
        this.context = context;
        this.dataStore = dataStore;
    }

    public NoteDataStore getDataStore() {
        return this.dataStore;
    }
}
