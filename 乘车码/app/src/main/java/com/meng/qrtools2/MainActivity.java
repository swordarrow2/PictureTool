package com.meng.qrtools2;

import android.app.*;
import android.content.*;
import android.os.*;
import com.meng.*;
import com.meng.qrtools.lib.*;
import java.io.*;

public class MainActivity extends Activity{
    public static MainActivity instence;

    public static Boolean lightTheme=true;
    private final String exDir=Environment.getExternalStorageDirectory().getAbsolutePath();
	private final String barcodePath=exDir+"/Pictures/QRcode/";

    protected void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
        instence=this;
        ExceptionCatcher.getInstance().init(this);
		startActivity(new Intent(MainActivity.this,MainActivity2.class).putExtra("setTheme",getIntent().getBooleanExtra("setTheme",false)));
        finish();
        overridePendingTransition(0,0);
	  }

    public String getBarcodePath(String barcodeFormat){
        File f=new File(barcodePath);
        if(!f.exists()) f.mkdirs();
        return barcodePath+barcodeFormat+"/"+String.valueOf(System.currentTimeMillis()/1000)+".png";
	  }

    public void doVibrate(long time){
        Vibrator vibrator=(Vibrator)getSystemService(VIBRATOR_SERVICE);
        vibrator.vibrate(time);
	  }

  }
