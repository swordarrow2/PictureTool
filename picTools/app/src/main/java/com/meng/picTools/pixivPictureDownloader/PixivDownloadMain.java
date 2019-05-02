package com.meng.picTools.pixivPictureDownloader;

import android.app.*;
import android.content.*;
import android.os.*;
import android.view.*;
import android.widget.*;
import android.widget.AdapterView.*;

import com.google.gson.*;
import com.google.gson.internal.LinkedTreeMap;
import com.meng.picTools.*;
import com.meng.picTools.javaBean.AnimPicJavaBean;
import com.meng.picTools.javaBean.LikeJavaBean;
import com.meng.picTools.javaBean.PictureInfoJavaBean;
import com.meng.picTools.javaBean.StaticPicJavaBean;
import com.meng.picTools.javaBean.allPics.PaitenerAllPictures;
import com.meng.picTools.mengViews.*;
import com.meng.picTools.qrtools.*;
import com.meng.picTools.qrtools.lib.SharedPreferenceHelper;

import java.io.*;
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
    private CheckBox checkBoxIsUID;
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

    private void init(View view) {
        TabHost tabHost = (TabHost) view.findViewById(R.id.pixiv_download_main_tabhost);
        tabHost.setup();
        tabHost.addTab(tabHost.newTabSpec("one").setIndicator("正在下载").setContent(R.id.pixiv_download_main_downloading));
        tabHost.addTab(tabHost.newTabSpec("two").setIndicator("已下载").setContent(R.id.pixiv_download_main_downloaded));
        tabHost.addTab(tabHost.newTabSpec("two").setIndicator("收藏").setContent(R.id.pixiv_download_main_like));
        gson = new Gson();
        downloadedList = (ListView) view.findViewById(R.id.saved_files_list);
        likeList = (ListView) view.findViewById(R.id.like_files_list);
        editTextURL = (EditText) view.findViewById(R.id.pixiv_download_main_edittext_url);
        Button btnStart = (Button) view.findViewById(R.id.pixiv_download_main_button_start);
        Button preStart = (Button) view.findViewById(R.id.pixiv_download_main_button_pre_start);
        btnStart.setOnClickListener(onClickListener);
        preStart.setOnClickListener(onClickListener);
        taskLinearLayout = (LinearLayout) view.findViewById(R.id.pixiv_download_main_downloadlist_task);
        checkBoxIsUID = (CheckBox) view.findViewById(R.id.pixiv_download_main_checkbox_user);
        String[] filesName = new File(MainActivity.instence.getPixivZipPath("")).list();
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
                                Toast.makeText(getActivity(), "正在读取信息", Toast.LENGTH_SHORT).show();
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
                                likeList.setAdapter(new ArrayAdapter<>(getActivity(), android.R.layout.simple_list_item_1, likeJavaBean.info));
                            }
                        }).setNegativeButton("取消", null).show();

                return true;
            }
        });

        File preDownloadJson = new File(MainActivity.instence.getPreDownloadJsonPath());
        if (preDownloadJson.exists()) {
            likeJavaBean = gson.fromJson(readStringFromFile(preDownloadJson), LikeJavaBean.class);
            likeList.setAdapter(new ArrayAdapter<>(getActivity(), android.R.layout.simple_list_item_1, likeJavaBean.info));
        }
    }

    View.OnClickListener onClickListener = new OnClickListener() {
        @Override
        public void onClick(View v) {
            switch (v.getId()) {
                case R.id.pixiv_download_main_button_start:
                    final String text = editTextURL.getText().toString();
                    editTextURL.setText("");
                    Toast.makeText(getActivity(), "正在读取信息", Toast.LENGTH_SHORT).show();
                    if (checkBoxIsUID.isChecked()) {
                        createDownloadAllPictureTask(text);
                    } else {
                        createDownloadTask(text);
                    }
                    break;
                case R.id.pixiv_download_main_button_pre_start:
                    File jsonFile = new File(MainActivity.instence.getPreDownloadJsonPath());
                    if (jsonFile.exists()) {
                        likeJavaBean = gson.fromJson(readStringFromFile(jsonFile), LikeJavaBean.class);
                    } else {
                        likeJavaBean = new LikeJavaBean();
                        likeJavaBean.info = new ArrayList<>();
                    }
                    likeJavaBean.info.add(editTextURL.getText().toString());
                    writeStringToFile(gson.toJson(likeJavaBean));
                    likeList.setAdapter(new ArrayAdapter<>(getActivity(), android.R.layout.simple_list_item_1, likeJavaBean.info));
                    editTextURL.setText("");
                    break;
            }
        }
    };

    private void createDownloadTask(final String url) {
        new Thread(new Runnable() {

            @Override
            public void run() {
                final PictureInfoJavaBean pictureInfoJavaBean = getPicInfo(getPixivId(url));
                getActivity().runOnUiThread(
                        new Runnable() {

                            @Override
                            public void run() {
                                if (pictureInfoJavaBean.isAnimPicture) {
                                    taskLinearLayout.addView(new MengProgressBar(getActivity(), downloadedList, pictureInfoJavaBean,
                                            SharedPreferenceHelper.getBoolean(Data.preferenceKeys.downloadBigPicture) ?
                                                    pictureInfoJavaBean.animPicJavaBean.body.originalSrc :
                                                    pictureInfoJavaBean.animPicJavaBean.body.src));
                                } else {
                                    for (int i = 0; i < pictureInfoJavaBean.staticPicJavaBean.body.size(); ++i)
                                        taskLinearLayout.addView(new MengProgressBar(getActivity(), downloadedList, pictureInfoJavaBean,
                                                SharedPreferenceHelper.getBoolean(Data.preferenceKeys.downloadBigPicture) ?
                                                        pictureInfoJavaBean.staticPicJavaBean.body.get(i).urls.original :
                                                        pictureInfoJavaBean.staticPicJavaBean.body.get(i).urls.regular));
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
                        LinkedTreeMap linkedTreeMap = (LinkedTreeMap) getAllPaint(text).body.illusts;
                        for (Object o : linkedTreeMap.keySet()) {
                            String key = (String) o;
                            //    String value = (String) linkedTreeMap.get(key);
                            createDownloadTask(key);
                            LogTool.i("添加任务:" + key);
                            try {
                                Thread.sleep(Integer.parseInt(SharedPreferenceHelper.getValue("sleep","2000")));
                            } catch (InterruptedException e) {
                                e.printStackTrace();
                            }
                        }
                          /*    while (it.hasNext()) {
                        String key = (String) it.next();
                        String value = (String) linkedTreeMap.get(key);
                        createDownloadTask(key);
                        try {
                            Thread.sleep(1000);
                        } catch (InterruptedException e) {
                            e.printStackTrace();
                        }
                        LogTool.i("id:" + key);
                    } */
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
            pijb.animPicJavaBean = getDynamicPicture(picId);
            if (pijb.animPicJavaBean.error.equals("true")) {
                pijb.staticPicJavaBean = getStaticPicture(picId);
                pijb.isAnimPicture = false;
            }
        } catch (Exception e) {
            LogTool.t(getActivity().getString(R.string.maybe_need_login));
            LogTool.e(e.toString());
            getActivity().startActivity(new Intent(getActivity(), login.class));
        }
        return pijb;
    }

    public AnimPicJavaBean getDynamicPicture(String id) {
        String picJsonAddress = "https://www.pixiv.net/ajax/illust/" + id + "/ugoira_meta";
        try {
            return new Gson().fromJson(readStringFromNetwork(picJsonAddress), AnimPicJavaBean.class);
        } catch (Exception e) {
            return new AnimPicJavaBean();
        }
    }

    public StaticPicJavaBean getStaticPicture(String id) {
        String picJsonAddress = "https://www.pixiv.net/ajax/illust/" + id + "/pages";
        try {
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
            connection.cookies(cookieToMap(SharedPreferenceHelper.getValue(Data.preferenceKeys.keyCookieValue)));
            connection.referrer("https://www.pixiv.net/member_illust.php?mode=medium&illust_id=" + getPixivId(url));
            connection.userAgent("Mozilla/5.0 (Windows NT 6.1; WOW64; rv:28.0) Gecko/20100101 Firefox/28.0");
            connection.ignoreContentType(true).method(Connection.Method.GET);
            response = connection.execute();
            //		showToast(String.valueOf(response.statusCode()));
            //		Thread.sleep(2900);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return response.body();
    }

    public Map<String, String> cookieToMap(String value) {
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

    public void showToast(final String msg) {
        getActivity().runOnUiThread(new Runnable() {

            @Override
            public void run() {
                Toast.makeText(getActivity(), msg, Toast.LENGTH_LONG).show();
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
