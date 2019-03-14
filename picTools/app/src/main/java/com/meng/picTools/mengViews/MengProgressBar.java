package com.meng.picTools.mengViews;

import android.app.*;
import android.content.*;
import android.view.*;
import android.widget.*;
import com.meng.picTools.*;
import com.meng.picTools.pixivGifDownloader.*;
import java.io.*;
import java.util.*;

public class MengProgressBar extends LinearLayout{
    Context context;

    TextView fileNameTextView;
    TextView statuTextView;
	TextView statusTextViewBytes;
    ProgressBar progressBar;
    DownloadZipThread downloadZipThread;
	UnzipThread unzipThread;
	createGif makeGif;
    ListView listView;

    public MengProgressBar(final Context context, ListView listView){
        super(context);
        this.listView=listView;
        this.context=context;
        LayoutInflater.from(context).inflate(R.layout.downloading_list_item,this);
        fileNameTextView=(TextView) findViewById(R.id.main_list_item_textview_filename);
        statuTextView=(TextView) findViewById(R.id.main_list_item_textview_statu);
		statusTextViewBytes=(TextView) findViewById(R.id.main_list_item_textview_statu_byte);
        progressBar=(ProgressBar) findViewById(R.id.main_list_item_progressbar);
        setOnClickListener(new OnClickListener() {
			  @Override
			  public void onClick(View v){
				  if(progressBar.getProgress()==100){
					  Intent intent = new Intent(context,playLayout.class);
					  intent.putExtra(Data.intentKeys.fileName,MainActivity.instence.getPixivZipPath(fileNameTextView.getText().toString()));
					  context.startActivity(intent);
					}else{
					  Toast.makeText(context,"下载完成后才可以查看",Toast.LENGTH_SHORT).show();
					}
				}
			});
	  }

    public void setProgress(int progress){
        progressBar.setProgress(progress);
        if(downloadZipThread.getDownloadedZipSize()==0){
            statuTextView.setText("正在连接");
		  }else{
            statuTextView.setText("正在下载");
			statusTextViewBytes.setText(downloadZipThread.getDownloadedZipSize()+"B/"+downloadZipThread.getZipSize()+"B ("+progress+"%)");
		  }
	  }

    public void startDownload(String PixivID){
        downloadZipThread=new DownloadZipThread(context,PixivID);
        downloadZipThread.start();
        update.start();
	  }

    Thread update = new Thread() {
        @Override
        public void run(){
            while(!downloadZipThread.isDownloaded){
                ((Activity) context).runOnUiThread(new Runnable() {
					  @Override
					  public void run(){
						  fileNameTextView.setText(downloadZipThread.getFileName());
						  setProgress((int) (((float) downloadZipThread.getDownloadedZipSize())/downloadZipThread.getZipSize()*100));
						}
					});
                try{
                    sleep(100);
				  }catch(InterruptedException e){
                    e.printStackTrace();
				  }
			  }
			  
			unzipThread=new UnzipThread(new File(MainActivity.instence.getPixivZipPath(downloadZipThread.getFileName())));
			unzipThread.start();
			while(!unzipThread.isUnzipSuccess){
                ((Activity) context).runOnUiThread(new Runnable() {
					  @Override
					  public void run(){
						//  fileNameTextView.setText(downloadZipThread.getFileName());
						  setProgress((int) (((float) unzipThread.getFilesCountNow())/unzipThread.getFilesCount()*100));
						}
					});
                try{
                    sleep(100);
				  }catch(InterruptedException e){
                    e.printStackTrace();
				  }
			  }
			  makeGif=  new createGif(
			  context,
			  unzipThread.getFrameFileFolder().getAbsolutePath(),
			  30);
			makeGif.start();
			  
			  
			while(!makeGif.isCreated){
                ((Activity) context).runOnUiThread(new Runnable() {
					  @Override
					  public void run(){
						  //  fileNameTextView.setText(downloadZipThread.getFileName());
						  setProgress((int) (((float) makeGif.getNowFile())/unzipThread.getFilesCount()*100));
						}
					});
                try{
                    sleep(100);
				  }catch(InterruptedException e){
                    e.printStackTrace();
				  }
			  }
			
            ((Activity) context).runOnUiThread(new Runnable() {
				  @Override
				  public void run(){
					  String[] downloadedFilesName = new File(MainActivity.instence.getPixivZipPath("")).list();
					  Arrays.sort(downloadedFilesName);
					  listView.setAdapter(new ArrayAdapter<String>(context,android.R.layout.simple_list_item_1,downloadedFilesName));
					  LinearLayout ll = (LinearLayout) getParent();
					  ll.removeView(MengProgressBar.this);
					}
				});
		  }
	  };

  }
