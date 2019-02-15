package com.meng;

import android.app.*;
import android.content.*;
import android.content.res.*;
import android.os.*;
import android.support.v4.widget.*;
import android.view.*;
import android.widget.*;
import com.meng.qrtools.creator.*;
import com.meng.qrtools.lib.materialDesign.*;
import com.meng.qrtools.reader.*;
import com.meng.qrtools2.*;

public class MainActivity2 extends Activity{
    public static MainActivity2 instence;
    private DrawerLayout mDrawerLayout;
    private ListView mDrawerList;
    private RelativeLayout rt;
    private ActionBarDrawerToggle mDrawerToggle;
    private DrawerArrowDrawable drawerArrow;
    private pictureEncry pictureEncryFragment;
    public TextView rightText;
    private pictureDecry pictureDecryFragment;

    public FragmentManager manager;

    public static final int SELECT_FILE_REQUEST_CODE = 822;

    @Override
    protected void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main_activity);
        instence=this;
        setActionBar();
        findViews();
        initFragment();
        setListener();
	  }

    public static void selectImage(Fragment f){
        Intent intent = new Intent(Intent.ACTION_OPEN_DOCUMENT);
        intent.addCategory(Intent.CATEGORY_OPENABLE);
        intent.setType("image/*");
        f.startActivityForResult(intent,SELECT_FILE_REQUEST_CODE);
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
															"加密", "解密"}
														));
        mDrawerList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
			  @Override
			  public void onItemClick(AdapterView<?> parent,View view,int position,long id){
				  switch(((TextView) view).getText().toString()){          
					  case "加密":
                        initBusFragment(true);
                        break;
					  case "解密":
                        initBusRFragment(true);
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
		initBusRFragment(false);
		initBusFragment(true);	
	  }

    private void initBusFragment(boolean showNow){
        FragmentTransaction transactionBus = manager.beginTransaction();
        if(pictureEncryFragment==null){
            pictureEncryFragment=new pictureEncry();
            transactionBus.add(R.id.main_activityLinearLayout,pictureEncryFragment);
		  }
        hideFragment(transactionBus);
        if(showNow){
            transactionBus.show(pictureEncryFragment);
		  }
        transactionBus.commit();
	  }

    private void initBusRFragment(boolean showNow){
        FragmentTransaction transactionBusR = manager.beginTransaction();
        if(pictureDecryFragment==null){
            pictureDecryFragment=new pictureDecry();
            transactionBusR.add(R.id.main_activityLinearLayout,pictureDecryFragment);
		  }
        hideFragment(transactionBusR);
        if(showNow){
            transactionBusR.show(pictureDecryFragment);
		  }
        transactionBusR.commit();
	  }

    public void hideFragment(FragmentTransaction transaction){
        Fragment fs[] = {
			pictureEncryFragment,
			pictureDecryFragment
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

