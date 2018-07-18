package com.meng.qrtools;

import android.app.*;
import android.content.*;
import android.os.*;
import com.meng.mbrowser.tools.*;
import android.widget.*;
import android.view.View.*;
import android.view.*;
import com.meng.qrtools.reader.*;
import com.meng.qrtools.creator.*;

public class MainActivity extends Activity 
{
	Button btnReadGallery,btnScan,btnCreate,btnCreateAwesome;
    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
		ExceptionCatcher.getInstance().init(this);
        setContentView(R.layout.main);
		btnReadGallery=(Button)findViewById(R.id.mainButton_readGallery);
		btnScan=(Button)findViewById(R.id.mainButton_scan);
		btnCreate=(Button)findViewById(R.id.mainButton_createQR);
		btnCreateAwesome=(Button)findViewById(R.id.mainButton_createAwesomeQR);
		
		btnReadGallery.setOnClickListener(new OnClickListener(){

				@Override
				public void onClick(View p1){
					// TODO: Implement this method
					Intent i=new Intent(MainActivity.this,galleryReader.class);
					startActivity(i);
					finish();
				}
			});
		btnScan.setOnClickListener(new OnClickListener(){

				@Override
				public void onClick(View p1){
					// TODO: Implement this method
					Intent i=new Intent(MainActivity.this,cameraReader.class);
					startActivity(i);
					finish();
				}
			});
			
		btnCreate.setOnClickListener(new OnClickListener(){

				@Override
				public void onClick(View p1){
					// TODO: Implement this method
					Intent i=new Intent(MainActivity.this,creator.class);
					startActivity(i);
					finish();
				}
			});
		btnCreateAwesome.setOnClickListener(new OnClickListener(){

				@Override
				public void onClick(View p1){
					// TODO: Implement this method
					Intent i=new Intent(MainActivity.this,awesomeCreator.class);
					startActivity(i);
					finish();
				}
			});
			
		
    }
}
