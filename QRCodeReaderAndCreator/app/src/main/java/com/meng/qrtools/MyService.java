package com.meng.qrtools;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.FragmentTransaction;
import android.app.Service;
import android.content.ClipData;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.IBinder;
import android.os.Vibrator;
import android.text.TextUtils;
import android.widget.Toast;

import com.google.zxing.Result;
import com.meng.MainActivity2;
import com.meng.qrtools.lib.qrcodelib.QrUtils;
import com.meng.qrtools.lib.screenshotListener;

/**
 * Created by Administrator on 2018/8/24.
 */

public class MyService extends Service{

    public static final String TAG="MyService";

    private screenshotListener manager;
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
            public void onShot(String imagePath){
                // TODO: Implement this method
                log.i("文件改变"+imagePath);
                try{
                    Thread.sleep(2000);
                }catch(InterruptedException e){}
                if(!TextUtils.isEmpty(imagePath)){
                    Result result=QrUtils.decodeImage(imagePath);
                    if(result!=null){
                        final String resultString=result.getText();
                        playBeepSoundAndVibrate(200);
                        new AlertDialog.Builder(MyService.this)
                                .setMessage(resultString)
                                .setPositiveButton("确定",null)
                                .setNeutralButton("复制文本",new DialogInterface.OnClickListener(){

                                    @Override
                                    public void onClick(DialogInterface p1,int p2){
                                        // TODO: Implement this method
                                        android.content.ClipboardManager clipboardManager=(android.content.ClipboardManager)getSystemService(Context.CLIPBOARD_SERVICE);
                                        ClipData clipData=ClipData.newPlainText("text",resultString);
                                        clipboardManager.setPrimaryClip(clipData);
                                    }
                                })
                         /*       .setNegativeButton("生成AwesomeQR",new DialogInterface.OnClickListener(){

                                    @Override
                                    public void onClick(DialogInterface p1,int p2){
                                        // TODO: Implement this method
                                        FragmentTransaction transaction=getActivity().getFragmentManager().beginTransaction();
                                        MainActivity2.instence.awesomeCreatorFragment.setDataStr(resultString);
                                        transaction.hide(MainActivity2.instence.cameraReaderFragment);
                                        transaction.show(MainActivity2.instence.awesomeCreatorFragment);
                                        transaction.commit();
                                    }
                                }) */
                                .create().show();
                    }else{
                        new AlertDialog.Builder(MyService.this)
                                .setTitle("提示")
                                .setMessage("此图片无法识别")
                                .setPositiveButton("确定",null)
                                .show();
                    }
                }
            }
        });
        manager.startListen();
        log.i("开始监听");
        return super.onStartCommand(intent,flags,startId);
    }

    @Override
    public void onDestroy(){
        manager.stopListen();
        super.onDestroy();
    }

    @Override
    public IBinder onBind(Intent intent){
        return null;
    }
    private void playBeepSoundAndVibrate(long ms){
        Vibrator vibrator=(Vibrator)getSystemService(Activity.VIBRATOR_SERVICE);
        vibrator.vibrate(ms);
    }
}
