package com.meng.qrtools.views;

import android.content.Context;
import android.content.res.TypedArray;
import android.text.TextWatcher;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.meng.qrtools.R;

/**
 * Created by Administrator on 2018/8/9.
 */
public class mengEdittext extends LinearLayout {
    Context c;

    private EditText et;
    private TextView tv;

    public mengEdittext(Context c, AttributeSet a) {
        super(c, a);
        this.c = c;
        LayoutInflater.from(c).inflate(R.layout.meng_textview, this);
        tv = (TextView) findViewById(R.id.test_view_textview);
        et = (EditText)findViewById(R.id.test_view_edittext);
        TypedArray typedArray = c.obtainStyledAttributes(a, R.styleable.mengViews);
        tv.setText(typedArray.getString(R.styleable.mengViews_textviewText));
        et.setHint(typedArray.getString(R.styleable.mengViews_edittextHint));
        typedArray.recycle();
    }

    public String getText(){
        return et.getText().toString();
    }

    public String getHint(){
        return et.getHint().toString();
    }

    public void setText(String s){
        et.setText(s);
    }

    public void addTextChangedListener(TextWatcher twColor) {
        et.addTextChangedListener(twColor);
    }

    public void setTextColor(int textColor) {
        tv.setTextColor(textColor);
    }

    public boolean isEmpty() {
        if(et.getText().toString()==null||et.getText().toString().trim().length()==0){
            return true;
        }else {
            return false;
        }
    }
}
