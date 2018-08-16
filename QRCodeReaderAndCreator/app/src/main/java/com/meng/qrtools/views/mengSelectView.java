package com.meng.qrtools.views;
import android.view.*;
import android.content.*;
import android.util.*;

public class mengSelectView extends View{

	public mengSelectView(Context context,float screenW){
		super(context);
		DisplayMetrics dm = new DisplayMetrics();
		getWindowManager().getDefaultDisplay().getMetrics(dm);
		float bmpW = arbAwesomeFragment.selectedBmp.getWidth();
		float bmpH = arbAwesomeFragment.selectedBmp.getHeight();
		float screenW=dm.widthPixels;
		float screenH=dm.heightPixels;
		xishu=Math.min(screenH/bmpH,screenW/bmpW);
		background=scaleBitmap(arbAwesomeFragment.selectedBmp,xishu);
		bmpUseRect=Bitmap.createBitmap((int) (wantedSize*xishu),(int) (wantedSize*xishu),Bitmap.Config.ARGB_8888);
		Canvas c = new Canvas(bmpUseRect);
		c.drawARGB(0x7f,0x7f,0xca,0x00);

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
		origin.recycle();
		return newBM;
	}

	@Override
	protected void onDraw(Canvas canvas){
		super.onDraw(canvas);
		canvas.drawBitmap(background,0,0,null); // 绘制背景图像
		canvas.drawBitmap(bmpUseRect,
						  mLeft,
						  mTop,
						  null); // 绘制选择框
	}

	@Override
	public boolean onTouchEvent(MotionEvent event){
		final int x = (int) event.getX(); // 获取当前触摸点的X轴坐标
		final int y = (int) event.getY(); // 获取当前触摸点的Y轴坐标
		mLeft=between(x-bmpUseRect.getWidth()/2,0,background.getWidth()-bmpUseRect.getWidth()); // 计算放大镜的左边距
		mTop=between(y-bmpUseRect.getHeight()/2,0,background.getHeight()-bmpUseRect.getHeight()); // 计算放大镜的右边距
		tv.setText("x:"+(mLeft/xishu)+"  y:"+(mTop/xishu));
		invalidate(); // 重绘画布
		return true;
	}
}
