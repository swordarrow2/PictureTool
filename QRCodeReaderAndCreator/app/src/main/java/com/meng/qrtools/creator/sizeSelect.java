package com.meng.qrtools.creator;

import android.app.Activity;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.widget.ImageView;

import com.meng.qrtools.MainActivity;
import com.meng.qrtools.R;

public class sizeSelect extends Activity {
    private String bmpPath = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        // TODO: Implement this method
        super.onCreate(savedInstanceState);
        setContentView(R.layout.size_select);
        bmpPath = getIntent().getStringExtra("content");
        ImageView iv=(ImageView)findViewById(R.id.test_imageView);
        iv.setImageBitmap(cropBitmap(BitmapFactory.decodeFile(bmpPath)));
    }

    private Bitmap cropBitmap(Bitmap bitmap) {
        int w = bitmap.getWidth(); // 得到图片的宽，高
        int h = bitmap.getHeight();
        int cropWidth = w >= h ? h : w;// 裁切后所取的正方形区域边长
        cropWidth /= 2;
        int cropHeight = (int) (cropWidth / 1.2);
        return Bitmap.createBitmap(bitmap, w / 3, 0, cropWidth, cropHeight, null, false);
    }

    @Override
    public void setTheme(int resid) {
        if (MainActivity.sharedPreference.getBoolean("useLightTheme", true)) {
            super.setTheme(R.style.AppThemeLight);
        } else {
            super.setTheme(R.style.AppThemeDark);
        }
    }

}
