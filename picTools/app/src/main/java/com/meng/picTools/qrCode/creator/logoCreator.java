package com.meng.picTools.qrCode.creator;

import android.app.*;
import android.content.*;
import android.graphics.*;
import android.net.*;
import android.os.*;
import android.view.*;
import android.view.View.*;
import android.widget.*;
import com.google.zxing.*;
import com.meng.picTools.LogTool;
import com.meng.picTools.MainActivity;
import com.meng.picTools.MainActivity2;
import com.meng.picTools.R;
import com.meng.picTools.lib.*;
import com.meng.picTools.qrCode.qrcodelib.*;
import com.meng.picTools.mengViews.*;
import java.io.*;

public class logoCreator extends Fragment{
    private ScrollView scrollView;
    private ImageView qrcodeImageView;
    private MengEditText mengEtContent;
    private MengEditText mengEtSize;
    private TextView tvImgPath;
    private Button btnSave;
    private Bitmap bmpQRcode=null;
    private Bitmap logoImage=null;
    private CheckBox cbAutoColor;
    private CheckBox cbCrop;
    private MengColorBar mColorBar;
    private String barcodeFormat;

    @Override
    public View onCreateView(LayoutInflater inflater,ViewGroup container,Bundle savedInstanceState){
        return inflater.inflate(R.layout.barcode_main,container,false);
	  }

    @Override
    public void onViewCreated(View view,Bundle savedInstanceState){
        super.onViewCreated(view,savedInstanceState);
        mColorBar=(MengColorBar)view.findViewById(R.id.gif_arb_qr_main_colorBar);
        qrcodeImageView=(ImageView)view.findViewById(R.id.qr_imageview);
        mengEtContent=(MengEditText)view.findViewById(R.id.qr_mengEditText_content);
        mengEtSize=(MengEditText)view.findViewById(R.id.qr_mengEditText_size);
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
				  if(!isChecked) LogTool.t("如果颜色搭配不合理,二维码将会难以识别");
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
            String path=ContentHelper.absolutePathFromUri(getActivity().getApplicationContext(),cropPhoto(data.getData(),cbCrop.isChecked()));
            tvImgPath.setText("当前图片："+path);
            if(!cbCrop.isChecked()){
                logoImage=BitmapFactory.decodeFile(path);
			  }
		  }else if(requestCode==MainActivity.instence.CROP_REQUEST_CODE&&resultCode==getActivity().RESULT_OK){
            Bundle bundle=data.getExtras();
            if(bundle!=null){
                logoImage=bundle.getParcelable("data");
                LogTool.t("图片添加成功");
			  }else{
                LogTool.t("取消了添加图片");
			  }
		  }else if(resultCode==getActivity().RESULT_CANCELED){
            Toast.makeText(getActivity().getApplicationContext(),"取消选择图片",Toast.LENGTH_SHORT).show();
		  }else{
            MainActivity2.selectImage(this);
		  }
        super.onActivityResult(requestCode,resultCode,data);
	  }

  }
