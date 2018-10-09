package com.meng.bilibiliDanmakuSender.creator;

import android.app.FragmentTransaction;
import android.os.*;
import android.view.*;
import android.widget.*;

import com.meng.bilibiliDanmakuSender.MainActivity;
import com.meng.bilibiliDanmakuSender.MainActivity2;
import com.meng.bilibiliDanmakuSender.mengEdittext;

import android.app.Fragment;
import com.meng.bilibiliDanmakuSender.R;

/**
 * Created by Administrator on 2018/7/19.
 */

public class FirstStep extends Fragment{

    
    private mengEdittext mengEtLink;
    private Button btnStart;
    

    @Override
    public View onCreateView(LayoutInflater inflater,ViewGroup container,Bundle savedInstanceState){
        // TODO: Implement this method
        return inflater.inflate(R.layout.first_step,container,false);
    }

    @Override
    public void onViewCreated(View view,Bundle savedInstanceState){
        // TODO: Implement this method
        super.onViewCreated(view,savedInstanceState);
        mengEtLink=(mengEdittext)view.findViewById(R.id.first_step_mengEditText_link);
        btnStart=(Button)view.findViewById(R.id.first_step_button_start);
        btnStart.setOnClickListener(click);
    }

    View.OnClickListener click=new View.OnClickListener(){
        @Override
        public void onClick(View v){
            switch(v.getId()){
                case R.id.first_step_mengEditText_link:

                    break;
                case R.id.first_step_button_start:
                    MainActivity.avLink=mengEtLink.getString();
                    FragmentTransaction transaction=getActivity().getFragmentManager().beginTransaction();
                    MainActivity2.instence.secondStepFragment.setDataStr(mengEtLink.getString());
                    transaction.hide(MainActivity2.instence.firstStepFragment);
                    transaction.show(MainActivity2.instence.secondStepFragment);
                    transaction.commit();
					break;
            }
        }
    };

}
