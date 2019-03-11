package com.meng.qrtools.reader;

import android.Manifest;
import android.app.AlertDialog;
import android.app.Fragment;
import android.app.FragmentTransaction;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Build;
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
import android.widget.Toast;

import com.google.zxing.Result;
import com.meng.MainActivity2;
import com.meng.qrtools.MainActivity;
import com.meng.pictools.R;
import com.meng.qrtools.lib.ContentHelper;
import com.meng.qrtools.lib.qrcodelib.QrUtils;

public class BusReader extends Fragment {
    private final int REQUEST_PERMISSION_PHOTO = 1001;
    private Button btnOpenGallery;
    private TextView tvResult;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.bus_read_gallery, container, false);
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        btnOpenGallery = (Button) view.findViewById(R.id.read_galleryButton);
        tvResult = (TextView) view.findViewById(R.id.read_galleryTextView_result);
        btnOpenGallery.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                openGallery();
            }
        });

    }


    public void handleDecode(Result result, Bitmap barcode) {
        String resultString = result.getText();
        MainActivity.instence.doVibrate(200L);
        handleResult(resultString, result.getBarcodeFormat().toString());
    }

    protected void handleResult(final String resultString, String format) {
        if (resultString.equals("")) {
            Toast.makeText(getActivity(), R.string.scan_failed, Toast.LENGTH_SHORT).show();
        } else {
            tvResult.setText(resultString);
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (resultCode == getActivity().RESULT_OK && data != null && requestCode == MainActivity2.SELECT_FILE_REQUEST_CODE) {
            Uri inputUri = data.getData();
            String path = ContentHelper.absolutePathFromUri(getActivity(), inputUri);
            if (!TextUtils.isEmpty(path)) {
                Result result = QrUtils.decodeImage(QrUtils.decryBitmap(path));
                if (result != null) {
                    handleDecode(result, null);
                } else {
                    new AlertDialog.Builder(getActivity()).setTitle("提示").setMessage("此图片无法识别").setPositiveButton("确定", null).show();
                }
            } else {
                Toast.makeText(getActivity().getApplicationContext(), "图片路径未找到", Toast.LENGTH_SHORT).show();
            }
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (grantResults.length > 0 && requestCode == REQUEST_PERMISSION_PHOTO) {
            if (grantResults[0] != PackageManager.PERMISSION_GRANTED) {
                new AlertDialog.Builder(getActivity())
                        .setTitle("提示")
                        .setMessage("请在系统设置中为App中开启文件权限后重试")
                        .setPositiveButton("确定", null)
                        .show();
            } else {
                MainActivity2.selectImage(this);
            }
        }
    }

    public void openGallery() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M
                && getActivity().checkSelfPermission(Manifest.permission.READ_EXTERNAL_STORAGE)
                != PackageManager.PERMISSION_GRANTED) {
            requestPermissions(new String[]{Manifest.permission.READ_EXTERNAL_STORAGE},
                    REQUEST_PERMISSION_PHOTO);
        } else {
            MainActivity2.selectImage(this);
        }
    }


}
