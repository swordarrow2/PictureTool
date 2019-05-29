package com.meng.picTools.qrCode.creator;


import android.app.*;
import android.content.*;
import android.graphics.*;
import android.net.*;
import android.os.*;
import android.util.*;
import android.view.*;
import android.widget.*;
import com.meng.picTools.*;
import com.meng.picTools.helpers.ContentHelper;
import com.meng.picTools.helpers.FileHelper;
import com.meng.picTools.helpers.FileType;
import com.meng.picTools.lib.*;
import com.meng.picTools.lib.mengViews.*;

import java.io.*;

import android.support.v7.app.AlertDialog;

public class AnimGIFArbAwesome extends Fragment {

    private boolean coding = false;
    private int qrSize;
    private Button btnSelectImage;
    private CheckBox cbAutoColor;
    private MengEditText mengEtDotScale;
    private MengEditText mengEtTextToEncode;
    private ProgressBar pbCodingProgress;
    private String strSelectedGifPath = "";
    private TextView tvImagePath;
    private MengColorBar mColorBar;
    private Button btnEncodeGif;

    private MengSelectRectView mengSelectView;
    private float screenW;
    private float screenH;
    private MengScrollView mengScrollView;
    private MengSeekBar mengSeekBar;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.gif_arb_qr_main, container, false);
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        mengScrollView = (MengScrollView) view.findViewById(R.id.gif_arb_awesome_qrMengScrollView);
        mengSelectView = (MengSelectRectView) view.findViewById(R.id.gif_arb_awesome_qrselectRectView);
        mColorBar = (MengColorBar) view.findViewById(R.id.gif_arb_qr_main_colorBar);
        btnEncodeGif = (Button) view.findViewById(R.id.gif_arb_qr_button_encode_gif);
        btnSelectImage = (Button) view.findViewById(R.id.gif_arb_qr_button_selectImg);
        cbAutoColor = (CheckBox) view.findViewById(R.id.gif_arb_qr_checkbox_autocolor);
        mengEtDotScale = (MengEditText) view.findViewById(R.id.gif_arb_qr_mengEdittext_dotScale);
        mengEtTextToEncode = (MengEditText) view.findViewById(R.id.gif_arb_qr_mainmengTextview_content);
        pbCodingProgress = (ProgressBar) view.findViewById(R.id.gif_arb_qr_mainProgressBar);
        tvImagePath = (TextView) view.findViewById(R.id.gif_arb_qr_selected_path);
        mengSeekBar = (MengSeekBar) view.findViewById(R.id.gif_arb_qr_mainMengSeekBar);
        cbAutoColor.setOnCheckedChangeListener(check);
        btnSelectImage.setOnClickListener(listenerBtnClick);
        btnEncodeGif.setOnClickListener(listenerBtnClick);
        mengScrollView.setSelectView(mengSelectView);
        DisplayMetrics dm = new DisplayMetrics();
        getActivity().getWindowManager().getDefaultDisplay().getMetrics(dm);
        screenW = dm.widthPixels;
        screenH = dm.heightPixels;
    }

    CompoundButton.OnCheckedChangeListener check = new CompoundButton.OnCheckedChangeListener() {
        @Override
        public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
            switch (buttonView.getId()) {
                case R.id.gif_arb_qr_checkbox_autocolor:
                    mColorBar.setVisibility(isChecked ? View.GONE : View.VISIBLE);
                    if (!isChecked) LogTool.t("如果颜色搭配不合理,二维码将会难以识别");
                    break;
            }
        }
    };
    View.OnClickListener listenerBtnClick = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            switch (v.getId()) {
                case R.id.gif_arb_qr_button_selectImg:
                    btnEncodeGif.setEnabled(true);
                    MainActivity2.instence.selectImage(AnimGIFArbAwesome.this);
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
                    String filePath = FileHelper.getFileAbsPath(FileType.awesomeQR);
                    try {
                        FileOutputStream fos = new FileOutputStream(filePath);
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
        return QrUtils.generate(
                mengEtTextToEncode.getString(),
                Float.parseFloat(mengEtDotScale.getString()),
                cbAutoColor.isChecked() ? Color.BLACK : mColorBar.getTrueColor(),
                cbAutoColor.isChecked() ? Color.WHITE : mColorBar.getFalseColor(),
                cbAutoColor.isChecked(),
                between(mengSelectView.getSelectLeft() / mengSelectView.getXishu(), 0, bg.getWidth() - qrSize),
                between(mengSelectView.getSelectTop() / mengSelectView.getXishu(), 0, bg.getHeight() - qrSize),
                qrSize,
                bg);
    }

    private int between(float a, int min, int max) {
        if (a < min) a = min;
        if (a > max) a = max;
        return (int) a;
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
        if (requestCode == MainActivity2.instence.SELECT_FILE_REQUEST_CODE && resultCode == Activity.RESULT_OK && data.getData() != null) {
            try {
                if (coding) {
                    LogTool.t("正在执行操作");
                } else {
                    Uri imageUri = data.getData();
                    strSelectedGifPath = ContentHelper.absolutePathFromUri(getActivity().getApplicationContext(), imageUri);
                    tvImagePath.setText(strSelectedGifPath);
                    final Bitmap selectedBmp = BitmapFactory.decodeFile(strSelectedGifPath);
                    final int selectedBmpWidth = selectedBmp.getWidth();
                    final int selectedBmpHeight = selectedBmp.getHeight();
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
                                    qrSize = msb.getProgress();
                                    //ll.addView(new mengSelectRectView(getActivity(),selectedBmp,screenW,screenH));
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
                                    mengSelectView.setup(selectedBmp, screenW, screenH, qrSize);
                                    ViewGroup.LayoutParams para = mengSelectView.getLayoutParams();
                                    para.height = (int) (screenW / selectedBmpWidth * selectedBmpHeight);
                                    mengSelectView.setLayoutParams(para);
                                    mengSelectView.setVisibility(View.VISIBLE);
                                    if (para.height > screenH * 2 / 3) {
                                        LogTool.t("可使用音量键滚动界面");
                                    }
                                    mengScrollView.post(new Runnable() {
                                        public void run() {
                                            mengScrollView.fullScroll(View.FOCUS_DOWN);
                                        }
                                    });
                                }
                            }).show();
                }
            } catch (Exception e) {
                LogTool.e(e);
            }
        } else if (resultCode == getActivity().RESULT_CANCELED) {
            Toast.makeText(getActivity().getApplicationContext(), "用户取消了操作", Toast.LENGTH_SHORT).show();
        } else {
            MainActivity2.instence.selectImage(this);
        }
        super.onActivityResult(requestCode, resultCode, data);
    }

    public void onKeyDown(int keyCode, KeyEvent event) {

        if ((keyCode == KeyEvent.KEYCODE_VOLUME_UP)) {
            mengScrollView.post(new Runnable() {
                public void run() {
                    mengScrollView.scrollBy(0, 0xffffff9c);//(0xffffff9c)16=(-100)10
                }
            });
        }
        if ((keyCode == KeyEvent.KEYCODE_VOLUME_DOWN)) {
            mengScrollView.post(new Runnable() {
                public void run() {
                    mengScrollView.scrollBy(0, 100);
                }
            });
        }
    }
}
