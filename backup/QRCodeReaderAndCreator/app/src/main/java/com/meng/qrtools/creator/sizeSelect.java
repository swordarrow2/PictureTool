package com.meng.qrtools.creator;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Matrix;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.meng.qrtools.MainActivity;
import com.meng.qrtools.R;

import java.io.ByteArrayOutputStream;
import com.meng.*;
import android.view.*;

public class sizeSelect extends Activity{
    private String bmpPath = "";
    private float mLeft = 0f;
    private float mTop = 0f;
    private Bitmap background;
    private Bitmap bmpUseRect;
    private TextView tv;
    private Button btn;
    private int wantedSize = 0;
    float xishu = 0f;
	private Bitmap sourceBmp=null;
	ViewGroup.LayoutParams para;
    @Override
    protected void onCreate(Bundle savedInstanceState){
        // TODO: Implement this method
        super.onCreate(savedInstanceState);
        setContentView(R.layout.size_select);
        bmpPath=getIntent().getStringExtra("content");
        LinearLayout ll = (LinearLayout) findViewById(R.id.frameLayout1);
        tv=(TextView) findViewById(R.id.size_text);
        btn=(Button) findViewById(R.id.size_button);
        btn.setOnClickListener(new View.OnClickListener() {
				@Override
				public void onClick(View v){
					Intent intent = new Intent();
					sourceBmp=BitmapFactory.decodeFile(bmpPath);
					MainActivity2.tmpBackground=Bitmap.createBitmap(sourceBmp,between(mLeft/xishu,0,sourceBmp.getWidth()-wantedSize),between(mTop/xishu,0,sourceBmp.getWidth()-wantedSize),wantedSize,wantedSize);
					intent.putExtra("top",mTop/xishu);
					intent.putExtra("left",mLeft/xishu);
					setResult(RESULT_OK,intent);
					finish();
				}
			});
		sourceBmp=BitmapFactory.decodeFile(bmpPath);
        ll.addView(new MyView(this,sourceBmp,getIntent().getIntExtra("size",0)));

    }

    @Override
    public void setTheme(int resid){
        if(MainActivity.sharedPreference.getBoolean("useLightTheme",true)){
            super.setTheme(R.style.AppThemeLight);
        }else{
            super.setTheme(R.style.AppThemeDark);
        }
    }

    public class MyView extends View{

        public MyView(Context context,Bitmap sourceBmp,int size){
            super(context);
            wantedSize=size;
            DisplayMetrics dm = new DisplayMetrics();
            getWindowManager().getDefaultDisplay().getMetrics(dm);
            float bmpW = sourceBmp.getWidth();
            float bmpH = sourceBmp.getHeight();
            float screenW=dm.widthPixels;
            float screenH=dm.heightPixels;
            xishu=Math.min(screenH/bmpH,screenW/bmpW);
            background=scaleBitmap(sourceBmp,xishu);
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
	private int between(float a,int min,int max){
		if(a<min){
			a=min;
		}
		if(a>max){
			a=max;
		}
		return (int)a;
	}
	
}
