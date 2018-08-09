package com.meng.qrtools.creator;

import android.app.*;
import android.content.*;
import android.graphics.*;
import android.net.*;
import android.os.*;
import android.text.*;
import android.view.*;
import android.view.View.*;
import android.widget.*;
import com.meng.qrtools.*;
import com.meng.qrtools.views.*;
import com.waynejo.androidndkgif.*;
import java.io.*;

public class gifAwesomeQr extends Fragment{

    private final int SELECT_FILE_REQUEST_CODE = 8212;

    private boolean coding = false;
    private String strTmpFolder = Environment.getExternalStorageDirectory().getAbsolutePath()+
	"/Pictures/QRcode/tmp/";
    private int intGifFrameDelay;
    private int intGifSize;

    private Bitmap[] bmpDecodedBitmaps;
    private Button btnEncodeGif;
    private Button btnSelectImage;
    private CheckBox cbAutoColor;
    private CheckBox cbLowMemoryMode;
    private CheckBox cbUseDither;
    private mengEdittext mengEtDarkDotColor;
    private mengEdittext mengEtDotScale;
    private mengEdittext mengEtLightDotColor;
    private mengEdittext mengEtTextToEncode;
    private mengEdittextWithCheckBox mengEtSize;
    private ProgressBar pbCodingProgress;
    private String strSelectedGifPath = "";
    private TextView tvImagePath;


    @Override
    public View onCreateView(LayoutInflater inflater,ViewGroup container,Bundle savedInstanceState){
        // TODO: Implement this method
        return inflater.inflate(R.layout.gif_qr_main,container,false);
    }

    @Override
    public void onViewCreated(View view,Bundle savedInstanceState){
        // TODO: Implement this method
        super.onViewCreated(view,savedInstanceState);
        btnEncodeGif=(Button) view.findViewById(R.id.gif_qr_button_encode_gif);
        btnSelectImage=(Button) view.findViewById(R.id.gif_qr_button_selectImg);
        cbAutoColor=(CheckBox) view.findViewById(R.id.gif_qr_checkbox_autocolor);
        cbLowMemoryMode=(CheckBox) view.findViewById(R.id.gif_qr_checkbox_low_memery);
        cbUseDither=(CheckBox) view.findViewById(R.id.gif_qr_checkbox_dither);
        mengEtDotScale=(mengEdittext) view.findViewById(R.id.gif_qr_mengEdittext_dotScale);
        mengEtTextToEncode=(mengEdittext) view.findViewById(R.id.gif_qr_mainmengTextview_content);
        mengEtDarkDotColor=(mengEdittext) view.findViewById(R.id.gif_qr_mainMengEditText_dot_dark);
        mengEtLightDotColor=(mengEdittext) view.findViewById(R.id.gif_qr_mainMengEditText_dot_color_light);
        mengEtSize=(mengEdittextWithCheckBox) view.findViewById(R.id.gif_qr_mainEditTextWithCheckbox_size);
        pbCodingProgress=(ProgressBar) view.findViewById(R.id.gif_qr_mainProgressBar);
        tvImagePath=(TextView) view.findViewById(R.id.gif_qr_selected_path);
        mengEtDarkDotColor.addTextChangedListener(twColor);
        mengEtLightDotColor.addTextChangedListener(twColor);
        cbAutoColor.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
				@Override
				public void onCheckedChanged(CompoundButton buttonView,boolean isChecked){
					if(isChecked){
						mengEtDarkDotColor.setVisibility(View.GONE);
						mengEtLightDotColor.setVisibility(View.GONE);
					}else{
						mengEtDarkDotColor.setVisibility(View.VISIBLE);
						mengEtLightDotColor.setVisibility(View.VISIBLE);
					}
				}
			});
        btnSelectImage.setOnClickListener(listenerBtnClick);
        btnEncodeGif.setOnClickListener(listenerBtnClick);
    }

    OnClickListener listenerBtnClick = new OnClickListener() {
        @Override
        public void onClick(View v){
            switch(v.getId()){
                case R.id.gif_qr_button_selectImg:
                    cbLowMemoryMode.setEnabled(false);
                    selectImage();
                    break;
                case R.id.gif_qr_button_encode_gif:
					if(coding){
						log.t(getActivity(),"正在执行操作");
					}else{
						btnSelectImage.setEnabled(false);
						encodeGIF();
					}
                    break;
            }
        }
    };

    private void encodeGIF(){
        new Thread(new Runnable() {
				@Override
				public void run(){
					try{
						coding=true;
						final String filePath = Environment.getExternalStorageDirectory().getAbsolutePath()+
                            "/Pictures/QRcode/GifAwesomeQR"+SystemClock.elapsedRealtime()+".gif";
						GifEncoder gifEncoder = new GifEncoder();
						gifEncoder.setDither(cbUseDither.isChecked());
						if(cbLowMemoryMode.isChecked()){
							gifEncoder.init(intGifSize,intGifSize,filePath,GifEncoder.EncodingType.ENCODING_TYPE_NORMAL_LOW_MEMORY);
							for(int t = 0; t<bmpDecodedBitmaps.length; t++){
								gifEncoder.encodeFrame(
                                    encodeAwesome(mengEtTextToEncode.getString().toString(),intGifSize,BitmapFactory.decodeFile(strTmpFolder+t+".png")
												  ),intGifFrameDelay);
							}
						}else{
							gifEncoder.init(intGifSize,intGifSize,filePath,GifEncoder.EncodingType.ENCODING_TYPE_FAST);
							for(int t = 0; t<bmpDecodedBitmaps.length; t++){
								gifEncoder.encodeFrame(
                                    encodeAwesome(
										mengEtTextToEncode.getString().toString(),intGifSize,bmpDecodedBitmaps[t]
                                    ),intGifFrameDelay);
								setProgress((int) ((t+1)*100.0f/bmpDecodedBitmaps.length),true);
							}
						}
						gifEncoder.close();
						getActivity().getApplicationContext().sendBroadcast(new Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE,Uri.fromFile(new File(filePath))));
						log.i(getActivity(),"done : "+filePath);
					}catch(FileNotFoundException e){
						log.e(getActivity(),e);
					}
					coding=false;
					System.gc();
					getActivity().runOnUiThread(new Runnable() {
							@Override
							public void run(){
								cbLowMemoryMode.setEnabled(true);
								btnSelectImage.setEnabled(true);
							}
						});
				}
			}).start();
    }

    private void selectImage(){
        Intent intent = new Intent(Intent.ACTION_OPEN_DOCUMENT);
        intent.addCategory(Intent.CATEGORY_OPENABLE);
        intent.setType("image/*");
        startActivityForResult(intent,SELECT_FILE_REQUEST_CODE);
    }

    private void decodeGif(final String path){
        if(cbLowMemoryMode.isChecked()){
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
									QRCode.saveMyBitmap(strTmpFolder+flag+++".png",next.bitmap);
								}catch(IOException e){
									log.e(getActivity(),e);
								}
								intGifFrameDelay=next.delayMs;
							}else{
								log.e(getActivity(),"解码失败，可能文件损坏");
							}
						}
						iterator.close();
						log.t(getActivity(),"共"+(flag-1)+"张,解码成功");
						intGifSize=BitmapFactory.decodeFile(strTmpFolder+"0.png").getWidth();
						createNomediaFile();
						coding=false;
						getActivity().runOnUiThread(new Runnable() {
								@Override
								public void run(){
									btnEncodeGif.setVisibility(View.VISIBLE);
									cbUseDither.setVisibility(View.VISIBLE);
									tvImagePath.setVisibility(View.VISIBLE);
								}
							});
					}
				}).start();
        }else{
            new Thread(new Runnable() {
					@Override
					public void run(){
						final GifDecoder gifDecoder = new GifDecoder();
						if(gifDecoder.load(path)){
							bmpDecodedBitmaps=new Bitmap[gifDecoder.frameNum()];
							intGifFrameDelay=gifDecoder.delay(1);
							for(int i = 0; i<gifDecoder.frameNum(); i++){
								bmpDecodedBitmaps[i]=gifDecoder.frame(i);
								setProgress((int) ((i+1)*100.0f/gifDecoder.frameNum()),false);
							}
							log.i(getActivity(),"共"+gifDecoder.frameNum()+"张,解码成功");
							intGifSize=bmpDecodedBitmaps[0].getWidth();
						}else{
							log.e(getActivity(),"解码失败，可能不是GIF文件");
						}
						coding=false;
						getActivity().runOnUiThread(new Runnable() {
								@Override
								public void run(){
									btnEncodeGif.setVisibility(View.VISIBLE);
									cbUseDither.setVisibility(View.VISIBLE);
									tvImagePath.setVisibility(View.VISIBLE);
								}
							});
					}
				}).start();
        }
    }

    private void createNomediaFile(){
        File f = new File(strTmpFolder+".nomedia");
        if(!f.exists()){
            try{
                f.createNewFile();
            }catch(IOException e){
                log.e(getActivity(),e);
            }
        }
    }

    private Bitmap encodeAwesome(String contents,int size,Bitmap bg){
        return AwesomeQRCode.create(
			contents.equals("")? mengEtTextToEncode.getHint().toString() :contents,
			mengEtSize.isChecked()? size :Integer.parseInt(mengEtSize.getText().toString()),
			(int) (size*0.025f),
			mengEtDotScale.isEmpty()? Float.parseFloat(mengEtDotScale.getHint()) :Float.parseFloat(mengEtDotScale.getString()),
			cbAutoColor.isChecked()? Color.BLACK :Color.parseColor(mengEtDarkDotColor.getString()),
			cbAutoColor.isChecked()? Color.WHITE :Color.parseColor(mengEtLightDotColor.getString()),
			bg,
			false,
			cbAutoColor.isChecked(),
			false,
			128);
    }

    private void setProgress(final int p,final boolean encoing){
        getActivity().runOnUiThread(new Runnable() {
				@Override
				public void run(){
					pbCodingProgress.setProgress(p);
					if(p==100){
						pbCodingProgress.setVisibility(View.GONE);
						if(encoing){
							log.t(getActivity(),"编码完成");
						}else{
							log.t(getActivity(),"解码完成");
						}
					}else{
						if(pbCodingProgress.getVisibility()==View.GONE){
							pbCodingProgress.setVisibility(View.VISIBLE);
						}
					}
				}
			});
    }

    TextWatcher twColor = new TextWatcher() {

        @Override
        public void beforeTextChanged(CharSequence p1,int p2,int p3,int p4){
        }

        @Override
        public void onTextChanged(CharSequence p1,int p2,int p3,int p4){
            try{
                mengEtDarkDotColor.setTextColor(Color.parseColor(mengEtDarkDotColor.getString().toString()));
            }catch(Exception e){
                mengEtDarkDotColor.setTextColor(Color.BLACK);
            }
            try{
                mengEtLightDotColor.setTextColor(Color.parseColor(mengEtLightDotColor.getString().toString()));
            }catch(Exception e){
                mengEtLightDotColor.setTextColor(Color.BLACK);
            }
        }

        @Override
        public void afterTextChanged(Editable p1){
        }
    };

    @Override
    public void onActivityResult(int requestCode,int resultCode,Intent data){
        if(requestCode==SELECT_FILE_REQUEST_CODE&&resultCode==getActivity().RESULT_OK&&data.getData()!=null){
            try{
                if(coding){
                    log.t(getActivity(),"正在执行操作");
                }else{
                    Uri imageUri = data.getData();
                    strSelectedGifPath=ContentHelper.absolutePathFromUri(getActivity().getApplicationContext(),imageUri);
                    tvImagePath.setText(strSelectedGifPath);
                    decodeGif(strSelectedGifPath);
                    coding=true;
                }
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
