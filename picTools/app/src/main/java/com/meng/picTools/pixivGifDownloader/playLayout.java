package com.meng.picTools.pixivGifDownloader;

import android.app.*;
import android.content.*;
import android.graphics.*;
import android.net.*;
import android.os.*;
import android.provider.*;
import android.util.*;
import android.view.*;
import android.widget.*;
import android.widget.SeekBar.*;

import com.meng.picTools.*;
import com.waynejo.androidndkgif.*;

import java.io.*;
import java.util.*;
import java.util.zip.*;

public class playLayout extends Activity {

    private LinearLayout seekbarlinearLayout;
    private LinearLayout gifLinearLayout;
    private ProgressBar loadProgress;
    private LinearLayout loadLinearLayout;
    private LinearLayout unzipLinearLayout;
    private ProgressBar unzipProgress;
    private ImageView imageView;
    private String zipAbsolutePath;
    private SeekBar seekBar;
    private TextView tv;
    private String[] filesName;
    private Bitmap[] bms;
    private int i = 0;
    private Thread loadImg;
    private Thread playimg;
    private boolean loadfinish = false;
    private int gifDelay = 20;
    private final int LOADIMAGEPROGRESS = 1;
    private final int LOADING = 2;
    private final int LOADED = 3;
    private final int NEXTFRAME = 4;
    private boolean makingGIf = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.play_layout);
        imageView = (ImageView) findViewById(R.id.play_image);
        seekBar = (SeekBar) findViewById(R.id.play_seekbar);
        tv = (TextView) findViewById(R.id.play_text);
        seekbarlinearLayout = (LinearLayout) findViewById(R.id.play_layout_seekbar_linearlayout);
        gifLinearLayout = (LinearLayout) findViewById(R.id.gif_linearlayout);
        loadLinearLayout = (LinearLayout) findViewById(R.id.load_linearlayout);
        loadProgress = (ProgressBar) findViewById(R.id.load_progressbar);
        unzipLinearLayout = (LinearLayout) findViewById(R.id.unzip_linearlayout);
        unzipProgress = (ProgressBar) findViewById(R.id.unzip_progressbar);

        Intent i = getIntent();
        zipAbsolutePath = i.getStringExtra(Data.intentKeys.fileName);
        loadfinish = false;
        gifDelay = seekBar.getProgress();
        seekBar.setOnSeekBarChangeListener(new OnSeekBarChangeListener() {

            @Override
            public void onProgressChanged(SeekBar p1, int p2, boolean p3) {
                gifDelay = p2;
                tv.setText(String.format("帧延迟%d", gifDelay));
            }

            @Override
            public void onStartTrackingTouch(SeekBar p1) {
            }

            @Override
            public void onStopTrackingTouch(SeekBar p1) {
            }
        });
    }

    public Handler handler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case NEXTFRAME:
                    imageView.setImageBitmap(bms[i]);
                    break;
                case LOADING:
                    loadLinearLayout.setVisibility(View.VISIBLE);
                    break;
                case LOADIMAGEPROGRESS:
                    loadProgress.setProgress(msg.arg1);
                    break;
                case LOADED:
                    loadLinearLayout.setVisibility(View.GONE);
                    imageView.setVisibility(View.VISIBLE);
                    seekbarlinearLayout.setVisibility(View.VISIBLE);
                    break;
            }
        }
    };

    private void loadBitmap() {
        messageLoading();
        loadImg = new threadLoadBitmap();
        loadImg.start();
    }

    private void playBitmap() {
        messageLoaded();
        playimg = new threadPlayImage();
        playimg.start();
    }

    private class threadPlayImage extends Thread {
        public void run() {
            while (true) {
                if (makingGIf) {
                    break;
                }
                messageNextFrame();
                try {
                    Thread.sleep(seekBar.getProgress());
                } catch (InterruptedException e) {
                }
                if (i == bms.length - 1) {
                    i = 0;
                } else {
                    i++;
                }
            }
        }
    }

    private class threadLoadBitmap extends Thread {
        @Override
        public void run() {
            try {
                BitmapFactory.Options options = new BitmapFactory.Options();
                if (MainActivity.instence.sharedPreference.getValue(Data.preferenceKeys.gifScale) == null || MainActivity.instence.sharedPreference.getValue(Data.preferenceKeys.gifScale).equals("")) {
                    MainActivity.instence.sharedPreference.putValue(Data.preferenceKeys.gifScale, "1");
                }
                options.inSampleSize = Integer.parseInt(MainActivity.instence.sharedPreference.getValue(Data.preferenceKeys.gifScale));
                messageLoading(0);
                bms[0] = BitmapFactory.decodeFile(frameFileFolder + File.separator + filesName[0], options);
                for (int j = 1; j < filesName.length; j++) {
                    messageLoading(j);
                    bms[j] = BitmapFactory.decodeFile(frameFileFolder + File.separator + filesName[j], options);
                }
            } catch (OutOfMemoryError e) {
            }
            loadfinish = true;
            playBitmap();
        }
    }


    private void messageLoaded() {
        Message m = new Message();
        m.what = LOADED;
        handler.sendMessage(m);
    }

    private void messageLoading() {
        Message m = new Message();
        m.what = LOADING;
        handler.sendMessage(m);
    }

    private void messageNextFrame() {
        Message m = new Message();
        m.what = NEXTFRAME;
        handler.sendMessage(m);
    }

    private void messageLoading(int progress) {
        Message m = new Message();
        m.what = LOADIMAGEPROGRESS;
        m.arg1 = progress;
        handler.sendMessage(m);
    }


    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK) {
            if (makingGIf) {
                Toast.makeText(this, "请等待gif制作完成", Toast.LENGTH_SHORT).show();
                return true;
            }
            if (!loadfinish) {
                return true;
            }
            try {
                unzip.interrupt();
                playimg.interrupt();
                loadImg.interrupt();
                Bitmap b = null;
                for (int j = 0; j < filesName.length; j++) {
                    bms[j] = b;
                }
                System.gc();
            } catch (Exception e) {

            }
        }
        return super.onKeyDown(keyCode, event);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (MainActivity.instence.sharedPreference.getBoolean(Data.preferenceKeys.cleanTmpOnStopWatch)) {
            File[] fs = frameFileFolder.listFiles();
            for (File f : fs) {
                f.delete();
            }
            frameFileFolder.delete();
        }
    }


    
}
