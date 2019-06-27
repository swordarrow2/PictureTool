package com.meng.picTools.libAndHelper.mengViews;

import android.content.Context;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;
import android.widget.ScrollView;

public class MengScrollView extends ScrollView {

    private MengSelectRectView mengSelectRectView;

    public MengScrollView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    /**
     * 覆写onInterceptTouchEvent方法，点击操作发生在ListView的区域的时候,
     * 返回false让ScrollView的onTouchEvent接收不到MotionEvent，而是把Event传到下一级的控件中
     */
    @Override
    public boolean onInterceptTouchEvent(MotionEvent motionEvent) {
        if (mengSelectRectView != null && checkArea(mengSelectRectView, motionEvent)) {
            return false;
        }
        return super.onInterceptTouchEvent(motionEvent);
    }

    private boolean checkArea(View view,MotionEvent event){
        float x = event.getRawX();
        float y = event.getRawY();
        int[] locate = new int[2];
        view.getLocationOnScreen(locate);
        int l = locate[0];
        int r = l + view.getWidth();
        int t = locate[1];
        int b = t + view.getHeight();
        return l < x && x < r && t < y && y < b;
    }

    public void setSelectView(MengSelectRectView mengSelectRectView) {
        this.mengSelectRectView = mengSelectRectView;
    }
}