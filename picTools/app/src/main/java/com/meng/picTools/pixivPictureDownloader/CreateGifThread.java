package com.meng.picTools.pixivPictureDownloader;

import android.content.*;
import android.graphics.*;
import android.net.*;

import com.meng.picTools.*;
import com.meng.picTools.javaBean.AnimPicJavaBean;
import com.meng.picTools.mengViews.*;
import com.meng.picTools.qrtools.lib.SharedPreferenceHelper;
import com.waynejo.androidndkgif.*;

import java.io.*;
import java.util.*;

public class CreateGifThread extends Thread {
    private Context context;
    private String picsFolder = "";
    public boolean isCreated = false;
    private int nowFile = 0;
    public String gifName;
    private MengProgressBar mengProgressBar;

    public CreateGifThread(Context context, MengProgressBar mm, String folder) {
        picsFolder = folder;
        picsFolder += File.separator;
        mengProgressBar = mm;
        this.context = context;
        gifName = folder.substring(folder.lastIndexOf("/") + 1, folder.length()).replace("/", "");
    }

    public String getGifName() {
        return gifName;
    }

    public int getNowFile() {
        return nowFile;
    }

    public int getFilesCount() {
        return mengProgressBar.pictureInfoJavaBean.animPicJavaBean.body.frames.size();
    }

    @Override
    public void run() {
        if (SharedPreferenceHelper.getBoolean(Data.preferenceKeys.useJava)) {
            createGifJava(picsFolder, gifName);
        } else {
            createGifNative(picsFolder, gifName);
        }
        isCreated = true;
    }

    private void createGifJava(String folder, String file_name) {
        List<AnimPicJavaBean.Body.Frames> lf = mengProgressBar.pictureInfoJavaBean.animPicJavaBean.body.frames;

        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        AnimatedGifEncoder localAnimatedGifEncoder = new AnimatedGifEncoder();
        localAnimatedGifEncoder.start(baos);//start
        localAnimatedGifEncoder.setRepeat(0);//设置生成gif的开始播放时间。0为立即开始播放

        for (int i = 0; i < lf.size(); i++) {
            localAnimatedGifEncoder.setDelay(Integer.parseInt(lf.get(i).delay));
            localAnimatedGifEncoder.addFrame(BitmapFactory.decodeFile(folder + lf.get(i).file));
            nowFile = i;
        }
        localAnimatedGifEncoder.finish();
        String path = MainActivity.instence.getGifPath(file_name);
        try {
            FileOutputStream fos = new FileOutputStream(path);
            baos.writeTo(fos);
            baos.flush();
            fos.flush();
            baos.close();
            fos.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
        registImage(path);
    }

    private void createGifNative(String folder, String file_name) {
        String filePath = MainActivity.instence.getGifPath(file_name);
        List<AnimPicJavaBean.Body.Frames> lf = mengProgressBar.pictureInfoJavaBean.animPicJavaBean.body.frames;
        Bitmap bmp = BitmapFactory.decodeFile(folder + lf.get(0).file);
        GifEncoder gifEncoder = new GifEncoder();
        gifEncoder.setDither(false);
        try {
            gifEncoder.init(bmp.getWidth(), bmp.getHeight(), filePath, GifEncoder.EncodingType.ENCODING_TYPE_NORMAL_LOW_MEMORY);
        } catch (FileNotFoundException e) {
        }
        for (int i = 0; i < lf.size(); i++) {
            gifEncoder.encodeFrame(BitmapFactory.decodeFile(folder + lf.get(i).file), Integer.parseInt(lf.get(i).delay));
            nowFile = i;
        }
        gifEncoder.close();
        registImage(filePath);
    }

    private void registImage(String path) {
        context.sendBroadcast(new Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE, Uri.parse("file://" + path)));
    }
}
