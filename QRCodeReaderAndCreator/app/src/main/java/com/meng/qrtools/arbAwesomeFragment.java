package com.meng.qrtools;

import android.Manifest;
import android.app.AlertDialog;
import android.app.Fragment;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.os.SystemClock;
import android.support.v4.app.ActivityCompat;
import android.util.DisplayMetrics;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.meng.MainActivity2;
import com.meng.qrtools.creator.ContentHelper;
import com.meng.qrtools.lib.qrcodelib.AwesomeQRCode;
import com.meng.qrtools.lib.qrcodelib.QrUtils;
import com.meng.qrtools.views.mengColorBar;
import com.meng.qrtools.views.mengEdittext;
import com.meng.qrtools.views.selectRectView;

import java.io.File;
import java.io.IOException;

/**
 * Created by Administrator on 2018/7/19.
 */

public class arbAwesomeFragment extends Fragment{

    private ImageView qrCodeImageView;
    private mengEdittext mengEtDotScale, mengEtContents;
    private Button btGenerate;
    private CheckBox ckbAutoColor;
    private Button btnSave;
    private TextView imgPathTextView;
    private mengColorBar mColorBar;

    private Bitmap tmpQRBackground=null;
    private Bitmap finallyBmp=null;
    private String selectedBmpPath="";
    private int qrSize;
    private float screenW;
    private float screenH;
    private selectRectView mv;

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
        mv=(selectRectView)view.findViewById(R.id.arb_awesome_qrselectRectView);
        mColorBar=(mengColorBar)view.findViewById(R.id.awesomeqr_main_colorBar);
        qrCodeImageView=(ImageView)view.findViewById(R.id.awesomeqr_main_qrcode);
        mengEtContents=(mengEdittext)view.findViewById(R.id.awesomeqr_main_content);
        mengEtDotScale=(mengEdittext)view.findViewById(R.id.awesomeqr_main_dotScale);
        Button btSelectBG=(Button)view.findViewById(R.id.awesomeqr_main_backgroundImage);
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
                    MainActivity2.selectImage(arbAwesomeFragment.this);
                    break;

                case R.id.awesomeqr_main_generate:
                    generate(mengEtContents.getString(),
                            Float.parseFloat(mengEtDotScale.getString()),
                            mColorBar.getTrueColor(),
                            ckbAutoColor.isChecked()?Color.WHITE:mColorBar.getFalseColor(),
                            ckbAutoColor.isChecked()
                    );
                    btnSave.setVisibility(View.VISIBLE);
                    qrCodeImageView.setVisibility(View.VISIBLE);
                    mv.setVisibility(View.GONE);
                    break;
                case R.id.awesomeqr_mainButton_save:
                    try{
                        String s=QrUtils.saveMyBitmap(
                                Environment.getExternalStorageDirectory().getAbsolutePath()+
                                        "/Pictures/QRcode/AwesomeQR"+SystemClock.elapsedRealtime()+".png",
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
            final EditText et=new EditText(getActivity());
            et.setHint("0<大小<"+(Math.min(selectedBmp.getWidth(),selectedBmp.getHeight())+1));
            new AlertDialog.Builder(getActivity())
                    .setTitle("输入要添加的二维码大小(像素)")
                    .setView(et)
                    .setPositiveButton("确定",new DialogInterface.OnClickListener(){
                        @Override
                        public void onClick(DialogInterface p1,int p2){
                            qrSize=Integer.parseInt(et.getText().toString());
                            //ll.addView(new selectRectView(getActivity(),selectedBmp,screenW,screenH));
                            mv.setup(selectedBmp,screenW,screenH,qrSize);
                            ViewGroup.LayoutParams para=mv.getLayoutParams();
                            Bitmap bmp=BitmapFactory.decodeFile(selectedBmpPath).copy(Bitmap.Config.ARGB_8888,true);
                            para.height=(int)(screenW/bmp.getWidth()*bmp.getHeight());
                            mv.setLayoutParams(para);
                            mv.setVisibility(View.VISIBLE);
                            ((InputMethodManager)getActivity().getSystemService(Context.INPUT_METHOD_SERVICE)).hideSoftInputFromWindow(et.getWindowToken(),0);
                        }
                    }).show();
        }else if(resultCode==getActivity().RESULT_CANCELED){
            Toast.makeText(getActivity().getApplicationContext(),"取消选择图片",Toast.LENGTH_SHORT).show();
        }else{
            MainActivity2.selectImage(arbAwesomeFragment.this);
        }
        super.onActivityResult(requestCode,resultCode,data);
    }

    private void generate(final String contents,final float dotScale,final int colorDark,
                          final int colorLight,final boolean autoColor){
        //  try {
        finallyBmp=BitmapFactory.decodeFile(selectedBmpPath).copy(Bitmap.Config.ARGB_8888,true);
        int cutX=between(mv.getSelectLeft()/mv.getXishu(),0,finallyBmp.getWidth()-qrSize);
        int cutY=between(mv.getSelectTop()/mv.getXishu(),0,finallyBmp.getHeight()-qrSize);
        tmpQRBackground=Bitmap.createBitmap(finallyBmp,cutX,cutY,qrSize,qrSize);
        Bitmap bmpQRcode=AwesomeQRCode.create(contents,qrSize,0,dotScale,colorDark,colorLight,tmpQRBackground,false,autoColor,false,128);
        Canvas c=new Canvas(finallyBmp);
        c.drawBitmap(bmpQRcode,cutX,cutY,new Paint());
        qrCodeImageView.setImageBitmap(QrUtils.scaleBitmap(finallyBmp,mv.getXishu()));
        ViewGroup.LayoutParams para=qrCodeImageView.getLayoutParams();
        para.height=(int)(screenW/finallyBmp.getWidth()*finallyBmp.getHeight());
        qrCodeImageView.setLayoutParams(para);

//		scrollView.post(new Runnable() {
//                @Override
//                public void run(){
//                    scrollView.fullScroll(View.FOCUS_DOWN);
//                }
//            });
        // } catch (Exception e) {
        //    log.e(getActivity(), e);
        //    generating = false;
        // }
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
