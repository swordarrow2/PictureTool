package com.meng.qrtools.creator;

import android.app.*;
import android.content.*;
import android.graphics.*;
import android.graphics.drawable.*;
import android.net.*;
import android.os.*;
import android.view.*;
import android.view.View.*;
import android.widget.*;
import com.google.zxing.*;
import com.meng.qrtools.*;
import com.meng.qrtools.views.*;
import java.io.*;
import android.text.*;

public class logoCreator extends Fragment{
	private ScrollView scrollView;
    private ImageView qrcodeImageView;
    private mengEdittext et;
    private Button btnSelectImg;
    private Button btnRemoveImg;
    private Button btnCreate;
    private TextView imgPath;
    private Button btnSave;
    private Bitmap bmp = null;
    private Bitmap logoImage = null;
    private mengEdittext etColorLight, etColorDark;
    private LinearLayout selectColorLinearLayout;
    private CheckBox ckbAutoColor;
    private final int SELECT_FILE_REQUEST_CODE = 822;

    @Override
    public View onCreateView(LayoutInflater inflater,ViewGroup container,Bundle savedInstanceState){
        // TODO: Implement this method
        return inflater.inflate(R.layout.qr_main,container,false);
    }

    @Override
    public void onViewCreated(View view,Bundle savedInstanceState){
        // TODO: Implement this method
        super.onViewCreated(view,savedInstanceState);
        qrcodeImageView=(ImageView) view.findViewById(R.id.qr_imageview);
        et=(mengEdittext) view.findViewById(R.id.qr_EditText);
		scrollView=(ScrollView)view.findViewById(R.id.qr_mainScrollView);
        ckbAutoColor=(CheckBox) view.findViewById(R.id.qr_main_autoColor);
        btnSelectImg=(Button) view.findViewById(R.id.qr_ButtonSelectImage);
        btnRemoveImg=(Button)view.findViewById(R.id.qr_ButtonRemoveImage);
        btnCreate=(Button) view.findViewById(R.id.qr_ButtonCreate);
        btnSave=(Button) view.findViewById(R.id.qr_ButtonSave);
        imgPath=(TextView)view.findViewById(R.id.qr_main_imgPathTextView);
        etColorLight=(mengEdittext) view.findViewById(R.id.qr_main_colorLight);
        etColorDark=(mengEdittext) view.findViewById(R.id.qr_main_colorDark);
        selectColorLinearLayout=(LinearLayout) view.findViewById(R.id.qr_main_select_color_linearLayout);
		etColorDark.addTextChangedListener(tw);
        etColorLight.addTextChangedListener(tw);
        btnSelectImg.setOnClickListener(new OnClickListener() {

				@Override
				public void onClick(View p1){
					// TODO: Implement this method
					selectImage();
				}
			});
        btnRemoveImg.setOnClickListener(new OnClickListener() {
				@Override
				public void onClick(View v){
					logoImage=null;
					imgPath.setText("未选择图片，将会生成普通二维码");
				}
			});
        ckbAutoColor.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
				@Override
				public void onCheckedChanged(CompoundButton buttonView,boolean isChecked){
					etColorLight.setEnabled(!isChecked);
					etColorDark.setEnabled(!isChecked);
					selectColorLinearLayout.setVisibility(isChecked? View.GONE :View.VISIBLE);
				}
			});
        btnCreate.setOnClickListener(new OnClickListener() {

				@Override
				public void onClick(View p1){
					// TODO: Implement this method
					if(logoImage==null){
						bmp=QRCode.createQRCode(
                            et.getString(),
                            ckbAutoColor.isChecked()? Color.BLACK :Color.parseColor(etColorDark.getString()),
                            ckbAutoColor.isChecked()? Color.WHITE :Color.parseColor(etColorLight.getString()),
                            BarcodeFormat.QR_CODE,
                            500);
					}else{
						bmp=QRCode.createLogoQR(
                            et.getString().toString()==null||et.getString().toString().equals("")? getActivity().getResources().getString(R.string.input_text) :et.getString().toString(),
                            ckbAutoColor.isChecked()? Color.BLACK :Color.parseColor(etColorDark.getString()),
                            ckbAutoColor.isChecked()? Color.WHITE :Color.parseColor(etColorLight.getString()),
                            500,
                            logoImage);
					}
					scrollView.post(new Runnable() {
							@Override
							public void run(){
								scrollView.fullScroll(View.FOCUS_DOWN);
							}
						});
					qrcodeImageView.setImageBitmap(bmp);
					btnSave.setVisibility(View.VISIBLE);
				}
			});
        btnSave.setOnClickListener(new OnClickListener() {

				@Override
				public void onClick(View p1){
					// TODO: Implement this method
					try{
						String s = QRCode.saveMyBitmap(Environment.getExternalStorageDirectory().getAbsolutePath()+"/Pictures/QRcode/LogoQR"+SystemClock.elapsedRealtime()+".png",bmp);
						Toast.makeText(getActivity().getApplicationContext(),"已保存至"+s,Toast.LENGTH_LONG).show();
						getActivity().getApplicationContext().sendBroadcast(new Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE,Uri.fromFile(new File(s))));//更新图库
					}catch(IOException e){
						Toast.makeText(getActivity().getApplicationContext(),e.getMessage(),Toast.LENGTH_LONG).show();
					}
				}
			});
    }

    public static Bitmap drawableToBitmap(Drawable drawable){
        if(drawable instanceof BitmapDrawable){
            return ((BitmapDrawable) drawable).getBitmap();
        }
        Bitmap bitmap = Bitmap.createBitmap(drawable.getIntrinsicWidth(),drawable.getIntrinsicHeight(),Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas(bitmap);
        drawable.setBounds(0,0,canvas.getWidth(),canvas.getHeight());
        drawable.draw(canvas);

        return bitmap;
    }

    private void selectImage(){
        Intent intent = new Intent(Intent.ACTION_OPEN_DOCUMENT);
        intent.addCategory(Intent.CATEGORY_OPENABLE);
        intent.setType("image/*");
        startActivityForResult(intent,SELECT_FILE_REQUEST_CODE);
    }
	TextWatcher tw = new TextWatcher() {

        @Override
        public void beforeTextChanged(CharSequence p1, int p2, int p3, int p4) {
        }

        @Override
        public void onTextChanged(CharSequence p1, int p2, int p3, int p4) {
            try {
                etColorLight.setTextColor(Color.parseColor(etColorLight.getString()));
            } catch (Exception e) {
                etColorLight.setTextColor(Color.BLACK);
            }
            try {
                etColorDark.setTextColor(Color.parseColor(etColorDark.getString()));
            } catch (Exception e) {
                etColorDark.setTextColor(Color.BLACK);
            }
        }

        @Override
        public void afterTextChanged(Editable p1) {

        }
    };
    @Override
    public void onActivityResult(int requestCode,int resultCode,Intent data){

        if(requestCode==SELECT_FILE_REQUEST_CODE&&resultCode==getActivity().RESULT_OK&&data.getData()!=null){
            try{
                Uri imageUri = data.getData();
                String path = ContentHelper.absolutePathFromUri(getActivity().getApplicationContext(),imageUri);
                imgPath.setText("当前："+path);
                logoImage=BitmapFactory.decodeFile(path);
            }catch(Exception e){
                log.e(getActivity(),e);
            }
        }else if(resultCode==getActivity().RESULT_CANCELED){
            Toast.makeText(getActivity().getApplicationContext(),"用户取消了操作",Toast.LENGTH_SHORT).show();
        }else{
            selectImage();
        }
        super.onActivityResult(requestCode,resultCode,data);
    }
}
