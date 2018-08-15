package com.meng.qrtools;

import android.app.*;
import android.os.*;
import android.view.*;
import android.widget.*;
import com.meng.*;
import com.meng.qrtools.creator.*;
import android.graphics.*;
import com.meng.qrtools.lib.qrcodelib.*;
import java.io.*;
import com.meng.qrtools.views.*;
import android.*;
import android.support.v4.app.*;
import android.content.*;
import android.content.pm.*;
import android.net.*;

/**
 * Created by Administrator on 2018/7/19.
 */

public class testFragment extends android.app.Fragment{

    private ImageView qrCodeImageView;
    private mengEdittext mengEtDotScale, mengEtContents, mengEtMargin, mengEtSize;
    private Button btGenerate, btSelectBG, btRemoveBG;
    private CheckBox ckbWhiteMargin;
    private Bitmap backgroundImage = null;

    private boolean generating = false;
    private CheckBox ckbAutoColor;
    private ScrollView scrollView;
    private CheckBox ckbBinarize;
    private mengEdittext mengEtBinarize;
    private Button btnSave;
    private TextView imgPathTextView;
    private Bitmap bmpQRcode = null;
    private mengColorBar mColorBar;
    private static final int CROP_REQUEST_CODE = 3;

    private static final int REQUEST_EXTERNAL_STORAGE = 1;
    private static String[] PERMISSIONS_STORAGE = {
		Manifest.permission.READ_EXTERNAL_STORAGE,
		Manifest.permission.WRITE_EXTERNAL_STORAGE
    };

    @Override
    public View onCreateView(LayoutInflater inflater,ViewGroup container,Bundle savedInstanceState){
        // TODO: Implement this method
        return inflater.inflate(R.layout.test,container,false);
    }

    @Override
    public void onViewCreated(View view,Bundle savedInstanceState){
        // TODO: Implement this method
        super.onViewCreated(view,savedInstanceState);
        mColorBar=(mengColorBar) view.findViewById(R.id.awesomeqr_main_colorBar);
        scrollView=(ScrollView) view.findViewById(R.id.awesomeqr_main_scrollView);
        qrCodeImageView=(ImageView) view.findViewById(R.id.awesomeqr_main_qrcode);
        mengEtContents=(mengEdittext) view.findViewById(R.id.awesomeqr_main_content);
        mengEtSize=(mengEdittext) view.findViewById(R.id.awesomeqr_main_mengEdittext_size);
        mengEtMargin=(mengEdittext) view.findViewById(R.id.awesomeqr_main_margin);
        mengEtDotScale=(mengEdittext) view.findViewById(R.id.awesomeqr_main_dotScale);
        btSelectBG=(Button) view.findViewById(R.id.awesomeqr_main_backgroundImage);
        btRemoveBG=(Button) view.findViewById(R.id.awesomeqr_main_removeBackgroundImage);
        btGenerate=(Button) view.findViewById(R.id.awesomeqr_main_generate);
        ckbWhiteMargin=(CheckBox) view.findViewById(R.id.awesomeqr_main_whiteMargin);
        ckbAutoColor=(CheckBox) view.findViewById(R.id.awesomeqr_main_autoColor);
        ckbBinarize=(CheckBox) view.findViewById(R.id.awesomeqr_main_binarize);
        mengEtBinarize=(mengEdittext) view.findViewById(R.id.awesomeqr_main_mengEdittext_binarizeThreshold);
        btnSave=(Button) view.findViewById(R.id.awesomeqr_mainButton_save);
        imgPathTextView=(TextView) view.findViewById(R.id.awesomeqr_main_imgPathTextView);
        ckbAutoColor.setOnCheckedChangeListener(check);
        ckbBinarize.setOnCheckedChangeListener(check);
        btSelectBG.setOnClickListener(click);
        btRemoveBG.setOnClickListener(click);
        btGenerate.setOnClickListener(click);
        btnSave.setOnClickListener(click);
    }

    CompoundButton.OnCheckedChangeListener check = new CompoundButton.OnCheckedChangeListener() {
        @Override
        public void onCheckedChanged(CompoundButton buttonView,boolean isChecked){
            switch(buttonView.getId()){
                case R.id.awesomeqr_main_autoColor:
                    mColorBar.setVisibility(isChecked? View.GONE :View.VISIBLE);
                    break;
                case R.id.awesomeqr_main_binarize:
                    mengEtBinarize.setVisibility(isChecked? View.VISIBLE :View.GONE);
                    break;
            }
        }
    };

    View.OnClickListener click = new View.OnClickListener() {
        @Override
        public void onClick(View v){
            switch(v.getId()){
                case R.id.awesomeqr_main_backgroundImage:
                    MainActivity2.selectImage(testFragment.this);
                    break;
                case R.id.awesomeqr_main_removeBackgroundImage:
                    backgroundImage=null;
                    imgPathTextView.setVisibility(View.GONE);
                    log.t(getActivity(),getResources().getString(R.string.Background_image_removed));
                    break;
                case R.id.awesomeqr_main_generate:
                    generate(mengEtContents.getString(),
							 Integer.parseInt(mengEtSize.getString()),
							 Integer.parseInt(mengEtMargin.getString()),
							 Float.parseFloat(mengEtDotScale.getString()),
							 mColorBar.getTrueColor(),
							 ckbAutoColor.isChecked()?Color.WHITE :mColorBar.getFalseColor(), 
							 backgroundImage,
							 ckbWhiteMargin.isChecked(),
							 ckbAutoColor.isChecked(),
							 ckbBinarize.isChecked(),
							 Integer.parseInt(mengEtBinarize.getString())
							 );
                    btnSave.setVisibility(View.VISIBLE);
                    break;
                case R.id.awesomeqr_mainButton_save:
                    try{
                        String s = QRCode.saveMyBitmap(Environment.getExternalStorageDirectory().getAbsolutePath()+"/Pictures/QRcode/AwesomeQR"+SystemClock.elapsedRealtime()+".png",bmpQRcode);
                        log.t(getActivity(),"已保存至"+s);
                        getActivity().getApplicationContext().sendBroadcast(new Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE,Uri.fromFile(new File(s))));//更新图库
                    }catch(IOException e){
                        log.e(getActivity(),e);
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
            Uri uri=data.getData();
            String path=ContentHelper.absolutePathFromUri(getActivity().getApplicationContext(),uri);
            imgPathTextView.setText("当前文件："+path);
            Bitmap bmp=BitmapFactory.decodeFile(path);
            cropPhoto(Math.min(bmp.getWidth(),bmp.getHeight()),path);
        }else if(requestCode==CROP_REQUEST_CODE){
            Bundle bundle = data.getExtras();
            if(bundle!=null){
                backgroundImage=bundle.getParcelable("data");
                log.t(getActivity(),getResources().getString(R.string.Background_image_added));
            }else{
                log.t(getActivity(),"裁剪失败");
            }
        }else if(resultCode==getActivity().RESULT_CANCELED){
            Toast.makeText(getActivity().getApplicationContext(),"取消选择图片",Toast.LENGTH_SHORT).show();
        }else{
            MainActivity2.selectImage(testFragment.this);
        }
        super.onActivityResult(requestCode,resultCode,data);
    }

    private void generate(final String contents,final int size,final int margin,final float dotScale,
                          final int colorDark,final int colorLight,final Bitmap background,final boolean whiteMargin,
                          final boolean autoColor,final boolean binarize,final int binarizeThreshold){
        if(generating) return;
        generating=true;
        new Thread(new Runnable() {
				@Override
				public void run(){
					try{
						final Bitmap b = AwesomeQRCode.create(contents,size,margin,dotScale,colorDark,colorLight,background,whiteMargin,autoColor,binarize,binarizeThreshold);
						getActivity().runOnUiThread(new Runnable() {
								@Override
								public void run(){
									qrCodeImageView.setImageBitmap(b);
									bmpQRcode=b;
									scrollView.post(new Runnable() {
											@Override
											public void run(){
												scrollView.fullScroll(View.FOCUS_DOWN);
											}
										});
									generating=false;
								}
							});
					}catch(Exception e){
						log.e(getActivity(),e);
						generating=false;
					}
				}
			}).start();
    }

    private void cropPhoto(final int size, final String path){
        EditText et=new EditText(getActivity());
        et.setHint("0<大小<"+(size+1));
        new AlertDialog.Builder(getActivity())
                .setTitle("输入要添加的二维码大小(像素)")
                .setView(et)
                .setPositiveButton("确定",new DialogInterface.OnClickListener(){
                    @Override
                    public void onClick(DialogInterface p1,int p2){
                        Intent i=new Intent(getActivity(),sizeSelect.class);
                        i.putExtra("content",path);
                        i.putExtra("size",size);
                        getActivity().startActivity(i);
                    }}).show();
    }

}
