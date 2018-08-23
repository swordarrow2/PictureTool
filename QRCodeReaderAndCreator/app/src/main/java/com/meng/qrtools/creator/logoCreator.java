package com.meng.qrtools.creator;

import android.app.Fragment;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.os.SystemClock;
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
import com.meng.qrtools.R;
import com.meng.qrtools.lib.ContentHelper;
import com.meng.qrtools.lib.qrcodelib.QrUtils;
import com.meng.qrtools.log;
import com.meng.qrtools.mengViews.mengColorBar;
import com.meng.qrtools.mengViews.mengEdittext;

import java.io.File;
import java.io.IOException;
import java.util.Date;

public class logoCreator extends Fragment{
    private ScrollView scrollView;
    private ImageView qrcodeImageView;
    private mengEdittext mengEtContent;
    private mengEdittext mengEtSize;
    private Button btnSelectImg;
    private Button btnRemoveImg;
    private Button btnCreate;
    private TextView tvImgPath;
    private Button btnSave;
    private Bitmap bmpQRcode=null;
    private Bitmap logoImage=null;
    private CheckBox cbAutoColor;
    private CheckBox cbCrop;
    private mengColorBar mColorBar;
    private Spinner spBarcodeFormat;
    private final int CROP_REQUEST_CODE=3;
    private String barcodeFormat;

    @Override
    public View onCreateView(LayoutInflater inflater,ViewGroup container,Bundle savedInstanceState){
        // TODO: Implement this method
        return inflater.inflate(R.layout.barcode_main,container,false);
    }

    @Override
    public void onViewCreated(View view,Bundle savedInstanceState){
        // TODO: Implement this method
        super.onViewCreated(view,savedInstanceState);
        mColorBar=(mengColorBar)view.findViewById(R.id.gif_arb_qr_main_colorBar);
        qrcodeImageView=(ImageView)view.findViewById(R.id.qr_imageview);
        mengEtContent=(mengEdittext)view.findViewById(R.id.qr_mengEditText_content);
        mengEtSize=(mengEdittext)view.findViewById(R.id.qr_mengEditText_size);
        scrollView=(ScrollView)view.findViewById(R.id.qr_mainScrollView);
        cbAutoColor=(CheckBox)view.findViewById(R.id.qr_main_autoColor);
        cbCrop=(CheckBox)view.findViewById(R.id.qr_main_crop);
        btnSelectImg=(Button)view.findViewById(R.id.qr_ButtonSelectImage);
        btnRemoveImg=(Button)view.findViewById(R.id.qr_ButtonRemoveImage);
        btnCreate=(Button)view.findViewById(R.id.qr_ButtonCreate);
        btnSave=(Button)view.findViewById(R.id.qr_ButtonSave);
        tvImgPath=(TextView)view.findViewById(R.id.qr_main_imgPathTextView);
        btnSelectImg.setOnClickListener(click);
        btnRemoveImg.setOnClickListener(click);
        btnCreate.setOnClickListener(click);
        btnSave.setOnClickListener(click);
        cbAutoColor.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener(){
            @Override
            public void onCheckedChanged(CompoundButton buttonView,boolean isChecked){
                mColorBar.setVisibility(isChecked?View.GONE:View.VISIBLE);
            }
        });
        spBarcodeFormat=(Spinner)view.findViewById(R.id.qr_main_spinner);
        //   String[] mItems=getResources().getStringArray(R.array.barcode_format);
        //   ArrayAdapter<String> adapter=new ArrayAdapter<String>(getActivity(),android.R.layout.simple_spinner_item,mItems);
        //   adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        //   spBarcodeFormat.setAdapter(adapter);
        spBarcodeFormat.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener(){
            @Override
            public void onItemSelected(AdapterView<?> parent,View view,int pos,long id){
                barcodeFormat=((TextView)view).getText().toString();
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent){
                // Another interface callback
            }
        });
    }

    /* public static Bitmap drawableToBitmap(Drawable drawable){
         if(drawable instanceof BitmapDrawable){
             return ((BitmapDrawable) drawable).getBitmap();
         }
         Bitmap bitmap = Bitmap.createBitmap(drawable.getIntrinsicWidth(),drawable.getIntrinsicHeight(),Bitmap.Config.ARGB_8888);
         Canvas canvas = new Canvas(bitmap);
         drawable.setBounds(0,0,canvas.getWidth(),canvas.getHeight());
         drawable.draw(canvas);

         return bitmap;
     }
 */
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
                    btnSave.setVisibility(View.VISIBLE);
                    break;
                case R.id.qr_ButtonSave:
                    try{
                        String s=QrUtils.saveMyBitmap(Environment.getExternalStorageDirectory().getAbsolutePath()+"/Pictures/QRcode/"+barcodeFormat+"/"+(new Date()).toString()+".png",bmpQRcode);
                        Toast.makeText(getActivity().getApplicationContext(),"已保存至"+s,Toast.LENGTH_LONG).show();
                        getActivity().getApplicationContext().sendBroadcast(new Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE,Uri.fromFile(new File(s))));//更新图库
                    }catch(IOException e){
                        Toast.makeText(getActivity().getApplicationContext(),e.getMessage(),Toast.LENGTH_LONG).show();
                    }
                    break;
            }
        }
    };

    private Uri cropPhoto(Uri uri){
        if(!cbCrop.isChecked()){
            return uri;
        }
        Intent intent=new Intent("com.android.camera.action.CROP");
        intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
        intent.addFlags(Intent.FLAG_GRANT_WRITE_URI_PERMISSION);
        intent.setDataAndType(uri,"image/*");
        intent.putExtra("crop","true");
        intent.putExtra("aspectX",300);
        intent.putExtra("aspectY",150);
        intent.putExtra("return-data",true);
        startActivityForResult(intent,CROP_REQUEST_CODE);
        return uri;
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
            String path=ContentHelper.absolutePathFromUri(getActivity().getApplicationContext(),cropPhoto(data.getData()));
            tvImgPath.setText("当前图片："+path);
            if(!cbCrop.isChecked()){
                logoImage=BitmapFactory.decodeFile(path);
            }
        }else if(requestCode==CROP_REQUEST_CODE){
            Bundle bundle=data.getExtras();
            if(bundle!=null){
                logoImage=bundle.getParcelable("data");
                log.t(getActivity(),"图片添加成功");
            }else{
                log.t(getActivity(),"取消了添加图片");
            }
        }else if(resultCode==getActivity().RESULT_CANCELED){
            Toast.makeText(getActivity().getApplicationContext(),"取消选择图片",Toast.LENGTH_SHORT).show();
        }else{
            MainActivity2.selectImage(this);
        }
        super.onActivityResult(requestCode,resultCode,data);
    }
}
