package com.meng.picTools.sauceNao;

import android.app.Activity;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.widget.ImageView;

import com.meng.picTools.MainActivity;

import java.net.HttpURLConnection;
import java.net.URL;

public class DownloadThumbnailRunnable implements Runnable {
    private Context context;
    private ImageView imageView;
    private String strUrl;

    public DownloadThumbnailRunnable(Context context, ImageView imageView, String strUrl) {
        this.context = context;
        this.imageView = imageView;
        this.strUrl = strUrl;
    }

    @Override
    public void run() {
        try {
            URL url = new URL(strUrl);
            HttpURLConnection connection = (HttpURLConnection) url.openConnection();
            connection.setConnectTimeout(60000);
            final Bitmap bmp= BitmapFactory.decodeStream( connection.getInputStream());
            MainActivity.instence.sauceNaoMain.hashMap.put(strUrl,bmp);
            ((Activity)context).runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    imageView.setImageBitmap(bmp);
                }
            });
        }catch (Exception e){

        }
    }
}
