package com.feedhenry.securenativeandroidtemplate.features.storage.views;

import android.app.Fragment;

import com.feedhenry.securenativeandroidtemplate.mvp.views.BaseAppView;

/**
 * Base class for the note details view.
 */

public abstract class NoteDetailAppViewImpl extends BaseAppView implements NoteDetailAppView {
    public NoteDetailAppViewImpl(Fragment fragment) {
        super(fragment);
    }
}
