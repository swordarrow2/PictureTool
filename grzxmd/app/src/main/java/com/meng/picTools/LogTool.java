package com.meng.picTools;

import android.widget.*;
import com.meng.picTools.activity.*;
import java.text.*;

public class LogTool {

    public static void e(final Object o){
        if(o instanceof Exception){
            ((Exception)o).printStackTrace();
        }
        MainActivity.instence.runOnUiThread(new Runnable(){

            @Override
            public void run(){
                Toast.makeText(MainActivity.instence,"发生错误:"+o.toString(),Toast.LENGTH_SHORT).show();
                i("发生错误:"+o.toString());
            }
        });
    }


    public static void i(final Object o){
        MainActivity.instence.runOnUiThread(new Runnable(){

            @Override
            public void run(){
                MainActivity.instence.rightText.setText(
				  MessageFormat.format("{0}\n{1}", o.toString(),MainActivity.instence.rightText.getText().toString())
                );
            }
        });
    }

    public static void t(final Object o){
        MainActivity.instence.runOnUiThread(new Runnable(){

            @Override
            public void run(){
                Toast.makeText(MainActivity.instence,o.toString(),Toast.LENGTH_SHORT).show();
                i(o.toString());
            }
        });
    }
}
