package com.meng.qrtools.creator;

import android.*;
import android.app.*;
import android.content.*;
import android.content.pm.*;
import android.graphics.*;
import android.net.*;
import android.os.*;
import android.text.*;
import android.view.*;
import android.view.View.*;
import android.widget.*;
import com.meng.*;
import com.meng.qrtools.*;
import com.meng.qrtools.lib.*;
import com.meng.qrtools.lib.qrcodelib.*;
import java.io.*;

import com.meng.qrtools.R;

public class pictureEncry extends Fragment{
    private final int REQUEST_PERMISSION_PHOTO = 1001;
    private Button btnOpenGallery;
	private ImageView imageView;
	private Bitmap encryBitmap;
	private Button btnSave;

    @Override
    public View onCreateView(LayoutInflater inflater,ViewGroup container,Bundle savedInstanceState){
        return inflater.inflate(R.layout.picture_encry_decry,container,false);
	  }

    @Override
    public void onViewCreated(View view,Bundle savedInstanceState){
        super.onViewCreated(view,savedInstanceState);
        btnOpenGallery=(Button) view.findViewById(R.id.read_galleryButton);
        imageView=(ImageView)view.findViewById(R.id.qr_imageview);
		btnSave=(Button) view.findViewById(R.id.qr_ButtonSave);
        btnOpenGallery.setOnClickListener(new OnClickListener() {
			  @Override
			  public void onClick(View v){
				  openGallery();
				}
			});
		btnSave.setOnClickListener(new OnClickListener(){

			  @Override
			  public void onClick(View p1){
				  try{
					  String s=QrUtils.saveMyBitmap(MainActivity.instence.getBarcodePath("bus"),encryBitmap);
					  getActivity().getApplicationContext().sendBroadcast(new Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE,Uri.fromFile(new File(s))));//更新图库
					}catch(IOException e){
					  log.t(e.toString());
					}
				}
			});
	  }

	private void createBitmap(String path){
        encryBitmap=QrUtils.encryBitmap(BitmapFactory.decodeFile(path));      
        imageView.setImageBitmap(encryBitmap);
		btnSave.setVisibility(View.VISIBLE);

	  }

    @Override
    public void onActivityResult(int requestCode,int resultCode,Intent data){
        if(resultCode==getActivity().RESULT_OK&&data!=null&&requestCode==MainActivity2.SELECT_FILE_REQUEST_CODE){
            Uri inputUri = data.getData();
            String path = ContentHelper.absolutePathFromUri(getActivity(),inputUri);
            if(!TextUtils.isEmpty(path)){     
				createBitmap(path);
			  }else{
                Toast.makeText(getActivity().getApplicationContext(),"图片路径未找到",Toast.LENGTH_SHORT).show();
			  }
		  }
	  }

    @Override
    public void onRequestPermissionsResult(int requestCode,String[] permissions,int[] grantResults){
        super.onRequestPermissionsResult(requestCode,permissions,grantResults);
        if(grantResults.length>0&&requestCode==REQUEST_PERMISSION_PHOTO){
            if(grantResults[0]!=PackageManager.PERMISSION_GRANTED){
                new AlertDialog.Builder(getActivity())
				  .setTitle("提示")
				  .setMessage("请在系统设置中为App中开启文件权限后重试")
				  .setPositiveButton("确定",null)
				  .show();
			  }else{
                MainActivity2.selectImage(this);
			  }
		  }
	  }

    public void openGallery(){
        if(Build.VERSION.SDK_INT>=Build.VERSION_CODES.M
		   &&getActivity().checkSelfPermission(Manifest.permission.READ_EXTERNAL_STORAGE)
		   !=PackageManager.PERMISSION_GRANTED){
            requestPermissions(new String[]{Manifest.permission.READ_EXTERNAL_STORAGE},
							   REQUEST_PERMISSION_PHOTO);
		  }else{
            MainActivity2.selectImage(this);
		  }
	  }

  }
