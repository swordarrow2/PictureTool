package com.meng.qrtools.lib;

import android.graphics.*;
import android.util.*;
import java.util.*;


public class MyRandom{

    public Random random = new Random(9961);
    private HashSet<Integer> hashSet = new HashSet<Integer>();
    private int flag = 1;
	private int num = 0;
    public MyRandom(int b){
        flag=b;
	  }

    public int next(){
		while(true){
			num=random.nextInt(flag);
            if(!hashSet.contains(num)){
                hashSet.add(num);
				break;
			  }  
		  }
		return num; 
	  }
	
	public void cle(){
		hashSet.clear();
	  }

  }
