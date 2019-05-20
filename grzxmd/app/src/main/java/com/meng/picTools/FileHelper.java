package com.meng.picTools;

import android.graphics.*;
import android.os.*;
import com.meng.picTools.lib.*;
import java.io.*;
import java.util.*;

public class FileHelper {
	private static final String exDir = Environment.getExternalStorageDirectory().getAbsolutePath() + "/Pictures/picTool/";

	public static File getFolder(FileType type) {
		File f=new File(exDir + type.toString());
		if (!f.exists())f.mkdirs();
		return f;
	  }
	  
	  public static String getFileAbsPath(String name,FileType t){
		  return exDir + t.toString()+"/"+name;
	  }
	  
	public static String getPreDownloadJsonPath() {
        return exDir + "/pixivLike.json";
	  }
	
	public static String saveBitmap(Bitmap bmp, FileType t) {
		File f = new File(getFolder(t).getAbsolutePath()+(SharedPreferenceHelper.getBoolean("useTimeStamp") ? String.valueOf(System.currentTimeMillis() / 1000) : new Date().toString())+".png");
        try {
			f.createNewFile();
			FileOutputStream fOut = null;
			fOut = new FileOutputStream(f);
			bmp.compress(Bitmap.CompressFormat.PNG, 100, fOut);
			fOut.flush();
			fOut.close();
		  } catch (IOException e) {
			return null;
		  }
        return f.getAbsolutePath();
	  }

  }
