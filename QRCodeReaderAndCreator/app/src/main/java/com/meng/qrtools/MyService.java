package com.meng.qrtools;

import android.app.*;
import android.content.*;
import android.os.*;
import android.view.*;
import com.google.zxing.*;
import com.meng.qrtools.lib.*;
import com.meng.qrtools.lib.qrcodelib.*;
import java.io.*;

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
				public void onShot(final String imagePath){
					// TODO: Implement this method
					log.i("文件改变"+imagePath);
					try{
						Thread.sleep(2000);
					}catch(InterruptedException e){}
					Result result=QrUtils.decodeImage(imagePath);
					if(result!=null){
						final String resultString=result.getText();
						playBeepSoundAndVibrate(200);
						AlertDialog dialog=new AlertDialog.Builder(MyService.this)
							.setTitle("类型:"+result.getBarcodeFormat().toString()).setMessage(resultString)
							.setPositiveButton("复制文本到剪贴板",new DialogInterface.OnClickListener(){

								@Override
								public void onClick(DialogInterface p1,int p2){
									// TODO: Implement this method
									ClipboardManager clipboardManager=(ClipboardManager)getSystemService(Context.CLIPBOARD_SERVICE);
									ClipData clipData=ClipData.newPlainText("text",resultString);
									clipboardManager.setPrimaryClip(clipData);
									deleteDialog(imagePath);
								}
							})
							.setNegativeButton("确定",new DialogInterface.OnClickListener(){

								@Override
								public void onClick(DialogInterface p1,int p2){
									// TODO: Implement this method
									deleteDialog(imagePath);
								}
							}).create();
						dialog.getWindow().setType(WindowManager.LayoutParams.TYPE_SYSTEM_ALERT);
						dialog. show();
					}else{
						AlertDialog dialog= new AlertDialog.Builder(MyService.this)
							.setTitle("提示").setMessage("此图片无法识别")
							.setPositiveButton("确定",new DialogInterface.OnClickListener(){

								@Override
								public void onClick(DialogInterface p1,int p2){
									// TODO: Implement this method
									deleteDialog(imagePath);
								}		
							}).create();
						dialog.getWindow().setType(WindowManager.LayoutParams.TYPE_SYSTEM_ALERT);
						dialog.show();
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
		log.i("停止监听");
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
	
	private void deleteDialog(final String path){
		AlertDialog dialog2=new AlertDialog.Builder(MyService.this)
			.setMessage("是否删除此屏幕截图？")
			.setPositiveButton("是",new DialogInterface.OnClickListener(){

				@Override
				public void onClick(DialogInterface p1,int p2){
					// TODO: Implement this method
					File f=new File(path);
					f.delete();
				}
			})
			.setNegativeButton("否",null).create();
		dialog2.getWindow().setType(WindowManager.LayoutParams.TYPE_SYSTEM_ALERT);
		dialog2. show();
	}
}
