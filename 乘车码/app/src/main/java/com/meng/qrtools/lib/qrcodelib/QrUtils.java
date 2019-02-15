package com.meng.qrtools.lib.qrcodelib;

import android.graphics.*;
import com.meng.qrtools.lib.*;
import java.io.*;


public class QrUtils{
    
	public static Bitmap encryBitmap(Bitmap bitmap){
		Bitmap encryedBitmap=Bitmap.createBitmap(bitmap.getWidth(),bitmap.getHeight(),Bitmap.Config.ARGB_8888);
		MyRandom myRandom=new MyRandom(bitmap.getWidth());
		for(int y=0;y<bitmap.getHeight();y++){	
			myRandom.random.setSeed(y);
			for(int x=0;x<bitmap.getWidth();x++){
				encryedBitmap.setPixel(x,y,bitmap.getPixel(myRandom.next(),y));
			  }
			myRandom.clean();
		  }
		return encryedBitmap;
	  }

	public static Bitmap decryBitmap(String path){
		Bitmap bitmap = BitmapFactory.decodeFile(path);
        if(bitmap==null) return null;
		return decryBitmap(bitmap);
	  }

    public static Bitmap decryBitmap(Bitmap bitmap){
        Bitmap decryedBitmap=Bitmap.createBitmap(bitmap.getWidth(),bitmap.getHeight(),Bitmap.Config.ARGB_8888);
		MyRandom myRandom=new MyRandom(bitmap.getWidth());
        for(int y=0;y<bitmap.getHeight();y++){
			myRandom.random.setSeed(y);
			for(int x=0;x<bitmap.getWidth();x++){
				decryedBitmap.setPixel(myRandom.next(),y,bitmap.getPixel(x,y));
			  }
			myRandom.clean();
		  }
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

  }
