package com.meng.bilibiliDanmakuSender;

import android.content.*;
import android.content.res.*;
import android.util.*;
import android.view.*;
import android.widget.*;
import com.meng.bilibiliDanmakuSender.*;

/**
 * Created by Administrator on 2018/8/9.
 */
public class mengEdittext extends LinearLayout{
    Context c;

    private EditText et;
    private TextView tv;

    public mengEdittext(Context c,AttributeSet a){
        super(c,a);
        this.c=c;
        LayoutInflater.from(c).inflate(R.layout.meng_textview,this);
        tv=(TextView)findViewById(R.id.test_view_textview);
        et=(EditText)findViewById(R.id.test_view_edittext);
        TypedArray typedArray=c.obtainStyledAttributes(a,R.styleable.mengViews);
        tv.setText(typedArray.getString(R.styleable.mengViews_textviewText));
        et.setHint(typedArray.getString(R.styleable.mengViews_edittextHint));
        typedArray.recycle();
    }

    public String getString(){
        return isEmpty()?et.getHint().toString():et.getText().toString();
    }

    public int getInt(){
        return Integer.parseInt(getString());
    }

    public void setString(String s){
        et.setText(s);
    }

    private boolean isEmpty(){
        if(et.getText().toString().trim().length()==0){
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
