package com.feedhenry.securenativeandroidtemplate.features.storage;

import android.app.Activity;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RelativeLayout;

import com.feedhenry.securenativeandroidtemplate.R;
import com.feedhenry.securenativeandroidtemplate.domain.models.Note;
import com.feedhenry.securenativeandroidtemplate.mvp.views.BaseFragment;
import com.feedhenry.securenativeandroidtemplate.features.storage.adapters.RVNoteAdapter;
import com.feedhenry.securenativeandroidtemplate.features.storage.presenters.NoteListPresenter;
import com.feedhenry.securenativeandroidtemplate.features.storage.views.NoteListAppView;
import com.feedhenry.securenativeandroidtemplate.features.storage.views.NoteListAppViewImpl;

import java.util.List;

import javax.inject.Inject;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.Unbinder;
import dagger.android.AndroidInjection;

/**
 * A fragment to show all the local notes.
 */

public class NotesListFragment extends BaseFragment<NoteListPresenter, NoteListAppView> {

    /**
     * Interface for listening on the note list events
     */
    public interface NoteListListener {
        void onNoteClicked(final Note note);
    }

    @Inject NoteListPresenter notesListPresenter;
    @Inject RVNoteAdapter notesAdapter;

    @BindView(R.id.notes_list_view)
    RecyclerView rvNotes;

    @BindView(R.id.add_note_btn)
    FloatingActionButton fabAddBtn;

    @BindView(R.id.rl_progress)
    RelativeLayout rlProgress;

    private Unbinder unbinder;
    private View notesListView;

    private NoteListListener noteListListener;

    private RVNoteAdapter.OnItemClickListener onItemClickListener = new RVNoteAdapter.OnItemClickListener() {
        @Override
        public void onNoteItemClicked(Note note) {
            if (notesListPresenter != null && note != null) {
                notesListPresenter.onNoteClicked(note);
            }
        }
    };


    public NotesListFragment() {
        setRetainInstance(true);
    }

    @Override
    public void onAttach(Activity activity) {
        AndroidInjection.inject(this);
        super.onAttach(activity);
        if (activity instanceof NoteListListener) {
            this.noteListListener = (NoteListListener) activity;
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        this.notesListPresenter = null;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, Bundle savedInstanceState) {
        notesListView = inflater.inflate(R.layout.fragment_notes_list, container, false);
        unbinder = ButterKnife.bind(this, notesListView);
        setupRecyclerView();
        return notesListView;
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        rvNotes.setAdapter(null);
        unbinder.unbind();
    }

    @Override
    protected NoteListPresenter initPresenter() {
        return notesListPresenter;
    }

    @Override
    protected NoteListAppView initView() {
        return new NoteListAppViewImpl(this) {
            @Override
            public void renderNotes(List<Note> notesCollection) {
                if (notesCollection != null) {
                    notesAdapter.setNotes(notesCollection);
                }
            }

            @Override
            public void viewNote(Note noteToView) {
                if (noteListListener != null) {
                    noteListListener.onNoteClicked(noteToView);
                }
            }

            @Override
            public void showLoading() {
                rlProgress.setVisibility(View.VISIBLE);
            }

            @Override
            public void hideLoading() {
                rlProgress.setVisibility(View.GONE);
            }
        };
    }

    @Override
    public int getHelpMessageResourceId() {
        return R.string.popup_storage_fragment;
    }

    private void setupRecyclerView() {
        this.notesAdapter.setOnItemClickListener(onItemClickListener);
        this.rvNotes.setLayoutManager(new LinearLayoutManager(getActivity()));
        this.rvNotes.setAdapter(notesAdapter);
    }
}
