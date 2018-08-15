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
		FrameLayout ll = (FrameLayout) findViewById(R.id.frameLayout1); // ��ȡ�����ļ��е�֡���ֹ�����
		ll.addView(new MyView(this)); // ���Զ�����ͼ��ӵ�֡���ֹ�������

	}

	public class MyView extends View {
		private Bitmap bitmap; // Դͼ��Ҳ���Ǳ���ͼ��
		private ShapeDrawable drawable;

		private final int RADIUS = 57; // �Ŵ󾵵İ뾶

		private final int FACTOR = 2; // �Ŵ���
		private Matrix matrix = new Matrix();
		private Bitmap bitmap_magnifier; // �Ŵ�λͼ
		private int m_left = 0; // �Ŵ󾵵���߾�
		private int m_top = 0; // �Ŵ󾵵Ķ��߾�

		public MyView(Context context) {
			super(context);
			Bitmap bitmap_source = BitmapFactory.decodeResource(getResources(),
					R.drawable.source);	//��ȡҪ��ʾ��Դͼ��
			bitmap = bitmap_source;
			BitmapShader shader = new BitmapShader(Bitmap.createScaledBitmap(
					bitmap_source, bitmap_source.getWidth() * FACTOR,
					bitmap_source.getHeight() * FACTOR, true), TileMode.CLAMP,
					TileMode.CLAMP);	//����BitmapShader����
			// Բ�ε�drawable
			drawable = new ShapeDrawable(new OvalShape());
			drawable.getPaint().setShader(shader);
			drawable.setBounds(0, 0, RADIUS * 2, RADIUS * 2); // ����Բ�����о���
			bitmap_magnifier = BitmapFactory.decodeResource(getResources(),
					R.drawable.magnifier);	//��ȡ�Ŵ�ͼ��
			m_left = RADIUS - bitmap_magnifier.getWidth() / 2; // ����Ŵ󾵵�Ĭ����߾�
			m_top = RADIUS - bitmap_magnifier.getHeight() / 2; // ����Ŵ󾵵�Ĭ���ұ߾�
		}

		@Override
		protected void onDraw(Canvas canvas) {
			super.onDraw(canvas);
			canvas.drawBitmap(bitmap, 0, 0, null); // ���Ʊ���ͼ��
			canvas.drawBitmap(bitmap_magnifier, m_left, m_top, null); // ���ƷŴ�
			drawable.draw(canvas); // ���ƷŴ���ͼ��
		}

		@Override
		public boolean onTouchEvent(MotionEvent event) {
			final int x = (int) event.getX(); // ��ȡ��ǰ�������X������
			final int y = (int) event.getY(); // ��ȡ��ǰ�������Y������
			matrix.setTranslate(RADIUS - x * FACTOR, RADIUS - y * FACTOR); // ƽ�Ƶ�����shader����ʼλ��
			drawable.getPaint().getShader().setLocalMatrix(matrix);
			drawable.setBounds(x - RADIUS, y - RADIUS, x + RADIUS, y + RADIUS); // ����Բ�����о���
			m_left = x - bitmap_magnifier.getWidth() / 2; // ����Ŵ󾵵���߾�
			m_top = y - bitmap_magnifier.getHeight() / 2; // ����Ŵ󾵵��ұ߾�
			invalidate(); // �ػ滭��
			return true;
		}
	}
}
