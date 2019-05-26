package com.meng.picTools.ocr;

import java.util.ArrayList;

import com.google.gson.annotations.SerializedName;

public class OcrJavaBean {

	public String session_id;
	public float angle;
	public ArrayList<Items> items;
	@SerializedName("class")
	public ArrayList<String> classs;
	public ArrayList<String> recognize_warn_msg;
	public int errorcode;
	public String errormsg;
	public ArrayList<String> recognize_warn_code;

	public class Items {
		public ArrayList<String> candword;
		public float itemconf;
		public String itemstring;

		public class Itemcoord {
			public int x;
			public int width;
			public int y;
			public int height;
		}

		public Itemcoord itemcoord;

		public class Coordpoint {
			public ArrayList<Integer> x;
		}

		public Coordpoint coordpoint;

		public class Words {
			public String character;
			public double confidence;
		}

		public ArrayList<Words> words;
		public ArrayList<Integer> wordcoordpoint;
		public ArrayList<Integer> coords;

		public class Parag {
			public int parag_no;
			public int word_size;
		}
	}
}
