package com.aerogear.androidshowcase.domain.store;

import com.aerogear.androidshowcase.domain.models.Note;

import java.util.List;

import static junit.framework.Assert.assertEquals;
import static junit.framework.Assert.assertNotNull;

/**
 * Created by weili on 25/09/2017.
 */

public class NoteStoreTestBase {

    protected void noteCRUDL(NoteDataStore noteStore) throws Exception {
        String noteTitle = "testNoteTitle";
        String noteContent = "this is a test note";
        //make sure no existing notes
        Note testNote = new Note(noteTitle, noteContent);
        List<Note> notes = noteStore.listNotes();
        assertEquals(0, notes.size());

        //create a note
        Note created = noteStore.createNote(testNote);
        assertNotNull(created);
        notes = noteStore.listNotes();
        assertEquals(1, notes.size());
        Note firstNoteInList = notes.get(0);
        assertNotNull(firstNoteInList.getId());
        assertNotNull(firstNoteInList.getTitle());
        assertNotNull(firstNoteInList.getCreatedAt());

        //read a note
        Note readNote = noteStore.readNote(testNote.getId());
        assertNotNull(readNote);
        assertEquals(readNote.getTitle(), testNote.getTitle());
        assertEquals(readNote.getContent(), testNote.getContent());
        assertEquals(readNote.getId(), testNote.getId());

        //update a note
        String titleUpdate = "testNoteTitleUpdated";
        testNote.setTitle(titleUpdate);
        Note updatedNote = noteStore.updateNote(testNote);
        assertNotNull(updatedNote);
        readNote = noteStore.readNote(testNote.getId());
        assertEquals(readNote.getTitle(), titleUpdate);

        //delete a note
        noteStore.deleteNote(testNote);
        notes = noteStore.listNotes();
        assertEquals(0, notes.size());
    }
}
