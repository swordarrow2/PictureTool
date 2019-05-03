package com.meng.picTools.mengViews;

import android.app.*;
import android.content.*;
import android.view.*;
import android.widget.*;

import com.meng.picTools.*;
import com.meng.picTools.javaBean.PictureInfoJavaBean;
import com.meng.picTools.pixivPictureDownloader.*;

import java.io.*;
import java.text.MessageFormat;
import java.util.*;

public class MengProgressBar extends LinearLayout {
    private Context context;
    private TextView fileNameTextView;
    private TextView statuTextView;
    private TextView statusTextViewBytes;
    private ProgressBar progressBar;
    private DownloadImageThread downloadImageThread;
    private UnzipThread unzipThread;
    private CreateGifThread makeGif;
    private ListView listView;
    public PictureInfoJavaBean pictureInfoJavaBean;

    public MengProgressBar(final Context context, ListView listView, PictureInfoJavaBean pictureInfoJavaBean, String url) {
        super(context);
        this.pictureInfoJavaBean = pictureInfoJavaBean;
        this.listView = listView;
        this.context = context;
        LayoutInflater.from(context).inflate(R.layout.downloading_list_item, this);
        fileNameTextView = (TextView) findViewById(R.id.main_list_item_textview_filename);
        statuTextView = (TextView) findViewById(R.id.main_list_item_textview_statu);
        statusTextViewBytes = (TextView) findViewById(R.id.main_list_item_textview_statu_byte);
        progressBar = (ProgressBar) findViewById(R.id.main_list_item_progressbar);
		
		String path="";
		String expandName = url.substring(url.lastIndexOf(".")+1,url.length()).toLowerCase();
		String fileName = url.substring(url.lastIndexOf("/")+1,url.lastIndexOf("."));
		if(expandName.equalsIgnoreCase("zip")){
			path= MainActivity.instence.getPixivZipPath(fileName+"."+expandName);
		  }else{
			  File folder=new File(MainActivity.instence.getPixivImagePath(pictureInfoJavaBean.id+"/"));
			  if(!folder.exists()){
				folder.mkdirs();
			  }
			if(pictureInfoJavaBean.staticPicJavaBean.body.size()>1){
				  path=MainActivity.instence.getPixivImagePath(pictureInfoJavaBean.id+"/"+fileName+"."+expandName);
			}else{
				  path=MainActivity.instence.getPixivImagePath(fileName+"."+expandName);
			}
		  }
		
        downloadImageThread = new DownloadImageThread(context, url, path);
        downloadImageThread.start();
        update.start();
    }

    public void setDownloadProgress(float downloadedSize, float fileSize) {
        float progress = downloadedSize / fileSize * 100;
        progressBar.setProgress((int) progress);
        if (downloadImageThread.getDownloadedFileSize() == 0) {
            statuTextView.setText("正在连接");
        } else {
            statuTextView.setText("正在下载");
            statusTextViewBytes.setText(MessageFormat.format("{0}B/{1}B ({2}%)", downloadedSize, fileSize, progress));
        }
    }

    public void setUnzipProgress(float unzipCount, float fileCount) {
        progressBar.setProgress((int) (unzipCount / fileCount * 100));
        statuTextView.setText("正在解压");
        statusTextViewBytes.setText(MessageFormat.format("{0}/{1}", unzipCount, fileCount));

    }

    public void setMakeGifProgress(float gifedCount, float fileCount) {
        progressBar.setProgress((int) (gifedCount / fileCount * 100));
        statuTextView.setText("正在生成gif");
        statusTextViewBytes.setText(MessageFormat.format("{0}/{1}", gifedCount, fileCount));
    }

    Thread update = new Thread() {
        @Override
        public void run() {
            while (!downloadImageThread.isDownloaded) {
                ((Activity) context).runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        fileNameTextView.setText(downloadImageThread.getFileName());
                        setDownloadProgress(downloadImageThread.getDownloadedFileSize(), downloadImageThread.getImageSize());
                    }
                });
                try {
                    sleep(100);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
            if (pictureInfoJavaBean.isAnimPicture) {
                unzipThread = new UnzipThread(new File(MainActivity.instence.getPixivZipPath(downloadImageThread.getFileName())));
                unzipThread.start();
                while (!unzipThread.isUnzipSuccess) {
                    ((Activity) context).runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            fileNameTextView.setText(unzipThread.zipName);
                            setUnzipProgress(unzipThread.getFilesCountNow(), unzipThread.getFilesCount());
                        }
                    });
                    try {
                        sleep(100);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }
                makeGif = new CreateGifThread(
                        context,
                        MengProgressBar.this,
                        unzipThread.getFrameFileFolder().getAbsolutePath()
                );
                makeGif.start();

                while (!makeGif.isCreated) {
                    ((Activity) context).runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            fileNameTextView.setText(MessageFormat.format("{0}.gif", makeGif.gifName));
                            setMakeGifProgress(makeGif.getNowFile(), makeGif.getFilesCount());
                        }
                    });
                    try {
                        sleep(100);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }
            }

            ((Activity) context).runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    String[] downloadedFilesName = new File(MainActivity.instence.getPixivZipPath("")).list();
                    Arrays.sort(downloadedFilesName);
                    listView.setAdapter(new ArrayAdapter<String>(context, android.R.layout.simple_list_item_1, downloadedFilesName));
                    LinearLayout ll = (LinearLayout) getParent();
                    ll.removeView(MengProgressBar.this);
					--MainActivity2.instence.pixivDownloadMainFragment.threadCount;
                }
            });
        }
    };
}
