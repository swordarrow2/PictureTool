package com.meng.qrtools.lib.qrcodelib;

import android.graphics.*;
import com.meng.qrtools.lib.*;
import java.io.*;


public class QrUtils{

	public static Bitmap encryBitmap(Bitmap bitmap){

		int[] tmp=new int[bitmap.getWidth()*bitmap.getHeight()];
		bitmap.getPixels(tmp,0,bitmap.getWidth(),0,0,bitmap.getWidth(),bitmap.getHeight());
		int[][] bitPix=convert(tmp,bitmap.getWidth());
		MyRandom myRandom=new MyRandom(bitmap.getWidth());	
		int[][] bitPix2=new int[bitmap.getWidth()][bitmap.getHeight()];
		for(int y=0;y<bitmap.getHeight();y++){	  
			myRandom.random.setSeed(y);
			for(int x=0;x<bitmap.getWidth();x++){
				bitPix2[x][y]=bitPix[myRandom.next()][y];
			  }
			myRandom.clean();  
		  }	  
		Bitmap encryedBitmap=Bitmap.createBitmap(bitmap.getWidth(),bitmap.getHeight(),Bitmap.Config.ARGB_8888);
		encryedBitmap.setPixels(convert(bitPix2,bitmap.getWidth(),bitmap.getHeight()),0,bitmap.getWidth(),0,0,bitmap.getWidth(),bitmap.getHeight());
		return encryedBitmap;
	  }

	public static Bitmap decryBitmap(String path){
		Bitmap bitmap = BitmapFactory.decodeFile(path);
        if(bitmap==null) return null;
		return decryBitmap(bitmap);
	  }

    public static Bitmap decryBitmap(Bitmap bitmap){
		MyRandom myRandom=new MyRandom(bitmap.getWidth());
		int[] tmp=new int[bitmap.getWidth()*bitmap.getHeight()];
		bitmap.getPixels(tmp,0,bitmap.getWidth(),0,0,bitmap.getWidth(),bitmap.getHeight());
		int[][] bitPix=convert(tmp,bitmap.getWidth());	
		int[][] bitPix2=new int[bitmap.getWidth()][bitmap.getHeight()];
		for(int y=0;y<bitmap.getHeight();y++){	
			myRandom.random.setSeed(y);
			for(int x=0;x<bitmap.getWidth();x++){
				bitPix2[myRandom.next()][y]=bitPix[x][y];
			  }
			myRandom.clean();
		  }	  
		Bitmap decryedBitmap=Bitmap.createBitmap(bitmap.getWidth(),bitmap.getHeight(),Bitmap.Config.ARGB_8888);
		decryedBitmap.setPixels(convert(bitPix2,bitmap.getWidth(),bitmap.getHeight()),0,bitmap.getWidth(),0,0,bitmap.getWidth(),bitmap.getHeight());
		return decryedBitmap;
	  }

    public static String saveMyBitmap(String bitName,Bitmap mBitmap) throws IOException{
        File f = new File(bitName);
        if(!f.getParentFile().exists()){
            f.getParentFile().mkdirs();
		  }
        f.createNewFile();
        FileOutputStream fOut = null;
        fOut=new FileOutputStream(f);
        mBitmap.compress(Bitmap.CompressFormat.PNG,100,fOut);
        fOut.flush();
        fOut.close();
        return f.getAbsolutePath();
	  }

	public static int[][] convert(int [] bitPixs,int width){
		int height=bitPixs.length/width;
		int [][] pixels=new int[width] [height];
		int flag=0;
		for(int y=0;y<height;y++){
			for(int x=0;x<width;x++){
				pixels[x][y]=bitPixs[flag];
				++flag;
			  }
		  }
		return pixels;
	  }

	public static int[] convert(int[][] bitPixs,int width,int height){
		int [] pixels=new int[width*height];
		int flag=0;
		for(int y=0;y<height;y++){
			for(int x=0;x<width;x++){
				pixels[flag]=bitPixs[x][y];
				++flag;
			  }
		  }
		return pixels;
	  }

  }
