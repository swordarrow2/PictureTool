package com.meng.picTools.qrCode.reader;

import android.*;
import android.app.*;
import android.content.*;
import android.content.pm.*;
import android.graphics.*;
import android.net.*;
import android.os.*;
import android.text.*;
import android.view.*;
import android.view.View.*;
import android.widget.*;
import com.google.zxing.*;
import com.meng.picTools.activity.*;
import com.meng.picTools.helpers.ContentHelper;
import com.meng.picTools.helpers.SharedPreferenceHelper;
import com.meng.picTools.lib.*;

import android.support.v7.app.AlertDialog;
import com.meng.picTools.R;

public class GalleryQRReader extends Fragment{
    private final int REQUEST_PERMISSION_PHOTO=1001;
    private Button btnOpenGallery, btnCreateAwesomeQR;
    private TextView tvResult;
    private CheckBox cbAutoRead;
    private TextView tvFormat;

    @Override
    public View onCreateView(LayoutInflater inflater,ViewGroup container,Bundle savedInstanceState){
        return inflater.inflate(R.layout.read_gallery,container,false);
    }

    @Override
    public void onViewCreated(View view,Bundle savedInstanceState){
        super.onViewCreated(view,savedInstanceState);
        btnOpenGallery=(Button)view.findViewById(R.id.read_galleryButton);
        tvResult=(TextView)view.findViewById(R.id.read_galleryTextView_result);
        tvFormat=(TextView)view.findViewById(R.id.read_galleryTextView_format);
        btnCreateAwesomeQR=(Button)view.findViewById(R.id.read_galleryButton_createAwesomeQR);
        btnOpenGallery.setOnClickListener(click);
        btnCreateAwesomeQR.setOnClickListener(click);
        cbAutoRead=(CheckBox)view.findViewById(R.id.read_gallery_autoread);
        boolean b= SharedPreferenceHelper.getBoolean("service",false);
        cbAutoRead.setChecked(b);
        if(b){
            startService();
        }else{
            stopService();
        }
        cbAutoRead.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener(){
            @Override
            public void onCheckedChanged(CompoundButton buttonView,boolean isChecked){
                SharedPreferenceHelper.putBoolean("service",isChecked);
                if(isChecked){
                    startService();
                }else{
                    stopService();
                }
            }
        });
    }

    private OnClickListener click=new OnClickListener(){
        @Override
        public void onClick(View v){
            switch(v.getId()){
                case R.id.read_galleryButton:
                    openGallery();
                    break;
                case R.id.read_galleryButton_createAwesomeQR:
				  MainActivity.instence.showAwesomeFragment(true);
                    MainActivity.instence.awesomeCreatorFragment.setDataStr(tvResult.getText().toString());
                    
                    break;
            }
        }
    };

    public void handleDecode(Result result,Bitmap barcode){
        String resultString=result.getText();
        MainActivity.instence.doVibrate(200L);
        handleResult(resultString,result.getBarcodeFormat().toString());
    }

    protected void handleResult(final String resultString,String format){
        if(resultString.equals("")){
            Toast.makeText(getActivity(), R.string.scan_failed,Toast.LENGTH_SHORT).show();
        }else{
            tvFormat.setText("二维码类型"+format);
            tvResult.setText(resultString);
            btnCreateAwesomeQR.setVisibility(View.VISIBLE);
        }
    }

    @Override
    public void onActivityResult(int requestCode,int resultCode,Intent data){
        if(resultCode==Activity.RESULT_OK&&data!=null&&requestCode==MainActivity.instence.SELECT_FILE_REQUEST_CODE){
            Uri inputUri=data.getData();
            String path= ContentHelper.absolutePathFromUri(getActivity(),inputUri);
            if(!TextUtils.isEmpty(path)){
                Result result=QrUtils.decodeImage(path);
                if(result!=null){
                    handleDecode(result,null);
                }else{
                    new AlertDialog.Builder(getActivity()).setTitle("提示").setMessage("此图片无法识别").setPositiveButton("确定",null).show();
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
                MainActivity.instence.selectImage(this);
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
            MainActivity.instence.selectImage(this);
        }
    }

    private void startService(){
        getActivity().startService(new Intent(getActivity(),ScreenShotListenService.class));
    }

    private void stopService(){
        getActivity().stopService(new Intent(getActivity(),ScreenShotListenService.class));
    }

}
