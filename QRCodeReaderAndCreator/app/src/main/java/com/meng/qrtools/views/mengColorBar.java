package com.meng.qrtools.views;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Color;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.meng.qrtools.R;

/**
 * Created by Administrator on 2018/8/13.
 */

public class mengColorBar extends LinearLayout {
    Context c;

    private EditText etTrue;
    private TextView tvTrue;
    private EditText etFalse;
    private TextView tvFalse;

    public mengColorBar(Context c, AttributeSet a) {
        super(c, a);
        this.c = c;
        LayoutInflater.from(c).inflate(R.layout.meng_colorbar, this);
        tvTrue = (TextView) findViewById(R.id.test_view_textview_trueDot);
        tvFalse = (TextView) findViewById(R.id.test_view_textview_falseDot);
        etTrue = (EditText) findViewById(R.id.test_view_edittext_true);
        etFalse = (EditText) findViewById(R.id.test_view_edittext_false);
        etTrue.addTextChangedListener(tw);
        etFalse.addTextChangedListener(tw);
    }

    public int getTrueColor() {
        return etTrue.getText().toString().trim().length() == 0 ?
                Color.parseColor(etTrue.getHint().toString()) :
                Color.parseColor(etTrue.getText().toString());
    }

    public int getFalseColor() {
        return etFalse.getText().toString().trim().length() == 0 ?
                Color.parseColor(etTrue.getHint().toString()) :
                Color.parseColor(etTrue.getText().toString());
    }

    TextWatcher tw = new TextWatcher() {

        @Override
        public void beforeTextChanged(CharSequence p1, int p2, int p3, int p4) {
        }

        @Override
        public void onTextChanged(CharSequence p1, int p2, int p3, int p4) {
            try {
                tvTrue.setTextColor(getTrueColor());
            } catch (Exception e) {
                tvTrue.setTextColor(Color.BLACK);
            }
            try {
                tvFalse.setTextColor(getFalseColor());
            } catch (Exception e) {
                tvFalse.setTextColor(Color.BLACK);
            }
        }

        @Override
        public void afterTextChanged(Editable p1) {

        }
    };
}