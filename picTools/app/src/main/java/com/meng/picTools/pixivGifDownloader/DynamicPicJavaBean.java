package com.meng.picTools.pixivGifDownloader;
import java.util.*;

public class DynamicPicJavaBean {
	public String error="true";
	public String message="";
	public Body body=new Body();

	public class Body{

		public String src="";
		public String originalSrc="";
		public String mime_type="";
		public List<Frames> frames=new ArrayList<Frames>();

		public class Frames{
			public String file="";
			public String delay="";
		  }
	  }
  }
