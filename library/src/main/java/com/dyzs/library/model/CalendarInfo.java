package com.dyzs.library.model;

import java.util.ArrayList;

public class CalendarInfo implements Comparable<CalendarInfo>{
    public String date = "";
    public int markCount = 0;
    public String category = "";

    public ArrayList<DataTypeInfo> dataTypeInfoList = new ArrayList<>();

    @Override
    public int compareTo(CalendarInfo another) {
        return date.compareTo(another.date);
    }
}