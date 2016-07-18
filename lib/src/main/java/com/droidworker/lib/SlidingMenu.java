package com.droidworker.lib;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.support.annotation.ColorInt;
import android.support.v4.content.ContextCompat;
import android.support.v4.view.ViewCompat;
import android.util.AttributeSet;
import android.view.Gravity;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.HorizontalScrollView;
import android.widget.LinearLayout;

/**
 * 侧滑菜单
 * 设计为从内容区域边缘滑动可以展开菜单,以便支持内容区域可以有水平滚动的视图.
 * 支持FitSystemWindow属性,可以绘制状态栏,借鉴了{@link android.support.v4.widget.DrawerLayout}的实现方式.
 *
 * @author https://github.com/DroidWorkerLYF
 */
public class SlidingMenu extends HorizontalScrollView implements SlidingMenuImpl {
    /**
     * 空状态:此状态下不需要在抬手后处理
     */
    private static final int EMPTY_STATE = 0x100;
    /**
     * 用户滑动状态:此状态跟随用户滑动,移动视图
     */
    private static final int USER_MOVE = 0x101;
    /**
     * 自动关闭状态:菜单需要隐藏
     */
    private static final int AUTO_CLOSE = 0x102;
    private static final int CONTENT_ELEVATION = 10;
    /**
     * 菜单宽度
     */
    private int mMenuWidth;
    /**
     * 菜单宽度的一半,用于判断菜单的展开和关闭操作
     */
    private int mHalfMenuWidth;
    /**
     * 内容视图边缘响应用户操作的区域宽度
     */
    private int mEdgeArea;
    /**
     * 菜单和内容区域的大小是否已经设置了
     */
    private boolean mMeasured;
    /**
     * 菜单是否打开
     */
    private boolean isOpen;
    /**
     * Touch事件触发的状态
     */
    private int mState;
    private onSlidingListener mOnSlidingListener;
    /**
     * 菜单视图
     */
    private ViewGroup mMenu;
    /**
     * 内容视图
     */
    private ViewGroup mContent;
    /**
     * 是否绘制系统的StatusBar的背景
     */
    private boolean mDrawStatusBarBackground;
    /**
     * 内容区域StatusBar的背景
     */
    private Drawable mStatusBarBackground;
    /**
     * 菜单区域StatusBar的背景
     */
    private Drawable mMenuStatusBarBackground;
    private Object mLastInsets;
    private float mContentElevation;

    public interface onSlidingListener {
        void opened();

        void closed();
    }

    interface SlidingMenuCompatImpl {
        void configureApplyInsets(View slidingMenu);

        void dispatchChildInsets(View child, Object insets, int menuGravity);

        void applyMarginInsets(MarginLayoutParams lp, Object insets, int menuGravity);

        int getTopInset(Object lastInsets);

        Drawable getDefaultStatusBarBackground(Context context);
    }

    static class DrawerLayoutCompatImplBase implements SlidingMenuCompatImpl {
        public void configureApplyInsets(View slidingMenu) {
        }

        public void dispatchChildInsets(View child, Object insets, int menuGravity) {
        }

        public void applyMarginInsets(MarginLayoutParams lp, Object insets, int menuGravity) {
        }

        public int getTopInset(Object insets) {
            return 0;
        }

        public Drawable getDefaultStatusBarBackground(Context context) {
            return null;
        }
    }

    static class DrawerLayoutCompatImplApi21 implements SlidingMenuCompatImpl {

        @Override
        public void configureApplyInsets(View slidingMenu) {
            SlidingMenuCompatApi21.configureApplyInsets(slidingMenu);
        }

        @Override
        public void dispatchChildInsets(View child, Object insets, int menuGravity) {
            SlidingMenuCompatApi21.dispatchChildInsets(child, insets, menuGravity);
        }

        @Override
        public void applyMarginInsets(MarginLayoutParams lp, Object insets, int menuGravity) {
            SlidingMenuCompatApi21.applyMarginInsets(lp, insets, menuGravity);
        }

        @Override
        public int getTopInset(Object lastInsets) {
            return SlidingMenuCompatApi21.getTopInset(lastInsets);
        }

        @Override
        public Drawable getDefaultStatusBarBackground(Context context) {
            return SlidingMenuCompatApi21.getDefaultStatusBarBackground(context);
        }
    }

    static final SlidingMenuCompatImpl IMPL;

    static {
        final int version = Build.VERSION.SDK_INT;
        if (version >= 21) {
            IMPL = new SlidingMenu.DrawerLayoutCompatImplApi21();
        } else {
            IMPL = new SlidingMenu.DrawerLayoutCompatImplBase();
        }
    }

    public SlidingMenu(Context context) {
        this(context, null);
    }

    public SlidingMenu(Context context, AttributeSet attrs) {
        super(context, attrs);

        final float density = getResources().getDisplayMetrics().density;
        TypedArray typedArray = context.obtainStyledAttributes(attrs, R.styleable.SlidingMenu);
        mMenuWidth = typedArray.getDimensionPixelSize(R.styleable.SlidingMenu_menu_width, 0);
        mEdgeArea = typedArray.getDimensionPixelSize(R.styleable.SlidingMenu_edge_area, (int) (20 * density));
        int color = typedArray.getColor(R.styleable.SlidingMenu_menu_status_bar_color, Color.TRANSPARENT);
        typedArray.recycle();

        if (ViewCompat.getFitsSystemWindows(this)) {
            if (color != 0) {
                mMenuStatusBarBackground = new ColorDrawable(color);
            } else {
                mMenuStatusBarBackground = IMPL.getDefaultStatusBarBackground(context);
            }
            mStatusBarBackground = IMPL.getDefaultStatusBarBackground(context);
            IMPL.configureApplyInsets(this);
        }

        mContentElevation = CONTENT_ELEVATION * density;
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {

        if (!mMeasured) {
            //设置菜单和内容的宽度
            LinearLayout wrapper = (LinearLayout) getChildAt(0);
            mMenu = (ViewGroup) wrapper.getChildAt(0);
            mContent = (ViewGroup) wrapper.getChildAt(1);

            mMenu.getLayoutParams().width = mMenuWidth;
            mContent.getLayoutParams().width = getResources().getDisplayMetrics().widthPixels;
            mHalfMenuWidth = mMenuWidth / 2;
        }
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);

        final boolean applyInsets = mLastInsets != null && ViewCompat.getFitsSystemWindows(this);
        LinearLayout wrapper = (LinearLayout) getChildAt(0);
        for (int i = 0; i < wrapper.getChildCount(); i++) {
            final View child = wrapper.getChildAt(i);
            if (applyInsets) {
                IMPL.dispatchChildInsets(child, mLastInsets, Gravity.LEFT);
            } else {
                final LinearLayout.LayoutParams lp = (LinearLayout.LayoutParams) child.getLayoutParams();
                IMPL.applyMarginInsets(lp, mLastInsets, Gravity.LEFT);
            }
        }
        ViewCompat.setElevation(mContent, mContentElevation);
    }

    @Override
    protected void onLayout(boolean changed, int l, int t, int r, int b) {
        super.onLayout(changed, l, t, r, b);

        if (changed) {
            //将菜单隐藏起来
            scrollTo(mMenuWidth, 0);
            mMeasured = false;
        }
    }

    @Override
    public void setChildInsets(Object insets, boolean draw) {
        mLastInsets = insets;
        mDrawStatusBarBackground = draw;
        setWillNotDraw(!draw && getBackground() == null);
        requestLayout();
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);

        if (mDrawStatusBarBackground && mMenuStatusBarBackground != null && mStatusBarBackground != null) {
            final int inset = IMPL.getTopInset(mLastInsets);
            if (inset > 0) {
                mMenuStatusBarBackground.setBounds(0, 0, mMenuWidth, inset);
                mMenuStatusBarBackground.draw(canvas);
                mStatusBarBackground.setBounds(mMenuWidth, 0, mMenuWidth + getWidth(), inset);
                mStatusBarBackground.draw(canvas);
            }
        }
    }

    @Override
    public boolean onInterceptTouchEvent(MotionEvent ev) {
        final int action = ev.getAction();
        mState = EMPTY_STATE;
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
        notifyScrollState(l);
    }

    public void setOnSlidingListener(onSlidingListener onSlidingListener) {
        mOnSlidingListener = onSlidingListener;
    }

    private void notifyScrollState(int l) {
        if (mOnSlidingListener != null) {
            if (l == 0) {
                mOnSlidingListener.opened();
            } else if (l == mMenuWidth) {
                mOnSlidingListener.closed();
            }
        }
    }

    public ViewGroup getMenu() {
        return mMenu;
    }

    public ViewGroup getContent() {
        return mContent;
    }

    public void setStatusBarBackground(Drawable bg) {
        mStatusBarBackground = bg;
        invalidate();
    }

    public void setStatusBarBackground(int resId) {
        Drawable drawable = resId != 0 ? ContextCompat.getDrawable(getContext(), resId) : null;
        setStatusBarBackground(drawable);
    }

    public void setStatusBarBackgroundColor(@ColorInt int color){
        setStatusBarBackground(new ColorDrawable(color));
    }

    public void setMenuStatusBarBackground(Drawable bg){
        mMenuStatusBarBackground = bg;
        invalidate();
    }

    public void setMenuStatusBarBackground(int resId) {
        Drawable drawable = resId != 0 ? ContextCompat.getDrawable(getContext(), resId) : null;
        setMenuStatusBarBackground(drawable);
    }

    public void setMenuStatusBarBackgroundColor(@ColorInt int color){
        setMenuStatusBarBackground(new ColorDrawable(color));
    }
}
