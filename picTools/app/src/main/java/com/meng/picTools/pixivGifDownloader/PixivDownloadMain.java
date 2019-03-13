package com.meng.picTools.pixivGifDownloader;

import android.app.*;
import android.content.*;
import android.os.*;
import android.util.*;
import android.view.*;
import android.widget.*;
import com.google.gson.*;
import com.meng.picTools.*;
import com.meng.picTools.qrtools.*;
import java.io.*;
import java.net.*;
import java.util.*;

public class PixivDownloadMain extends Fragment{

    public static String tmpFolder;
    private String[][] fileData = new String[512][2];
    private String[] filesName;
    private int fileDataFlag = 0;
    private EditText etUrl;
    private Button btnStart;
    private Thread tFirstThread;
    private ListView downloadingList;
    private final int TOAST = 1;
    private final int UPDATEPROGRESS = 5;
    private final int SETADAPTER = 6;
    private final String keyCookieValue = "cookievalue";

    private ListView downloadedList;

    @Override
    public void onViewCreated(View view,Bundle savedInstanceState){
        super.onViewCreated(view,savedInstanceState);
        init(view);
	  }

    @Override
    public View onCreateView(LayoutInflater inflater,ViewGroup container,Bundle savedInstanceState){
        return inflater.inflate(R.layout.pixiv_download_main,container,false);
	  }

    Handler handler = new Handler() {
        @Override
        public void handleMessage(Message msg){
            switch(msg.what){
                case TOAST:
				  Toast.makeText(getActivity(),msg.obj.toString(),Toast.LENGTH_SHORT).show();
				  break;
                case UPDATEPROGRESS:
				  try{
					  LinearLayout ll = (LinearLayout) downloadingList.getChildAt(msg.arg1);
					  int progre = msg.arg2;
					  ((TextView) ll.findViewById(R.id.main_list_item_textview_statu)).setText(progre==100? getString(R.string.download_success) :getString(R.string.downloading));
					  ((TextView) ll.findViewById(R.id.main_list_item_textview_filename)).setText(msg.obj.toString());
					  ((ProgressBar) ll.findViewById(R.id.main_list_item_progressbar)).setProgress(progre);
					  if(((TextView) ll.findViewById(R.id.main_list_item_textview_statu)).getText().equals(R.string.download_success)){
						  String[] downloadedFilesName = new File(MainActivity.instence.getPixivZipPath("")).list();
						  Arrays.sort(downloadedFilesName);
						  downloadedList.setAdapter(new ArrayAdapter<String>(getActivity(),android.R.layout.simple_list_item_1,downloadedFilesName));
                        }
                    }catch(Exception e){
					  Log.e(getString(R.string.app_name),e.toString());
                    }
				  break;
                case SETADAPTER:
				  ArrayAdapter adapterDownloadListAdapter = new listAdapter(getActivity(),filesName);
				  downloadingList.setAdapter(adapterDownloadListAdapter);
				  break;
			  }
		  }
	  };

    private void init(View v){
        TabHost tabHost = (TabHost) v.findViewById(R.id.pixiv_download_main_tabhost);
        tabHost.setup();
        tabHost.addTab(tabHost.newTabSpec("one").setIndicator("正在下载").setContent(R.id.pixiv_download_main_downloadlist));
        tabHost.addTab(tabHost.newTabSpec("two").setIndicator("已下载").setContent(R.id.pixiv_download_main_downloaded));
        downloadedList=(ListView) v.findViewById(R.id.saved_files_list);
        etUrl=(EditText) v.findViewById(R.id.et);
        btnStart=(Button) v.findViewById(R.id.start);
        downloadingList=(ListView) v.findViewById(R.id.main_listview);
        if(MainActivity.instence.sharedPreference.getValue(Data.preferenceKeys.tmpPath)==null||MainActivity.instence.sharedPreference.getValue(Data.preferenceKeys.tmpPath).equals("")){
            MainActivity.instence.sharedPreference.putValue(Data.preferenceKeys.tmpPath,Environment.getExternalStorageDirectory().getPath()+"/tmp/");
		  }
        tmpFolder=MainActivity.instence.sharedPreference.getValue(Data.preferenceKeys.tmpPath);
        File zipFod = new File(MainActivity.instence.getPixivZipPath(""));

		String[] filesName = zipFod.list();
		Arrays.sort(filesName);
		ListAdapter adapter = new ArrayAdapter<String>(getActivity(),android.R.layout.simple_list_item_1,filesName);
		downloadedList.setAdapter(adapter);
		downloadedList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
			  @Override
			  public void onItemClick(AdapterView<?> adapterView,View view,int i,long l){
				  Intent intent = new Intent(getActivity(),playLayout.class);
				  intent.putExtra(Data.intentKeys.fileName,MainActivity.instence.getPixivZipPath(adapterView.getItemAtPosition(i).toString()));
				  startActivity(intent);
                }
            });

        downloadingList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
			  @Override
			  public void onItemClick(AdapterView<?> adapterView,View view,int i,long l){
				  LinearLayout ll = (LinearLayout) view;
				  TextView tv = (TextView) ll.findViewById(R.id.main_list_item_textview_filename);
				  ProgressBar pb = (ProgressBar) ll.findViewById(R.id.main_list_item_progressbar);
				  if(pb.getProgress()==100){
					  Intent intent = new Intent(getActivity(),playLayout.class);
					  intent.putExtra(Data.intentKeys.fileName,MainActivity.instence.getPixivZipPath(tv.getText().toString()));
					  startActivity(intent);
					}else{
					  Toast.makeText(getActivity(),"下载完成后才可以查看",Toast.LENGTH_SHORT).show();
					}
				}
			});
        btnStart.setOnClickListener(new View.OnClickListener() {
			  @Override
			  public void onClick(View view){
				  tFirstThread=new DownloadZip(Integer.parseInt(etUrl.getText().toString()));
				  tFirstThread.start();
				}
			});
	  }

	public class DownloadZip extends Thread{
		int id=0;
		public DownloadZip(int pixivId){
            id=pixivId;
		  }

		@Override
		public void run(){
			String zipUrl=getZipUrl(id);
			download(zipUrl);
		  }

		private String getZipUrl(int picId){
			try{
				URL u = new URL(toLink(String.valueOf(picId)));
				HttpURLConnection connection = (HttpURLConnection) u.openConnection();
				connection.setRequestMethod("GET");
				connection.setConnectTimeout(10000);
				connection.setReadTimeout(10000);
				connection.setRequestProperty("cookie",MainActivity.instence.sharedPreference.getValue(keyCookieValue));
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
				String deletedUrl = MainActivity.instence.sharedPreference.getBoolean(Data.preferenceKeys.downloadBigPicture)? zjb.body.originalSrc :zjb.body.src;
				return deletedUrl;
			  }catch(FileNotFoundException e){
				sendToast(getString(R.string.maybe_need_login));
				startActivity(new Intent(getActivity(),login.class));
			  }catch(Exception e){
				log.e(e);
			  }
			return "";
		  }
		private void download(String zipUrl){
			try{
                URL url = new URL(zipUrl);
                String expandName = zipUrl.substring(zipUrl.lastIndexOf(".")+1,zipUrl.length()).toLowerCase();
                String fileName = zipUrl.substring(zipUrl.lastIndexOf("/")+1,zipUrl.lastIndexOf("."));
                File file = new File(MainActivity.instence.getPixivZipPath(fileName+"."+expandName));
                if(file.exists()){
                    sendToast(getString(R.string.file_exist)+file.getName());
				  }else{
                    HttpURLConnection urlConn = (HttpURLConnection) url.openConnection();
                    urlConn.setRequestProperty("User-Agent","Mozilla/5.0 (Windows NT 6.1; WOW64; rv:28.0) Gecko/20100101 Firefox/28.0");
                    urlConn.setRequestProperty("cookie",MainActivity.instence.sharedPreference.getValue(keyCookieValue));
                    urlConn.setRequestProperty("Referer","https://www.pixiv.net/member_illust.php?mode=medium&illust_id="+etUrl.getText().toString());
                    fileData[fileDataFlag][0]=file.getAbsolutePath();
                    fileData[fileDataFlag][1]=String.valueOf(urlConn.getContentLength());
                    if(downloadingList.getAdapter()==null){
                        filesName=new String[]{
							file.getName()
						  };
					  }else{
                        filesName=addData(filesName,file.getName());
					  }
                    Message m = new Message();
                    m.what=SETADAPTER;
                    handler.sendMessage(m);
                    fileDataFlag++;
                    InputStream is = urlConn.getInputStream();
                    if(is!=null){
                        FileOutputStream fos = new FileOutputStream(file);
                        byte buf[] = new byte[4096];
                        while(true){
                            int numread = is.read(buf);
                            if(numread<=0){
                                break;
							  }else{
                                fos.write(buf,0,numread);
							  }
						  }
					  }
                    is.close();
                    urlConn.disconnect();
				  }
			  }catch(IOException e){
                sendToast(e.toString());
                Log.e(getString(R.string.app_name),e.toString());
			  }
		  }

	  }


    private void sendToast(String message){
        Message m = new Message();
        m.what=TOAST;
        m.obj=message;
        handler.sendMessage(m);
	  }

    private class listAdapter extends ArrayAdapter<String>{
        public listAdapter(Context c,String[] s){
            super(c,R.layout.main_list_item,s);
		  }

        @Override
        public View getView(int position,View convertView,ViewGroup parent){
            LayoutInflater inf = LayoutInflater.from(getContext());
            View view = inf.inflate(R.layout.main_list_item,parent,false);
            TextView tv = (TextView) view.findViewById(R.id.main_list_item_textview_filename);
            tv.setText(filesName[position]);
            Thread update = new updateThread(position);
            update.start();
            return view;
		  }
	  }

    private class updateThread extends Thread{
        int position;
        String fileName;
        int fileSize;
        File f;

        updateThread(int position){
            this.position=position;
            fileName=fileData[position][0];
            fileSize=Integer.parseInt(fileData[position][1]);
            f=new File(fileName);
		  }

        @Override
        public void run(){
            while(f.length()<=fileSize){
                Message m = new Message();
                m.what=UPDATEPROGRESS;
                m.arg1=position;
                m.arg2=(int) (f.length()*100/fileSize);
                m.obj=f.getName();
                handler.sendMessage(m);
                try{
                    sleep(200);
				  }catch(InterruptedException e){
                    e.printStackTrace();
				  }
			  }
		  }
	  }

    private String[] addData(String[] s,String text){
        String[] tmp = s;
        s=new String[tmp.length+1];
        System.arraycopy(tmp,0,s,0,tmp.length);
        s[s.length-1]=text;
        return s;
	  }

    private String toLink(String id){
        return "https://www.pixiv.net/ajax/illust/"+id+"/ugoira_meta";
	  }

  }
