package org.sjhstudio.diary.helper;

import android.view.View;

import org.sjhstudio.diary.note.NoteViewHolder;

public interface OnNoteItemClickListener {
    public void onItemClick(NoteViewHolder holder, View view, int position);
}
