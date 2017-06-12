package com.tuwa.smarthome.view;

import android.content.Context;
import android.support.v4.view.ViewPager;
import android.util.AttributeSet;
import android.view.MotionEvent;


/**
 * @类名    NoScrollViewPager
 * @创建者   ppa
 * @创建时间 2016-3-22
 * @描述   屏蔽左右滑动的ViewPager
 */
public class NoScrollViewPager extends ViewPager{

	public NoScrollViewPager(Context context) {
		super(context);
		// TODO Auto-generated constructor stub
	}

	public NoScrollViewPager(Context context, AttributeSet attrs) {
		super(context, attrs);
		// TODO Auto-generated constructor stub
	}
    //使ViewPager不能左右滑动
	@Override
	public boolean onTouchEvent(MotionEvent arg0) {
		// TODO Auto-generated method stub
		return false;
	}
	
	//不拦截子页面的滑动事件
	@Override
	public boolean onInterceptTouchEvent(MotionEvent arg0) {
		// TODO Auto-generated method stub
		return false;
	}
}
