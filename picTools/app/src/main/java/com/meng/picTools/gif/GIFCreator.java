package com.meng.picTools.gif;

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

import com.meng.picTools.MainActivity;
import com.meng.picTools.MainActivity2;
import com.meng.picTools.R;
import com.meng.picTools.LogTool;
import com.meng.picTools.lib.AnimatedGifEncoder;
import com.meng.picTools.lib.ContentHelper;
import com.meng.picTools.lib.mengViews.MengEditText;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;

/**
 * gif生成
 */

public class GIFCreator extends Fragment{

    private CheckBox cbAutoSize;
    private MengEditText mengEtGifHeight;
    private MengEditText mengEtGifWidth;
    private MengEditText mengEtFrameDelay;
    public ArrayList<GIFFrame> selected = new ArrayList<>();

    @Override
    public View onCreateView(LayoutInflater inflater,ViewGroup container,Bundle savedInstanceState){
        return inflater.inflate(R.layout.gif_creator,container,false);
    }

    @Override
    public void onViewCreated(View view,Bundle savedInstanceState){
        super.onViewCreated(view,savedInstanceState);
        cbAutoSize=(CheckBox)view.findViewById(R.id.gif_creator_autosize);
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
                    Intent intent=new Intent(getActivity(),GIFSelectFrame.class);
                    getActivity().startActivityForResult(intent,9961);
                    break;
                case R.id.gif_creator_finish:
				  LogTool.t("开始生成gif");
				  new Thread(new Runnable(){

						@Override
						public void run() {
							try{
								String filePath= MainActivity.instence.getGifPath();
								ByteArrayOutputStream baos = new ByteArrayOutputStream();
								AnimatedGifEncoder localAnimatedGifEncoder = new AnimatedGifEncoder();
								localAnimatedGifEncoder.start(baos);//start
								localAnimatedGifEncoder.setRepeat(0);//设置生成gif的开始播放时间。0为立即开始播放
								  for(GIFFrame gifFrame:selected){
                                      localAnimatedGifEncoder.setDelay(gifFrame.delay);
                                      localAnimatedGifEncoder.addFrame(gifFrame.bitmap);
                                  }
								localAnimatedGifEncoder.finish();
								try{
									FileOutputStream fos = new FileOutputStream(filePath);
									baos.writeTo(fos);
									baos.flush();
									fos.flush();
									baos.close();
									fos.close();
								  }catch(IOException e){
									LogTool.e("gif异常"+e.toString());
								  }
								getActivity().getApplicationContext().sendBroadcast(new Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE, Uri.fromFile(new File(filePath))));
								LogTool.t("完成 : "+filePath);
							  }catch(Exception e){
								LogTool.e(e);
							  }
						  }
					  }).start();
                    break;
            }
        }
    };

    @Override
    public void onActivityResult(int requestCode,int resultCode,Intent data){
        if(requestCode==9961&&resultCode==getActivity().RESULT_OK&&data.getData()!=null){

        }
        super.onActivityResult(requestCode,resultCode,data);
    }

}
