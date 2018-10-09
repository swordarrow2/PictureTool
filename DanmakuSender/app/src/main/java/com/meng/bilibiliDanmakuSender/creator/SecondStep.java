package com.meng.bilibiliDanmakuSender.creator;

import android.app.Activity;
import android.app.Fragment;
import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.os.Bundle;
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
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.PrintStream;
import java.net.HttpURLConnection;
import java.net.URL;

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
                ClipboardManager clipboardManager = (ClipboardManager)getActivity(). getSystemService(Context.CLIPBOARD_SERVICE);
                ClipData clipData = ClipData.newPlainText("text",""+getUidFromArray(position));
                clipboardManager.setPrimaryClip(clipData);
                log.t(getUidFromArray(position)+"已经复制到剪贴板");
            }
        });
    }

    private void readXml(final String avUrl){
        new Thread(new Runnable(){
            @Override
            public void run(){
                String htmlCode=readCode(avUrl);
                save(htmlCode);
                parser=new XmlParser(context);
                Thread t=new checkParse();
                t.start();
                getActivity().runOnUiThread(new Runnable(){
                    @Override
                    public void run(){
                    }
                });
            }
        }).start();
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

    private int getUidFromArray(int position){
        return Integer.parseInt(showItem[position][0]);
    }

    private void save(String data) {
        FileOutputStream out = null;
        PrintStream ps = null;
        try {
            out =getActivity().openFileOutput("danmaku.xml", Activity.MODE_PRIVATE);
            ps = new PrintStream(out);
            ps.println(data);
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (out != null) {
                try {
                    out.close();
                    ps.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    }
    public String readCode(String url) {
        try {
            HttpURLConnection connection = (HttpURLConnection) new URL(url).openConnection();
            connection.setRequestMethod("GET");
            connection.setConnectTimeout(10000);
            connection.setReadTimeout(10000);
          //  connection.setRequestProperty("cookie", MainActivity.instence.sharedPreference.getValue(Data.preferenceKey.cookieValue));
            connection.setRequestProperty("User-Agent","Mozilla/5.0 (Windows NT 6.1; WOW64; rv:52.0) Gecko/20100101 Firefox/52.0");
            InputStream in = connection.getInputStream();
            BufferedReader br = new BufferedReader(new InputStreamReader(in));
            StringBuilder sb = new StringBuilder();
            String line = null;
            while ((line = br.readLine()) != null) {
                sb.append(line);
            }
            return sb.toString();

        } catch (Exception e) {
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
