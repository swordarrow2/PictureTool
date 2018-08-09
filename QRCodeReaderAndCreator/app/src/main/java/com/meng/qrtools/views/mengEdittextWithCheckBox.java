package com.meng.qrtools.views;

import android.content.Context;
import android.content.res.TypedArray;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.meng.qrtools.R;

/**
 * Created by Administrator on 2018/8/9.
 */
public class mengEdittextWithCheckBox extends LinearLayout {
    Context c;
    private CheckBox cb;
    private EditText et;
    private TextView tv;

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
        typedArray.recycle();
    }


}
