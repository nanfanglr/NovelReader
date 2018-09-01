package com.example.newbiechen.ireader.widget.PicPage;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Rect;
import android.support.v4.content.ContextCompat;

import com.example.newbiechen.ireader.R;
import com.example.newbiechen.ireader.model.local.ReadSettingManager;
import com.example.newbiechen.ireader.widget.page.PageMode;

/**
 * Created by newbiechen on 17-7-1.
 */

public class PicPageLoader {
    // 图片数据集合
    protected int[] mIds;

    private boolean isPrev;
    private Context mContext;
    // 页面显示类
    private PicPageView mPageView;
    // 当前绘制的url
    private int mCurPos = 0;
    // 绘制背景颜色的画笔(用来擦除需要重绘的部分)
    private Paint mBgPaint;
    // 阅读器的配置选项
    private ReadSettingManager mSettingManager;
    // 页面的翻页效果模式
    private PageMode mPageMode;
    //应用的宽高
    private int mDisplayWidth;
    private int mDisplayHeight;
    //当前页面的背景
    private int mBgColor;
    private boolean isFirstLoad = true;

    public PicPageLoader(PicPageView pageView, int[] urls) {
        mPageView = pageView;
        mContext = pageView.getContext();
        mIds = urls;
//        mCurPos = mIds.get(0);
        // 初始化数据
        initData();
        // 初始化画笔
        initPaint();
        // 初始化PageView
        initPageView();
    }

    private void initData() {
        // 获取配置管理器
        mSettingManager = ReadSettingManager.getInstance();
        // 获取配置参数
        mPageMode = PageMode.SIMULATION;
    }

    private void initPaint() {
        // 绘制背景的画笔
        mBgColor = ContextCompat.getColor(mContext, R.color.white);
        mBgPaint = new Paint();
        mBgPaint.setColor(mBgColor);
//        mPageView.drawCurPage();
    }

    private void initPageView() {
        //配置参数
        mPageView.setPageMode(mPageMode);
        mPageView.setBgColor(mBgColor);
    }

    /***********************************default method***********************************************/

    void drawPage(Bitmap bitmap) {
//        drawBackground(mPageView.getBgBitmap(), isUpdate);
        drawContent(bitmap);
        //更新绘制
        mPageView.invalidate();
    }

    private void drawContent(Bitmap bitmap) {
        Canvas canvas = new Canvas(bitmap);
//        /******绘制内容****/

        if (mIds != null) {
//            Glide.with(mContext)
//                    .load(getResUrl(mCurPos))
//                    .asBitmap()
//                    .centerCrop()
//                    .into(new SimpleTarget<Bitmap>() {
//                        @Override
//                        public void onResourceReady(Bitmap resource, GlideAnimation<? super Bitmap> glideAnimation) {
//                            Rect rect = new Rect(0, 0, mDisplayWidth, mDisplayHeight);
//                            canvas.drawBitmap(resource, null, rect, mBgPaint);
//                            if (isFirstLoad) {
//                                mPageView.invalidate();
//                                isFirstLoad = false;
//                            }
//                        }
//                    });

            Bitmap viewBitmap = BitmapFactory.decodeResource(mContext.getResources(), mIds[mCurPos]);
            Rect rect = new Rect(0, 0, mDisplayWidth, mDisplayHeight);
            if (viewBitmap != null) {
                canvas.drawBitmap(viewBitmap, null, rect, mBgPaint);
//                canvas.drawBitmap(viewBitmap, 0, 0, mBgPaint);
            }
        }


    }

    public String getResUrl(int position) {
        return position % 2 == 0 ? "https://ws1.sinaimg.cn/large/0065oQSqly1fubd0blrbuj30ia0qp0yi.jpg"
                : "https://ws1.sinaimg.cn/large/0065oQSqly1fuh5fsvlqcj30sg10onjk.jpg";
    }

    void prepareDisplay(int w, int h) {
        // 获取PageView的宽高
        mDisplayWidth = w;
        mDisplayHeight = h;
        // 重置 PageMode
        mPageView.setPageMode(mPageMode);
        // 重新绘制当前页
        mPageView.drawCurPage();
    }

    /**
     * 翻阅上一页
     *
     * @return
     */
    boolean prev() {
        mCurPos--;
        if (mCurPos < 0) {
            mCurPos = 0;
            return false;
        } else {
            isPrev = true;
            mPageView.drawNextPage();
            return true;
        }
    }

    /**
     * 翻到下一页
     *
     * @return:是否允许翻页
     */
    boolean next() {
        mCurPos++;
        if (mCurPos < mIds.length) {
            isPrev = false;
            mPageView.drawNextPage();
            return true;
        } else {
            mCurPos = mIds.length - 1;
            return false;
        }
    }

    // 取消翻页
    void pageCancel() {
        if (isPrev) {
            mCurPos++;
        } else {
            mCurPos--;
        }
    }


}
