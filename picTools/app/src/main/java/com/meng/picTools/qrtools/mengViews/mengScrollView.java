package com.meng.picTools.qrtools.mengViews;

import android.content.Context;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;
import android.widget.ListView;
import android.widget.ScrollView;

/**
 * Created by Administrator on 2018/8/27.
 */
public class mengScrollView extends ScrollView {

    private mengSelectRectView v;

    public mengScrollView(Context context,AttributeSet attrs) {
        super(context, attrs);
    }

    /**
     * 覆写onInterceptTouchEvent方法，点击操作发生在ListView的区域的时候,
     * 返回false让ScrollView的onTouchEvent接收不到MotionEvent，而是把Event传到下一级的控件中
     */
    @Override
    public boolean onInterceptTouchEvent(MotionEvent ev) {
        // TODO Auto-generated method stub
        if (v != null && checkArea(v, ev)) {
            return false;
        }
        return super.onInterceptTouchEvent(ev);
    }

    private boolean checkArea(View v,MotionEvent event){
        float x = event.getRawX();
        float y = event.getRawY();
        int[] locate = new int[2];
        v.getLocationOnScreen(locate);
        int l = locate[0];
        int r = l + v.getWidth();
        int t = locate[1];
        int b = t + v.getHeight();
        if (l < x && x < r && t < y && y < b) {
            return true;
        }
        return false;
    }

    public void setSelectView(mengSelectRectView v) {
        this.v = v;
    }
}