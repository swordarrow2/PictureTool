package com.meng.mbrowser.tools;

import android.app.*;
import android.content.*;
import java.util.*;

/**
 * Created by Administrator on 2018/6/8.
 */

public class AppManager{

    private static Stack<Activity> activityStack;
    private static AppManager instance;
    private AppManager(){
    }
    public static AppManager getAppManager(){
        if(instance==null){
            instance=new AppManager();
        }
        return instance;
    }

    public void addActivity(Activity activity){
        if(activityStack==null){
            activityStack=new Stack<Activity>();
        }
        activityStack.add(activity);
    }
    public Activity currentActivity(){
        Activity activity = activityStack.lastElement();
        return activity;
    }

    public void finishActivity(){
        Activity activity = activityStack.lastElement();
        finishActivity(activity);
    }

    public void finishActivity(Activity activity){
        if(activity!=null){
            activityStack.remove(activity);
            activity.finish();
            activity=null;
        }
    }

    public void finishActivity(Class<?> cls){
        for(Activity activity : activityStack){
            if(activity.getClass().equals(cls)){
                finishActivity(activity);
                break;
            }
        }
    }

    public void finishAllActivity(){
        for(int i = 0; i<activityStack.size(); i++){
            if(null!=activityStack.get(i)){
                activityStack.get(i).finish();
            }
        }
        activityStack.clear();
    }

    public void AppExit(Context context){
        try{
            finishAllActivity();
            //退出程序
            android.os.Process.killProcess(android.os.Process.myPid());
            System.exit(1);
        }catch(Exception e){
        }
    }
}
