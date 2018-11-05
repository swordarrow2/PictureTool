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


import com.meng.qrtools.settings;
import com.meng.qrtools.lib.materialDesign.*;
import com.meng.tencos.*;
import com.meng.tencos.ui.*;


public class MainActivity2 extends Activity{
    public static MainActivity2 instence;
    private DrawerLayout mDrawerLayout;
    private ListView mDrawerList;
    private RelativeLayout rt;
    private ActionBarDrawerToggle mDrawerToggle;
    private DrawerArrowDrawable drawerArrow;

    private settings settingsFragment;
	private MainList listFragment;

    public TextView rightText;

    public FragmentManager manager;


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


    private void changeTheme(){
        if(BizService.sharedPreference.getBoolean("useLightTheme",true)){
            mDrawerList.setBackgroundColor(getResources().getColor(android.R.color.background_light));
            rt.setBackgroundColor(getResources().getColor(android.R.color.background_light));
        }else{
            mDrawerList.setBackgroundColor(getResources().getColor(android.R.color.background_dark));
            rt.setBackgroundColor(getResources().getColor(android.R.color.background_dark));
        }
        if(getIntent().getBooleanExtra("setTheme",false)){
            initSettingsFragment(true);
        }else{
            initListFragment(true);
            if(BizService.sharedPreference.getBoolean("opendraw",true)){
                mDrawerLayout.openDrawer(mDrawerList);
            }
        }
    }

    @Override
    public void setTheme(int resid){
        if(MainView.lightTheme){
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
															"文件","设置","退出"
														}));
        mDrawerList.setOnItemClickListener(new AdapterView.OnItemClickListener(){
				@Override
				public void onItemClick(AdapterView<?> parent,View view,int position,long id){
					switch(((TextView)view).getText().toString()){
						case "文件":
							initListFragment(true);
							break;
						case "设置":
							initSettingsFragment(true);
							break;
						case "退出":
							if(BizService.sharedPreference.getBoolean("exitsettings")){
								System.exit(0);
							}else{
								finish();
							}
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
		initListFragment(true);
        initSettingsFragment(false);
		
    }
	private void initListFragment(boolean showNow){
        FragmentTransaction transactionsettings=manager.beginTransaction();
        if(listFragment==null){
            listFragment=new MainList();
            transactionsettings.add(R.id.main_activityLinearLayout,listFragment);
        }
        hideFragment(transactionsettings);
        if(showNow){
            transactionsettings.show(listFragment);
        }
        transactionsettings.commit();
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
			listFragment,
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

        if(keyCode==KeyEvent.KEYCODE_BACK||keyCode==KeyEvent.KEYCODE_MENU){
			if(listFragment.onBackPressed()){
				return true;
			}
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


