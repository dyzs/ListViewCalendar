package com.dyzs.library.activity;


import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.widget.AbsListView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;

import com.dyzs.library.R;
import com.dyzs.library.calendar.CalListViewAdapter;
import com.dyzs.library.calendar.CalListViewLunarView;
import com.dyzs.library.calendar.CalListViewMonth;
import com.dyzs.library.calendar.CalListViewMonthView;
import com.dyzs.library.model.CalendarInfo;
import com.dyzs.library.utils.DateUtil;
import com.dyzs.library.utils.ToastUtil;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;

public class CalendarActivity extends Activity {
    private static final String TAG = CalendarActivity.class.getSimpleName();
    private static final int ON_DATE_SELECTED = 8;
    private static final int REFRESH_MARKER = 2;
    private static final int DATE_REQUEST_SUCCESS = 3;


    private Context mContext;
    private LinearLayout container;

    private ListView lv_container;
    private CalListViewAdapter adapter;

    CalListViewMonth mMinMonth = new CalListViewMonth(2015, 0, 1);
    int mCurrentMonthPosition = 0;


    private TextView tv_title;
    String getCurrentMonth = "";


    private HashMap<Integer, HashMap<String, Integer>> mFinalData = new HashMap<>();
    private HashMap<String, Integer> mHmMarker = new HashMap<>();
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_calendar);


        mContext = this;
        initBeforeView();
        initView();
        initEvent();
        initData();
    }

    private void initBeforeView () {
        mCurrentMonthPosition = getIndexOfCurrentMonth();
    }

    private void initView () {
        container = (LinearLayout) findViewById(R.id.container);

        lv_container = (ListView) findViewById(R.id.lv_container);

        adapter = new CalListViewAdapter(mContext, new CalListViewLunarView(mContext));
        lv_container.setAdapter(adapter);
        lv_container.setSelection(mCurrentMonthPosition);
        lv_container.setFooterDividersEnabled(false);

        tv_title = (TextView) findViewById(R.id.tv_title);
    }

    private void initEvent () {
        lv_container.setOnScrollListener(new AbsListView.OnScrollListener() {
            CalListViewMonth curDateMonth = null;
            private boolean fling = false;

            @Override
            public void onScrollStateChanged(AbsListView view, int scrollState) {
                switch (scrollState) {
                    case SCROLL_STATE_FLING:
                        fling = true;
                        break;
                    case SCROLL_STATE_IDLE:     // 滑动结束时按时间段加载
                        fling = false;
//                        mCurrentMonthPosition = view.getFirstVisiblePosition();
//                        CalendarListMonth curDataMonth = adapter.getCalendarListMonth(mCurrentMonthPosition);
//                        // 获取上个月的一号的时间戳
//                        long fromTime = DateUtil.getMonthTimeMillisOffset(
//                                curDataMonth.getYear() + "",
//                                (curDataMonth.getMonth() + 1) + "", null, -1);
//
//                        // 获取下下下个月第一天的时间戳
//                        long toTime = DateUtil.getMonthTimeMillisOffset(
//                                curDataMonth.getYear() + "",
//                                (curDataMonth.getMonth() + 1) + "", null, 3);
//
//                        parseAsyncData(fromTime, toTime);
//                        // TODO: 2016/8/11 请求当前 position 的前后一个月的marker数据
//                        Message msg = mHandler.obtainMessage();
//                        msg.what = 1;
//                        msg.obj = view.getFirstVisiblePosition();
//                        mHandler.sendMessage(msg);
                        break;
                    case SCROLL_STATE_TOUCH_SCROLL:
                        fling = true;
                        break;
                }
            }

            @Override
            public void onScroll(AbsListView view, int firstVisibleItem, int visibleItemCount, int totalItemCount) {
                if (fling) {
                    if (visibleItemCount == 3) {
                        curDateMonth = adapter.getCalendarListMonth(firstVisibleItem + 1);
                    } else if (visibleItemCount == 2) {
                        curDateMonth = adapter.getCalendarListMonth(firstVisibleItem);
                    }
                    tv_title.setText(curDateMonth.getYear() + "年" + (curDateMonth.getMonth() + 1) + "月");
                }
            }
        });

        adapter.setOnDayClickListener(new CalListViewAdapter.OnDayClickListener() {
            @Override
            public void onDayClick(long timeMillis) {
                Message msg = mHandler.obtainMessage();
                msg.what = ON_DATE_SELECTED;
                msg.obj = timeMillis;
                mHandler.sendMessage(msg);
            }
        });

    }

    private void initData () {
        // 初始化时, 只获取一个月前到当前的数据
        String today = DateUtil.getCurrentDate();
        parseAsyncData(
                DateUtil.getMonthTimeMillisOffset(DateUtil.getYear(today), DateUtil.getMonth(today), null, -1),
                System.currentTimeMillis()
        );

        getCurrentMonth = "2016-08-26"; //getIntent().getStringExtra("currentMonth");
        int year = Integer.valueOf(DateUtil.getYear(getCurrentMonth));
        int month = Integer.valueOf(DateUtil.getMonth(getCurrentMonth)) - 1;
        mCurrentMonthPosition = getIndexOfMonth(year, month);
        tv_title.setText(year + "年" + (month + 1) + "月");
    }


    private Handler mHandler = new Handler(new Handler.Callback() {
        @Override
        public boolean handleMessage(Message msg) {
            ArrayList<CalendarInfo> calendarInfoList;
            switch (msg.what) {
                case REFRESH_MARKER:
                    adapter.setAdapterMarkers(mFinalData);
                    break;
                case DATE_REQUEST_SUCCESS:         // 新查询数据方法
                    calendarInfoList = (ArrayList<CalendarInfo>) msg.obj;
                    if (calendarInfoList != null) {
                        Collections.sort(calendarInfoList);
                        mFinalData.clear();
                        HashMap<Integer, HashMap<String, Integer>> tempData = new HashMap<>();
                        HashMap<String, Integer> tempDataChild = new HashMap<>();
                        CalendarInfo info;
                        int lastPos = 0;
                        int position = 0;
                        Date date = null;
                        for (int i = 0; i < calendarInfoList.size(); i ++) {
                            info = calendarInfoList.get(i);
                            date = new Date(DateUtil.getTimeMills(info.date));
                            int year = date.getYear() + 1900;
                            int month = date.getMonth();
                            position = adapter.getIndexOfMonth(year, month);
                            if (i == 0) {
                                lastPos = position;
                            }


                            if  (i == calendarInfoList.size() - 1) {
                                tempDataChild.put(info.date, getStarType(info.dataTypeInfoList.size()));
                                tempData.put(lastPos, tempDataChild);
                            } else {
                                if (lastPos != position) {
                                    tempData.put(lastPos, tempDataChild);
                                    tempDataChild = new HashMap<>();
                                }
                                tempDataChild.put(info.date, getStarType(info.dataTypeInfoList.size()));
                            }
                            lastPos = position;
                        }
                        mFinalData = tempData;
                        msg = mHandler.obtainMessage();
                        msg.what = REFRESH_MARKER;
                        mHandler.sendMessage(msg);
                    }
                    break;
                case ON_DATE_SELECTED:
                    long time = (long) msg.obj;
                    String strTime = DateUtil.getStringDate(time);
                    String tempyear = DateUtil.getYear(strTime);
                    int tempmonth = Integer.valueOf(DateUtil.getMonth(strTime));
                    ToastUtil.makeText(mContext, tempyear + "年" + tempmonth + "月");
//                    Intent intent = new Intent();
//                    intent.putExtra("timeMillis", time);
//                    activity.setResult(105, intent);
//                    activity.finish();
                    break;
            }
            return true;
        }
    });




    private void parseAsyncData(long fromTime, long toTime) {
//        DataManager.asyncGetDataForCalendar(
//                new DataManager.ICallBackManager() {
//                    @Override
//                    public void onSuccess(Object... msg) {
//                        sendMainHandlerMessage(GET_ALL_MARKER, msg[0]);
//                    }
//                },
//                fromTime,
//                toTime
//        );
        ArrayList<CalendarInfo> calendarInfoList = new ArrayList<>();
        CalendarInfo info = null;
        for (int i = 10; i < 28; i ++) {
            info = new CalendarInfo();
            info.markCount = i + 1;
            info.date = "2016-06-" + i;
            calendarInfoList.add(info);
        }
        for (int i = 10; i < 15; i ++) {
            info = new CalendarInfo();
            info.markCount = i + 1;
            info.date = "2016-08-" + i;
            calendarInfoList.add(info);
        }
        for (int i = 25; i < 31; i ++) {
            info = new CalendarInfo();
            info.markCount = i + 1;
            info.date = "2016-08-" + i;
            calendarInfoList.add(info);
        }
        Message msg = mHandler.obtainMessage();
        msg.what = DATE_REQUEST_SUCCESS;
        msg.obj = calendarInfoList;
        mHandler.sendMessage(msg);
    }


    // 通过 count 计算返回的星星类型
    private int getStarType(int count) {
        if (count < 3) {
            return CalListViewMonthView.TYPE_STAR_SILVER;
        }
        return CalListViewMonthView.TYPE_STAR_GOLDEN_NORMAL;
    }


    /**
     * 得到当前月份的索引？
     * Get the index of month for today.
     * @return index of month
     */
    public int getIndexOfCurrentMonth() {
        Calendar calendar = Calendar.getInstance();
        calendar.setTimeInMillis(System.currentTimeMillis());
        int year = calendar.get(Calendar.YEAR);
        int month = calendar.get(Calendar.MONTH);
        return getIndexOfMonth(year, month);
    }
    /**
     * 得到指定的年份的月份索引？(2015-1900) * 12 + 12(当前月份) => 1393
     * Get the index of given year and month.
     * @param year  the specified year
     * @param month the specified month
     * @return the index
     */
    public int getIndexOfMonth(int year, int month) {
        return (year - mMinMonth.getYear()) * 12 + month;
    }
}
