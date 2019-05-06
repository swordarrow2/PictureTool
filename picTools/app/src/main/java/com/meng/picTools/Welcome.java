package com.meng.picTools;

import android.app.Fragment;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.meng.picTools.R;

public class Welcome extends Fragment{

    private TextView tv;
    public Welcome(){
    }

    @Override
    public View onCreateView(LayoutInflater inflater,ViewGroup container,Bundle savedInstanceState){
        return inflater.inflate(R.layout.text_fragment,container,false);
    }

    @Override
    public void onViewCreated(View view,Bundle savedInstanceState){
        super.onViewCreated(view,savedInstanceState);
        tv=(TextView)view.findViewById(R.id.aboutTextView);
                welcome();
    }

    private void welcome(){
        tv.setText("选择想要使用的功能吧");
    }

}
