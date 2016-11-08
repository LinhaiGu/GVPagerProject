package glh.gvpager.view;

import android.content.Context;
import android.content.res.TypedArray;
import android.os.Handler;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.util.AttributeSet;
import android.view.KeyEvent;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AccelerateInterpolator;
import android.view.animation.Interpolator;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.LinearLayout;
import android.widget.ListAdapter;
import android.widget.Scroller;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

import glh.gvpager.R;

/**
 * ViewPager内嵌ListView(仿GridView)
 * Created by glh on 2016-11-07.
 */
public class GVPager extends ViewPager {

    private List<HGridView> mHGridViewList = null;//内嵌的GridView
    private BaseAdapter mAdapter;
    private int mSelection = -1;
    private static final int DEFAULT_COLUMN_NUMBER = 2;
    private static final int DEFAULT_ROW_NUMBER = 3;
    private int mRowNumber = DEFAULT_ROW_NUMBER;// 行
    private int mColumnNumber = DEFAULT_COLUMN_NUMBER;// 列
    private float mColumnMargin = 0;//列间距
    private float mRowMargin = 0;//行间距
    private int mPaddingLeft = 0;//左边距
    private int mPaddingRight = 0;//右边距
    private IndicatorView mIndicatorView;
    private Timer mTimer = null;
    private TimerTask mTask = null;
    // 默认3秒跳转广告.
    private int mAutoSwitchRate = 3000;
    private final static int FLAG_AUTO_SWITCH = 0x10;
    private int pageIndex = 0;
    private boolean isAuto = false;

    private final Handler mAutoSwitchHandler = new Handler() {
        @Override
        public void handleMessage(android.os.Message msg) {
            switch (msg.what) {
                case FLAG_AUTO_SWITCH:
                    if (isAuto) {
                        switchItem();
                    }
                    break;
                default:
                    super.handleMessage(msg);
            }
        }

    };


    public GVPager(Context context) {
        this(context, null);
    }

    public GVPager(Context context, AttributeSet attrs) {
        super(context, attrs);
        getAttributeSet(attrs);
        mHGridViewList = new ArrayList<>();
        addListener();
    }

    private void addListener() {
        addOnPageChangeListener(new ViewPager.OnPageChangeListener() {

            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
            }

            @Override
            public void onPageSelected(int position) {
                pageIndex = position;
                if (mIndicatorView != null) {
                    mIndicatorView.setSelectIndex(position);
                }
            }


            @Override
            public void onPageScrollStateChanged(int state) {

            }
        });
    }

    /**
     * 获取设置的属性值
     *
     * @param _attrs AttributeSet
     */
    private void getAttributeSet(AttributeSet _attrs) {
        if (_attrs != null) {
            TypedArray typedArray = getContext().obtainStyledAttributes(_attrs, R.styleable.GridViewPager);
            int count = typedArray.getIndexCount();
            for (int i = 0; i < count; i++) {
                int attr = typedArray.getIndex(i);
                switch (attr) {
                    case R.styleable.GridViewPager_columnNumber:
                        mColumnNumber = typedArray.getInt(attr, -1);
                        break;
                    case R.styleable.GridViewPager_rowNumber:
                        mRowNumber = typedArray.getInt(attr, -1);
                        break;
                    case R.styleable.GridViewPager_columnMargin:
                        mColumnMargin = typedArray.getDimension(attr, 0);
                        break;
                    case R.styleable.GridViewPager_rowMargin:
                        mRowMargin = typedArray.getDimension(attr, 0);
                        break;
                    case R.styleable.GridViewPager_android_padding:
                        int padding = typedArray.getDimensionPixelSize(attr, 0);
                        setPadding(padding, padding, padding, padding);
                        break;
                    case R.styleable.GridViewPager_android_paddingLeft:
                        mPaddingLeft = typedArray.getDimensionPixelSize(attr, 0);
                        break;
                    case R.styleable.GridViewPager_android_paddingRight:
                        mPaddingRight = typedArray.getDimensionPixelSize(attr, 0);
                        break;
                    default:
                        break;
                }
            }
            typedArray.recycle();
        }

    }

    @Override
    public boolean onTouchEvent(MotionEvent ev) {
        isAuto = false;
        if (ev.getAction() == MotionEvent.ACTION_UP) {
            isAuto = true;
        } else if (ev.getAction() == MotionEvent.ACTION_DOWN) {
            if (isAuto) {
                setAutoDuration(800);
            }
        }
        return super.onTouchEvent(ev);
    }


    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
    }

    @Override
    public void setPadding(int left, int top, int right, int bottom) {
        mPaddingLeft = left;
        mPaddingRight = right;
        super.setPadding(0, top, 0, bottom);
    }


    private void resetAdapter() {
        // 行*列=当前屏的总个数
        int pageSize = mColumnNumber * mRowNumber;
        if (pageSize <= 0)
            return;

        if (mAdapter.getCount() == 0) {
            mHGridViewList.removeAll(mHGridViewList);
        }
        int pageCount = mAdapter.getCount() / pageSize;
        int listSize = mHGridViewList.size() - 1;
        HGridView hGridView;
        GridAdapter gridAdapter;
        for (int i = 0, page = (mAdapter.getCount() % pageSize == 0) ? --pageCount : pageCount; i <= Math.max(listSize, page); i++) {
            if (i <= listSize && i <= page) {
                // 更新
                hGridView = mHGridViewList.get(i);
                gridAdapter = new GridAdapter(i, pageSize, mAdapter);
                hGridView.setAdapter(gridAdapter);
                mHGridViewList.set(i, hGridView);
                continue;
            }
            if (i > listSize && i <= page) {
                // 添加
                hGridView = new HGridView();
                gridAdapter = new GridAdapter(i, pageSize, mAdapter);
                hGridView.setAdapter(gridAdapter);
                mHGridViewList.add(hGridView);
                continue;
            }
            if (i > page && i <= listSize) {// 以设置的Adapter中的个数为准，超过移除View
                mHGridViewList.remove(page + 1);// 每次都移除page+1位置的GridView
                continue;
            }
        }
        super.setAdapter(new GridPagerAdapter());
        if (mSelection >= 0) {
            setSelection(mSelection);
        }
    }

    private class GridPagerAdapter extends PagerAdapter {

        @Override
        public int getCount() {
            if (mIndicatorView != null) {
                /*
                    指示器
                */
                mIndicatorView.setTotal(mHGridViewList.size());
            }
            return mHGridViewList.size();
        }

        @Override
        public boolean isViewFromObject(View arg0, Object arg1) {
            return arg0 == arg1;
        }

        @Override
        public void destroyItem(ViewGroup container, int position, Object object) {
            container.removeView((View) object);
        }


        @Override
        public Object instantiateItem(ViewGroup container, int position) {
            container.addView(mHGridViewList.get(position), new LinearLayout.LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT));
            return mHGridViewList.get(position);

        }
    }

    /**
     * 通知需要切换广告
     */
    protected void notifyAutoSwitch() {
        mAutoSwitchHandler.sendEmptyMessage(FLAG_AUTO_SWITCH);
    }


    /**
     * 切换广告内容
     */
    protected void switchItem() {
        if ((pageIndex + 1) > (mHGridViewList.size() - 1)) {
            setCurrentItem(0, false);
        } else {
            setCurrentItem(pageIndex + 1, true);
        }
    }


    class FixedSpeedScroller extends Scroller {
        private int mDuration = 1500;

        public FixedSpeedScroller(Context context) {
            super(context);
        }

        public FixedSpeedScroller(Context context, Interpolator interpolator) {
            super(context, interpolator);
        }

        @Override
        public void startScroll(int startX, int startY, int dx, int dy, int duration) {
            // Ignore received duration, use fixed one instead
            super.startScroll(startX, startY, dx, dy, mDuration);
        }

        @Override
        public void startScroll(int startX, int startY, int dx, int dy) {
            // Ignore received duration, use fixed one instead
            super.startScroll(startX, startY, dx, dy, mDuration);
        }

        public void setmDuration(int time) {
            mDuration = time;
        }

        public int getmDuration() {
            return mDuration;
        }
    }

    //=============================================================================================
    //====================================内部的AdapterView=========================================
    //=============================================================================================

    /**
     * <p>
     * 自定义AdapterView,通过实现setAdapter、onMeasure、onLayout这几个主要方法来说实现列表项的测量与定位。
     * </p>
     */
    public class HGridView extends AdapterView<ListAdapter> {

        private ListAdapter adapter;

        public HGridView() {
            super(GVPager.this.getContext());
        }

        @Override
        public ListAdapter getAdapter() {
            return adapter;
        }

        @Override
        public void setAdapter(ListAdapter listAdapter) {
            this.adapter = listAdapter;
            int oldChildCount = getChildCount();
            int newChildCount = adapter.getCount();
            int deleteChildCount = oldChildCount - newChildCount;
            for (int i = oldChildCount; i < newChildCount; i++) {
                View child = adapter.getView(i, null, this);
                addViewInLayout(child, i, new LayoutParams(0, 0));
            }
            if (deleteChildCount > 0) {
                removeViewsInLayout(newChildCount, deleteChildCount);
            }
        }

        @Override
        public View getSelectedView() {
            if (getChildCount() > 0) {
                return getChildAt(0);
            }
            return null;
        }

        @Override
        public void setSelection(int i) {

        }

        @Override
        public int getPaddingLeft() {
            return mPaddingLeft;
        }

        @Override
        public int getPaddingRight() {
            return mPaddingRight;
        }

        @Override
        protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
            /*
            获取子View宽高
             */
            int width = (int) ((MeasureSpec.getSize(widthMeasureSpec) - getPaddingLeft() - getPaddingRight() - mColumnMargin * (mColumnNumber - 1)) / mColumnNumber);
            int height = (int) ((MeasureSpec.getSize(heightMeasureSpec) - mRowMargin * (mRowNumber - 1)) / mRowNumber);
            int count = getChildCount();
            for (int i = 0; i < count; i++) {
                View child = getChildAt(i);
                /*
                给子View设置宽高
                 */
                LayoutParams layoutParams = child.getLayoutParams();
                layoutParams.width = width;
                layoutParams.height = height;
                child.measure(MeasureSpec.makeMeasureSpec(width, MeasureSpec.EXACTLY), MeasureSpec.makeMeasureSpec(height, MeasureSpec.EXACTLY));
            }
            setMeasuredDimension(getDefaultSize(getSuggestedMinimumWidth(), widthMeasureSpec), getDefaultSize(getSuggestedMinimumHeight(), heightMeasureSpec));
        }

        @Override
        protected void onLayout(boolean changed, int left, int top, int right, int bottom) {
            int childCount = getChildCount();
            int childLeft = 0;
            int childTop = 0;
            for (int i = 0; i < childCount && i < mRowNumber * mColumnNumber; i++) {
                View child = getChildAt(i);
                int x = i % mColumnNumber;
                if (x == 0) {
                    /*
                    每一行的第一个子View
                     */
                    childLeft = getPaddingLeft();
                }
                LayoutParams layoutParams = child.getLayoutParams();
                child.layout(childLeft, childTop, childLeft + layoutParams.width, childTop + layoutParams.height);
                childLeft += layoutParams.width + mColumnMargin;
                if (x == mColumnNumber - 1) {
                    /*
                    每一行最后一个子View,要另起一行
                     */
                    childTop += layoutParams.height + mRowMargin;
                }
            }
        }
    }

    private class GridAdapter extends BaseAdapter {

        private int page;
        private int size;
        private BaseAdapter adapter;

        public GridAdapter(int _page, int _size, BaseAdapter _adapter) {
            this.size = _size;
            this.page = _page;
            this.adapter = _adapter;
        }

        @Override
        public int getCount() {
            if (adapter.getCount() % size == 0 || page < adapter.getCount() / size) {
                return size;
            }
            return adapter.getCount() % size;
        }

        @Override
        public Object getItem(int i) {
            return adapter.getItem(page * size + i);
        }

        @Override
        public long getItemId(int i) {
            return adapter.getItemId(page * size + i);
        }

        @Override
        public View getView(int i, View view, ViewGroup viewGroup) {
            return adapter.getView(page * size + i, view, viewGroup);
        }
    }
    //==============================================================================================
    //==============================================================================================
    //==============================================================================================

    /**
     * 设置ViewPager的Adapter
     *
     * @param _adapter BaseAdapter
     */
    public void setAdapter(BaseAdapter _adapter) {
        mAdapter = _adapter;
        resetAdapter();
    }

    /**
     * 刷新
     */
    public void notifyDataSetChanged() {
        resetAdapter();
    }

    /**
     * 定位位置
     *
     * @param position
     */
    public void setSelection(int position) {
        final int pageSize = getPageSize();
        if (mAdapter == null || pageSize <= 0) {
            mSelection = position;
            return;
        }
        this.pageIndex = position;
        mSelection = -1;
        if (mIndicatorView != null) {
            mIndicatorView.setSelectIndex(position);
        }
        setCurrentItem(position / pageSize, true);
    }

    /**
     * 获取总页数
     *
     * @return
     */
    public int getPageCount() {
        return mHGridViewList.size();
    }

    /**
     * 获取总个数
     *
     * @return
     */
    public int getPageSize() {
        return mColumnNumber * mRowNumber;
    }

    /**
     * 设置指示器
     *
     * @param _indicator
     */
    public void setIndicator(IndicatorView _indicator) {
        this.mIndicatorView = _indicator;
    }


    /**
     * 自动切换开启.
     */
    public void play() {
        if (mHGridViewList.size() <= 1) {
            return;
        }
        mTimer = new Timer();
        mTask = new TimerTask() {
            @Override
            public void run() {
                notifyAutoSwitch();
            }
        };
        isAuto = true;
        mTimer.schedule(mTask, mAutoSwitchRate, mAutoSwitchRate);
    }

    /**
     * 停止播放
     */
    public void stop() {
        if (mTask != null) {
            mTask.cancel();
            mTask = null;
        }

        if (mTimer != null) {
            mTimer.cancel();
            mTimer = null;
        }

        if (mAutoSwitchHandler != null) {
            isAuto = false;
            mAutoSwitchHandler.removeMessages(FLAG_AUTO_SWITCH);
        }
    }

    /**
     * 设置ViewPager的切换动画
     *
     * @param _pageTransformer
     */
    public void setPageTransformer(PageTransformer _pageTransformer) {
        setPageTransformer(true, _pageTransformer);
    }

    /**
     * 滑动速度
     *
     * @param _duration
     */
    public void setAutoDuration(int _duration) {
        try {
            Field field = ViewPager.class.getDeclaredField("mScroller");
            field.setAccessible(true);
            FixedSpeedScroller scroller = new FixedSpeedScroller(this.getContext(),
                    new AccelerateInterpolator());
            field.set(this, scroller);
            scroller.setmDuration(_duration);
        } catch (Exception e) {
        }
    }
}
