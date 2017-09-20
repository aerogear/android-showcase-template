package com.feedhenry.securenativeandroidtemplate.features.storage.views;

import com.feedhenry.securenativeandroidtemplate.domain.models.Note;
import com.feedhenry.securenativeandroidtemplate.mvp.views.AppView;

/**
 * The interface for the note details view
 */

public interface NoteDetailAppView extends AppView {

    public void onNoteSaved(Note note);

    public void loadNote(Note note);
}
