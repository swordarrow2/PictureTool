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
import com.meng.qrtools.*;
import java.io.*;

public class logoCreator extends Fragment{
	ImageView qrcode5;
	EditText et;
	Button btnImg;
	Button btnCreate;
	Button btnSave;
	Bitmap bmp=null;
	private Bitmap backgroundImage = null;
	private final int SELECT_FILE_REQUEST_CODE = 822;
	@Override
	public View onCreateView(LayoutInflater inflater,ViewGroup container,Bundle savedInstanceState){
		// TODO: Implement this method
		return inflater.inflate(R.layout.qr_logo_main,container,false);
	}

	@Override
	public void onViewCreated(View view,Bundle savedInstanceState){
		// TODO: Implement this method
		super.onViewCreated(view,savedInstanceState);
		qrcode5=(ImageView)view. findViewById(R.id.qrcode5);	
		et=(EditText)view.findViewById(R.id.qr_logo_mainEditText);
		btnImg=(Button)view.findViewById(R.id.qr_logo_mainButtonImage);
		btnCreate=(Button)view.findViewById(R.id.qr_logo_mainButtonCreate);
		btnSave=(Button)view.findViewById(R.id.qr_logo_mainButtonSave);
		backgroundImage=BitmapFactory.decodeResource(getActivity().getResources(),R.drawable.ic_launcher);
		btnImg.setOnClickListener(new OnClickListener(){

				@Override
				public void onClick(View p1){
					// TODO: Implement this method
					selectImage();
				}
			});		

		btnCreate.setOnClickListener(new OnClickListener(){

				@Override
				public void onClick(View p1){
					// TODO: Implement this method
					bmp=QRCode.createLogoQR(
						0xff37b19e,
						0xffffffff,
						et.getText().toString()==null||et.getText().toString().equals("")?getActivity().getResources().getString(R.string.Makito_loves_Kafuu_Chino):et.getText().toString(),
						500, 
						backgroundImage);
					qrcode5.setImageBitmap(bmp);
					btnSave.setVisibility(View.VISIBLE);
				}
			});		
		btnSave.setOnClickListener(new OnClickListener(){

				@Override
				public void onClick(View p1){
					// TODO: Implement this method
					try{
						String s=QRCode.saveMyBitmap(Environment.getExternalStorageDirectory().getAbsolutePath()+"/Pictures/QRcode/LogoQR"+SystemClock.elapsedRealtime()+".png",bmp);
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
	@Override
	public void onActivityResult(int requestCode,int resultCode,Intent data){

		if(requestCode==SELECT_FILE_REQUEST_CODE&&resultCode==getActivity().RESULT_OK&&data.getData()!=null){
			try{
				Uri imageUri = data.getData();
				String path=ContentHelper.absolutePathFromUri(getActivity().getApplicationContext(),imageUri);
				btnImg.setText(btnImg.getText()+" 当前："+path);
				backgroundImage=BitmapFactory.decodeFile(path);	
			}catch(Exception e){
				e.printStackTrace();
			}
		}else if(resultCode==getActivity().RESULT_CANCELED){
			Toast.makeText(getActivity().getApplicationContext(),"用户取消了操作",Toast.LENGTH_SHORT).show();
		}else{
			selectImage();
		}
		super.onActivityResult(requestCode,resultCode,data);
	}
}
