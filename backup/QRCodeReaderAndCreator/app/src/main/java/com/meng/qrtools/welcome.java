package com.meng.qrtools;

import android.app.*;
import android.os.*;
import android.view.*;
import android.widget.*;

/**
 * Created by Administrator on 2018/7/19.
 */

public class welcome extends Fragment {
    TextView tv;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        // TODO: Implement this method
        return inflater.inflate(R.layout.app_main, container, false);
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        // TODO: Implement this method
        super.onViewCreated(view, savedInstanceState);
        tv = (TextView) view.findViewById(R.id.app_mainTextView);
        tv.setText("选择想要使用的功能吧");
    }

}
