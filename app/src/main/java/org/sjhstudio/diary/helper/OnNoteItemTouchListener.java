package org.sjhstudio.diary.helper;

import android.view.MotionEvent;
import android.view.View;

import org.sjhstudio.diary.note.NoteViewHolder;

public interface OnNoteItemTouchListener {
    public void onItemTouch(NoteViewHolder holder, View view, int position, MotionEvent event);
}
