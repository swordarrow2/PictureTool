package com.meng.picTools.pixivPictureDownloader;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.widget.ArrayAdapter;
import android.widget.LinearLayout;
import android.widget.ListView;

import com.meng.picTools.MainActivity;
import com.meng.picTools.R;
import com.meng.picTools.javaBean.AnimPicJavaBean;
import com.meng.picTools.mengViews.MengProgressBar;
import com.meng.picTools.qrtools.LogTool;
import com.meng.picTools.qrtools.lib.SharedPreferenceHelper;
import com.waynejo.androidndkgif.GifEncoder;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.text.MessageFormat;
import java.util.Arrays;
import java.util.List;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;

public class DownloadRunnable implements Runnable {

    private String picUrl = "";
    private String absolutePath = "";
    private MengProgressBar mengProgressBar;
    private long imageSize = 0;
    private long downloadedFileSize = 0;
    private int filesNow = 0;
    private long filesCount = 0;
    private ListView listView;
    private TaskState taskState = TaskState.nothing;

    public DownloadRunnable(MengProgressBar mengProgressBar, String picUrl, String absolutePath, ListView listView) {
        this.mengProgressBar = mengProgressBar;
        this.picUrl = picUrl;
        this.listView = listView;
        this.absolutePath = absolutePath;
    }

    @Override
    public void run() {
        new Thread(new Runnable() {
            @Override
            public void run() {
                while (taskState != TaskState.end) {
                    switch (taskState) {
                        case nothing:
                            break;
                        case connecting:
                        case downloading:
                            setDownloadProgress(downloadedFileSize, imageSize);
                            break;
                        case unziping:
                            setUnzipProgress(filesNow, filesCount);
                            break;
                        case creatingGif:
                            setCreateGifProgress(filesNow, filesCount);
                            break;
                        case end:
                            break;
                    }
                    try {
                        Thread.sleep(100);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }
            }
        }).start();

        File file = new File(absolutePath);
        taskState = TaskState.connecting;
        try {
            URL u = new URL(picUrl);
            HttpURLConnection connection = (HttpURLConnection) u.openConnection();
            connection.setRequestMethod("GET");
            connection.setRequestProperty("Referer", picUrl);
            connection.setRequestProperty("cookie", SharedPreferenceHelper.getValue(Data.preferenceKeys.keyCookieValue));
            connection.setRequestProperty("User-Agent", "Mozilla/5.0 (Windows NT 6.1; WOW64; rv:28.0) Gecko/20100101 Firefox/28.0");
            imageSize = connection.getContentLength();
            setProgressBarFileName(file.getName());
            if (file.exists()) {
                if (file.length() == imageSize) {
                    LogTool.t(mengProgressBar.context.getString(R.string.file_exist) + file.getName());
                } else {
                    LogTool.t(mengProgressBar.context.getString(R.string.file_exist) + "但似乎并不完整，正在重新下载");
                    InputStream is = connection.getInputStream();
                    taskState = TaskState.downloading;
                    if (is != null) {
                        FileOutputStream fos = new FileOutputStream(file);
                        byte buf[] = new byte[4096];
                        int len = 0;
                        while ((len = is.read(buf)) > 0) {
                            fos.write(buf, 0, len);
                            downloadedFileSize += len;
                        }
                    }
                    is.close();
                }
            } else {
                taskState = TaskState.downloading;
                InputStream is = connection.getInputStream();
                if (is != null) {
                    FileOutputStream fos = new FileOutputStream(file);
                    byte buf[] = new byte[4096];
                    int len = 0;
                    while ((len = is.read(buf)) > 0) {
                        fos.write(buf, 0, len);
                        downloadedFileSize += len;
                    }
                }
                is.close();
            }
            connection.disconnect();
        } catch (IOException e) {
            LogTool.i(e.toString());
            downloadEnd();
            return;
        }
        if (mengProgressBar.pictureInfoJavaBean.isAnimPicture) {
            String zipName = absolutePath.substring(absolutePath.lastIndexOf("/") + 1, absolutePath.lastIndexOf("."));
            File frameFileFolder = new File(MainActivity.instence.getTmpFolder() + zipName);
            File nomedia = new File(frameFileFolder.getParent() + File.separator + ".nomedia");
            setProgressBarFileName(zipName);
            taskState = TaskState.unziping;
            try {
                if (!nomedia.exists()) nomedia.createNewFile();
                if (!frameFileFolder.exists()) frameFileFolder.mkdirs();
                byte[] buffer = new byte[1024];
                filesCount = countFilesInZip(file);
                ZipInputStream zis = new ZipInputStream(new FileInputStream(file));
                ZipEntry ze = zis.getNextEntry();
                while (ze != null) {
                    filesNow++;
                    String fileName = ze.getName();
                    File frameFile = new File(frameFileFolder.getAbsolutePath() + File.separator + fileName);
                    FileOutputStream nfos = new FileOutputStream(frameFile);
                    int len;
                    while ((len = zis.read(buffer)) > 0) {
                        nfos.write(buffer, 0, len);
                    }
                    nfos.close();
                    ze = zis.getNextEntry();
                }
            } catch (Exception e) {
                LogTool.t(e.toString());
                downloadEnd();
                return;
            }
            taskState = TaskState.creatingGif;
            filesNow = 0;
            setProgressBarFileName(MainActivity.instence.getGifPath(zipName));
            if (SharedPreferenceHelper.getBoolean(Data.preferenceKeys.useJava)) {
                createGifJava(frameFileFolder.getAbsolutePath() + File.separator, zipName);
            } else {
                createGifNative(frameFileFolder.getAbsolutePath() + File.separator, zipName);
            }
        }
        taskState = TaskState.end;
        downloadEnd();
    }

    private void setProgressBarFileName(final String gifPath) {
        mengProgressBar.context.runOnUiThread(new Runnable() {
            @Override
            public void run() {
                mengProgressBar.setFileName(gifPath);
            }
        });
    }

    private void createGifJava(String folder, String fileName) {
        List<AnimPicJavaBean.Body.Frames> lf = mengProgressBar.pictureInfoJavaBean.animPicJavaBean.body.frames;
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        AnimatedGifEncoder localAnimatedGifEncoder = new AnimatedGifEncoder();
        localAnimatedGifEncoder.start(baos);//start
        localAnimatedGifEncoder.setRepeat(0);//设置生成gif的开始播放时间。0为立即开始播放

        for (int i = 0; i < lf.size(); i++) {
            localAnimatedGifEncoder.setDelay(Integer.parseInt(lf.get(i).delay));
            localAnimatedGifEncoder.addFrame(BitmapFactory.decodeFile(folder + lf.get(i).file));
            filesNow = i;
        }
        localAnimatedGifEncoder.finish();
        String path = MainActivity.instence.getGifPath(fileName);
        try {
            FileOutputStream fos = new FileOutputStream(path);
            baos.writeTo(fos);
            baos.flush();
            fos.flush();
            baos.close();
            fos.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
        registImage(path);
    }

    private void createGifNative(String folder, String fileName) {
        String filePath = MainActivity.instence.getGifPath(fileName);
        List<AnimPicJavaBean.Body.Frames> lf = mengProgressBar.pictureInfoJavaBean.animPicJavaBean.body.frames;
        Bitmap bmp = BitmapFactory.decodeFile(folder + lf.get(0).file);
        GifEncoder gifEncoder = new GifEncoder();
        gifEncoder.setDither(false);
        try {
            gifEncoder.init(bmp.getWidth(), bmp.getHeight(), filePath, GifEncoder.EncodingType.ENCODING_TYPE_NORMAL_LOW_MEMORY);
        } catch (FileNotFoundException e) {
            LogTool.e(e.toString());
            return;
        }
        for (int i = 0; i < lf.size(); i++) {
            gifEncoder.encodeFrame(BitmapFactory.decodeFile(folder + lf.get(i).file), Integer.parseInt(lf.get(i).delay));
            filesNow = i;
        }
        gifEncoder.close();
        registImage(filePath);
    }

    private int countFilesInZip(File zipFile) {
        int filesCount = 0;
        try {
            ZipInputStream zis = new ZipInputStream(new FileInputStream(zipFile));
            ZipEntry ze = zis.getNextEntry();
            while (ze != null) {
                filesCount++;
                ze = zis.getNextEntry();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return filesCount;
    }

    public void setDownloadProgress(float downloadedSize, float fileSize) {
        float progress = downloadedSize / fileSize * 100;
        mengProgressBar.setProgress((int) progress);
        if (downloadedSize == 0) {
            mengProgressBar.setStatusText("正在连接");
        } else {
            mengProgressBar.setStatusText("正在下载");
            mengProgressBar.setProgressText(MessageFormat.format("{0}B/{1}B ({2}%)", downloadedSize, fileSize, progress));
        }
    }

    public void setUnzipProgress(float unzipCount, float fileCount) {
        mengProgressBar.setProgress((int) (unzipCount / fileCount * 100));
        mengProgressBar.setStatusText("正在解压");
        mengProgressBar.setProgressText(MessageFormat.format("{0}/{1}", unzipCount, fileCount));

    }

    public void setCreateGifProgress(float gifedCount, float fileCount) {
        mengProgressBar.setProgress((int) (gifedCount / fileCount * 100));
        mengProgressBar.setStatusText("正在生成gif");
        mengProgressBar.setProgressText(MessageFormat.format("{0}/{1}", gifedCount, fileCount));
    }

    private void registImage(String path) {
        mengProgressBar.context.sendBroadcast(new Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE, Uri.parse("file://" + path)));
    }

    private void downloadEnd() {
        mengProgressBar.context.runOnUiThread(new Runnable() {
            @Override
            public void run() {
                String[] downloadedFilesName = new File(MainActivity.instence.getPixivZipPath("")).list();
                Arrays.sort(downloadedFilesName);
                listView.setAdapter(new ArrayAdapter<String>(mengProgressBar.context, android.R.layout.simple_list_item_1, downloadedFilesName));
                LinearLayout ll = (LinearLayout) mengProgressBar.getParent();
                ll.removeView(mengProgressBar);
            }
        });
    }
    //使用系统下载器下载
  /*  private void systemDownload(String zipUrl){
        String expandName = zipUrl.substring(zipUrl.lastIndexOf(".")+1,zipUrl.length()).toLowerCase();
        String fileName = zipUrl.substring(zipUrl.lastIndexOf("/")+1,zipUrl.lastIndexOf("."));
        DownloadManager.Request request = new DownloadManager.Request(Uri.parse(zipUrl));
        request.setNotificationVisibility(DownloadManager.Request.VISIBILITY_VISIBLE);
        request.setVisibleInDownloadsUi(true);
        request.setTitle("Pixiv图片下载");
        request.setDescription(fileName+"."+expandName);
        request.setDestinationInExternalPublicDir(MainActivity.instence.getPixivZipPath(""),fileName+"."+expandName);
        request.addRequestHeader("User-Agent","Mozilla/5.0 (Windows NT 6.1; WOW64; rv:28.0) Gecko/20100101 Firefox/28.0");
        request.addRequestHeader("cookie",SharedPreferenceHelper.getValue(Data.preferenceKeys.keyCookieValue));
        request.addRequestHeader("Referer",picUrl);
        DownloadManager downloadManager = (DownloadManager) context.getSystemService(Context.DOWNLOAD_SERVICE);
        downloadManager.enqueue(request);
        context.registerReceiver(new BroadcastReceiver() {
			  @Override
			  public void onReceive(Context context,Intent intent){
				  isDownloaded=true;
				}
			},new IntentFilter(DownloadManager.ACTION_DOWNLOAD_COMPLETE));
	  }
*/
}
