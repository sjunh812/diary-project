package org.sjhstudio.diary.helper;

import org.sjhstudio.diary.note.Note;

public interface OnTabItemSelectedListener {
    public void onTabSelected(int position);
    public void showWriteFragment(Note item);
    public void setIsSelected(Boolean flag);
}
