package com.meng.picTools.libAndHelper.mengViews;

import android.app.Dialog;
import android.content.Context;
import android.graphics.Color;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.meng.picTools.R;

import java.text.MessageFormat;


public class MengColorPickerDialog extends Dialog{
    private EditText editText;
    private MengColorPicker mengColorPicker;

    public MengColorPickerDialog(Context context, EditText editText){
        super(context);
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
                tv.setText(MessageFormat.format("R：{0}\nG：{1}\nB：{2}\n{3}", r, g, b, mengColorPicker.getStrColor()));
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
