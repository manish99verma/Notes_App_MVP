package com.example.notesappmvp.ui.adapter;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.AsyncListDiffer;
import androidx.recyclerview.widget.DiffUtil;
import androidx.recyclerview.widget.RecyclerView;

import com.example.notesappmvp.R;
import com.example.notesappmvp.data.model.Note;
import com.example.notesappmvp.databinding.NoteViewItemBinding;

import java.util.List;

public class NotesAdapter extends RecyclerView.Adapter<NotesAdapter.MyViewHolder> {
    Context context;
    ClickEvent clickEvent;

    private final AsyncListDiffer<Note> mDiffer = new AsyncListDiffer(this, DIFF_CALLBACK);

    public void submitList(List<Note> list) {
        mDiffer.submitList(list);
    }

    public static final DiffUtil.ItemCallback<Note> DIFF_CALLBACK
            = new DiffUtil.ItemCallback<Note>() {
        @Override
        public boolean areItemsTheSame(
                @NonNull Note oldNote, @NonNull Note newNote) {
            // Note properties may have changed if reloaded from the DB, but ID is fixed
            return oldNote.getId() == newNote.getId();
        }

        @Override
        public boolean areContentsTheSame(
                @NonNull Note oldNote, @NonNull Note newNote) {
            // NOTE: if you use equals, your object must properly override Object#equals()
            // Incorrectly returning false here will result in too many animations.
            return oldNote.getId() == newNote.getId();
        }
    };

    private static final int[] noteBackgroundColors = {R.color.note_bg_color_1, R.color.note_bg_color_2, R.color.note_bg_color_3, R.color.note_bg_color_4, R.color.note_bg_color_5, R.color.note_bg_color_6};
    private static int bg_color_index = 0;

    public NotesAdapter(Context context) {
        this.context = context;
    }


    public void setClickEvent(ClickEvent clickEvent) {
        this.clickEvent = clickEvent;
    }

    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        NoteViewItemBinding binding = NoteViewItemBinding.inflate(LayoutInflater.from(parent.getContext()), parent, false);
        return new MyViewHolder(binding, context);
    }

    @Override
    public void onBindViewHolder(@NonNull MyViewHolder holder, int position) {
        holder.bind(mDiffer.getCurrentList().get(position), clickEvent);
    }

    @Override
    public int getItemCount() {
        return mDiffer.getCurrentList().size();
    }

    static class MyViewHolder extends RecyclerView.ViewHolder {
        NoteViewItemBinding mBinding;
        Context context;

        public MyViewHolder(NoteViewItemBinding binding, Context context) {
            super(binding.getRoot());
            this.mBinding = binding;
            this.context = context;
        }

        void bind(Note note, ClickEvent clickEvent) {
            //Loading data
            int color = getBgColor();
            mBinding.backgroundCard.setCardBackgroundColor(ContextCompat.getColor(context, color));

            mBinding.txtTitle.setText(note.getTitle());
            mBinding.txtDate.setText(note.getDate());

            //Click
            mBinding.backgroundCard.setOnClickListener(v -> {
                clickEvent.onClick(note);
            });
        }
    }

    private static int getBgColor() {
        if (bg_color_index >= noteBackgroundColors.length)
            bg_color_index = 0;
        return noteBackgroundColors[bg_color_index++];
    }

    public interface ClickEvent {
        void onClick(Note note);
    }
}
