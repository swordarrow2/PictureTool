package com.meng.qrtools;
import android.widget.*;
import com.meng.*;

public class log
{

	public static void e(Object o){
		MainActivity2.logString=MainActivity2.logString+o.toString()+"\n";
	}

	public static void c(Object o){
		MainActivity2.logString=MainActivity2.logString+"点击:"+o.toString()+"\n";
	}
	public static void i(Object o){
		MainActivity2.logString=MainActivity2.logString+o.toString()+"\n";
	}
	public static void t(Object o){
		Toast.makeText(MainActivity2.instence,o.toString(),Toast.LENGTH_SHORT).show();
	}
}
