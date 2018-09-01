package com.example.newbiechen.ireader.widget.PicPage;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.RectF;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewConfiguration;

import com.example.newbiechen.ireader.widget.animation.HorizonPageAnim;
import com.example.newbiechen.ireader.widget.animation.PageAnimation;
import com.example.newbiechen.ireader.widget.animation.ScrollPageAnim;
import com.example.newbiechen.ireader.widget.animation.SimulationPageAnim;
import com.example.newbiechen.ireader.widget.page.PageMode;

/**
 * Created by Administrator on 2016/8/29 0029.
 * 原作者的GitHub Project Path:(https://github.com/PeachBlossom/treader)
 * 绘制页面显示内容的类
 */
public class PicPageView extends View {

    private int mViewWidth = 0; // 当前View的宽
    private int mViewHeight = 0; // 当前View的高

    private int mStartX = 0;
    private int mStartY = 0;
    private boolean isMove = false;
    // 初始化参数
    private int mBgColor = 0xFFCEC29C;
    // 是否允许点击
    private boolean canTouch = true;
    // 唤醒菜单的区域
    private RectF mCenterRect = null;
    private boolean isPrepare;
    // 动画类
    private PageAnimation mPageAnim;
    //点击监听
    private TouchListener mTouchListener;
    //内容加载器
    private PicPageLoader mPageLoader;
    // 动画监听类
    private PageAnimation.OnPageChangeListener mPageAnimListener = new PageAnimation.OnPageChangeListener() {
        @Override
        public boolean hasPrev() {
            return PicPageView.this.hasPrevPage();
        }

        @Override
        public boolean hasNext() {
            return PicPageView.this.hasNextPage();
        }

        @Override
        public void pageCancel() {
            PicPageView.this.pageCancel();
        }
    };

    public PicPageView(Context context) {
        this(context, null);
    }

    public PicPageView(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public PicPageView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    //设置翻页的模式
    void setPageMode(PageMode pageMode) {
        //视图未初始化的时候，禁止调用
        if (mViewWidth == 0 || mViewHeight == 0) return;
        mPageAnim = new SimulationPageAnim(mViewWidth, mViewHeight, this, mPageAnimListener);
    }

    /**
     * 判断是否存在上一页
     *
     * @return
     */
    private boolean hasPrevPage() {
        mTouchListener.prePage();
        return mPageLoader.prev();
    }

    /**
     * 判断是否下一页存在
     *
     * @return
     */
    private boolean hasNextPage() {
        mTouchListener.nextPage();
        return mPageLoader.next();
    }

    public void setBgColor(int color) {
        mBgColor = color;
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        super.onTouchEvent(event);

        if (!canTouch && event.getAction() != MotionEvent.ACTION_DOWN) return true;

        int x = (int) event.getX();
        int y = (int) event.getY();
        switch (event.getAction()) {
            case MotionEvent.ACTION_DOWN:
                mStartX = x;
                mStartY = y;
                isMove = false;
                canTouch = mTouchListener.onTouch();
                mPageAnim.onTouchEvent(event);
                break;
            case MotionEvent.ACTION_MOVE:
                // 判断是否大于最小滑动值。
                int slop = ViewConfiguration.get(getContext()).getScaledTouchSlop();
                if (!isMove) {
                    isMove = Math.abs(mStartX - event.getX()) > slop || Math.abs(mStartY - event.getY()) > slop;
                }

                // 如果滑动了，则进行翻页。
                if (isMove) {
                    mPageAnim.onTouchEvent(event);
                }
                break;
            case MotionEvent.ACTION_UP:
                if (!isMove) {
                    //设置中间区域范围
                    if (mCenterRect == null) {
                        mCenterRect = new RectF(mViewWidth / 5, mViewHeight / 3,
                                mViewWidth * 4 / 5, mViewHeight * 2 / 3);
                    }

                    //是否点击了中间
                    if (mCenterRect.contains(x, y)) {
                        if (mTouchListener != null) {
                            mTouchListener.center();
                        }
                        return true;
                    }
                }
                mPageAnim.onTouchEvent(event);
                break;
        }
        return true;
    }

    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        super.onSizeChanged(w, h, oldw, oldh);
        mViewWidth = w;
        mViewHeight = h;

        isPrepare = true;

        if (mPageLoader != null) {
            mPageLoader.prepareDisplay(w, h);
        }
    }

    @Override
    public void computeScroll() {
        //进行滑动
        mPageAnim.scrollAnim();
        super.computeScroll();
    }

    @Override
    protected void onDraw(Canvas canvas) {

        //绘制背景
        canvas.drawColor(mBgColor);

        //绘制动画
        mPageAnim.draw(canvas);
    }

    @Override
    protected void onDetachedFromWindow() {
        super.onDetachedFromWindow();
        mPageAnim.abortAnim();
        mPageAnim.clear();

        mPageLoader = null;
        mPageAnim = null;
    }

    private void pageCancel() {
        mTouchListener.cancel();
        mPageLoader.pageCancel();
    }

    public void setTouchListener(TouchListener mTouchListener) {
        this.mTouchListener = mTouchListener;
    }

    public void drawNextPage() {
        if (!isPrepare) return;

        if (mPageAnim instanceof HorizonPageAnim) {
            ((HorizonPageAnim) mPageAnim).changePage();
        }
        mPageLoader.drawPage(getNextBitmap());
    }

    public Bitmap getNextBitmap() {
        if (mPageAnim == null) return null;
        return mPageAnim.getNextBitmap();
    }

    /**
     * 绘制当前页。
     */
    public void drawCurPage() {
        if (!isPrepare) return;

        if (mPageAnim instanceof ScrollPageAnim) {
            ((ScrollPageAnim) mPageAnim).resetBitmap();
        }


        mPageLoader.drawPage(getNextBitmap());
    }

    /**
     * 获取 PageLoader，
     * 这里完成了mPageLoader的初始化工作
     *
     * @param ids
     * @return
     */
    public PicPageLoader getPageLoader(int[] ids) {
        // 判是否已经存在
        if (mPageLoader != null) {
            return mPageLoader;
        }
        // 根据书籍类型，获取具体的加载器，本地存储或则是网络获取
        mPageLoader = new PicPageLoader(this, ids);
        // 判断是否 PageView 已经初始化完成
        if (mViewWidth != 0 || mViewHeight != 0) {
            // 初始化 PageLoader 的屏幕大小
            mPageLoader.prepareDisplay(mViewWidth, mViewHeight);
        }

        return mPageLoader;
    }

    public interface TouchListener {
        boolean onTouch();

        void center();

        void prePage();

        void nextPage();

        void cancel();
    }
}
