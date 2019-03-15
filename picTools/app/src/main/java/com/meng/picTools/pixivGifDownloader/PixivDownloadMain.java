package com.meng.picTools.pixivGifDownloader;

import android.app.*;
import android.content.*;
import android.os.*;
import android.view.*;
import android.view.View.*;
import android.widget.*;
import com.google.gson.*;
import com.meng.picTools.*;
import com.meng.picTools.mengViews.*;
import com.meng.picTools.qrtools.*;
import java.io.*;
import java.util.*;
import android.widget.AdapterView.*;

public class PixivDownloadMain extends Fragment{

    private EditText editTextURL;
    private ListView downloadedList;
	private ListView likeList;
    private LinearLayout taskLinearLayout;
	private LikeJavaBean likeJavaBean;
	private Gson gson;

    @Override
    public void onViewCreated(View view,Bundle savedInstanceState){
        super.onViewCreated(view,savedInstanceState);
        init(view);
	  }

    @Override
    public View onCreateView(LayoutInflater inflater,ViewGroup container,Bundle savedInstanceState){
        return inflater.inflate(R.layout.pixiv_download_main,container,false);
	  }

    private void init(View v){
        TabHost tabHost = (TabHost) v.findViewById(R.id.pixiv_download_main_tabhost);
        tabHost.setup();
        tabHost.addTab(tabHost.newTabSpec("one").setIndicator("正在下载").setContent(R.id.pixiv_download_main_downloading));
        tabHost.addTab(tabHost.newTabSpec("two").setIndicator("已下载").setContent(R.id.pixiv_download_main_downloaded));
		tabHost.addTab(tabHost.newTabSpec("two").setIndicator("收藏").setContent(R.id.pixiv_download_main_like));
		gson=new Gson();
        downloadedList=(ListView) v.findViewById(R.id.saved_files_list);
		likeList=(ListView)v.findViewById(R.id.like_files_list);
        editTextURL=(EditText) v.findViewById(R.id.pixiv_download_main_edittext_url);
        Button btnStart = (Button) v.findViewById(R.id.pixiv_download_main_button_start);
		Button preStart=(Button)v.findViewById(R.id.pixiv_download_main_button_pre_start);
        taskLinearLayout=(LinearLayout) v.findViewById(R.id.pixiv_download_main_downloadlist_task);
        String[] filesName = new File(MainActivity.instence.getPixivZipPath("")).list();
        Arrays.sort(filesName);
        ListAdapter adapter = new ArrayAdapter<String>(getActivity(),android.R.layout.simple_list_item_1,filesName);
        downloadedList.setAdapter(adapter);
        downloadedList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
			  @Override
			  public void onItemClick(AdapterView<?> adapterView,View view,int i,long l){
				  Intent intent = new Intent(getActivity(),playLayout.class);
				  intent.putExtra(Data.intentKeys.fileName,MainActivity.instence.getPixivZipPath(adapterView.getItemAtPosition(i).toString()));
				  //  startActivity(intent);
				}
			});

        btnStart.setOnClickListener(new View.OnClickListener() {
			  @Override
			  public void onClick(View view){
				  MengProgressBar mpb = new MengProgressBar(getActivity(),downloadedList);
				  taskLinearLayout.addView(mpb);
				  mpb.startDownload(editTextURL.getText().toString());
				  editTextURL.setText("");
				}
			});

		preStart.setOnClickListener(new OnClickListener(){
			  @Override
			  public void onClick(View p1){
				  File jsonFile=new File(MainActivity.instence.getPreDownloadJsonPath());
				  if(jsonFile.exists()){
					  likeJavaBean=gson.fromJson(loadFromSDFile(jsonFile),LikeJavaBean.class);			
					}else{
					  likeJavaBean=new LikeJavaBean();
					  likeJavaBean.info=new ArrayList<String>();
					}
				  likeJavaBean.info.add(editTextURL.getText().toString());
				  stringTxt(gson.toJson(likeJavaBean));
				  likeList.setAdapter(new ArrayAdapter<String>(getActivity(),android.R.layout.simple_list_item_1,likeJavaBean.info));  
				  editTextURL.setText("");
				}
			});

		likeList.setOnItemClickListener(new OnItemClickListener(){

			  @Override
			  public void onItemClick(final AdapterView<?> p1,View p2,final int p3,long p4){

				  new AlertDialog.Builder(getActivity())
					.setTitle("开始下载？")
					.setPositiveButton("确定",new DialogInterface.OnClickListener() {
						@Override
						public void onClick(DialogInterface p11,int p2){

							editTextURL.setText(p1.getItemAtPosition(p3).toString());

							MengProgressBar mpb = new MengProgressBar(getActivity(),downloadedList);
							taskLinearLayout.addView(mpb);
							mpb.startDownload(editTextURL.getText().toString());
							editTextURL.setText("");

						  }
					  }).setNegativeButton("取消",null).show();

				}
			});

		likeList.setOnItemLongClickListener(new OnItemLongClickListener(){

			  @Override
			  public boolean onItemLongClick(AdapterView<?> p1,View p2,final int p3,long p4){
				  new AlertDialog.Builder(getActivity())
					.setTitle("确定删除吗")
					.setPositiveButton("确定",new DialogInterface.OnClickListener() {
						@Override
						public void onClick(DialogInterface p1,int p2){
							likeJavaBean.info.remove(p3);
							stringTxt(gson.toJson(likeJavaBean));
							likeList.setAdapter(new ArrayAdapter<String>(getActivity(),android.R.layout.simple_list_item_1,likeJavaBean.info));  
						  }
					  }).setNegativeButton("取消",null).show();

				  return true;
				}
			});

		File preDownloadJson=new File(MainActivity.instence.getPreDownloadJsonPath());
		if(preDownloadJson.exists()){
			likeJavaBean=gson.fromJson(loadFromSDFile(preDownloadJson),LikeJavaBean.class);
			likeList.setAdapter(new ArrayAdapter<String>(getActivity(),android.R.layout.simple_list_item_1,likeJavaBean.info));
		  }
	  }

	private String loadFromSDFile(File f){
        String result=null;
        try{
            int length=(int)f.length();
            byte[] buff=new byte[length];
            FileInputStream fin=new FileInputStream(f);
            fin.read(buff);
            fin.close();
            result=new String(buff,"UTF-8");
		  }catch(Exception e){
            e.printStackTrace();
            log.t(e.toString());
		  }
        return result;
	  }

	public void stringTxt(String str){
		try{
			FileWriter fw = new FileWriter(MainActivity.instence.getPreDownloadJsonPath());//SD卡中的路径
			fw.flush();
			fw.write(str);
			fw.close();
		  }catch(Exception e){
			e.printStackTrace();
		  }
	  }

  }
