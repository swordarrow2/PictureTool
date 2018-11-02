package com.meng.qrtools.creator;

import android.app.Fragment;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.ImageView;
import android.widget.ScrollView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.google.zxing.BarcodeFormat;
import com.meng.MainActivity2;
import com.meng.qrtools.MainActivity;
import com.meng.qrtools.R;
import com.meng.qrtools.lib.ContentHelper;
import com.meng.qrtools.lib.qrcodelib.QrUtils;
import com.meng.qrtools.log;
import com.meng.qrtools.mengViews.mengColorBar;
import com.meng.qrtools.mengViews.mengEdittext;

import java.io.File;
import java.io.IOException;

public class logoCreator extends Fragment{
    private ScrollView scrollView;
    private ImageView qrcodeImageView;
    private mengEdittext mengEtContent;
    private mengEdittext mengEtSize;
    private TextView tvImgPath;
    private Button btnSave;
    private Bitmap bmpQRcode=null;
    private Bitmap logoImage=null;
    private CheckBox cbAutoColor;
    private CheckBox cbCrop;
    private mengColorBar mColorBar;
    private String barcodeFormat;

    @Override
    public View onCreateView(LayoutInflater inflater,ViewGroup container,Bundle savedInstanceState){
        return inflater.inflate(R.layout.barcode_main,container,false);
    }

    @Override
    public void onViewCreated(View view,Bundle savedInstanceState){
        super.onViewCreated(view,savedInstanceState);
        mColorBar=(mengColorBar)view.findViewById(R.id.gif_arb_qr_main_colorBar);
        qrcodeImageView=(ImageView)view.findViewById(R.id.qr_imageview);
        mengEtContent=(mengEdittext)view.findViewById(R.id.qr_mengEditText_content);
        mengEtSize=(mengEdittext)view.findViewById(R.id.qr_mengEditText_size);
        scrollView=(ScrollView)view.findViewById(R.id.qr_mainScrollView);
        cbAutoColor=(CheckBox)view.findViewById(R.id.qr_main_autoColor);
        cbCrop=(CheckBox)view.findViewById(R.id.qr_main_crop);
        btnSave=(Button)view.findViewById(R.id.qr_ButtonSave);
        tvImgPath=(TextView)view.findViewById(R.id.qr_main_imgPathTextView);
        ((Button)view.findViewById(R.id.qr_ButtonSelectImage)).setOnClickListener(click);
        ((Button)view.findViewById(R.id.qr_ButtonRemoveImage)).setOnClickListener(click);
        ((Button)view.findViewById(R.id.qr_ButtonCreate)).setOnClickListener(click);
        btnSave.setOnClickListener(click);
        cbAutoColor.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener(){
            @Override
            public void onCheckedChanged(CompoundButton buttonView,boolean isChecked){
                mColorBar.setVisibility(isChecked?View.GONE:View.VISIBLE);
            }
        });
        ((Spinner)view.findViewById(R.id.qr_main_spinner)).setOnItemSelectedListener(new AdapterView.OnItemSelectedListener(){
            @Override
            public void onItemSelected(AdapterView<?> parent,View view,int pos,long id){
                barcodeFormat=((TextView)view).getText().toString();
                if(btnSave.getVisibility()==View.VISIBLE){
                    createBarcode();
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent){
            }
        });
    }

    OnClickListener click=new OnClickListener(){
        @Override
        public void onClick(View v){
            switch(v.getId()){
                case R.id.qr_ButtonSelectImage:
                    MainActivity2.selectImage(logoCreator.this);
                    break;
                case R.id.qr_ButtonRemoveImage:
                    logoImage=null;
                    tvImgPath.setText("未选择图片，将会生成普通二维码");
                    break;
                case R.id.qr_ButtonCreate:
                    createBarcode();
                    btnSave.setVisibility(View.VISIBLE);
                    break;
                case R.id.qr_ButtonSave:
                    try{
                        String s=QrUtils.saveMyBitmap(MainActivity.instence.getBarcodePath(barcodeFormat),bmpQRcode);
                        Toast.makeText(getActivity().getApplicationContext(),"已保存至"+s,Toast.LENGTH_LONG).show();
                        getActivity().getApplicationContext().sendBroadcast(new Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE,Uri.fromFile(new File(s))));//更新图库
                    }catch(IOException e){
                        Toast.makeText(getActivity().getApplicationContext(),e.getMessage(),Toast.LENGTH_LONG).show();
                    }
                    break;
            }
        }
    };

    private void createBarcode(){
        bmpQRcode=QrUtils.flex(
                QrUtils.createBarcode(
                        mengEtContent.getString(),
                        switchFormat(barcodeFormat),
                        cbAutoColor.isChecked()?Color.BLACK:mColorBar.getTrueColor(),
                        cbAutoColor.isChecked()?Color.WHITE:mColorBar.getFalseColor(),
                        500,
                        logoImage),
                mengEtSize.getInt());
        scrollView.post(new Runnable(){
            @Override
            public void run(){
                scrollView.fullScroll(View.FOCUS_DOWN);
            }
        });
        qrcodeImageView.setImageBitmap(bmpQRcode);
    }

    private BarcodeFormat switchFormat(String s){
        switch(s){
            case "QRcode":
                return BarcodeFormat.QR_CODE;
            case "AZTEC":
                return BarcodeFormat.AZTEC;
            case "DataMatrix":
                return BarcodeFormat.DATA_MATRIX;
            case "PDF417":
                return BarcodeFormat.PDF_417;
        }
        return BarcodeFormat.QR_CODE;
    }

    @Override
    public void onActivityResult(int requestCode,int resultCode,Intent data){
        if(requestCode==MainActivity2.SELECT_FILE_REQUEST_CODE&&resultCode==getActivity().RESULT_OK&&data.getData()!=null){
            String path=ContentHelper.absolutePathFromUri(getActivity().getApplicationContext(),MainActivity.instence.cropPhoto(data.getData(),cbCrop.isChecked()));
            tvImgPath.setText("当前图片："+path);
            if(!cbCrop.isChecked()){
                logoImage=BitmapFactory.decodeFile(path);
            }
        }else if(requestCode==MainActivity.instence.CROP_REQUEST_CODE&&resultCode==getActivity().RESULT_OK){
            Bundle bundle=data.getExtras();
            if(bundle!=null){
                logoImage=bundle.getParcelable("data");
                log.t("图片添加成功");
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

}
