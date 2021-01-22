package com.monresto.acidlabs.monresto;

import android.content.Context;

import androidx.viewpager.widget.ViewPager;

import android.util.AttributeSet;
import android.view.MotionEvent;

public class HackViewPager extends ViewPager {

    public HackViewPager(Context context) {
        super(context);
    }

    public HackViewPager(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    @Override
    public boolean onTouchEvent(MotionEvent ev) {
        return (getCurrentItem() != 0 || getChildCount() != 0) && super.onTouchEvent(ev);

    }

    @Override
    public boolean onInterceptTouchEvent(MotionEvent ev) {
        return (getCurrentItem() != 0 || getChildCount() != 0) && super.onInterceptTouchEvent(ev);

    }
}