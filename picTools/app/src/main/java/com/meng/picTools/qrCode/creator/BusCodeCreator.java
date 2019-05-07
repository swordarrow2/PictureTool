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
import com.meng.picTools.MainActivity;
import com.meng.picTools.R;
import com.meng.picTools.qrCode.qrcodelib.*;
import com.meng.picTools.lib.mengViews.*;

import java.io.*;

public class BusCodeCreator extends Fragment{
    private ScrollView scrollView;
    private ImageView qrcodeImageView;
    private MengEditText mengEtContent;
    private Button btnSave;
    private Bitmap bmpQRcode = null;

    @Override
    public View onCreateView(LayoutInflater inflater,ViewGroup container,Bundle savedInstanceState){
        return inflater.inflate(R.layout.bus,container,false);
	  }

    @Override
    public void onViewCreated(View view,Bundle savedInstanceState){
        super.onViewCreated(view,savedInstanceState);

        qrcodeImageView=(ImageView) view.findViewById(R.id.qr_imageview);
        mengEtContent=(MengEditText) view.findViewById(R.id.qr_mengEditText_content);
        scrollView=(ScrollView) view.findViewById(R.id.qr_mainScrollView);

        btnSave=(Button) view.findViewById(R.id.qr_ButtonSave);
        ((Button) view.findViewById(R.id.qr_ButtonCreate)).setOnClickListener(click);
        btnSave.setOnClickListener(click);

	  }

    OnClickListener click = new OnClickListener() {
        @Override
        public void onClick(View v){
            switch(v.getId()){
                case R.id.qr_ButtonCreate:
				  createBarcode();
				  btnSave.setVisibility(View.VISIBLE);
				  break;
                case R.id.qr_ButtonSave:
				  try{
					  String s = QrUtils.saveMyBitmap(MainActivity.instence.getBarcodePath("bus"),bmpQRcode);
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
        bmpQRcode=
		  QrUtils.encryBitmap(
		  QrUtils.createBarcode(
			mengEtContent.getString(),
			BarcodeFormat.PDF_417,
			Color.BLACK,
			Color.WHITE,
			500,
			null));
        scrollView.post(new Runnable() {
			  @Override
			  public void run(){
				  scrollView.fullScroll(View.FOCUS_DOWN);
				}
			});
        qrcodeImageView.setImageBitmap(bmpQRcode);
	  }

  }
