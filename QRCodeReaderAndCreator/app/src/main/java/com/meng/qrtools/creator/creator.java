package com.meng.qrtools.creator;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.os.*;
import android.support.v7.app.AppCompatActivity;
import android.widget.ImageView;

import com.meng.qrtools.R;
import android.app.*;
import android.view.*;

public class creator extends Fragment {
	ImageView qrcode1;
	ImageView qrcode5;

	@Override
	public View onCreateView(LayoutInflater inflater,ViewGroup container,Bundle savedInstanceState){
		// TODO: Implement this method
		return inflater.inflate(R.layout.qr_main, container, false);
		//return super.onCreateView(inflater,container,savedInstanceState);
	}

	@Override
	public void onViewCreated(View view,Bundle savedInstanceState){
		// TODO: Implement this method
		super.onViewCreated(view,savedInstanceState);
		
		qrcode1 = (ImageView)view. findViewById(R.id.qrcode1);
		qrcode5 = (ImageView)view. findViewById(R.id.qrcode5);

		qrcode1.setImageBitmap(QRCode.createQRCode("http://www.tmtpost.com/2536837.html"));
		qrcode5.setImageBitmap(QRCode.createQRCodeWithLogo5("http://www.jianshu.com/users/4a4eb4feee62/latest_articles", 500, drawableToBitmap(getResources().getDrawable(R.drawable.head))));
		
	}

	
	/*
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.qr_main);

		qrcode1 = (ImageView) findViewById(R.id.qrcode1);
		qrcode5 = (ImageView) findViewById(R.id.qrcode5);
		
		qrcode1.setImageBitmap(QRCode.createQRCode("http://www.tmtpost.com/2536837.html"));
		qrcode5.setImageBitmap(QRCode.createQRCodeWithLogo5("http://www.jianshu.com/users/4a4eb4feee62/latest_articles", 500, drawableToBitmap(getResources().getDrawable(R.drawable.head))));
	}
	*/

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
