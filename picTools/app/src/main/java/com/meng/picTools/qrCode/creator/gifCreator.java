package com.meng.picTools.qrCode.creator;

import android.app.Fragment;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;

import com.meng.picTools.MainActivity2;
import com.meng.picTools.MainActivity;
import com.meng.picTools.R;
import com.meng.picTools.LogTool;
import com.meng.picTools.lib.ContentHelper;
import com.meng.picTools.qrCode.qrcodelib.QrUtils;
import com.meng.picTools.mengViews.MengEditText;
import com.waynejo.androidndkgif.GifEncoder;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;

/**
 * gif生成
 */

public class gifCreator extends Fragment{

    private CheckBox cbAutoSize;
    private CheckBox cbCrop;
    private MengEditText mengEtGifHeight;
    private MengEditText mengEtGifWidth;
    private MengEditText mengEtFrameDelay;
    private HashMap<Integer,Bitmap> dataMap=new HashMap<Integer,Bitmap>();
    private int bitmapFlag=0;
    private final int CROP_REQUEST_CODE=3;
    private int bmpH=0;
    private int bmpW=0;

    @Override
    public View onCreateView(LayoutInflater inflater,ViewGroup container,Bundle savedInstanceState){
        return inflater.inflate(R.layout.gif_creator,container,false);
    }

    @Override
    public void onViewCreated(View view,Bundle savedInstanceState){
        super.onViewCreated(view,savedInstanceState);
        cbAutoSize=(CheckBox)view.findViewById(R.id.gif_creator_autosize);
        cbCrop=(CheckBox)view.findViewById(R.id.gif_creator_crop);
        mengEtGifHeight=(MengEditText)view.findViewById(R.id.gif_creator_height);
        mengEtGifWidth=(MengEditText)view.findViewById(R.id.gif_creator_width);
        mengEtFrameDelay=(MengEditText)view.findViewById(R.id.gif_creator_delay);
        ((Button)view.findViewById(R.id.gif_creator_add)).setOnClickListener(listenerBtnClick);
        ((Button)view.findViewById(R.id.gif_creator_finish)).setOnClickListener(listenerBtnClick);
        cbAutoSize.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener(){
            @Override
            public void onCheckedChanged(CompoundButton buttonView,boolean isChecked){
                mengEtGifHeight.setVisibility(isChecked?View.GONE:View.VISIBLE);
                mengEtGifWidth.setVisibility(isChecked?View.GONE:View.VISIBLE);
            }
        });
    }

    View.OnClickListener listenerBtnClick=new View.OnClickListener(){


        @Override
        public void onClick(View v){
            switch(v.getId()){
                case R.id.gif_creator_add:
                    MainActivity2.selectImage(gifCreator.this);
                    break;
                case R.id.gif_creator_finish:
                    try{
                        String filePath=MainActivity.instence.getGifPath();
                        GifEncoder gifEncoder=new GifEncoder();
                        gifEncoder.setDither(false);
                        if(cbAutoSize.isChecked()){
                            bmpW=dataMap.get(0).getWidth();
                            bmpH=dataMap.get(0).getHeight();
                            gifEncoder.init(bmpW,bmpH,filePath,GifEncoder.EncodingType.ENCODING_TYPE_NORMAL_LOW_MEMORY);
                        }else{
                            bmpW=mengEtGifWidth.getInt();
                            bmpH=mengEtGifHeight.getInt();
                            gifEncoder.init(bmpW,bmpH,filePath,GifEncoder.EncodingType.ENCODING_TYPE_NORMAL_LOW_MEMORY);
                        }
                        for(int i=0;i<bitmapFlag;i++){
                            gifEncoder.encodeFrame(QrUtils.scale(dataMap.get(i),bmpW,bmpH),mengEtFrameDelay.getInt());
                        }
                        gifEncoder.close();
                        getActivity().getApplicationContext().sendBroadcast(new Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE,Uri.fromFile(new File(filePath))));
                        LogTool.t("完成 : "+filePath);
                        dataMap.clear();
                        bitmapFlag=0;
                    }catch(IOException e){
                        LogTool.e(e);
                    }
                    break;
            }
        }
    };

    @Override
    public void onActivityResult(int requestCode,int resultCode,Intent data){
        if(requestCode==MainActivity2.SELECT_FILE_REQUEST_CODE&&resultCode==getActivity().RESULT_OK&&data.getData()!=null){
            if(cbCrop.isChecked()){
                cropPhoto(
                        data.getData(),
                        cbAutoSize.isChecked()?bmpH:mengEtGifHeight.getInt(),
                        cbAutoSize.isChecked()?bmpW:mengEtGifWidth.getInt()
                );
            }else{
                dataMap.put(bitmapFlag,
                        BitmapFactory.decodeFile(
                                ContentHelper.absolutePathFromUri(getActivity(),data.getData())
                        ));
            }
        }else if(requestCode==CROP_REQUEST_CODE&&resultCode==getActivity().RESULT_OK){
            Bundle bundle=data.getExtras();
            if(bundle!=null){
                dataMap.put(bitmapFlag,(Bitmap)bundle.getParcelable("data"));
                bitmapFlag++;
                LogTool.t("图片添加成功");
            }else{
                LogTool.t("取消了添加图片");
            }
        }else if(resultCode==getActivity().RESULT_CANCELED){
            LogTool.t("取消选择图片");
        }
        super.onActivityResult(requestCode,resultCode,data);
    }

    private Uri cropPhoto(Uri uri,int height,int width){
        Intent intent=new Intent("com.android.camera.action.CROP");
        intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
        intent.addFlags(Intent.FLAG_GRANT_WRITE_URI_PERMISSION);
        intent.setDataAndType(uri,"image/*");
        intent.putExtra("crop","true");
        if(height!=0){
            intent.putExtra("outputX",height);
        }
        if(width!=0){
            intent.putExtra("outputY",width);
        }
        intent.putExtra("return-data",true);
        startActivityForResult(intent,CROP_REQUEST_CODE);
        return uri;
    }
}
