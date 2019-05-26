package com.meng.picTools.lib.mengViews;

import android.content.Context;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.widget.LinearLayout;
import android.widget.SeekBar;
import android.widget.TextView;

import com.meng.picTools.R;

import java.text.MessageFormat;

public class MengSeekBar extends LinearLayout{
    private TextView textView;
    private SeekBar seekBar;
    public MengSeekBar(Context context){
        super(context);
        afterCreate(context);
    }
    public MengSeekBar(Context context, AttributeSet attributeSet){
        super(context,attributeSet);
        afterCreate(context);
    }
    private void afterCreate(Context context){
        LayoutInflater.from(context).inflate(R.layout.meng_seekbar_view,this);
        textView =(TextView)findViewById(R.id.progress_view_tv);
        seekBar =(SeekBar)findViewById(R.id.progress_view_sb);
        seekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener(){
            @Override
            public void onProgressChanged(SeekBar seekBar,int progress,boolean fromUser){
                progress=progress%2==1?progress-1:progress;
				progress=progress<1?2:progress;
                textView.setText(MessageFormat.format("当前:{0}", progress));
				MengSeekBar.this.seekBar.setProgress(progress);
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar){

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar){
            }
        });
    }

    public void setMax(int max){
        seekBar.setMax(max);
    }
    public int getMax(){
        return seekBar.getMax();
    }

    public void setProgress(int progress){
        seekBar.setProgress(progress);
    }

    public int getProgress(){
        return seekBar.getProgress();
    }
    public void setOnSeekBarChangeListener(SeekBar.OnSeekBarChangeListener listener){
        seekBar.setOnSeekBarChangeListener(listener);
        textView.setText("二维码大小:");
    }
}
