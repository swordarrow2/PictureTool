package com.meng.qrtools.views;

import android.content.*;
import android.content.res.*;
import android.util.*;
import android.view.*;
import android.widget.*;
import com.meng.qrtools.*;

/**
 * Created by Administrator on 2018/8/9.
 */
public class mengEdittextWithCheckBox extends LinearLayout {
    Context c;
    private CheckBox cb;
    private EditText et;
    private TextView tv;
    private boolean showOnCheck;

    public mengEdittextWithCheckBox(Context c, AttributeSet a) {
        super(c, a);
        this.c = c;
        LayoutInflater.from(c).inflate(R.layout.meng_textview_with_checkbox, this);
        tv = (TextView) findViewById(R.id.test_view_textview);
        et = (EditText)findViewById(R.id.test_view_edittext);
        cb = (CheckBox) findViewById(R.id.test_view_checkbox);
        TypedArray typedArray = c.obtainStyledAttributes(a, R.styleable.mengViews);
        tv.setText(typedArray.getString(R.styleable.mengViews_textviewText));
        et.setHint(typedArray.getString(R.styleable.mengViews_edittextHint));
        cb.setText(typedArray.getString(R.styleable.mengViews_checkboxText));
        cb.setChecked(typedArray.getBoolean(R.styleable.mengViews_checked,false));
        showOnCheck=typedArray.getBoolean(R.styleable.mengViews_showOnCheck,true);
        if(showOnCheck==cb.isChecked()){
            et.setVisibility(VISIBLE);
            tv.setVisibility(VISIBLE);
        }else {
            et.setVisibility(GONE);
            tv.setVisibility(GONE);
        }
        typedArray.recycle();
        cb.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if(showOnCheck==isChecked){
                    et.setVisibility(VISIBLE);
                    tv.setVisibility(VISIBLE);
                }else {
                    et.setVisibility(GONE);
                    tv.setVisibility(GONE);
                }
            }
        });
    }


    public boolean isChecked() {
        return cb.isChecked();
    }

    public String getString() {
        return isEmpty()?et.getHint().toString():et.getText().toString();
    }
	private boolean isEmpty() {
        if(et.getText()==null||et.getText().toString().trim().length()==0){
            return true;
        }else {
            return false;
        }
    }
}
