package com.feedhenry.securenativeandroidtemplate.domain.services;

import com.feedhenry.securenativeandroidtemplate.BuildConfig;
import com.feedhenry.securenativeandroidtemplate.domain.callbacks.Callback;
import com.feedhenry.securenativeandroidtemplate.domain.models.Note;
import com.feedhenry.securenativeandroidtemplate.domain.repositories.NoteRepository;

import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnit;
import org.mockito.junit.MockitoRule;
import org.robolectric.RobolectricTestRunner;
import org.robolectric.annotation.Config;

import java.util.ArrayList;
import java.util.List;

import static junit.framework.Assert.assertEquals;
import static junit.framework.Assert.assertNotNull;
import static junit.framework.Assert.assertNull;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

/**
 * Created by weili on 20/09/2017.
 */

@RunWith(RobolectricTestRunner.class)
@Config(constants = BuildConfig.class)
public class NoteCrudlServiceTest {

    @Rule
    public MockitoRule rule = MockitoJUnit.rule();

    @Mock
    NoteRepository noteRepository;

    @InjectMocks NoteCrudlService serviceToTest;

    @Test
    public void testService() throws Exception {
        String testTitle = "testTitle";
        String testContent = "testContent";
        Note note = new Note(testTitle, testContent);
        List<Note> notes = new ArrayList<Note>();
        notes.add(note);
        when(noteRepository.createNote(any(Note.class))).thenReturn(note);
        when(noteRepository.readNote(anyString())).thenReturn(note);
        when(noteRepository.updateNote(any(Note.class))).thenReturn(note);
        when(noteRepository.listNotes()).thenReturn(notes);
        when(noteRepository.deleteNote(any(Note.class))).thenReturn(note);

        serviceToTest.createNote(note, new Callback<Note>() {
            @Override
            public void onSuccess(Note models) {
                assertNotNull(models);
            }

            @Override
            public void onError(Throwable error) {
                assertNotNull(error);
            }
        });
        verify(noteRepository, times(1)).createNote(any(Note.class));

        serviceToTest.updateNote(note, new Callback<Note>() {
            @Override
            public void onSuccess(Note models) {
                assertNotNull(models);
            }

            @Override
            public void onError(Throwable error) {
                assertNull(error);
            }
        });
        verify(noteRepository, times(1)).updateNote(any(Note.class));

        serviceToTest.readNote(note.getId(), new Callback<Note>() {
            @Override
            public void onSuccess(Note models) {
                assertNotNull(models);
            }

            @Override
            public void onError(Throwable error) {
                assertNull(error);
            }
        });
        verify(noteRepository, times(1)).readNote(anyString());

        serviceToTest.listNotes(new Callback<List<Note>>() {
            @Override
            public void onSuccess(List<Note> models) {
                assertEquals(models.size(), 1);
            }

            @Override
            public void onError(Throwable error) {
                assertNull(error);
            }
        });
        verify(noteRepository, times(1)).listNotes();

        serviceToTest.deleteNote(note, new Callback<Note>() {
            @Override
            public void onSuccess(Note models) {
                assertNotNull(models);
            }

            @Override
            public void onError(Throwable error) {
                assertNull(error);
            }
        });
        verify(noteRepository, times(1)).deleteNote(any(Note.class));
    }

}
