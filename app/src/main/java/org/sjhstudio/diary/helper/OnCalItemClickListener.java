package org.sjhstudio.diary.helper;

import android.view.View;

import org.sjhstudio.diary.calendar.CalendarViewHolder;

public interface OnCalItemClickListener {
    public void onItemClick(CalendarViewHolder holder, View view, int position);
}
