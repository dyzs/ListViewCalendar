package com.dyzs.library.calendar;

import android.content.Context;
import android.content.res.Resources;
import android.content.res.TypedArray;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.support.annotation.ColorInt;
import android.support.annotation.ColorRes;
import android.support.annotation.DrawableRes;
import android.util.AttributeSet;
import android.view.View;
import android.widget.AbsListView;
import android.widget.LinearLayout;
import android.widget.ListView;

import com.dyzs.library.R;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;

/**
 * Created by maidou on 2016/7/26.
 */
public class CalListViewLunarView extends LinearLayout{
    private Resources res = getContext().getResources();
    // 当前月份的日期的颜色，除了周六日以外
    private int mSolarTextColor = res.getColor(R.color.calendar_normal_text_color);
    // 农历文本颜色
    private int mLunarTextColor = res.getColor(R.color.calendar_normal_lunar_text_color);
    // 仅周六日日期字体颜色
    private int mHightlistColor = res.getColor(R.color.calendar_weekends_solar_color);
    private int mUncheckableColor = 0xffb0b0b0;			// 特殊节假日字体颜色，上月下月的字体颜色
    private int mMonthBackgroundColor = Color.WHITE;	//0xfffafafa;		// 日期面板背景颜色
    private int mWeekLabelBackgroundColor = Color.WHITE; 	// 0xfffafafa;	// 周一到周五的导航条
    private Drawable mTodayBackground;
    private boolean mShouldPickOnMonthChange = true;	// 应该选取，在月份更改的时候？

    private ListView mListView;
    private CalListViewAdapter mAdapter;					// 日历数据
    private OnDatePickListener mOnDatePickListener;		// 日期选择监听
    private int mCurrentPager = 0;//1393;

    public CalListViewLunarView(Context context) {
        this(context, null);
    }

    public CalListViewLunarView(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public CalListViewLunarView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(attrs);
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        int measureWidth = MeasureSpec.getSize(widthMeasureSpec);

        for (int i = 0; i < getChildCount(); i++) {
            final View child = getChildAt(i);

            int childWidthMeasureSpec = MeasureSpec.makeMeasureSpec(
                    measureWidth, MeasureSpec.EXACTLY);
            int childHeightMeasureSpec = MeasureSpec.makeMeasureSpec(
                    measureWidth, MeasureSpec.EXACTLY);
            child.measure(childWidthMeasureSpec, childHeightMeasureSpec);
        }
        int measureHeight = (int) (measureWidth * 6f / 7f);// + mWeekLabelView.getMeasuredHeight();
        setMeasuredDimension(measureWidth, measureHeight);
    }

    /* init lunar view */
    private void init(AttributeSet attrs) {
        // System.out.println("Init LunarView......");
		/* get custom attrs */
        TypedArray a = getContext().obtainStyledAttributes(attrs, R.styleable.LunarView);
        mMonthBackgroundColor = a.getColor(R.styleable.LunarView_monthBackgroundColor, mMonthBackgroundColor);
        mWeekLabelBackgroundColor = a.getColor(R.styleable.LunarView_monthBackgroundColor, mWeekLabelBackgroundColor);
        mSolarTextColor = a.getColor(R.styleable.LunarView_solarTextColor, mSolarTextColor);
        mLunarTextColor = a.getColor(R.styleable.LunarView_lunarTextColor, mLunarTextColor);
        mHightlistColor = a.getColor(R.styleable.LunarView_highlightColor, mHightlistColor);
        mUncheckableColor = a.getColor(R.styleable.LunarView_uncheckableColor, mUncheckableColor);
        mTodayBackground = a.getDrawable(R.styleable.LunarView_todayBackground);
        mShouldPickOnMonthChange = a.getBoolean(R.styleable.LunarView_shouldPickOnMonthChange, mShouldPickOnMonthChange);
        a.recycle();

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
			/* if we're on good Android versions, turn off clipping for cool effects */
            setClipToPadding(false);
            setClipChildren(false);
        } else {
			/* old Android does not like _not_ clipping view pagers, we need to clip */
            setClipChildren(true);
            setClipToPadding(true);
        }
		/* set the orientation to vertical */
    }

    private ListView.OnScrollListener mScrollListener = new AbsListView.OnScrollListener() {
        @Override
        public void onScrollStateChanged(AbsListView view, int scrollState) {

        }

        @Override
        public void onScroll(AbsListView view, int firstVisibleItem, int visibleItemCount, int totalItemCount) {
            mCurrentPager = totalItemCount;
            mInterceptFirstTimeDatePick = true;
//            mAdapter.resetSelectedDay(totalItemCount);
        }
    };

    /**
     * maidou modify
     * @param resId
     * @return
     */
    @SuppressWarnings("deprecation")
    private int getColor(@ColorRes int resId) {
//		if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
//			return getResources().getColor(resId, null);
//		} else {
//			return getResources().getColor(resId);
//		}
        return getResources().getColor(resId);
    }

    @SuppressWarnings("deprecation")
    private Drawable getDrawable(@DrawableRes int resId) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            return getResources().getDrawable(resId, null);
        } else {
            return getResources().getDrawable(resId);
        }
    }

    /**
     * 定义回调接口，修改选中的日期
     * Interface definition for a callback to be invoked when date picked.
     */
    public interface OnDatePickListener {
        /**
         * Invoked when date picked.
         *
         * @param view     {@link }
         * @param monthDay {@link MonthDay}
         */
        void onDatePick(CalListViewLunarView view, MonthDay monthDay);
    }

    /**
     * Get the color of month view background.
     *
     * @return color of month background
     */
    protected int getMonthBackgroundColor() {
        return mMonthBackgroundColor;
    }

    /**
     * Get the text color of solar day.
     *
     * @return text color of solar day
     */
    protected int getSolarTextColor() {
        return mSolarTextColor;
    }

    /**
     * Get the text color of lunar day.
     *
     * @return text color of lunar day
     */
    protected int getLunarTextColor() {
        return mLunarTextColor;
    }

    /**
     * Get the highlight color.
     *
     * @return thighlight color
     */
    protected int getHightlightColor() {
        return mHightlistColor;
    }

    /**
     * 得到没有选中的日期的颜色
     * Get the color of uncheckable day.
     *
     * @return uncheckable color
     */
    protected int getUnCheckableColor() {
        return mUncheckableColor;
    }

    /**
     * Get the background of today.
     *
     * @return background drawable
     */
    protected Drawable getTodayBackground() {
        return mTodayBackground;
    }

    /**
     * 自动判断并改变月份
     * Auto pick date when month changed or not.
     *
     * @return true or false
     */
    protected boolean getShouldPickOnMonthChange() {
        return mShouldPickOnMonthChange;
    }

    /**
     * Set the text color of solar day.
     *
     * @param color color
     */
    public void setSolarTextColor(@ColorInt int color) {
        mSolarTextColor = color;
    }

    /**
     * Set the text color resource of solar day.
     *
     * @param resId resource id
     */
    public void setSolarTextColorRes(@ColorRes int resId) {
        mSolarTextColor = getColor(resId);
    }

    /**
     * Set the text color of lunar day.
     *
     * @param color color
     */
    public void setLunarTextColor(@ColorInt int color) {
        mLunarTextColor = color;
    }

    /**
     * Set the text color resource of lunar day.
     *
     * @param resId resource id
     */
    public void setLunarTextColorRes(@ColorRes int resId) {
        mLunarTextColor = getColor(resId);
    }

    /**
     * Set the highlight color.
     *
     * @param color color
     */
    public void setHighlightColor(@ColorInt int color) {
        mHightlistColor = color;
    }

    /**
     * Set the highlight color resource.
     *
     * @param resId resource id
     */
    public void setHighlightColorRes(@ColorRes int resId) {
        mHightlistColor = getColor(resId);
    }

    /**
     * Set the text color resource of uncheckable day.
     *
     * @param resId resource id
     */
    public void setUncheckableColorRes(@ColorRes int resId) {
        mUncheckableColor = getColor(resId);
    }

    /**
     * Set the text color of uncheckable day.
     *
     * @param color color
     */
    public void setUncheckableColor(@ColorInt int color) {
        mUncheckableColor = color;
    }

    /**
     * Set the background drawable of today.
     *
     * @param resId drawable resource id
     */
    public void setTodayBackground(@DrawableRes int resId) {
        mTodayBackground = getDrawable(resId);
    }

    /**
     * 设置日期点击监听事件，当日期被选中了
     * Set on date click listener. This listener will be invoked
     * when a day in month was picked.
     *
     * @param l date pick listner
     */
    public void setOnDatePickListener(OnDatePickListener l) {
        mOnDatePickListener = l;
    }

    /**
     * 分发选中日期的监听事件，
     * Dispatch date pick listener. This will be invoked be {@link }
     *
     * @param monthDay month day
     */
    protected void dispatchDateClickListener(MonthDay monthDay) {
        if (mOnDatePickListener != null) {
            mOnDatePickListener.onDatePick(this, monthDay);
        }
    }


    /**
     显示指定的月份页位置和选中的日期{@param selectDay example like 25}，只要把 marker 坐标传递给它就行了！！！！！！！
     这个需要的是 goToMonth 方法，可以传递参数 year 和 month，goToMonth 这是用
     showMonth(mAdapter.getIndexOfMonth(year, month), day);
     show the month page with specified pager position and selected day
     modify by maidou
     */
    public void showMonth(int position, int selectedDay) {
//		mIsChangedByUser = true;


//        if (!mInterceptFirstTimeDatePick) {		// add by maidou
//            mInterceptFirstTimeDatePick = true;
//        }
//        mAdapter.setSelectedDay(position, selectedDay);
//        mPager.setCurrentItem(position, true);
//
//        System.out.println("showMonth Page Position:" + position);
//
//        invalidate();
    }

    /**
     * Show previous month page with selected day.
     *
     * @param selectedDay selected day
     */
    protected void showPrevMonth(int selectedDay) {
//        int position = mPager.getCurrentItem() - 1;
//        showMonth(position, selectedDay);
////		postInvalidate();
    }

    /**
     * Show next month page with selected day.
     *
     * @param selectedDay selected day
     */
    protected void showNextMonth(int selectedDay) {
//        int position = mPager.getCurrentItem() + 1;
//        showMonth(position, selectedDay);
////		postInvalidate();
    }

    /**
     * Show previous month view.
     */
    public void showPrevMonth() {
        showPrevMonth(1);
    }

    /**
     * Show next month view.
     */
    public void showNextMonth() {
        showNextMonth(1);
    }

    /**
     * Go to the month with specified year and month.
     *
     * @param year  the specified year
     * @param month the specified month
     */
    public void goToMonth(int year, int month) {
//        showMonth(mAdapter.getIndexOfMonth(year, month), 1);
    }

    /**
     * 跳转到指定的年月日？
     * Go to the month with specified year, month and day.
     *
     * @param year  the specified year
     * @param month the specified month
     * @param day   the specified day
     */
    public void goToMonthDay(int year, int month, int day) {
//        showMonth(mAdapter.getIndexOfMonth(year, month), day);
    }

    /**
     * 返回到当前日期的月份
     * Go back to the month of today.
     */
    public void backToToday() {
        Calendar today = Calendar.getInstance();
        today.setTimeInMillis(System.currentTimeMillis());
//        showMonth(mAdapter.getIndexOfCurrentMonth(), today.get(Calendar.DAY_OF_MONTH));
    }

    /**
     * Set the range of date.
     *
     * @param minYear min year
     * @param maxYear max year
     */
    public void setDateRange(int minYear, int maxYear) {
        CalListViewMonth min = new CalListViewMonth(minYear, 0, 1);
        CalListViewMonth max = new CalListViewMonth(maxYear, 11, 1);
        mAdapter.setDateRange(min, max);
    }

    private ArrayList<String> mMarkerDataList;
    public void setMarkerList(ArrayList<String> list) {
        this.mMarkerDataList = list;
    }
    public ArrayList<String> getMarkerList() {
        if (mMarkerDataList != null) {
            return mMarkerDataList;
        }
        return null;
    }

    private HashMap<String, Integer> mHmMarker;
    public void setHmMarker(HashMap<String, Integer> hm) {
        this.mHmMarker = hm;
    }
    public HashMap<String, Integer> getHnMarker() {
        if (mHmMarker != null) {
            return mHmMarker;
        }
        return null;
    }




//	/**
//	 * 移除一个日期的标记点
//	 * @param marker
//	 */
//	public void removeOneMarker(String marker) {
//		mAdapter.removeOneMarker(mCurrentPager, marker);
//	}
//
//	/**
//	 * 新增一个日期的标记点
//	 * @param marker
//	 */
//	public void addMarker(String marker) {
//		mAdapter.addOneMarker(mCurrentPager, marker);
//	}
//
//
//	/**
//	 * 移除所以日期的日程安排的方法
//	 */
//	public void removeAllMarkers(ArrayList<String> markers) {
//		for (String marker:markers) {
//			mAdapter.removeOneMarker(mAdapter.getIndexOfCurrentMonth(),marker);
//		}
//	}


    /**
     * create by maidou 15-12-18
     * 屏蔽控件一加载完成后自动执行的点击事件
     * @descript this member mFirstTimeClick,use to command the first time execute onTouchEvent.
     * intercept for the first time onDatePick ,at the same time,it also to get calendar
     */
    private boolean mInterceptFirstTimeDatePick = true;
    public boolean getInterceptFirstTimeDatePick() {
        return mInterceptFirstTimeDatePick;
    }
    public void setInterceptFirstTimeDatePick(boolean b) {
        this.mInterceptFirstTimeDatePick = b;
    }
}
