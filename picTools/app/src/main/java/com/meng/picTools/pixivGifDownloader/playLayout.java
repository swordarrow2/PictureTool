package com.meng.picTools.pixivGifDownloader;

import android.app.*;
import android.content.*;
import android.graphics.*;
import android.net.*;
import android.os.*;
import android.provider.*;
import android.util.*;
import android.view.*;
import android.widget.*;
import android.widget.SeekBar.*;
import com.meng.picTools.*;
import com.waynejo.androidndkgif.*;
import java.io.*;
import java.util.*;
import java.util.zip.*;

public class playLayout extends Activity{

    public static ProgressBar gifProgress;
    private LinearLayout seekbarlinearLayout;
    private LinearLayout gifLinearLayout;
    private ProgressBar loadProgress;
    private LinearLayout loadLinearLayout;
    private LinearLayout unzipLinearLayout;
    private ProgressBar unzipProgress;
    private ImageView imageView;
    private String zipAbsolutePath;
    private SeekBar seekBar;
    private TextView tv;
    private String[] filesName;
    private Bitmap[] bms;
    private int i = 0;
    private File frameFileFolder;
    private Thread unzip;
    private Thread loadImg;
    private Thread playimg;
    private boolean loadfinish = false;
    private int gifHeight = 1;
    private int gifWidth = 1;
    private int gifDelay = 1;
    private int gifQuality = 10;
    private String fileName;
    private final int LOADIMAGEPROGRESS = 1;
    private final int LOADING = 2;
    private final int LOADED = 3;
    private final int NEXTFRAME = 4;
    private final int STARTMAKEGIF = 5;
    private final int GIFSUCCESS = 6;
    private final int UNZIPIMAGEPROGRESS = 7;
    private final int UNZIPSUCCESS = 8;
    private final int UNZIP = 9;
    private boolean makingGIf = false;

    @Override
    protected void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
        setContentView(R.layout.play_layout);
        imageView=(ImageView) findViewById(R.id.play_image);
        seekBar=(SeekBar) findViewById(R.id.play_seekbar);
        tv=(TextView) findViewById(R.id.play_text);
        gifProgress=(ProgressBar) findViewById(R.id.make_gif_progressbar);
        seekbarlinearLayout=(LinearLayout) findViewById(R.id.play_layout_seekbar_linearlayout);
        gifLinearLayout=(LinearLayout) findViewById(R.id.gif_linearlayout);
        loadLinearLayout=(LinearLayout) findViewById(R.id.load_linearlayout);
        loadProgress=(ProgressBar) findViewById(R.id.load_progressbar);
        unzipLinearLayout=(LinearLayout) findViewById(R.id.unzip_linearlayout);
        unzipProgress=(ProgressBar) findViewById(R.id.unzip_progressbar);

        Intent i = getIntent();
        zipAbsolutePath=i.getStringExtra(Data.intentKeys.fileName);
        fileName=zipAbsolutePath.substring(zipAbsolutePath.lastIndexOf("/")+1,zipAbsolutePath.lastIndexOf("."));
        unzip=new unzipThread(new File(zipAbsolutePath),fileName);
        unzip.start();
        loadfinish=false;
        gifDelay=seekBar.getProgress();
        seekBar.setOnSeekBarChangeListener(new OnSeekBarChangeListener() {

			  @Override
			  public void onProgressChanged(SeekBar p1,int p2,boolean p3){
				  gifDelay=p2;
				  tv.setText(String.format("帧延迟%d",gifDelay));
				}

			  @Override
			  public void onStartTrackingTouch(SeekBar p1){
				}

			  @Override
			  public void onStopTrackingTouch(SeekBar p1){
				}
			});
	  }

    public Handler handler = new Handler() {
        @Override
        public void handleMessage(Message msg){
            switch(msg.what){
                case NEXTFRAME:
				  imageView.setImageBitmap(bms[i]);
				  break;
                case UNZIP:
				  unzipLinearLayout.setVisibility(View.VISIBLE);
				  break;
                case UNZIPIMAGEPROGRESS:
				  unzipProgress.setProgress(msg.arg1);
				  break;
                case UNZIPSUCCESS:
				  unzipLinearLayout.setVisibility(View.GONE);
				  break;
                case LOADING:
				  loadLinearLayout.setVisibility(View.VISIBLE);
				  break;
                case LOADIMAGEPROGRESS:
				  loadProgress.setProgress(msg.arg1);
				  break;
                case LOADED:
				  loadLinearLayout.setVisibility(View.GONE);
				  imageView.setVisibility(View.VISIBLE);
				  seekbarlinearLayout.setVisibility(View.VISIBLE);
				  break;
                case STARTMAKEGIF:
				  gifLinearLayout.setVisibility(View.VISIBLE);
				  imageView.setVisibility(View.GONE);
				  seekbarlinearLayout.setVisibility(View.GONE);
				  break;
                case GIFSUCCESS:
				  gifLinearLayout.setVisibility(View.GONE);
				  break;
			  }
		  }
	  };

    private void loadBitmap(){
        messageLoading();
        loadImg=new threadLoadBitmap();
        loadImg.start();
	  }

    private void playBitmap(){
        messageLoaded();
        playimg=new threadPlayImage();
        playimg.start();
	  }

    private class threadPlayImage extends Thread{
        public void run(){
            while(true){
                if(makingGIf){
                    break;
				  }
                messageNextFrame();
                try{
                    Thread.sleep(seekBar.getProgress());
				  }catch(InterruptedException e){
				  }
                if(i==bms.length-1){
                    i=0;
				  }else{
                    i++;
				  }
			  }
		  }
	  }

    private class threadLoadBitmap extends Thread{
        @Override
        public void run(){
            try{
                BitmapFactory.Options options = new BitmapFactory.Options();
                if(MainActivity.instence.sharedPreference.getValue(Data.preferenceKeys.gifScale)==null||MainActivity.instence.sharedPreference.getValue(Data.preferenceKeys.gifScale).equals("")){
                    MainActivity.instence.sharedPreference.putValue(Data.preferenceKeys.gifScale,"1");
				  }
                options.inSampleSize=Integer.parseInt(MainActivity.instence.sharedPreference.getValue(Data.preferenceKeys.gifScale));
                messageLoading(0);
                bms[0]=BitmapFactory.decodeFile(frameFileFolder+File.separator+filesName[0],options);
                gifHeight=bms[0].getHeight();
                gifWidth=bms[0].getWidth();
                for(int j = 1; j<filesName.length; j++){
                    messageLoading(j);
                    bms[j]=BitmapFactory.decodeFile(frameFileFolder+File.separator+filesName[j],options);
				  }
			  }catch(OutOfMemoryError e){
			  }
            loadfinish=true;
            playBitmap();
		  }
	  }

    public boolean onCreateOptionsMenu(Menu menu){
        menu.add(0,0,0,"生成GIF");
        return super.onCreateOptionsMenu(menu);
	  }

    public boolean onOptionsItemSelected(MenuItem item){
        switch(item.getItemId()){
            case 0: 
			  LayoutInflater inflater = LayoutInflater.from(playLayout.this);
			  LinearLayout ll = (LinearLayout) inflater.inflate(R.layout.set_gif,null);
			  final EditText h = (EditText) ll.findViewById(R.id.set_gif_edittext_height);
			  final EditText w = (EditText) ll.findViewById(R.id.set_gif_edittext_width);
			  final EditText d = (EditText) ll.findViewById(R.id.set_gif_edittext_delay);
			  final EditText q = (EditText) ll.findViewById(R.id.set_gif_edittext_quality);
			  h.setText(String.valueOf(gifHeight));
			  w.setText(String.valueOf(gifWidth));
			  d.setText(String.valueOf(gifDelay));
			  q.setText(String.valueOf(gifQuality));
			  new AlertDialog.Builder(playLayout.this)
				.setTitle("输入参数")
				.setIcon(android.R.drawable.ic_dialog_info)
				.setView(ll)
				.setPositiveButton("确定",new DialogInterface.OnClickListener() {
					@Override
					public void onClick(DialogInterface p1,int p2){
						messageStartMakeGif();
						Thread makeGif = new gifThread(
						  Integer.parseInt(h.getText().toString()),
						  Integer.parseInt(w.getText().toString()),
						  Integer.parseInt(d.getText().toString()),
						  Integer.parseInt(q.getText().toString()));
						makeGif.start();
						makingGIf=true;
					  }
				  }).setNegativeButton("取消",null).show();
			  break;
		  }

        return false;
	  }

    private class unzipThread extends Thread{
        File zipFile;
        String zipName;
        int filesCountNow = 0;
        int filesCount = 0;

        public unzipThread(File zipFile,String zipName){
            this.zipFile=zipFile;
            this.zipName=zipName;
		  }

        @Override
        public void run(){
            try{
                byte[] buffer = new byte[1024];
                frameFileFolder=new File(PixivDownloadMain.tmpFolder+zipName);
                if(!frameFileFolder.exists()){
                    frameFileFolder.mkdirs();
				  }
                File nomedia = new File(frameFileFolder.getParent()+File.separator+".nomedia");
                if(!nomedia.exists()){
                    try{
                        nomedia.createNewFile();
					  }catch(IOException e){
                        e.printStackTrace();
					  }
				  }
                messageUnzipStart();
                filesCount=countFilesInZip(zipFile);
                ZipInputStream zis = new ZipInputStream(new FileInputStream(zipFile));
                ZipEntry ze = zis.getNextEntry();
                while(ze!=null){
                    messageUnzipping(filesCountNow*100/filesCount);
                    filesCountNow++;
                    String fileName = ze.getName();
                    File frameFile = new File(frameFileFolder.getAbsolutePath()+File.separator+fileName);
                    if(frameFile.exists()){
                        ze=zis.getNextEntry();
					  }
                    FileOutputStream nfos = new FileOutputStream(frameFile);
                    int len;
                    while((len=zis.read(buffer))>0){
                        nfos.write(buffer,0,len);
					  }
                    nfos.close();
                    ze=zis.getNextEntry();
				  }
                messageUnzipSuccess();
                filesName=frameFileFolder.list();
                Arrays.sort(filesName);
                bms=new Bitmap[filesName.length];
                loadBitmap();
			  }catch(Exception e){
                Log.e(getString(R.string.app_name),e.toString());
			  }
		  }
	  }

    private void messageLoaded(){
        Message m = new Message();
        m.what=LOADED;
        handler.sendMessage(m);
	  }

    private void messageLoading(){
        Message m = new Message();
        m.what=LOADING;
        handler.sendMessage(m);
	  }

    private void messageNextFrame(){
        Message m = new Message();
        m.what=NEXTFRAME;
        handler.sendMessage(m);
	  }

    private void messageStartMakeGif(){
        Message m = new Message();
        m.what=STARTMAKEGIF;
        handler.sendMessage(m);
	  }

    private void messageMakeGifSuccess(){
        Message m = new Message();
        m.what=GIFSUCCESS;
        handler.sendMessage(m);
	  }

    private void messageUnzipStart(){
        Message m = new Message();
        m.what=UNZIP;
        handler.sendMessage(m);
	  }

    private void messageUnzipSuccess(){
        Message m = new Message();
        m.what=UNZIPSUCCESS;
        handler.sendMessage(m);
	  }

    private void messageUnzipping(int progress){
        Message m = new Message();
        m.what=UNZIPIMAGEPROGRESS;
        m.arg1=progress;
        handler.sendMessage(m);
	  }

    private void messageLoading(int progress){
        Message m = new Message();
        m.what=LOADIMAGEPROGRESS;
        m.arg1=progress;
        handler.sendMessage(m);
	  }

    private class gifThread extends Thread{
        int h;
        int w;
        int d;
        int q;

        public gifThread(int h,int w,int d,int q){
            this.h=h;
            this.w=w;
            this.d=d;
            this.q=q;
		  }

        @Override
        public void run(){
            if(MainActivity.instence.sharedPreference.getBoolean(Data.preferenceKeys.useJava)){
                createGifJava(fileName,d);
			  }else{
                createGifNative(fileName,w,h,d,q);
			  }
		  }
	  }

    @Override
    public boolean onKeyDown(int keyCode,KeyEvent event){
        if(keyCode==KeyEvent.KEYCODE_BACK){
            if(makingGIf){
                Toast.makeText(this,"请等待gif制作完成",Toast.LENGTH_SHORT).show();
                return true;
			  }
            if(!loadfinish){
                return true;
			  }
            try{
                unzip.interrupt();
                playimg.interrupt();
                loadImg.interrupt();
                Bitmap b = null;
                for(int j = 0; j<filesName.length; j++){
                    bms[j]=b;
				  }
                System.gc();
			  }catch(Exception e){

			  }
		  }
        return super.onKeyDown(keyCode,event);
	  }

    @Override
    protected void onDestroy(){
        super.onDestroy();
        if(MainActivity.instence.sharedPreference.getBoolean(Data.preferenceKeys.cleanTmpOnStopWatch)){
            File[] fs = frameFileFolder.listFiles();
            for(File f : fs){
                f.delete();
			  }
            frameFileFolder.delete();
		  }
	  }

    private void createGifJava(String file_name,int delay){
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        AnimatedGifEncoder localAnimatedGifEncoder = new AnimatedGifEncoder();
        localAnimatedGifEncoder.start(baos);//start
        localAnimatedGifEncoder.setRepeat(0);//设置生成gif的开始播放时间。0为立即开始播放
        localAnimatedGifEncoder.setDelay(delay);

        int count = bms.length;
        for(int i = 0; i<count; i++){
            localAnimatedGifEncoder.addFrame(bms[i]);
            playLayout.gifProgress.setProgress(i*100/count);
		  }
        localAnimatedGifEncoder.finish(); 
        final String path = MainActivity.instence.getPixivGifPath(file_name);
        try{
            FileOutputStream fos = new FileOutputStream(path);
            baos.writeTo(fos);
            baos.flush();
            fos.flush();
            baos.close();
            fos.close();
		  }catch(IOException e){
            e.printStackTrace();
		  }
        registImage(path);
        loadBitmap();
        makingGIf=false;
        messageMakeGifSuccess();
	  }

    private void createGifNative(String file_name,int w,int h,int d,int q){
        String filePath = MainActivity.instence.getGifPath();
        GifEncoder gifEncoder = new GifEncoder();
        gifEncoder.setDither(false);
        try{
            gifEncoder.init(w,h,filePath,GifEncoder.EncodingType.ENCODING_TYPE_NORMAL_LOW_MEMORY);
		  }catch(FileNotFoundException e){
            e.printStackTrace();
            return;
		  }

		for(int i=0;i<bms.length;i++){
			gifEncoder.encodeFrame(bms[i],d);
			playLayout.gifProgress.setProgress(i*100/bms.length);

		  }
        gifEncoder.close();
	  }

    private void registImage(String path){
        try{
            File f = new File(path);
            MediaStore.Images.Media.insertImage(playLayout.this.getContentResolver(),f.getAbsolutePath(),f.getName(),null);
		  }catch(FileNotFoundException e){
            e.printStackTrace();
		  }
        // 最后通知图库更新
        playLayout.this.sendBroadcast(new Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE,Uri.parse("file://"+path)));
	  }

    private int countFilesInZip(File zipFile){
        int filesCount = 0;
        try{
            ZipInputStream zis = new ZipInputStream(new FileInputStream(zipFile));
            ZipEntry ze = zis.getNextEntry();
            while(ze!=null){
                filesCount++;
                ze=zis.getNextEntry();
			  }
		  }catch(IOException e){
            e.printStackTrace();
		  }
        return filesCount;
	  }
  }
