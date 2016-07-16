package com.droidworker.lib;

import android.content.Context;
import android.content.res.TypedArray;
import android.util.AttributeSet;
import android.util.TypedValue;
import android.view.MotionEvent;
import android.view.ViewGroup;
import android.widget.HorizontalScrollView;
import android.widget.LinearLayout;

/**
 * @author luoyanfeng@le.com
 */
public class SlidingMenu extends HorizontalScrollView {
    private final static int EMPTY_STATE = 0x100;
    private final static int USER_MOVE = 0x101;
    private final static int AUTO_CLOSE = 0x102;
    private int mMenuWidth;
    private int mHalfMenuWidth;
    private int mEdgeArea;
    private boolean mMeasured;
    private boolean isOpen;
    private int mState;
    private SlidingMenuListener mSlidingMenuListener;
    private ViewGroup mMenu;
    private ViewGroup mContent;

    public interface SlidingMenuListener {
        void opened();

        void closed();
    }

    public SlidingMenu(Context context) {
        this(context, null);
    }

    public SlidingMenu(Context context, AttributeSet attrs) {
        super(context, attrs);

        TypedArray typedArray = context.obtainStyledAttributes(attrs, R.styleable.SlidingMenu);
        mMenuWidth = typedArray.getDimensionPixelSize(R.styleable.SlidingMenu_menu_width, 0);
        mEdgeArea = typedArray.getDimensionPixelSize(R.styleable.SlidingMenu_edge_area, (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 20, getResources().getDisplayMetrics()));
        typedArray.recycle();
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {

        if (!mMeasured) {
            LinearLayout wrapper = (LinearLayout) getChildAt(0);
            mMenu = (ViewGroup) wrapper.getChildAt(0);
            mContent = (ViewGroup) wrapper.getChildAt(1);

            mMenu.getLayoutParams().width = mMenuWidth;
            mContent.getLayoutParams().width = getResources().getDisplayMetrics().widthPixels;
            mHalfMenuWidth = mMenuWidth / 2;
        }
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
    }

    @Override
    protected void onLayout(boolean changed, int l, int t, int r, int b) {
        super.onLayout(changed, l, t, r, b);

        if (changed) {
            scrollTo(mMenuWidth, 0);
            mMeasured = true;
        }
    }

    @Override
    public boolean onInterceptTouchEvent(MotionEvent ev) {
        final int action = ev.getAction();
        switch (action) {
            case MotionEvent.ACTION_DOWN:
                final float downX = ev.getX();
                if (isOpen) {
                    //如果菜单打开了,用户触摸在指定区域内,则可以滑动,否则直接关闭菜单
                    if (mMenuWidth <= downX && downX <= mMenuWidth + mEdgeArea) {
                        mState = USER_MOVE;
                        return true;
                    } else if (downX > mMenuWidth + mEdgeArea) {
                        mState = AUTO_CLOSE;
                        return true;
                    }
                } else {
                    //如果菜单关闭,用户触摸在指定区域内,则可以滑动,否则不处理touch事件
                    if (downX <= mEdgeArea) {
                        mState = USER_MOVE;
                        return true;
                    }
                }
        }
        mState = EMPTY_STATE;
        return false;
    }

    @Override
    public boolean onTouchEvent(MotionEvent ev) {
        final int action = ev.getAction();
        switch (action) {
            case MotionEvent.ACTION_UP:
                //用户滑动,则判断是完成打开还是关闭动作,如果是自动关闭,则关闭菜单
                final float upX = ev.getX();
                if (mState == USER_MOVE) {
                    if (upX >= mHalfMenuWidth) {
                        open();
                    } else {
                        close();
                    }
                    return true;
                } else if (mState == AUTO_CLOSE) {
                    close();
                    return true;
                }

        }
        return super.onTouchEvent(ev);
    }

    /**
     * 打开菜单
     */
    public void open() {
        smoothScrollTo(0, 0);
        isOpen = true;
    }

    /**
     * 关闭菜单
     */
    public void close() {
        smoothScrollTo(mMenuWidth, 0);
        isOpen = false;
    }

    /**
     * Toggle
     */
    public void toggle() {
        if (isOpen) {
            close();
        } else {
            open();
        }
    }

    @Override
    protected void onScrollChanged(int l, int t, int oldl, int oldt) {
        super.onScrollChanged(l, t, oldl, oldt);

        final float scale = l * 1.0f / mMenuWidth;
        mMenu.setTranslationX(mMenuWidth * scale * 0.6f);
    }

    public void setSlidingMenuListener(SlidingMenuListener slidingMenuListener) {
        mSlidingMenuListener = slidingMenuListener;
    }

    public ViewGroup getMenu(){
        return mMenu;
    }

    public ViewGroup getContent(){
        return mContent;
    }
}
