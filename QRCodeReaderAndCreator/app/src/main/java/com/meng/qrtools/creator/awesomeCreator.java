package com.meng.qrtools.creator;

import android.Manifest;
import android.app.Fragment;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.os.SystemClock;
import android.support.v4.app.ActivityCompat;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

import com.meng.qrtools.R;
import com.meng.qrtools.log;
import com.meng.qrtools.views.mengEdittext;

import java.io.File;
import java.io.IOException;

public class awesomeCreator extends Fragment {

    private final int SELECT_FILE_REQUEST_CODE = 822;

    private ImageView qrCodeImageView;
    private mengEdittext mengEtColorLight, mengEtDotScale, mengEtColorDark, mengEtContents, mengEtMargin, mengEtSize;
    private Button btGenerate, btSelectBG, btRemoveBackgroundImage;
    private CheckBox ckbWhiteMargin;
    private Bitmap backgroundImage = null;

    private boolean generating = false;
    private CheckBox ckbAutoColor;
    private ScrollView scrollView;
    private CheckBox ckbBinarize;
    private mengEdittext mengEtBinarizeThreshold;
    private Button btnSave;
    private TextView imgPathTextView;
    private Bitmap bmp = null;
    private static final int CROP_REQUEST_CODE = 3;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        // TODO: Implement this method
        return inflater.inflate(R.layout.awesomeqr_main, container, false);
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        // TODO: Implement this method
        super.onViewCreated(view, savedInstanceState);
        scrollView = (ScrollView) view.findViewById(R.id.awesomeqr_main_scrollView);
        qrCodeImageView = (ImageView) view.findViewById(R.id.awesomeqr_main_qrcode);
        mengEtColorLight = (mengEdittext) view.findViewById(R.id.awesomeqr_main_colorLight);
        mengEtColorDark = (mengEdittext) view.findViewById(R.id.awesomeqr_main_colorDark);
        mengEtContents = (mengEdittext) view.findViewById(R.id.awesomeqr_main_content);
        mengEtSize = (mengEdittext) view.findViewById(R.id.awesomeqr_main_mengEdittext_size);
        mengEtMargin = (mengEdittext) view.findViewById(R.id.awesomeqr_main_margin);
        mengEtDotScale = (mengEdittext) view.findViewById(R.id.awesomeqr_main_dotScale);
        btSelectBG = (Button) view.findViewById(R.id.awesomeqr_main_backgroundImage);
        btRemoveBackgroundImage = (Button) view.findViewById(R.id.awesomeqr_main_removeBackgroundImage);
        btGenerate = (Button) view.findViewById(R.id.awesomeqr_main_generate);
        ckbWhiteMargin = (CheckBox) view.findViewById(R.id.awesomeqr_main_whiteMargin);
        ckbAutoColor = (CheckBox) view.findViewById(R.id.awesomeqr_main_autoColor);
        ckbBinarize = (CheckBox) view.findViewById(R.id.awesomeqr_main_binarize);
        mengEtBinarizeThreshold = (mengEdittext) view.findViewById(R.id.awesomeqr_main_mengEdittext_binarizeThreshold);
        btnSave = (Button) view.findViewById(R.id.awesomeqr_mainButton);
        imgPathTextView = (TextView) view.findViewById(R.id.awesomeqr_main_imgPathTextView);
        mengEtColorDark.addTextChangedListener(tw);
        mengEtColorLight.addTextChangedListener(tw);
        ckbAutoColor.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                mengEtColorDark.setVisibility(isChecked ? View.GONE : View.VISIBLE);
                mengEtColorLight.setVisibility(isChecked ? View.GONE : View.VISIBLE);
            }
        });

        ckbBinarize.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                mengEtBinarizeThreshold.setVisibility(isChecked ? View.VISIBLE : View.GONE);
            }
        });

        btSelectBG.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                selectImage();
            }
        });

        btRemoveBackgroundImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                backgroundImage = null;
                imgPathTextView.setVisibility(View.GONE);
                log.t(getActivity(), getResources().getString(R.string.Background_image_removed));
            }
        });

        btGenerate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                generate(mengEtContents.getString(),
                        Integer.parseInt(mengEtSize.getString()),
                        Integer.parseInt(mengEtMargin.getString()),
                        Float.parseFloat(mengEtDotScale.getString()),
                        ckbAutoColor.isChecked() ? Color.BLACK : Color.parseColor(mengEtColorDark.getString()),
                        ckbAutoColor.isChecked() ? Color.WHITE : Color.parseColor(mengEtColorLight.getString()),
                        backgroundImage,
                        ckbWhiteMargin.isChecked(),
                        ckbAutoColor.isChecked(),
                        ckbBinarize.isChecked(),
                        Integer.parseInt(mengEtBinarizeThreshold.getString())
                );
                btnSave.setVisibility(View.VISIBLE);
            }
        });
        btnSave.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View p1) {
                // TODO: Implement this method
                try {
                    String s = QRCode.saveMyBitmap(Environment.getExternalStorageDirectory().getAbsolutePath() + "/Pictures/QRcode/AwesomeQR" + SystemClock.elapsedRealtime() + ".png", bmp);
                    log.t(getActivity(), "已保存至" + s);
                    getActivity().getApplicationContext().sendBroadcast(new Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE, Uri.fromFile(new File(s))));//更新图库
                } catch (IOException e) {
                    log.e(getActivity(), e);
                }
            }
        });
    }

    private void selectImage() {
        Intent intent = new Intent(Intent.ACTION_OPEN_DOCUMENT);
        intent.addCategory(Intent.CATEGORY_OPENABLE);
        intent.setType("image/*");
        startActivityForResult(intent, SELECT_FILE_REQUEST_CODE);
    }

    public void setDataStr(String s) {
        mengEtContents.setString(s);
    }

    @Override
    public void onResume() {
        super.onResume();
        acquireStoragePermissions();
    }

    private static final int REQUEST_EXTERNAL_STORAGE = 1;
    private static String[] PERMISSIONS_STORAGE = {
            Manifest.permission.READ_EXTERNAL_STORAGE,
            Manifest.permission.WRITE_EXTERNAL_STORAGE
    };

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
        if (requestCode == SELECT_FILE_REQUEST_CODE && resultCode == getActivity().RESULT_OK && data.getData() != null) {
            imgPathTextView.setVisibility(View.VISIBLE);
            imgPathTextView.setText("当前文件：" + ContentHelper.absolutePathFromUri(getActivity().getApplicationContext(), cropPhoto(data.getData())));
        } else if (requestCode == CROP_REQUEST_CODE) {
            Bundle bundle = data.getExtras();
            if (bundle != null) {
                backgroundImage = bundle.getParcelable("data");
                log.t(getActivity(), getResources().getString(R.string.Background_image_added));
            } else {
                log.t(getActivity(), "裁剪失败");
            }
        } else if (resultCode == getActivity().RESULT_CANCELED) {
            Toast.makeText(getActivity().getApplicationContext(), "取消选择图片", Toast.LENGTH_SHORT).show();
        } else {
            selectImage();
        }
        super.onActivityResult(requestCode, resultCode, data);
    }

    private void generate(final String contents, final int size, final int margin, final float dotScale,
                          final int colorDark, final int colorLight, final Bitmap background, final boolean whiteMargin,
                          final boolean autoColor, final boolean binarize, final int binarizeThreshold) {
        if (generating) return;
        generating = true;

        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    final Bitmap b = AwesomeQRCode.create(contents, size, margin, dotScale, colorDark, colorLight, background, whiteMargin, autoColor, binarize, binarizeThreshold);
                    getActivity().runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            qrCodeImageView.setImageBitmap(b);
                            bmp = b;
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
                    getActivity().runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            generating = false;
                        }
                    });
                }
            }
        }).start();
    }

    TextWatcher tw = new TextWatcher() {

        @Override
        public void beforeTextChanged(CharSequence p1, int p2, int p3, int p4) {
        }

        @Override
        public void onTextChanged(CharSequence p1, int p2, int p3, int p4) {
            try {
                mengEtColorLight.setTextColor(Color.parseColor(mengEtColorLight.getString()));
            } catch (Exception e) {
                mengEtColorLight.setTextColor(Color.BLACK);
            }
            try {
                mengEtColorDark.setTextColor(Color.parseColor(mengEtColorDark.getString()));
            } catch (Exception e) {
                mengEtColorDark.setTextColor(Color.BLACK);
            }
        }

        @Override
        public void afterTextChanged(Editable p1) {

        }
    };

    private Uri cropPhoto(Uri uri) {
        Intent intent = new Intent("com.android.camera.action.CROP");
        intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
        intent.addFlags(Intent.FLAG_GRANT_WRITE_URI_PERMISSION);
        intent.setDataAndType(uri, "image/*");
        intent.putExtra("crop", "true");
        intent.putExtra("aspectX", 1);
        intent.putExtra("aspectY", 1);
        intent.putExtra("outputX", 300);
        intent.putExtra("outputY", 300);
        intent.putExtra("return-data", true);
        startActivityForResult(intent, CROP_REQUEST_CODE);
        return uri;
    }

}
