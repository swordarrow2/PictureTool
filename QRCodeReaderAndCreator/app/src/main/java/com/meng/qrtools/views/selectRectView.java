package com.meng.qrtools.views;

import android.content.*;
import android.graphics.*;
import android.util.*;
import android.view.*;

public class selectRectView extends View{
	
	public Bitmap imageViewBackground = null;
	float xishu;
	private Bitmap bmpUseRect = null;
	private float mLeft;
    private float mTop;
	boolean seted=false;
	
	public selectRectView(Context c,AttributeSet attr){
		super(c,attr);
	}

	public selectRectView(Context context,Bitmap seleBmp,float screenW,float screenH,int qrSize){
		super(context);
		setup(seleBmp,screenW,screenH,qrSize);
		seted=true;
	}

	public void setup(Bitmap seleBmp,float screenW,float screenH,int qrSize){
		float bmpW = seleBmp.getWidth();
		float bmpH = seleBmp.getHeight();
		xishu=Math.min(screenH/bmpH,screenW/bmpW);
		imageViewBackground=scaleBitmap(seleBmp,xishu);
		bmpUseRect=Bitmap.createBitmap((int) (qrSize*xishu),(int) (qrSize*xishu),Bitmap.Config.ARGB_8888);
		Canvas c = new Canvas(bmpUseRect);
		c.drawARGB(0x7f,0x7f,0xca,0x00);
		seted=true;
	}   
	private Bitmap scaleBitmap(Bitmap origin,float ratio){
        if(origin==null){
            return null;
        }
        int width = origin.getWidth();
        int height = origin.getHeight();
        Matrix matrix = new Matrix();
        matrix.preScale(ratio,ratio);
        Bitmap newBM = Bitmap.createBitmap(origin,0,0,width,height,matrix,false);
        if(newBM.equals(origin)){
            return newBM;
        }
     //   origin.recycle();
        return newBM;
    }
	@Override
	protected void onDraw(Canvas canvas){
		super.onDraw(canvas);
		if(!seted)return;
		canvas.drawBitmap(imageViewBackground,0,0,null); // 绘制背景图像
		canvas.drawBitmap(bmpUseRect,
						  mLeft,
						  mTop,
						  null); // 绘制选择框
	}

	@Override
	public boolean onTouchEvent(MotionEvent event){
		if(!seted)return true;
		final int x = (int) event.getX(); // 获取当前触摸点的X轴坐标
		final int y = (int) event.getY(); // 获取当前触摸点的Y轴坐标
		mLeft=between(x-bmpUseRect.getWidth()/2,0,imageViewBackground.getWidth()-bmpUseRect.getWidth()); // 计算放大镜的左边距
		mTop=between(y-bmpUseRect.getHeight()/2,0,imageViewBackground.getHeight()-bmpUseRect.getHeight()); // 计算放大镜的右边距
		//tv.setText("x:"+(mLeft/xishu)+"  y:"+(mTop/xishu));
		invalidate(); // 重绘画布
		return true;
	}
	
	private int between(float a,int min,int max){
        if(a<min){
            a=min;
        }
        if(a>max){
            a=max;
        }
        return (int) a;
    }
	public float getSelectLeft(){
		return mLeft;
	}
	public float getSelectTop(){
		return mTop;
	}
	public float getXishu(){
		return xishu;
	}
}
