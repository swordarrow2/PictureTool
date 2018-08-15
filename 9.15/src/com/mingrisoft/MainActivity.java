package com.mingrisoft;

import android.app.Activity;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.BitmapShader;
import android.graphics.Canvas;
import android.graphics.Matrix;
import android.graphics.Shader.TileMode;
import android.graphics.drawable.ShapeDrawable;
import android.graphics.drawable.shapes.OvalShape;
import android.os.Bundle;
import android.view.MotionEvent;
import android.view.View;
import android.widget.FrameLayout;

public class MainActivity extends Activity {
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.main);
		FrameLayout ll = (FrameLayout) findViewById(R.id.frameLayout1); // 获取布局文件中的帧布局管理器
		ll.addView(new MyView(this)); // 将自定义视图添加到帧布局管理器中

	}

	public class MyView extends View {
		private Bitmap bitmap; // 源图像，也就是背景图像
		private ShapeDrawable drawable;

		private final int RADIUS = 57; // 放大镜的半径

		private final int FACTOR = 2; // 放大倍数
		private Matrix matrix = new Matrix();
		private Bitmap bitmap_magnifier; // 放大镜位图
		private int m_left = 0; // 放大镜的左边距
		private int m_top = 0; // 放大镜的顶边距

		public MyView(Context context) {
			super(context);
			Bitmap bitmap_source = BitmapFactory.decodeResource(getResources(),
					R.drawable.source);	//获取要显示的源图像
			bitmap = bitmap_source;
			BitmapShader shader = new BitmapShader(Bitmap.createScaledBitmap(
					bitmap_source, bitmap_source.getWidth() * FACTOR,
					bitmap_source.getHeight() * FACTOR, true), TileMode.CLAMP,
					TileMode.CLAMP);	//创建BitmapShader对象
			// 圆形的drawable
			drawable = new ShapeDrawable(new OvalShape());
			drawable.getPaint().setShader(shader);
			drawable.setBounds(0, 0, RADIUS * 2, RADIUS * 2); // 设置圆的外切矩形
			bitmap_magnifier = BitmapFactory.decodeResource(getResources(),
					R.drawable.magnifier);	//获取放大镜图像
			m_left = RADIUS - bitmap_magnifier.getWidth() / 2; // 计算放大镜的默认左边距
			m_top = RADIUS - bitmap_magnifier.getHeight() / 2; // 计算放大镜的默认右边距
		}

		@Override
		protected void onDraw(Canvas canvas) {
			super.onDraw(canvas);
			canvas.drawBitmap(bitmap, 0, 0, null); // 绘制背景图像
			canvas.drawBitmap(bitmap_magnifier, m_left, m_top, null); // 绘制放大镜
			drawable.draw(canvas); // 绘制放大后的图像
		}

		@Override
		public boolean onTouchEvent(MotionEvent event) {
			final int x = (int) event.getX(); // 获取当前触摸点的X轴坐标
			final int y = (int) event.getY(); // 获取当前触摸点的Y轴坐标
			matrix.setTranslate(RADIUS - x * FACTOR, RADIUS - y * FACTOR); // 平移到绘制shader的起始位置
			drawable.getPaint().getShader().setLocalMatrix(matrix);
			drawable.setBounds(x - RADIUS, y - RADIUS, x + RADIUS, y + RADIUS); // 设置圆的外切矩形
			m_left = x - bitmap_magnifier.getWidth() / 2; // 计算放大镜的左边距
			m_top = y - bitmap_magnifier.getHeight() / 2; // 计算放大镜的右边距
			invalidate(); // 重绘画布
			return true;
		}
	}
}
