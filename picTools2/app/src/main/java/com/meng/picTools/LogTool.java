package com.meng.picTools;

import android.graphics.Color;
import android.support.design.widget.Snackbar;
import android.view.View;
import android.widget.*;

import java.text.*;

public class LogTool {

    public static void e(final Object o) {
        if (o instanceof Exception) {
            ((Exception) o).printStackTrace();
        }
        MainActivity2.instence.runOnUiThread(new Runnable() {

            @Override
            public void run() {
                Snackbar snackbar = Snackbar.make(MainActivity2.instence.mainLinearLayout, "发生错误:" + o.toString(), Snackbar.LENGTH_LONG).setAction("Action", null);
                //new View.OnClickListener() {
                //     @Override
                //     public void onClick(View view) {
                //      }
                //   });
                //   snackbar.setText("动态文本");//动态设置文本显示内容
                //    snackbar.setActionTextColor(Color.RED);//动态设置Action文本的颜色
                snackbar.setDuration(2000);//动态设置显示时间

                View snackbarView = snackbar.getView();//获取Snackbar显示的View对象
                //获取显示文本View,并设置其显示颜色
                ((TextView) snackbarView.findViewById(android.support.design.R.id.snackbar_text)).setTextColor(Color.BLACK);
                //获取Action文本View，并设置其显示颜色
                ((TextView) snackbarView.findViewById(android.support.design.R.id.snackbar_action)).setTextColor(Color.BLUE);
                //设置Snackbar的背景色
                switch (MainActivity2.instence.theme) {
                    case R.style.green:
                        snackbarView.setBackgroundColor(Color.GREEN);
                        break;
                    case R.style.red:
                        snackbarView.setBackgroundColor(Color.RED);
                        break;
                    case R.style.blue:
                        snackbarView.setBackgroundColor(Color.BLUE);
                        break;
                    case R.style.black:
                        ((TextView) snackbarView.findViewById(android.support.design.R.id.snackbar_text)).setTextColor(Color.WHITE);
                        snackbarView.setBackgroundColor(Color.BLACK);
                        break;
                    case R.style.purple:
                        snackbarView.setBackgroundColor(Color.argb(0xFF, 0xAD, 0x7B, 0xE8));
                        break;
                    default:
                        snackbarView.setBackgroundColor(Color.GREEN);
                        break;
                }
                snackbar.show();
                //设置Snackbar显示的位置
                //     ViewGroup.LayoutParams params = snackbarView.getLayoutParams();
                //      CoordinatorLayout.LayoutParams layoutParams = new CoordinatorLayout.LayoutParams(params.width, params.height);
                //      layoutParams.gravity = Gravity.CENTER_VERTICAL;//垂直居中
                //      snackbarView.setLayoutParams(layoutParams);
                //   Toast.makeText(MainActivity2.instence,"发生错误:"+o.toString(),Toast.LENGTH_SHORT).show();
                i("发生错误:" + o.toString());
            }
        });
    }


    public static void i(final Object o) {
        MainActivity2.instence.runOnUiThread(new Runnable() {

            @Override
            public void run() {
                MainActivity2.instence.rightText.setText(
                        MessageFormat.format("{0}\n{1}", o.toString(), MainActivity2.instence.rightText.getText().toString())
                );
            }
        });
    }

    public static void t(final Object o) {
        MainActivity2.instence.runOnUiThread(new Runnable() {

            @Override
            public void run() {
                Snackbar snackbar = Snackbar.make(MainActivity2.instence.mainLinearLayout, o.toString(), Snackbar.LENGTH_LONG).setAction("Action", null);
                snackbar.setDuration(5000);
                View snackbarView = snackbar.getView();
                ((TextView) snackbarView.findViewById(android.support.design.R.id.snackbar_text)).setTextColor(Color.BLACK);
                ((TextView) snackbarView.findViewById(android.support.design.R.id.snackbar_action)).setTextColor(Color.BLUE);
                switch (MainActivity2.instence.theme) {
                    case R.style.green:
                        snackbarView.setBackgroundColor(Color.GREEN);
                        break;
                    case R.style.red:
                        snackbarView.setBackgroundColor(Color.RED);
                        break;
                    case R.style.blue:
                        snackbarView.setBackgroundColor(Color.BLUE);
                        break;
                    case R.style.black:
                        ((TextView) snackbarView.findViewById(android.support.design.R.id.snackbar_text)).setTextColor(Color.WHITE);
                        snackbarView.setBackgroundColor(Color.BLACK);
                        break;
                    case R.style.purple:
                        snackbarView.setBackgroundColor(Color.argb(0xFF, 0xAD, 0x7B, 0xE8));
                        break;
                    default:
                        snackbarView.setBackgroundColor(Color.GREEN);
                        break;
                }
                snackbar.show();
                //          Toast.makeText(MainActivity2.instence,o.toString(),Toast.LENGTH_SHORT).show();
                i(o.toString());
            }
        });
    }
}
