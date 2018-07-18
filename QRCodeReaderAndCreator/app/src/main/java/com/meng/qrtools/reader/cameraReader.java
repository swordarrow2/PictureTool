package com.meng.qrtools.reader;

import android.app.Activity;
import android.content.DialogInterface;
import android.os.Bundle;
import android.text.TextUtils;
import android.widget.Toast;

import com.meng.qrtools.reader.qrcodelib.CaptureActivity;
import android.app.*;

/**
 * Created by xdj on 16/9/17.
 */

public class cameraReader extends CaptureActivity {
    
    private AlertDialog mDialog;
/*
    @Override
    protected void handleResult(String resultString) {
        if (TextUtils.isEmpty(resultString)) {
            Toast.makeText(getActivity(), "string.scan_failed", Toast.LENGTH_SHORT).show();
            restartPreview();
        } else {
            if (mDialog == null) {
                mDialog = new AlertDialog.Builder(getActivity())
                        .setMessage(resultString)
                        .setPositiveButton("确定", null)
                        .create();
                mDialog.setOnDismissListener(new DialogInterface.OnDismissListener() {
                    @Override
                    public void onDismiss(DialogInterface dialog) {
                        restartPreview();
                    }
                });
            }
            if (!mDialog.isShowing()) {
                mDialog.setMessage(resultString);
                mDialog.show();
            }
        }
    }
	*/
}
