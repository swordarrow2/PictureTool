package com.meng.picTools.pixivPictureDownloader;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.widget.Toast;

import com.meng.picTools.MainActivity2;

public class MashMellow extends Activity {
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        try {
            boolean readonly = getIntent().getBooleanExtra(Intent.EXTRA_PROCESS_TEXT_READONLY, false);
            CharSequence text = getIntent().getCharSequenceExtra(Intent.EXTRA_PROCESS_TEXT);
            String ss = text.toString().toLowerCase();
            if (MainActivity2.instence != null) {
                MainActivity2.instence.pixivDownloadMainFragment.editTextURL.setText(ss);
                MainActivity2.instence.pixivDownloadMainFragment.startDownload();
                MainActivity2.instence.pixivDownloadMainFragment.editTextURL.setText("");
            } else {
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        Toast.makeText(MashMellow.this, "请先启动主程序", Toast.LENGTH_SHORT).show();
                    }
                });
            }
        } catch (Exception e) {
            showToast(e);
        }

    }

    private void showToast(Exception e) {
        Toast.makeText(MashMellow.this, e.toString(), Toast.LENGTH_LONG).show();
    }
}
