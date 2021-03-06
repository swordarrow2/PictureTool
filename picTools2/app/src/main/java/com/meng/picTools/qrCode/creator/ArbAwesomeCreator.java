package com.meng.picTools.qrCode.creator;


import android.*;
import android.app.Activity;
import android.content.*;
import android.content.pm.*;
import android.graphics.*;
import android.net.*;
import android.os.*;
import android.support.v4.app.*;
import android.util.*;
import android.view.*;
import android.widget.*;

import com.meng.picTools.*;
import com.meng.picTools.libAndHelper.ContentHelper;
import com.meng.picTools.libAndHelper.FileHelper;
import com.meng.picTools.libAndHelper.FileType;
import com.meng.picTools.libAndHelper.QrUtils;
import com.meng.picTools.libAndHelper.mengViews.*;

import java.io.*;
import java.text.*;

import android.app.Fragment;
import android.support.v7.app.AlertDialog;

import com.meng.picTools.R;

public class ArbAwesomeCreator extends Fragment {

    private ImageView qrCodeImageView;
    private MengEditText mengEtDotScale, mengEtContents;
    private Button btGenerate;
    private CheckBox ckbAutoColor;
    private Button btnSave;
    private TextView imgPathTextView;
    private MengColorBar mColorBar;
    private MengScrollView sv;

    private MengSeekBar mengSeekBar;
    private Bitmap finallyBmp = null;
    private String selectedBmpPath = "";
    private int qrSize;
    private int selectedBmpWidth = 0;
    private int selectedBmpHeight = 0;
    private float screenW;
    private float screenH;
    private MengSelectRectView mengSelectView;
    private String[] PERMISSIONS_STORAGE = {
            Manifest.permission.READ_EXTERNAL_STORAGE,
            Manifest.permission.WRITE_EXTERNAL_STORAGE
    };

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.arb_awesome_qr, container, false);
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        sv = (MengScrollView) view.findViewById(R.id.awesomeqr_main_scrollView);
        mengSelectView = (MengSelectRectView) view.findViewById(R.id.arb_awesome_qrselectRectView);
        mColorBar = (MengColorBar) view.findViewById(R.id.gif_arb_qr_main_colorBar);
        qrCodeImageView = (ImageView) view.findViewById(R.id.awesomeqr_main_qrcode);
        mengEtContents = (MengEditText) view.findViewById(R.id.awesomeqr_main_content);
        mengEtDotScale = (MengEditText) view.findViewById(R.id.awesomeqr_main_dotScale);
        btGenerate = (Button) view.findViewById(R.id.awesomeqr_main_generate);
        ckbAutoColor = (CheckBox) view.findViewById(R.id.awesomeqr_main_autoColor);
        btnSave = (Button) view.findViewById(R.id.awesomeqr_mainButton_save);
        imgPathTextView = (TextView) view.findViewById(R.id.awesomeqr_main_imgPathTextView);
        mengSeekBar = (MengSeekBar) view.findViewById(R.id.awesomeqr_mainMengSeekBar);
        ((Button) view.findViewById(R.id.awesomeqr_main_backgroundImage)).setOnClickListener(click);
        btGenerate.setOnClickListener(click);
        btnSave.setOnClickListener(click);
        sv.setSelectView(mengSelectView);
        ckbAutoColor.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                mColorBar.setVisibility(isChecked ? View.GONE : View.VISIBLE);
                if (!isChecked) LogTool.t("如果颜色搭配不合理,二维码将会难以识别");
            }
        });
        DisplayMetrics dm = new DisplayMetrics();
        getActivity().getWindowManager().getDefaultDisplay().getMetrics(dm);
        screenW = dm.widthPixels;
        screenH = dm.heightPixels;
    }

    View.OnClickListener click = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            switch (v.getId()) {
                case R.id.awesomeqr_main_backgroundImage:
                    qrCodeImageView.setVisibility(View.GONE);
                    btGenerate.setEnabled(true);
                    btnSave.setVisibility(View.GONE);
                    MainActivity2.instence.selectImage(ArbAwesomeCreator.this);
                    break;
                case R.id.awesomeqr_main_generate:
                    generate();
                    mengSeekBar.setVisibility(View.GONE);
                    btnSave.setVisibility(View.VISIBLE);
                    qrCodeImageView.setVisibility(View.VISIBLE);
                    mengSelectView.setVisibility(View.GONE);
                    break;
                case R.id.awesomeqr_mainButton_save:
                    String s = FileHelper.saveBitmap(finallyBmp, FileType.awesomeQR);
                    if (s == null) {
                        LogTool.e("保存出错");
                        break;
                    }
                    LogTool.t("已保存至" + s);
                    getActivity().getApplicationContext().sendBroadcast(new Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE, Uri.fromFile(new File(s))));//更新图库
                    btnSave.setVisibility(View.GONE);
                    break;
            }
        }
    };

    @Override
    public void onResume() {
        super.onResume();
        acquireStoragePermissions();
    }

    private void acquireStoragePermissions() {
        int permission = ActivityCompat.checkSelfPermission(getActivity().getApplicationContext(), Manifest.permission.WRITE_EXTERNAL_STORAGE);
        if (permission != PackageManager.PERMISSION_GRANTED) {
            int REQUEST_EXTERNAL_STORAGE = 1;
            ActivityCompat.requestPermissions(getActivity(), PERMISSIONS_STORAGE, REQUEST_EXTERNAL_STORAGE);
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == MainActivity2.instence.SELECT_FILE_REQUEST_CODE && resultCode == Activity.RESULT_OK && data.getData() != null) {
            imgPathTextView.setVisibility(View.VISIBLE);
            Uri uri = data.getData();
            selectedBmpPath = ContentHelper.absolutePathFromUri(getActivity().getApplicationContext(), uri);
            imgPathTextView.setText(MessageFormat.format("当前文件：{0}", selectedBmpPath));
            final Bitmap selectedBmp = BitmapFactory.decodeFile(selectedBmpPath);
            selectedBmpWidth = selectedBmp.getWidth();
            selectedBmpHeight = selectedBmp.getHeight();
            final MengSeekBar msb = new MengSeekBar(getActivity());
            int maxProg = Math.min(selectedBmpWidth, selectedBmpHeight);
            msb.setMax(maxProg);
            msb.setProgress(maxProg / 3);
            new AlertDialog.Builder(getActivity())
                    .setTitle("输入要添加的二维码大小(像素)")
                    .setView(msb)
                    .setPositiveButton("确定", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface p1, int p2) {
                            mengSeekBar.setVisibility(View.VISIBLE);
                            mengSeekBar.setMax(msb.getMax());
                            mengSeekBar.setProgress(msb.getProgress());
                            mengSeekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
                                @Override
                                public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                                    mengSelectView.setSize(qrSize = mengSeekBar.getProgress());
                                }

                                @Override
                                public void onStartTrackingTouch(SeekBar seekBar) {

                                }

                                @Override
                                public void onStopTrackingTouch(SeekBar seekBar) {

                                }
                            });
                            qrSize = msb.getProgress();
                            mengSelectView.setup(selectedBmp, screenW, screenH, qrSize);
                            ViewGroup.LayoutParams para = mengSelectView.getLayoutParams();
                            para.height = (int) (screenW / selectedBmpWidth * selectedBmpHeight);
                            mengSelectView.setLayoutParams(para);
                            mengSelectView.setVisibility(View.VISIBLE);
                            if (para.height > screenH * 2 / 3) LogTool.t("可使用音量键滚动界面");
                            sv.post(new Runnable() {
                                public void run() {
                                    sv.fullScroll(View.FOCUS_DOWN);
                                }
                            });
                        }
                    }).show();
        } else if (resultCode == Activity.RESULT_CANCELED) {
            LogTool.t("用户取消了操作");
        } else {
            MainActivity2.instence.selectImage(ArbAwesomeCreator.this);
        }
        super.onActivityResult(requestCode, resultCode, data);
    }

    private void generate() {
        finallyBmp = QrUtils.generate(
                mengEtContents.getString(),
                Float.parseFloat(mengEtDotScale.getString()),
                ckbAutoColor.isChecked() ? Color.BLACK : mColorBar.getTrueColor(),
                ckbAutoColor.isChecked() ? Color.WHITE : mColorBar.getFalseColor(),
                ckbAutoColor.isChecked(),
                between(mengSelectView.getSelectLeft() / mengSelectView.getXishu(), 0, selectedBmpWidth - qrSize),
                between(mengSelectView.getSelectTop() / mengSelectView.getXishu(), 0, selectedBmpHeight - qrSize),
                qrSize,
                BitmapFactory.decodeFile(selectedBmpPath).copy(Bitmap.Config.ARGB_8888, true));
        qrCodeImageView.setImageBitmap(QrUtils.scaleBitmap(finallyBmp, mengSelectView.getXishu()));
    }

    private int between(float a, int min, int max) {
        if (a < min) a = min;
        if (a > max) a = max;
        return (int) a;
    }

    public void onKeyDown(int keyCode, KeyEvent event) {

        if ((keyCode == KeyEvent.KEYCODE_VOLUME_UP)) {
            sv.post(new Runnable() {
                public void run() {
                    sv.scrollBy(0, 0xffffff9c);//(0xffffff9c)16=(-100)10
                }
            });
        }
        if ((keyCode == KeyEvent.KEYCODE_VOLUME_DOWN)) {
            sv.post(new Runnable() {
                public void run() {
                    sv.scrollBy(0, 100);
                }
            });
        }

    }

}
