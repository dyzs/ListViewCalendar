package com.dyzs.listviewcalendar;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

import com.dyzs.library.activity.CalendarActivity;

import java.util.Calendar;

public class MainActivity extends Activity implements View.OnClickListener{
    private static final String TAG = MainActivity.class.getSimpleName();


    private TextView goto_list_view_calendar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);


        initView();

        initEvent();
    }

    private void initView () {
        goto_list_view_calendar = (TextView) findViewById(R.id.goto_list_view_calendar);
    }




    private void initEvent () {
        goto_list_view_calendar.setOnClickListener(this);
    }






    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.goto_list_view_calendar:
                Intent intent = new Intent(this, CalendarActivity.class);
                startActivity(intent);
                break;
        }
    }
}
