package com.aerogear.androidshowcase.domain.store;

import android.util.Log;

import com.aerogear.androidshowcase.domain.models.Note;
import com.aerogear.androidshowcase.features.storage.NotesDetailFragment;
import com.apollographql.apollo.ApolloCall;
import com.apollographql.apollo.ApolloClient;
import com.apollographql.apollo.ApolloSubscriptionCall;
import com.apollographql.apollo.api.Response;
import com.apollographql.apollo.exception.ApolloException;
import com.apollographql.apollo.fetcher.ApolloResponseFetchers;
import com.apollographql.apollo.subscription.WebSocketSubscriptionTransport;

import org.aerogear.mobile.app.CreateNoteMutation;
import org.aerogear.mobile.app.DeleteNoteMutation;
import org.aerogear.mobile.app.ListNotesQuery;
import org.aerogear.mobile.app.NoteCreatedSubscription;
import org.aerogear.mobile.app.ReadNoteQuery;
import org.aerogear.mobile.app.UpdateNoteMutation;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.Future;

import javax.annotation.Nonnull;

import okhttp3.OkHttpClient;

/**
 * Created by dmartin on 23/05/18.
 */

public class GraphqlNoteStore implements NoteDataStore {

    private ApolloClient apolloClient;
    private static final String BASE_URL = "http://10.0.2.2:8000/graphql/";
    private static final String SUBSCRIPTION_BASE_URL = "ws://10.0.2.2:8000/subscriptions";
    private static final String TAG = "GraphqlNoteStore";

    public GraphqlNoteStore() {
        OkHttpClient okHttpClient = new OkHttpClient.Builder()
                .build();

        apolloClient = ApolloClient.builder()
                .serverUrl(BASE_URL)
                .okHttpClient(okHttpClient)
                //.normalizedCache(normalizedCacheFactory, cacheKeyResolver)
                .subscriptionTransportFactory(new WebSocketSubscriptionTransport.Factory(SUBSCRIPTION_BASE_URL, okHttpClient))
                .build();
    }

    @Override
    public Future<Note> createNote(Note note) throws Exception {
        CreateNoteMutation mutation = CreateNoteMutation.builder()
                .id(note.getId())
                .title(note.getTitle())
                .content(note.getContent())
                .timestamp((double)note.getCreatedAt().getTime())
                .build();

        ApolloCall<CreateNoteMutation.Data> createNoteQuery = apolloClient
                .mutate(mutation);

        CompletableFuture<Note> future = new CompletableFuture<Note>();
        createNoteQuery.enqueue(new ApolloCall.Callback<CreateNoteMutation.Data>() {
            @Override
            public void onResponse(@Nonnull Response<CreateNoteMutation.Data> response) {
                Log.d(TAG, "onResponse" + response.data().toString());
                CreateNoteMutation.CreateNote createNote = response.data().createNote();
                Note createdNote = new Note(createNote.id(), createNote.title(), createNote.content(), createNote.timestamp().longValue());
                createdNote.setStoreType(getType());
                future.complete(createdNote);
            }

            @Override
            public void onFailure(@Nonnull ApolloException e) {
                Log.e(TAG, "onFailure", e);
                future.completeExceptionally(e);
            }
        });
        return future;
    }

    @Override
    public Future<Note> updateNote(Note note) throws Exception {
        UpdateNoteMutation mutation = UpdateNoteMutation.builder()
                .id(note.getId())
                .title(note.getTitle())
                .content(note.getContent())
                .build();

        ApolloCall<UpdateNoteMutation.Data> updateNoteQuery = apolloClient
                .mutate(mutation);

        CompletableFuture<Note> future = new CompletableFuture<Note>();
        updateNoteQuery.enqueue(new ApolloCall.Callback<UpdateNoteMutation.Data>() {
            @Override
            public void onResponse(@Nonnull Response<UpdateNoteMutation.Data> response) {
                Log.d(TAG, "onResponse" + response.data().toString());
                UpdateNoteMutation.UpdateNote updateNote = response.data().updateNote();
                Note updatedNote = new Note(updateNote.id(), updateNote.title(), updateNote.content(), updateNote.timestamp().longValue());
                updatedNote.setStoreType(getType());
                future.complete(updatedNote);
            }

            @Override
            public void onFailure(@Nonnull ApolloException e) {
                Log.e(TAG, "onFailure", e);
                future.completeExceptionally(e);
            }
        });
        return future;
    }

    @Override
    public Future<Note> deleteNote(Note note) throws Exception {
        DeleteNoteMutation mutation = DeleteNoteMutation.builder()
                .id(note.getId())
                .build();

        ApolloCall<DeleteNoteMutation.Data> deleteNoteQuery = apolloClient
                .mutate(mutation);
        CompletableFuture<Note> future = new CompletableFuture<>();
        deleteNoteQuery.enqueue(new ApolloCall.Callback<DeleteNoteMutation.Data>() {
            @Override
            public void onResponse(@Nonnull Response<DeleteNoteMutation.Data> response) {
                Log.d(TAG, "onResponse" + response.data().toString());
                DeleteNoteMutation.DeleteNote deleteNote = response.data().deleteNote();
                Note deletedNote = new Note(deleteNote.id(), deleteNote.title(), deleteNote.content(), deleteNote.timestamp().longValue());
                deletedNote.setStoreType(getType());
                future.complete(deletedNote);
            }

            @Override
            public void onFailure(@Nonnull ApolloException e) {
                Log.e(TAG, "onFailure", e);
                future.completeExceptionally(e);
            }
        });
        return future;
    }

    @Override
    public Future<Note> readNote(String noteId) throws Exception {
        ApolloCall<ReadNoteQuery.Data> readNoteQuery = apolloClient
                .query(new ReadNoteQuery(noteId))
                .responseFetcher(ApolloResponseFetchers.NETWORK_ONLY);

        CompletableFuture<Note> future = new CompletableFuture<Note>();
        readNoteQuery.enqueue(new ApolloCall.Callback<ReadNoteQuery.Data>() {
            @Override
            public void onResponse(@Nonnull Response<ReadNoteQuery.Data> response) {
                Log.d(TAG, "onResponse" + response.data().toString());
                ReadNoteQuery.ReadNote readNote = response.data().readNote();
                Note newNote = new Note(readNote.id(), readNote.title(), readNote.content(), readNote.timestamp().longValue());
                newNote.setStoreType(getType());
                future.complete(newNote);
            }

            @Override
            public void onFailure(@Nonnull ApolloException e) {
                Log.e(TAG, "onFailure", e);
                future.completeExceptionally(e);
            }
        });
        return future;
    }

    @Override
    public Future<List<Note>> listNotes() throws Exception {
        ApolloCall<ListNotesQuery.Data> listNotesQuery = apolloClient
                .query(new ListNotesQuery())
                .responseFetcher(ApolloResponseFetchers.NETWORK_ONLY);

        CompletableFuture<List<Note>> future = new CompletableFuture<List<Note>>();
        listNotesQuery.enqueue(new ApolloCall.Callback<ListNotesQuery.Data>() {
            @Override
            public void onResponse(@Nonnull Response<ListNotesQuery.Data> response) {
                Log.d(TAG, "onResponse" + response.data().toString());
                List<ListNotesQuery.ListNote> listNotes = response.data().listNotes();
                List<Note> notes = new ArrayList<>();
                for(ListNotesQuery.ListNote note: listNotes) {
                    Note newNote = new Note(note.id(), note.title(), note.content(), note.timestamp().longValue());
                    newNote.setStoreType(getType());
                    notes.add(newNote);
                }
                future.complete(notes);
            }

            @Override
            public void onFailure(@Nonnull ApolloException e) {
                Log.e(TAG, "onFailure", e);
                future.completeExceptionally(e);
            }
        });
        return future;
    }

    @Override
    public void noteCreated(NotesDetailFragment.SaveNoteListener listener) throws Exception {
        ApolloSubscriptionCall<NoteCreatedSubscription.Data> subscriptionCall = apolloClient
                .subscribe(new NoteCreatedSubscription());
        // This is a simplified subscription example that doesn't handle view lifecycle
        subscriptionCall.execute(new ApolloSubscriptionCall.Callback<NoteCreatedSubscription.Data>() {
            @Override
            public void onResponse(@Nonnull Response<NoteCreatedSubscription.Data> response) {
                Log.d(TAG, "NoteCreatedSubscription onResponse" + response.data().toString());
                NoteCreatedSubscription.NoteCreated createdNote = response.data().noteCreated();
                long createdAt = 0l;
                if (createdNote.timestamp() != null) {
                    createdAt = createdNote.timestamp().longValue();
                }
                Note newNote = new Note(createdNote.id(), createdNote.title(), createdNote.content(), createdAt);
                listener.onNoteSaved(newNote);
            }

            @Override
            public void onFailure(@Nonnull ApolloException e) {
                Log.e(TAG, "NoteCreatedSubscription onFailure", e);

            }

            @Override
            public void onCompleted() {
                Log.d(TAG, "NoteCreatedSubscription onCompleted ");
            }
        });
    }

    @Override
    public int getType() {
        return NoteDataStore.STORE_TYPE_GRAPHQL;
    }

    @Override
    public long count() throws Exception {
        return 0;
    }
}
