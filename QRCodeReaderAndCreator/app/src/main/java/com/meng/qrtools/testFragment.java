package com.meng.qrtools;

import android.Manifest;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.os.SystemClock;
import android.support.v4.app.ActivityCompat;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

import com.meng.MainActivity2;
import com.meng.qrtools.creator.ContentHelper;
import com.meng.qrtools.creator.sizeSelect;
import com.meng.qrtools.lib.qrcodelib.AwesomeQRCode;
import com.meng.qrtools.lib.qrcodelib.QRCode;
import com.meng.qrtools.views.mengColorBar;
import com.meng.qrtools.views.mengEdittext;

import java.io.File;
import java.io.IOException;

/**
 * Created by Administrator on 2018/7/19.
 */

public class testFragment extends android.app.Fragment {

    private ImageView qrCodeImageView;
    private mengEdittext mengEtDotScale, mengEtContents;
    private Button btGenerate, btSelectBG, btRemoveBG;
    private boolean generating = false;
    private CheckBox ckbAutoColor;
    private ScrollView scrollView;
    private Button btnSave;
    private TextView imgPathTextView;
    private mengColorBar mColorBar;
    private static final int CROP_REQUEST_CODE = 3;
    public static final int selectRect = 2;

    private static final int REQUEST_EXTERNAL_STORAGE = 1;
    private static String[] PERMISSIONS_STORAGE = {
            Manifest.permission.READ_EXTERNAL_STORAGE,
            Manifest.permission.WRITE_EXTERNAL_STORAGE
    };
    private Bitmap selectedBmp = null;
    private Bitmap finallyBmp = null;
    private Bitmap tmpBackground = null;
    private float topMargin = 0f;
    private float leftMargin = 0f;
    private int qrSize = 0;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        // TODO: Implement this method
        return inflater.inflate(R.layout.test, container, false);
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        // TODO: Implement this method
        super.onViewCreated(view, savedInstanceState);
        mColorBar = (mengColorBar) view.findViewById(R.id.awesomeqr_main_colorBar);
        scrollView = (ScrollView) view.findViewById(R.id.awesomeqr_main_scrollView);
        qrCodeImageView = (ImageView) view.findViewById(R.id.awesomeqr_main_qrcode);
        mengEtContents = (mengEdittext) view.findViewById(R.id.awesomeqr_main_content);
        mengEtDotScale = (mengEdittext) view.findViewById(R.id.awesomeqr_main_dotScale);
        btSelectBG = (Button) view.findViewById(R.id.awesomeqr_main_backgroundImage);
        btRemoveBG = (Button) view.findViewById(R.id.awesomeqr_main_removeBackgroundImage);
        btGenerate = (Button) view.findViewById(R.id.awesomeqr_main_generate);
        ckbAutoColor = (CheckBox) view.findViewById(R.id.awesomeqr_main_autoColor);
        btnSave = (Button) view.findViewById(R.id.awesomeqr_mainButton_save);
        imgPathTextView = (TextView) view.findViewById(R.id.awesomeqr_main_imgPathTextView);
        ckbAutoColor.setOnCheckedChangeListener(check);
        btSelectBG.setOnClickListener(click);
        btRemoveBG.setOnClickListener(click);
        btGenerate.setOnClickListener(click);
        btnSave.setOnClickListener(click);
    }

    CompoundButton.OnCheckedChangeListener check = new CompoundButton.OnCheckedChangeListener() {
        @Override
        public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
            switch (buttonView.getId()) {
                case R.id.awesomeqr_main_autoColor:
                    mColorBar.setVisibility(isChecked ? View.GONE : View.VISIBLE);
                    break;
            }
        }
    };

    View.OnClickListener click = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            switch (v.getId()) {
                case R.id.awesomeqr_main_backgroundImage:
                    MainActivity2.selectImage(testFragment.this);
                    break;
                case R.id.awesomeqr_main_removeBackgroundImage:
                    tmpBackground = null;
                    imgPathTextView.setVisibility(View.GONE);
                    log.t(getActivity(), getResources().getString(R.string.Background_image_removed));
                    break;
                case R.id.awesomeqr_main_generate:
                    generate(mengEtContents.getString(),
                            qrSize,
                            Float.parseFloat(mengEtDotScale.getString()),
                            mColorBar.getTrueColor(),
                            ckbAutoColor.isChecked() ? Color.WHITE : mColorBar.getFalseColor(),
                            tmpBackground,
                            ckbAutoColor.isChecked()
                    );
                    btnSave.setVisibility(View.VISIBLE);
                    break;
                case R.id.awesomeqr_mainButton_save:
                    try {
                        String s = QRCode.saveMyBitmap(
                                Environment.getExternalStorageDirectory().getAbsolutePath() +
                                        "/Pictures/QRcode/AwesomeQR" + SystemClock.elapsedRealtime() + ".png",
                                finallyBmp);
                        log.t(getActivity(), "已保存至" + s);
                        getActivity().getApplicationContext().sendBroadcast(new Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE, Uri.fromFile(new File(s))));//更新图库
                    } catch (IOException e) {
                        log.e(getActivity(), e);
                    }
                    break;
            }
        }
    };

    public void setDataStr(String s) {
        mengEtContents.setString(s);
    }

    @Override
    public void onResume() {
        super.onResume();
        acquireStoragePermissions();
    }

    private void acquireStoragePermissions() {
        int permission = ActivityCompat.checkSelfPermission(getActivity().getApplicationContext(), Manifest.permission.WRITE_EXTERNAL_STORAGE);
        if (permission != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(
                    getActivity(),
                    PERMISSIONS_STORAGE,
                    REQUEST_EXTERNAL_STORAGE
            );
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == MainActivity2.SELECT_FILE_REQUEST_CODE && resultCode == getActivity().RESULT_OK && data.getData() != null) {
            imgPathTextView.setVisibility(View.VISIBLE);
            Uri uri = data.getData();
            String path = ContentHelper.absolutePathFromUri(getActivity().getApplicationContext(), uri);
            imgPathTextView.setText("当前文件：" + path);
            selectedBmp = BitmapFactory.decodeFile(path).copy(Bitmap.Config.ARGB_8888, true);
            cropPhoto(Math.min(selectedBmp.getWidth(), selectedBmp.getHeight()), path);
        } else if (requestCode == selectRect && resultCode == getActivity().RESULT_OK) {
            byte[] bis = data.getByteArrayExtra("bitmap");
            tmpBackground = BitmapFactory.decodeByteArray(bis, 0, bis.length);
            leftMargin = data.getFloatExtra("left", 0f);
            topMargin = data.getFloatExtra("top", 0);
        } else if (resultCode == getActivity().RESULT_CANCELED) {
            Toast.makeText(getActivity().getApplicationContext(), "取消选择图片", Toast.LENGTH_SHORT).show();
        } else {
            MainActivity2.selectImage(testFragment.this);
        }
        super.onActivityResult(requestCode, resultCode, data);
    }

    private void generate(final String contents, final int size, final float dotScale, final int colorDark,
                          final int colorLight, final Bitmap background, final boolean autoColor) {
        if (generating) return;
        generating = true;
        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    final Bitmap bmpQRcode = AwesomeQRCode.create(contents, size, 0, dotScale, colorDark, colorLight, background, false, autoColor, false, 128);
                    getActivity().runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            qrCodeImageView.setImageBitmap(bmpQRcode);
                            finallyBmp = selectedBmp;
                            Canvas c = new Canvas(finallyBmp);
                            c.drawBitmap(bmpQRcode, leftMargin, topMargin, new Paint());
                            qrCodeImageView.setImageBitmap(finallyBmp);
                            scrollView.post(new Runnable() {
                                @Override
                                public void run() {
                                    scrollView.fullScroll(View.FOCUS_DOWN);
                                }
                            });
                            generating = false;
                        }
                    });
                } catch (Exception e) {
                    log.e(getActivity(), e);
                    generating = false;
                }
            }
        }).start();
    }

    private void cropPhoto(final int maxSize, final String path) {
        final EditText et = new EditText(getActivity());
        et.setHint("0<大小<" + (maxSize + 1));
        new AlertDialog.Builder(getActivity())
                .setTitle("输入要添加的二维码大小(像素)")
                .setView(et)
                .setPositiveButton("确定", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface p1, int p2) {
                        qrSize = Integer.parseInt(et.getText().toString());
                        Intent i = new Intent(getActivity(), sizeSelect.class);
                        i.putExtra("content", path);
                        i.putExtra("size", qrSize);
                        startActivityForResult(i, selectRect);
                    }
                }).show();
    }
}
