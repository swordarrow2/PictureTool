package com.meng.qrtools;

import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.os.Vibrator;

import com.meng.MainActivity2;
import com.meng.qrtools.lib.ExceptionCatcher;
import com.meng.qrtools.lib.SharedPreferenceHelper;

import java.io.File;
import java.io.IOException;
import java.util.Date;

public class MainActivity extends Activity{
    public static MainActivity instence;
    public SharedPreferenceHelper sharedPreference;
    public static Boolean lightTheme=true;
    private final String exDir=Environment.getExternalStorageDirectory().getAbsolutePath();
    private final String tmpFolder=exDir+"/Pictures/QRcode/tmp/";
    private final String awesomeQRPath=exDir+"/Pictures/QRcode/AwesomeQR/";
    private final String arbAwesomeQRPath=exDir+"/Pictures/QRcode/arbAwesomeQR/";
    private final String gifArbAwesomeQRPath=exDir+"/Pictures/QRcode/gifArbAwesomeQR/";
    private final String gifAwesomeQRPath=exDir+"/Pictures/QRcode/gifAwesomeQR/";
    private final String gifPath=exDir+"/Pictures/QRcode/gif/";
    private final String barcodePath=exDir+"/Pictures/QRcode/";
    public final int CROP_REQUEST_CODE=3;

    protected void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
        instence=this;
        ExceptionCatcher.getInstance().init(this);
        sharedPreference=new SharedPreferenceHelper(this,"main");
        lightTheme=sharedPreference.getBoolean("useLightTheme",true);
        startActivity(new Intent(MainActivity.this,MainActivity2.class).putExtra("setTheme",getIntent().getBooleanExtra("setTheme",false)));
        finish();
        overridePendingTransition(0,0);
    }

    public String getTmpFolder(){
        File f=new File(tmpFolder+".nomedia");
        if(!f.exists()){
            try{
                f.createNewFile();
            }catch(IOException e){
                log.e(e);
            }
        }
        return tmpFolder;
    }

    public String getAwesomeQRPath(){
        File f=new File(awesomeQRPath);
        if(!f.exists()) f.mkdirs();
        return awesomeQRPath+(new Date()).toString()+".png";
    }

    public String getArbAwesomeQRPath(){
        File f=new File(arbAwesomeQRPath);
        if(!f.exists()) f.mkdirs();
        return arbAwesomeQRPath+(new Date()).toString()+".png";
    }

    public String getGifAwesomeQRPath(){
        File f=new File(gifAwesomeQRPath);
        if(!f.exists()) f.mkdirs();
        return gifAwesomeQRPath+(new Date()).toString()+".gif";
    }

    public String getGifArbAwesomeQRPath(){
        File f=new File(gifArbAwesomeQRPath);
        if(!f.exists()) f.mkdirs();
        return gifArbAwesomeQRPath+(new Date()).toString()+".gif";
    }

    public String getGifPath(){
        File f=new File(gifPath);
        if(!f.exists()) f.mkdirs();
        return gifPath;
    }

    public String getBarcodePath(String barcodeFormat){
        File f=new File(barcodePath);
        if(!f.exists()) f.mkdirs();
        return barcodePath+barcodeFormat+"/"+(new Date()).toString()+".png";
    }

    public void doVibrate(long time){
        Vibrator vibrator=(Vibrator)getSystemService(VIBRATOR_SERVICE);
        vibrator.vibrate(time);
    }

    public Uri cropPhoto(Uri uri,boolean needCrop){
        if(!needCrop) return uri;
        Intent intent=new Intent("com.android.camera.action.CROP");
        intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
        intent.addFlags(Intent.FLAG_GRANT_WRITE_URI_PERMISSION);
        intent.setDataAndType(uri,"image/*");
        intent.putExtra("crop","true");
        intent.putExtra("aspectX",1);
        intent.putExtra("aspectY",1);
        intent.putExtra("outputX",300);
        intent.putExtra("outputY",300);
        intent.putExtra("return-data",true);
        startActivityForResult(intent,CROP_REQUEST_CODE);
        return uri;
    }
}
