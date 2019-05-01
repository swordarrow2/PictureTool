package com.meng.picTools.pixivGifDownloader;

import android.content.*;
import android.graphics.*;
import android.net.*;
import com.meng.picTools.*;
import com.meng.picTools.mengViews.*;
import com.waynejo.androidndkgif.*;
import java.io.*;
import java.util.*;

public class CreateGifThread extends Thread{
    private Context context;
    private String picsFolder = "";
    public boolean isCreated = false;
    private int nowFile = 0;
    public String gifName;
    private int allFrameFile = 0;
	MengProgressBar mpb;

    public CreateGifThread(Context context, MengProgressBar mm, String folder){
        picsFolder=folder;
		picsFolder+=File.separator;
		mpb=mm;
        this.context=context;
        gifName=folder.substring(folder.lastIndexOf("/")+1,folder.length()).replace("/","");
	  }

    public String getGifName(){
        return gifName;
	  }

    public int getNowFile(){
        return nowFile;
	  }

    public int getAllFrameFile(){
        return allFrameFile;
	  }

    @Override
    public void run(){
        if(MainActivity.instence.sharedPreference.getBoolean(Data.preferenceKeys.useJava)){
            createGifJava(picsFolder,gifName);
		  }else{
            createGifNative(picsFolder,gifName);
		  }
        isCreated=true;

	  }

    private void createGifJava(String folder,String file_name){
		List<DynamicPicJavaBean.Body.Frames> lf=mpb.pijb.dynamicPicJavaBean.body.frames;

        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        AnimatedGifEncoder localAnimatedGifEncoder = new AnimatedGifEncoder();
        localAnimatedGifEncoder.start(baos);//start
        localAnimatedGifEncoder.setRepeat(0);//设置生成gif的开始播放时间。0为立即开始播放


        allFrameFile=lf.size();

        for(int i = 0; i<allFrameFile; i++){
			localAnimatedGifEncoder.setDelay(Integer.parseInt(lf.get(i).delay));
            localAnimatedGifEncoder.addFrame(BitmapFactory.decodeFile(folder+lf.get(i).file));
            nowFile=i;
		  }
        localAnimatedGifEncoder.finish();
        String path = MainActivity.instence.getGifPath(file_name);
        try{
            FileOutputStream fos = new FileOutputStream(path);
            baos.writeTo(fos);
            baos.flush();
            fos.flush();
            baos.close();
            fos.close();
		  }catch(IOException e){
            e.printStackTrace();
		  }
        registImage(path);
	  }

    private void createGifNative(String folder,String file_name){
        String filePath = MainActivity.instence.getGifPath(file_name);
        List<DynamicPicJavaBean.Body.Frames> lf=mpb.pijb.dynamicPicJavaBean.body.frames;
        Bitmap bmp = BitmapFactory.decodeFile(folder+lf.get(0).file);
        GifEncoder gifEncoder = new GifEncoder();
        gifEncoder.setDither(false);
		try{
			gifEncoder.init(bmp.getWidth(),bmp.getHeight(),filePath,GifEncoder.EncodingType.ENCODING_TYPE_NORMAL_LOW_MEMORY);
		  }catch(FileNotFoundException e){}
        allFrameFile=lf.size();
		for(int i = 0; i<allFrameFile; i++){  
			gifEncoder.encodeFrame(BitmapFactory.decodeFile(folder+lf.get(i).file),Integer.parseInt(lf.get(i).delay));
			nowFile=i;
            nowFile=i;
		  }
        gifEncoder.close();
        registImage(filePath);
	  }

    private void registImage(String path){
        context.sendBroadcast(new Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE,Uri.parse("file://"+path)));
	  }
  }
