package com.meng.picTools.pixivPictureDownloader;

import android.app.*;
import android.content.*;
import android.graphics.*;
import android.os.*;
import android.support.v7.app.*;
import android.text.*;
import android.view.*;
import android.view.View.*;
import android.widget.*;
import android.widget.AdapterView.*;
import com.google.gson.*;
import com.google.gson.internal.*;
import com.meng.picTools.*;
import com.meng.picTools.helpers.*;
import com.meng.picTools.lib.MaterialDesign.*;
import com.meng.picTools.lib.javaBean.*;
import com.meng.picTools.lib.javaBean.allPics.*;
import com.meng.picTools.lib.mengViews.*;
import java.io.*;
import java.net.*;
import java.util.*;
import java.util.concurrent.*;
import java.util.regex.*;
import org.jsoup.*;

import android.support.v7.app.AlertDialog;
import android.view.View.OnClickListener;

public class PixivDownloadMain extends Fragment {

    private EditText editTextURL;
    private ListView downloadedList;
    private ListView likeList;
    private LinearLayout taskLinearLayout;
    private LikeJavaBean likeJavaBean;
    private CheckBox checkBoxIsUID;
    private Gson gson;
    public ExecutorService threadPool;
    private String title = "pids";
    private ImageView imageView;
    private String token = "";
    private FloatingActionMenu menuStar;
    private FloatingActionButton fabStartDownload;
    private FloatingActionButton fabAddMine;
    private FloatingActionButton fabAddPixiv;
	private ArrayAdapter likeAdapter;
    public enum Type {
        pid,
        uid
		}

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        init(view);
	  }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.pixiv_download_main, container, false);
	  }

    private void init(View view) {
		new Thread(new Runnable(){

			  @Override
			  public void run() {
				  getToken();
				  final Bitmap b=getPixivHead();
				  getActivity().runOnUiThread(new Runnable(){

						@Override
						public void run() {
							if (b == null)return;
							MainActivity.instence.pixivHead.setImageBitmap(b);
						  }
					  });
				}
			}).start();
        TabHost tabHost = (TabHost) view.findViewById(R.id.pixiv_download_main_tabhost);
        tabHost.setup();
		tabHost.addTab(tabHost.newTabSpec("zero").setIndicator("预览").setContent(R.id.pixiv_download_main_browser));
        tabHost.addTab(tabHost.newTabSpec("one").setIndicator("正在下载").setContent(R.id.pixiv_download_main_downloading));
        tabHost.addTab(tabHost.newTabSpec("two").setIndicator("已下载").setContent(R.id.pixiv_download_main_downloaded));
        tabHost.addTab(tabHost.newTabSpec("three").setIndicator("收藏").setContent(R.id.pixiv_download_main_like));
        gson = new Gson();
        fabStartDownload = (FloatingActionButton) view.findViewById(R.id.fab_start_download);
		fabAddMine = (FloatingActionButton) view.findViewById(R.id.fab_add_mine);
		fabAddPixiv = (FloatingActionButton) view.findViewById(R.id.fab_add_pixiv);
        imageView = (ImageView) view.findViewById(R.id.imageview);
        downloadedList = (ListView) view.findViewById(R.id.saved_files_list);
        likeList = (ListView) view.findViewById(R.id.like_files_list);
        menuStar = (FloatingActionMenu) view.findViewById(R.id.menu_star);
        editTextURL = (EditText) view.findViewById(R.id.pixiv_download_main_edittext_url);
        fabStartDownload = (FloatingActionButton) view.findViewById(R.id.fab_start_download);    
		fabAddPixiv.setOnClickListener(onClickListener);
		fabAddMine.setOnClickListener(onClickListener);
        taskLinearLayout = (LinearLayout) view.findViewById(R.id.pixiv_download_main_downloadlist_task);
        checkBoxIsUID = (CheckBox) view.findViewById(R.id.pixiv_download_main_checkbox_user);
        String[] filesName = FileHelper.getFolder(FileType.pixivZIP).list();
        Arrays.sort(filesName);
        downloadedList.setAdapter(new ArrayAdapter<>(getActivity(), android.R.layout.simple_list_item_1, filesName));
        likeList.setOnItemClickListener(new OnItemClickListener() {

			  @Override
			  public void onItemClick(final AdapterView<?> p1, View p2, final int p3, long p4) {

				  new AlertDialog.Builder(getActivity())
					.setTitle("开始下载？")
					.setPositiveButton("确定", new DialogInterface.OnClickListener() {
						@Override
						public void onClick(DialogInterface p11, int p2) {
							createDownloadTask(p1.getItemAtPosition(p3).toString());
							LogTool.t("正在读取信息");
							fabStartDownload.setShowProgressBackground(true);
							fabStartDownload.setIndeterminate(true);
						  }
					  }).setNegativeButton("取消", null).show();
				}
			});
        fabStartDownload.setOnClickListener(onClickListener);
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
							likeList.setAdapter(new ArrayAdapter<>(getActivity(), android.R.layout.simple_list_item_1, likeJavaBean.info));
						  }
					  }).setNegativeButton("取消", null).show();

				  return true;
				}
			});
		editTextURL.addTextChangedListener(textWatcher);
        menuStar.setAnimated(true);
        menuStar.hideMenuButton(false);
		menuStar.setClosedOnTouchOutside(true);
		menuStar.setIconAnimated(false);
        fabStartDownload.hide(false);
		new Handler().postDelayed(new Runnable() {
			  @Override
			  public void run() {
				  fabStartDownload.show(true);
				}
			}, 150);
		new Handler().postDelayed(new Runnable() {
			  @Override
			  public void run() {
				  menuStar.showMenuButton(true);
				}
			}, 300);
        File preDownloadJson = new File(FileHelper.getPreDownloadJsonPath());
        if (preDownloadJson.exists()) {
            likeJavaBean = gson.fromJson(readStringFromFile(preDownloadJson), LikeJavaBean.class);
			likeAdapter = new ArrayAdapter<>(getActivity(), android.R.layout.simple_list_item_1, likeJavaBean.info);
            likeList.setAdapter(likeAdapter);
		  }
        threadPool = Executors.newFixedThreadPool(Integer.parseInt(SharedPreferenceHelper.getValue("threads", "3")));
	  }

    OnClickListener onClickListener = new OnClickListener() {
        @Override
        public void onClick(View v) {
            switch (v.getId()) {
                case R.id.fab_add_mine:
				  File jsonFile = new File(FileHelper.getPreDownloadJsonPath());
				  if (jsonFile.exists()) {
					  likeJavaBean = gson.fromJson(readStringFromFile(jsonFile), LikeJavaBean.class);
                    } else {
					  likeJavaBean = new LikeJavaBean();
					  likeJavaBean.info = new ArrayList<>();
                    }
				  likeJavaBean.info.add(editTextURL.getText().toString());
				  writeStringToFile(gson.toJson(likeJavaBean));
				  likeAdapter.notifyDataSetChanged();
				  //	  editTextURL.setText("");

				  break;
				case R.id.fab_add_pixiv:
				  new Thread(new Runnable(){

						@Override
						public void run() {
							addFa(getPixivId(editTextURL.getText().toString()));
						  }
					  }).start();
				  break;
                case R.id.fab_start_download:
				  final String text = editTextURL.getText().toString();
				  //	  editTextURL.setText("");
				  if (text.equals("")) {
					  LogTool.e("ID不能为空");
					  return;
					}
				  LogTool.t("正在读取信息");
				  fabStartDownload.setShowProgressBackground(true);
				  fabStartDownload.setIndeterminate(true);
				  if (checkBoxIsUID.isChecked()) {
					  createDownloadAllPictureTask(text);
                    } else {
					  createDownloadTask(text);
                    }
				  break;      
			  }
		  }
	  };

	TextWatcher textWatcher = new TextWatcher() {
        @Override
        public void beforeTextChanged(CharSequence s, int start, int count, int after) {

		  }

        @Override
        public void onTextChanged(CharSequence s, int start, int before, int count) {

		  }

        @Override
        public void afterTextChanged(Editable s) {
            new Thread(new Runnable(){

				  @Override
				  public void run() {
					  final Bitmap b=getThumb(getPixivId(editTextURL.getText().toString()));
					  if (b == null)return;
					  getActivity().runOnUiThread(new Runnable(){

							@Override
							public void run() {
								imageView.setImageBitmap(b);
							  }
						  });
					}
				}).start();
		  }
	  };


    private void createDownloadTask(final String url) {
        new Thread(new Runnable() {

			  @Override
			  public void run() {
				  final PictureInfoJavaBean pictureInfoJavaBean = getPicInfo(getPixivId(url));
				  fabStartDownload.hideProgress();
				  if (pictureInfoJavaBean == null) {
					  LogTool.e("未获取到有效的图片信息");
					  return;
					}
				  getActivity().runOnUiThread(
					new Runnable() {

						@Override
						public void run() {
							if (pictureInfoJavaBean.isAnimPicture) {
								if (pictureInfoJavaBean.animPicJavaBean.error.equals("true")) {
									LogTool.e("动态图信息读取错误");
									return;
								  }
								taskLinearLayout.addView(new MengProgressBar(getActivity(), downloadedList, pictureInfoJavaBean,
																			 SharedPreferenceHelper.getBoolean(Data.preferenceKeys.downloadBigGif) ?
																			 pictureInfoJavaBean.animPicJavaBean.body.originalSrc :
																			 pictureInfoJavaBean.animPicJavaBean.body.src));
							  } else {
								if (pictureInfoJavaBean.staticPicJavaBean.error.equals("true")) {
									LogTool.e("图片信息读取错误");
									return;
								  }
								for (int i = 0; i < pictureInfoJavaBean.staticPicJavaBean.body.size(); ++i) {
									taskLinearLayout.addView(new MengProgressBar(getActivity(), downloadedList, pictureInfoJavaBean,
																				 SharedPreferenceHelper.getBoolean(Data.preferenceKeys.downloadBigPicture) ?
																				 pictureInfoJavaBean.staticPicJavaBean.body.get(i).urls.original :
																				 pictureInfoJavaBean.staticPicJavaBean.body.get(i).urls.regular));

								  }
							  }
						  }
					  }
				  );
				}
			}).start();
	  }

    private void createDownloadAllPictureTask(final String text) {
        new Thread(
		  new Runnable() {
			  @Override
			  public void run() {
				  try {
					  LinkedTreeMap linkedTreeMap = (LinkedTreeMap) getAllPaint(getPixivId(text)).body.illusts;
					  for (Object o : linkedTreeMap.keySet()) {
						  String key = (String) o;
						  //    String value = (String) linkedTreeMap.get(key);
						  createDownloadTask(key);
						  LogTool.i("添加任务:" + key);
						  Thread.sleep(Integer.parseInt(SharedPreferenceHelper.getValue("sleep", "2000")));
						}
					} catch (InterruptedException e) {
					  e.printStackTrace();
					  if (getAllPaint(text).body.illusts instanceof ArrayList) {
						  for (Object o : (ArrayList) (getAllPaint(text).body.illusts)) {
							  LogTool.i(String.valueOf(o));
							}
						}
					}
				}
			}
        ).start();
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
            FileWriter fw = new FileWriter(FileHelper.getPreDownloadJsonPath());//SD卡中的路径
            fw.flush();
            fw.write(str);
            fw.close();
		  } catch (Exception e) {
            e.printStackTrace();
		  }
	  }

    private Bitmap getThumb(String picId) {
        String main = readStringFromNetwork("https://www.pixiv.net/member_illust.php?mode=medium&illust_id=" + picId);
		if (main == null)return null;
        String flag = "\"small\":\"";
        int index1 = main.indexOf(flag) + flag.length();
        int index2 = main.indexOf("\"", index1);
        String picUrl = main.substring(index1, index2);
        try {
            return getBitmapFromNetwork(picUrl);
		  } catch (IOException e) {
            e.printStackTrace();
			//       LogTool.e(e.toString());
            return null;
		  }
	  }

    public void getToken() {
        String main = readStringFromNetwork("https://www.pixiv.net/member_illust.php?mode=medium&illust_id=74780259");
        String flag = "token: \"";
        int index1 = main.indexOf(flag) + flag.length();
        int index2 = main.indexOf("\"", index1);
        token = main.substring(index1, index2);
	  }

    public Bitmap getPixivHead() {
        String main = readStringFromNetwork("https://www.pixiv.net/member_illust.php?mode=medium&illust_id=74780259");
        String flag = "profileImg: \"";
        int index1 = main.indexOf(flag) + flag.length();
        int index2 = main.indexOf("\"", index1);
        String picUrl = main.substring(index1, index2);
		LogTool.i("" + index1 + " " + index2 + " " + picUrl.replace("\\", ""));
        try {
            return getBitmapFromNetwork(picUrl.replace("\\", ""));
		  } catch (IOException e) {
            e.printStackTrace();
			//        LogTool.e(e.toString());
            return null;
		  }
	  }

    public void addFa(String pixivID) {

		HttpURLConnection conn=null;
        try {
			LogTool.t("start");
            String Strurl="https://www.pixiv.net/ajax/illusts/bookmarks/add";
            URL url = new URL(Strurl);
            conn = (HttpURLConnection) url.openConnection();
            conn.setRequestMethod("POST");
			//   conn.setRequestProperty("ser-Agent", "Fiddler");
            conn.setRequestProperty("Content-Type", "application/json");
            conn.setConnectTimeout(5 * 1000);
			conn.addRequestProperty("Referer", "https://www.pixiv.net/member_illust.php?mode=medium&illust_id=" + pixivID);
			//	conn.setRequestProperty(":authority", "www.pixiv.net");
			conn.setRequestProperty(":authority", "www.pixiv.net");
			conn.setRequestProperty(":method", "POST");
			conn.setRequestProperty(":path", "/ajax/illusts/bookmarks/add");
			conn.setRequestProperty(":scheme", "https");
			//conn.setRequestProperty(":path", "/ajax/illusts/bookmarks/add");
			//	conn.setRequestProperty(":scheme", "https");
			conn.setRequestProperty("accept", "application/json");
			conn.setRequestProperty("accept-encoding", "gzip, deflate, br");
			conn.setRequestProperty("accept-language", "zh-CN,zh;q=0.9");
			conn.setRequestProperty("content-type", "application/json; charset=utf-8");
			conn.setRequestProperty("origin", "https://www.pixiv.net");
			conn.setRequestProperty("x-csrf-token", token);
			String c=URLEncoder.encode("{\"illust_id\":" + pixivID + ",\"restrict\":0,\"comment\":\"\",\"tags\":[]}".toString(), "UTF-8");
			conn.setRequestProperty("Content-Length", c.length() + "");
            OutputStream outputStream = conn.getOutputStream();
            outputStream.write(c.getBytes());
            outputStream.flush();
            outputStream.close();
            if (HttpURLConnection.HTTP_OK == conn.getResponseCode()) {
				//     Log.i("PostGetUtil","post请求成功");
                InputStream in=conn.getInputStream();
				//          String backcontent=IOUtils.readString(in);

				InputStreamReader isr = new InputStreamReader(in);
				BufferedReader bufferReader = new BufferedReader(isr);
				String inputLine = "";
				String resultData="";
				while ((inputLine = bufferReader.readLine()) != null) {
					resultData += inputLine + "\n";
				  }

				LogTool.e(resultData);	
				//      backcontent= URLDecoder.decode(backcontent,"UTF-8");
				//     Log.i("PostGetUtil",backcontent);
                in.close();
			  } else {
				LogTool.i("post请求失败" + conn.getResponseCode());
			  }

		  } catch (Exception e) {
			LogTool.e(e.toString());
            e.printStackTrace();
		  } finally {
            conn.disconnect();
		  }


		/* 
		 Map<String, String> map = new HashMap<>();
		 map.put(":authority", "www.pixiv.net");
		 map.put(":method", "POST");
		 map.put(":path", "/ajax/illusts/bookmarks/add");
		 map.put(":scheme", "https");
		 map.put("accept", "application/json");
		 map.put("accept-encoding", "gzip, deflate, br");
		 map.put("accept-language", "zh-CN,zh;q=0.9");
		 map.put("content-type", "application/json; charset=utf-8");
		 map.put("origin", "https://www.pixiv.net");
		 map.put("x-csrf-token", token);
		 Connection connection = Jsoup.connect("https://www.pixiv.net/ajax/illusts/bookmarks/add");
		 connection.cookies(cookieToMap(SharedPreferenceHelper.getValue(Data.preferenceKeys.cookieValue)));
		 connection.userAgent("Mozilla/5.0 (Windows NT 6.1; WOW64; rv:28.0) Gecko/20100101 Firefox/28.0");
		 connection.referrer("https://www.pixiv.net/member_illust.php?mode=medium&illust_id=74810169");
		 connection.data("illust_id","74810169");
		 connection.data("restrict","0");
		 connection.data("comment","");
		 connection.data("tags","");


		 //     connection.data("{\"illust_id\":\"74810169\",\"restrict\":0,\"comment\":\"\",\"tags\":[]}");
		 connection.headers(map);
		 connection.ignoreContentType(true).method(Connection.Method.POST);
		 try {
		 Connection.Response response = connection.execute();
		 LogTool.t(response.body());
		 } catch (IOException e) {
		 LogTool.e(e.toString());
		 e.printStackTrace();
		 }*/
	  }

    private Bitmap getBitmapFromNetwork(String picUrl) throws IOException {
        URL url = new URL(picUrl);
        HttpURLConnection conn = (HttpURLConnection) url.openConnection();
        conn.setConnectTimeout(5 * 1000);
		conn.addRequestProperty("Referer", "https://www.pixiv.net/member_illust.php?mode=medium&illust_id=74780259");
        conn.setRequestMethod("GET");
        if (conn.getResponseCode() == HttpURLConnection.HTTP_OK) {
            return BitmapFactory.decodeStream(conn.getInputStream());
		  }
        return null;
	  }

    private PictureInfoJavaBean getPicInfo(String picId) {
        String main = readStringFromNetwork("https://www.pixiv.net/member_illust.php?mode=medium&illust_id=" + picId);
		if (main == null)return null;
        String flag = "\"illustType\":";
        int index1 = main.indexOf(flag) + flag.length();
        int type = Integer.parseInt(main.substring(index1, index1 + 1));
        PictureInfoJavaBean pijb = new PictureInfoJavaBean();
        try {
            pijb.id = picId;
            switch (type) {
                case 0:

                case 1://unkonwn
				  pijb.isAnimPicture = false;
				  pijb.staticPicJavaBean = getStaticPicture(picId);

				  break;
                case 2:
				  pijb.isAnimPicture = true;
				  pijb.animPicJavaBean = getDynamicPicture(picId);
				  break;
			  }
		  } catch (Exception e) {
            LogTool.t(getActivity().getString(R.string.maybe_need_login));
            LogTool.e(e.toString());
            getActivity().startActivity(new Intent(getActivity(), LoginPixivActivity.class));
		  }
        return pijb;
	  }

    public AnimPicJavaBean getDynamicPicture(String id) {
        String picJsonAddress = "https://www.pixiv.net/ajax/illust/" + id + "/ugoira_meta";
        try {
            LogTool.i(picJsonAddress);
            return new Gson().fromJson(readStringFromNetwork(picJsonAddress), AnimPicJavaBean.class);
		  } catch (Exception e) {
            return new AnimPicJavaBean();
		  }
	  }

    public StaticPicJavaBean getStaticPicture(String id) {
        String picJsonAddress = "https://www.pixiv.net/ajax/illust/" + id + "/pages";
        try {
            LogTool.i(picJsonAddress);
            return new Gson().fromJson(readStringFromNetwork(picJsonAddress), StaticPicJavaBean.class);
		  } catch (Exception e) {
            return new StaticPicJavaBean();
		  }
	  }

    public PaitenerAllPictures getAllPaint(String uid) {
        String picJsonAddress = "https://www.pixiv.net/ajax/user/" + uid + "/profile/all";
        try {
            return new Gson().fromJson(readStringFromNetwork(picJsonAddress), PaitenerAllPictures.class);
		  } catch (Exception e) {
            return new PaitenerAllPictures();
		  }
	  }

    public String readStringFromNetwork(String url) {
        Connection.Response response = null;
        try {
            Connection connection = Jsoup.connect(url);
            connection.cookies(cookieToMap(SharedPreferenceHelper.getValue(Data.preferenceKeys.cookieValue)));
            connection.referrer("https://www.pixiv.net/member_illust.php?mode=medium&illust_id=" + getPixivId(url));
            connection.userAgent("Mozilla/5.0 (Windows NT 6.1; WOW64; rv:28.0) Gecko/20100101 Firefox/28.0");
            connection.ignoreContentType(true).method(Connection.Method.GET);
            response = connection.execute();
            //		showToast(String.valueOf(response.statusCode()));
            //		Thread.sleep(2900);
			//       LogTool.i(response.body());
		  } catch (Exception e) {
            LogTool.i(e.toString());
			return null;
		  }
        return response.body();
	  }

    public Map<String, String> cookieToMap(String value) {
        if (value == null) {
            LogTool.t("请先登录");
            getActivity().startActivity(new Intent(getActivity(), LoginPixivActivity.class));
            return null;
		  }
        Map<String, String> map = new HashMap<>();
        String values[] = value.split("; ");
        for (String val : values) {
            String vals[] = val.split("=");
            if (vals.length == 2) {
                map.put(vals[0], vals[1]);
			  } else if (vals.length == 1) {
                map.put(vals[0], "");
			  }
		  }
        return map;
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
