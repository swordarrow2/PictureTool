package com.meng.picTools.qrCode.creator;

import android.app.Fragment;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.meng.picTools.MainActivity;
import com.meng.picTools.MainActivity2;
import com.meng.picTools.R;
import com.meng.picTools.LogTool;
import com.meng.picTools.lib.AnimatedGifDecoder;
import com.meng.picTools.lib.AnimatedGifEncoder;
import com.meng.picTools.lib.ContentHelper;
import com.meng.picTools.qrCode.qrcodelib.AwesomeQRCode;
import com.meng.picTools.lib.mengViews.MengColorBar;
import com.meng.picTools.lib.mengViews.MengEditText;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;

public class AnimGIFAwesomeQr extends Fragment {

    private boolean coding = false;
    private Button btnSelectImage;
    private CheckBox cbAutoColor;
    private MengEditText mengEtDotScale;
    private MengEditText mengEtTextToEncode;
    private CheckBox cbAutoSize;
    private MengEditText mengEtSize;
    private ProgressBar pbCodingProgress;
    private String strSelectedGifPath = "";
    private TextView tvImagePath;
    private MengColorBar mColorBar;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.gif_qr_main, container, false);
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        mColorBar = (MengColorBar) view.findViewById(R.id.gif_arb_qr_main_colorBar);
        Button btnEncodeGif = (Button) view.findViewById(R.id.gif_arb_qr_button_encode_gif);
        btnSelectImage = (Button) view.findViewById(R.id.gif_arb_qr_button_selectImg);
        cbAutoColor = (CheckBox) view.findViewById(R.id.gif_arb_qr_checkbox_autocolor);
        mengEtDotScale = (MengEditText) view.findViewById(R.id.gif_arb_qr_mengEdittext_dotScale);
        mengEtTextToEncode = (MengEditText) view.findViewById(R.id.gif_arb_qr_mainmengTextview_content);
        mengEtSize = (MengEditText) view.findViewById(R.id.gif_qr_mainEditText_size);
        cbAutoSize = (CheckBox) view.findViewById(R.id.gif_qr_mainCheckbox_size);
        pbCodingProgress = (ProgressBar) view.findViewById(R.id.gif_arb_qr_mainProgressBar);
        tvImagePath = (TextView) view.findViewById(R.id.gif_arb_qr_selected_path);
        cbAutoSize.setOnCheckedChangeListener(check);
        cbAutoColor.setOnCheckedChangeListener(check);
        btnSelectImage.setOnClickListener(listenerBtnClick);
        btnEncodeGif.setOnClickListener(listenerBtnClick);
    }

    CompoundButton.OnCheckedChangeListener check = new CompoundButton.OnCheckedChangeListener() {
        @Override
        public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
            switch (buttonView.getId()) {
                case R.id.gif_qr_mainCheckbox_size:
                    mengEtSize.setVisibility(isChecked ? View.GONE : View.VISIBLE);
                    break;
                case R.id.gif_arb_qr_checkbox_autocolor:
                    mColorBar.setVisibility(isChecked ? View.GONE : View.VISIBLE);
                    if (!isChecked) LogTool.t("如果颜色搭配不合理,二维码将会难以识别");
                    break;
            }
        }
    };
    OnClickListener listenerBtnClick = new OnClickListener() {
        @Override
        public void onClick(View v) {
            switch (v.getId()) {
                case R.id.gif_arb_qr_button_selectImg:
                    MainActivity2.selectImage(AnimGIFAwesomeQr.this);
                    break;
                case R.id.gif_arb_qr_button_encode_gif:
                    if (coding) {
                        LogTool.t("正在执行操作");
                    } else {
                        btnSelectImage.setEnabled(false);
                        encodeGIF(strSelectedGifPath);
                    }
                    break;
            }
        }
    };

    private void encodeGIF(final String oldGifPath) {

        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    coding = true;
                    AnimatedGifDecoder gifDecoder = new AnimatedGifDecoder();
                    File gifFile = new File(oldGifPath);
                    FileInputStream fis = new FileInputStream(gifFile);
                    int statusCode = 0;
                    statusCode = gifDecoder.read(fis, fis.available());
                    if (statusCode != 0) {
                        LogTool.e("read error " + oldGifPath);
                        return;
                    }
                    String filePath = MainActivity.instence.getGifAwesomeQRPath();
                    ByteArrayOutputStream baos = new ByteArrayOutputStream();
                    AnimatedGifEncoder localAnimatedGifEncoder = new AnimatedGifEncoder();
                    localAnimatedGifEncoder.start(baos);//start
                    localAnimatedGifEncoder.setRepeat(0);//设置生成gif的开始播放时间。0为立即开始播放
                    for (int i = 0; i < gifDecoder.getFrameCount(); i++) {
                        float pro = ((float) gifDecoder.getCurrentFrameIndex()) / gifDecoder.getFrameCount() * 100;
                        setProgress((int) pro);
                        gifDecoder.advance();
                        localAnimatedGifEncoder.setDelay(gifDecoder.getNextDelay());
                        localAnimatedGifEncoder.addFrame(encodeAwesome(gifDecoder.getNextFrame()));
                    }
                    localAnimatedGifEncoder.finish();
                    String path = MainActivity.instence.getGifAwesomeQRPath();
                    path=MainActivity.instence.getImagePath(AnimGIFAwesomeQr.class);
                    try {
                        FileOutputStream fos = new FileOutputStream(path);
                        baos.writeTo(fos);
                        baos.flush();
                        fos.flush();
                        baos.close();
                        fos.close();
                    } catch (IOException e) {
                        LogTool.e("gif异常" + e.toString());
                    }
                    getActivity().getApplicationContext().sendBroadcast(new Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE, Uri.fromFile(new File(filePath))));
                    LogTool.t("完成 : " + filePath);
                } catch (Exception e) {
                    LogTool.e(e);
                }
                coding = false;
                System.gc();
                getActivity().runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        btnSelectImage.setEnabled(true);
                    }
                });
            }
        }).start();
    }

    private Bitmap encodeAwesome(Bitmap bg) {
        int size = bg.getWidth();
        return AwesomeQRCode.create(
                mengEtTextToEncode.getString(),
                cbAutoSize.isChecked() ? size : mengEtSize.getInt(),
                (int) (size * 0.025f),
                Float.parseFloat(mengEtDotScale.getString()),
                mColorBar.getTrueColor(),
                cbAutoColor.isChecked() ? Color.WHITE : mColorBar.getFalseColor(),
                bg,
                false,
                cbAutoColor.isChecked(),
                false,
                128);
    }

    private void setProgress(final int p) {
        getActivity().runOnUiThread(new Runnable() {
            @Override
            public void run() {
                if (p == 100) {
                    pbCodingProgress.setVisibility(View.GONE);
                    LogTool.t("完成");
                } else {
                    pbCodingProgress.setProgress(p);
                    if (pbCodingProgress.getVisibility() == View.GONE) {
                        pbCodingProgress.setVisibility(View.VISIBLE);
                    }
                }
            }
        });
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == MainActivity2.SELECT_FILE_REQUEST_CODE && resultCode == getActivity().RESULT_OK && data.getData() != null) {
            try {
                if (coding) {
                    LogTool.t("正在执行操作");
                } else {
                    strSelectedGifPath = ContentHelper.absolutePathFromUri(getActivity().getApplicationContext(), data.getData());
                    tvImagePath.setText(strSelectedGifPath);
                }
            } catch (Exception e) {
                LogTool.e(e);
            }
        } else if (resultCode == getActivity().RESULT_CANCELED) {
            Toast.makeText(getActivity().getApplicationContext(), "用户取消了操作", Toast.LENGTH_SHORT).show();
        } else {
            MainActivity2.selectImage(this);
        }
        super.onActivityResult(requestCode, resultCode, data);
    }

}
