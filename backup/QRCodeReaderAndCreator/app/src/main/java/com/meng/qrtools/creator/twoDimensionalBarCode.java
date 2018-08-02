package com.meng.qrtools.creator;

import android.app.Fragment;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Spinner;
import android.widget.Toast;

import com.meng.qrtools.R;
import com.meng.qrtools.lib.*;
import com.google.zxing.*;
import android.widget.*;
import android.graphics.*;
import android.view.View.*;
import android.os.*;
import java.io.*;
import android.content.*;
import android.net.*;
import com.meng.qrtools.*;

/**
 * Created by Administrator on 2018/8/2.
 */

public class twoDimensionalBarCode extends Fragment{
    Spinner spinner;
	ImageView qrcode1;
	EditText et;
	Button btn;
	Button btnSave;
	private Bitmap b;
	BarcodeFormat bf;

    public View onCreateView(LayoutInflater inflater,ViewGroup container,Bundle savedInstanceState){
        return inflater.inflate(R.layout.two_dimensional_bar_code,container,false);
    }

    @Override
    public void onViewCreated(View view,Bundle savedInstanceState){
        super.onViewCreated(view,savedInstanceState);
        spinner=(Spinner) view.findViewById(R.id.two_dimensional_bar_codeSpinner);
		qrcode1=(ImageView)view. findViewById(R.id.two_dimensional_bar_codeImageView);
		et=(EditText)view.findViewById(R.id.two_dimensional_bar_codeEditText);
		btn=(Button)view.findViewById(R.id.two_dimensional_bar_codeButton);
		btnSave=(Button)view.findViewById(R.id.two_dimensional_bar_codeButtonSave);

        String[] mItems = getResources().getStringArray(R.array.code_type);
        ArrayAdapter<String> adapter = new ArrayAdapter<String>(getActivity().getApplicationContext(),R.layout.activity_tipsprice_spinner,mItems);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinner.setAdapter(adapter);
        spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
				@Override
				public void onItemSelected(AdapterView<?> parent,View view,int pos,long id){
					switch(pos){
						case 0:
							bf=BarcodeFormat.AZTEC;
							break;
						case 1:
							bf=BarcodeFormat.DATA_MATRIX;
							break;
						case 2:
							bf=BarcodeFormat.PDF_417;
							break;
					}
				}

				@Override
				public void onNothingSelected(AdapterView<?> parent){
					// Another interface callback
				}
			});

		btn.setOnClickListener(new OnClickListener(){	

				@Override
				public void onClick(View p1){
					// TODO: Implement this method
					b=QRCode.createQRCode(
						tools.stringToUnicode(
							et.getText().toString()==null||et.getText().toString().equals("")?
							et.getHint().toString():
							et.getText().toString()
						),bf);
					qrcode1.setImageBitmap(b);
					btnSave.setVisibility(View.VISIBLE);
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
						log.t(e);
					}
				}
			});
    }
}
