package com.dyzs.library.calendar;

import android.content.Context;
import android.support.v4.util.SparseArrayCompat;
import android.util.SparseIntArray;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.dyzs.library.R;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;

/**
 * Created by maidou on 2016/7/26.
 * todo 单层分离MonthView，改为listview
 *
 *
 * todo 在最后完成后写一个 cache 管理器, 管理创建的每一个月份 item
 */
public class CalListViewAdapter extends BaseAdapter {
    private Context mContext;
    private CalListViewLunarView mLunarView;
    private int mTotalCount;
    private CalListViewMonth mMinMonth;	// 最小月份，从1900开始
    private CalListViewMonth mMaxMonth;	// 最大月份，到2100结束
    private SparseIntArray mSelectedDayCache = new SparseIntArray();
    private SparseIntArray mSelectedMarkerDayCache = new SparseIntArray();
    private SparseArrayCompat<CalListViewMonth> mMonthCache = new SparseArrayCompat<>();
    private SparseArrayCompat<CalListViewMonthView> mViewCache = new SparseArrayCompat<>();

    private String reg = "[0-9]{2}";
    // TODO: 2016/8/11 记录marker day

    // 记录选中的日期~
    private SparseArrayCompat<ArrayList<String>> selDay = new SparseArrayCompat<>();
    public void setSelectedDay (SparseArrayCompat<ArrayList<String>> data) {
        this.selDay = data;     // 在显示动画的时候展示一个渐变的效果
        notifyDataSetChanged();
    }

    /**
     * String 表示当前日期, Integer 表示包含的日期数量
     * 第一个Integer : 表示当前月份的索引 for example:1399(2016/08)
     *
     */
    private HashMap<Integer, HashMap<String, Integer>> markers = new HashMap<>();
    public void setAdapterMarkers(HashMap<Integer, HashMap<String, Integer>> marker) {
        this.markers = marker;
        this.notifyDataSetChanged();
    }


    public CalListViewAdapter(Context ctx, CalListViewLunarView lunarView) {
        this.mContext = ctx;
        mLunarView = lunarView;
//        mMinMonth = new CalListViewMonth(1900, 0, 1);
//        mMaxMonth = new CalListViewMonth(2100, 11, 1);
        mMinMonth = new CalListViewMonth(2015, 0, 1);
        mMaxMonth = new CalListViewMonth(2100, 11, 1);
        calculateRange(mMinMonth, mMaxMonth);
        System.out.println("getIndexOfCurrentMonth:" + getIndexOfCurrentMonth());
    }

    public CalListViewMonth getCalendarListMonth (int pos) {
        return getItemMonth(pos);
    }

    @Override
    public int getCount() {
        return mTotalCount; // 返回 2100-1900 的月份总个数
    }

    @Override
    public Object getItem(int position) {
        return position;
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolderMonthView holder = null;
//        CalListViewMonthView monthView = null;
        if (convertView == null) {
            convertView = View.inflate(mContext, R.layout.item_view_calendar, null);
        }
        holder = ViewHolderMonthView.getHolder(convertView);
        CalListViewMonth month = getItemMonth(position);
        holder.tv_date.setText(month.getYear() + "年" + (month.getMonth() + 1) + "月");
        holder.lcvmv.setMonth(month);
        holder.lcvmv.setSelectedDay(0);



//        monthView = new CalListViewMonthView(mContext);
//        monthView.setMonth(month);
////        monthView.setSelectedDay(0);
        if (markers != null) {
            if (markers.containsKey(position)) {
                holder.lcvmv.setMarkerData(markers.get(position));
                holder.lcvmv.invalidate();
            }
        }


        holder.lcvmv.setOnMonthViewClickListener(new CalListViewMonthView.MonthViewClickListener() {
            @Override
            public void onDayClick(View view, MonthDay monthDay) {
                Calendar calendar = monthDay.getCalendar();
                long timeMillis = calendar.getTimeInMillis();
                if (mOnDayClickListener != null) {
                    mOnDayClickListener.onDayClick(timeMillis);
                }
//                int year = monthDay.getCalendar().get(Calendar.YEAR);
//                int month = monthDay.getCalendar().get(Calendar.MONTH) + 1;
//                int day = monthDay.getCalendar().get(Calendar.DAY_OF_MONTH);
//                String strMonth = month + "";
//                String strDay = day + "";
//                if (!strMonth.matches(reg)) {
//                    strMonth = "0" + month;
//                }
//
//                if (!strDay.matches(reg)) {
//                    strDay = "0" + day;
//                }
//                if (mLunarView.getInterceptFirstTimeDatePick()) {
//                    mLunarView.setInterceptFirstTimeDatePick(!mLunarView.getInterceptFirstTimeDatePick());
//                    return;
//                }
//                //                ToastUtil.makeText(CalendarActivity.this,
//                //                        String.format("%d-%d-%d  %s月%s", year, month, day, lunarMonth, lunarDay));
//                String dateFormat = String.format("%d-%d-%d", year, month, day);
//                ToastUtil.makeText(mContext, "当前选中的日期是-->" + dateFormat);
            }

            @Override
            public void onDayClickGetMonthDay(View view, int selectedIndex) {

            }
        });


//        holder.ll_item.removeAllViews();
//        holder.ll_item.addView(monthView);


//        CalListViewMonthView monthView = new CalListViewMonthView(mContext, (CalListViewMonth) getItem(position), mLunarView);
//        int selectedDay = mSelectedDayCache.get(position, -1);
//        if (selectedDay != -1) {
//            monthView.setSelectedDay(selectedDay);
//            mSelectedDayCache.removeAt(mSelectedDayCache.indexOfKey(position));
//        }
//        mViewCache.put(position, monthView);
        return convertView;
    }

    /**
     * 得到当前月份的索引？
     * Get the index of month for today.
     *
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
     *
     * @param year  the specified year
     * @param month the specified month
     * @return the index
     */
    public int getIndexOfMonth(int year, int month) {
        return (year - mMinMonth.getYear()) * 12 + month;
    }



    /* get month item from cache array */
    public CalListViewMonth getItemMonth(int position) {
        CalListViewMonth monthItem = mMonthCache.get(position);
        if (monthItem != null) {
            return monthItem;
        }

        int numYear = position / 12;
        int numMonth = position % 12;

        int year = mMinMonth.getYear() + numYear;
        int month = mMinMonth.getMonth() + numMonth;
        if (month >= 12) {
            year += 1;
            month -= 12;
        }
        monthItem = new CalListViewMonth(year, month, 1);
        mMonthCache.put(position, monthItem);

        return monthItem;
    }

    /**
     * calculate month range 计算月份的范围，作为adapter的item数量
     */
    private void calculateRange(CalListViewMonth minDate, CalListViewMonth maxDate) {
		/* calculate total month */
        int minYear = minDate.getYear();
        int minMonth = minDate.getMonth();
        int maxYear = maxDate.getYear();
        int maxMonth = maxDate.getMonth();

        mTotalCount = (maxYear - minYear) * 12 + maxMonth - minMonth;
        System.out.println("mTotalCount:" + mTotalCount);   // 2412
    }

    /**
     * Set date range of lunar view.
     *
     * @param minDate min date month
     * @param maxDate max date month
     */
    protected void setDateRange(CalListViewMonth minDate, CalListViewMonth maxDate) {
        mMinMonth = minDate;
        mMaxMonth = maxDate;
        calculateRange(minDate, maxDate);
        notifyDataSetChanged();
    }




    static class ViewHolderMonthView{
        private LinearLayout ll_item;
        private TextView tv_date;
        private CalListViewMonthView lcvmv;
        public ViewHolderMonthView(View convertView) {
            ll_item = (LinearLayout) convertView.findViewById(R.id.ll_item);
            tv_date = (TextView) convertView.findViewById(R.id.tv_date);
            lcvmv = (CalListViewMonthView) convertView.findViewById(R.id.lcvmv);
        }
        public static ViewHolderMonthView getHolder(View convertView) {
            ViewHolderMonthView holder = (ViewHolderMonthView) convertView.getTag();
            if (holder == null) {
                holder = new ViewHolderMonthView(convertView);
                convertView.setTag(holder);
            }
            return holder;
        }
    }

    private OnDayClickListener mOnDayClickListener;
    public interface OnDayClickListener {
        void onDayClick(long timeMillis);
    }
    public void setOnDayClickListener (OnDayClickListener listener) {
        this.mOnDayClickListener = listener;
    }
}
