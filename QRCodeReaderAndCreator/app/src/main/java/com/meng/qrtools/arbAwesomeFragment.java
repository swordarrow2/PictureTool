package com.meng.qrtools;

import android.*;
import android.app.*;
import android.content.*;
import android.content.pm.*;
import android.graphics.*;
import android.net.*;
import android.os.*;
import android.support.v4.app.*;
import android.util.*;
import android.view.*;
import android.widget.*;
import com.meng.*;
import com.meng.qrtools.creator.*;
import com.meng.qrtools.lib.qrcodelib.*;
import com.meng.qrtools.views.*;
import java.io.*;
import android.view.inputmethod.*;

/**
 * Created by Administrator on 2018/7/19.
 */

public class arbAwesomeFragment extends android.app.Fragment{

    private ImageView qrCodeImageView;
    private mengEdittext mengEtDotScale, mengEtContents;
    private Button btGenerate, btSelectBG;
    private boolean generating = false;
    private CheckBox ckbAutoColor;
    private ScrollView scrollView;
    private Button btnSave;
    private TextView imgPathTextView;
    private mengColorBar mColorBar;
    public static final int selectRect = 2;

    private static final int REQUEST_EXTERNAL_STORAGE = 1;
    private static String[] PERMISSIONS_STORAGE = {
		Manifest.permission.READ_EXTERNAL_STORAGE,
		Manifest.permission.WRITE_EXTERNAL_STORAGE
    };

    // private Bitmap tmpQR = null;
    private Bitmap tmpQRBackground = null;
    //private Bitmap imageViewBackground = null;
    
  //  private Bitmap finallyBmp = null;
	Bitmap bmp=null;
    private String selectedBmpPath = "";

    private int qrSize;
    
    float screenW;
    float screenH;
	
	selectRectView mv;
	
	
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
		
        mColorBar=(mengColorBar) view.findViewById(R.id.awesomeqr_main_colorBar);
        scrollView=(ScrollView) view.findViewById(R.id.awesomeqr_main_scrollView);
        qrCodeImageView=(ImageView) view.findViewById(R.id.awesomeqr_main_qrcode);
        mengEtContents=(mengEdittext) view.findViewById(R.id.awesomeqr_main_content);
        mengEtDotScale=(mengEdittext) view.findViewById(R.id.awesomeqr_main_dotScale);
        btSelectBG=(Button) view.findViewById(R.id.awesomeqr_main_backgroundImage);
        btGenerate=(Button) view.findViewById(R.id.awesomeqr_main_generate);
        ckbAutoColor=(CheckBox) view.findViewById(R.id.awesomeqr_main_autoColor);
        btnSave=(Button) view.findViewById(R.id.awesomeqr_mainButton_save);
        imgPathTextView=(TextView) view.findViewById(R.id.awesomeqr_main_imgPathTextView);
        ckbAutoColor.setOnCheckedChangeListener(check);
        btSelectBG.setOnClickListener(click);
        btGenerate.setOnClickListener(click);
        btnSave.setOnClickListener(click);
        DisplayMetrics dm = new DisplayMetrics();
        getActivity().getWindowManager().getDefaultDisplay().getMetrics(dm);
        screenW=dm.widthPixels;
        screenH=dm.heightPixels;
    }

    CompoundButton.OnCheckedChangeListener check = new CompoundButton.OnCheckedChangeListener() {
        @Override
        public void onCheckedChanged(CompoundButton buttonView,boolean isChecked){
            switch(buttonView.getId()){
                case R.id.awesomeqr_main_autoColor:
                    mColorBar.setVisibility(isChecked? View.GONE :View.VISIBLE);
                    break;
            }
        }
    };

    View.OnClickListener click = new View.OnClickListener() {
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
							 ckbAutoColor.isChecked()? Color.WHITE :mColorBar.getFalseColor(),
							 ckbAutoColor.isChecked()
							 );
                    btnSave.setVisibility(View.VISIBLE);
					qrCodeImageView.setVisibility(View.VISIBLE);
					mv.setVisibility(View.GONE);
                    break;
                case R.id.awesomeqr_mainButton_save:
                    try{
                        String s = QRCode.saveMyBitmap(
							Environment.getExternalStorageDirectory().getAbsolutePath()+
							"/Pictures/QRcode/AwesomeQR"+SystemClock.elapsedRealtime()+".png",
							bmp);
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
        int permission = ActivityCompat.checkSelfPermission(getActivity().getApplicationContext(),Manifest.permission.WRITE_EXTERNAL_STORAGE);
        if(permission!=PackageManager.PERMISSION_GRANTED){
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
            Uri uri = data.getData();
            selectedBmpPath=ContentHelper.absolutePathFromUri(getActivity().getApplicationContext(),uri);
            imgPathTextView.setText("当前文件："+selectedBmpPath);
            final Bitmap selectedBmp = BitmapFactory.decodeFile(selectedBmpPath);
            final EditText et = new EditText(getActivity());
            et.setHint("0<大小<"+(Math.min(selectedBmp.getWidth(),selectedBmp.getHeight())+1));
            new AlertDialog.Builder(getActivity())
				.setTitle("输入要添加的二维码大小(像素)")
				.setView(et)
				.setPositiveButton("确定",new DialogInterface.OnClickListener() {
					@Override
					public void onClick(DialogInterface p1,int p2){
						qrSize=Integer.parseInt(et.getText().toString());
						//ll.addView(new selectRectView(getActivity(),selectedBmp,screenW,screenH));
						mv.setup(selectedBmp,screenW,screenH,qrSize);
						ViewGroup.LayoutParams para = mv.getLayoutParams();
						DisplayMetrics dm = new DisplayMetrics();
						getActivity().getWindowManager().getDefaultDisplay().getMetrics(dm);
						float screenW = dm.widthPixels;
						Bitmap bmp = BitmapFactory.decodeFile(selectedBmpPath).copy(Bitmap.Config.ARGB_8888,true);
						para.height=(int) (screenW/bmp.getWidth()*bmp.getHeight());
						mv.setLayoutParams(para);
						mv.setVisibility(View.VISIBLE);
						InputMethodManager imm = (InputMethodManager)getActivity(). getSystemService(Context.INPUT_METHOD_SERVICE);
						imm.hideSoftInputFromWindow(et.getWindowToken(), 0);
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
        if(generating) return;
        generating=true;
		//  try {
		bmp = BitmapFactory.decodeFile(selectedBmpPath).copy(Bitmap.Config.ARGB_8888,true);
		int cutX=between(mv.getSelectLeft()/mv.getXishu(),0,bmp.getWidth()-qrSize);
		int cutY=between(mv.getSelectTop()/mv.getXishu(),0,bmp.getHeight()-qrSize);
		tmpQRBackground=Bitmap.createBitmap(
			bmp,
			cutX,
			cutY,
			qrSize,
			qrSize);
		Bitmap bmpQRcode = AwesomeQRCode.create(contents,qrSize,0,dotScale,colorDark,colorLight,tmpQRBackground,false,autoColor,false,128);
		Canvas c = new Canvas(bmp);
		c.drawBitmap(
			bmpQRcode, 
			cutX,
			cutY, 
			new Paint());	
		qrCodeImageView.setImageBitmap(scaleBitmap(bmp,mv.getXishu()));
		ViewGroup.LayoutParams para = qrCodeImageView.getLayoutParams();
		DisplayMetrics dm = new DisplayMetrics();
		getActivity().getWindowManager().getDefaultDisplay().getMetrics(dm);
		float screenW = dm.widthPixels;
		para.height=(int) (screenW/bmp.getWidth()*bmp.getHeight());
		qrCodeImageView.setLayoutParams(para);
		
//		scrollView.post(new Runnable() {
//                @Override
//                public void run(){
//                    scrollView.fullScroll(View.FOCUS_DOWN);
//                }
//            });
		generating=false;

		// } catch (Exception e) {
        //    log.e(getActivity(), e);
        //    generating = false;
		// }
    }

    private Bitmap scaleBitmap(Bitmap origin,float ratio){
        if(origin==null){
            return null;
        }
        int width = origin.getWidth();
        int height = origin.getHeight();
        Matrix matrix = new Matrix();
        matrix.preScale(ratio,ratio);
        Bitmap newBM = Bitmap.createBitmap(origin,0,0,width,height,matrix,false);
        if(newBM.equals(origin)){
            return newBM;
        }
      //  origin.recycle();
        return newBM;
    }

    private int between(float a,int min,int max){
        if(a<min){
            a=min;
        }
        if(a>max){
            a=max;
        }
        return (int) a;
    }

    

}
