package com.meng.bilibiliDanmakuSender.lib;

import android.content.Context;
import android.content.res.XmlResourceParser;
import android.util.Xml;

import com.meng.bilibiliDanmakuSender.R;
import com.meng.bilibiliDanmakuSender.creator.SecondStep;
import com.meng.bilibiliDanmakuSender.log;

import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserException;

import java.io.IOException;
import java.io.InputStream;
import android.os.*;


/**
 * Created by Administrator on 2018/3/25.
 */

public class XmlParser{
    Context c;
    int xmlLenth=0;
    boolean parsered=false;

    public XmlParser(Context context){
        c=context;
    }

    public int getXmlLength(){
        run();
        return xmlLenth;
    }

    public boolean isParsedSeccuss(){
        return parsered;
    }

    public void startParse(){
        parsered=false;
        Thread t=new parseXml();
        t.start();
    }

    class parseXml extends Thread{

        InputStream inputStream=null;
        XmlPullParser xmlParser=Xml.newPullParser();

        @Override
        public void run(){
            try{
                inputStream=c.getResources().getAssets().open(Environment.getExternalStorageDirectory()+"/danmaku.xml");
                xmlParser.setInput(inputStream,"utf-8");
                int evtType=xmlParser.getEventType();
                int i=0;
                while(evtType!=XmlPullParser.END_DOCUMENT){
                    switch(evtType){
                        case XmlPullParser.START_TAG:
                            if(xmlParser.getName().equals("d")){
                                SecondStep.danmakuAndSenderArray[i][0]=xmlParser.getAttributeValue(0);
                                SecondStep.danmakuAndSenderArray[i][1]=xmlParser.getText();
                                log.i(SecondStep.danmakuAndSenderArray[i][0]);
                                log.i(SecondStep.danmakuAndSenderArray[i][1]);
                            }
                            break;
                    }
                    evtType=xmlParser.next();
                    i++;
                }
            }catch(XmlPullParserException e){
				log.e(e);
			}catch(IOException e1){
				log.e(e1);
			}
            parsered=true;
        }
    }

    public void run(){
        InputStream inputStream=null;
        XmlPullParser xmlParser=Xml.newPullParser();
        try{
            inputStream=c.getResources().getAssets().open(Environment.getExternalStorageDirectory()+"/danmaku.xml");
            xmlParser.setInput(inputStream,"utf-8");
            int evtType=xmlParser.getEventType();
            while(evtType!=XmlPullParser.END_DOCUMENT){
                switch(evtType){
                    case XmlPullParser.START_TAG:
                        if(xmlParser.getName().equals("d")){
                            xmlLenth++;
                        }
                        break;
                }
                evtType=xmlParser.next();
            }
        }catch(XmlPullParserException e){
            log.e(e);
        }catch(IOException e1){
            log.e(e1);
        }

    }

}
