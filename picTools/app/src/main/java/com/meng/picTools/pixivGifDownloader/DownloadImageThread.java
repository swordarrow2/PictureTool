package com.meng.picTools.pixivGifDownloader;

import android.app.*;
import android.content.*;
import android.net.*;

import com.google.gson.*;
import com.meng.picTools.*;
import com.meng.picTools.qrtools.*;

import java.io.*;
import java.net.*;

import com.meng.picTools.mengViews.*;

public class DownloadImageThread extends Thread {
    private String pixivId = "";
    private Context context;
    public boolean isDownloaded = false;
    private long imageSize = 0;
    private long downloadedFileSize = 0;
    private String fileName = "";
    private MengProgressBar mengProgressBar;
    private PixivZipJavaBean pixivZipJavaBean;
    private String imageUrl = "";

    public DownloadImageThread(Context c, MengProgressBar mengp, String pixivId) {
        context = c;
        mengProgressBar = mengp;
        this.pixivId = pixivId;
    }

    public String getFileName() {
        return fileName;
    }

    public long getDownloadedFileSize() {
        return downloadedFileSize;
    }

    public long getImageSize() {
        return imageSize;
    }

    @Override
    public void run() {
        getPicInfo(pixivId);
        if (pixivZipJavaBean.error.equals("false")) {
            downloadFile(MainActivity.instence.sharedPreference.getBoolean(Data.preferenceKeys.downloadBigPicture) ? pixivZipJavaBean.body.originalSrc : pixivZipJavaBean.body.src);
        } else {
            downloadFile(imageUrl);
        }
        // downloadFile();
    }

    private HttpURLConnection getConnection(URL u) throws IOException {
        HttpURLConnection connection = (HttpURLConnection) u.openConnection();
        connection.setRequestMethod("GET");
        connection.setRequestProperty("Referer", "https://www.pixiv.net/member_illust.php?mode=medium&illust_id=" + pixivId.replace("https://www.pixiv.net/member_illust.php?mode=medium&illust_id=", ""));
        connection.setRequestProperty("cookie", MainActivity.instence.sharedPreference.getValue(Data.preferenceKeys.keyCookieValue));
        connection.setRequestProperty("User-Agent", "Mozilla/5.0 (Windows NT 6.1; WOW64; rv:28.0) Gecko/20100101 Firefox/28.0");
        return connection;
    }

    private void getPicInfo(String picId) {
        try {
            URL u = new URL(getPicInfoJsonAddress(picId));
            HttpURLConnection connection = getConnection(u);
            connection.setConnectTimeout(10000);
            connection.setReadTimeout(10000);
            InputStream in = connection.getInputStream();
            BufferedReader br = new BufferedReader(new InputStreamReader(in));
            StringBuilder sb = new StringBuilder();
            String line;
            while ((line = br.readLine()) != null) {
                sb.append(line);
            }
            String tmpStr = sb.toString();
            Gson gson = new Gson();
            pixivZipJavaBean = gson.fromJson(tmpStr, PixivZipJavaBean.class);
            mengProgressBar.zjb = pixivZipJavaBean;
            if (pixivZipJavaBean.error.equals("true")) {
                URL url = new URL("https://www.pixiv.net/member_illust.php?mode=medium&illust_id=" + picId);
                HttpURLConnection hc = getConnection(url);
                InputStream in2 = hc.getInputStream();
                BufferedReader br2 = new BufferedReader(new InputStreamReader(in2));
                StringBuilder sb2 = new StringBuilder();
                String line2;
                while ((line2 = br2.readLine()) != null) {
                    sb2.append(line2);
                }
                String tmpStr2 = sb2.toString();
                tmpStr2 = tmpStr2.substring(tmpStr2.indexOf("\"original\":\"") + "\"original\":\"".length());
                tmpStr2 = tmpStr2.substring(0, tmpStr2.indexOf("\""));
                imageUrl=tmpStr2;
            }
        } catch (FileNotFoundException e) {
            log.t(context.getString(R.string.maybe_need_login));
            context.startActivity(new Intent(context, login.class));
        } catch (Exception e) {
            log.e(e);
        }
    }

    private void downloadFile(String zipUrl) {
        try {
            URL u = new URL(zipUrl);
            HttpURLConnection connection = getConnection(u);
            imageSize = connection.getContentLength();
            String expandName = zipUrl.substring(zipUrl.lastIndexOf(".") + 1, zipUrl.length()).toLowerCase();
            String fileName = zipUrl.substring(zipUrl.lastIndexOf("/") + 1, zipUrl.lastIndexOf("."));
            File file = new File(expandName.equalsIgnoreCase("zip")? MainActivity.instence.getPixivZipPath(fileName + "." + expandName):MainActivity.instence.getPixivImagePath(fileName + "." + expandName));
            this.fileName = fileName + "." + expandName;
            if (file.exists()) {
                if (file.length() == imageSize) {
                    log.t(context.getString(R.string.file_exist) + file.getName());
                } else {
                    log.t(context.getString(R.string.file_exist) + "但似乎并不完整，正在重新下载");
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
            } else {
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
            isDownloaded = true;
        } catch (IOException e) {
        }
    }

    public static String getPicInfoJsonAddress(String id) {
        String pix = "https://www.pixiv.net/member_illust.php?mode=medium&illust_id=";
        String picJsonAddress = "https://www.pixiv.net/ajax/illust/";
        String picJsonAddress2 = "/ugoira_meta";
        if (id.startsWith(pix)) {
            return picJsonAddress + id.replace(pix, "") + picJsonAddress2;
        }
        return picJsonAddress + id + picJsonAddress2;
    }

    //使用系统下载器下载
    private void systemDownload(String zipUrl) {
        String expandName = zipUrl.substring(zipUrl.lastIndexOf(".") + 1, zipUrl.length()).toLowerCase();
        String fileName = zipUrl.substring(zipUrl.lastIndexOf("/") + 1, zipUrl.lastIndexOf("."));
        DownloadManager.Request request = new DownloadManager.Request(Uri.parse(zipUrl));
        request.setNotificationVisibility(DownloadManager.Request.VISIBILITY_VISIBLE);
        request.setVisibleInDownloadsUi(true);
        request.setTitle("Pixiv动图下载");
        request.setDescription(fileName + "." + expandName);
        request.setDestinationInExternalPublicDir(MainActivity.instence.getPixivZipPath(""), fileName + "." + expandName);
        request.addRequestHeader("User-Agent", "Mozilla/5.0 (Windows NT 6.1; WOW64; rv:28.0) Gecko/20100101 Firefox/28.0");
        request.addRequestHeader("cookie", MainActivity.instence.sharedPreference.getValue(Data.preferenceKeys.keyCookieValue));
        request.addRequestHeader("Referer", "https://www.pixiv.net/member_illust.php?mode=medium&illust_id=" + pixivId);
        DownloadManager downloadManager = (DownloadManager) context.getSystemService(Context.DOWNLOAD_SERVICE);
        downloadManager.enqueue(request);
        context.registerReceiver(new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                isDownloaded = true;
            }
        }, new IntentFilter(DownloadManager.ACTION_DOWNLOAD_COMPLETE));
    }
}
