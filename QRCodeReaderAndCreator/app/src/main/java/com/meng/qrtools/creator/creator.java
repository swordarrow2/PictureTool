package com.meng.qrtools.creator;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.*;
import android.support.v7.app.AppCompatActivity;
import android.widget.ImageView;

import com.meng.qrtools.R;
import android.app.*;
import android.view.*;
import android.widget.Toast;

public class creator extends Fragment {
	ImageView qrcode1;

	@Override
	public View onCreateView(LayoutInflater inflater,ViewGroup container,Bundle savedInstanceState){
		// TODO: Implement this method
		return inflater.inflate(R.layout.qr_main, container, false);
			}

	@Override
	public void onViewCreated(View view,Bundle savedInstanceState){
		// TODO: Implement this method
		super.onViewCreated(view,savedInstanceState);
		qrcode1 = (ImageView)view. findViewById(R.id.qrcode1);
		qrcode1.setImageBitmap(QRCode.createQRCode("http://www.tmtpost.com/2536837.html"));
	}




}
