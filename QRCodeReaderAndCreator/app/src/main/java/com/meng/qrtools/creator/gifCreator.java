package com.meng.qrtools.creator;

import android.app.Fragment;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CheckBox;

import com.meng.MainActivity2;
import com.meng.qrtools.R;
import com.meng.qrtools.lib.ContentHelper;
import com.meng.qrtools.lib.qrcodelib.QrUtils;
import com.meng.qrtools.log;
import com.meng.qrtools.mengViews.mengEdittext;
import com.waynejo.androidndkgif.GifEncoder;

import java.io.File;
import java.util.Date;

/**
 * Created by Administrator on 2018/8/23.
 */

public class gifCreator extends Fragment{

    private CheckBox cbAutoSize;
    private mengEdittext mengEtGifHeight;
    private mengEdittext mengEtGifWidth;
    private mengEdittext mengEtFrameDelay;
    private Button btnAddFrame;
    private Button btnFinish;
    private GifEncoder gifEncoder;
    private boolean encoderInited=false;
    private String filePath;
    private String strSelectedGifPath;

    @Override
    public View onCreateView(LayoutInflater inflater,ViewGroup container,Bundle savedInstanceState){
        // TODO: Implement this method
        return inflater.inflate(R.layout.gif_creator,container,false);
    }

    @Override
    public void onViewCreated(View view,Bundle savedInstanceState){
        // TODO: Implement this method
        super.onViewCreated(view,savedInstanceState);
        cbAutoSize=(CheckBox)view.findViewById(R.id.gif_creator_autosize);
        mengEtGifHeight=(mengEdittext)view.findViewById(R.id.gif_creator_height);
        mengEtGifWidth=(mengEdittext)view.findViewById(R.id.gif_creator_width);
        mengEtFrameDelay=(mengEdittext)view.findViewById(R.id.gif_creator_delay);
        btnAddFrame=(Button)view.findViewById(R.id.gif_creator_add);
        btnFinish=(Button)view.findViewById(R.id.gif_creator_finish);
        btnAddFrame.setOnClickListener(listenerBtnClick);
        btnFinish.setOnClickListener(listenerBtnClick);
    }

    View.OnClickListener listenerBtnClick=new View.OnClickListener(){
        @Override
        public void onClick(View v){
            switch(v.getId()){
                case R.id.gif_creator_add:
                    MainActivity2.selectImage(gifCreator.this);
                    break;
                case R.id.gif_creator_finish:
                    gifEncoder.close();
                    getActivity().getApplicationContext().sendBroadcast(new Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE,Uri.fromFile(new File(filePath))));
                    log.i(getActivity(),"done : "+filePath);
                    break;
            }
        }
    };

    @Override
    public void onActivityResult(int requestCode,int resultCode,Intent data){
        if(requestCode==MainActivity2.SELECT_FILE_REQUEST_CODE&&resultCode==getActivity().RESULT_OK&&data.getData()!=null){
            try{
                strSelectedGifPath=ContentHelper.absolutePathFromUri(getActivity().getApplicationContext(),data.getData());
                if(!encoderInited){
                    filePath=Environment.getExternalStorageDirectory().getAbsolutePath()+
                            "/Pictures/QRcode/gif"+(new Date()).toString()+".gif";
                    GifEncoder gifEncoder=new GifEncoder();
                    gifEncoder.setDither(false);
                    if(cbAutoSize.isChecked()){
                        Bitmap bmp=BitmapFactory.decodeFile(strSelectedGifPath);
                        gifEncoder.init(
                                bmp.getWidth(),
                                bmp.getHeight(),
                                filePath,
                                GifEncoder.EncodingType.ENCODING_TYPE_NORMAL_LOW_MEMORY);
                    }else{
                        gifEncoder.init(
                                mengEtGifWidth.getInt(),
                                mengEtGifHeight.getInt(),
                                filePath,
                                GifEncoder.EncodingType.ENCODING_TYPE_NORMAL_LOW_MEMORY);
                    }
                    encoderInited=true;
                }
                gifEncoder.encodeFrame(
                        QrUtils.flex(
                                BitmapFactory.decodeFile(strSelectedGifPath),
                                mengEtGifWidth.getInt(),
                                mengEtGifHeight.getInt()),
                        mengEtFrameDelay.getInt());
            }catch(Exception e){
                log.e(getActivity(),e);
            }
            super.onActivityResult(requestCode,resultCode,data);
        }
    }

}
