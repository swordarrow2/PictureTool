package com.meng.picTools.pixivGifDownloader;

import android.content.*;
import android.net.*;
import android.provider.*;
import com.meng.picTools.*;
import com.meng.picTools.pixivGifDownloader.*;
import com.waynejo.androidndkgif.*;
import java.io.*;
import android.graphics.*;
import java.util.*;
import com.meng.picTools.qrtools.*;

public class createGif extends Thread{
  int h;
	int w;
	int d;
	int q;
	String pid="";
	Context c;
	private String picsFolder="";
	
	public createGif(Context co,String folder,String pid,int h,int w,int d,int q){
		this.h=h;
		this.w=w;
		this.d=d;
		this.q=q;
		this.pid=pid;
		picsFolder=folder;
		c=co;
	  }

	@Override
	public void run(){
		if(MainActivity.instence.sharedPreference.getBoolean(Data.preferenceKeys.useJava)){
			createGifJava(picsFolder,pid,d);
		  }else{
			createGifNative(picsFolder,pid,w,h,d,q);
		  }

	  }
	private void createGifJava(String folder, String file_name,int delay){
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        AnimatedGifEncoder localAnimatedGifEncoder = new AnimatedGifEncoder();
        localAnimatedGifEncoder.start(baos);//start
        localAnimatedGifEncoder.setRepeat(0);//设置生成gif的开始播放时间。0为立即开始播放
        localAnimatedGifEncoder.setDelay(delay);
		String[] images=new File(folder).list();
        int count = images.length;
        for(int i = 0; i<count; i++){
            localAnimatedGifEncoder.addFrame(BitmapFactory.decodeFile(folder+"/"+images[i]));
            playLayout.gifProgress.setProgress(i*100/count);
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

    private void createGifNative(String folder, String file_name,int w,int h,int d,int q){
        String filePath = MainActivity.instence.getGifPath(file_name);
        GifEncoder gifEncoder = new GifEncoder();
        gifEncoder.setDither(false);
        try{
            gifEncoder.init(w,h,filePath,GifEncoder.EncodingType.ENCODING_TYPE_NORMAL_LOW_MEMORY);
		  }catch(FileNotFoundException e){
            e.printStackTrace();
            return;
		  }
		String[] images=new File(folder).list();
		Arrays.sort(images);
	//	log.t(filePath+" "+file_name+" "+images.length+" "+folder);
		for(int i=0;i<images.length;i++){		
		  try{	
			  gifEncoder.encodeFrame(BitmapFactory.decodeFile(folder+"/"+ images[i]),d);
		  }catch(Exception e){
			log.t(e.toString());
		  }
			playLayout.gifProgress.setProgress(i*100/images.length);

		  }
        gifEncoder.close();
		registImage(filePath);
	  }

    private void registImage(String path){
        c.sendBroadcast(new Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE,Uri.parse("file://"+path)));
	  }
  }
