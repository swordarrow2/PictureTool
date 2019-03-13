package com.meng.picTools.qrtools.mengViews;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.graphics.Color;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.meng.picTools.R;

public class mengColorBar extends LinearLayout{
    Context c;

    private EditText etTrue;
    private TextView tvTrue;
    private EditText etFalse;
    private TextView tvFalse;
    private Button btnTrue;
    private Button btnFalse;
    private ImageButton ib;

    public mengColorBar(Context c,AttributeSet a){
        super(c,a);
        this.c=c;
        LayoutInflater.from(c).inflate(R.layout.meng_colorbar,this);
        tvTrue=(TextView)findViewById(R.id.meng_colorbar_textview_trueDot);
        tvFalse=(TextView)findViewById(R.id.meng_colorbar_textview_falseDot);
        etTrue=(EditText)findViewById(R.id.meng_colorbar_edittext_true);
        etFalse=(EditText)findViewById(R.id.meng_colorbar_edittext_false);
        btnTrue=(Button)findViewById(R.id.meng_colorbar_button_select_true_color);
        btnFalse=(Button)findViewById(R.id.meng_colorbar_button_select_false_color);
        ib=(ImageButton)findViewById(R.id.meng_colorbar_imagebutton);
        etTrue.addTextChangedListener(tw);
        etFalse.addTextChangedListener(tw);
        btnTrue.setOnClickListener(clickListener);
        btnFalse.setOnClickListener(clickListener);
        ib.setOnClickListener(clickListener);
    }

    private OnClickListener clickListener=new OnClickListener(){
        @Override
        public void onClick(View v){
            switch(v.getId()){
                case R.id.meng_colorbar_button_select_true_color:
                    Dialog dialog=new mengColorPickerDialog(getContext(),etTrue);
                    dialog.show();
                    break;
                case R.id.meng_colorbar_button_select_false_color:
                    Dialog dialog2=new mengColorPickerDialog(getContext(),etFalse);
                    dialog2.show();
                    break;
                case R.id.meng_colorbar_imagebutton:
                    new AlertDialog.Builder(c).setTitle("").setMessage("真值点就是普通二维码中的黑色部分,其余部分为假值点").setPositiveButton("我知道了",null).show();
                    break;
            }
        }
    };

    public int getTrueColor(){
        return etTrue.getText().toString().trim().length()==0?
                Color.parseColor(etTrue.getHint().toString()):
                Color.parseColor(etTrue.getText().toString());
    }

    public int getFalseColor(){
        return etFalse.getText().toString().trim().length()==0?
                Color.parseColor(etFalse.getHint().toString()):
                Color.parseColor(etFalse.getText().toString());
    }

    TextWatcher tw=new TextWatcher(){

        @Override
        public void beforeTextChanged(CharSequence p1,int p2,int p3,int p4){
        }

        @Override
        public void onTextChanged(CharSequence p1,int p2,int p3,int p4){
            try{
                tvTrue.setTextColor(getTrueColor());
            }catch(Exception e){
                tvTrue.setTextColor(Color.BLACK);
            }
            try{
                tvFalse.setTextColor(getFalseColor());
            }catch(Exception e){
                tvFalse.setTextColor(Color.BLACK);
            }
        }

        @Override
        public void afterTextChanged(Editable p1){
        }
    };
}
