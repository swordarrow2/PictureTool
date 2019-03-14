package com.meng.picTools.mengViews;

import android.app.Dialog;
import android.content.Context;
import android.graphics.Color;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.meng.picTools.R;


public class MengColorPickerDialog extends Dialog{
    Context context;
    EditText editText;
    MengColorPicker mengColorPicker;

    public MengColorPickerDialog(Context context, EditText editText){
        super(context);
        this.context=context;
        this.editText =editText;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
        this.setContentView(R.layout.meng_color_picker);
        final TextView tv=(TextView)findViewById(R.id.meng_color_textview);
        Button btnOK=(Button)findViewById(R.id.meng_color_picker_ok);
        Button btnCancal=(Button)findViewById(R.id.meng_color_picker_cancal);
        mengColorPicker =(MengColorPicker)findViewById(R.id.meng_color_picker);
        mengColorPicker.setOnColorBackListener(new MengColorPicker.OnColorBackListener(){
            @Override
            public void onColorBack(int a,int r,int g,int b){
                tv.setText("R："+r+"\nG："+g+"\nB："+b+"\n"+ mengColorPicker.getStrColor());
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
                    editText.setText(mengColorPicker.getStrColor());
                case R.id.meng_color_picker_cancal:
                    hide();
            }
        }
    };
}
