package com.meng.qrtools;

import android.app.Fragment;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

/**
 * Created by Administrator on 2018/7/19.
 */

public class textFragment extends Fragment {

    private TextView tv;
    private int flag=0;
    public textFragment(int i) {
    flag=i;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        // TODO: Implement this method
        return inflater.inflate(R.layout.text_fragment, container, false);
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        // TODO: Implement this method
        super.onViewCreated(view, savedInstanceState);
        tv = (TextView) view.findViewById(R.id.aboutTextView);
        switch (flag) {
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

    private void about() {
        String[] s = new String[]{
                //	"欢迎使用QRtools\n",
                "    这是一个可以对二维码进行读取内容和生成二维码的软件。\n",
                "    可以从相机扫描条形码与二维码也可以从相册中选择已有的图片进行识别。\n",
                "    可以扫描的二维码类型:QRcode，DataMatrix，Aztec，PDF417。\n",
                "    可以生成普通的QR二维码,带有logo的QR二维码以及AwesomeQR码。\n",
                "    使用的开源项目:\n",
                "    https://github.com/keklikhasan/LDrawer/\n",
                "    https://github.com/SumiMakito/AwesomeQRCode/\n",
                "    https://github.com/XuDaojie/QRCode-Android/\n",
                "    https://github.com/vivian8725118/ZXingGenerator/\n",
                "    https://github.com/zxing/zxing/"
        };
        String temp = "";
        for (String ss : s) {
            temp += ss;
        }
        tv.setText(temp);
    }
}
