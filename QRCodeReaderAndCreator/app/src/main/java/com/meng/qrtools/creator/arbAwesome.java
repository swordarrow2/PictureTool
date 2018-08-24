package com.meng.qrtools.creator;

import android.Manifest;
import android.app.AlertDialog;
import android.app.Fragment;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.support.v4.app.ActivityCompat;
import android.util.DisplayMetrics;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.meng.MainActivity2;
import com.meng.qrtools.R;
import com.meng.qrtools.lib.ContentHelper;
import com.meng.qrtools.lib.qrcodelib.QrUtils;
import com.meng.qrtools.log;
import com.meng.qrtools.mengViews.mengColorBar;
import com.meng.qrtools.mengViews.mengEdittext;
import com.meng.qrtools.mengViews.mengSeekBar;
import com.meng.qrtools.mengViews.mengSelectRectView;

import java.io.File;
import java.io.IOException;
import java.util.Date;

/**
 * Created by Administrator on 2018/7/19.
 */

public class arbAwesome extends Fragment{

    private ImageView qrCodeImageView;
    private mengEdittext mengEtDotScale, mengEtContents;
    private Button btGenerate;
    private CheckBox ckbAutoColor;
    private Button btnSave;
    private TextView imgPathTextView;
    private mengColorBar mColorBar;

    private Bitmap finallyBmp=null;
    private String selectedBmpPath="";
    private int qrSize;
    private int selectedBmpWidth=0;
    private int selectedBmpHeight=0;
    private float screenW;
    private float screenH;
    private mengSelectRectView mv;
    private Button btSelectBG;
    private String[] PERMISSIONS_STORAGE={
            Manifest.permission.READ_EXTERNAL_STORAGE,
            Manifest.permission.WRITE_EXTERNAL_STORAGE
    };

    @Override
    public View onCreateView(LayoutInflater inflater,ViewGroup container,Bundle savedInstanceState){
        // TODO: Implement this method
        return inflater.inflate(R.layout.arb_awesome_qr,container,false);
    }

    @Override
    public void onViewCreated(View view,Bundle savedInstanceState){
        // TODO: Implement this method
        super.onViewCreated(view,savedInstanceState);
        mv=(mengSelectRectView)view.findViewById(R.id.arb_awesome_qrselectRectView);
        mColorBar=(mengColorBar)view.findViewById(R.id.gif_arb_qr_main_colorBar);
        qrCodeImageView=(ImageView)view.findViewById(R.id.awesomeqr_main_qrcode);
        mengEtContents=(mengEdittext)view.findViewById(R.id.awesomeqr_main_content);
        mengEtDotScale=(mengEdittext)view.findViewById(R.id.awesomeqr_main_dotScale);
        btSelectBG=(Button)view.findViewById(R.id.awesomeqr_main_backgroundImage);
        btGenerate=(Button)view.findViewById(R.id.awesomeqr_main_generate);
        ckbAutoColor=(CheckBox)view.findViewById(R.id.awesomeqr_main_autoColor);
        btnSave=(Button)view.findViewById(R.id.awesomeqr_mainButton_save);
        imgPathTextView=(TextView)view.findViewById(R.id.awesomeqr_main_imgPathTextView);
        btSelectBG.setOnClickListener(click);
        btGenerate.setOnClickListener(click);
        btnSave.setOnClickListener(click);
        ckbAutoColor.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener(){
            @Override
            public void onCheckedChanged(CompoundButton buttonView,boolean isChecked){
                mColorBar.setVisibility(isChecked?View.GONE:View.VISIBLE);
            }
        });
        DisplayMetrics dm=new DisplayMetrics();
        getActivity().getWindowManager().getDefaultDisplay().getMetrics(dm);
        screenW=dm.widthPixels;
        screenH=dm.heightPixels;
    }

    /*  CompoundButton.OnCheckedChangeListener check = new CompoundButton.OnCheckedChangeListener() {
          @Override
          public void onCheckedChanged(CompoundButton buttonView,boolean isChecked){
              switch(buttonView.getId()){
                  case R.id.awesomeqr_main_autoColor:
                      mColorBar.setVisibility(isChecked? View.GONE :View.VISIBLE);
                      break;
              }
          }
      };
  */
    View.OnClickListener click=new View.OnClickListener(){
        @Override
        public void onClick(View v){
            switch(v.getId()){
                case R.id.awesomeqr_main_backgroundImage:
                    qrCodeImageView.setVisibility(View.GONE);
                    btGenerate.setEnabled(true);
                    MainActivity2.selectImage(arbAwesome.this);
                    break;

                case R.id.awesomeqr_main_generate:
                    generate();
                    btnSave.setVisibility(View.VISIBLE);
                    qrCodeImageView.setVisibility(View.VISIBLE);
                    mv.setVisibility(View.GONE);
                    break;
                case R.id.awesomeqr_mainButton_save:
                    try{
                        String s=QrUtils.saveMyBitmap(
                                Environment.getExternalStorageDirectory().getAbsolutePath()+
                                        "/Pictures/QRcode/AwesomeQR"+(new Date()).toString()+".png",
                                finallyBmp);
                        log.t(getActivity(),"已保存至"+s);
                        getActivity().getApplicationContext().sendBroadcast(new Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE,Uri.fromFile(new File(s))));//更新图库
                    }catch(IOException e){
                        log.e(getActivity(),e);
                    }
                    break;
            }
        }
    };

    @Override
    public void onResume(){
        super.onResume();
        acquireStoragePermissions();
    }

    private void acquireStoragePermissions(){
        int permission=ActivityCompat.checkSelfPermission(getActivity().getApplicationContext(),Manifest.permission.WRITE_EXTERNAL_STORAGE);
        if(permission!=PackageManager.PERMISSION_GRANTED){
            int REQUEST_EXTERNAL_STORAGE=1;
            ActivityCompat.requestPermissions(
                    getActivity(),
                    PERMISSIONS_STORAGE,
                    REQUEST_EXTERNAL_STORAGE
            );
        }
    }

    @Override
    public void onActivityResult(int requestCode,int resultCode,Intent data){
        if(requestCode==MainActivity2.SELECT_FILE_REQUEST_CODE&&resultCode==getActivity().RESULT_OK&&data.getData()!=null){
            imgPathTextView.setVisibility(View.VISIBLE);
            Uri uri=data.getData();
            selectedBmpPath=ContentHelper.absolutePathFromUri(getActivity().getApplicationContext(),uri);
            imgPathTextView.setText("当前文件："+selectedBmpPath);
            final Bitmap selectedBmp=BitmapFactory.decodeFile(selectedBmpPath);
            selectedBmpWidth=selectedBmp.getWidth();
            selectedBmpHeight=selectedBmp.getHeight();
            final mengSeekBar msb=new mengSeekBar(getActivity());
            int maxProg=Math.min(selectedBmpWidth,selectedBmpHeight);
            msb.setMax(maxProg);
            msb.setProgress(maxProg/3);
            new AlertDialog.Builder(getActivity())
                    .setTitle("输入要添加的二维码大小(像素)")
                    .setView(msb)
                    .setPositiveButton("确定",new DialogInterface.OnClickListener(){
                        @Override
                        public void onClick(DialogInterface p1,int p2){
                            qrSize=msb.getProgress();
                            //ll.addView(new mengSelectRectView(getActivity(),selectedBmp,screenW,screenH));
                            mv.setup(selectedBmp,screenW,screenH,qrSize);
                            ViewGroup.LayoutParams para=mv.getLayoutParams();
                            para.height=(int)(screenW/selectedBmpWidth*selectedBmpHeight);
                            mv.setLayoutParams(para);
                            mv.setVisibility(View.VISIBLE);
                        }
                    }).show();
        }else if(resultCode==getActivity().RESULT_CANCELED){
            Toast.makeText(getActivity().getApplicationContext(),"取消选择图片",Toast.LENGTH_SHORT).show();
        }else{
            MainActivity2.selectImage(arbAwesome.this);
        }
        super.onActivityResult(requestCode,resultCode,data);
    }

    private void generate(){
        finallyBmp=QrUtils.generate(
                mengEtContents.getString(),
                Float.parseFloat(mengEtDotScale.getString()),
                ckbAutoColor.isChecked()?Color.BLACK:mColorBar.getTrueColor(),
                ckbAutoColor.isChecked()?Color.WHITE:mColorBar.getFalseColor(),
                ckbAutoColor.isChecked(),
                between(mv.getSelectLeft()/mv.getXishu(),0,selectedBmpWidth-qrSize),
                between(mv.getSelectTop()/mv.getXishu(),0,selectedBmpHeight-qrSize),
                qrSize,
                BitmapFactory.decodeFile(selectedBmpPath).copy(Bitmap.Config.ARGB_8888,true));
        qrCodeImageView.setImageBitmap(QrUtils.scaleBitmap(finallyBmp,mv.getXishu()));
    }


    private int between(float a,int min,int max){
        if(a<min){
            a=min;
        }
        if(a>max){
            a=max;
        }
        return (int)a;
    }


}
