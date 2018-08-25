package com.meng.qrtools.reader;

import android.Manifest;
import android.app.AlertDialog;
import android.app.Fragment;
import android.app.FragmentTransaction;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Vibrator;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.google.zxing.Result;
import com.meng.MainActivity2;
import com.meng.qrtools.lib.ContentHelper;
import com.meng.qrtools.lib.qrcodelib.QrUtils;
import com.meng.qrtools.*;
import com.meng.qrtools.lib.screenshotListener;

public class galleryReader extends Fragment{
    private final int REQUEST_PERMISSION_PHOTO=1001;
    private Button btn, btnqr;
    private TextView tv;
    private boolean vibrate;

    @Override
    public View onCreateView(LayoutInflater inflater,ViewGroup container,Bundle savedInstanceState){
        // TODO: Implement this method
        return inflater.inflate(com.meng.qrtools.R.layout.read_gallery,container,false);
    }

    @Override
    public void onViewCreated(View view,Bundle savedInstanceState){
        // TODO: Implement this method
        super.onViewCreated(view,savedInstanceState);
        btn=(Button)view.findViewById(com.meng.qrtools.R.id.read_galleryButton);
        tv=(TextView)view.findViewById(com.meng.qrtools.R.id.read_galleryTextView);
        btnqr=(Button)view.findViewById(com.meng.qrtools.R.id.read_galleryButtonQR);
		init();
        btn.setOnClickListener(new OnClickListener(){

				@Override
				public void onClick(View p1){
					// TODO: Implement this method
					openGallery();
				}
			});
        btnqr.setOnClickListener(new OnClickListener(){

				@Override
				public void onClick(View p1){
					// TODO: Implement this method
					FragmentTransaction transaction=getActivity().getFragmentManager().beginTransaction();
					MainActivity2.instence.awesomeCreatorFragment.setDataStr(tv.getText().toString());
					transaction.hide(MainActivity2.instence.galleryReaderFragment);
					transaction.show(MainActivity2.instence.awesomeCreatorFragment);
					transaction.commit();
				}
			});
        vibrate=true;
    }


    public void handleDecode(Result result,Bitmap barcode){
        String resultString=result.getText();
        playBeepSoundAndVibrate(200L);
        handleResult(resultString);
    }

    protected void handleResult(final String resultString){
        if(resultString.equals("")){
            Toast.makeText(getActivity(),com.meng.qrtools.R.string.scan_failed,Toast.LENGTH_SHORT).show();
        }else{
            tv.setText(resultString);
            btnqr.setVisibility(View.VISIBLE);
            /*	if(mDialog==null){
             mDialog=new AlertDialog.Builder(getActivity())
			 .setMessage(resultString).setNegativeButton("确定",new DialogInterface.OnClickListener() {

			 @Override
			 public void onClick(DialogInterface p1,int p2){
			 // TODO: Implement this method
			 //		finish();
			 }
			 })
			 .setNeutralButton("复制文本",new DialogInterface.OnClickListener() {

			 @Override
			 public void onClick(DialogInterface p1,int p2){
			 // TODO: Implement this method
			 android.content.ClipboardManager clipboardManager = (android.content.ClipboardManager)getActivity(). getSystemService(Context.CLIPBOARD_SERVICE);
			 ClipData clipData = ClipData.newPlainText("text",resultString);
			 clipboardManager.setPrimaryClip(clipData);
			 //		finish();
			 }
			 }).create();
			 }
			 mDialog.show();
			 */
        }
    }

    @Override
    public void onActivityResult(int requestCode,int resultCode,Intent data){
        if(resultCode==getActivity().RESULT_OK&&data!=null&&requestCode==MainActivity2.SELECT_FILE_REQUEST_CODE){
            Uri inputUri=data.getData();
            String path=null;
            path=ContentHelper.absolutePathFromUri(getActivity(),inputUri);

            if(!TextUtils.isEmpty(path)){
                Result result=QrUtils.decodeImage(path);
                if(result!=null){
                    handleDecode(result,null);
                }else{
                    new AlertDialog.Builder(getActivity())
						.setTitle("提示")
						.setMessage("此图片无法识别")
						.setPositiveButton("确定",null)
						.show();
                }
            }else{
                Toast.makeText(getActivity().getApplicationContext(),"图片路径未找到",Toast.LENGTH_SHORT).show();
            }
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode,String[] permissions,int[] grantResults){
        super.onRequestPermissionsResult(requestCode,permissions,grantResults);
        if(grantResults.length>0&&requestCode==REQUEST_PERMISSION_PHOTO){
            if(grantResults[0]!=PackageManager.PERMISSION_GRANTED){
                new AlertDialog.Builder(getActivity())
					.setTitle("提示")
					.setMessage("请在系统设置中为App中开启文件权限后重试")
					.setPositiveButton("确定",null)
					.show();
            }else{
                MainActivity2.selectImage(this);
            }
        }
    }

    public void openGallery(){
        if(Build.VERSION.SDK_INT>=Build.VERSION_CODES.M
		   &&getActivity().checkSelfPermission(Manifest.permission.READ_EXTERNAL_STORAGE)
		   !=PackageManager.PERMISSION_GRANTED){
            requestPermissions(new String[]{Manifest.permission.READ_EXTERNAL_STORAGE},
							   REQUEST_PERMISSION_PHOTO);
        }else{
            MainActivity2.selectImage(this);
        }
    }
	private void playBeepSoundAndVibrate(long ms){
            Vibrator vibrator=(Vibrator)getActivity().getSystemService(getActivity().VIBRATOR_SERVICE);
            vibrator.vibrate(ms);
    }
	private void init(){
        Intent startIntent = new Intent(getActivity(), MyService.class);
        getActivity().startService(startIntent);
	}

	@Override
	public void onDestroy(){
		// TODO: Implement this method
		Intent stopIntent = new Intent(getActivity(), MyService.class);
        getActivity().stopService(stopIntent);
		super.onDestroy();
	}
	

}
