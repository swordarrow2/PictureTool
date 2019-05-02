package com.meng.picTools.pixivPictureDownloader;

import android.app.*;
import android.content.*;
import android.os.*;
import android.view.*;
import android.widget.*;
import android.widget.AdapterView.*;

import com.google.gson.*;
import com.meng.picTools.*;
import com.meng.picTools.javaBean.AnimPicJavaBean;
import com.meng.picTools.javaBean.LikeJavaBean;
import com.meng.picTools.javaBean.PictureInfoJavaBean;
import com.meng.picTools.javaBean.StaticPicJavaBean;
import com.meng.picTools.mengViews.*;
import com.meng.picTools.qrtools.*;
import com.meng.picTools.qrtools.lib.SharedPreferenceHelper;

import java.io.*;
import java.net.*;
import java.util.*;
import java.util.regex.*;

import android.view.View.OnClickListener;
import org.jsoup.*;

public class PixivDownloadMain extends Fragment {

    private EditText editTextURL;
    private ListView downloadedList;
    private ListView likeList;
    private LinearLayout taskLinearLayout;
    private LikeJavaBean likeJavaBean;
    private Gson gson;

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        init(view);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.pixiv_download_main, container, false);
    }

    private void init(View v) {
        TabHost tabHost = (TabHost) v.findViewById(R.id.pixiv_download_main_tabhost);
        tabHost.setup();
        tabHost.addTab(tabHost.newTabSpec("one").setIndicator("正在下载").setContent(R.id.pixiv_download_main_downloading));
        tabHost.addTab(tabHost.newTabSpec("two").setIndicator("已下载").setContent(R.id.pixiv_download_main_downloaded));
        tabHost.addTab(tabHost.newTabSpec("two").setIndicator("收藏").setContent(R.id.pixiv_download_main_like));
        gson = new Gson();
        downloadedList = (ListView) v.findViewById(R.id.saved_files_list);
        likeList = (ListView) v.findViewById(R.id.like_files_list);
        editTextURL = (EditText) v.findViewById(R.id.pixiv_download_main_edittext_url);
        Button btnStart = (Button) v.findViewById(R.id.pixiv_download_main_button_start);
        Button preStart = (Button) v.findViewById(R.id.pixiv_download_main_button_pre_start);
        taskLinearLayout = (LinearLayout) v.findViewById(R.id.pixiv_download_main_downloadlist_task);
        String[] filesName = new File(MainActivity.instence.getPixivZipPath("")).list();
        Arrays.sort(filesName);
        ListAdapter adapter = new ArrayAdapter<String>(getActivity(), android.R.layout.simple_list_item_1, filesName);
        downloadedList.setAdapter(adapter);
		
		new Thread(new Runnable(){

			  @Override
			  public void run(){
				  String s=readStringFromNetwork("https://www.pixiv.net/ajax/user/544479/profile/all",SharedPreferenceHelper.getValue(Data.preferenceKeys.keyCookieValue),"https://www.pixiv.net/member.php?id=544479");
				  LogTool.i(s);
				}
			}).start();

        btnStart.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                final String text = editTextURL.getText().toString();
                editTextURL.setText("");
                Toast.makeText(getActivity(), "正在读取信息", Toast.LENGTH_SHORT).show();
                createDownloadTask(text);
            }
        });

        preStart.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View p1) {
                File jsonFile = new File(MainActivity.instence.getPreDownloadJsonPath());
                if (jsonFile.exists()) {
                    likeJavaBean = gson.fromJson(readStringFromFile(jsonFile), LikeJavaBean.class);
                } else {
                    likeJavaBean = new LikeJavaBean();
                    likeJavaBean.info = new ArrayList<String>();
                }
                likeJavaBean.info.add(editTextURL.getText().toString());
                writeStringToFile(gson.toJson(likeJavaBean));
                likeList.setAdapter(new ArrayAdapter<String>(getActivity(), android.R.layout.simple_list_item_1, likeJavaBean.info));
                editTextURL.setText("");
            }
        });

        likeList.setOnItemClickListener(new OnItemClickListener() {

            @Override
            public void onItemClick(final AdapterView<?> p1, View p2, final int p3, long p4) {

                new AlertDialog.Builder(getActivity())
                        .setTitle("开始下载？")
                        .setPositiveButton("确定", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface p11, int p2) {
                                createDownloadTask(p1.getItemAtPosition(p3).toString());
                            }
                        }).setNegativeButton("取消", null).show();

            }
        });

        likeList.setOnItemLongClickListener(new OnItemLongClickListener() {

            @Override
            public boolean onItemLongClick(AdapterView<?> p1, View p2, final int p3, long p4) {
                new AlertDialog.Builder(getActivity())
                        .setTitle("确定删除吗")
                        .setPositiveButton("确定", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface p1, int p2) {
                                likeJavaBean.info.remove(p3);
                                writeStringToFile(gson.toJson(likeJavaBean));
                                likeList.setAdapter(new ArrayAdapter<String>(getActivity(), android.R.layout.simple_list_item_1, likeJavaBean.info));
                            }
                        }).setNegativeButton("取消", null).show();

                return true;
            }
        });

        File preDownloadJson = new File(MainActivity.instence.getPreDownloadJsonPath());
        if (preDownloadJson.exists()) {
            likeJavaBean = gson.fromJson(readStringFromFile(preDownloadJson), LikeJavaBean.class);
            likeList.setAdapter(new ArrayAdapter<String>(getActivity(), android.R.layout.simple_list_item_1, likeJavaBean.info));
        }
    }

    private void createDownloadTask(final String url) {
        Toast.makeText(getActivity(), "正在读取信息", Toast.LENGTH_SHORT).show();
        new Thread(new Runnable() {

            @Override
            public void run() {
                final PictureInfoJavaBean pijb = getPicInfo(getPixivId(url));
                getActivity().runOnUiThread(new Runnable() {

                                                @Override
                                                public void run() {
                                                    if (pijb.isAnimPicture) {
                                                        String url = SharedPreferenceHelper.getBoolean(Data.preferenceKeys.downloadBigPicture) ? pijb.animPicJavaBean.body.originalSrc : pijb.animPicJavaBean.body.src;
                                                        taskLinearLayout.addView(new MengProgressBar(getActivity(), downloadedList, pijb, url));
                                                    } else {
                                                        for (int i = 0; i < pijb.staticPicJavaBean.body.size(); ++i) {
                                                            String url = SharedPreferenceHelper.getBoolean(Data.preferenceKeys.downloadBigPicture) ? pijb.staticPicJavaBean.body.get(i).urls.original : pijb.staticPicJavaBean.body.get(i).urls.regular;
                                                            taskLinearLayout.addView(new MengProgressBar(getActivity(), downloadedList, pijb, url));
                                                        }
                                                    }
                                                }
                                            }
                );
            }
        }).start();
    }

    private String readStringFromFile(File f) {
        String result = null;
        try {
            int length = (int) f.length();
            byte[] buff = new byte[length];
            FileInputStream fin = new FileInputStream(f);
            fin.read(buff);
            fin.close();
            result = new String(buff, "UTF-8");
        } catch (Exception e) {
            e.printStackTrace();
            LogTool.t(e.toString());
        }
        return result;
    }

    public void writeStringToFile(String str) {
        try {
            FileWriter fw = new FileWriter(MainActivity.instence.getPreDownloadJsonPath());//SD卡中的路径
            fw.flush();
            fw.write(str);
            fw.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private PictureInfoJavaBean getPicInfo(String picId) {
        PictureInfoJavaBean pijb = new PictureInfoJavaBean();
        try {
            pijb.animPicJavaBean = getDynamicPictureJsonAddress(picId);
            if (pijb.animPicJavaBean.error.equals("true")) {
                pijb.staticPicJavaBean = getStaticPictureJsonAddress(picId);
                pijb.isAnimPicture = false;
            }
        } catch (Exception e) {
            LogTool.t(getActivity().getString(R.string.maybe_need_login));
            getActivity().startActivity(new Intent(getActivity(), login.class));
        }
        return pijb;
    }

    public AnimPicJavaBean getDynamicPictureJsonAddress(String id) {
        String picJsonAddress = "https://www.pixiv.net/ajax/illust/" + id + "/ugoira_meta";
        return new Gson().fromJson(readStringFromNetwork(picJsonAddress,SharedPreferenceHelper.getValue(Data.preferenceKeys.keyCookieValue),null), AnimPicJavaBean.class);
    }

    public StaticPicJavaBean getStaticPictureJsonAddress(String id) {
        String picJsonAddress = "https://www.pixiv.net/ajax/illust/" + id + "/pages";
        return new Gson().fromJson(readStringFromNetwork(picJsonAddress,SharedPreferenceHelper.getValue(Data.preferenceKeys.keyCookieValue),null), StaticPicJavaBean.class);
    }
	
	public String readStringFromNetwork(String url,String cookie,String refer){
        Connection.Response response = null;
        Connection connection = null;
        try{
            connection=Jsoup.connect(url);
            if(cookie!=null){
                connection.cookies(cookieToMap(cookie));
			  }
			if(refer!=null){
                connection.referrer(refer);
              }
			connection.userAgent("Mozilla/5.0 (Windows NT 6.1; WOW64; rv:28.0) Gecko/20100101 Firefox/28.0");
            connection.ignoreContentType(true).method(Connection.Method.GET);
            response=connection.execute();
            if(response.statusCode()!=200){
                showToast(String.valueOf(response.statusCode()));
			  }
		  }catch(IOException e){
            e.printStackTrace();
		  }
        return response.body();
	  }
	
	public Map<String, String> cookieToMap(String value){
        Map<String, String> map = new HashMap<String, String>();
        String values[] = value.split("; ");
        for(String val : values){
            String vals[] = val.split("=");
            if(vals.length==2){
                map.put(vals[0],vals[1]);
			  }else if(vals.length==1){
                map.put(vals[0],"");
			  }
		  }
        return map;
	  }
	
	public void showToast(final String msg){
     getActivity().runOnUiThread(new Runnable() {

			  @Override
			  public void run(){
				  Toast.makeText(getActivity(),msg,Toast.LENGTH_LONG).show();
				}
			});
	  }
	    
    public String getPixivId(String str) {
        int pageIndex = str.indexOf("&page");
        if (pageIndex > 1) {
            str = str.substring(0, pageIndex);
        }
        String regEx = "[^0-9]";
        Pattern p = Pattern.compile(regEx);
        Matcher m = p.matcher(str);
        return m.replaceAll("").trim();
    }
}
