package com.meng.picTools.pixivGifDownloader;

import android.app.*;
import android.content.*;
import android.os.*;
import android.view.*;
import android.widget.*;
import com.meng.picTools.*;
import com.meng.picTools.mengViews.*;
import java.io.*;
import java.util.*;

public class PixivDownloadMain extends Fragment{

    private EditText editTextURL;
    private ListView downloadedList;
    private LinearLayout taskLinearLayout;

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
        downloadedList=(ListView) v.findViewById(R.id.saved_files_list);
        editTextURL=(EditText) v.findViewById(R.id.pixiv_download_main_edittext_url);
        Button btnStart = (Button) v.findViewById(R.id.pixiv_download_main_button_start);
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
				}
			});
	  }
  }
