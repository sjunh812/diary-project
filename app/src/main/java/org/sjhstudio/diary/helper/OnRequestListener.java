package org.sjhstudio.diary.helper;


import org.sjhstudio.diary.note.Note;

import java.util.Date;

public interface OnRequestListener {
    public void onRequest(String command);
    public void onRequest(String command, Date date);
    public void onRequestDetailActivity(Note item);
    public void onRequestWriteFragmentFromCal(Date date);
    public boolean checkLocationPermission();
    public void getDateOnly(Date date);
}
