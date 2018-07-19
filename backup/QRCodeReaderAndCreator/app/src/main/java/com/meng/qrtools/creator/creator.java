package com.meng.qrtools.creator;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.*;

import android.widget.ImageView;

import com.meng.qrtools.R;
import android.app.*;
import android.view.*;
import android.widget.Toast;
import android.widget.*;
import android.view.View.*;
import java.io.*;

public class creator extends Fragment{
	ImageView qrcode1;
	EditText et;
	Button btn;
	Button btnSave;
	private Bitmap b;
	@Override
	public View onCreateView(LayoutInflater inflater,ViewGroup container,Bundle savedInstanceState){
		// TODO: Implement this method
		return inflater.inflate(R.layout.qr_main,container,false);
	}

	@Override
	public void onViewCreated(View view,Bundle savedInstanceState){
		// TODO: Implement this method
		super.onViewCreated(view,savedInstanceState);
		qrcode1=(ImageView)view. findViewById(R.id.qrcode5);
		et=(EditText)view.findViewById(R.id.qr_mainEditText);
		btn=(Button)view.findViewById(R.id.qr_mainButton);
		btnSave=(Button)view.findViewById(R.id.qr_mainButtonSave);
		btn.setOnClickListener(new OnClickListener(){	

				@Override
				public void onClick(View p1){
					// TODO: Implement this method
					 b=QRCode.createQRCode(et.getText().toString()==null||et.getText().toString().equals("")?et.getHint().toString():et.getText().toString());
					qrcode1.setImageBitmap(b);
					
				}
			});		
		btnSave.setOnClickListener(new OnClickListener(){

				@Override
				public void onClick(View p1){
					// TODO: Implement this method
					try{
						String s= QRCode.saveMyBitmap(Environment.getExternalStorageDirectory().getAbsolutePath()+"/Pictures/QRcode/QR"+SystemClock.elapsedRealtime()+".png",b);
						Toast.makeText(getActivity().getApplicationContext(),"已保存至"+s,Toast.LENGTH_LONG).show();
						getActivity().getApplicationContext().sendBroadcast(new Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE,Uri.fromFile(new File(s))));//更新图库
					}catch(IOException e){
						Toast.makeText(getActivity().getApplicationContext(),e.getMessage(),Toast.LENGTH_LONG).show();
					}
				}
			});
	}




}
