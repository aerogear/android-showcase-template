package com.aerogear.androidshowcase.features.storage.views;

import com.aerogear.androidshowcase.domain.models.Note;
import com.aerogear.androidshowcase.mvp.views.AppView;

import java.util.List;

/**
 * Define the interface for the view that will show the notes list
 */

public interface NoteListAppView extends AppView {

    void renderNotes(List<Note> notesCollection);

    void viewNote(Note noteToView);

    void createNote();
}
