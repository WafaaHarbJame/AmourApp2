package com.amour.shop.SpichalViews;

import android.content.Context;
import android.util.AttributeSet;
import android.view.MotionEvent;

import androidx.viewpager.widget.ViewPager;

/**
 * Created by iSoft4is on 8/27/2016.
 */
public class CustomViewPager extends ViewPager {

    private boolean isPagingEnabled = true;


    public CustomViewPager(Context context) {
        super(context);
//        if (ViewCompat.getLayoutDirection(this.getRootView()) == ViewCompat.LAYOUT_DIRECTION_LTR) {
//            this.setScaleX(1);
//        } else {
//            this.setScaleX(-1);
//        }
    }

    public CustomViewPager(Context context, AttributeSet attrs) {
        super(context, attrs);
//        if (ViewCompat.getLayoutDirection(this.getRootView()) == ViewCompat.LAYOUT_DIRECTION_LTR) {
//            this.setScaleX(1);
//        } else {
//            this.setScaleX(-1);
//        }
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        return this.isPagingEnabled && super.onTouchEvent(event);
    }

    @Override
    public boolean onInterceptTouchEvent(MotionEvent event) {
        return this.isPagingEnabled && super.onInterceptTouchEvent(event);
    }

    public void setPagingEnabled(boolean b) {
        this.isPagingEnabled = b;
    }
}
