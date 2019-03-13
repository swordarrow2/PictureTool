package com.meng.picTools.pixivGifDownloader;

import android.app.*;
import android.content.*;
import android.net.*;
import com.google.gson.*;
import com.meng.picTools.*;
import com.meng.picTools.qrtools.*;
import java.io.*;
import java.net.*;

public class DownloadZipThread extends Thread{
    private String id = "";
    private Context context;
    public boolean isDownloaded = false;
    private long zipSize = 0;
    private long downloadedZipSize = 0;
    private String fileName = "";

    public DownloadZipThread(Context c,String pixivId){
        context=c;
        id=pixivId;
	  }

    public String getFileName(){
        return fileName;
	  }

    public long getDownloadedZipSize(){
        return downloadedZipSize;
	  }

    public long getZipSize(){
        return zipSize;
	  }

    @Override
    public void run(){
        download(getZipUrl(id));
	  }

    private String getZipUrl(String picId){
        try{
            URL u = new URL(getPicInfoJsonAddress(picId));
            HttpURLConnection connection = (HttpURLConnection) u.openConnection();
            connection.setRequestMethod("GET");
            connection.setConnectTimeout(10000);
            connection.setReadTimeout(10000);
            connection.setRequestProperty("cookie",MainActivity.instence.sharedPreference.getValue(Data.preferenceKeys.keyCookieValue));
            connection.setRequestProperty("User-Agent","Mozilla/5.0 (Windows NT 6.1; WOW64; rv:28.0) Gecko/20100101 Firefox/28.0");
            InputStream in = connection.getInputStream();
            BufferedReader br = new BufferedReader(new InputStreamReader(in));
            StringBuilder sb = new StringBuilder();
            String line;
            while((line=br.readLine())!=null){
                sb.append(line);
			  }
            Gson gson = new Gson();
            zipJavaBean zjb = gson.fromJson(sb.toString(),zipJavaBean.class);
            return MainActivity.instence.sharedPreference.getBoolean(Data.preferenceKeys.downloadBigPicture)? zjb.body.originalSrc :zjb.body.src;
		  }catch(FileNotFoundException e){
            log.t(context.getString(R.string.maybe_need_login));
            context.startActivity(new Intent(context,login.class));
		  }catch(Exception e){
            log.e(e);
		  }
        return "";
	  }

    private void download(String zipUrl){
        try{
            URL url = new URL(zipUrl);
            HttpURLConnection urlConn = (HttpURLConnection) url.openConnection();
            urlConn.setRequestProperty("User-Agent","Mozilla/5.0 (Windows NT 6.1; WOW64; rv:28.0) Gecko/20100101 Firefox/28.0");
            urlConn.setRequestProperty("cookie",MainActivity.instence.sharedPreference.getValue(Data.preferenceKeys.keyCookieValue));
            urlConn.setRequestProperty("Referer","https://www.pixiv.net/member_illust.php?mode=medium&illust_id="+id.replace("https://www.pixiv.net/member_illust.php?mode=medium&illust_id=",""));
            zipSize=urlConn.getContentLength();
            String expandName = zipUrl.substring(zipUrl.lastIndexOf(".")+1,zipUrl.length()).toLowerCase();
            String fileName = zipUrl.substring(zipUrl.lastIndexOf("/")+1,zipUrl.lastIndexOf("."));
            File file = new File(MainActivity.instence.getPixivZipPath(fileName+"."+expandName));
            this.fileName=fileName+"."+expandName;
            if(file.exists()){
                if(file.length()==zipSize){
                    log.t(context.getString(R.string.file_exist)+file.getName());
				  }else{
                    log.t(context.getString(R.string.file_exist)+"但似乎并不完整，正在重新下载");
                    InputStream is = urlConn.getInputStream();
                    if(is!=null){
                        FileOutputStream fos = new FileOutputStream(file);
                        byte buf[] = new byte[4096];
                        int len = 0;
                        while((len=is.read(buf))>0){
                            fos.write(buf,0,len);
                            downloadedZipSize+=len;
						  }
					  }
                    is.close();
				  }
			  }else{
                InputStream is = urlConn.getInputStream();
                if(is!=null){
                    FileOutputStream fos = new FileOutputStream(file);
                    byte buf[] = new byte[4096];
                    int len = 0;
                    while((len=is.read(buf))>0){
                        fos.write(buf,0,len);
                        downloadedZipSize+=len;
					  }
				  }
                is.close();
			  }
            urlConn.disconnect();
            isDownloaded=true;
		  }catch(IOException e){
		  }
	  }

    private String getPicInfoJsonAddress(String id){
        String pix = "https://www.pixiv.net/member_illust.php?mode=medium&illust_id=";
        String picJsonAddress = "https://www.pixiv.net/ajax/illust/";
        String picJsonAddress2 = "/ugoira_meta";
        if(id.startsWith(pix)){
            return picJsonAddress+id.replace(pix,"")+picJsonAddress2;
		  }
        return picJsonAddress+id+picJsonAddress2;
	  }

    //使用系统下载器下载
    private void systemDownload(String zipUrl){
        String expandName = zipUrl.substring(zipUrl.lastIndexOf(".")+1,zipUrl.length()).toLowerCase();
        String fileName = zipUrl.substring(zipUrl.lastIndexOf("/")+1,zipUrl.lastIndexOf("."));
        DownloadManager.Request request = new DownloadManager.Request(Uri.parse(zipUrl));
        request.setNotificationVisibility(DownloadManager.Request.VISIBILITY_VISIBLE);
        request.setVisibleInDownloadsUi(true);
        request.setTitle("Pixiv动图下载");
        request.setDescription(fileName+"."+expandName);
        request.setDestinationInExternalPublicDir(MainActivity.instence.getPixivZipPath(""),fileName+"."+expandName);
        request.addRequestHeader("User-Agent","Mozilla/5.0 (Windows NT 6.1; WOW64; rv:28.0) Gecko/20100101 Firefox/28.0");
        request.addRequestHeader("cookie",MainActivity.instence.sharedPreference.getValue(Data.preferenceKeys.keyCookieValue));
        request.addRequestHeader("Referer","https://www.pixiv.net/member_illust.php?mode=medium&illust_id="+id);
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
