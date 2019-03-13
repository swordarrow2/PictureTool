package com.meng.picTools.pixivGifDownloader;
import java.util.*;

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
