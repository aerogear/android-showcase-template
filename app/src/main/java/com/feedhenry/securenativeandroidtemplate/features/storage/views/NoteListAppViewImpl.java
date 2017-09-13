package com.feedhenry.securenativeandroidtemplate.features.storage.views;

import android.app.Fragment;

import com.feedhenry.securenativeandroidtemplate.mvp.views.BaseAppView;
import com.feedhenry.securenativeandroidtemplate.features.storage.views.NoteListAppView;

/**
 * The implementation calss for the notes list view. It will wrap around the actual fragment that will be used to render the list.
 */

public abstract class NoteListAppViewImpl extends BaseAppView implements NoteListAppView {

    public NoteListAppViewImpl(Fragment fragment) {
        super(fragment);
    }
}
