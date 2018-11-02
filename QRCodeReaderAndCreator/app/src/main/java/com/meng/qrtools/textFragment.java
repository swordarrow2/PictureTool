package com.meng.qrtools;

import android.app.Fragment;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

public class textFragment extends Fragment{

    private TextView tv;
    private int flag=0;

    public textFragment(int i){
        flag=i;
    }

    public textFragment(){
    }

    @Override
    public View onCreateView(LayoutInflater inflater,ViewGroup container,Bundle savedInstanceState){
        return inflater.inflate(R.layout.text_fragment,container,false);
    }

    @Override
    public void onViewCreated(View view,Bundle savedInstanceState){
        super.onViewCreated(view,savedInstanceState);
        tv=(TextView)view.findViewById(R.id.aboutTextView);
        switch(flag){
            case 0:
                welcome();
                break;
            case 1:
                about();
                break;
        }
    }

    private void welcome(){
        tv.setText("选择想要使用的功能吧");
    }

    private void about(){
        String[] s=new String[]{
                "    可以扫描的二维码类型:QRcode，DataMatrix，Aztec，PDF417。\n",
                "    可以生成QRcode，DataMatrix，Aztec，PDF417以及AwesomeQR。\n",
                "    可以将AwesomeQR码添加到GIF图片中。\n",
                "    带有简单的GIF生成功能\n",
                "    使用的开源项目:\n",
                "    https://github.com/keklikhasan/LDrawer/\n",
                "    https://github.com/SumiMakito/AwesomeQRCode/\n",
                "    https://github.com/XuDaojie/QRCode-Android/\n",
                "    https://github.com/vivian8725118/ZXingGenerator/\n",
                "    https://github.com/zxing/zxing/\n",
                "    https://github.com/waynejo/android-ndk-gif\n"
        };
        String temp="";
        for(String ss : s){
            temp+=ss;
        }
        tv.setText(temp);
    }
}
