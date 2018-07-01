package com.meng.qqbotandroid;

import android.app.*;
import android.graphics.*;
import android.graphics.drawable.*;
import android.os.*;
import android.widget.*;
import com.meng.mbrowser.tools.*;

public class ShowQrCodeActivity extends Activity{
    ImageView QRCodeImageView;

    @Override
    protected void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
        QRCodeImageView =new ImageView(this);
        setContentView(QRCodeImageView);
        QRCodeImageView.setImageBitmap(BitmapFactory.decodeFile(getIntent().getStringExtra("url")));
	}


}
