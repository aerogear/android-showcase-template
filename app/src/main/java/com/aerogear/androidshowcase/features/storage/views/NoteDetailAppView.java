package com.aerogear.androidshowcase.features.storage.views;

import com.aerogear.androidshowcase.domain.models.Note;
import com.aerogear.androidshowcase.mvp.views.AppView;

/**
 * The interface for the note details view
 */

public interface NoteDetailAppView extends AppView {

    public void onNoteSaved(Note note);

    public void loadNote(Note note);
}
