package com.meng.qrtools.creator;

import android.app.Fragment;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.os.SystemClock;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.meng.qrtools.R;
import com.meng.qrtools.log;
import com.waynejo.androidndkgif.GifDecoder;
import com.waynejo.androidndkgif.GifEncoder;
import com.waynejo.androidndkgif.GifImage;
import com.waynejo.androidndkgif.GifImageIterator;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.text.DecimalFormat;

public class gifAwesomeQr extends Fragment {

    private boolean useDither = true;
    private ImageView imageView;
    private Button btnSelectImg, btnStartDecode,
            encodeGifBtn;
    private CheckBox lowMem, dither;
    private TextView imgPath;
    private final int SELECT_FILE_REQUEST_CODE = 8212;
    private String selectGifPath = "";
    private Bitmap[] bitmaps;
    private int gifDelay;
    private String tmpFolder=Environment.getExternalStorageDirectory().getAbsolutePath() +
            "/Pictures/QRcode/tmp/";

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        // TODO: Implement this method
        return inflater.inflate(R.layout.gif_qr_main, container, false);
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        // TODO: Implement this method
        super.onViewCreated(view, savedInstanceState);
        lowMem = (CheckBox) view.findViewById(R.id.gif_qr_checkbox_low_memery);
        dither = (CheckBox) view.findViewById(R.id.gif_qr_checkbox_dither);
        imageView = (ImageView) view.findViewById(R.id.image_view);
        btnSelectImg = (Button) view.findViewById(R.id.gif_qr_button_selectImg);
        btnStartDecode = (Button) view.findViewById(R.id.gif_qr_button_startDecodeImg);
        encodeGifBtn = (Button) view.findViewById(R.id.gif_qr_button_encode_gif);
        imgPath = (TextView) view.findViewById(R.id.gif_qr_selected_path);


        btnSelectImg.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View p1) {
                // TODO: Implement this method
                selectImage();
            }
        });
        btnStartDecode.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View p1) {
                // TODO: Implement this method
                decodeGif(selectGifPath);
            }
        });

        encodeGifBtn.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View p1) {
                // TODO: Implement this method
                onEncodeGIF();
            }
        });
    }


    public void onDecodeGIF() {
        new Thread(new Runnable() {
            @Override
            public void run() {
                String destFile = "";// = setupSampleFile();

                final GifDecoder gifDecoder = new GifDecoder();
                final boolean isSucceeded = gifDecoder.load(destFile);
                getActivity().runOnUiThread(new Runnable() {
                    int idx = 0;

                    @Override
                    public void run() {
                        if (isSucceeded) {
                            Bitmap bitmap = gifDecoder.frame(idx);
                            imageView.setImageBitmap(bitmap);
                            if (idx + 1 < gifDecoder.frameNum()) {
                                imageView.postDelayed(this, gifDecoder.delay(idx));
                            }
                            ++idx;
                        } else {
                            log.t("Failed");
                        }
                    }
                });
            }
        }).start();
    }

    public void onDecodeGIFUsingIterator() {
        new Thread(new Runnable() {
            @Override
            public void run() {
                String destFile = "";//setupSampleFile();

                final GifDecoder gifDecoder = new GifDecoder();
                final GifImageIterator iterator = gifDecoder.loadUsingIterator(destFile);
                getActivity().runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        if (iterator.hasNext()) {
                            GifImage next = iterator.next();
                            if (null != next) {
                                imageView.setImageBitmap(next.bitmap);
                                imageView.postDelayed(this, next.delayMs);
                            } else {
                                log.t("Failed");
                            }
                        } else {
                            iterator.close();
                        }
                    }
                });
            }
        }).start();
    }

    public void onEncodeGIF() {
        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    encodeGIF();
                } catch (FileNotFoundException e) {
                    e.printStackTrace();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }).start();
    }

    private void encodeGIF() throws IOException {
        String dstFile = "result.gif";
        // final String filePath = Environment.getExternalStorageDirectory()+File.separator+dstFile;
        final String filePath = Environment.getExternalStorageDirectory().getAbsolutePath() + "/Pictures/QRcode/AwesomeQR" + SystemClock.elapsedRealtime() + dstFile;
        int width = 500;
        int height = 500;
        int delayMs = 50;

        GifEncoder gifEncoder = new GifEncoder();
        gifEncoder.init(width, height, filePath, GifEncoder.EncodingType.ENCODING_TYPE_NORMAL_LOW_MEMORY);
        gifEncoder.setDither(useDither);
        Bitmap bitmap = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas(bitmap);
        Paint p = new Paint();
        int[] colors = new int[]{0xFF000000, 0xFFFFFFFF};
        for (int color : colors) {
            p.setColor(color);
            canvas.drawRect(0, 0, width, height, p);
            gifEncoder.encodeFrame(bitmap, delayMs);
        }
        gifEncoder.close();
        getActivity().runOnUiThread(new Runnable() {
            @Override
            public void run() {
                log.t("done : " + filePath);
            }
        });
    }

    public void onDisableDithering() {
        useDither = false;
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        // TODO: Implement this method
        if (requestCode == SELECT_FILE_REQUEST_CODE && resultCode == getActivity().RESULT_OK && data.getData() != null) {
            try {
                Uri imageUri = data.getData();
                imgPath.setText(selectGifPath);
                selectGifPath = ContentHelper.absolutePathFromUri(getActivity().getApplicationContext(), imageUri);
            } catch (Exception e) {
                log.e(e);
            }
        } else if (resultCode == getActivity().RESULT_CANCELED) {
            Toast.makeText(getActivity().getApplicationContext(), "用户取消了操作", Toast.LENGTH_SHORT).show();
        } else {
              selectImage();
        }
        super.onActivityResult(requestCode, resultCode, data);
    }

    private void selectImage() {
        Intent intent = new Intent(Intent.ACTION_OPEN_DOCUMENT);
        intent.addCategory(Intent.CATEGORY_OPENABLE);
        intent.setType("image/*");
        startActivityForResult(intent, SELECT_FILE_REQUEST_CODE);
    }

    private void decodeGif(final String path) {
        if (lowMem.isChecked()) {
            imgPath.setText(path + "\n使用了低配置模式");
            new Thread(new Runnable() {
                @Override
                public void run() {
                    GifDecoder gifDecoder = new GifDecoder();
                    GifImageIterator iterator = gifDecoder.loadUsingIterator(path);
                    int flag = 0;
                    DecimalFormat df=new DecimalFormat();
                    String style = "000";//定义要显示的数字的格式
                    df.applyPattern(style);// 将格式应用于格式化器
                    while (iterator.hasNext()) {
                        GifImage next = iterator.next();
                        if (next != null) {
                            try {
                                QRCode.saveMyBitmap(tmpFolder + df.format(flag++) + ".png", next.bitmap);
                            } catch (IOException e) {
                                log.e(e);
                            }
                            gifDelay = next.delayMs;
                        } else {
                            log.t("解码失败，可能文件损坏");
                        }
                    }
                    iterator.close();
                }
            }).start();
        } else {
            imgPath.setText(path + "\n未使用低配置模式");
            new Thread(new Runnable() {
                @Override
                public void run() {
                    final GifDecoder gifDecoder = new GifDecoder();
                    if (gifDecoder.load(path)) {
                        bitmaps = new Bitmap[gifDecoder.frameNum()];
                        gifDelay = gifDecoder.delay(1);
                        for (int i = 0; i < gifDecoder.frameNum(); i++) {
                            bitmaps[i] = gifDecoder.frame(i);
                            log.i("第" + i + "张解码成功");
                        }
                    } else {
                        log.e("解码失败，可能不是GIF文件");
                    }

                }
            }).start();
        }
    }

}
