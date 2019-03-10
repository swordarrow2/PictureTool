package com.meng.pixivGifDownloader;
import java.util.*;
import com.meng.pixivGifDownloader.zipJavaBean.*;

public class zipJavaBean{
	public String error;
	public String message;
	public Body body;

	public class Body{

		public String src;
		public String originalSrc;
		public String mime_type;
		public List<Frames> frames;

		public class Frames{
			public String file;
			public String delay;
		  }
	  }
  }
