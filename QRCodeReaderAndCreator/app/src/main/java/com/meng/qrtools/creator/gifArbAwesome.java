package com.meng.qrtools.creator;

import android.app.AlertDialog;
import android.app.Fragment;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.os.SystemClock;
import android.util.DisplayMetrics;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.meng.MainActivity2;
import com.meng.qrtools.R;
import com.meng.qrtools.lib.ContentHelper;
import com.meng.qrtools.lib.qrcodelib.QrUtils;
import com.meng.qrtools.log;
import com.meng.qrtools.mengViews.mengColorBar;
import com.meng.qrtools.mengViews.mengEdittext;
import com.meng.qrtools.mengViews.mengSeekBar;
import com.meng.qrtools.mengViews.mengSelectRectView;
import com.waynejo.androidndkgif.GifDecoder;
import com.waynejo.androidndkgif.GifEncoder;
import com.waynejo.androidndkgif.GifImage;
import com.waynejo.androidndkgif.GifImageIterator;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.Date;

/**
 * Created by Administrator on 2018/8/18.
 */

public class gifArbAwesome extends Fragment{


    private boolean coding=false;
    private String strTmpFolder=Environment.getExternalStorageDirectory().getAbsolutePath()+
            "/Pictures/QRcode/tmp/";
    private int intGifFrameDelay;
    private int qrSize;
    private Bitmap[] bmpDecodedBitmaps;
    private Button btnEncodeGif;
    private Button btnSelectImage;
    private CheckBox cbAutoColor;
    private CheckBox cbLowMemoryMode;
    private CheckBox cbUseDither;
    private mengEdittext mengEtDotScale;
    private mengEdittext mengEtTextToEncode;
    private ProgressBar pbCodingProgress;
    private String strSelectedGifPath="";
    private TextView tvImagePath;
    private mengColorBar mColorBar;

    private mengSelectRectView mv;
    private float screenW;
    private float screenH;
    private int gifWidth;
    private int gifHeight;
    private int bmpCount;

    @Override
    public View onCreateView(LayoutInflater inflater,ViewGroup container,Bundle savedInstanceState){
        // TODO: Implement this method
        return inflater.inflate(R.layout.gif_arb_qr_main,container,false);
    }

    @Override
    public void onViewCreated(View view,Bundle savedInstanceState){
        // TODO: Implement this method
        super.onViewCreated(view,savedInstanceState);
        mv=(mengSelectRectView)view.findViewById(R.id.gif_arb_awesome_qrselectRectView);
        mColorBar=(mengColorBar)view.findViewById(R.id.gif_arb_qr_main_colorBar);
        btnEncodeGif=(Button)view.findViewById(R.id.gif_arb_qr_button_encode_gif);
        btnSelectImage=(Button)view.findViewById(R.id.gif_arb_qr_button_selectImg);
        cbAutoColor=(CheckBox)view.findViewById(R.id.gif_arb_qr_checkbox_autocolor);
        cbLowMemoryMode=(CheckBox)view.findViewById(R.id.gif_arb_qr_checkbox_low_memery);
        cbUseDither=(CheckBox)view.findViewById(R.id.gif_arb_qr_checkbox_dither);
        mengEtDotScale=(mengEdittext)view.findViewById(R.id.gif_arb_qr_mengEdittext_dotScale);
        mengEtTextToEncode=(mengEdittext)view.findViewById(R.id.gif_arb_qr_mainmengTextview_content);
        pbCodingProgress=(ProgressBar)view.findViewById(R.id.gif_arb_qr_mainProgressBar);
        tvImagePath=(TextView)view.findViewById(R.id.gif_arb_qr_selected_path);
        cbAutoColor.setOnCheckedChangeListener(check);
        btnSelectImage.setOnClickListener(listenerBtnClick);
        btnEncodeGif.setOnClickListener(listenerBtnClick);
        DisplayMetrics dm=new DisplayMetrics();
        getActivity().getWindowManager().getDefaultDisplay().getMetrics(dm);
        screenW=dm.widthPixels;
        screenH=dm.heightPixels;
    }

    CompoundButton.OnCheckedChangeListener check=new CompoundButton.OnCheckedChangeListener(){
        @Override
        public void onCheckedChanged(CompoundButton buttonView,boolean isChecked){
            switch(buttonView.getId()){
                case R.id.gif_arb_qr_checkbox_autocolor:
                    mColorBar.setVisibility(isChecked?View.GONE:View.VISIBLE);
                    break;
            }
        }
    };
    View.OnClickListener listenerBtnClick=new View.OnClickListener(){
        @Override
        public void onClick(View v){
            switch(v.getId()){
                case R.id.gif_arb_qr_button_selectImg:
                    cbLowMemoryMode.setEnabled(false);
                    MainActivity2.selectImage(gifArbAwesome.this);
                    break;
                case R.id.gif_arb_qr_button_encode_gif:
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
        new Thread(new Runnable(){
            @Override
            public void run(){
                try{
                    coding=true;
                    final String filePath=Environment.getExternalStorageDirectory().getAbsolutePath()+
                            "/Pictures/QRcode/gifAwesomeQR"+(new Date()).toString()+".gif";
                    GifEncoder gifEncoder=new GifEncoder();
                    gifEncoder.setDither(cbUseDither.isChecked());
                    if(cbLowMemoryMode.isChecked()){
                        gifEncoder.init(gifWidth,gifHeight,filePath,GifEncoder.EncodingType.ENCODING_TYPE_NORMAL_LOW_MEMORY);
                        for(int t=0;t<bmpCount;t++){
                            gifEncoder.encodeFrame(
                                    encodeAwesome(BitmapFactory.decodeFile(strTmpFolder+t+".png")),
                                    intGifFrameDelay);
                            setProgress((int)((t+1)*100.0f/bmpDecodedBitmaps.length),true);
                        }
                    }else{
                        gifEncoder.init(gifWidth,gifHeight,filePath,GifEncoder.EncodingType.ENCODING_TYPE_FAST);
                        for(int t=0;t<bmpCount;t++){
                            gifEncoder.encodeFrame(
                                    encodeAwesome(bmpDecodedBitmaps[t]),
                                    intGifFrameDelay);
                            setProgress((int)((t+1)*100.0f/bmpDecodedBitmaps.length),true);
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
                getActivity().runOnUiThread(new Runnable(){
                    @Override
                    public void run(){
                        cbLowMemoryMode.setEnabled(true);
                        btnSelectImage.setEnabled(true);
                    }
                });
            }
        }).start();
    }

    private void decodeGif(final String path){
        if(cbLowMemoryMode.isChecked()){
            new Thread(new Runnable(){
                @Override
                public void run(){
                    GifDecoder gifDecoder=new GifDecoder();
                    GifImageIterator iterator=gifDecoder.loadUsingIterator(path);
                    int flag=0;
                    while(iterator.hasNext()){
                        GifImage next=iterator.next();
                        if(next!=null){
                            gifHeight=next.bitmap.getHeight();
                            gifWidth=next.bitmap.getWidth();
                            try{
                                QrUtils.saveMyBitmap(strTmpFolder+flag+++".png",next.bitmap);
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
                    bmpCount=flag;
                    //			final Bitmap tmpbmp=BitmapFactory.decodeFile(strTmpFolder+"0.png");
                    createNomediaFile();
                    coding=false;
                    getActivity().runOnUiThread(new Runnable(){
                        @Override
                        public void run(){
                            btnEncodeGif.setVisibility(View.VISIBLE);
                            cbUseDither.setVisibility(View.VISIBLE);
                            tvImagePath.setVisibility(View.VISIBLE);
                            mv.setup(
                                    BitmapFactory.decodeFile(strTmpFolder+"0.png"),
                                    screenW,
                                    screenH,
                                    qrSize);
                            ViewGroup.LayoutParams para=mv.getLayoutParams();
                            para.height=(int)(screenW/gifWidth*gifHeight);
                            //	para.height=(int)(screenW/tmpbmp.getWidth()*tmpbmp.getHeight());
                            mv.setLayoutParams(para);
                            mv.setVisibility(View.VISIBLE);
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
                        log.i(getActivity(),"共"+(bmpCount=gifDecoder.frameNum())+"张,解码成功");
                    }else{
                        log.e(getActivity(),"解码失败，可能不是GIF文件");
                    }
                    coding=false;
                    getActivity().runOnUiThread(new Runnable(){
                        @Override
                        public void run(){
                            btnEncodeGif.setVisibility(View.VISIBLE);
                            cbUseDither.setVisibility(View.VISIBLE);
                            tvImagePath.setVisibility(View.VISIBLE);
                            gifHeight=bmpDecodedBitmaps[0].getHeight();
                            gifWidth=bmpDecodedBitmaps[0].getWidth();
                            log.i(getActivity(),"setup");
                            mv.setup(
                                    bmpDecodedBitmaps[0],
                                    screenW,
                                    screenH,
                                    qrSize);
                            log.i(getActivity(),"setPara");
                            ViewGroup.LayoutParams para=mv.getLayoutParams();
                            para.height=(int)(screenW/gifWidth*gifHeight);
                            mv.setLayoutParams(para);
                            mv.setVisibility(View.VISIBLE);
                        }
                    });
                }
            }).start();
        }
    }

    private void createNomediaFile(){
        File f=new File(strTmpFolder+".nomedia");
        if(!f.exists()){
            try{
                f.createNewFile();
            }catch(IOException e){
                log.e(getActivity(),e);
            }
        }
    }

    private Bitmap encodeAwesome(Bitmap bg){
        /* return AwesomeQRCode.create(
		 mengEtTextToEncode.getString(),
		 cbAutoSize.isChecked()?size:Integer.parseInt(mengEtSize.getString()),
		 (int)(size*0.025f),
		 Float.parseFloat(mengEtDotScale.getString()),
		 mColorBar.getTrueColor(),
		 cbAutoColor.isChecked()?Color.WHITE:mColorBar.getFalseColor(),
		 bg,
		 false,
		 cbAutoColor.isChecked(),
		 false,
		 128);*/
        return QrUtils.generate(
                mengEtTextToEncode.getString(),
                Float.parseFloat(mengEtDotScale.getString()),
                cbAutoColor.isChecked()?Color.BLACK:mColorBar.getTrueColor(),
                cbAutoColor.isChecked()?Color.WHITE:mColorBar.getFalseColor(),
                cbAutoColor.isChecked(),
                between(mv.getSelectLeft()/mv.getXishu(),0,bg.getWidth()-qrSize),
                between(mv.getSelectTop()/mv.getXishu(),0,bg.getHeight()-qrSize),
                qrSize,
                bg);
    }

    private int between(float a,int min,int max){
        if(a<min){
            a=min;
        }
        if(a>max){
            a=max;
        }
        return (int)a;
    }

    private void setProgress(final int p,final boolean encoing){
        getActivity().runOnUiThread(new Runnable(){
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

    @Override
    public void onActivityResult(int requestCode,int resultCode,Intent data){
        if(requestCode==MainActivity2.SELECT_FILE_REQUEST_CODE&&resultCode==getActivity().RESULT_OK&&data.getData()!=null){
            try{
                if(coding){
                    log.t(getActivity(),"正在执行操作");
                }else{
                    Uri imageUri=data.getData();
                    strSelectedGifPath=ContentHelper.absolutePathFromUri(getActivity().getApplicationContext(),imageUri);
                    tvImagePath.setText(strSelectedGifPath);

                    final Bitmap selectedBmp=BitmapFactory.decodeFile(strSelectedGifPath);
                    final int selectedBmpWidth=selectedBmp.getWidth();
                    final int selectedBmpHeight=selectedBmp.getHeight();
                    final mengSeekBar msb=new mengSeekBar(getActivity());
                    int maxProg=Math.min(selectedBmpWidth,selectedBmpHeight);
                    msb.setMax(maxProg);
                    msb.setProgress(maxProg/3);
                    new AlertDialog.Builder(getActivity())
                            .setTitle("输入要添加的二维码大小(像素)")
                            .setView(msb)
                            .setPositiveButton("确定",new DialogInterface.OnClickListener(){
                                @Override
                                public void onClick(DialogInterface p1,int p2){
                                    qrSize=msb.getProgress();
                                    //ll.addView(new mengSelectRectView(getActivity(),selectedBmp,screenW,screenH));
                                    mv.setup(selectedBmp,screenW,screenH,qrSize);
                                    ViewGroup.LayoutParams para=mv.getLayoutParams();
                                    para.height=(int)(screenW/selectedBmpWidth*selectedBmpHeight);
                                    mv.setLayoutParams(para);
                                    mv.setVisibility(View.VISIBLE);
                                    decodeGif(strSelectedGifPath);
                                    coding=true;
                                }
                            }).show();
                }
            }catch(Exception e){
                log.e(getActivity(),e);
            }
        }else if(resultCode==getActivity().RESULT_CANCELED){
            Toast.makeText(getActivity().getApplicationContext(),"用户取消了操作",Toast.LENGTH_SHORT).show();
        }else{
            MainActivity2.selectImage(this);
        }
        super.onActivityResult(requestCode,resultCode,data);
    }

}
