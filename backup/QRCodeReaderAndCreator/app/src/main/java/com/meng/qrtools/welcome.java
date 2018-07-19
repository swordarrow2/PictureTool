package com.meng.qrtools;

import android.app.Activity;
import android.app.Fragment;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.meng.qrtools.creator.QRCode;
import android.widget.*;

/**
 * Created by Administrator on 2018/7/19.
 */

public class welcome extends Fragment {
	TextView tv;
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState){
        // TODO: Implement this method
        return inflater.inflate(R.layout.app_main, container, false);
    }

    @Override
    public void onViewCreated(View view,Bundle savedInstanceState){
        // TODO: Implement this method
        super.onViewCreated(view,savedInstanceState);
		tv=(TextView)view.findViewById(R.id.app_mainTextView);
		tv.setText("选择想要使用的功能吧");
    }
	
}
