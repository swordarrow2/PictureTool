package com.meng.picTools;

import android.app.*;
import android.content.*;
import android.content.res.*;
import android.os.*;
import android.support.v4.widget.*;
import android.view.*;
import android.widget.*;

import com.meng.picTools.pixivPictureDownloader.Data;
import com.meng.picTools.pixivPictureDownloader.PixivDownloadMain;
import com.meng.picTools.qrtools.*;
import com.meng.picTools.qrtools.creator.*;
import com.meng.picTools.qrtools.lib.SharedPreferenceHelper;
import com.meng.picTools.qrtools.lib.materialDesign.*;
import com.meng.picTools.qrtools.reader.*;

public class MainActivity2 extends Activity {
    public static MainActivity2 instence;
    private DrawerLayout mDrawerLayout;
    private ListView mDrawerList;
    private RelativeLayout rt;
    private ActionBarDrawerToggle mDrawerToggle;
    private DrawerArrowDrawable drawerArrow;

    private Welcome welcomeFragment;
    private logoCreator logoCreatorFragment;
    public awesomeCreator awesomeCreatorFragment;
    public cameraReader cameraReaderFragment;
    public galleryReader galleryReaderFragment;
    private gifAwesomeQr gifAwesomeFragment;
    private arbAwesome arbAwesomeFragment;
    private gifCreator gifCreatorFragment;
    private SettingsPreference settingsFragment;
    private gifArbAwesome gifArbAwesomeFragment;
    private BusCodeCreator busCodeCreatorFragment;
    private BusCodeReader busCodeReaderFragment;
    private pictureEncry pictureEncryFragment;
    private pictureDecry pictureDecryFragment;
    private PixivDownloadMain pixivDownloadMainFragment;

    public FragmentManager manager;
    public TextView rightText;

    public static final int SELECT_FILE_REQUEST_CODE = 822;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main_activity);
        instence = this;
        findViews();
        initFragment();
        setActionBar();
        setListener();
        changeTheme();

    }

    public static void selectImage(Fragment f) {
        Intent intent = new Intent(Intent.ACTION_OPEN_DOCUMENT);
        intent.addCategory(Intent.CATEGORY_OPENABLE);
        intent.setType("image/*");
        f.startActivityForResult(intent, SELECT_FILE_REQUEST_CODE);
    }

    private void changeTheme() {
        if (SharedPreferenceHelper.getBoolean("useLightTheme", true)) {
            mDrawerList.setBackgroundColor(getResources().getColor(android.R.color.background_light));
            rt.setBackgroundColor(getResources().getColor(android.R.color.background_light));
        } else {
            mDrawerList.setBackgroundColor(getResources().getColor(android.R.color.background_dark));
            rt.setBackgroundColor(getResources().getColor(android.R.color.background_dark));
        }
        if (getIntent().getBooleanExtra("setTheme", false)) {
            showSettingsFragment(true);
        } else {
            initWelcome(true);
            if (SharedPreferenceHelper.getBoolean("opendraw", true)) {
                mDrawerLayout.openDrawer(mDrawerList);
            }
        }
    }

    private void setActionBar() {
        ActionBar ab = getActionBar();
        ab.setDisplayHomeAsUpEnabled(true);
        ab.setHomeButtonEnabled(true);
    }

    @Override
    public void setTheme(int resid) {
        if (MainActivity.lightTheme) {
            super.setTheme(R.style.AppThemeLight);
        } else {
            super.setTheme(R.style.AppThemeDark);
        }
    }

    private void setListener() {
        drawerArrow = new DrawerArrowDrawable(this) {
            @Override
            public boolean isLayoutRtl() {
                return false;
            }
        };
        mDrawerToggle = new ActionBarDrawerToggle(this, mDrawerLayout, drawerArrow, R.string.open, R.string.close) {

            public void onDrawerClosed(View view) {
                super.onDrawerClosed(view);
                invalidateOptionsMenu();
            }

            public void onDrawerOpened(View drawerView) {
                super.onDrawerOpened(drawerView);
                invalidateOptionsMenu();
            }

            @Override
            public void onDrawerSlide(View drawerView, float slideOffset) {
                super.onDrawerSlide(drawerView, slideOffset);
            }
        };
        mDrawerLayout.setDrawerListener(mDrawerToggle);
        mDrawerToggle.syncState();
        mDrawerList.setAdapter(new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1, android.R.id.text1, new String[]{
                "首页(大概)", "读取二维码", "创建二维码",
                "乘车码", "图片加密解密", "生成gif",
                "Pixiv图片下载", "设置", "退出"
        }));
        mDrawerList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                switch (((TextView) view).getText().toString()) {
                    case "首页(大概)":
                        initWelcome(true);
                        break;
                    case "读取二维码":
                        new AlertDialog.Builder(MainActivity2.this)
                                .setTitle("读取方式")
                                .setPositiveButton("从相册", new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface p1, int p2) {
                                        showGalleryReaderFragment(true);
                                    }
                                }).setNegativeButton("从相机", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                showCameraReaderFragment(true);
                            }
                        }).show();
                        break;
                    case "创建二维码":
                        new AlertDialog.Builder(MainActivity2.this)
                                .setTitle("要创建的二维码类型")
                                .setPositiveButton("普通二维码", new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface p1, int p2) {
                                        showLogoCreatorFragment(true);
                                    }
                                }).setNegativeButton("Awesome二维码", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                new AlertDialog.Builder(MainActivity2.this)
                                        .setTitle("选择类型")
                                        .setPositiveButton("静态", new DialogInterface.OnClickListener() {
                                            @Override
                                            public void onClick(DialogInterface p1, int p2) {
                                                new AlertDialog.Builder(MainActivity2.this)
                                                        .setTitle("选择添加二维码的方式")
                                                        .setPositiveButton("普通方式", new DialogInterface.OnClickListener() {
                                                            @Override
                                                            public void onClick(DialogInterface p1, int p2) {
                                                                showAwesomeFragment(true);
                                                            }
                                                        }).setNegativeButton("自选位置", new DialogInterface.OnClickListener() {
                                                    @Override
                                                    public void onClick(DialogInterface dialog, int which) {
                                                        showArbFragmentFragment(true);
                                                    }
                                                }).show();
                                            }
                                        }).setNegativeButton("动态", new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialog, int which) {
                                        new AlertDialog.Builder(MainActivity2.this)
                                                .setTitle("选择添加二维码的方式")
                                                .setPositiveButton("普通方式", new DialogInterface.OnClickListener() {
                                                    @Override
                                                    public void onClick(DialogInterface p1, int p2) {
                                                        showGifAwesomeFragment(true);
                                                    }
                                                }).setNegativeButton("自选位置", new DialogInterface.OnClickListener() {
                                            @Override
                                            public void onClick(DialogInterface dialog, int which) {
                                                showGifArbAwesomeFragment(true);
                                            }
                                        }).show();
                                    }
                                }).show();
                            }
                        }).show();
                        break;
                    case "乘车码":
                        new AlertDialog.Builder(MainActivity2.this)
                                .setTitle("选择功能")
                                .setPositiveButton("生成", new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface p1, int p2) {
                                        showBusFragment(true);
                                    }
                                }).setNegativeButton("读取", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                showBusRFragment(true);
                            }
                        }).show();
                        break;
                    case "图片加密解密":
                        new AlertDialog.Builder(MainActivity2.this)
                                .setTitle("图片加密解密")
                                .setPositiveButton("加密", new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface p1, int p2) {
                                        showPicEncryFragment(true);
                                    }
                                }).setNegativeButton("解密", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                showPicDecryFragment(true);
                            }
                        }).show();
                        break;
                    case "生成gif":
                        showGifFragment(true);
                        break;
                    case "设置":
                        showSettingsFragment(true);
                        break;
                    case "Pixiv图片下载":
                        showPixivDownloadFragment(true);
                        break;
                    case "退出":
                        if (SharedPreferenceHelper.getBoolean("exitsettings")) {
                            System.exit(0);
                        } else {
                            finish();
                        }
                        break;
                }
                mDrawerToggle.syncState();
                mDrawerLayout.closeDrawer(mDrawerList);
            }

        });
    }

    private void findViews() {
        rt = (RelativeLayout) findViewById(R.id.right_drawer);
        rightText = (TextView) findViewById(R.id.main_activityTextViewRight);
        mDrawerLayout = (DrawerLayout) findViewById(R.id.drawer_layout);
        mDrawerList = (ListView) findViewById(R.id.navdrawer);
    }

    private void initFragment() {
        manager = getFragmentManager();
        if (SharedPreferenceHelper.getBoolean(Data.preferenceKeys.loadGalleryReader)) {
            showGalleryReaderFragment(false);
        }
        if (SharedPreferenceHelper.getBoolean(Data.preferenceKeys.loadCameraReader)) {
            showCameraReaderFragment(false);
        }
        if (SharedPreferenceHelper.getBoolean("ldlgqr")) {
            showLogoCreatorFragment(false);
        }
        showAwesomeFragment(false);
        if (SharedPreferenceHelper.getBoolean("ldgif")) {
            showGifAwesomeFragment(false);
        }
        if (SharedPreferenceHelper.getBoolean("ldaw2")) {
            showArbFragmentFragment(false);
        }
        if (SharedPreferenceHelper.getBoolean("ldaw3")) {
            showGifArbAwesomeFragment(false);
        }
        if (SharedPreferenceHelper.getBoolean("ldgif")) {
            showGifFragment(false);
        }
        if (SharedPreferenceHelper.getBoolean("SettingsPreference")) {
            showSettingsFragment(false);
        }
        if (SharedPreferenceHelper.getBoolean("BusCodeCreator")) {
            showBusFragment(false);
        }
        if (SharedPreferenceHelper.getBoolean("BusCodeReader")) {
            showBusRFragment(false);
        }
        if (SharedPreferenceHelper.getBoolean("pice")) {
            showPicEncryFragment(false);
        }
        if (SharedPreferenceHelper.getBoolean("picd")) {
            showPicDecryFragment(false);
        }
        if (SharedPreferenceHelper.getBoolean("loadPixivDownload")) {
            showPixivDownloadFragment(false);
        }
    }

    private void initWelcome(boolean showNow) {
        FragmentTransaction transactionWelcome = manager.beginTransaction();
        if (welcomeFragment == null) {
            welcomeFragment = new Welcome();
            transactionWelcome.add(R.id.main_activityLinearLayout, welcomeFragment);
        }
        hideFragment(transactionWelcome);
        if (showNow) {
            transactionWelcome.show(welcomeFragment);
        }
        transactionWelcome.commit();
    }

    private void showGalleryReaderFragment(boolean showNow) {
        FragmentTransaction transactionGalleryReaderFragment = manager.beginTransaction();
        if (galleryReaderFragment == null) {
            galleryReaderFragment = new galleryReader();
            transactionGalleryReaderFragment.add(R.id.main_activityLinearLayout, galleryReaderFragment);
        }
        hideFragment(transactionGalleryReaderFragment);
        if (showNow) {
            transactionGalleryReaderFragment.show(galleryReaderFragment);
        }
        transactionGalleryReaderFragment.commit();
    }

    private void showCameraReaderFragment(boolean showNow) {
        FragmentTransaction transactionCameraReaderFragment = manager.beginTransaction();
        if (cameraReaderFragment == null) {
            cameraReaderFragment = new cameraReader();
            transactionCameraReaderFragment.add(R.id.main_activityLinearLayout, cameraReaderFragment);
        }
        hideFragment(transactionCameraReaderFragment);
        if (showNow) {
            transactionCameraReaderFragment.show(cameraReaderFragment);
        }
        transactionCameraReaderFragment.commit();
    }

    private void showLogoCreatorFragment(boolean showNow) {
        FragmentTransaction transactionLogoCreatorFragment = manager.beginTransaction();
        if (logoCreatorFragment == null) {
            logoCreatorFragment = new logoCreator();
            transactionLogoCreatorFragment.add(R.id.main_activityLinearLayout, logoCreatorFragment);
        }
        hideFragment(transactionLogoCreatorFragment);
        if (showNow) {
            transactionLogoCreatorFragment.show(logoCreatorFragment);
        }
        transactionLogoCreatorFragment.commit();
    }

    private void showAwesomeFragment(boolean showNow) {
        FragmentTransaction transactionAwesomeCreatorFragment = manager.beginTransaction();
        if (awesomeCreatorFragment == null) {
            awesomeCreatorFragment = new awesomeCreator();
            transactionAwesomeCreatorFragment.add(R.id.main_activityLinearLayout, awesomeCreatorFragment);
        }
        hideFragment(transactionAwesomeCreatorFragment);
        if (showNow) {
            transactionAwesomeCreatorFragment.show(awesomeCreatorFragment);
        }
        transactionAwesomeCreatorFragment.commit();
    }

    private void showGifAwesomeFragment(boolean showNow) {
        FragmentTransaction transactionGifAwesomeCreatorFragment = manager.beginTransaction();
        if (gifAwesomeFragment == null) {
            gifAwesomeFragment = new gifAwesomeQr();
            transactionGifAwesomeCreatorFragment.add(R.id.main_activityLinearLayout, gifAwesomeFragment);
        }
        hideFragment(transactionGifAwesomeCreatorFragment);
        if (showNow) {
            transactionGifAwesomeCreatorFragment.show(gifAwesomeFragment);
        }
        transactionGifAwesomeCreatorFragment.commit();
    }

    private void showArbFragmentFragment(boolean showNow) {
        FragmentTransaction transactionTestFragment = manager.beginTransaction();
        if (arbAwesomeFragment == null) {
            arbAwesomeFragment = new arbAwesome();
            transactionTestFragment.add(R.id.main_activityLinearLayout, arbAwesomeFragment);
        }
        hideFragment(transactionTestFragment);
        if (showNow) {
            transactionTestFragment.show(arbAwesomeFragment);
        }
        transactionTestFragment.commit();
    }

    private void showGifArbAwesomeFragment(boolean showNow) {
        FragmentTransaction transactionGifArbAwesomeFragment = manager.beginTransaction();
        if (gifArbAwesomeFragment == null) {
            gifArbAwesomeFragment = new gifArbAwesome();
            transactionGifArbAwesomeFragment.add(R.id.main_activityLinearLayout, gifArbAwesomeFragment);
        }
        hideFragment(transactionGifArbAwesomeFragment);
        if (showNow) {
            transactionGifArbAwesomeFragment.show(gifArbAwesomeFragment);
        }
        transactionGifArbAwesomeFragment.commit();
    }

    private void showGifFragment(boolean showNow) {
        FragmentTransaction transactionGifFragment = manager.beginTransaction();
        if (gifCreatorFragment == null) {
            gifCreatorFragment = new gifCreator();
            transactionGifFragment.add(R.id.main_activityLinearLayout, gifCreatorFragment);
        }
        hideFragment(transactionGifFragment);
        if (showNow) {
            transactionGifFragment.show(gifCreatorFragment);
        }
        transactionGifFragment.commit();
    }

    private void showSettingsFragment(boolean showNow) {
        FragmentTransaction transactionsettings = manager.beginTransaction();
        if (settingsFragment == null) {
            settingsFragment = new SettingsPreference();
            transactionsettings.add(R.id.main_activityLinearLayout, settingsFragment);
        }
        hideFragment(transactionsettings);
        if (showNow) {
            transactionsettings.show(settingsFragment);
        }
        transactionsettings.commit();
    }


    private void showBusFragment(boolean showNow) {
        FragmentTransaction transactionBus = manager.beginTransaction();
        if (busCodeCreatorFragment == null) {
            busCodeCreatorFragment = new BusCodeCreator();
            transactionBus.add(R.id.main_activityLinearLayout, busCodeCreatorFragment);
        }
        hideFragment(transactionBus);
        if (showNow) {
            transactionBus.show(busCodeCreatorFragment);
        }
        transactionBus.commit();
    }

    private void showBusRFragment(boolean showNow) {
        FragmentTransaction transactionBusR = manager.beginTransaction();
        if (busCodeReaderFragment == null) {
            busCodeReaderFragment = new BusCodeReader();
            transactionBusR.add(R.id.main_activityLinearLayout, busCodeReaderFragment);
        }
        hideFragment(transactionBusR);
        if (showNow) {
            transactionBusR.show(busCodeReaderFragment);
        }
        transactionBusR.commit();
    }

    private void showPicEncryFragment(boolean showNow) {
        FragmentTransaction transactionBus = manager.beginTransaction();
        if (pictureEncryFragment == null) {
            pictureEncryFragment = new pictureEncry();
            transactionBus.add(R.id.main_activityLinearLayout, pictureEncryFragment);
        }
        hideFragment(transactionBus);
        if (showNow) {
            transactionBus.show(pictureEncryFragment);
        }
        transactionBus.commit();
    }

    private void showPixivDownloadFragment(boolean showNow) {
        FragmentTransaction transactionBusR = manager.beginTransaction();
        if (pixivDownloadMainFragment == null) {
            pixivDownloadMainFragment = new PixivDownloadMain();
            transactionBusR.add(R.id.main_activityLinearLayout, pixivDownloadMainFragment);
        }
        hideFragment(transactionBusR);
        if (showNow) {
            transactionBusR.show(pixivDownloadMainFragment);
        }
        transactionBusR.commit();
    }

    private void showPicDecryFragment(boolean showNow) {
        FragmentTransaction transactionBusR = manager.beginTransaction();
        if (pictureDecryFragment == null) {
            pictureDecryFragment = new pictureDecry();
            transactionBusR.add(R.id.main_activityLinearLayout, pictureDecryFragment);
        }
        hideFragment(transactionBusR);
        if (showNow) {
            transactionBusR.show(pictureDecryFragment);
        }
        transactionBusR.commit();
    }


    public void hideFragment(FragmentTransaction transaction) {
        Fragment fs[] = {
                welcomeFragment,
                logoCreatorFragment,
                awesomeCreatorFragment,
                gifAwesomeFragment,
                cameraReaderFragment,
                galleryReaderFragment,
                arbAwesomeFragment,
                gifCreatorFragment,
                gifArbAwesomeFragment,
                settingsFragment,
                busCodeCreatorFragment,
                busCodeReaderFragment,
                pictureEncryFragment,
                pictureDecryFragment,
                pixivDownloadMainFragment
        };
        for (Fragment f : fs) {
            if (f != null) {
                transaction.hide(f);
            }
        }
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

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK || keyCode == KeyEvent.KEYCODE_MENU) {
            if (mDrawerLayout.isDrawerOpen(mDrawerList)) {
                mDrawerLayout.closeDrawer(mDrawerList);
            } else {
                mDrawerLayout.openDrawer(mDrawerList);
            }
            return true;
        }
        if (arbAwesomeFragment != null && arbAwesomeFragment.isVisible()) {
            arbAwesomeFragment.onKeyDown(keyCode, event);
            return true;
        }
        if (gifArbAwesomeFragment != null && gifArbAwesomeFragment.isVisible()) {
            gifArbAwesomeFragment.onKeyDown(keyCode, event);
            return true;
        }
        return super.onKeyDown(keyCode, event);
    }


}

