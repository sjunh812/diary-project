package org.sjhstudio.diary.note;

import java.util.ArrayList;
import java.util.HashMap;

public interface NoteDatabaseCallback {
    public void insertDB(Object[] objs);
    public void insertDB2(Object[] objs);

    public void deleteDB(int id);

    public void updateDB(Note item);
    public void updateDB2(Note item);

    public ArrayList<Note> selectAllDB();
    public ArrayList<Note> selectKeyword(String keyword);
    public int selectAllCount();
    public int selectStarCount();
    public ArrayList<Note> selectPart(int year, int month);
    public HashMap<Integer, Integer> selectMoodCount(boolean isAll, boolean isYear, boolean isMonth);
    public HashMap<Integer, Integer> selectMoodCountWeek(int weekOfDay);
    public int selectLastYear();
}
