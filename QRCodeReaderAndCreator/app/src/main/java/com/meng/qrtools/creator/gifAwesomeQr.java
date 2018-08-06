package com.meng.qrtools.creator;

import android.app.Fragment;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.os.SystemClock;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.meng.qrtools.R;
import com.meng.qrtools.log;
import com.waynejo.androidndkgif.GifDecoder;
import com.waynejo.androidndkgif.GifEncoder;
import com.waynejo.androidndkgif.GifImage;
import com.waynejo.androidndkgif.GifImageIterator;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import android.widget.*;

public class gifAwesomeQr extends Fragment{
	ProgressBar pb;
	EditText contentEt;

	EditText dark,light;
    private boolean useDither = true;
    private ImageView imageView;
    private Button btnSelectImg, btnStartDecode,
	encodeGifBtn;
    private CheckBox lowMem, dither;
    private TextView imgPath;
    private final int SELECT_FILE_REQUEST_CODE = 8212;
    private String selectGifPath = "";
    private Bitmap[] bitmaps;
    private int gifDelay;
    private int gifSize;
    private String tmpFolder=Environment.getExternalStorageDirectory().getAbsolutePath()+
	"/Pictures/QRcode/tmp/";

    @Override
    public View onCreateView(LayoutInflater inflater,ViewGroup container,Bundle savedInstanceState){
        // TODO: Implement this method
        return inflater.inflate(R.layout.gif_qr_main,container,false);
    }

    @Override
    public void onViewCreated(View view,Bundle savedInstanceState){
        // TODO: Implement this method
        super.onViewCreated(view,savedInstanceState);
		pb=(ProgressBar)view.findViewById(R.id.gif_qr_mainProgressBar);
        lowMem=(CheckBox) view.findViewById(R.id.gif_qr_checkbox_low_memery);
        dither=(CheckBox) view.findViewById(R.id.gif_qr_checkbox_dither);
        imageView=(ImageView) view.findViewById(R.id.image_view);
        btnSelectImg=(Button) view.findViewById(R.id.gif_qr_button_selectImg);
        btnStartDecode=(Button) view.findViewById(R.id.gif_qr_button_startDecodeImg);
        encodeGifBtn=(Button) view.findViewById(R.id.gif_qr_button_encode_gif);
        imgPath=(TextView) view.findViewById(R.id.gif_qr_selected_path);
		contentEt=(EditText)view.findViewById(R.id.gif_qr_mainEditText_content);
		dark=(EditText)view.findViewById(R.id.gif_qr_mainEditText_dot_dark);
		light=(EditText)view.findViewById(R.id.gif_qr_mainEditText_dot_color_light);
        btnSelectImg.setOnClickListener(new OnClickListener() {

				@Override
				public void onClick(View p1){
					// TODO: Implement this method
					selectImage();
				}
			});
        btnStartDecode.setOnClickListener(new OnClickListener() {

				@Override
				public void onClick(View p1){
					// TODO: Implement this method
					decodeGif(selectGifPath);
				}
			});

        encodeGifBtn.setOnClickListener(new OnClickListener() {

				@Override
				public void onClick(View p1){
					// TODO: Implement this method
					onEncodeGIF();
				}
			});
    }


    public void onDecodeGIF(){
        new Thread(new Runnable() {
				@Override
				public void run(){
					String destFile = "";// = setupSampleFile();

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
					}catch(Exception e){
						e.printStackTrace();
					}
				}
			}).start();
    }

    private void encodeGIF() throws IOException{
        final String filePath = Environment.getExternalStorageDirectory().getAbsolutePath()+
			"/Pictures/QRcode/GifAwesomeQR"+SystemClock.elapsedRealtime()+".gif";
        GifEncoder gifEncoder = new GifEncoder();
        gifEncoder.setDither(dither.isChecked());

		gifSize=300;
        if(lowMem.isChecked()){
            gifEncoder.init(gifSize,gifSize,filePath,GifEncoder.EncodingType.ENCODING_TYPE_NORMAL_LOW_MEMORY);
            for(int t=0;t<bitmaps.length;t++){
                gifEncoder.encodeFrame(
					encodeAwesome(
						contentEt.getText().toString(),
						gifSize,
						0.4f,
						BitmapFactory.decodeFile(tmpFolder+t+".png")
					),gifDelay);
				final int fl=(int)((t+1)*100.0f/bitmaps.length);

				getActivity().runOnUiThread(new Runnable(){

						@Override
						public void run(){
							// TODO: Implement this method
							pb.setProgress(fl);
						}
					});

            }
        }else{
            gifEncoder.init(gifSize,gifSize,filePath,GifEncoder.EncodingType.ENCODING_TYPE_FAST);
            for(int t=0;t<bitmaps.length;t++){
                gifEncoder.encodeFrame(
					encodeAwesome(
						contentEt.getText().toString(),
						gifSize,
						0.4f,
						bitmaps[t]
					),gifDelay);
				final int fl=(int)((t+1)*100.0f/bitmaps.length);

				getActivity().runOnUiThread(new Runnable(){

						@Override
						public void run(){
							// TODO: Implement this method
							pb.setProgress(fl);
						}
					});
            }
        }
		gifEncoder.close();
		getActivity().getApplicationContext().sendBroadcast(
			new Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE,Uri.fromFile(new File(filePath))));//更新图库
		log.i("done : "+filePath);
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
                imgPath.setText(selectGifPath);
                selectGifPath=ContentHelper.absolutePathFromUri(getActivity().getApplicationContext(),imageUri);
            }catch(Exception e){
                log.e(e);
            }
        }else if(resultCode==getActivity().RESULT_CANCELED){
            Toast.makeText(getActivity().getApplicationContext(),"用户取消了操作",Toast.LENGTH_SHORT).show();
        }else{
			selectImage();
        }
        super.onActivityResult(requestCode,resultCode,data);
    }

    private void selectImage(){
        Intent intent = new Intent(Intent.ACTION_OPEN_DOCUMENT);
        intent.addCategory(Intent.CATEGORY_OPENABLE);
        intent.setType("image/*");
        startActivityForResult(intent,SELECT_FILE_REQUEST_CODE);
    }

    private void decodeGif(final String path){
        if(lowMem.isChecked()){
            imgPath.setText(path+"\n使用了低配置模式");
            new Thread(new Runnable() {
					@Override
					public void run(){
						GifDecoder gifDecoder = new GifDecoder();
						GifImageIterator iterator = gifDecoder.loadUsingIterator(path);
						int flag = 0;
						while(iterator.hasNext()){
							GifImage next = iterator.next();
							if(next!=null){
								try{
									QRCode.saveMyBitmap(tmpFolder+flag+++".png",next.bitmap);
								}catch(IOException e){
									log.e(e);
								}
								gifDelay=next.delayMs;
							}else{
								log.e("解码失败，可能文件损坏");
							}
						}
						iterator.close();
						log.i("共"+(flag-1)+"张,解码成功");
						bitmaps=new Bitmap[flag-1];
						gifSize=BitmapFactory.decodeFile(tmpFolder+"0.png").getWidth();
					}
				}).start();
        }else{
            imgPath.setText(path+"\n未使用低配置模式");
            new Thread(new Runnable() {
					@Override
					public void run(){
						final GifDecoder gifDecoder = new GifDecoder();
						if(gifDecoder.load(path)){
							bitmaps=new Bitmap[gifDecoder.frameNum()];
							gifDelay=gifDecoder.delay(1);
							for(int i = 0; i<gifDecoder.frameNum(); i++){
								bitmaps[i]=gifDecoder.frame(i);
								try{
									QRCode.saveMyBitmap(tmpFolder+i+".png",bitmaps[i]);
								}catch(IOException e){
									e.printStackTrace();
								}
								final int fl=(int)((i+1)*100.0f/gifDecoder.frameNum());

								getActivity().runOnUiThread(new Runnable(){

										@Override
										public void run(){
											// TODO: Implement this method
											pb.setProgress(fl);
										}
									});

							}
							log.i("共"+gifDecoder.frameNum()+"张,解码成功");
							gifSize=bitmaps[0].getWidth();
						}else{
							log.e("解码失败，可能不是GIF文件");
						}

					}
				}).start();
        }
    }

    private Bitmap encodeAwesome(String contents,int size,float dotScale,Bitmap bg){
        return AwesomeQRCode.create(
			contents,
			size,
			(int)(size*0.025f),
			dotScale, 
			dark.getText().toString().equals("")?Color.BLACK: Color.parseColor(dark.getText().toString()),
			light.getText().toString().equals("")?Color.WHITE: Color.parseColor(light.getText().toString()),
			bg,
			false,
			false,
			false,
			0);

    }

}
