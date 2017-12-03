package com.dyzs.listviewcalendar;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

import com.dyzs.library.activity.CalendarActivity;


public class MainActivity extends Activity implements View.OnClickListener{
    private static final String TAG = MainActivity.class.getSimpleName();


    private TextView goto_list_view_calendar, goto_view_pager_calendar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);


        initView();

        initEvent();
    }

    private void initView () {
        goto_list_view_calendar = (TextView) findViewById(R.id.goto_list_view_calendar);
        goto_view_pager_calendar = (TextView) findViewById(R.id.goto_view_pager_calendar);
    }




    private void initEvent () {
        goto_list_view_calendar.setOnClickListener(this);
        goto_view_pager_calendar.setOnClickListener(this);
    }






    @Override
    public void onClick(View v) {
        Intent intent;
        switch (v.getId()) {
            case R.id.goto_list_view_calendar:
                intent = new Intent(this, CalendarActivity.class);
                startActivity(intent);
                break;
            case R.id.goto_view_pager_calendar:
                intent = new Intent(this, ViewPagerCalendarActivity.class);
                startActivity(intent);
                break;
        }
    }
}
