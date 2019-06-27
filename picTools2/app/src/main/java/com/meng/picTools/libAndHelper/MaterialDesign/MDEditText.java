package com.meng.picTools.libAndHelper.MaterialDesign;

import android.content.*;
import android.content.res.*;
import android.support.annotation.*;
import android.support.design.widget.*;
import android.support.v4.content.*;
import android.text.*;
import android.util.*;
import android.view.*;
import android.widget.*;
import com.meng.picTools.*;

public class MDEditText extends LinearLayout {

    private Context mContext;

    private EditText mEditText;
    private TextInputLayout mInputLayout;

    public MDEditText(Context cxt, AttributeSet attrs) {
        super(cxt, attrs);
        mContext = cxt;
        init();
        TypedArray a = mContext.obtainStyledAttributes(attrs, R.styleable.MDEdittext);
        if (a != null) {
            int textColor = a.getColor(R.styleable.MDEdittext_textColor,  ContextCompat.getColor(mContext, R.color.color_c4));
            mEditText.setTextColor(textColor);
            float textSize = a.getDimension(R.styleable.MDEdittext_textSize,mContext.getResources().getDimension(R.dimen.common_s3));
            mEditText.setTextSize(TypedValue.COMPLEX_UNIT_PX, textSize);
            String hint = a.getString(R.styleable.MDEdittext_hint);
            mInputLayout.setHint(hint);
            mInputLayout.setErrorEnabled(a.getBoolean(R.styleable.MDEdittext_errorEnable, false));
            if (mInputLayout.isErrorEnabled()) {
                mInputLayout.setError("");
			  }
            a.recycle();
		  }
	  }

    private void init() {
        LayoutInflater.from(mContext).inflate(R.layout.conmon_edittext_layout, this, true);
        mEditText = (EditText) findViewById(R.id.edittext);
        mInputLayout = (TextInputLayout) findViewById(R.id.input_layout);
	  }

    public TextInputLayout getInputLayout() {
        return mInputLayout;
	  }

    public EditText getEditText() {
        return mEditText;
	  }

    public void setHintAnimationEnabled(boolean enabled) {
        mInputLayout.setHintAnimationEnabled(enabled);
	  }

    public void setErrorEnabled(boolean enabled) {
        mInputLayout.setErrorEnabled(enabled);
	  }

    public void setError(CharSequence error) {
        mInputLayout.setError(error);
	  }

    public void setHintTextAppearance(int resid) {
        mInputLayout.setHintTextAppearance(resid);
	  }

    public void setText(CharSequence text) {
        mEditText.setText(text);
	  }

    public void setText(@StringRes int resid) {
        mEditText.setText(resid);

	  }

    public void setSelection(int index) {
        mEditText.setSelection(index);

	  }

    public void addTextChangedListener(TextWatcher watcher) {
        mEditText.addTextChangedListener(watcher);
	  }

    public void setOnFocusChangeListener(OnFocusChangeListener l) {
        mEditText.setOnFocusChangeListener(l);
	  }
  }
