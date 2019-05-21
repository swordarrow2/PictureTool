package com.meng.picTools.qrCode.reader;



import android.*;
import android.app.*;
import android.content.*;
import android.content.pm.*;
import android.graphics.*;
import android.net.*;
import android.os.*;
import android.text.*;
import android.view.*;
import android.view.View.*;
import android.widget.*;
import com.google.zxing.*;
import com.meng.picTools.activity.*;
import com.meng.picTools.helpers.ContentHelper;
import com.meng.picTools.lib.QrUtils;

import android.support.v7.app.AlertDialog;
import com.meng.picTools.R;

public class BusCodeReader extends Fragment {
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
        if (resultCode == Activity.RESULT_OK && data != null && requestCode == MainActivity.instence.SELECT_FILE_REQUEST_CODE) {
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
                MainActivity.instence.selectImage(this);
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
            MainActivity.instence.selectImage(this);
        }
    }


}
