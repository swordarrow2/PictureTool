package com.meng.qrtools.creator;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.os.*;
import android.support.v7.app.AppCompatActivity;
import android.widget.ImageView;

import com.meng.qrtools.R;

public class creator extends AppCompatActivity {
	ImageView qrcode1;
	ImageView qrcode5;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.qr_main);

		qrcode1 = (ImageView) findViewById(R.id.qrcode1);
		qrcode5 = (ImageView) findViewById(R.id.qrcode5);
		
		qrcode1.setImageBitmap(QRCode.createQRCode("http://www.tmtpost.com/2536837.html"));
		qrcode5.setImageBitmap(QRCode.createQRCodeWithLogo5("http://www.jianshu.com/users/4a4eb4feee62/latest_articles", 500, drawableToBitmap(getResources().getDrawable(R.drawable.head))));
	}

	public static Bitmap drawableToBitmap(Drawable drawable) {
		if (drawable instanceof BitmapDrawable) {
			return ((BitmapDrawable) drawable).getBitmap();
		}

		Bitmap bitmap = Bitmap.createBitmap(drawable.getIntrinsicWidth(), drawable.getIntrinsicHeight(), Bitmap.Config.ARGB_8888);
		Canvas canvas = new Canvas(bitmap);
		drawable.setBounds(0, 0, canvas.getWidth(), canvas.getHeight());
		drawable.draw(canvas);

		return bitmap;
	}
}
