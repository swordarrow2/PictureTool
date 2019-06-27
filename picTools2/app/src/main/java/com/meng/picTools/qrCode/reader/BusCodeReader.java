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
import android.widget.TextView;

import com.google.zxing.Result;
import com.meng.picTools.LogTool;
import com.meng.picTools.MainActivity2;
import com.meng.picTools.R;
import com.meng.picTools.libAndHelper.ContentHelper;
import com.meng.picTools.libAndHelper.QrUtils;

public class BusCodeReader extends Fragment {
    private TextView tvResult;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.bus_read_gallery, container, false);
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        Button btnOpenGallery = (Button) view.findViewById(R.id.read_galleryButton);
        tvResult = (TextView) view.findViewById(R.id.read_galleryTextView_result);
        btnOpenGallery.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                MainActivity2.instence.selectImage(BusCodeReader.this);
            }
        });
    }


    public void handleDecode(Result result, Bitmap barcode) {
        String resultString = result.getText();
        MainActivity2.instence.doVibrate(200L);
        handleResult(resultString, result.getBarcodeFormat().toString());
    }

    protected void handleResult(final String resultString, String format) {
        if (resultString.equals("")) {
            LogTool.e(R.string.scan_failed);
        } else {
            tvResult.setText(resultString);
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (resultCode == Activity.RESULT_OK && data != null && requestCode == MainActivity2.instence.SELECT_FILE_REQUEST_CODE) {
            Uri inputUri = data.getData();
            String path = ContentHelper.absolutePathFromUri(getActivity(), inputUri);
            if (!TextUtils.isEmpty(path)) {
                Result result = QrUtils.decodeImage(QrUtils.decryBitmap(path));
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
}
