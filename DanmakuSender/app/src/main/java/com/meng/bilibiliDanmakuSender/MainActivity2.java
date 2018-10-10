package com.meng.bilibiliDanmakuSender;

import android.Manifest;
import android.app.ActionBar;
import android.app.Activity;
import android.app.Fragment;
import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.content.pm.PackageManager;
import android.content.res.Configuration;
import android.os.Bundle;
import android.support.v4.app.ActivityCompat;
import android.support.v4.widget.DrawerLayout;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.meng.bilibiliDanmakuSender.creator.FirstStep;
import com.meng.bilibiliDanmakuSender.creator.SecondStep;
import com.meng.bilibiliDanmakuSender.lib.materialDesign.ActionBarDrawerToggle;
import com.meng.bilibiliDanmakuSender.lib.materialDesign.DrawerArrowDrawable;

public class MainActivity2 extends Activity{
    public static MainActivity2 instence;
    private DrawerLayout mDrawerLayout;
    private ListView mDrawerList;
    private RelativeLayout rt;
    private ActionBarDrawerToggle mDrawerToggle;
    private DrawerArrowDrawable drawerArrow;
    public FirstStep firstStepFragment;
    public SecondStep secondStepFragment;
    private settings settingsFragmrnt;
    public TextView rightText;
    public FragmentManager manager;
    private static final int REQUEST_EXTERNAL_STORAGE=1;

    private static String[] PERMISSIONS_STORAGE={
            Manifest.permission.READ_EXTERNAL_STORAGE,
            Manifest.permission.WRITE_EXTERNAL_STORAGE
    };

    @Override
    protected void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main_activity);
        instence=this;
        setActionBar();
        findViews();
        initFragment();
        log.i("initFragment");
        setListener();
        log.i("setListener");
        changeTheme();
        log.i("changeTheme");
    }

    private void changeTheme(){
        if(MainActivity.sharedPreference.getBoolean("useLightTheme",true)){
            mDrawerList.setBackgroundColor(getResources().getColor(android.R.color.background_light));
            rt.setBackgroundColor(getResources().getColor(android.R.color.background_light));
        }else{
            mDrawerList.setBackgroundColor(getResources().getColor(android.R.color.background_dark));
            rt.setBackgroundColor(getResources().getColor(android.R.color.background_dark));
        }
    }

    @Override
    public void setTheme(int resid){
        if(MainActivity.lightTheme){
            super.setTheme(R.style.AppThemeLight);
        }else{
            super.setTheme(R.style.AppThemeDark);
        }
    }

    private void setActionBar(){
        ActionBar ab=getActionBar();
        ab.setDisplayHomeAsUpEnabled(true);
        ab.setHomeButtonEnabled(true);
    }

    private void setListener(){
        drawerArrow=new DrawerArrowDrawable(this){
            @Override
            public boolean isLayoutRtl(){
                return false;
            }
        };
        mDrawerToggle=new ActionBarDrawerToggle(this,mDrawerLayout,drawerArrow,R.string.open,R.string.close){

            public void onDrawerClosed(View view){
                super.onDrawerClosed(view);
                invalidateOptionsMenu();
            }

            public void onDrawerOpened(View drawerView){
                super.onDrawerOpened(drawerView);
                invalidateOptionsMenu();
            }

            @Override
            public void onDrawerSlide(View drawerView,float slideOffset){
                super.onDrawerSlide(drawerView,slideOffset);
            }
        };
        mDrawerLayout.setDrawerListener(mDrawerToggle);
        mDrawerToggle.syncState();
        mDrawerList.setAdapter(new ArrayAdapter<String>(this,android.R.layout.simple_list_item_1,android.R.id.text1,new String[]{
                "1:输入视频链接","2:选择弹幕","设置","退出"
        }));
        mDrawerList.setOnItemClickListener(new AdapterView.OnItemClickListener(){
            @Override
            public void onItemClick(AdapterView<?> parent,View view,int position,long id){
                switch(((TextView)view).getText().toString()){
                    case "1:输入视频链接":
                        initFirstStepFragment(true);
                        break;
                    case "2:选择弹幕":
                        initSecondStepFragment(true);
                        break;
                    case "设置":
                        initSettingsFragment(true);
                        break;
                    case "退出":
                        if(MainActivity.sharedPreference.getBoolean("exitsettings")){
                            System.exit(0);
                        }else{
                            finish();
                        }
                        break;
                }
                mDrawerToggle.syncState();
                mDrawerLayout.closeDrawer(mDrawerList);
            }

        });
    }

    private void findViews(){
        rt=(RelativeLayout)findViewById(R.id.right_drawer);
        rightText=(TextView)findViewById(R.id.main_activityTextViewRight);
        mDrawerLayout=(DrawerLayout)findViewById(R.id.drawer_layout);
        mDrawerList=(ListView)findViewById(R.id.navdrawer);
    }

    private void initFragment(){
        manager=getFragmentManager();
        initSecondStepFragment(false);
        initSettingsFragment(false);
        initFirstStepFragment(true);
    }

    private void initFirstStepFragment(boolean showNow){
        FragmentTransaction transactionTestFragment=manager.beginTransaction();
        if(firstStepFragment==null){
            firstStepFragment=new FirstStep();
            transactionTestFragment.add(R.id.main_activityLinearLayout,firstStepFragment);
        }
        hideFragment(transactionTestFragment);
        if(showNow){
            transactionTestFragment.show(firstStepFragment);
        }
        transactionTestFragment.commit();
    }

    private void initSecondStepFragment(boolean showNow){
        FragmentTransaction transactionGifArbAwesomeFragment=manager.beginTransaction();
        if(secondStepFragment==null){
            secondStepFragment=new SecondStep();
            transactionGifArbAwesomeFragment.add(R.id.main_activityLinearLayout,secondStepFragment);
        }
        hideFragment(transactionGifArbAwesomeFragment);
        if(showNow){
            transactionGifArbAwesomeFragment.show(secondStepFragment);
        }
        transactionGifArbAwesomeFragment.commit();
    }

    private void initSettingsFragment(boolean showNow){
        FragmentTransaction transactionSettingsFragment=manager.beginTransaction();
        if(settingsFragmrnt==null){
            settingsFragmrnt=new settings();
            transactionSettingsFragment.add(R.id.main_activityLinearLayout,settingsFragmrnt);
        }
        hideFragment(transactionSettingsFragment);
        if(showNow){
            transactionSettingsFragment.show(settingsFragmrnt);
        }
        transactionSettingsFragment.commit();
    }

    public void hideFragment(FragmentTransaction transaction){
        Fragment fs[]={
                firstStepFragment,
                secondStepFragment,
                settingsFragmrnt
        };
        for(Fragment f : fs){
            if(f!=null){
                transaction.hide(f);
            }
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item){
        if(item.getItemId()==android.R.id.home){
            if(mDrawerLayout.isDrawerOpen(mDrawerList)){
                mDrawerLayout.closeDrawer(mDrawerList);
            }else{
                mDrawerLayout.openDrawer(mDrawerList);
            }
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onPostCreate(Bundle savedInstanceState){
        super.onPostCreate(savedInstanceState);
        mDrawerToggle.syncState();
    }

    @Override
    public void onConfigurationChanged(Configuration newConfig){
        super.onConfigurationChanged(newConfig);
        mDrawerToggle.onConfigurationChanged(newConfig);
    }

    @Override
    public void onResume(){
        super.onResume();
        verifyStoragePermissions(this);
    }

    private void acquireStoragePermissions(){
        int permission=ActivityCompat.checkSelfPermission(this,Manifest.permission.WRITE_EXTERNAL_STORAGE);
        if(permission!=PackageManager.PERMISSION_GRANTED){
            ActivityCompat.requestPermissions(
                    this,
                    PERMISSIONS_STORAGE,
                    REQUEST_EXTERNAL_STORAGE
            );
        }
    }

    public static void verifyStoragePermissions(Activity activity){
        // Check if we have write permission
        int permission=ActivityCompat.checkSelfPermission(activity,Manifest.permission.WRITE_EXTERNAL_STORAGE);
        if(permission!=PackageManager.PERMISSION_GRANTED){
// We don't have permission so prompt the user
            ActivityCompat.requestPermissions(activity,PERMISSIONS_STORAGE,
                    REQUEST_EXTERNAL_STORAGE);
        }
    }

}

