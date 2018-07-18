package com.meng;

import android.app.*;
import android.content.*;
import android.content.res.*;
import android.net.*;
import android.os.*;
import android.support.v4.widget.*;
import android.view.*;
import android.widget.*;
import com.ikimuhendis.ldrawer.*;
import com.meng.qrtools.*;
import com.meng.qrtools.creator.*;
import com.meng.qrtools.reader.*;




public class MainActivity2 extends Activity {
    private DrawerLayout mDrawerLayout;
    private ListView mDrawerList;
    private ActionBarDrawerToggle mDrawerToggle;
    private DrawerArrowDrawable drawerArrow;
    private boolean drawerArrowColor;
	private TextView description;
	
	private Intent i=new Intent();
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main_activity);
        ActionBar ab = getActionBar();
        ab.setDisplayHomeAsUpEnabled(true);
        ab.setHomeButtonEnabled(true);

        mDrawerLayout = (DrawerLayout) findViewById(R.id.drawer_layout);
        mDrawerList = (ListView) findViewById(R.id.navdrawer);
		description=(TextView)findViewById(R.id.main_activityTextView);

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
            "Stop Animation (Back icon)",
            "读取相册二维码",
            "相机扫描二维码",
            "创建普通二维码",
            "创建Awesome二维码",
            "Share",
            "Rate"
        };
        ArrayAdapter<String> adapter = new ArrayAdapter<String>(this,
																android.R.layout.simple_list_item_1, android.R.id.text1, values);
        mDrawerList.setAdapter(adapter);
        mDrawerList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
				@Override
				public void onItemClick(AdapterView<?> parent,View view,int position,long id){
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
					
					switch(position){
						case 0:
							//MainActivity.this.setTheme(R.style.ActionBar_1);
							//i.setClass(MainActivity.this,loginAndRegister.class);
							//mDrawerToggle.syncState();
							description.setText("无意义");
							break;
						case 1:

							//View v = inflater.inflate(R.layout.main_list_header,null);
							//	ImageView iv=(ImageView) v.findViewById(R.id.main_list_headerImageView);
							//	TextView tv2=(TextView) v.findViewById(R.id.main_list_headerTextView);
							//	iv.setOnClickListener(onc);
							//	tv2.setOnClickListener(onc);
							//	rl.removeAllViews();
							//	rl.addView(v);
							//	overridePendingTransition(android.R.anim.slide_in_left,android.R.anim.slide_out_right);
							//	drawerLayoutList.addFooterView(v);
							description.setText("读取相册文件");
							i.setClass(MainActivity2.this,galleryReader .class);
							mDrawerToggle.syncState();
							break;
						case 2:
							description.setText("使用相机扫描");
							i.setClass(MainActivity2.this,cameraReader.class);
							mDrawerToggle.syncState();
							break;
						case 3:
							description.setText("创建普通二维码");
							i.setClass(MainActivity2.this,creator.class);
							mDrawerToggle.syncState();
							break;
						case 4:
							description.setText("创建AwesomeQR码");
							i.setClass(MainActivity2.this,awesomeCreator.class);
							mDrawerToggle.syncState();
							break;
						case 5:
							i.setAction(Intent.ACTION_VIEW);
							i.setData(Uri.parse("https://github.com/IkiMuhendis/LDrawer"));

							break;
						case 6:
							i.setAction(Intent.ACTION_SEND);
							i.setType("text/plain");
							i.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
							i.putExtra(Intent.EXTRA_SUBJECT,
									   getString(R.string.app_name));
							i.putExtra(Intent.EXTRA_TEXT,"getString(R.string.app_description)"+"\n"+
									   "GitHub Page :  https://github.com/IkiMuhendis/LDrawer\n"+
									   "Sample App : https://play.google.com/store/apps/details?id="+
									   getPackageName());
							//startActivity(Intent.createChooser(share,getString(R.string.app_name)));
							break;
						case 7:
							String appUrl = "https://play.google.com/store/apps/details?id="+getPackageName();
							i.setAction(Intent.ACTION_VIEW);
							i.setData(Uri.parse(appUrl));

							break;
					}
					
					startActivity(i);
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
}

