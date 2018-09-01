package com.example.newbiechen.ireader.ui.activity;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;

import com.example.newbiechen.ireader.R;
import com.example.newbiechen.ireader.utils.BarUtils;
import com.example.newbiechen.ireader.utils.BrightnessUtils;
import com.example.newbiechen.ireader.widget.PicPage.PicPageLoader;
import com.example.newbiechen.ireader.widget.PicPage.PicPageView;

import butterknife.BindView;
import butterknife.ButterKnife;

public class PicShowActivity extends AppCompatActivity {

    @BindView(R.id.pic_page)
    PicPageView mPvPage;

    PicPageLoader mPageLoader;
    int[] resIds = {R.drawable.p1_1080
            , R.drawable.p2_1080
            , R.drawable.p3_1080
            , R.drawable.p4_1080
            , R.drawable.p5_1080
            , R.drawable.p6_1080
            , R.drawable.p7_1080
            , R.drawable.theme_leather_bg
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_pic_show);
        ButterKnife.bind(this);
//        SystemBarUtils.hideStableStatusBar(this);
        BarUtils.setStatusBarVisibility(this, false);
        BrightnessUtils.setBrightness(this,255);
        //获取页面加载器，初始化页面加载器
        mPageLoader = mPvPage.getPageLoader(resIds);

        mPvPage.setTouchListener(new PicPageView.TouchListener() {
            @Override
            public boolean onTouch() {
                return true;
            }

            @Override
            public void center() {

            }

            @Override
            public void prePage() {
            }

            @Override
            public void nextPage() {
            }

            @Override
            public void cancel() {

            }
        });
    }

    public String getResUrl(int position) {
        return position % 2 == 0 ? "https://ws1.sinaimg.cn/large/0065oQSqly1fubd0blrbuj30ia0qp0yi.jpg"
                : "https://ws1.sinaimg.cn/large/0065oQSqly1fuh5fsvlqcj30sg10onjk.jpg";
    }
}
