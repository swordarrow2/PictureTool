package com.meng.picTools.pixivPictureDownloader;

import android.app.*;
import android.content.*;
import android.net.*;

import com.meng.picTools.*;
import com.meng.picTools.qrtools.*;
import com.meng.picTools.qrtools.lib.SharedPreferenceHelper;

import java.io.*;
import java.net.*;

public class DownloadImageThread extends Thread{
    private String picUrl = "";
    private Context context;
    public boolean isDownloaded = false;
    private long imageSize = 0;
    private long downloadedFileSize = 0;
    private String fileName = "";

    public DownloadImageThread(Context c, String picUrl){
        context=c;
        this.picUrl=picUrl;
	  }

    public String getFileName(){
        return fileName;
	  }

    public long getDownloadedFileSize(){
        return downloadedFileSize;
	  }

    public long getImageSize(){
        return imageSize;
	  }

    @Override
    public void run(){     
		downloadFile(picUrl);	  
	  }

    private HttpURLConnection getConnection(URL u) throws IOException{
        HttpURLConnection connection = (HttpURLConnection) u.openConnection();
        connection.setRequestMethod("GET");
        connection.setRequestProperty("Referer",picUrl);
        connection.setRequestProperty("cookie", SharedPreferenceHelper.getValue(Data.preferenceKeys.keyCookieValue));
        connection.setRequestProperty("User-Agent","Mozilla/5.0 (Windows NT 6.1; WOW64; rv:28.0) Gecko/20100101 Firefox/28.0");
        return connection;
	  }

    private void downloadFile(String fileUrl){
        try{
            URL u = new URL(fileUrl);
            HttpURLConnection connection = getConnection(u);
            imageSize=connection.getContentLength();
            String expandName = fileUrl.substring(fileUrl.lastIndexOf(".")+1,fileUrl.length()).toLowerCase();
            String fileName = fileUrl.substring(fileUrl.lastIndexOf("/")+1,fileUrl.lastIndexOf("."));
            File file = new File(expandName.equalsIgnoreCase("zip")? MainActivity.instence.getPixivZipPath(fileName+"."+expandName):MainActivity.instence.getPixivImagePath(fileName+"."+expandName));
            this.fileName=fileName+"."+expandName;
            if(file.exists()){
                if(file.length()==imageSize){
                    LogTool.t(context.getString(R.string.file_exist)+file.getName());
				  }else{
                    LogTool.t(context.getString(R.string.file_exist)+"但似乎并不完整，正在重新下载");
                    InputStream is = connection.getInputStream();
                    if(is!=null){
                        FileOutputStream fos = new FileOutputStream(file);
                        byte buf[] = new byte[4096];
                        int len = 0;
                        while((len=is.read(buf))>0){
                            fos.write(buf,0,len);
                            downloadedFileSize+=len;
						  }
					  }
                    is.close();
				  }
			  }else{
                InputStream is = connection.getInputStream();
                if(is!=null){
                    FileOutputStream fos = new FileOutputStream(file);
                    byte buf[] = new byte[4096];
                    int len = 0;
                    while((len=is.read(buf))>0){
                        fos.write(buf,0,len);
                        downloadedFileSize+=len;
					  }
				  }
                is.close();
			  }
            connection.disconnect();
            isDownloaded=true;
		  }catch(IOException e){
		  }
	  }

    //使用系统下载器下载
    private void systemDownload(String zipUrl){
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

  }
