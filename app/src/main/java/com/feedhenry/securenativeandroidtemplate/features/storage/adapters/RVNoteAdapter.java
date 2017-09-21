package com.feedhenry.securenativeandroidtemplate.features.storage.adapters;

import android.content.Context;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.feedhenry.securenativeandroidtemplate.R;
import com.feedhenry.securenativeandroidtemplate.domain.models.Note;

import java.util.Collections;
import java.util.List;

import javax.inject.Inject;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * Implement the RecyclerView adapter.
 */

public class RVNoteAdapter extends RecyclerView.Adapter<RVNoteAdapter.NoteViewHolder> {

    public static class NoteViewHolder extends RecyclerView.ViewHolder {

        @BindView(R.id.singleNoteView)
        CardView noteCardView;
        @BindView(R.id.noteTitle)
        TextView noteTitle;

        NoteViewHolder(View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
        }
    }

    public interface OnItemClickListener {
        void onNoteItemClicked(Note note);
    }


    private OnItemClickListener onItemClickListener;
    private final LayoutInflater layoutInflater;
    List<Note> notes;

    @Inject
    public RVNoteAdapter(Context context) {
        this.layoutInflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        this.notes = Collections.emptyList();
    }

    @Override
    public NoteViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View v = this.layoutInflater.inflate(R.layout.fragment_note, parent, false);
        NoteViewHolder nvh = new NoteViewHolder(v);
        return nvh;
    }

    @Override
    public void onBindViewHolder(NoteViewHolder holder, int position) {
        final Note selectedNote = notes.get(position);
        holder.noteTitle.setText(selectedNote.getTitle());
        holder.itemView.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v) {
                if (onItemClickListener != null) {
                    onItemClickListener.onNoteItemClicked(selectedNote);
                }
            }
        });
    }

    @Override
    public int getItemCount() {
        return this.notes.size();
    }

    public void setNotes(List<Note> notes) {
        if (notes != null) {
            this.notes = notes;
            this.notifyDataSetChanged();
        }
    }

    public void setOnItemClickListener(OnItemClickListener onItemClickListener) {
        this.onItemClickListener = onItemClickListener;
    }
}
