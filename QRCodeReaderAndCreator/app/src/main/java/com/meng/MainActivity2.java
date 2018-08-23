package com.meng;

import android.app.ActionBar;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.Fragment;
import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.res.Configuration;
import android.os.Bundle;
import android.support.v4.widget.DrawerLayout;
import android.view.KeyEvent;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.meng.qrtools.MainActivity;
import com.meng.qrtools.R;
import com.meng.qrtools.creator.arbAwesome;
import com.meng.qrtools.creator.awesomeCreator;
import com.meng.qrtools.creator.gifArbAwesome;
import com.meng.qrtools.creator.gifAwesomeQr;
import com.meng.qrtools.creator.gifCreator;
import com.meng.qrtools.creator.logoCreator;
import com.meng.qrtools.lib.materialDesign.ActionBarDrawerToggle;
import com.meng.qrtools.lib.materialDesign.DrawerArrowDrawable;
import com.meng.qrtools.reader.cameraReader;
import com.meng.qrtools.reader.galleryReader;
import com.meng.qrtools.settings;
import com.meng.qrtools.textFragment;

public class MainActivity2 extends Activity{
    public static MainActivity2 instence;
    private DrawerLayout mDrawerLayout;
    private ListView mDrawerList;
    private RelativeLayout rt;
    private ActionBarDrawerToggle mDrawerToggle;
    private DrawerArrowDrawable drawerArrow;

    private textFragment welcomeFragment;
    private logoCreator logoCreatorFragment;
    public awesomeCreator awesomeCreatorFragment;
    public cameraReader cameraReaderFragment;
    public galleryReader galleryReaderFragment;
    private textFragment aboutFragment;
    private gifAwesomeQr gifFragment;
    private arbAwesome arbAwesome;
    private settings settingsFragment;
    private gifArbAwesome gifArbAwesomeFragment;
    private gifCreator gifCreatorFragment;
    public TextView rightText;

    public FragmentManager manager;

    public static final int SELECT_FILE_REQUEST_CODE=822;

    @Override
    protected void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main_activity);
        instence=this;
        setActionBar();
        findViews();
        initFragment();
        setListener();
        changeTheme();

    }

    public static void selectImage(Fragment f){
        Intent intent=new Intent(Intent.ACTION_OPEN_DOCUMENT);
        intent.addCategory(Intent.CATEGORY_OPENABLE);
        intent.setType("image/*");
        f.startActivityForResult(intent,SELECT_FILE_REQUEST_CODE);
    }

    private void changeTheme(){
        if(MainActivity.sharedPreference.getBoolean("useLightTheme",true)){
            mDrawerList.setBackgroundColor(getResources().getColor(android.R.color.background_light));
            rt.setBackgroundColor(getResources().getColor(android.R.color.background_light));
        }else{
            mDrawerList.setBackgroundColor(getResources().getColor(android.R.color.background_dark));
            rt.setBackgroundColor(getResources().getColor(android.R.color.background_dark));
        }
        if(getIntent().getBooleanExtra("setTheme",false)){
            initSettingsFragment(true);
        }else{
            initWelcome(true);
            if(MainActivity.sharedPreference.getBoolean("opendraw",true)){
                mDrawerLayout.openDrawer(mDrawerList);
            }
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
                "首页(大概)","读取相册二维码","相机扫描二维码","创建普通二维码",
                "创建AwesomeQR","创建动态AwesomeQR","生成gif","关于","设置","退出"
        }));
        mDrawerList.setOnItemClickListener(new AdapterView.OnItemClickListener(){
            @Override
            public void onItemClick(AdapterView<?> parent,View view,int position,long id){
                switch(((TextView)view).getText().toString()){
                    case "首页(?)":
                        initWelcome(true);
                        break;
                    case "读取相册二维码":
                        initGalleryReaderFragment(true);
                        break;
                    case "相机扫描二维码":
                        initCameraReaderFragment(true);
                        break;
                    case "创建普通二维码":
                        initLogoCreatorFragment(true);
                        break;
                    case "创建AwesomeQR":
                        new AlertDialog.Builder(MainActivity2.this)
                                .setTitle("选择添加二维码的方式")
                                .setPositiveButton("普通方式",new DialogInterface.OnClickListener(){
                                    @Override
                                    public void onClick(DialogInterface p1,int p2){
                                        initAwesomeFragment(true);
                                    }
                                }).setNegativeButton("自选位置",new DialogInterface.OnClickListener(){
                            @Override
                            public void onClick(DialogInterface dialog,int which){
                                initArbFragmentFragment(true);
                            }
                        }).show();
                        break;
                    case "创建动态AwesomeQR":
                        new AlertDialog.Builder(MainActivity2.this)
                                .setTitle("选择添加二维码的方式")
                                .setPositiveButton("普通方式",new DialogInterface.OnClickListener(){
                                    @Override
                                    public void onClick(DialogInterface p1,int p2){
                                        initGifAwesomeFragment(true);
                                    }
                                }).setNegativeButton("自选位置",new DialogInterface.OnClickListener(){
                            @Override
                            public void onClick(DialogInterface dialog,int which){
                                initGifArbAwesomeFragment(true);
                            }
                        }).show();
                        break;
                    case "生成gif":

                    case "关于":
                        initAboutFragment(true);
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
        if(MainActivity.sharedPreference.getBoolean("ldgr")){
            initGalleryReaderFragment(false);
        }
        if(MainActivity.sharedPreference.getBoolean("ldcr")){
            initCameraReaderFragment(false);
        }
        if(MainActivity.sharedPreference.getBoolean("ldlgqr")){
            initLogoCreatorFragment(false);
        }
        initAwesomeFragment(false);
        if(MainActivity.sharedPreference.getBoolean("ldgif")){
            initGifAwesomeFragment(false);
        }
        if(MainActivity.sharedPreference.getBoolean("ldaw2")){
            initArbFragmentFragment(false);
        }
        if(MainActivity.sharedPreference.getBoolean("ldaw3")){
            initGifArbAwesomeFragment(false);
        }
        if(MainActivity.sharedPreference.getBoolean("textFragment")){
            initAboutFragment(false);
        }
        if(MainActivity.sharedPreference.getBoolean("settings")){
            initSettingsFragment(false);
        }
    }

    private void initWelcome(boolean showNow){
        FragmentTransaction transactionWelcome=manager.beginTransaction();
        if(welcomeFragment==null){
            welcomeFragment=new textFragment(0);
            transactionWelcome.add(R.id.main_activityLinearLayout,welcomeFragment);
        }
        hideFragment(transactionWelcome);
        if(showNow){
            transactionWelcome.show(welcomeFragment);
        }
        transactionWelcome.commit();
    }

    private void initGalleryReaderFragment(boolean showNow){
        FragmentTransaction transactionGalleryReaderFragment=manager.beginTransaction();
        if(galleryReaderFragment==null){
            galleryReaderFragment=new galleryReader();
            transactionGalleryReaderFragment.add(R.id.main_activityLinearLayout,galleryReaderFragment);
        }
        hideFragment(transactionGalleryReaderFragment);
        if(showNow){
            transactionGalleryReaderFragment.show(galleryReaderFragment);
        }
        transactionGalleryReaderFragment.commit();
    }

    private void initCameraReaderFragment(boolean showNow){
        FragmentTransaction transactionCameraReaderFragment=manager.beginTransaction();
        if(cameraReaderFragment==null){
            cameraReaderFragment=new cameraReader();
            transactionCameraReaderFragment.add(R.id.main_activityLinearLayout,cameraReaderFragment);
        }
        hideFragment(transactionCameraReaderFragment);
        if(showNow){
            transactionCameraReaderFragment.show(cameraReaderFragment);
        }
        transactionCameraReaderFragment.commit();
    }

    private void initLogoCreatorFragment(boolean showNow){
        FragmentTransaction transactionLogoCreatorFragment=manager.beginTransaction();
        if(logoCreatorFragment==null){
            logoCreatorFragment=new logoCreator();
            transactionLogoCreatorFragment.add(R.id.main_activityLinearLayout,logoCreatorFragment);
        }
        hideFragment(transactionLogoCreatorFragment);
        if(showNow){
            transactionLogoCreatorFragment.show(logoCreatorFragment);
        }
        transactionLogoCreatorFragment.commit();
    }

    private void initAwesomeFragment(boolean showNow){
        FragmentTransaction transactionAwesomeCreatorFragment=manager.beginTransaction();
        if(awesomeCreatorFragment==null){
            awesomeCreatorFragment=new awesomeCreator();
            transactionAwesomeCreatorFragment.add(R.id.main_activityLinearLayout,awesomeCreatorFragment);
        }
        hideFragment(transactionAwesomeCreatorFragment);
        if(showNow){
            transactionAwesomeCreatorFragment.show(awesomeCreatorFragment);
        }
        transactionAwesomeCreatorFragment.commit();
    }

    private void initGifAwesomeFragment(boolean showNow){
        FragmentTransaction transactionGifAwesomeCreatorFragment=manager.beginTransaction();
        if(gifFragment==null){
            gifFragment=new gifAwesomeQr();
            transactionGifAwesomeCreatorFragment.add(R.id.main_activityLinearLayout,gifFragment);
        }
        hideFragment(transactionGifAwesomeCreatorFragment);
        if(showNow){
            transactionGifAwesomeCreatorFragment.show(gifFragment);
        }
        transactionGifAwesomeCreatorFragment.commit();
    }

    private void initArbFragmentFragment(boolean showNow){
        FragmentTransaction transactionTestFragment=manager.beginTransaction();
        if(arbAwesome==null){
            arbAwesome=new arbAwesome();
            transactionTestFragment.add(R.id.main_activityLinearLayout,arbAwesome);
        }
        hideFragment(transactionTestFragment);
        if(showNow){
            transactionTestFragment.show(arbAwesome);
        }
        transactionTestFragment.commit();
    }

    private void initGifArbAwesomeFragment(boolean showNow){
        FragmentTransaction transactionGifArbAwesomeFragment=manager.beginTransaction();
        if(gifArbAwesomeFragment==null){
            gifArbAwesomeFragment=new gifArbAwesome();
            transactionGifArbAwesomeFragment.add(R.id.main_activityLinearLayout,gifArbAwesomeFragment);
        }
        hideFragment(transactionGifArbAwesomeFragment);
        if(showNow){
            transactionGifArbAwesomeFragment.show(gifArbAwesomeFragment);
        }
        transactionGifArbAwesomeFragment.commit();
    }

    private void initGifFragment(boolean showNow){
        FragmentTransaction transactionAboutFragment=manager.beginTransaction();
        if(aboutFragment==null){
            aboutFragment=new textFragment(1);
            transactionAboutFragment.add(R.id.main_activityLinearLayout,aboutFragment);
        }
        hideFragment(transactionAboutFragment);
        if(showNow){
            transactionAboutFragment.show(aboutFragment);
        }
        transactionAboutFragment.commit();
    }
    private void initAboutFragment(boolean showNow){
        FragmentTransaction transactionAboutFragment=manager.beginTransaction();
        if(aboutFragment==null){
            aboutFragment=new textFragment(1);
            transactionAboutFragment.add(R.id.main_activityLinearLayout,aboutFragment);
        }
        hideFragment(transactionAboutFragment);
        if(showNow){
            transactionAboutFragment.show(aboutFragment);
        }
        transactionAboutFragment.commit();
    }

    private void initSettingsFragment(boolean showNow){
        FragmentTransaction transactionsettings=manager.beginTransaction();
        if(settingsFragment==null){
            settingsFragment=new settings();
            transactionsettings.add(R.id.main_activityLinearLayout,settingsFragment);
        }
        hideFragment(transactionsettings);
        if(showNow){
            transactionsettings.show(settingsFragment);
        }
        transactionsettings.commit();
    }

    public void hideFragment(FragmentTransaction transaction){
        Fragment fs[]={
                welcomeFragment,
                logoCreatorFragment,
                awesomeCreatorFragment,
                gifFragment,
                cameraReaderFragment,
                galleryReaderFragment,
                arbAwesome,
                gifArbAwesomeFragment,
                aboutFragment,
                settingsFragment
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
    public boolean onKeyDown(int keyCode,KeyEvent event){
        // TODO: Implement this method
        if(keyCode==KeyEvent.KEYCODE_BACK||keyCode==KeyEvent.KEYCODE_MENU){
            if(mDrawerLayout.isDrawerOpen(mDrawerList)){
                mDrawerLayout.closeDrawer(mDrawerList);
            }else{
                mDrawerLayout.openDrawer(mDrawerList);
            }
            return true;
        }
        return super.onKeyDown(keyCode,event);
    }


}

