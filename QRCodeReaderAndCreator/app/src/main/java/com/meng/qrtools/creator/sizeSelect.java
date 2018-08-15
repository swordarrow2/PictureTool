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

public class sizeSelect extends Activity {
    private String bmpPath = "";
    private float m_left = 0f;
    private float m_top = 0f;
    private Bitmap background;
    private Bitmap bmpUseRect;
    private TextView tv;
    private Button btn;
    private int wantedSize = 0;
    float xishu = 0f;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        // TODO: Implement this method
        super.onCreate(savedInstanceState);
        setContentView(R.layout.size_select);
        bmpPath = getIntent().getStringExtra("content");
        LinearLayout ll = (LinearLayout) findViewById(R.id.frameLayout1);
        tv = (TextView) findViewById(R.id.size_text);
        btn = (Button) findViewById(R.id.size_button);
        btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent();
                Bitmap bmp = Bitmap.createBitmap(BitmapFactory.decodeFile(bmpPath), (int) (m_left / xishu), (int) (m_top / xishu), wantedSize, wantedSize);
                ByteArrayOutputStream baos = new ByteArrayOutputStream();
                bmp.compress(Bitmap.CompressFormat.PNG, 100, baos);
                byte[] bitmapByte = baos.toByteArray();
                intent.putExtra("bitmap", bitmapByte);
                intent.putExtra("top",m_top/xishu);
                intent.putExtra("left",m_left/xishu);
                setResult(RESULT_OK, intent);
                finish();
            }
        });
        ll.addView(new MyView(this, BitmapFactory.decodeFile(bmpPath), getIntent().getIntExtra("size", 0)), 0);
    }

    @Override
    public void setTheme(int resid) {
        if (MainActivity.sharedPreference.getBoolean("useLightTheme", true)) {
            super.setTheme(R.style.AppThemeLight);
        } else {
            super.setTheme(R.style.AppThemeDark);
        }
    }

    public class MyView extends View {

        public MyView(Context context, Bitmap sourceBmp, int size) {
            super(context);
            wantedSize = size;
            DisplayMetrics dm = new DisplayMetrics();
            getWindowManager().getDefaultDisplay().getMetrics(dm);
            float bmpW = sourceBmp.getWidth();
            float bmpH = sourceBmp.getHeight();
            float screenW = dm.widthPixels;
            float screenH = dm.heightPixels;
            xishu = Math.min(screenH / bmpH, screenW / bmpW);
            background = scaleBitmap(sourceBmp, xishu);
            bmpUseRect = Bitmap.createBitmap((int) (wantedSize * xishu), (int) (wantedSize * xishu), Bitmap.Config.ARGB_8888);
            Canvas c = new Canvas(bmpUseRect);
            c.drawARGB(0x7f, 0x7f, 0xca, 0x00);
        }

        private Bitmap scaleBitmap(Bitmap origin, float ratio) {
            if (origin == null) {
                return null;
            }
            int width = origin.getWidth();
            int height = origin.getHeight();
            Matrix matrix = new Matrix();
            matrix.preScale(ratio, ratio);
            Bitmap newBM = Bitmap.createBitmap(origin, 0, 0, width, height, matrix, false);
            if (newBM.equals(origin)) {
                return newBM;
            }
            origin.recycle();
            return newBM;
        }

        @Override
        protected void onDraw(Canvas canvas) {
            super.onDraw(canvas);
            canvas.drawBitmap(background, 0, 0, null); // 绘制背景图像
            canvas.drawBitmap(bmpUseRect, m_left, m_top, null); // 绘制选择框
        }

        @Override
        public boolean onTouchEvent(MotionEvent event) {
            final int x = (int) event.getX(); // 获取当前触摸点的X轴坐标
            final int y = (int) event.getY(); // 获取当前触摸点的Y轴坐标
            m_left = x - bmpUseRect.getWidth() / 2; // 计算放大镜的左边距
            m_top = y - bmpUseRect.getHeight() / 2; // 计算放大镜的右边距
            tv.setText("x:" + (m_left/xishu) + "  y:" + (m_top/xishu));
            invalidate(); // 重绘画布
            return true;
        }
    }

}
