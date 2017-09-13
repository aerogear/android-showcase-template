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

    @Inject
    public NoteDataStoreFactory(@NonNull Context context) {
        this.context = context;
    }

    public NoteDataStore getDataStore() {
        return new InMemoryNoteStore();
    }
}
