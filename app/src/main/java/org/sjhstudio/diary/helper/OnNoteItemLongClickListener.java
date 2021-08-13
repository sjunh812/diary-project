package org.sjhstudio.diary.helper;

import android.view.View;

import org.sjhstudio.diary.note.NoteViewHolder;

public interface OnNoteItemLongClickListener {
    public void onLongClick(NoteViewHolder holder, View view, int position);
}
