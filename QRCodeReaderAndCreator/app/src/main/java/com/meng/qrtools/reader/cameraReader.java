package com.meng.qrtools.reader;

import android.Manifest;
import android.app.AlertDialog;
import android.app.Fragment;
import android.app.FragmentTransaction;
import android.content.ClipData;
import android.content.Context;
import android.content.DialogInterface;
import android.content.pm.PackageManager;
import android.content.res.AssetFileDescriptor;
import android.graphics.Bitmap;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.media.MediaPlayer.OnCompletionListener;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.os.Vibrator;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.SurfaceHolder;
import android.view.SurfaceHolder.Callback;
import android.view.SurfaceView;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.Toast;

import com.google.zxing.BarcodeFormat;
import com.google.zxing.Result;
import com.meng.MainActivity2;
import com.meng.qrtools.R;
import com.meng.qrtools.lib.qrcodelib.zxing.camera.CameraManager;
import com.meng.qrtools.lib.qrcodelib.zxing.decoding.CaptureActivityHandler;
import com.meng.qrtools.lib.qrcodelib.zxing.decoding.InactivityTimer;
import com.meng.qrtools.lib.qrcodelib.zxing.view.ViewfinderView;
import com.meng.qrtools.log;

import java.io.IOException;
import java.util.Vector;

/**
 * Initial the camera
 *
 * @author Ryan.Tang
 */
public class cameraReader extends Fragment implements Callback{

    private final int REQUEST_PERMISSION_CAMERA=1000;
    private CaptureActivityHandler handler;
    private ViewfinderView viewfinderView;
    private boolean hasSurface;
    private Vector<BarcodeFormat> decodeFormats;
    private String characterSet;
    private InactivityTimer inactivityTimer;
    //private MediaPlayer mediaPlayer;
    //private boolean playBeep;
    //private static final float BEEP_VOLUME=0.10f;
    private boolean vibrate;
    private boolean flashLightOpen=false;

    private ImageButton flashIbtn;

    private AlertDialog mDialog;


    @Override
    public View onCreateView(LayoutInflater inflater,ViewGroup container,Bundle savedInstanceState){
        // TODO: Implement this method
        return inflater.inflate(R.layout.qr_camera,container,false);
        //return super.onCreateView(inflater,container,savedInstanceState);
    }

    @Override
    public void onViewCreated(View view,Bundle savedInstanceState){
        // TODO: Implement this method
        super.onViewCreated(view,savedInstanceState);
        hasSurface=false;
        inactivityTimer=new InactivityTimer(getActivity());
        CameraManager.init(getActivity());
        if(Build.VERSION.SDK_INT>=Build.VERSION_CODES.M){
            if(getActivity().checkSelfPermission(Manifest.permission.CAMERA)
                    !=PackageManager.PERMISSION_GRANTED){
                requestPermissions(new String[]{Manifest.permission.CAMERA},
                        REQUEST_PERMISSION_CAMERA);
            }
        }

        viewfinderView=(ViewfinderView)view.findViewById(R.id.viewfinder_view);
        flashIbtn=(ImageButton)view.findViewById(R.id.flash_ibtn);
        flashIbtn.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v){
                if(flashLightOpen){
                    flashIbtn.setImageResource(R.drawable.ic_flash_off_white_24dp);
                }else{
                    flashIbtn.setImageResource(R.drawable.ic_flash_on_white_24dp);
                }
                toggleFlashLight();
            }
        });
    }


    @Override
    public void onResume(){
        super.onResume();
        SurfaceView surfaceView=(SurfaceView)this.getView().findViewById(R.id.preview_view);
        SurfaceHolder surfaceHolder=surfaceView.getHolder();
        if(hasSurface){
            initCamera(surfaceHolder);
        }else{
            surfaceHolder.addCallback(this);
            surfaceHolder.setType(SurfaceHolder.SURFACE_TYPE_PUSH_BUFFERS);
        }
        decodeFormats=null;
        characterSet=null;

    //    playBeep=true;
    //    final AudioManager audioService=(AudioManager)getActivity().getSystemService(getActivity().AUDIO_SERVICE);
   //     if(audioService.getRingerMode()!=AudioManager.RINGER_MODE_NORMAL){
    //        playBeep=false;
     //   }
      //  initBeepSound();
        vibrate=true;
    }

    @Override
    public void onPause(){
        super.onPause();
        if(handler!=null){
            handler.quitSynchronously();
            handler=null;
        }
        if(flashIbtn!=null){
            flashIbtn.setImageResource(R.drawable.ic_flash_off_white_24dp);
        }
        CameraManager.get().closeDriver();
    }

    @Override
    public void onDestroy(){
        inactivityTimer.shutdown();
        super.onDestroy();
    }


    @Override
    public void onRequestPermissionsResult(int requestCode,String[] permissions,int[] grantResults){
        super.onRequestPermissionsResult(requestCode,permissions,grantResults);
        if(grantResults.length>0&&requestCode==REQUEST_PERMISSION_CAMERA){
            if(grantResults[0]!=PackageManager.PERMISSION_GRANTED){
                // 未获得Camera权限
                new AlertDialog.Builder(getActivity())
                        .setTitle("提示")
                        .setMessage("请在系统设置中为App开启摄像头权限后重试")
                        .setPositiveButton("确定",new DialogInterface.OnClickListener(){
                            @Override
                            public void onClick(DialogInterface dialog,int which){
                                //       mActivity.finish();
                            }
                        })
                        .show();
            }
        }
    }

    /**
     * Handler scan result
     *
     * @param result
     * @param barcode
     */
    public void handleDecode(Result result,Bitmap barcode){
        inactivityTimer.onActivity();
        playBeepSoundAndVibrate();
        handleResult(result.getText(),result.getBarcodeFormat().toString());
    }

    public void handleResult(final String resultString,String format){
        if(TextUtils.isEmpty(resultString)){
            log.t("scan_failed");
            restartPreview();
        }else{
            if(mDialog==null){
                mDialog=new AlertDialog.Builder(getActivity())
				.setTitle("二维码类型:"+format)
                        .setMessage(resultString)
                        .setPositiveButton("确定",null)
                        .setNeutralButton("复制文本",new DialogInterface.OnClickListener(){

                            @Override
                            public void onClick(DialogInterface p1,int p2){
                                // TODO: Implement this method
                                android.content.ClipboardManager clipboardManager=(android.content.ClipboardManager)getActivity().getSystemService(Context.CLIPBOARD_SERVICE);
                                ClipData clipData=ClipData.newPlainText("text",resultString);
                                clipboardManager.setPrimaryClip(clipData);
                            }
                        })
                        .setNegativeButton("生成AwesomeQR",new DialogInterface.OnClickListener(){

                            @Override
                            public void onClick(DialogInterface p1,int p2){
                                // TODO: Implement this method
                                FragmentTransaction transaction=getActivity().getFragmentManager().beginTransaction();
                                MainActivity2.instence.awesomeCreatorFragment.setDataStr(resultString);
                                transaction.hide(MainActivity2.instence.cameraReaderFragment);
                                transaction.show(MainActivity2.instence.awesomeCreatorFragment);
                                transaction.commit();
                            }
                        }).create();
                mDialog.setOnDismissListener(new DialogInterface.OnDismissListener(){
                    @Override
                    public void onDismiss(DialogInterface dialog){
						mDialog=null;
                        restartPreview();
                    }
                });
            }
            if(!mDialog.isShowing()){
                
                mDialog.show();
            }
        }
    }


    protected void setViewfinderView(ViewfinderView view){
        viewfinderView=view;
    }

    /**
     * 切换散光灯状态
     */
    public void toggleFlashLight(){
        if(flashLightOpen){
            setFlashLightOpen(false);
        }else{
            setFlashLightOpen(true);
        }
    }

    /**
     * 设置闪光灯是否打开
     *
     * @param open
     */
    public void setFlashLightOpen(boolean open){
        if(flashLightOpen==open) return;

        flashLightOpen=!flashLightOpen;
        CameraManager.get().setFlashLight(open);
    }

    /**
     * 当前散光灯是否打开
     *
     * @return
     */
    public boolean isFlashLightOpen(){
        return flashLightOpen;
    }

    /**
     * 打开相册
     */


    private void initCamera(SurfaceHolder surfaceHolder){
        try{
            CameraManager.get().openDriver(surfaceHolder);
        }catch(IOException ioe){
            return;
        }catch(RuntimeException e){
            return;
        }
        if(handler==null){
            handler=new CaptureActivityHandler(this,decodeFormats,
                    characterSet);
        }
    }

    @Override
    public void surfaceChanged(SurfaceHolder holder,int format,
                               int width,int height){
    }

    @Override
    public void surfaceCreated(SurfaceHolder holder){
        if(!hasSurface){
            hasSurface=true;
            initCamera(holder);
        }

    }

    @Override
    public void surfaceDestroyed(SurfaceHolder holder){
        hasSurface=false;
    }

    public ViewfinderView getViewfinderView(){
        return viewfinderView;
    }

    public Handler getHandler(){
        return handler;
    }

    public void drawViewfinder(){
        viewfinderView.drawViewfinder();
    }

    protected void restartPreview(){
        // 当界面跳转时 handler 可能为null
        if(handler!=null){
            Message restartMessage=Message.obtain();
            restartMessage.what=R.id.restart_preview;
            handler.handleMessage(restartMessage);
        }
    }

  /*  private void initBeepSound(){
        if(playBeep&&mediaPlayer==null){
            // The volume on STREAM_SYSTEM is not adjustable, and users found it
            // too loud,
            // so we now play on the music stream.
            getActivity().setVolumeControlStream(AudioManager.STREAM_MUSIC);
            mediaPlayer=new MediaPlayer();
            mediaPlayer.setAudioStreamType(AudioManager.STREAM_MUSIC);
            mediaPlayer.setOnCompletionListener(beepListener);

            AssetFileDescriptor file=getResources().openRawResourceFd(
                    R.raw.beep);
            try{
                mediaPlayer.setDataSource(file.getFileDescriptor(),
                        file.getStartOffset(),file.getLength());
                file.close();
                mediaPlayer.setVolume(BEEP_VOLUME,BEEP_VOLUME);
                mediaPlayer.prepare();
            }catch(IOException e){
                mediaPlayer=null;
            }
        }
    }
*/

    private void playBeepSoundAndVibrate(){
      //  if(playBeep&&mediaPlayer!=null){
      //      mediaPlayer.start();
     //   }
        if(vibrate){
            Vibrator vibrator=(Vibrator)getActivity().getSystemService(getActivity().VIBRATOR_SERVICE);
            vibrator.vibrate(200L);
        }
    }

    /**
     * When the beep has finished playing, rewind to queue up another one.
     */
    private final OnCompletionListener beepListener=new OnCompletionListener(){
        public void onCompletion(MediaPlayer mediaPlayer){
            mediaPlayer.seekTo(0);
        }
    };

}
