package com.meng.picTools.qrCode.reader;

import android.app.Activity;
import android.app.Fragment;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.TextView;

import com.google.zxing.Result;
import com.meng.picTools.LogTool;
import com.meng.picTools.MainActivity2;
import com.meng.picTools.R;
import com.meng.picTools.libAndHelper.ContentHelper;
import com.meng.picTools.libAndHelper.SharedPreferenceHelper;
import com.meng.picTools.libAndHelper.QrUtils;
import com.meng.picTools.libAndHelper.ScreenShotListenService;

public class GalleryQRReader extends Fragment {
    private Button btnCreateAwesomeQR;
    private TextView tvResult;
    private TextView tvFormat;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.read_gallery, container, false);
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        Button btnOpenGallery = (Button) view.findViewById(R.id.read_galleryButton);
        tvResult = (TextView) view.findViewById(R.id.read_galleryTextView_result);
        tvFormat = (TextView) view.findViewById(R.id.read_galleryTextView_format);
        btnCreateAwesomeQR = (Button) view.findViewById(R.id.read_galleryButton_createAwesomeQR);
        btnOpenGallery.setOnClickListener(click);
        btnCreateAwesomeQR.setOnClickListener(click);
        CheckBox cbAutoRead = (CheckBox) view.findViewById(R.id.read_gallery_autoread);
        boolean b = SharedPreferenceHelper.getBoolean("service", false);
        cbAutoRead.setChecked(b);
        if (b) {
            startService();
        } else {
            stopService();
        }
        cbAutoRead.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                SharedPreferenceHelper.putBoolean("service", isChecked);
                if (isChecked) {
                    startService();
                } else {
                    stopService();
                }
            }
        });
    }

    private OnClickListener click = new OnClickListener() {
        @Override
        public void onClick(View v) {
            switch (v.getId()) {
                case R.id.read_galleryButton:
                    MainActivity2.instence.selectImage(GalleryQRReader.this);
                    break;
                case R.id.read_galleryButton_createAwesomeQR:
                    MainActivity2.instence.showAwesomeFragment(true);
                    MainActivity2.instence.awesomeCreatorFragment.setDataStr(tvResult.getText().toString());
                    break;
            }
        }
    };

    public void handleDecode(Result result, Bitmap barcode) {
        String resultString = result.getText();
        MainActivity2.instence.doVibrate(200L);
        handleResult(resultString, result.getBarcodeFormat().toString());
    }

    protected void handleResult(final String resultString, String format) {
        if (resultString.equals("")) {
            LogTool.e(R.string.scan_failed);
        } else {
            tvFormat.setText(String.format("二维码类型%s", format));
            tvResult.setText(resultString);
            btnCreateAwesomeQR.setVisibility(View.VISIBLE);
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (resultCode == Activity.RESULT_OK && data != null && requestCode == MainActivity2.instence.SELECT_FILE_REQUEST_CODE) {
            Uri inputUri = data.getData();
            String path = ContentHelper.absolutePathFromUri(getActivity(), inputUri);
            if (!TextUtils.isEmpty(path)) {
                Result result = QrUtils.decodeImage(path);
                if (result != null) {
                    handleDecode(result, null);
                } else {
                    LogTool.t("此图片无法识别");
                }
            } else {
                LogTool.t("图片路径未找到");
            }
        }
    }

    private void startService() {
        getActivity().startService(new Intent(getActivity(), ScreenShotListenService.class));
    }

    private void stopService() {
        getActivity().stopService(new Intent(getActivity(), ScreenShotListenService.class));
    }

}
