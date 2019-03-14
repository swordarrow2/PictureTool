package com.meng.picTools.mengViews;

import android.content.Context;
import android.content.res.TypedArray;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.meng.picTools.R;

public class MengEditText extends LinearLayout{
    Context context;

    private EditText editText;
    private TextView textView;

    public MengEditText(Context context, AttributeSet attributeSet){
        super(context,attributeSet);
        this.context =context;
        LayoutInflater.from(context).inflate(R.layout.meng_textview,this);
        textView =(TextView)findViewById(R.id.test_view_textview);
        editText =(EditText)findViewById(R.id.test_view_edittext);
        TypedArray typedArray=context.obtainStyledAttributes(attributeSet,R.styleable.mengViews);
        textView.setText(typedArray.getString(R.styleable.mengViews_textviewText));
        editText.setHint(typedArray.getString(R.styleable.mengViews_edittextHint));
        typedArray.recycle();
    }

    public String getString(){
        return isEmpty()? editText.getHint().toString(): editText.getText().toString();
    }

    public int getInt(){
        return Integer.parseInt(getString());
    }

    public void setString(String s){
        editText.setText(s);
    }

    private boolean isEmpty(){
        if(editText.getText().toString().trim().length()==0){
            return true;
        }else{
            return false;
        }
    }

    @Override
    public void setEnabled(boolean enabled){
        super.setEnabled(enabled);
        setVisibility(enabled?VISIBLE:GONE);
    }
}
