package org.sjhstudio.diary.note;

import java.io.Serializable;

public class Note implements Serializable {
    private int _id;                // DB 에서 사용될 id
    private int weather;            // 날씨
    private String address;         // 주소
    private String locationX;
    private String locationY;
    private String contents;        // 내용
    private int mood;               // 기분
    private String picture;         // 사진 이미지 경로
    private String createDateStr;   // 일기 작성 일자(yyyy년 MM월 dd일)
    private String createDateStr2;  // 일기 작성 일자(yyyy-MM-dd)
    private String time;            // 일기 작성 시간(PM 5:00)
    private String dayOfWeek;       // 일기 작성 요일(화)
    private int year;               // 날짜 정렬을 위한 int형 년도
    private int day;                // 날짜 정렬을 위한 int형 월
    private int starIndex;          // 즐겨찾기 여부 (0 = 즐찾x, 1 = 즐찾)

    public Note(int _id, int weather, String address, String locationX, String locationY, String contents,
                int mood, String picture, String createDateStr, String time, String dayOfWeek, int year, int day, int starIndex) {
        this._id = _id;
        this.weather = weather;
        this.address = address;
        this.locationX = locationX;
        this.locationY = locationY;
        this.contents = contents;
        this.mood = mood;
        this.picture = picture;
        this.createDateStr = createDateStr;
        this.time = time;
        this.dayOfWeek = dayOfWeek;
        this.year = year;
        this.day = day;
        this.starIndex = starIndex;
    }

    public int get_id() {
        return _id;
    }

    public void set_id(int _id) {
        this._id = _id;
    }

    public int getWeather() {
        return weather;
    }

    public void setWeather(int weather) {
        this.weather = weather;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public String getLocationX() {
        return locationX;
    }

    public void setLocationX(String locationX) {
        this.locationX = locationX;
    }

    public String getLocationY() {
        return locationY;
    }

    public void setLocationY(String locationY) {
        this.locationY = locationY;
    }

    public String getContents() {
        return contents;
    }

    public void setContents(String contents) {
        this.contents = contents;
    }

    public int getMood() {
        return mood;
    }

    public void setMood(int mood) {
        this.mood = mood;
    }

    public String getPicture() {
        return picture;
    }

    public void setPicture(String picture) {
        this.picture = picture;
    }

    public String getCreateDateStr() {
        return createDateStr;
    }

    public void setCreateDateStr(String createDateStr) {
        this.createDateStr = createDateStr;
    }

    public String getTime() {
        return time;
    }

    public void setTime(String time) {
        this.time = time;
    }

    public String getDayOfWeek() {
        return dayOfWeek;
    }

    public void setDayOfWeek(String dayOfWeek) {
        this.dayOfWeek = dayOfWeek;
    }

    public int getYear() {
        return year;
    }

    public void setYear(int year) {
        this.year = year;
    }

    public int getDay() {
        return day;
    }

    public void setDay(int day) {
        this.day = day;
    }

    public String getCreateDateStr2() {
        return createDateStr2;
    }

    public void setCreateDateStr2(String createDateStr2) {
        this.createDateStr2 = createDateStr2;
    }

    public int getStarIndex() {
        return starIndex;
    }

    public void setStarIndex(int starIndex) {
        this.starIndex = starIndex;
    }
}
