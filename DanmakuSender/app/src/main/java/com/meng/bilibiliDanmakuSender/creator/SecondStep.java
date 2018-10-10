package com.meng.bilibiliDanmakuSender.creator;

import android.app.Fragment;
import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.meng.bilibiliDanmakuSender.R;
import com.meng.bilibiliDanmakuSender.lib.XmlParser;
import com.meng.bilibiliDanmakuSender.log;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * Created by Administrator on 2018/8/18.
 */

public class SecondStep extends Fragment{
    public static final int PARSING=1;
    public static final int PARSE_COMPETE=2;
    public static final int REFRESH=3;


    private int xmlLength=0;
    private int showFlag;
    private EditText et;
    public static String[][] danmakuAndSenderArray;
    public String[][] showItem;
    private XmlParser parser;
    private ListView lv;
    private ProgressBar pb;
    private Context context;
    private ArrayAdapter adapter;
    private TextView textView;
    private String sourceCode="";


    @Override
    public View onCreateView(LayoutInflater inflater,ViewGroup container,Bundle savedInstanceState){
        // TODO: Implement this method
        return inflater.inflate(R.layout.second_step,container,false);
    }

    @Override
    public void onViewCreated(View view,Bundle savedInstanceState){
        // TODO: Implement this method
        super.onViewCreated(view,savedInstanceState);
        context=getActivity();
        lv=(ListView)view.findViewById(R.id.mainList);
        pb=(ProgressBar)view.findViewById(R.id.activity_mainProgressBar);
        et=(EditText)view.findViewById(R.id.mainInput);
        textView=(TextView)view.findViewById(R.id.mainText);
        et.addTextChangedListener(tw);
        lv.setOnItemClickListener(new AdapterView.OnItemClickListener(){
            @Override
            public void onItemClick(AdapterView<?> parent,View view,int position,long id){
                ClipboardManager clipboardManager=(ClipboardManager)getActivity().getSystemService(Context.CLIPBOARD_SERVICE);
                ClipData clipData=ClipData.newPlainText("text",""+getUidFromString(position));
                clipboardManager.setPrimaryClip(clipData);
                log.t(getUidFromString(position)+"已经复制到剪贴板");
            }
        });
    }

    private void readXml(final String avUrl){
        new Thread(new Runnable(){
            @Override
            public void run(){
                sourceCode=readCode(avUrl.replace("http://","https://"));
                //	log.i(sourceCode);
                int index=sourceCode.trim().indexOf("\"cid\":")+6;
                int end=sourceCode.indexOf(",",index);
                String cid=sourceCode.substring(index,end);
                log.i("cid:"+cid);
                String xmlCode=download("http://comment.bilibili.com/"+cid+".xml");
                log.i(xmlCode);
         //       try{
          //          xmlCode=new String(xmlCode.getBytes("UTF-8"),"UTF-8");
          //          log.i(readCode("https://comment.bilibili.com/48677698.xml"));
           //     }catch(Exception e){
           //         e.printStackTrace();
           //     }
          //         if(downloadXml("https://comment.bilibili.com/"+cid+".xml")){
           //            parser=new XmlParser(context);
            //           Thread t=new checkParse();
            //          t.start();
             //     }else{
             //         log.t("downloadFailed");
              //    }

                //     save(xmlCode);
                //    log.i(xmlCode);
                //         getActivity().runOnUiThread(new Runnable(){
                //            @Override
                //            public void run(){

                //           }
                //       });
            }
        }).start();
    }

    private boolean downloadXml(String xmlUrl){
        try{
            URL url=new URL(xmlUrl);
            String expandName=xmlUrl.substring(xmlUrl.lastIndexOf(".")+1,xmlUrl.length()).toLowerCase();
            String fileName=xmlUrl.substring(xmlUrl.lastIndexOf("/")+1,xmlUrl.lastIndexOf("."));
            File file=new File(Environment.getExternalStorageDirectory()+File.separator+fileName+"."+expandName);
            if(!file.exists()){
                file.createNewFile();
            }
            HttpURLConnection urlConn=(HttpURLConnection)url.openConnection();
            // urlConn.setRequestProperty("User-Agent","Mozilla/5.0 (Windows NT 6.1; WOW64; rv:28.0) Gecko/20100101 Firefox/28.0");
            //        urlConn.setRequestProperty("cookie",sp.getValue(keyCookieValue));
            //        urlConn.setRequestProperty("Referer",toLink(etUrl.getText().toString()));

            urlConn.setRequestMethod("GET");
            urlConn.setRequestProperty("Content-Type","application/x-www-form-urlencoded; charset=UTF-8");
            //  connection.setRequestProperty("cookie", MainActivity.instence.sharedPreference.getValue(Data.preferenceKey.cookieValue));
            urlConn.setRequestProperty("User-Agent","Mozilla/5.0 (Windows NT 6.1; WOW64) AppleWebKit/537.36 (KHTML, like Gecko)"+
                    " Chrome/55.0.2883.87 Safari/537.36");
            urlConn.setRequestProperty("Host","comment.bilibili.com");
            urlConn.setRequestProperty("Connection","keep-alive");
            urlConn.setRequestProperty("Cache-Control","max-age=0");
            urlConn.setRequestProperty("Upgrade-Insecure-Requests","1");
            urlConn.setRequestProperty("Accept","text/html,application/xhtml+xml,application/xml;q=0.9,image/webp,*/*;q=0.8");
            urlConn.setRequestProperty("Accept-Encoding","gzip, deflate, br");
            urlConn.setRequestProperty("Accept-Language","zh-CN,zh;q=0.8");
            urlConn.setRequestProperty("cookie",
                    "fts=1509037707; sid=hwqicc8e; rpdid=omqxwqwkqpdoswlwkmxqw; pgv_pvi=2928584704; "
                            +"LIVE_BUVID=545efa86a230dcd329e38b73080179c5; LIVE_BUVID__ckMd5=99788681a69c645d;"
                            +" buvid3=08099554-0125-478D-94BA-08B2667C073914560infoc; DedeUserID=64483321;"
                            +" DedeUserID__ckMd5=ae48d3a90319855b; SESSDATA=8a949502%2C1539070479%2C60bd7408;"
                            +" bili_jct=0de67fbb32ed97ecbd5d32d93b8c3e3e; "
                            +"UM_distinctid=165dd0db61f134-01fd72ef80ec3-6b1b1279-100200-165dd0db62019c;"
                            +" Hm_lvt_8a6e55dbd2870f0f5bc9194cddf32a02=1537110754,1537115478,1537115504;"
                            +" finger=05ae7751; im_seqno_64483321=12; im_local_unread_64483321=0; CURRENT_QUALITY=64;"
                            +" im_notify_type_64483321=0; bp_t_offset_64483321=172072835308537041; CURRENT_FNVAL=8; stardustvideo=1;"
                            +" _dfcaptcha=36ed05e5335ab6d05df4fa6957337b95");

            InputStream is=urlConn.getInputStream();
            if(is!=null){
                FileOutputStream fos=new FileOutputStream(file);
                byte buf[]=new byte[4096];
                while(true){
                    int numread=is.read(buf);
                    if(numread<=0){
                        break;
                    }else{
                        fos.write(buf,0,numread);
                    }
                }
            }
            is.close();
            urlConn.disconnect();

        }catch(Exception e){
            log.e(e);
            return false;
        }
        return true;
    }
    public String download(String urlStr) {
            URL url = null;
            HttpURLConnection httpURLConnection = null;
            try {
                url = new URL(urlStr);
                //1 提取url中的文件名
                int location =urlStr.lastIndexOf('/');
                String filename = urlStr.substring(location+1);
                //2.打开连接
                httpURLConnection = (HttpURLConnection) url.openConnection();
                httpURLConnection.setRequestMethod("GET");
                httpURLConnection.setRequestProperty("Content-Type","application/x-www-form-urlencoded; charset=UTF-8");
                //  connection.setRequestProperty("cookie", MainActivity.instence.sharedPreference.getValue(Data.preferenceKey.cookieValue));
                httpURLConnection.setRequestProperty("User-Agent","Mozilla/5.0 (Windows NT 6.1; WOW64) AppleWebKit/537.36 (KHTML, like Gecko)"+
                        " Chrome/55.0.2883.87 Safari/537.36");
                httpURLConnection.setRequestProperty("Host","comment.bilibili.com");
                httpURLConnection.setRequestProperty("Connection","keep-alive");
                httpURLConnection.setRequestProperty("Cache-Control","max-age=0");
                httpURLConnection.setRequestProperty("Upgrade-Insecure-Requests","1");
                httpURLConnection.setRequestProperty("Accept","text/html,application/xhtml+xml,application/xml;q=0.9,image/webp,*/*;q=0.8");
                httpURLConnection.setRequestProperty("Accept-Encoding","gzip, deflate, br");
                httpURLConnection.setRequestProperty("Accept-Language","zh-CN,zh;q=0.8");
                httpURLConnection.setRequestProperty("cookie",
                        "fts=1509037707; sid=hwqicc8e; rpdid=omqxwqwkqpdoswlwkmxqw; pgv_pvi=2928584704; "
                                +"LIVE_BUVID=545efa86a230dcd329e38b73080179c5; LIVE_BUVID__ckMd5=99788681a69c645d;"
                                +" buvid3=08099554-0125-478D-94BA-08B2667C073914560infoc; DedeUserID=64483321;"
                                +" DedeUserID__ckMd5=ae48d3a90319855b; SESSDATA=8a949502%2C1539070479%2C60bd7408;"
                                +" bili_jct=0de67fbb32ed97ecbd5d32d93b8c3e3e; "
                                +"UM_distinctid=165dd0db61f134-01fd72ef80ec3-6b1b1279-100200-165dd0db62019c;"
                                +" Hm_lvt_8a6e55dbd2870f0f5bc9194cddf32a02=1537110754,1537115478,1537115504;"
                                +" finger=05ae7751; im_seqno_64483321=12; im_local_unread_64483321=0; CURRENT_QUALITY=64;"
                                +" im_notify_type_64483321=0; bp_t_offset_64483321=172072835308537041; CURRENT_FNVAL=8; stardustvideo=1;"
                                +" _dfcaptcha=36ed05e5335ab6d05df4fa6957337b95");

                //3 获得相应头
                Map<String, List<String>> map = httpURLConnection.getHeaderFields();
                Set<String> keySet = map.keySet();
                StringBuilder builder = new StringBuilder();
                builder.append("filename : "+filename+"\n");
                List<String> values ;
                if (keySet != null){
                    for (String s:keySet) {
                        builder.append(s+":");
                        values = map.get(s);
                        for (String ss:values ) {
                            builder.append(ss);
                        }
                        builder.append("\n");
                    }
                }
                //4. 获得读入流
                InputStream in = httpURLConnection.getInputStream();
                //4.1获得文件长度
                int length = Integer.valueOf(map.get("Content-Length").get(0));
                byte []bbb = new byte[length];
                //读出文件
                in.read(bbb);
                String ssss = new String(bbb);
                builder.append(ssss);
                //关闭流
                in.close();
                httpURLConnection.disconnect();
                return builder.toString();
            } catch (MalformedURLException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            }
            return "";
    }

    public Handler handler=new Handler(){
        @Override
        public void handleMessage(Message msg){
            super.handleMessage(msg);
            switch(msg.arg1){
                case PARSE_COMPETE:
                    pb.setVisibility(View.GONE);
                case REFRESH:
                    String tmp=et.getText().toString();
                    showFlag=0;
                    for(int i=0;i<xmlLength;i++){
                        if((danmakuAndSenderArray[i][0].contains(tmp))||(danmakuAndSenderArray[i][1].contains(tmp))){
                            showItem[showFlag][0]=danmakuAndSenderArray[i][0];
                            showItem[showFlag][1]=danmakuAndSenderArray[i][1];
                            showFlag++;
                        }
                    }
                    adapter=new ArrayAdapter(getActivity(),android.R.layout.simple_list_item_1,getDanmakuFromArray());
                    lv.setAdapter(adapter);
            }
        }
    };

    private String[] getDanmakuFromArray(){
        String[] tmp=new String[showItem.length];
        for(int i=0;i<showItem.length;i++){
            tmp[1]=showItem[i][1];
        }
        return tmp;
    }

    private int getUidFromString(int position){
        return 1234567;
    }

    private String getStringFromArray(int position){
        return showItem[position][0];
    }

    private void save(String data){
        String message=et.getText().toString();
        try{
            File f=new File(Environment.getExternalStorageDirectory()+"/danmaku.xml");
            if(!f.exists()){
                f.createNewFile();
            }
            log.i(f.getAbsolutePath());
            FileOutputStream fout=new FileOutputStream(f);
            byte[] bytes=message.getBytes();
            fout.write(bytes);
            fout.close();
        }catch(Exception e){
            log.e(e);
        }
    }

    public String readCode(String url){
        try{
            HttpURLConnection connection=(HttpURLConnection)new URL(url).openConnection();
            connection.setRequestMethod("GET");
            connection.setConnectTimeout(10000);
            connection.setReadTimeout(10000);
            connection.setRequestProperty("User-Agent",
                    "Mozilla/5.0 (Windows NT 6.1; WOW64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/55.0.2883.87 Safari/537.36");


            InputStream in=connection.getInputStream();
            BufferedReader br=new BufferedReader(new InputStreamReader(in,"utf-8"));
            StringBuilder sb=new StringBuilder();
            String line=null;
            while((line=br.readLine())!=null){
                sb.append(line);
            }
            return sb.toString();

        }catch(Exception e){
            return e.toString();
        }
    }

    TextWatcher tw=new TextWatcher(){

        @Override
        public void beforeTextChanged(CharSequence p1,int p2,int p3,int p4){
        }

        @Override
        public void onTextChanged(CharSequence p1,int p2,int p3,int p4){
        }

        @Override
        public void afterTextChanged(Editable p1){
            createNewMessage(REFRESH);
        }
    };

    private class checkParse extends Thread{
        @Override
        public void run(){
            try{
                xmlLength=parser.getXmlLength();
                danmakuAndSenderArray=new String[xmlLength][2];
                showItem=new String[xmlLength][2];
                for(int i=0;i<xmlLength;i++){
                    for(int k=0;k<2;k++){
                        danmakuAndSenderArray[i][k]="";
                    }
                }
                showItem=danmakuAndSenderArray;
                parser.startParse();
                while(!parser.isParsedSeccuss()){
                    sleep(10);
                }
                createNewMessage(PARSE_COMPETE);
                createNewMessage(REFRESH);
            }catch(Exception e){
                log.e(et.getText().toString());
            }
        }
    }

    void createNewMessage(int i){
        Message m=new Message();
        m.arg1=i;
        handler.sendMessage(m);
    }

    public void setDataStr(String avUrl){
        readXml(avUrl);
    }
}
