package com.dyzs.listviewcalendar;

import android.app.Activity;
import android.content.Context;
import android.os.Bundle;

/**
 * Created by NKlaus on 2017/12/3.
 */

public class ViewPagerCalendarActivity extends Activity{
    private static final String TAG = ViewPagerCalendarActivity.class.getSimpleName();

    private Context mContext;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_view_pager_calendar);



    }
}
