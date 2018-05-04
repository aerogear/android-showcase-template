package com.aerogear.androidshowcase.features.storage.views;

import android.app.Fragment;

import com.aerogear.androidshowcase.mvp.views.BaseAppView;

/**
 * The implementation calss for the notes list view. It will wrap around the actual fragment that will be used to render the list.
 */

public abstract class NoteListAppViewImpl extends BaseAppView implements NoteListAppView {

    public NoteListAppViewImpl(Fragment fragment) {
        super(fragment);
    }
}
