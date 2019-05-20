package com.meng.picTools;

import android.app.*;
import android.content.*;
import android.content.res.*;
import android.net.Uri;
import android.os.*;
import android.support.v4.widget.*;
import android.view.*;
import android.widget.*;

import com.meng.picTools.encryAndDecry.pictureDecry;
import com.meng.picTools.encryAndDecry.pictureEncry;
import com.meng.picTools.gif.GIFCreator;
import com.meng.picTools.lib.javaBean.UpdateInfo;
import com.meng.picTools.pixivPictureDownloader.PixivDownloadMain;
import com.meng.picTools.qrCode.creator.*;
import com.meng.picTools.lib.SharedPreferenceHelper;
import com.meng.picTools.lib.materialDesign.*;
import com.meng.picTools.qrCode.reader.*;

import org.jsoup.Connection;
import org.jsoup.Jsoup;

import java.util.Map;
import com.meng.picTools.lib.*;

public class MainActivity2 extends Activity {
    public static MainActivity2 instence;
    private DrawerLayout mDrawerLayout;
    private ListView mDrawerList;
    private RelativeLayout rightLayout;
    private ActionBarDrawerToggle mDrawerToggle;

    private Welcome welcomeFragment;
    private LogoQRCreator logoCreatorFragment;
    public AwesomeCreator awesomeCreatorFragment;
    public CameraQRReader cameraReaderFragment;
    public GalleryQRReader galleryReaderFragment;
    private AnimGIFAwesomeQr gifAwesomeFragment;
    private ArbAwesomeCreator arbAwesomeFragment;
    public GIFCreator gifCreatorFragment;
    private SettingsPreference settingsFragment;
    private AnimGIFArbAwesome gifArbAwesomeFragment;
    private BusCodeCreator busCodeCreatorFragment;
    private BusCodeReader busCodeReaderFragment;
    private pictureEncry pictureEncryFragment;
    private pictureDecry pictureDecryFragment;
    public PixivDownloadMain pixivDownloadMainFragment;

    public FragmentManager manager;
    public TextView rightText;

    public static final int SELECT_FILE_REQUEST_CODE = 822;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main_activity);
        instence = this;
        rightLayout = (RelativeLayout) findViewById(R.id.right_drawer);
        rightText = (TextView) findViewById(R.id.main_activityTextViewRight);
        mDrawerLayout = (DrawerLayout) findViewById(R.id.drawer_layout);
        mDrawerList = (ListView) findViewById(R.id.navdrawer);
        manager = getFragmentManager();
        showAwesomeFragment(false);
/*      showGifAwesomeFragment(false);
        showArbFragmentFragment(false);
        showGifArbAwesomeFragment(false);
        showGifFragment(false);
        showSettingsFragment(false);
        showBusFragment(false);
        showBusRFragment(false);
        showPicEncryFragment(false);
        showPicDecryFragment(false);
        showPixivDownloadFragment(false);
        showGalleryReaderFragment(false);
        showCameraReaderFragment(false);
        showLogoCreatorFragment(false);
*/
        ActionBar actionBar = getActionBar();
        actionBar.setDisplayHomeAsUpEnabled(true);
        actionBar.setHomeButtonEnabled(true);

        setListener();
        changeTheme();
        new GithubUpdateManager(this,"swordarrow2","PicTools");
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
            rightLayout.setBackgroundColor(getResources().getColor(android.R.color.background_light));
        } else {
            mDrawerList.setBackgroundColor(getResources().getColor(android.R.color.background_dark));
            rightLayout.setBackgroundColor(getResources().getColor(android.R.color.background_dark));
        }
        if (getIntent().getBooleanExtra("setTheme", false)) {
            showSettingsFragment(true);
        } else {
            showWelcome(true);
            if (SharedPreferenceHelper.getBoolean("opendraw", true)) {
                mDrawerLayout.openDrawer(mDrawerList);
            }
        }
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
        DrawerArrowDrawable drawerArrow = new DrawerArrowDrawable(this) {
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
                        showWelcome(true);
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
                        final View view1 = getLayoutInflater().inflate(R.layout.select_qr_function, null);
                        RadioGroup r1 = (RadioGroup) view1.findViewById(R.id.select_qr_function_g1);
                        final RadioGroup r2 = (RadioGroup) view1.findViewById(R.id.select_qr_function_g2);
                        final RadioGroup r3 = (RadioGroup) view1.findViewById(R.id.select_qr_function_g3);
                        r2.setEnabled(false);
                        r3.setEnabled(false);
                        r1.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
                            @Override
                            public void onCheckedChanged(RadioGroup group, int checkedId) {
                                if (group.getCheckedRadioButtonId() == R.id.select_qr_function_normal_qr) {
                                    r2.setVisibility(View.GONE);
                                    r3.setVisibility(View.GONE);
                                } else {
                                    r2.setVisibility(View.VISIBLE);
                                    r3.setVisibility(View.VISIBLE);
                                }
                            }
                        });
                        final RadioButton rbNormal = (RadioButton) view1.findViewById(R.id.select_qr_function_normal_qr);
                        final RadioButton rbAnim = (RadioButton) view1.findViewById(R.id.select_qr_function_anim);
                        final RadioButton rbArb = (RadioButton) view1.findViewById(R.id.select_qr_function_arb);
                        new AlertDialog.Builder(MainActivity2.this)
                                .setTitle("选择二维码类型")
								.setView(view1)
                                .setPositiveButton("确定", new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface p1, int p2) {
                                        if (rbNormal.isChecked()) {
                                            showLogoCreatorFragment(true);
                                        } else {
                                            if (rbArb.isChecked()) {
                                                if (rbAnim.isChecked()) {
                                                    showGifArbAwesomeFragment(true);
                                                } else {
                                                    showArbFragmentFragment(true);
                                                }
                                            } else {
                                                if (rbAnim.isChecked()) {
                                                    showGifAwesomeFragment(true);
                                                } else {
                                                    showAwesomeFragment(true);
                                                }
                                            }
                                        }
                                    }
                                }).setNegativeButton("返回", null).show();
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

    private void showWelcome(boolean showNow) {
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
            galleryReaderFragment = new GalleryQRReader();
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
            cameraReaderFragment = new CameraQRReader();
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
            logoCreatorFragment = new LogoQRCreator();
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
            awesomeCreatorFragment = new AwesomeCreator();
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
            gifAwesomeFragment = new AnimGIFAwesomeQr();
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
            arbAwesomeFragment = new ArbAwesomeCreator();
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
            gifArbAwesomeFragment = new AnimGIFArbAwesome();
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
            gifCreatorFragment = new GIFCreator();
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

