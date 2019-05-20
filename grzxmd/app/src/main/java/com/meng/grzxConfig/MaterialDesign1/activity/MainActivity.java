package com.meng.grzxConfig.MaterialDesign1.activity;

import android.*;
import android.content.*;
import android.content.pm.*;
import android.net.*;
import android.os.*;
import android.support.design.widget.*;
import android.support.v4.app.*;
import android.support.v4.content.*;
import android.support.v4.view.*;
import android.support.v4.widget.*;
import android.support.v7.app.*;
import android.support.v7.widget.*;
import android.view.*;
import com.meng.grzxConfig.MaterialDesign.fragment.*;
import com.meng.grzxConfig.MaterialDesign1.*;

import android.support.v7.app.ActionBarDrawerToggle;
import com.meng.grzxConfig.MaterialDesign1.R;

public class MainActivity extends AppCompatActivity {

	public static MainActivity instence;
    private DrawerLayout mDrawerLayout;

    public boolean onWifi = false;
    public HomeFragment homeFragment;
	public MenusFragment menusFragment;
	public ProgressFragment progressFragment;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main_activity);
		instence = this;
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
	    setSupportActionBar(toolbar);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            // 权限是否已经 授权 GRANTED---授权  DINIED---拒绝
            if (ContextCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
                new AlertDialog.Builder(this)
				  .setTitle("权限申请")
				  .setMessage("本软件需要存储权限用于部分数据存储")
				  .setPositiveButton("我知道了", new DialogInterface.OnClickListener() {
					  @Override
					  public void onClick(DialogInterface dialog, int which) {
						  ActivityCompat.requestPermissions(MainActivity.this, new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, 1);
						}
					}).setCancelable(false).show();
			  }
		  }
        mDrawerLayout = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
		  this, mDrawerLayout, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        mDrawerLayout.setDrawerListener(toggle);
        toggle.syncState();

        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(navigationItemSelectedListener);
		initHomeFragment(true);
		//initProgressFragment(false);
		//initMenuFragment(false);

		ConnectivityManager connMgr = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo wifiNetworkInfo = connMgr.getNetworkInfo(ConnectivityManager.TYPE_WIFI);
        onWifi = wifiNetworkInfo.isConnected();
		mDrawerLayout.openDrawer(GravityCompat.START);
        navigationView.setCheckedItem(R.id.home);
	  }

    NavigationView.OnNavigationItemSelectedListener navigationItemSelectedListener = new NavigationView.OnNavigationItemSelectedListener() {
        @Override
        public boolean onNavigationItemSelected(MenuItem item) {
            mDrawerLayout.closeDrawer(GravityCompat.START);
			//   Fragment fragment = null;
			//    final FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
            switch (item.getItemId()) {
                case R.id.home:
				  //	  fragment = new HomeFragment();
				  initHomeFragment(true);
				  break;
                case R.id.menus:
				  initMenuFragment(true);
				  //	  fragment = new MenusFragment();
				  break;
                case R.id.progress:
				  initProgressFragment(true);
				  //	  fragment = new ProgressFragment();
				  break;
				case R.id.group_config:
				  //	  initGroupConfigFragment(true);
				  break;
				case R.id.qq_not_reply:
				  //	  initQQFragment(true);
				  break;
				case R.id.word_not_reply:
				  //	  initWordFragment(true);
				  break;
				case R.id.accounts:
				  //	  initPersonFragment(true);
				  break;
			  }

			//    ft.replace(R.id.fragment, fragment).commit();
            return true;
		  }
	  };

	public void initHomeFragment(boolean showNow) {
        FragmentTransaction transactionWelcome = getSupportFragmentManager().beginTransaction();
        if (homeFragment == null) {
            homeFragment = new HomeFragment();
            transactionWelcome.add(R.id.fragment, homeFragment);
		  }
        hideFragment(transactionWelcome);
        if (showNow) {
            transactionWelcome.show(homeFragment);
		  }
        transactionWelcome.commit();
	  } 
	public void initMenuFragment(boolean showNow) {
        FragmentTransaction transactionWelcome = getSupportFragmentManager().beginTransaction();
        if (menusFragment == null) {
            menusFragment = new MenusFragment();
            transactionWelcome.add(R.id.fragment, menusFragment);
		  }
        hideFragment(transactionWelcome);
        if (showNow) {
            transactionWelcome.show(menusFragment);
		  }
        transactionWelcome.commit();
	  } 

	public void initProgressFragment(boolean showNow) {
        FragmentTransaction transactionWelcome = getSupportFragmentManager().beginTransaction();
        if (progressFragment == null) {
            progressFragment = new ProgressFragment();
            transactionWelcome.add(R.id.fragment, progressFragment);
		  }
        hideFragment(transactionWelcome);
        if (showNow) {
            transactionWelcome.show(progressFragment);
		  }
        transactionWelcome.commit();
	  } 

	public void hideFragment(FragmentTransaction transaction) {
        Fragment fs[] = {            
			homeFragment,
			menusFragment,
			progressFragment
		  };
        for (Fragment f : fs) {
            if (f != null) {
                transaction.hide(f);
			  }
		  }
	  }

	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		if (keyCode == KeyEvent.KEYCODE_BACK) {
		  mDrawerLayout.openDrawer(GravityCompat.START);
			return true;
		  } else {
			return false;
		  }
		//	return super.onKeyDown(keyCode, event);
	  }

  }

