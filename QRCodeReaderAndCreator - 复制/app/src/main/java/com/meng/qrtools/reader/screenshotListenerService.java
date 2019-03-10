package com.meng.qrtools.reader;

import android.app.AlertDialog;
import android.app.Service;
import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.IBinder;
import android.view.WindowManager;

import com.google.zxing.Result;
import com.meng.qrtools.MainActivity;
import com.meng.qrtools.lib.qrcodelib.QrUtils;
import com.meng.qrtools.lib.screenshotListener;
import com.meng.qrtools.log;

import java.io.File;

/**
 * Created by Administrator on 2018/8/24.
 */

public class screenshotListenerService extends Service{
    private screenshotListener manager;
    private AlertDialog dialog=null;

    @Override
    public void onCreate(){
        super.onCreate();

    }

    @Override
    public int onStartCommand(Intent intent,int flags,int startId){

        manager=screenshotListener.newInstance(this);
        log.i("监听器");
        manager.setListener(new screenshotListener.OnScreenShotListener(){
            @Override
            public void onShot(final String imagePath){
                // TODO: Implement this method
                log.i("文件改变"+imagePath);
                try{
                    Thread.sleep(1000);
                }catch(InterruptedException e){
                }
                if(dialog==null){
                    Result result=QrUtils.decodeImage(imagePath);
                    if(result!=null){
                        final String resultString=result.getText();
                        MainActivity.instence.doVibrate(200L);
                        dialog=new AlertDialog.Builder(screenshotListenerService.this)
                                .setTitle("类型:"+result.getBarcodeFormat().toString()).setMessage(resultString)
                                .setPositiveButton("复制文本到剪贴板",new DialogInterface.OnClickListener(){

                                    @Override
                                    public void onClick(DialogInterface p1,int p2){
                                        ClipboardManager clipboardManager=(ClipboardManager)getSystemService(Context.CLIPBOARD_SERVICE);
                                        ClipData clipData=ClipData.newPlainText("text",resultString);
                                        clipboardManager.setPrimaryClip(clipData);
                                    }
                                })
                                .setNegativeButton("确定",null).create();
                        dialog.getWindow().setType(WindowManager.LayoutParams.TYPE_SYSTEM_ALERT);
                        dialog.show();
                    }else{
                        AlertDialog dialog=new AlertDialog.Builder(screenshotListenerService.this)
                                .setTitle("提示").setMessage("此图片无法识别")
                                .setPositiveButton("确定",null).create();
                        dialog.getWindow().setType(WindowManager.LayoutParams.TYPE_SYSTEM_ALERT);
                        dialog.show();
                    }
                }
                dialog.setOnDismissListener(new DialogInterface.OnDismissListener(){
                    @Override
                    public void onDismiss(DialogInterface d){
                        deleteDialog(imagePath);
						dialog.setOnDismissListener(null);
                        dialog=null;
                    }
                });
            }
        });
        manager.startListen();
        log.i("开始监听");
        return super.onStartCommand(intent,flags,startId);
    }

    @Override
    public void onDestroy(){
        manager.stopListen();
        log.i("停止监听");
        super.onDestroy();
    }

    @Override
    public IBinder onBind(Intent intent){
        return null;
    }

    private void deleteDialog(final String path){
        AlertDialog dialog2=new AlertDialog.Builder(screenshotListenerService.this)
                .setMessage("是否删除此屏幕截图？")
                .setPositiveButton("是",new DialogInterface.OnClickListener(){

                    @Override
                    public void onClick(DialogInterface p1,int p2){
                        new File(path).delete();
                    }
                })
                .setNegativeButton("否",null).create();
        dialog2.getWindow().setType(WindowManager.LayoutParams.TYPE_SYSTEM_ALERT);
        dialog2.show();
    }
}
