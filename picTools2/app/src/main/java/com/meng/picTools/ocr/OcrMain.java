package com.meng.picTools.ocr;

import android.app.Activity;
import android.app.Fragment;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AnimationUtils;
import android.widget.TextView;
import android.widget.Toast;

import com.google.gson.Gson;
import com.meng.picTools.LogTool;
import com.meng.picTools.MainActivity;
import com.meng.picTools.R;
import com.meng.picTools.helpers.ContentHelper;
import com.meng.picTools.lib.MaterialDesign.FloatingActionButton;
import com.meng.picTools.sauceNao.PicResults;
import com.meng.picTools.sauceNao.ResultAdapter;
import com.meng.picTools.sauceNao.SauceNaoMain;

import org.json.JSONException;
import org.json.JSONObject;
import org.jsoup.Connection;
import org.jsoup.Jsoup;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.security.KeyManagementException;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;

public class OcrMain extends Fragment {
    public static final String APP_ID = "10173140";
    public static final String SECRET_ID = "AKIDRmqfEXsNxHOFBrrpx2rVzDG3arCPs2Uh";
    public static final String SECRET_KEY = "71hGdBXfZIG1wWSLNI2YtCJrz62rIe8t";
    public static final String USER_ID = "2856986197"; // qq号
    public Youtu faceYoutu = new Youtu(APP_ID, SECRET_ID, SECRET_KEY, Youtu.API_YOUTU_END_POINT, USER_ID);
    private FloatingActionButton mFabSelect;

    public boolean running = false;

    public OcrMain() {
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.saucenao_main, container, false);
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        mFabSelect = (FloatingActionButton) view.findViewById(R.id.fab_select);
        mFabSelect.setOnClickListener(onClickListener);
        mFabSelect.hide(false);
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                mFabSelect.show(true);
                mFabSelect.setShowAnimation(AnimationUtils.loadAnimation(getActivity(), R.anim.show_from_bottom));
                mFabSelect.setHideAnimation(AnimationUtils.loadAnimation(getActivity(), R.anim.hide_to_bottom));
            }
        }, 450);

    }

    View.OnClickListener onClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View view) {
            switch (view.getId()) {
                case R.id.fab_select:
                    if (running) return;
                    running = true;
                    mFabSelect.setShowProgressBackground(true);
                    mFabSelect.setIndeterminate(true);
                    MainActivity.instence.selectImage(OcrMain.this);
                    break;
            }
        }
    };

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (data.getData() != null && resultCode == Activity.RESULT_OK) {
            if (requestCode == MainActivity.instence.SELECT_FILE_REQUEST_CODE) {
                //    uploadBmpAbsPath = ContentHelper.absolutePathFromUri(getActivity(), data.getData());//= Environment.getExternalStorageDirectory().getAbsolutePath() + "/Pictures/picTool/search_tmp.png";
                final String path = ContentHelper.absolutePathFromUri(getActivity(), data.getData());
                if (path == null) {
                    LogTool.e("select pic error");
                    return;
                }
                new Thread(new Runnable() {
                    @Override
                    public void run() {
                        JSONObject response = null;
                        File image = new File(path);
                        try {
                            response = faceYoutu.GeneralOcr(image.getPath());
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                        StringBuilder sb = new StringBuilder();
                        OcrJavaBean ocrJavaBean = new Gson().fromJson(response.toString(), OcrJavaBean.class);
                        ArrayList<OcrJavaBean.Items> items = ocrJavaBean.items;
                        for (OcrJavaBean.Items s : items) {
                            sb.append(s.itemstring);
                        }
                        LogTool.t(sb.toString());
                        getActivity().runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                mFabSelect.setImageResource(R.drawable.fab_add);
                                mFabSelect.hideProgress();
                                running = false;
                            }
                        });
                    }
                }).start();
            }
        } else if (resultCode == Activity.RESULT_CANCELED) {
            mFabSelect.hideProgress();
            mFabSelect.setImageResource(R.drawable.ic_progress);
            running = false;
            Toast.makeText(getActivity().getApplicationContext(), "取消选择图片", Toast.LENGTH_SHORT).show();
        } else {
            MainActivity.instence.selectImage(this);
        }
        super.onActivityResult(requestCode, resultCode, data);
    }
}
