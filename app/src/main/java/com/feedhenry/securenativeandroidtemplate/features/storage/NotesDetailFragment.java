package com.feedhenry.securenativeandroidtemplate.features.storage;

import android.app.Activity;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RelativeLayout;

import com.feedhenry.securenativeandroidtemplate.R;
import com.feedhenry.securenativeandroidtemplate.domain.Constants;
import com.feedhenry.securenativeandroidtemplate.domain.models.Note;
import com.feedhenry.securenativeandroidtemplate.features.storage.presenters.NoteDetailPresenter;
import com.feedhenry.securenativeandroidtemplate.features.storage.views.NoteDetailAppView;
import com.feedhenry.securenativeandroidtemplate.features.storage.views.NoteDetailAppViewImpl;
import com.feedhenry.securenativeandroidtemplate.mvp.views.BaseFragment;

import javax.inject.Inject;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import butterknife.OnTextChanged;
import butterknife.Unbinder;
import dagger.android.AndroidInjection;

/**
 * The note details view. It can load an existing note to allow user to update, or create a new note.
 */

public class NotesDetailFragment extends BaseFragment<NoteDetailPresenter, NoteDetailAppView> {

    public static final String TAG = "noteDetails";

    public interface SaveNoteListener {
        void onNoteSaved(Note note);
    }

    @Inject
    NoteDetailPresenter noteDetailPresenter;

    @BindView(R.id.note_title_field)
    EditText titleField;

    @BindView(R.id.note_content_field)
    EditText contentField;

    @BindView(R.id.rl_progress)
    RelativeLayout progressBar;

    @BindView(R.id.delete_note_btn)
    Button deleteButton;

    private View noteCreateView;
    private Unbinder unbinder;

    private Note existingNote;

    private SaveNoteListener noteCreationListener;

    public NotesDetailFragment() {
    }

    /**
     * Create the note details fragment. It can either load an existing note, or create a new one.
     * @param note the note to load in the fragment. If it is set, the fragment will be used to update the note, otherwise the fragment will be used to create a new note.
     * @return the fragment to load
     */
    public static NotesDetailFragment forNote(Note note) {
        NotesDetailFragment detailsFragment = new NotesDetailFragment();
        if (note != null) {
            Bundle args = new Bundle();
            args.putString(Constants.NOTE_FIELDS.ID_FIELD, note.getId());
            detailsFragment.setArguments(args);
        }
        return detailsFragment;
    }

    @Override
    public void onAttach(Activity activity) {
        AndroidInjection.inject(this);
        super.onAttach(activity);
        if (activity instanceof SaveNoteListener) {
            noteCreationListener = (SaveNoteListener) activity;
        }
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, Bundle savedInstanceState) {
        noteCreateView = inflater.inflate(R.layout.fragment_note_edit, container, false);
        unbinder = ButterKnife.bind(this, noteCreateView);
        return noteCreateView;
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        Bundle args = this.getArguments();
        if (args != null) {
            setNoteFields(args);
        } else {
            clearNoteFields();
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        unbinder.unbind();
    }

    @Override
    protected NoteDetailPresenter initPresenter() {
        return noteDetailPresenter;
    }

    @Override
    protected NoteDetailAppView initView() {
        return new NoteDetailAppViewImpl(this) {
            @Override
            public void onNoteSaved(Note note) {
                if (noteCreationListener != null) {
                    noteCreationListener.onNoteSaved(note);
                }
            }

            @Override
            public void showLoading() {
                progressBar.setVisibility(View.VISIBLE);
            }

            @Override
            public void hideLoading() {
                progressBar.setVisibility(View.GONE);
            }

            @Override
            public void loadNote(Note note) {
                existingNote = note;
                titleField.setText(note.getTitle());
                contentField.setText(note.getContent());
                deleteButton.setVisibility(View.VISIBLE);
            }
        };
    }

    @Override
    public int getHelpMessageResourceId() {
        return R.string.popup_storage_fragment;
    }

    @OnClick(R.id.save_note_btn)
    public void saveNote() {
        String noteTitle = titleField.getText().toString();
        if (TextUtils.isEmpty(noteTitle)) {
            titleField.setError(getActivity().getString(R.string.error_missing_note_title));
            titleField.requestFocus();
            return;
        }
        String noteContent = contentField.getText().toString();
        if (existingNote != null) {
            existingNote.setTitle(noteTitle);
            existingNote.setContent(noteContent);
            noteDetailPresenter.updateNote(existingNote);
        } else {
            noteDetailPresenter.createNote(noteTitle, noteContent);
        }
    }

    @OnClick(R.id.delete_note_btn)
    public void deleteNote() {
        if (existingNote != null) {
            noteDetailPresenter.deleteNote(existingNote);
        }
    }

    @OnTextChanged(R.id.note_title_field)
    public void onTextChange(CharSequence text) {
        if (!TextUtils.isEmpty(text)) {
            titleField.setError(null);
        }
    }

    private void setNoteFields(Bundle args) {
        String noteId = args.getString(Constants.NOTE_FIELDS.ID_FIELD);
        if (!TextUtils.isEmpty(noteId)) {
            noteDetailPresenter.loadNoteWithId(noteId);
        }
    }

    private void clearNoteFields() {
        titleField.getText().clear();
        contentField.getText().clear();
    }
}
