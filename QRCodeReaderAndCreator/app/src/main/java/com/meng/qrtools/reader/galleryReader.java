package com.meng.qrtools.reader;

import android.*;
import android.app.*;
import android.content.*;
import android.content.pm.*;
import android.database.*;
import android.graphics.*;
import android.net.*;
import android.os.*;
import android.provider.*;
import android.text.*;
import android.webkit.*;
import android.widget.*;
import com.google.zxing.*;
import com.meng.qrtools.reader.qrcodelib.common.*;

public class galleryReader extends Activity{
	private final int REQUEST_PERMISSION_PHOTO = 1001;
	private AlertDialog mDialog;

	@Override
	protected void onCreate(Bundle savedInstanceState){
		// TODO: Implement this method
		super.onCreate(savedInstanceState);
		setContentView(new LinearLayout(this));
		openGallery();
	}

	public void handleDecode(Result result,Bitmap barcode){
        String resultString = result.getText();
        handleResult(resultString);
    }

    protected void handleResult(final String resultString){
        if(resultString.equals("")){
            Toast.makeText(galleryReader.this,com.meng.qrtools.R.string.scan_failed,Toast.LENGTH_SHORT).show();
        }else{
			if(mDialog==null){
                mDialog=new AlertDialog.Builder(this)
				.setMessage(resultString).setNegativeButton("确定",null)
					.setNeutralButton("复制文本",new DialogInterface.OnClickListener() {

						@Override
						public void onClick(DialogInterface p1,int p2){
							// TODO: Implement this method
							android.content.ClipboardManager clipboardManager = (android.content.ClipboardManager) getSystemService(Context.CLIPBOARD_SERVICE);
							ClipData clipData = ClipData.newPlainText("text",resultString);
							clipboardManager.setPrimaryClip(clipData);
							finish();
						}
					}).create();
            }
			mDialog.show();

			/*       Intent resultIntent = new Intent();
			 Bundle bundle = new Bundle();
			 bundle.putString("result", resultString);
			 resultIntent.putExtras(bundle);
			 this.setResult(RESULT_OK, resultIntent); */
        }
    }
	@Override
    protected void onActivityResult(int requestCode,int resultCode,Intent data){
        super.onActivityResult(requestCode,resultCode,data);
        if(resultCode==RESULT_OK
		   &&data!=null
		   &&requestCode==ActionUtils.PHOTO_REQUEST_GALLERY){
            Uri inputUri = data.getData();
            String path = null;

            if(URLUtil.isFileUrl(inputUri.toString())){
                // 小米手机直接返回的文件路径
                path=inputUri.getPath();
            }else{
                String[] proj = {MediaStore.Images.Media.DATA};
                Cursor cursor = getContentResolver().query(inputUri,proj,null,null,null);
                if(cursor!=null&&cursor.moveToFirst()){
                    path=cursor.getString(cursor.getColumnIndex(MediaStore.MediaColumns.DATA));
                }
            }
            if(!TextUtils.isEmpty(path)){
                Result result = QrUtils.decodeImage(path);
                if(result!=null){
                    handleDecode(result,null);
                }else{
                    new AlertDialog.Builder(galleryReader.this)
						.setTitle("提示")
						.setMessage("此图片无法识别")
						.setPositiveButton("确定",null)
						.show();
                }
            }else{
				Toast.makeText(galleryReader.this,"图片路径未找到",Toast.LENGTH_SHORT).show();
            }
        }
    }
	@Override
    public void onRequestPermissionsResult(int requestCode,String[] permissions,int[] grantResults){
        super.onRequestPermissionsResult(requestCode,permissions,grantResults);
        if(grantResults.length>0&&requestCode==REQUEST_PERMISSION_PHOTO){
            if(grantResults[0]!=PackageManager.PERMISSION_GRANTED){
                new AlertDialog.Builder(galleryReader.this)
					.setTitle("提示")
					.setMessage("请在系统设置中为App中开启文件权限后重试")
					.setPositiveButton("确定",null)
					.show();
            }else{
                ActionUtils.startActivityForGallery(galleryReader.this,ActionUtils.PHOTO_REQUEST_GALLERY);
            }
        }
    }
	public void openGallery(){
        if(Build.VERSION.SDK_INT>=Build.VERSION_CODES.M
		   &&checkSelfPermission(Manifest.permission.READ_EXTERNAL_STORAGE)
		   !=PackageManager.PERMISSION_GRANTED){
            requestPermissions(new String[]{Manifest.permission.READ_EXTERNAL_STORAGE},
							   REQUEST_PERMISSION_PHOTO);
        }else{
            ActionUtils.startActivityForGallery(galleryReader.this,ActionUtils.PHOTO_REQUEST_GALLERY);
        }
    }
}
