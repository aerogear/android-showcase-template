package com.feedhenry.securenativeandroidtemplate.domain.store;

import com.feedhenry.securenativeandroidtemplate.domain.models.Note;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * An in-memory storage for the notes.
 */
public class InMemoryNoteStore implements NoteDataStore {

    private Map<String, Note> inMemoryStore = new HashMap<String, Note>();

    @Override
    public Note createNote(Note note) {
        if (!inMemoryStore.containsKey(note.getId())) {
            inMemoryStore.put(note.getId(), note);
        }
        return note;
    }

    @Override
    public Note updateNote(Note note) {
        inMemoryStore.put(note.getId(), note);
        return note;
    }

    @Override
    public Note deleteNote(Note note) {
        inMemoryStore.remove(note.getId());
        return note;
    }

    @Override
    public Note readNote(String noteId) {
        Note note = inMemoryStore.get(noteId);
        return note;
    }

    @Override
    public List<Note> listNotes() {
        List<Note> notes =  new ArrayList<Note>();
        notes.addAll(inMemoryStore.values());
        return notes;
    }

    @Override
    public int getType() {
        return STORE_TYPE_INMEMORY;
    }

    @Override
    public long count() throws Exception {
        return inMemoryStore.values().size();
    }
}
