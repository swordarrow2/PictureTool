package com.meng;

import android.app.*;
import android.content.res.*;
import android.os.*;
import android.support.v4.widget.*;
import android.view.*;
import android.widget.*;
import com.meng.qrtools.*;
import com.meng.qrtools.creator.*;
import com.meng.qrtools.lib.materialDesign.*;
import com.meng.qrtools.reader.*;

public class MainActivity2 extends Activity{
    public static MainActivity2 instence;
    private final String logString = "这都被发现了(\n感谢岁41发现了一个玄学问题(\n并且已经修正\n大概吧(\n以下为操作记录：\n";
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

    private settings settingsFragment;
    public static TextView rightText;

    public FragmentManager manager;

    @Override
    protected void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main_activity);
        instence=this;
        setActionBar();
        findViews();
		rightText.setText(logString);
		initFragment();
        setListener();
        changeTheme();

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
        if(MainActivity.sharedPreference.getBoolean("useLightTheme",true)){
            super.setTheme(R.style.AppThemeLight);
        }else{
            super.setTheme(R.style.AppThemeDark);
        }
    }

    private void setActionBar(){
        ActionBar ab = getActionBar();
        ab.setDisplayHomeAsUpEnabled(true);
        ab.setHomeButtonEnabled(true);
    }

    private void setListener(){
        drawerArrow=new DrawerArrowDrawable(this) {
            @Override
            public boolean isLayoutRtl(){
                return false;
            }
        };
        mDrawerToggle=new ActionBarDrawerToggle(this,mDrawerLayout,drawerArrow,R.string.open,R.string.close) {

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
															"首页(?)","读取相册二维码","相机扫描二维码","创建二维码",
															"创建Awesome二维码","创建动态Awesome二维码","关于","设置","退出"
														}));
        mDrawerList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
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
						case "创建二维码":
							initLogoCreatorFragment(true);
							break;
						case "创建Awesome二维码":
							initAwesomeFragment(true);
							break;
						case "创建动态Awesome二维码":
							initGifAwesomeFragment(true);
							break;
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
        rt=(RelativeLayout) findViewById(R.id.right_drawer);
        rightText=(TextView) findViewById(R.id.main_activityTextViewRight);
        mDrawerLayout=(DrawerLayout) findViewById(R.id.drawer_layout);
        mDrawerList=(ListView) findViewById(R.id.navdrawer);
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
		initGifAwesomeFragment(false);
        if(MainActivity.sharedPreference.getBoolean("textFragment")){
            initAboutFragment(false);
        }
        if(MainActivity.sharedPreference.getBoolean("settings")){
            initSettingsFragment(false);
        }
    }

    private void initWelcome(boolean showNow){
        FragmentTransaction transactionWelcome = manager.beginTransaction();
        if(welcomeFragment==null){
            welcomeFragment=new textFragment(0);
            transactionWelcome.add(R.id.main_activityLinearLayout,welcomeFragment);
        }
        hideFragment(transactionWelcome);
        if(showNow){
            transactionWelcome.show(welcomeFragment);
            log.c(this,"welcome");
        }else{
            log.i(this,"initWelcome");
        }
        transactionWelcome.commit();
    }

    private void initGalleryReaderFragment(boolean showNow){
        FragmentTransaction transactionGalleryReaderFragment = manager.beginTransaction();
        if(galleryReaderFragment==null){
            galleryReaderFragment=new galleryReader();
            transactionGalleryReaderFragment.add(R.id.main_activityLinearLayout,galleryReaderFragment);
        }
        hideFragment(transactionGalleryReaderFragment);
        if(showNow){
            transactionGalleryReaderFragment.show(galleryReaderFragment);
            log.c(this,"galleryReader");
        }else{
            log.i(this,"initGalleryReaderFragment");
        }
        transactionGalleryReaderFragment.commit();
    }

    private void initCameraReaderFragment(boolean showNow){
        FragmentTransaction transactionCameraReaderFragment = manager.beginTransaction();
        if(cameraReaderFragment==null){
            cameraReaderFragment=new cameraReader();
            transactionCameraReaderFragment.add(R.id.main_activityLinearLayout,cameraReaderFragment,"cameraReader");
        }
        hideFragment(transactionCameraReaderFragment);
        if(showNow){
            transactionCameraReaderFragment.show(cameraReaderFragment);
            log.c(this,"CameraReader");
        }else{
            log.i(this,"initCameraReaderFragment");
        }
        transactionCameraReaderFragment.commit();
    }

    private void initLogoCreatorFragment(boolean showNow){
        FragmentTransaction transactionLogoCreatorFragment = manager.beginTransaction();
        if(logoCreatorFragment==null){
            logoCreatorFragment=new logoCreator();
            transactionLogoCreatorFragment.add(R.id.main_activityLinearLayout,logoCreatorFragment);
        }
        hideFragment(transactionLogoCreatorFragment);
        if(showNow){
            transactionLogoCreatorFragment.show(logoCreatorFragment);
            log.c(this,"LogoCreator");
        }else{
            log.i(this,"initLogoCreatorFragment");
        }
        transactionLogoCreatorFragment.commit();
    }

    private void initAwesomeFragment(boolean showNow){
        FragmentTransaction transactionAwesomeCreatorFragment = manager.beginTransaction();
        if(awesomeCreatorFragment==null){
            awesomeCreatorFragment=new awesomeCreator();
            transactionAwesomeCreatorFragment.add(R.id.main_activityLinearLayout,awesomeCreatorFragment);
        }
        hideFragment(transactionAwesomeCreatorFragment);
        if(showNow){
            transactionAwesomeCreatorFragment.show(awesomeCreatorFragment);
            log.c(this,"AwesomeQR");
        }else{
            log.i(this,"initAwesomeFragment");
        }
        transactionAwesomeCreatorFragment.commit();
    }

	private void initGifAwesomeFragment(boolean showNow){
        FragmentTransaction transactionGifAwesomeCreatorFragment = manager.beginTransaction();
        if(gifFragment==null){
            gifFragment=new gifAwesomeQr();
            transactionGifAwesomeCreatorFragment.add(R.id.main_activityLinearLayout,gifFragment);
        }
        hideFragment(transactionGifAwesomeCreatorFragment);
        if(showNow){
            transactionGifAwesomeCreatorFragment.show(gifFragment);
            log.c(this,"gifAwesomeQR");
        }else{
            log.i(this,"initGifAwesomeFragment");
        }
        transactionGifAwesomeCreatorFragment.commit();
    }

    private void initAboutFragment(boolean showNow){
        FragmentTransaction transactionAboutFragment = manager.beginTransaction();
        if(aboutFragment==null){
            aboutFragment=new textFragment(1);
            transactionAboutFragment.add(R.id.main_activityLinearLayout,aboutFragment);
        }
        hideFragment(transactionAboutFragment);
        if(showNow){
            transactionAboutFragment.show(aboutFragment);
            log.c(this,"About");
        }else{
            log.i(this,"initAboutFragment");
        }
        transactionAboutFragment.commit();
    }

    private void initSettingsFragment(boolean showNow){
        FragmentTransaction transactionsettings = manager.beginTransaction();
        if(settingsFragment==null){
            settingsFragment=new settings();
            transactionsettings.add(R.id.main_activityLinearLayout,settingsFragment);
        }
        hideFragment(transactionsettings);
        if(showNow){
            transactionsettings.show(settingsFragment);
            log.c(this,"settings");
        }else{
            log.i(this,"initSettingsFragment");
        }
        transactionsettings.commit();
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

    public void hideFragment(FragmentTransaction transaction){
        Fragment fs[] = {
			welcomeFragment,
			logoCreatorFragment,
			awesomeCreatorFragment,
			gifFragment,
			cameraReaderFragment,
			galleryReaderFragment,
			aboutFragment,
			settingsFragment
        };
        for(Fragment f : fs){
            if(f!=null){
                transaction.hide(f);
            }
        }
    }

}

