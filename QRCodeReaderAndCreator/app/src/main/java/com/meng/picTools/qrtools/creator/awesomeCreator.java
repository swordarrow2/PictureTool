package com.meng.picTools.qrtools.creator;

import android.Manifest;
import android.app.Fragment;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.ActivityCompat;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.ImageView;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

import com.meng.picTools.MainActivity2;
import com.meng.picTools.R;
import com.meng.picTools.MainActivity;
import com.meng.picTools.qrtools.lib.ContentHelper;
import com.meng.picTools.qrtools.lib.qrcodelib.AwesomeQRCode;
import com.meng.picTools.qrtools.lib.qrcodelib.QrUtils;
import com.meng.picTools.qrtools.log;
import com.meng.picTools.qrtools.mengViews.mengColorBar;
import com.meng.picTools.qrtools.mengViews.mengEdittext;

import java.io.File;
import java.io.IOException;

/**
 * 普通的Awesome QRcode
 */

public class awesomeCreator extends Fragment{

    private ImageView qrCodeImageView;
    private mengEdittext mengEtDotScale, mengEtContents, mengEtMargin, mengEtSize;
    private CheckBox ckbWhiteMargin;
    private Bitmap backgroundImage=null;

    private boolean generating=false;
    private CheckBox ckbAutoColor;
    private ScrollView scrollView;
    private CheckBox ckbBinarize;
    private CheckBox cbCrop;
    private mengEdittext mengEtBinarize;
    private Button btnSave;
    private TextView imgPathTextView;
    private Bitmap bmpQRcode=null;
    private mengColorBar mColorBar;

    private static final int REQUEST_EXTERNAL_STORAGE=1;
    private static String[] PERMISSIONS_STORAGE={
            Manifest.permission.READ_EXTERNAL_STORAGE,
            Manifest.permission.WRITE_EXTERNAL_STORAGE
    };

    @Override
    public View onCreateView(LayoutInflater inflater,ViewGroup container,Bundle savedInstanceState){
        return inflater.inflate(R.layout.awesomeqr_main,container,false);
    }

    @Override
    public void onViewCreated(View view,Bundle savedInstanceState){
        super.onViewCreated(view,savedInstanceState);
        mColorBar=(mengColorBar)view.findViewById(R.id.gif_arb_qr_main_colorBar);
        scrollView=(ScrollView)view.findViewById(R.id.awesomeqr_main_scrollView);
        qrCodeImageView=(ImageView)view.findViewById(R.id.awesomeqr_main_qrcode);
        mengEtContents=(mengEdittext)view.findViewById(R.id.awesomeqr_main_content);
        mengEtSize=(mengEdittext)view.findViewById(R.id.awesomeqr_main_mengEdittext_size);
        mengEtMargin=(mengEdittext)view.findViewById(R.id.awesomeqr_main_margin);
        mengEtDotScale=(mengEdittext)view.findViewById(R.id.awesomeqr_main_dotScale);
        ckbWhiteMargin=(CheckBox)view.findViewById(R.id.awesomeqr_main_whiteMargin);
        ckbAutoColor=(CheckBox)view.findViewById(R.id.awesomeqr_main_autoColor);
        ckbBinarize=(CheckBox)view.findViewById(R.id.awesomeqr_main_binarize);
        mengEtBinarize=(mengEdittext)view.findViewById(R.id.awesomeqr_main_mengEdittext_binarizeThreshold);
        btnSave=(Button)view.findViewById(R.id.awesomeqr_mainButton_save);
        imgPathTextView=(TextView)view.findViewById(R.id.awesomeqr_main_imgPathTextView);
        cbCrop=(CheckBox)view.findViewById(R.id.awesomeqr_main_crop);
        ckbAutoColor.setOnCheckedChangeListener(check);
        ckbBinarize.setOnCheckedChangeListener(check);
        ((Button)view.findViewById(R.id.awesomeqr_main_backgroundImage)).setOnClickListener(click);
        ((Button)view.findViewById(R.id.awesomeqr_main_removeBackgroundImage)).setOnClickListener(click);
        ((Button)view.findViewById(R.id.awesomeqr_main_generate)).setOnClickListener(click);
        btnSave.setOnClickListener(click);
    }

    CompoundButton.OnCheckedChangeListener check=new CompoundButton.OnCheckedChangeListener(){
        @Override
        public void onCheckedChanged(CompoundButton buttonView,boolean isChecked){
            switch(buttonView.getId()){
                case R.id.awesomeqr_main_autoColor:
                    mColorBar.setVisibility(isChecked?View.GONE:View.VISIBLE);
                    if(!isChecked) log.t("如果颜色搭配不合理,二维码将会难以识别");
                    break;
                case R.id.awesomeqr_main_binarize:
                    mengEtBinarize.setEnabled(isChecked);
                    break;
            }
        }
    };

    View.OnClickListener click=new View.OnClickListener(){
        @Override
        public void onClick(View v){
            switch(v.getId()){
                case R.id.awesomeqr_main_backgroundImage:
                    MainActivity2.selectImage(awesomeCreator.this);
                    break;
                case R.id.awesomeqr_main_removeBackgroundImage:
                    backgroundImage=null;
                    imgPathTextView.setVisibility(View.GONE);
                    log.t(getResources().getString(R.string.Background_image_removed));
                    break;
                case R.id.awesomeqr_main_generate:
                    generate(mengEtContents.getString(),
                            mengEtSize.getInt(),
                            mengEtMargin.getInt(),
                            Float.parseFloat(mengEtDotScale.getString()),
                            mColorBar.getTrueColor(),
                            ckbAutoColor.isChecked()?Color.WHITE:mColorBar.getFalseColor(),
                            backgroundImage,
                            ckbWhiteMargin.isChecked(),
                            ckbAutoColor.isChecked(),
                            ckbBinarize.isChecked(),
                            mengEtBinarize.getInt()
                    );
                    btnSave.setVisibility(View.VISIBLE);
                    break;
                case R.id.awesomeqr_mainButton_save:
                    try{
                        String s=QrUtils.saveMyBitmap(MainActivity.instence.getAwesomeQRPath(),bmpQRcode);
                        log.t("已保存至"+s);
                        getActivity().getApplicationContext().sendBroadcast(new Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE,Uri.fromFile(new File(s))));//更新图库
                    }catch(IOException e){
                        log.e(e);
                    }
                    break;
            }
        }
    };

    public void setDataStr(String s){
        mengEtContents.setString(s);
    }

    @Override
    public void onResume(){
        super.onResume();
        acquireStoragePermissions();
    }


    private void acquireStoragePermissions(){
        int permission=ActivityCompat.checkSelfPermission(getActivity().getApplicationContext(),Manifest.permission.WRITE_EXTERNAL_STORAGE);
        if(permission!=PackageManager.PERMISSION_GRANTED){
            ActivityCompat.requestPermissions(
                    getActivity(),
                    PERMISSIONS_STORAGE,
                    REQUEST_EXTERNAL_STORAGE
            );
        }
    }
	private Uri cropPhoto(Uri uri,boolean needCrop){
        if(!needCrop) return uri;
        Intent intent=new Intent("com.android.camera.action.CROP");
        intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
        intent.addFlags(Intent.FLAG_GRANT_WRITE_URI_PERMISSION);
        intent.setDataAndType(uri,"image/*");
        intent.putExtra("crop","true");
        intent.putExtra("aspectX",1);
        intent.putExtra("aspectY",1);
        intent.putExtra("outputX",300);
        intent.putExtra("outputY",300);
        intent.putExtra("return-data",true);
        startActivityForResult(intent,MainActivity.instence.CROP_REQUEST_CODE);
        return uri;
    }
    @Override
    public void onActivityResult(int requestCode,int resultCode,Intent data){
        if(requestCode==MainActivity2.SELECT_FILE_REQUEST_CODE&&resultCode==getActivity().RESULT_OK&&data.getData()!=null){
            imgPathTextView.setVisibility(View.VISIBLE);
            String path=ContentHelper.absolutePathFromUri(getActivity().getApplicationContext(),cropPhoto(data.getData(),cbCrop.isChecked()));
            imgPathTextView.setText("当前图片："+path);
            if(!cbCrop.isChecked()){
                backgroundImage=BitmapFactory.decodeFile(path);
            }
        }else if(requestCode==MainActivity.instence.CROP_REQUEST_CODE){
            Bundle bundle=data.getExtras();
			if(bundle==null){
				log.t("bundle is null");
			}
            if(bundle!=null){
                backgroundImage=bundle.getParcelable("data");
                log.t(getResources().getString(R.string.Background_image_added));
            }else{
                log.t("取消了添加图片");
            }
        }else if(resultCode==getActivity().RESULT_CANCELED){
            Toast.makeText(getActivity().getApplicationContext(),"取消选择图片",Toast.LENGTH_SHORT).show();
        }else{
            MainActivity2.selectImage(this);
        }
        super.onActivityResult(requestCode,resultCode,data);
    }

    private void generate(final String contents,final int size,final int margin,final float dotScale,
                          final int colorDark,final int colorLight,final Bitmap background,final boolean whiteMargin,
                          final boolean autoColor,final boolean binarize,final int binarizeThreshold){
        if(generating) return;
        generating=true;
        new Thread(new Runnable(){
            @Override
            public void run(){
                try{
                    final Bitmap b=AwesomeQRCode.create(contents,size,margin,dotScale,colorDark,colorLight,background,whiteMargin,autoColor,binarize,binarizeThreshold);
                    getActivity().runOnUiThread(new Runnable(){
                        @Override
                        public void run(){
                            qrCodeImageView.setImageBitmap(b);
                            bmpQRcode=b;
                            scrollView.post(new Runnable(){
                                @Override
                                public void run(){
                                    scrollView.fullScroll(View.FOCUS_DOWN);
                                }
                            });
                            generating=false;
                        }
                    });
                }catch(Exception e){
                    log.e(e);
                    generating=false;
                }
            }
        }).start();
    }
}
