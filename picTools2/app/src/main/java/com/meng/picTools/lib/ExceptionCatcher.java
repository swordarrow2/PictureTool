package com.meng.picTools.lib;

import android.content.Context;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Environment;
import android.os.Looper;
import android.util.Log;
import android.widget.Toast;

import java.io.File;
import java.io.FileOutputStream;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.io.Writer;
import java.lang.reflect.Field;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

public class ExceptionCatcher implements Thread.UncaughtExceptionHandler{

    private Thread.UncaughtExceptionHandler mDefaultHandler;
    private Context mContext;
    private Map<String,String> paramsMap=new HashMap<>();
    private SimpleDateFormat format=new SimpleDateFormat("yyyy-MM-dd-HH-mm-ss");
    private String TAG=this.getClass().getSimpleName();
    private static ExceptionCatcher mInstance;
	private String fileName;

    private ExceptionCatcher(){
    }

    public static synchronized ExceptionCatcher getInstance(){
        if(null==mInstance){
            mInstance=new ExceptionCatcher();
        }
        return mInstance;
    }

    public void init(Context context){
        mContext=context;
        mDefaultHandler=Thread.getDefaultUncaughtExceptionHandler();
        Thread.setDefaultUncaughtExceptionHandler(this);
    }

    @Override
    public void uncaughtException(Thread thread,Throwable ex){
        if(!handleException(ex)&&mDefaultHandler!=null){
            mDefaultHandler.uncaughtException(thread,ex);
        }else{
            try{
                Thread.sleep(5000);
            }catch(InterruptedException e){
            }
            System.exit(0);
        }
    }

    private boolean handleException(Throwable ex){
        if(ex==null){
            return false;
        }
        collectDeviceInfo(mContext);
        addCustomInfo();
        saveCrashInfo2File(ex);
		new Thread(){
            @Override
            public void run(){
                Looper.prepare();
                Toast.makeText(mContext,"程序开小差了呢..",Toast.LENGTH_SHORT).show();
				Toast.makeText(mContext,"崩溃记录已保存至"+fileName,Toast.LENGTH_LONG).show();
                Looper.loop();
            }
        }.start();
        return true;
    }


    public void collectDeviceInfo(Context ctx){
        try{
            PackageManager pm=ctx.getPackageManager();
            PackageInfo pi=pm.getPackageInfo(ctx.getPackageName(),PackageManager.GET_ACTIVITIES);
            if(pi!=null){
                String versionName=pi.versionName==null?"null":pi.versionName;
                String versionCode=pi.versionCode+"";
                paramsMap.put("versionName",versionName);
                paramsMap.put("versionCode",versionCode);
            }
        }catch(PackageManager.NameNotFoundException e){
            Log.e(TAG,e.toString());
        }
        Field[] fields=Build.class.getDeclaredFields();
        for(Field field : fields){
            try{
                field.setAccessible(true);
                paramsMap.put(field.getName(),field.get(null).toString());
            }catch(Exception e){
                Log.e(TAG,e.toString());
            }
        }
    }

    private void addCustomInfo(){

    }

    private String saveCrashInfo2File(Throwable ex){

        StringBuffer sb=new StringBuffer();
        for(Map.Entry<String,String> entry : paramsMap.entrySet()){
            String key=entry.getKey();
            String value=entry.getValue();
            sb.append(key+"="+value+"\n");
        }

        Writer writer=new StringWriter();
        PrintWriter printWriter=new PrintWriter(writer);
        ex.printStackTrace(printWriter);
        Throwable cause=ex.getCause();
        while(cause!=null){
            cause.printStackTrace(printWriter);
            cause=cause.getCause();
        }
        printWriter.close();
        String result=writer.toString();
        sb.append(result);
        try{
            long timestamp=System.currentTimeMillis();
            String time=format.format(new Date());
             fileName="crash-"+time+"-"+timestamp+".log";
            if(Environment.getExternalStorageState().equals(Environment.MEDIA_MOUNTED)){
                String path=Environment.getExternalStorageDirectory().getAbsolutePath()+"/crash/";
                File dir=new File(path);
                if(!dir.exists()){
                    dir.mkdirs();
                }
                FileOutputStream fos=new FileOutputStream(path+fileName);
                fos.write(sb.toString().getBytes());
                fos.close();
            }
            return fileName;
        }catch(Exception e){
            Log.e(TAG,e.toString());
        }
        return null;
    }
}
