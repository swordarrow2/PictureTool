package com.meng.picTools;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.os.Environment;
import android.os.Vibrator;

import com.meng.picTools.lib.DataBaseHelper;
import com.meng.picTools.lib.ExceptionCatcher;
import com.meng.picTools.lib.SharedPreferenceHelper;

import java.io.File;
import java.io.IOException;
import java.util.Date;

public class MainActivity extends Activity {
    public static MainActivity instence;
    public final int CROP_REQUEST_CODE = 3;
    public static Boolean lightTheme = true;
    private final String exDir = Environment.getExternalStorageDirectory().getAbsolutePath();

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        instence = this;
        ExceptionCatcher.getInstance().init(this);
        SharedPreferenceHelper.Init(this, "main");
        DataBaseHelper.init(this);
        lightTheme = SharedPreferenceHelper.getBoolean("useLightTheme", true);
        startActivity(new Intent(MainActivity.this, MainActivity2.class).putExtra("setTheme", getIntent().getBooleanExtra("setTheme", false)));
        finish();
        overridePendingTransition(0, 0);
    }

    public String getTmpFolder() {
        String tmpFolder = exDir + "/Pictures/picTool/tmp/";
        File f = new File(tmpFolder + ".nomedia");
        if (!f.exists()) {
            try {
                f.createNewFile();
            } catch (IOException e) {
                LogTool.e(e);
            }
        }
        return tmpFolder;
    }

    public String getPreDownloadJsonPath() {
        return exDir + "/pixivLike.json";
    }

    public String getImagePath(Class tClass) {
        File folder = new File(exDir + "/Pictures/picTool/" + tClass.getSimpleName());
        if (!folder.exists()) folder.mkdirs();
        return exDir + "/Pictures/picTool/" + tClass.getSimpleName() + "/" +
                (SharedPreferenceHelper.getBoolean("useTimeStamp") ? String.valueOf(System.currentTimeMillis()) : new Date().toString());
    }

    public String getAwesomeQRPath() {
        String awesomeQRPath = exDir + "/Pictures/picTool/AwesomeQR/";
        File f = new File(awesomeQRPath);
        if (!f.exists()) f.mkdirs();
        return awesomeQRPath + (SharedPreferenceHelper.getBoolean("useTimeStamp") ? String.valueOf(System.currentTimeMillis() / 1000) : new Date().toString()) + ".png";
    }

    public String getPixivZipPath(String zipName) {
        String pixivZipPath = exDir + "/Pictures/pixivZip/";
        File f = new File(pixivZipPath);
        if (!f.exists()) f.mkdirs();
        return pixivZipPath + zipName;
    }

    public String getPixivImagePath(String imgName) {
        String pixivImagePath = exDir + "/Pictures/pixivImage/";
        File f = new File(pixivImagePath);
        if (!f.exists()) f.mkdirs();
        return pixivImagePath + imgName;
    }

    public String getArbAwesomeQRPath() {
        String arbAwesomeQRPath = exDir + "/Pictures/picTool/arbAwesomeQR/";
        File f = new File(arbAwesomeQRPath);
        if (!f.exists()) f.mkdirs();
        return arbAwesomeQRPath + (SharedPreferenceHelper.getBoolean("useTimeStamp") ? String.valueOf(System.currentTimeMillis() / 1000) : new Date().toString()) + ".png";
    }

    public String getGifAwesomeQRPath() {
        String gifAwesomeQRPath = exDir + "/Pictures/picTool/gifAwesomeQR/";
        File f = new File(gifAwesomeQRPath);
        if (!f.exists()) f.mkdirs();
        return gifAwesomeQRPath + (SharedPreferenceHelper.getBoolean("useTimeStamp") ? String.valueOf(System.currentTimeMillis() / 1000) : new Date().toString()) + ".gif";
    }

    public String getGifArbAwesomeQRPath() {
        String gifArbAwesomeQRPath = exDir + "/Pictures/picTool/gifArbAwesomeQR/";
        File f = new File(gifArbAwesomeQRPath);
        if (!f.exists()) f.mkdirs();
        return gifArbAwesomeQRPath + (SharedPreferenceHelper.getBoolean("useTimeStamp") ? String.valueOf(System.currentTimeMillis() / 1000) : new Date().toString()) + ".gif";
    }

    public String getGifPath() {
        String gifPath = exDir + "/Pictures/picTool/gif/";
        File f = new File(gifPath);
        if (!f.exists()) f.mkdirs();
        return gifPath + (SharedPreferenceHelper.getBoolean("useTimeStamp") ? String.valueOf(System.currentTimeMillis() / 1000) : new Date().toString()) + ".gif";
    }

    public String getGifPath(String zipName) {
        String gifPath = exDir + "/Pictures/picTool/gif/";
        File f = new File(gifPath);
        if (!f.exists()) f.mkdirs();
        return gifPath + zipName.replace(".zip", "").replace(".gif", "") + ".gif";
    }


    public String getBarcodePath(String barcodeFormat) {
        String barcodePath = exDir + "/Pictures/QRcode/";
        File f = new File(barcodePath);
        if (!f.exists()) f.mkdirs();
        return barcodePath + barcodeFormat + "/" + (SharedPreferenceHelper.getBoolean("useTimeStamp") ? String.valueOf(System.currentTimeMillis() / 1000) : new Date().toString()) + ".png";
    }

    public void doVibrate(long time) {
        Vibrator vibrator = (Vibrator) getSystemService(VIBRATOR_SERVICE);
        vibrator.vibrate(time);
    }

}
