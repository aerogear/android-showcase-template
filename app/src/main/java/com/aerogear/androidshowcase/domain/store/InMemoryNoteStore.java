package com.aerogear.androidshowcase.domain.store;

import android.support.annotation.NonNull;

import com.aerogear.androidshowcase.domain.models.Note;
import com.aerogear.androidshowcase.features.storage.NotesDetailFragment;
import com.aerogear.androidshowcase.features.storage.NotesListFragment;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

/**
 * An in-memory storage for the notes.
 */
public class InMemoryNoteStore implements NoteDataStore {

    private Map<String, Note> inMemoryStore = new HashMap<String, Note>();

    @Override
    public Future<Note> createNote(Note note) {
        CompletableFuture<Note> future = new CompletableFuture<>();
        if (!inMemoryStore.containsKey(note.getId())) {
            inMemoryStore.put(note.getId(), note);
        }
        future.complete(note);
        return future;
    }

    @Override
    public Future<Note> updateNote(Note note) {
        CompletableFuture<Note> future = new CompletableFuture<>();
        inMemoryStore.put(note.getId(), note);
        future.complete(note);
        return future;
    }

    @Override
    public Future<Note> deleteNote(Note note) {
        CompletableFuture<Note> future = new CompletableFuture<>();
        inMemoryStore.remove(note.getId());
        future.complete(note);
        return future;
    }

    @Override
    public Future<Note> readNote(String noteId) {
        CompletableFuture<Note> future = new CompletableFuture<>();
        Note note = inMemoryStore.get(noteId);
        future.complete(note);
        return future;
    }

    @Override
    public Future<List<Note>> listNotes() {
        CompletableFuture<List<Note>> future = new CompletableFuture<>();
        List<Note> notes = new ArrayList<Note>();
        notes.addAll(inMemoryStore.values());
        future.complete(notes);
        return future;
    }

    @Override
    public void noteCreated(NotesDetailFragment.SaveNoteListener listener) throws Exception {
        // noop
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
