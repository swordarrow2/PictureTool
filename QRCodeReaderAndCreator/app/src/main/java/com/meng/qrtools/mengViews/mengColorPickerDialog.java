package com.meng.qrtools.mengViews;

import android.app.Dialog;
import android.content.Context;
import android.graphics.Color;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.meng.pictools.R;


/**
 * Created by Administrator on 2018/12/18.
 */

public class mengColorPickerDialog extends Dialog{
    Context context;
    EditText et;
    mengColorPicker mcp;

    public mengColorPickerDialog(Context context,EditText cs){
        super(context);
        this.context=context;
        et=cs;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
        this.setContentView(R.layout.meng_color_picker);
        final TextView tv=(TextView)findViewById(R.id.meng_color_textview);
        Button btnOK=(Button)findViewById(R.id.meng_color_picker_ok);
        Button btnCancal=(Button)findViewById(R.id.meng_color_picker_cancal);
        mcp=(mengColorPicker)findViewById(R.id.meng_color_picker);
        mcp.setOnColorBackListener(new mengColorPicker.OnColorBackListener(){
            @Override
            public void onColorBack(int a,int r,int g,int b){
                tv.setText("R："+r+"\nG："+g+"\nB："+b+"\n"+mcp.getStrColor());
                tv.setTextColor(Color.argb(a,r,g,b));
            }
        });
        btnOK.setOnClickListener(onClickListener);
        btnCancal.setOnClickListener(onClickListener);
    }

    private View.OnClickListener onClickListener=new View.OnClickListener(){
        @Override
        public void onClick(View v){
            switch(v.getId()){
                case R.id.meng_color_picker_ok:
                    et.setText(mcp.getStrColor());
                case R.id.meng_color_picker_cancal:
                    hide();
            }
        }
    };
}
