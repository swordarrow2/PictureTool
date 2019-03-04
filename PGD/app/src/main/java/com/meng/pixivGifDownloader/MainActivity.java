package com.meng.pixivGifDownloader;

import android.app.*;
import android.content.*;
import android.os.*;
import android.util.*;
import android.view.*;
import android.widget.*;

import java.io.*;
import java.net.*;

public class MainActivity extends Activity {

    public static sharedPreferenceHelper sp;
    public static String zipFolder;
    public static String gifFolder;
    public static String tmpFolder;
    private String[][] fileData = new String[512][2];
    private String[] filesName;
    private int fileDataFlag = 0;
    private EditText etUrl;
    private Button btnStart;
    private Thread tFirstThread;
    private ListView listViewDownloadList;
    private final int TOAST = 1;
    private final int UPDATEPROGRESS = 5;
    private final int SETADAPTER = 6;
    private final String keyCookieValue = "cookievalue";
    private ArrayAdapter adapterDownloadListAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main_layout);
        init();
    }

    Handler handler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case TOAST:
                    Toast.makeText(MainActivity.this, msg.obj.toString(), Toast.LENGTH_SHORT).show();
                    break;
                case UPDATEPROGRESS:
                    try {
                        LinearLayout ll = (LinearLayout) listViewDownloadList.getChildAt(msg.arg1);
                        int progre = msg.arg2;
                        ((TextView) ll.findViewById(R.id.main_list_item_textview_statu)).setText(progre == 100 ? getString(R.string.download_success) : getString(R.string.downloading));
                        ((TextView) ll.findViewById(R.id.main_list_item_textview_filename)).setText(msg.obj.toString());
                        ((ProgressBar) ll.findViewById(R.id.main_list_item_progressbar)).setProgress(progre);
                    } catch (Exception e) {
                        Log.e(getString(R.string.app_name), e.toString());
                    }
                    break;
                case SETADAPTER:
                    adapterDownloadListAdapter = new listAdapter(MainActivity.this, filesName);
                    listViewDownloadList.setAdapter(adapterDownloadListAdapter);
                    break;
            }
        }
    };

    private void init() {
        etUrl = (EditText) findViewById(R.id.et);
        btnStart = (Button) findViewById(R.id.start);
        listViewDownloadList = (ListView) findViewById(R.id.main_listview);
        sp = new sharedPreferenceHelper(this, Data.preferenceKeys.mainPreferenceName);
        if (sp.getValue(Data.preferenceKeys.zipPath) == null || sp.getValue(Data.preferenceKeys.zipPath).equals("")) {
            sp.putValue(Data.preferenceKeys.zipPath, Environment.getExternalStorageDirectory().getPath() + File.separator + "pixivPictures/");
        }
        if (sp.getValue(Data.preferenceKeys.gifPath) == null || sp.getValue(Data.preferenceKeys.gifPath).equals("")) {
            sp.putValue(Data.preferenceKeys.gifPath, Environment.getExternalStorageDirectory().getPath() + File.separator + "pixivGifs/");
        }
        if (sp.getValue(Data.preferenceKeys.tmpPath) == null || sp.getValue(Data.preferenceKeys.tmpPath).equals("")) {
            sp.putValue(Data.preferenceKeys.tmpPath, Environment.getExternalStorageDirectory().getPath() + "/tmp/");
        }
        zipFolder = sp.getValue(Data.preferenceKeys.zipPath);
        gifFolder = sp.getValue(Data.preferenceKeys.gifPath);
        tmpFolder = sp.getValue(Data.preferenceKeys.tmpPath);
        autoInput();
        listViewDownloadList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                LinearLayout ll = (LinearLayout) view;
                TextView tv = (TextView) ll.findViewById(R.id.main_list_item_textview_filename);
                ProgressBar pb = (ProgressBar) ll.findViewById(R.id.main_list_item_progressbar);
                if (pb.getProgress() == 100) {
                    Intent intent = new Intent(MainActivity.this, playLayout.class);
                    intent.putExtra(Data.intentKeys.fileName, zipFolder + File.separator + tv.getText().toString());
                    startActivity(intent);
                } else {
                    Toast.makeText(MainActivity.this, "下载完成后才可以查看", Toast.LENGTH_SHORT).show();
                }
            }
        });
        btnStart.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                tFirstThread = new getFileUrlThread(toLink(etUrl.getText().toString()));
                tFirstThread.start();
            }
        });
    }

    @Override
    protected void onResume() {
        super.onResume();
        autoInput();
    }

    private class getFileUrlThread extends Thread {
        String url;

        public getFileUrlThread(String url) {
            this.url = url;
        }

        @Override
        public void run() {
            try {
                File fileFolder = new File(zipFolder);
                if (!fileFolder.exists()) {
                    if (!fileFolder.mkdirs()) {
                        throw new IOException();
                    }
                }
                URL u = new URL(url);
                HttpURLConnection connection = (HttpURLConnection) u.openConnection();
                connection.setRequestMethod("GET");
                connection.setConnectTimeout(10000);
                connection.setReadTimeout(10000);
                connection.setRequestProperty("cookie", sp.getValue(keyCookieValue));
                connection.setRequestProperty("User-Agent", "Mozilla/5.0 (Windows NT 6.1; WOW64; rv:28.0) Gecko/20100101 Firefox/28.0");
                InputStream in = connection.getInputStream();
                BufferedReader br = new BufferedReader(new InputStreamReader(in));
                StringBuilder sb = new StringBuilder();
                String line;
                while ((line = br.readLine()) != null) {
                    sb.append(line);
                }
                String htmlText = sb.toString();
                sendToast(htmlText);
                int startIndex = sp.getBoolean(Data.preferenceKeys.downloadBigPicture) ?
                        htmlText.lastIndexOf("pixiv.context.ugokuIllustFullscreenData  = {\"src\":\"")
                                + "pixiv.context.ugokuIllustFullscreenData  = {\"src\":\"".length()
                        : htmlText.lastIndexOf("pixiv.context.ugokuIllustData            = {\"src\":\"")
                        + "pixiv.context.ugokuIllustData            = {\"src\":\"".length();
                int endIndex = htmlText.indexOf("\",\"mime_type\":\"image\\/jpeg\",\"frames\"", startIndex);
                String subedStr = htmlText.substring(startIndex, endIndex);
                String deletedUrl = "";
                for (int i = 0; i < subedStr.length(); i++) {
                    if (subedStr.charAt(i) != '\\') {
                        deletedUrl += subedStr.charAt(i);
                    }
                }
                Thread downloadzip = new downloadFile(deletedUrl);
                downloadzip.start();
            } catch (MalformedURLException e) {
                sendToast(e.toString());
                Log.e(getString(R.string.app_name), e.toString());
            } catch (IOException e) {
                sendToast(e.toString());
                Log.e(getString(R.string.app_name), e.toString());
            } catch (RuntimeException e) {
                sendToast(e.toString());
                Log.e(getString(R.string.app_name), e.toString());
            }
        }
    }

    private class downloadFile extends Thread {
        String zipFlieUrl = "";

        downloadFile(String url) {
            zipFlieUrl = url;
        }

        @Override
        public void run() {
            try {
                URL url = new URL(zipFlieUrl);
                String expandName = zipFlieUrl.substring(zipFlieUrl.lastIndexOf(".") + 1, zipFlieUrl.length()).toLowerCase();
                String fileName = zipFlieUrl.substring(zipFlieUrl.lastIndexOf("/") + 1, zipFlieUrl.lastIndexOf("."));
                File file = new File(zipFolder + fileName + "." + expandName);
                if (file.exists()) {
                    sendToast(getString(R.string.file_exist) + file.getName());
                } else {
                    HttpURLConnection urlConn = (HttpURLConnection) url.openConnection();
                    urlConn.setRequestProperty("User-Agent", "Mozilla/5.0 (Windows NT 6.1; WOW64; rv:28.0) Gecko/20100101 Firefox/28.0");
                    urlConn.setRequestProperty("cookie", sp.getValue(keyCookieValue));
                    urlConn.setRequestProperty("Referer", toLink(etUrl.getText().toString()));
                    fileData[fileDataFlag][0] = file.getAbsolutePath();
                    fileData[fileDataFlag][1] = String.valueOf(urlConn.getContentLength());
                    if (listViewDownloadList.getAdapter() == null) {
                        filesName = new String[]{
                                file.getName()
                        };
                    } else {
                        filesName = addData(filesName, file.getName());
                    }
                    Message m = new Message();
                    m.what = SETADAPTER;
                    handler.sendMessage(m);
                    fileDataFlag++;
                    InputStream is = urlConn.getInputStream();
                    if (is != null) {
                        FileOutputStream fos = new FileOutputStream(file);
                        byte buf[] = new byte[4096];
                        while (true) {
                            int numread = is.read(buf);
                            if (numread <= 0) {
                                break;
                            } else {
                                fos.write(buf, 0, numread);
                            }
                        }
                    }
                    is.close();
                    urlConn.disconnect();
                }
                //  Thread unzip = new unzipThread(file, fileName);
                //  unzip.start();
            } catch (MalformedURLException e) {
                sendToast(getString(R.string.maybe_need_login));
                try {
                    sleep(1000);
                } catch (InterruptedException e1) {
                }
                Intent in = new Intent(MainActivity.this, login.class);
                in.putExtra(Data.intentKeys.url, toLink(etUrl.getText().toString()));
                startActivityForResult(in, 1);
            } catch (IOException e) {
                sendToast(e.toString());
                Log.e(getString(R.string.app_name), e.toString());
            }
        }
    }

    public boolean onCreateOptionsMenu(Menu menu) {
        menu.add(0, 0, 0, R.string.look_as_web);
        menu.add(1, 1, 1, R.string.look_downloaded);
        menu.add(2, 2, 2, R.string.settings);
        menu.add(3, 3, 3, R.string.update_content);
        return super.onCreateOptionsMenu(menu);
    }

    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case 0:
                Intent in = new Intent(this, login.class);
                in.putExtra(Data.intentKeys.url, toLink(etUrl.getText().toString()));
                startActivity(in);
                break;
            case 1:
                Intent in1 = new Intent(this, savedFiles.class);
                startActivity(in1);
                break;
            case 2:
                Intent in2 = new Intent(this, preferenceActivity.class);
                startActivity(in2);
                break;
            case 3:
                Intent i3 = new Intent(this, newVersion.class);
                startActivity(i3);
        }

        return false;
    }

    private void sendToast(String message) {
        Message m = new Message();
        m.what = TOAST;
        m.obj = message;
        handler.sendMessage(m);
    }

    private class listAdapter extends ArrayAdapter<String> {
        public listAdapter(Context c, String[] s) {
            super(c, R.layout.main_list_item, s);
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            LayoutInflater inf = LayoutInflater.from(getContext());
            View view = inf.inflate(R.layout.main_list_item, parent, false);
            TextView tv = (TextView) view.findViewById(R.id.main_list_item_textview_filename);
            tv.setText(filesName[position]);
            Thread update = new updateThread(position);
            update.start();
            return view;
        }
    }

    private class updateThread extends Thread {
        int position;
        String fileName;
        int fileSize;
        File f;

        updateThread(int position) {
            this.position = position;
            fileName = fileData[position][0];
            fileSize = Integer.parseInt(fileData[position][1]);
            f = new File(fileName);
        }

        @Override
        public void run() {
            while (f.length() <= fileSize) {
                Message m = new Message();
                m.what = UPDATEPROGRESS;
                m.arg1 = position;
                m.arg2 = (int) (f.length() * 100 / fileSize);
                m.obj = f.getName();
                handler.sendMessage(m);
                try {
                    sleep(200);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    private String[] addData(String[] s, String text) {
        String[] tmp = s;
        s = new String[tmp.length + 1];
        System.arraycopy(tmp, 0, s, 0, tmp.length);
        s[s.length - 1] = text;
        return s;
    }

    private void autoInput() {
        if (sp.getBoolean(Data.preferenceKeys.autoInput)) {
            try {
                ClipboardManager clipboardManager = (ClipboardManager) getSystemService(Context.CLIPBOARD_SERVICE);
                if (clipboardManager != null && clipboardManager.hasPrimaryClip()) {
                    ClipData clipData = clipboardManager.getPrimaryClip();
                    String text = clipData.getItemAt(0).getText().toString();
                    try {
                        int pixivID = Integer.parseInt(text);
                        etUrl.setText(String.valueOf(pixivID));
                    } catch (Exception e) {
                        if (text.substring(0, 62).equalsIgnoreCase("https://www.pixiv.net/member_illust.php?mode=medium&illust_id=")) {
                            etUrl.setText(text);
                        }
                    }
                } else {
                    sendToast("链接不能为空");
                }
            } catch (Exception e) {
            }
        }
    }

    private String toLink(String urlOrId) {
        if (urlOrId != null) {
            try {
                int pixivID = Integer.parseInt(urlOrId);
                return "https://www.pixiv.net/member_illust.php?mode=medium&illust_id=" + pixivID;
            } catch (NumberFormatException e) {
                int length = urlOrId.length() > 62 ? 62 : urlOrId.length();
                if (urlOrId.substring(0, length).equalsIgnoreCase("https://www.pixiv.net/member_illust.php?mode=medium&illust_id=")) {
                    return urlOrId;
                } else {
                    return "";
                }
            }
        } else {
            sendToast("链接不能为空");
            return "";
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if ((data.getStringExtra(Data.intentKeys.result)).equals(Data.status.success)) {
            tFirstThread = new getFileUrlThread(toLink(etUrl.getText().toString()));
            tFirstThread.start();
        }
    }
}
