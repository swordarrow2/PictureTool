package com.meng.bilibiliDanmakuSender;

import android.widget.Toast;

import com.meng.bilibiliDanmakuSender.MainActivity2;

public class log{

    public static void e(final Object o){
        if(o instanceof Exception){
            ((Exception)o).printStackTrace();
        }
        MainActivity2.instence.runOnUiThread(new Runnable(){

            @Override
            public void run(){
                // TODO: Implement this method
                Toast.makeText(MainActivity2.instence,"发生错误:"+o.toString(),Toast.LENGTH_SHORT).show();
                i("发生错误:"+o.toString());
            }
        });
    }


    public static void i(final Object o){
        MainActivity2.instence.runOnUiThread(new Runnable(){

            @Override
            public void run(){
                // TODO: Implement this method
                MainActivity2.instence.rightText.setText(
                        MainActivity2.instence.rightText.getText().toString()+
                                o.toString()+"\n"
                );
            }
        });
    }

    public static void t(final Object o){
        MainActivity2.instence.runOnUiThread(new Runnable(){

            @Override
            public void run(){
                // TODO: Implement this method
                Toast.makeText(MainActivity2.instence,o.toString(),Toast.LENGTH_SHORT).show();
                i(o.toString());
            }
        });
    }
}
