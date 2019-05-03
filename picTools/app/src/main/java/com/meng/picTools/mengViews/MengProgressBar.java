package com.meng.picTools.mengViews;

import android.app.*;
import android.content.*;
import android.view.*;
import android.widget.*;

import com.meng.picTools.*;
import com.meng.picTools.javaBean.PictureInfoJavaBean;
import com.meng.picTools.pixivPictureDownloader.*;

import java.io.*;
import java.util.*;

public class MengProgressBar extends LinearLayout {
    public Activity context;
    private TextView fileNameTextView;
    private TextView textViewStatus;
    private TextView textViewProgress;
    private ProgressBar progressBar;
    public PictureInfoJavaBean pictureInfoJavaBean;

    public MengProgressBar(Activity context, ListView listView, PictureInfoJavaBean pictureInfoJavaBean, String picUrl) {
        super(context);
        this.pictureInfoJavaBean = pictureInfoJavaBean;
        this.context = context;
        LayoutInflater.from(context).inflate(R.layout.downloading_list_item, this);
        fileNameTextView = (TextView) findViewById(R.id.main_list_item_textview_filename);
        textViewStatus = (TextView) findViewById(R.id.main_list_item_textview_statu);
        textViewProgress = (TextView) findViewById(R.id.main_list_item_textview_statu_byte);
        progressBar = (ProgressBar) findViewById(R.id.main_list_item_progressbar);

        String fileAbsolutePath = "";
        String expandName = picUrl.substring(picUrl.lastIndexOf(".") + 1, picUrl.length()).toLowerCase();
        String fileName = picUrl.substring(picUrl.lastIndexOf("/") + 1, picUrl.lastIndexOf("."));
        if (expandName.equalsIgnoreCase("zip")) {
            fileAbsolutePath = MainActivity.instence.getPixivZipPath(fileName + "." + expandName);
        } else {
            File folder = new File(MainActivity.instence.getPixivImagePath(pictureInfoJavaBean.id + "/"));
            if (!folder.exists()) folder.mkdirs();
            if (pictureInfoJavaBean.staticPicJavaBean.body.size() > 1) {
                fileAbsolutePath = MainActivity.instence.getPixivImagePath(pictureInfoJavaBean.id + "/" + fileName + "." + expandName);
            } else {
                fileAbsolutePath = MainActivity.instence.getPixivImagePath(fileName + "." + expandName);
            }
        }
        MainActivity2.instence.pixivDownloadMainFragment.threadPool.execute(new DownloadRunnable(this, picUrl, fileAbsolutePath, listView));
    }

    public void setProgress(int progress) {
        progressBar.setProgress(progress);
    }

    public void setStatusText(String statusText) {
        textViewStatus.setText(statusText);
    }

    public void setProgressText(String progressText) {
        textViewProgress.setText(progressText);
    }

    public void setFileName(String fileName) {
        fileNameTextView.setText(fileName);
    }

}
