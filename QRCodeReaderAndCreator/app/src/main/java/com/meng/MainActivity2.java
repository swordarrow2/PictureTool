package com.meng;

import android.app.ActionBar;
import android.app.Activity;
import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.content.Intent;
import android.content.res.Configuration;
import android.os.Bundle;
import android.support.v4.widget.DrawerLayout;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import com.meng.qrtools.R;
import com.meng.qrtools.creator.awesomeCreator;
import com.meng.qrtools.creator.creator;
import com.meng.qrtools.lib.materialDesign.ActionBarDrawerToggle;
import com.meng.qrtools.lib.materialDesign.DrawerArrowDrawable;
import com.meng.qrtools.reader.galleryReader;
import com.meng.qrtools.reader.cameraReader;

public class MainActivity2 extends Activity {
    private DrawerLayout mDrawerLayout;
    private ListView mDrawerList;
    private ActionBarDrawerToggle mDrawerToggle;
    private DrawerArrowDrawable drawerArrow;
    private boolean drawerArrowColor;
    private Intent i;

    private creator fragment1;
    private awesomeCreator fragment2;
    private cameraReader fragment3;
    private galleryReader fragment4;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main_activity);
        ActionBar ab = getActionBar();
        ab.setDisplayHomeAsUpEnabled(true);
        ab.setHomeButtonEnabled(true);
        final FragmentManager manager = getFragmentManager();

        mDrawerLayout = (DrawerLayout) findViewById(R.id.drawer_layout);
        mDrawerList = (ListView) findViewById(R.id.navdrawer);
        drawerArrow = new DrawerArrowDrawable(this) {
            @Override
            public boolean isLayoutRtl() {
                return false;
            }
        };
        mDrawerToggle = new ActionBarDrawerToggle(this, mDrawerLayout,
                drawerArrow, R.string.open,
                R.string.close) {

            public void onDrawerClosed(View view) {
                super.onDrawerClosed(view);
                invalidateOptionsMenu();
            }

            public void onDrawerOpened(View drawerView) {
                super.onDrawerOpened(drawerView);
                invalidateOptionsMenu();
            }
        };
        mDrawerLayout.setDrawerListener(mDrawerToggle);
        mDrawerToggle.syncState();

        String[] values = new String[]{
                "读取相册二维码",
                "相机扫描二维码",
                "创建普通二维码",
                "创建Awesome二维码",
                "Share",
                "Rate"
        };
        ArrayAdapter<String> adapter = new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1, android.R.id.text1, values);
        mDrawerList.setAdapter(adapter);
        mDrawerList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
            /*		switch (position) {
						case 0:
							mDrawerToggle.setAnimateEnabled(false);
							drawerArrow.setProgress(1f);
							break;
						case 1:
							mDrawerToggle.setAnimateEnabled(false);
							drawerArrow.setProgress(0f);
							break;
						case 2:
							mDrawerToggle.setAnimateEnabled(true);
							mDrawerToggle.syncState();
							break;
						case 3:
							if (drawerArrowColor) {
								drawerArrowColor = false;
								drawerArrow.setColor(R.color.ldrawer_color);
							} else {
								drawerArrowColor = true;
								drawerArrow.setColor(R.color.drawer_arrow_second_color);
							}
							mDrawerToggle.syncState();
							break;
						case 4:
							Intent browserIntent = new Intent(Intent.ACTION_VIEW, Uri.parse("https://github.com/IkiMuhendis/LDrawer"));
							startActivity(browserIntent);
							break;
						case 5:
							Intent share = new Intent(Intent.ACTION_SEND);
							share.setType("text/plain");
							share.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
							share.putExtra(Intent.EXTRA_SUBJECT,
										   getString(R.string.app_name));
							share.putExtra(Intent.EXTRA_TEXT, "getString(R.string.app_description)" + "\n" +
										   "GitHub Page :  https://github.com/IkiMuhendis/LDrawer\n" +
										   "Sample App : https://play.google.com/store/apps/details?id=" +
										   getPackageName());
							startActivity(Intent.createChooser(share,
															   getString(R.string.app_name)));
							break;
						case 6:
							String appUrl = "https://play.google.com/store/apps/details?id=" + getPackageName();
							Intent rateIntent = new Intent(Intent.ACTION_VIEW, Uri.parse(appUrl));
							startActivity(rateIntent);
							break;
					}
			*/

                switch (position) {
                    case 0:
                        FragmentTransaction transaction4 = manager.beginTransaction();
                        if (fragment4 == null) {
                            fragment4 = new galleryReader();
                            transaction4.add(R.id.main_activityLinearLayout, fragment4);
                        }
                        hideFragment(transaction4);
                        transaction4.show(fragment4);
                        transaction4.commit();
                        i = null;
                        mDrawerToggle.syncState();

                        break;
                    case 1:
                        FragmentTransaction transaction3 = manager.beginTransaction();
                        if (fragment3 == null) {
                            fragment3 = new cameraReader();
                            transaction3.add(R.id.main_activityLinearLayout, fragment3);
                        }
                        hideFragment(transaction3);
                        transaction3.show(fragment3);
                        transaction3.commit();
                        i = null;
                        mDrawerToggle.syncState();
                        break;
                    case 2:
                        FragmentTransaction transaction = manager.beginTransaction();
                        if (fragment1 == null) {
                            fragment1 = new creator();
                            transaction.add(R.id.main_activityLinearLayout, fragment1);
                        }
                        hideFragment(transaction);
                        transaction.show(fragment1);
                        transaction.commit();
                        mDrawerToggle.syncState();
                        i = null;
                        break;
                    case 3:
                        FragmentTransaction transaction2 = manager.beginTransaction();
                        if (fragment2 == null) {
                            fragment2 = new awesomeCreator();
                            transaction2.add(R.id.main_activityLinearLayout, fragment2);
                        }
                        hideFragment(transaction2);
                        transaction2.show(fragment2);
                        transaction2.commit();
                        mDrawerToggle.syncState();
                        i = null;
                        break;
                    case 4:
                        mDrawerToggle.syncState();
                        i = null;
                        break;
                }
                if (i != null) {
                    startActivity(i);
                }
                mDrawerLayout.closeDrawer(mDrawerList);
            }
        });
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            if (mDrawerLayout.isDrawerOpen(mDrawerList)) {
                mDrawerLayout.closeDrawer(mDrawerList);
            } else {
                mDrawerLayout.openDrawer(mDrawerList);
            }
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onPostCreate(Bundle savedInstanceState) {
        super.onPostCreate(savedInstanceState);
        mDrawerToggle.syncState();
    }

    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
        mDrawerToggle.onConfigurationChanged(newConfig);
    }

    private void hideFragment(FragmentTransaction transaction) {
        if (fragment1 != null) {
            transaction.hide(fragment1);
        }
        if (fragment2 != null) {
            transaction.hide(fragment2);
        }
        if (fragment3 != null) {
            transaction.hide(fragment3);
        }
        if (fragment4 != null) {
            transaction.hide(fragment4);
        }
    }

}

