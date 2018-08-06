package com.example.androidndkgif;

import android.app.*;
import android.content.res.*;
import android.graphics.*;
import android.os.*;
import android.view.*;
import android.view.View.*;
import android.widget.*;
import com.meng.qrtools.*;
import com.waynejo.androidndkgif.*;
import java.io.*;
import android.content.*;
import android.net.*;
import com.meng.qrtools.creator.*;

public class ExampleActivity extends Fragment{

    private boolean useDither = true;
    private ImageView imageView;
	Button decode_gif_btn
	,btn_selectImg
	,decode_gif_using_iterator_btn
	,encode_gif_btn
	,gif_qr_mainButton;
	private final int SELECT_FILE_REQUEST_CODE = 8212;
	
	@Override
	public View onCreateView(LayoutInflater inflater,ViewGroup container,Bundle savedInstanceState){
		// TODO: Implement this method
		return inflater.inflate(R.layout.gif_qr_main,container,false);
	}

	@Override
	public void onViewCreated(View view,Bundle savedInstanceState){
		// TODO: Implement this method
		super.onViewCreated(view,savedInstanceState);
        imageView=(ImageView)view. findViewById(R.id.image_view);
		btn_selectImg=(Button)view.findViewById(R.id.gif_qr_mainButton_selectImg);
		decode_gif_btn=(Button)view.findViewById(R.id.decode_gif_btn);
		decode_gif_using_iterator_btn=(Button)view.findViewById(R.id.decode_gif_using_iterator_btn);
		encode_gif_btn=(Button)view.findViewById(R.id.encode_gif_btn);
		gif_qr_mainButton=(Button)view.findViewById(R.id.gif_qr_mainButton);

		
		btn_selectImg.setOnClickListener(new OnClickListener(){

				@Override
				public void onClick(View p1){
					// TODO: Implement this method
					Intent intent = new Intent(Intent.ACTION_OPEN_DOCUMENT);
					intent.addCategory(Intent.CATEGORY_OPENABLE);
					intent.setType("image/*");
					startActivityForResult(intent,SELECT_FILE_REQUEST_CODE);
					
				}
			});
		decode_gif_btn.setOnClickListener(new OnClickListener(){

				@Override
				public void onClick(View p1){
					// TODO: Implement this method
					onDecodeGIF();
				}
			});
		decode_gif_using_iterator_btn.setOnClickListener(new OnClickListener(){

				@Override
				public void onClick(View p1){
					// TODO: Implement this method
					onDecodeGIFUsingIterator();
				}
			});
		encode_gif_btn.setOnClickListener(new OnClickListener(){

				@Override
				public void onClick(View p1){
					// TODO: Implement this method
					onEncodeGIF();
				}
			});
		gif_qr_mainButton.setOnClickListener(new OnClickListener(){

				@Override
				public void onClick(View p1){
					// TODO: Implement this method
					onDisableDithering();
				}
			});
    }

    

    

    public void onDecodeGIF(){
        new Thread(new Runnable() {
				@Override
				public void run(){
					String destFile="";// = setupSampleFile();

					final GifDecoder gifDecoder = new GifDecoder();
					final boolean isSucceeded = gifDecoder.load(destFile);
					getActivity().runOnUiThread(new Runnable() {
							int idx = 0;
							@Override
							public void run(){
								if(isSucceeded){
									Bitmap bitmap = gifDecoder.frame(idx);
									imageView.setImageBitmap(bitmap);
									if(idx+1<gifDecoder.frameNum()){
										imageView.postDelayed(this,gifDecoder.delay(idx));
									}
									++idx;
								}else{
									log.t("Failed");
								}
							}
						});
				}
			}).start();
    }

    public void onDecodeGIFUsingIterator(){
        new Thread(new Runnable() {
				@Override
				public void run(){
					String destFile = "";//setupSampleFile();

					final GifDecoder gifDecoder = new GifDecoder();
					final GifImageIterator iterator = gifDecoder.loadUsingIterator(destFile);
					getActivity().runOnUiThread(new Runnable() {
							@Override
							public void run(){
								if(iterator.hasNext()){
									GifImage next = iterator.next();
									if(null!=next){
										imageView.setImageBitmap(next.bitmap);
										imageView.postDelayed(this,next.delayMs);
									}else{
										log.t("Failed");
									}
								}else{
									iterator.close();
								}
							}
						});
				}
			}).start();
    }

    public void onEncodeGIF(){
        new Thread(new Runnable() {
				@Override
				public void run(){
					try{
						encodeGIF();
					}catch(FileNotFoundException e){
						e.printStackTrace();
					}catch(IOException e){
						e.printStackTrace();
					}
				}
			}).start();
    }

    private void encodeGIF() throws IOException{
        String dstFile = "result.gif";
       // final String filePath = Environment.getExternalStorageDirectory()+File.separator+dstFile;
		final String filePath=Environment.getExternalStorageDirectory().getAbsolutePath()+"/Pictures/QRcode/AwesomeQR"+SystemClock.elapsedRealtime()+dstFile;
        int width = 500;
        int height = 500;
        int delayMs = 50;

        GifEncoder gifEncoder = new GifEncoder();
        gifEncoder.init(width,height,filePath,GifEncoder.EncodingType.ENCODING_TYPE_NORMAL_LOW_MEMORY);
        gifEncoder.setDither(useDither);
        Bitmap bitmap = Bitmap.createBitmap(width,height,Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas(bitmap);
        Paint p = new Paint();
        int[] colors = new int[] {0xFF000000, 0xFFFFFFFF};
        for(int color : colors){
            p.setColor(color);
            canvas.drawRect(0,0,width,height,p);
            gifEncoder.encodeFrame(bitmap,delayMs);
        }
        gifEncoder.close();

		getActivity().runOnUiThread(new Runnable() {
				@Override
				public void run(){
					log.t("done : "+filePath);
				}
			});
    }

    public void onDisableDithering(){
        useDither=false;
    }

	@Override
	public void onActivityResult(int requestCode,int resultCode,Intent data){
		// TODO: Implement this method
		if(requestCode==SELECT_FILE_REQUEST_CODE&&resultCode==getActivity().RESULT_OK&&data.getData()!=null){
            try{
                Uri imageUri = data.getData();
                final String path = ContentHelper.absolutePathFromUri(getActivity().getApplicationContext(),imageUri);
				new Thread(new Runnable() {
						@Override
						public void run(){
							String destFile = path;

							final GifDecoder gifDecoder = new GifDecoder();
							final boolean isSucceeded = gifDecoder.load(destFile);
							getActivity().runOnUiThread(new Runnable() {
									int idx = 0;
									@Override
									public void run(){
										
										if(isSucceeded){
											Bitmap bitmap = gifDecoder.frame(idx);
											imageView.setImageBitmap(bitmap);
											
											if(idx+1<gifDecoder.frameNum()){
												imageView.postDelayed(this,gifDecoder.delay(idx));
												++idx;
											}else{
												idx=0;
											
											
											}
										}else{
											log.t("Failed");
										}
									}
								});
						}
					}).start();
            }catch(Exception e){
                log.e(e);
            }
        }else if(resultCode==getActivity().RESULT_CANCELED){
            Toast.makeText(getActivity().getApplicationContext(),"用户取消了操作",Toast.LENGTH_SHORT).show();
        }else{
          //  selectImage();
        }
		super.onActivityResult(requestCode,resultCode,data);
	}
	
}
