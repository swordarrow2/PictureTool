package com.meng.picTools.qrCode.creator;

import android.app.Fragment;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.meng.picTools.MainActivity2;
import com.meng.picTools.MainActivity;
import com.meng.picTools.R;
import com.meng.picTools.LogTool;
import com.meng.picTools.lib.ContentHelper;
import com.meng.picTools.qrCode.qrcodelib.AwesomeQRCode;
import com.meng.picTools.qrCode.qrcodelib.QrUtils;
import com.meng.picTools.mengViews.MengColorBar;
import com.meng.picTools.mengViews.MengEditText;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;

public class gifAwesomeQr extends Fragment{

    private boolean coding=false;
    private int intGifFrameDelay;
    private int intGifSize;

    private Bitmap[] bmpDecodedBitmaps;
    private Button btnEncodeGif;
    private Button btnSelectImage;
    private CheckBox cbAutoColor;
    private CheckBox cbLowMemoryMode;
    private CheckBox cbUseDither;
    private MengEditText mengEtDotScale;
    private MengEditText mengEtTextToEncode;
    private CheckBox cbAutoSize;
    private MengEditText mengEtSize;
    private ProgressBar pbCodingProgress;
    private String strSelectedGifPath="";
    private TextView tvImagePath;
    private MengColorBar mColorBar;


    @Override
    public View onCreateView(LayoutInflater inflater,ViewGroup container,Bundle savedInstanceState){
        // TODO: Implement this method
        return inflater.inflate(R.layout.gif_qr_main,container,false);
    }

    @Override
    public void onViewCreated(View view,Bundle savedInstanceState){
        // TODO: Implement this method
        super.onViewCreated(view,savedInstanceState);
        mColorBar=(MengColorBar)view.findViewById(R.id.gif_arb_qr_main_colorBar);
        btnEncodeGif=(Button)view.findViewById(R.id.gif_arb_qr_button_encode_gif);
        btnSelectImage=(Button)view.findViewById(R.id.gif_arb_qr_button_selectImg);
        cbAutoColor=(CheckBox)view.findViewById(R.id.gif_arb_qr_checkbox_autocolor);
        cbLowMemoryMode=(CheckBox)view.findViewById(R.id.gif_arb_qr_checkbox_low_memery);
        cbUseDither=(CheckBox)view.findViewById(R.id.gif_arb_qr_checkbox_dither);
        mengEtDotScale=(MengEditText)view.findViewById(R.id.gif_arb_qr_mengEdittext_dotScale);
        mengEtTextToEncode=(MengEditText)view.findViewById(R.id.gif_arb_qr_mainmengTextview_content);
        mengEtSize=(MengEditText)view.findViewById(R.id.gif_qr_mainEditText_size);
        cbAutoSize=(CheckBox)view.findViewById(R.id.gif_qr_mainCheckbox_size);
        pbCodingProgress=(ProgressBar)view.findViewById(R.id.gif_arb_qr_mainProgressBar);
        tvImagePath=(TextView)view.findViewById(R.id.gif_arb_qr_selected_path);
        cbAutoSize.setOnCheckedChangeListener(check);
        cbAutoColor.setOnCheckedChangeListener(check);
        btnSelectImage.setOnClickListener(listenerBtnClick);
        btnEncodeGif.setOnClickListener(listenerBtnClick);
    }

    CompoundButton.OnCheckedChangeListener check=new CompoundButton.OnCheckedChangeListener(){
        @Override
        public void onCheckedChanged(CompoundButton buttonView,boolean isChecked){
            switch(buttonView.getId()){
                case R.id.gif_qr_mainCheckbox_size:
                    mengEtSize.setVisibility(isChecked?View.GONE:View.VISIBLE);
                    break;
                case R.id.gif_arb_qr_checkbox_autocolor:
                    mColorBar.setVisibility(isChecked?View.GONE:View.VISIBLE);
                    if(!isChecked) LogTool.t("如果颜色搭配不合理,二维码将会难以识别");
                    break;
            }
        }
    };
    OnClickListener listenerBtnClick=new OnClickListener(){
        @Override
        public void onClick(View v){
            switch(v.getId()){
                case R.id.gif_arb_qr_button_selectImg:
                    cbLowMemoryMode.setEnabled(false);
                    MainActivity2.selectImage(gifAwesomeQr.this);
                    break;
                case R.id.gif_arb_qr_button_encode_gif:
                    if(coding){
                        LogTool.t("正在执行操作");
                    }else{
                        btnSelectImage.setEnabled(false);
                        encodeGIF();
                    }
                    break;
            }
        }
    };

    private void encodeGIF(){
       /* new Thread(new Runnable(){
            @Override
            public void run(){
                try{
                    coding=true;
                    String filePath=MainActivity.instence.getGifAwesomeQRPath();
                    GifEncoder gifEncoder=new GifEncoder();
                    gifEncoder.setDither(cbUseDither.isChecked());
                    if(!cbAutoSize.isChecked()){
                        intGifSize=mengEtSize.getInt();
                    }
                    if(cbLowMemoryMode.isChecked()){
                        gifEncoder.init(intGifSize,intGifSize,filePath,GifEncoder.EncodingType.ENCODING_TYPE_NORMAL_LOW_MEMORY);
                        for(int t=0;t<bmpDecodedBitmaps.length;t++){
                            gifEncoder.encodeFrame(
                                    encodeAwesome(intGifSize,BitmapFactory.decodeFile(MainActivity.instence.getTmpFolder()+t+".png")),
                                    intGifFrameDelay);
                        }
                    }else{
                        gifEncoder.init(intGifSize,intGifSize,filePath,GifEncoder.EncodingType.ENCODING_TYPE_FAST);
                        for(int t=0;t<bmpDecodedBitmaps.length;t++){
                            gifEncoder.encodeFrame(
                                    encodeAwesome(intGifSize,bmpDecodedBitmaps[t]),
                                    intGifFrameDelay);
                            setProgress((int)((t+1)*100.0f/bmpDecodedBitmaps.length),true);
                        }
                    }
                    gifEncoder.close();
                    getActivity().getApplicationContext().sendBroadcast(new Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE,Uri.fromFile(new File(filePath))));
                    LogTool.t("完成 : "+filePath);
                }catch(FileNotFoundException e){
                    LogTool.e(e);
                }
                coding=false;
                System.gc();
                getActivity().runOnUiThread(new Runnable(){
                    @Override
                    public void run(){
                        cbLowMemoryMode.setEnabled(true);
                        btnSelectImage.setEnabled(true);
                    }
                });
            }
        }).start();*/
    }

    private void decodeGif(final String path){
     /*   if(cbLowMemoryMode.isChecked()){
            new Thread(new Runnable(){
                @Override
                public void run(){
                    GifDecoder gifDecoder=new GifDecoder();
                    GifImageIterator iterator=gifDecoder.loadUsingIterator(path);
                    int flag=0;
                    while(iterator.hasNext()){
                        GifImage next=iterator.next();
                        if(next!=null){
                            try{
                                QrUtils.saveMyBitmap(MainActivity.instence.getTmpFolder()+flag+++".png",next.bitmap);
                            }catch(IOException e){
                                LogTool.e(e);
                            }
                            intGifFrameDelay=next.delayMs;
                        }else{
                            LogTool.e("解码失败，可能文件损坏");
                        }
                    }
                    iterator.close();
                    LogTool.t("共"+(flag-1)+"张,解码成功");
                    bmpDecodedBitmaps=new Bitmap[flag];
                    intGifSize=BitmapFactory.decodeFile(MainActivity.instence.getTmpFolder()+"0.png").getWidth();
                    coding=false;
                    getActivity().runOnUiThread(new Runnable(){
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
            new Thread(new Runnable(){
                @Override
                public void run(){
                    final GifDecoder gifDecoder=new GifDecoder();
                    if(gifDecoder.load(path)){
                        bmpDecodedBitmaps=new Bitmap[gifDecoder.frameNum()];
                        intGifFrameDelay=gifDecoder.delay(1);
                        for(int i=0;i<gifDecoder.frameNum();i++){
                            bmpDecodedBitmaps[i]=gifDecoder.frame(i);
                            setProgress((int)((i+1)*100.0f/gifDecoder.frameNum()),false);
                        }
                        LogTool.t("共"+gifDecoder.frameNum()+"张,解码成功");
                        intGifSize=bmpDecodedBitmaps[0].getWidth();
                    }else{
                        LogTool.e("解码失败，可能不是GIF文件");
                    }
                    coding=false;
                    getActivity().runOnUiThread(new Runnable(){
                        @Override
                        public void run(){
                            btnEncodeGif.setVisibility(View.VISIBLE);
                            cbUseDither.setVisibility(View.VISIBLE);
                            tvImagePath.setVisibility(View.VISIBLE);
                        }
                    });
                }
            }).start();
        }*/
    }

    private Bitmap encodeAwesome(int size,Bitmap bg){
        return AwesomeQRCode.create(
                mengEtTextToEncode.getString(),
                cbAutoSize.isChecked()?size:mengEtSize.getInt(),
                (int)(size*0.025f),
                Float.parseFloat(mengEtDotScale.getString()),
                mColorBar.getTrueColor(),
                cbAutoColor.isChecked()?Color.WHITE:mColorBar.getFalseColor(),
                bg,
                false,
                cbAutoColor.isChecked(),
                false,
                128);
    }

    private void setProgress(final int p,final boolean encoing){
        getActivity().runOnUiThread(new Runnable(){
            @Override
            public void run(){
                pbCodingProgress.setProgress(p);
                if(p==100){
                    pbCodingProgress.setVisibility(View.GONE);
                    if(encoing){
                        LogTool.t("编码完成");
                    }else{
                        LogTool.t("解码完成");
                    }
                }else{
                    if(pbCodingProgress.getVisibility()==View.GONE){
                        pbCodingProgress.setVisibility(View.VISIBLE);
                    }
                }
            }
        });
    }

    @Override
    public void onActivityResult(int requestCode,int resultCode,Intent data){
        if(requestCode==MainActivity2.SELECT_FILE_REQUEST_CODE&&resultCode==getActivity().RESULT_OK&&data.getData()!=null){
            try{
                if(coding){
                    LogTool.t("正在执行操作");
                }else{
                    strSelectedGifPath=ContentHelper.absolutePathFromUri(getActivity().getApplicationContext(),data.getData());
                    tvImagePath.setText(strSelectedGifPath);
                    decodeGif(strSelectedGifPath);
                    coding=true;
                }
            }catch(Exception e){
                LogTool.e(e);
            }
        }else if(resultCode==getActivity().RESULT_CANCELED){
            Toast.makeText(getActivity().getApplicationContext(),"用户取消了操作",Toast.LENGTH_SHORT).show();
        }else{
            MainActivity2.selectImage(this);
        }
        super.onActivityResult(requestCode,resultCode,data);
    }

}
