package com.meng.picTools.qrtools;

import android.widget.Toast;

import com.meng.picTools.MainActivity2;

import java.text.MessageFormat;

public class LogTool {

    public static void e(final Object o){
        if(o instanceof Exception){
            ((Exception)o).printStackTrace();
        }
        MainActivity2.instence.runOnUiThread(new Runnable(){

            @Override
            public void run(){
                Toast.makeText(MainActivity2.instence,"发生错误:"+o.toString(),Toast.LENGTH_SHORT).show();
                i("发生错误:"+o.toString());
            }
        });
    }


    public static void i(final Object o){
        MainActivity2.instence.runOnUiThread(new Runnable(){

            @Override
            public void run(){
                MainActivity2.instence.rightText.setText(
				  MessageFormat.format("{0}\n{1}", o.toString(),MainActivity2.instence.rightText.getText().toString())
                );
            }
        });
    }

    public static void t(final Object o){
        MainActivity2.instence.runOnUiThread(new Runnable(){

            @Override
            public void run(){
                Toast.makeText(MainActivity2.instence,o.toString(),Toast.LENGTH_SHORT).show();
                i(o.toString());
            }
        });
    }
}
