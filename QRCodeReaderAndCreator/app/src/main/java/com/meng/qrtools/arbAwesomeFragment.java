package com.meng.qrtools;

import android.Manifest;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.os.SystemClock;
import android.support.v4.app.ActivityCompat;
import android.util.DisplayMetrics;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

import com.meng.MainActivity2;
import com.meng.qrtools.creator.ContentHelper;
import com.meng.qrtools.lib.qrcodelib.AwesomeQRCode;
import com.meng.qrtools.lib.qrcodelib.QRCode;
import com.meng.qrtools.views.mengColorBar;
import com.meng.qrtools.views.mengEdittext;

import java.io.File;
import java.io.IOException;

/**
 * Created by Administrator on 2018/7/19.
 */

public class arbAwesomeFragment extends android.app.Fragment {

    private ImageView qrCodeImageView;
    private mengEdittext mengEtDotScale, mengEtContents;
    private Button btGenerate, btSelectBG, btRemoveBG;
    private boolean generating = false;
    private CheckBox ckbAutoColor;
    private ScrollView scrollView;
    private Button btnSave;
    private TextView imgPathTextView;
    private mengColorBar mColorBar;
    public static final int selectRect = 2;

    private static final int REQUEST_EXTERNAL_STORAGE = 1;
    private static String[] PERMISSIONS_STORAGE = {
            Manifest.permission.READ_EXTERNAL_STORAGE,
            Manifest.permission.WRITE_EXTERNAL_STORAGE
    };

    // private Bitmap tmpQR = null;
    private Bitmap tmpQRBackground = null;
    //private Bitmap imageViewBackground = null;
    private Bitmap bmpUseRect = null;
    private Bitmap finallyBmp = null;

    private String selectedBmpPath = "";

    private int qrSize = 0;
    private float xishu = 0f;

    private float mLeft = 0f;
    private float mTop = 0f;
    private LinearLayout ll;
    private TextView tv;

    private Button btn;

    float screenW = 0;
    float screenH = 0;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        // TODO: Implement this method
        return inflater.inflate(R.layout.arb_awesome_qr, container, false);
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
        ll = (LinearLayout) view.findViewById(R.id.arbll);
        tv = (TextView) view.findViewById(R.id.size_text);
        btn = (Button) view.findViewById(R.id.size_button);
        btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Bitmap b = BitmapFactory.decodeFile(selectedBmpPath).copy(Bitmap.Config.ARGB_8888, true);
                tmpQRBackground = Bitmap.createBitmap(
                        b,
                        between(mLeft / xishu, 0, b.getWidth() - qrSize),
                        between(mTop / xishu, 0, b.getWidth() - qrSize),
                        qrSize, qrSize);
            }
        });
        ckbAutoColor.setOnCheckedChangeListener(check);
        btSelectBG.setOnClickListener(click);
        btRemoveBG.setOnClickListener(click);
        btGenerate.setOnClickListener(click);
        btnSave.setOnClickListener(click);
        DisplayMetrics dm = new DisplayMetrics();
        getActivity().getWindowManager().getDefaultDisplay().getMetrics(dm);
        screenW = dm.widthPixels;
        screenH = dm.heightPixels;
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
                    MainActivity2.selectImage(arbAwesomeFragment.this);
                    break;
                case R.id.awesomeqr_main_removeBackgroundImage:
                    imgPathTextView.setVisibility(View.GONE);
                    log.t(getActivity(), getResources().getString(R.string.Background_image_removed));
                    break;
                case R.id.awesomeqr_main_generate:
                    generate(mengEtContents.getString(),
                            qrSize,
                            Float.parseFloat(mengEtDotScale.getString()),
                            mColorBar.getTrueColor(),
                            ckbAutoColor.isChecked() ? Color.WHITE : mColorBar.getFalseColor(),
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
            selectedBmpPath = ContentHelper.absolutePathFromUri(getActivity().getApplicationContext(), uri);
            imgPathTextView.setText("当前文件：" + selectedBmpPath);
            final Bitmap selectedBmp = BitmapFactory.decodeFile(selectedBmpPath);
            final EditText et = new EditText(getActivity());
            et.setHint("0<大小<" + (Math.min(selectedBmp.getWidth(), selectedBmp.getHeight()) + 1));
            new AlertDialog.Builder(getActivity())
                    .setTitle("输入要添加的二维码大小(像素)")
                    .setView(et)
                    .setPositiveButton("确定", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface p1, int p2) {
                            qrSize = Integer.parseInt(et.getText().toString());
                            ll.addView(new selectRectView(getActivity(), selectedBmp, screenW, screenH));
                        }
                    }).show();
        } else if (resultCode == getActivity().RESULT_CANCELED) {
            Toast.makeText(getActivity().getApplicationContext(), "取消选择图片", Toast.LENGTH_SHORT).show();
        } else {
            MainActivity2.selectImage(arbAwesomeFragment.this);
        }
        super.onActivityResult(requestCode, resultCode, data);
    }

    private void generate(final String contents, final int size, final float dotScale, final int colorDark,
                          final int colorLight, final boolean autoColor) {
        if (generating) return;
        generating = true;
      //  try {
            Bitmap bmp = BitmapFactory.decodeFile(selectedBmpPath).copy(Bitmap.Config.ARGB_8888,true);
            tmpQRBackground = Bitmap.createBitmap(
                    bmp,
                    between(mLeft / xishu, 0, bmp.getWidth() - qrSize),
                    between(mTop / xishu, 0, bmp.getHeight() - qrSize),
                    qrSize,
                    qrSize);
            Bitmap bmpQRcode = AwesomeQRCode.create(contents, size, 0, dotScale, colorDark, colorLight, tmpQRBackground, false, autoColor, false, 128);

            Canvas c = new Canvas(bmp);
            c.drawBitmap(bmpQRcode, mLeft, mTop, new Paint());
            qrCodeImageView.setImageBitmap(scaleBitmap(bmp, xishu));
            finallyBmp = bmp;
            //   ViewGroup.LayoutParams para = qrCodeImageView.getLayoutParams();
            //   DisplayMetrics dm = new DisplayMetrics();
            //  getActivity().getWindowManager().getDefaultDisplay().getMetrics(dm);
            //  float screenW = dm.widthPixels;
            // para.height = (int) (screenW / bmp.getWidth() * bmp.getHeight());
            // qrCodeImageView.setLayoutParams(para);
            scrollView.post(new Runnable() {
                @Override
                public void run() {
                    scrollView.fullScroll(View.FOCUS_DOWN);
                }
            });
            generating = false;

       // } catch (Exception e) {
        //    log.e(getActivity(), e);
        //    generating = false;
       // }
    }

    private Bitmap scaleBitmap(Bitmap origin, float ratio) {
        if (origin == null) {
            return null;
        }
        int width = origin.getWidth();
        int height = origin.getHeight();
        Matrix matrix = new Matrix();
        matrix.preScale(ratio, ratio);
        Bitmap newBM = Bitmap.createBitmap(origin, 0, 0, width, height, matrix, false);
        if (newBM.equals(origin)) {
            return newBM;
        }
        origin.recycle();
        return newBM;
    }

    private int between(float a, int min, int max) {
        if (a < min) {
            a = min;
        }
        if (a > max) {
            a = max;
        }
        return (int) a;
    }

    public class selectRectView extends View {
        Bitmap imageViewBackground = null;

        public selectRectView(Context context, Bitmap seleBmp, float screenW, float screenH) {
            super(context);
            float bmpW = seleBmp.getWidth();
            float bmpH = seleBmp.getHeight();
            xishu = Math.min(screenH / bmpH, screenW / bmpW);
            imageViewBackground = scaleBitmap(seleBmp, xishu);
            bmpUseRect = Bitmap.createBitmap((int) (qrSize * xishu), (int) (qrSize * xishu), Bitmap.Config.ARGB_8888);
            Canvas c = new Canvas(bmpUseRect);
            c.drawARGB(0x7f, 0x7f, 0xca, 0x00);
        }

        private Bitmap scaleBitmap(Bitmap origin, float ratio) {
            if (origin == null) {
                return null;
            }
            Matrix matrix = new Matrix();
            matrix.preScale(ratio, ratio);
            Bitmap newBM = Bitmap.createBitmap(origin, 0, 0, origin.getWidth(), origin.getHeight(), matrix, false);
            if (newBM.equals(origin)) {
                return newBM;
            }
            origin.recycle();
            return newBM;
        }

        @Override
        protected void onDraw(Canvas canvas) {
            super.onDraw(canvas);
            canvas.drawBitmap(imageViewBackground, 0, 0, null); // 绘制背景图像
            canvas.drawBitmap(bmpUseRect,
                    mLeft,
                    mTop,
                    null); // 绘制选择框
        }

        @Override
        public boolean onTouchEvent(MotionEvent event) {
            final int x = (int) event.getX(); // 获取当前触摸点的X轴坐标
            final int y = (int) event.getY(); // 获取当前触摸点的Y轴坐标
            mLeft = between(x - bmpUseRect.getWidth() / 2, 0, imageViewBackground.getWidth() - bmpUseRect.getWidth()); // 计算放大镜的左边距
            mTop = between(y - bmpUseRect.getHeight() / 2, 0, imageViewBackground.getHeight() - bmpUseRect.getHeight()); // 计算放大镜的右边距
            tv.setText("x:" + (mLeft / xishu) + "  y:" + (mTop / xishu));
            invalidate(); // 重绘画布
            return true;
        }
    }

}
