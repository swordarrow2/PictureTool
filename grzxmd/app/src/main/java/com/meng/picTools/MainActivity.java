package com.meng.picTools;

import android.*;
import android.app.*;
import android.content.*;
import android.content.pm.*;
import android.content.res.*;
import android.net.*;
import android.os.*;
import android.support.design.widget.*;
import android.support.v4.app.*;
import android.support.v4.content.*;
import android.support.v4.view.*;
import android.support.v4.widget.*;
import android.support.v7.app.*;
import android.support.v7.graphics.drawable.*;
import android.support.v7.widget.*;
import android.view.*;
import android.widget.*;
import com.meng.picTools.*;
import com.meng.picTools.encryAndDecry.*;
import com.meng.picTools.fragment.*;
import com.meng.picTools.gif.*;
import com.meng.picTools.helpers.*;
import com.meng.picTools.lib.*;
import com.meng.picTools.pixivPictureDownloader.*;
import com.meng.picTools.qrCode.creator.*;
import com.meng.picTools.qrCode.reader.*;

import android.app.Fragment;
import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.Toolbar;
import com.meng.picTools.R;



public class MainActivity extends AppCompatActivity {

    public static MainActivity instence;
    private DrawerLayout mDrawerLayout;

    public boolean onWifi = false;

    public FragmentManager manager;

    public HomeFragment homeFragment;
    public MenusFragment menusFragment;
    public ProgressFragment progressFragment;

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

    public final int CROP_REQUEST_CODE = 3;
    public final int SELECT_FILE_REQUEST_CODE = 822;
    public TextView rightText;

    private ActionBarDrawerToggle toggle;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main_activity);
        instence = this;
        ExceptionCatcher.getInstance().init(this);
        SharedPreferenceHelper.Init(this, "main");
        DataBaseHelper.init(this);
        FileHelper.init(this);
        manager = getFragmentManager();
        rightText = (TextView) findViewById(R.id.main_activityTextViewRight);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        new GithubUpdateManager(this, "swordarrow2", "PicTools");
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
        toggle = new  ActionBarDrawerToggle(
                this, mDrawerLayout, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        mDrawerLayout.setDrawerListener(toggle);
        toggle.syncState();

        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(navigationItemSelectedListener);
        //initHomeFragment(true);
        //initProgressFragment(false);
        //initMenuFragment(false);
        showWelcome(true);
        ConnectivityManager connMgr = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo wifiNetworkInfo = connMgr.getNetworkInfo(ConnectivityManager.TYPE_WIFI);
        onWifi = wifiNetworkInfo.isConnected();
        mDrawerLayout.openDrawer(GravityCompat.START);
        navigationView.setCheckedItem(R.id.first_page);
    }

    NavigationView.OnNavigationItemSelectedListener navigationItemSelectedListener = new NavigationView.OnNavigationItemSelectedListener() {
        @Override
        public boolean onNavigationItemSelected(MenuItem item) {
            mDrawerLayout.closeDrawer(GravityCompat.START);
			mDrawerLayout.closeDrawer(GravityCompat.END);
            switch (item.getItemId()) {
                case R.id.home:
                    initHomeFragment(true);
                    break;
                case R.id.menus:
                    initMenuFragment(true);
                    break;
                case R.id.progress:
                    initProgressFragment(true);
                    break;
                case R.id.first_page:
                    showWelcome(true);
                    break;
                case R.id.read_barcode:
                    new AlertDialog.Builder(MainActivity.this)
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
                case R.id.create_barcode:
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
                    new AlertDialog.Builder(MainActivity.this)
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
                case R.id.bus:
                    new AlertDialog.Builder(MainActivity.this)
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
                case R.id.encry_and_decry:
                    new AlertDialog.Builder(MainActivity.this)
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
                case R.id.encode_gif:
                    showGifFragment(true);
                    break;
                case R.id.settings:
                    showSettingsFragment(true);
                    break;
                case R.id.pixiv_download:
                    showPixivDownloadFragment(true);
                    break;
                case R.id.exit:
                    if (SharedPreferenceHelper.getBoolean("exitsettings")) {
                        System.exit(0);
                    } else {
                        finish();
                    }
                    break;
            }
            return true;
        }
    };

    public void selectImage(Fragment f) {
        Intent intent = new Intent(Intent.ACTION_OPEN_DOCUMENT);
        intent.addCategory(Intent.CATEGORY_OPENABLE);
        intent.setType("image/*");
        f.startActivityForResult(intent, SELECT_FILE_REQUEST_CODE);
    }

    public void initHomeFragment(boolean showNow) {
        FragmentTransaction transactionWelcome = manager.beginTransaction();
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
        FragmentTransaction transactionWelcome = manager.beginTransaction();
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
        FragmentTransaction transactionWelcome = manager.beginTransaction();
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


    private void showWelcome(boolean showNow) {
        FragmentTransaction transactionWelcome = manager.beginTransaction();
        if (welcomeFragment == null) {
            welcomeFragment = new Welcome();
            transactionWelcome.add(R.id.fragment, welcomeFragment);
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
            transactionGalleryReaderFragment.add(R.id.fragment, galleryReaderFragment);
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
            transactionCameraReaderFragment.add(R.id.fragment, cameraReaderFragment);
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
            transactionLogoCreatorFragment.add(R.id.fragment, logoCreatorFragment);
        }
        hideFragment(transactionLogoCreatorFragment);
        if (showNow) {
            transactionLogoCreatorFragment.show(logoCreatorFragment);
        }
        transactionLogoCreatorFragment.commit();
    }

    public void showAwesomeFragment(boolean showNow) {
        FragmentTransaction transactionAwesomeCreatorFragment = manager.beginTransaction();
        if (awesomeCreatorFragment == null) {
            awesomeCreatorFragment = new AwesomeCreator();
            transactionAwesomeCreatorFragment.add(R.id.fragment, awesomeCreatorFragment);
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
            transactionGifAwesomeCreatorFragment.add(R.id.fragment, gifAwesomeFragment);
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
            transactionTestFragment.add(R.id.fragment, arbAwesomeFragment);
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
            transactionGifArbAwesomeFragment.add(R.id.fragment, gifArbAwesomeFragment);
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
            transactionGifFragment.add(R.id.fragment, gifCreatorFragment);
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
            transactionsettings.add(R.id.fragment, settingsFragment);
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
            transactionBus.add(R.id.fragment, busCodeCreatorFragment);
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
            transactionBusR.add(R.id.fragment, busCodeReaderFragment);
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
            transactionBus.add(R.id.fragment, pictureEncryFragment);
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
            transactionBusR.add(R.id.fragment, pixivDownloadMainFragment);
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
            transactionBusR.add(R.id.fragment, pictureDecryFragment);
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
                pixivDownloadMainFragment,
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
    protected void onPostCreate(Bundle savedInstanceState) {
        super.onPostCreate(savedInstanceState);
        toggle.syncState();
    }

    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
        toggle.onConfigurationChanged(newConfig);
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK || keyCode == KeyEvent.KEYCODE_MENU) {
            if (mDrawerLayout.isDrawerOpen(GravityCompat.START)) {
                mDrawerLayout.closeDrawer(GravityCompat.START);
            } else {
                mDrawerLayout.openDrawer(GravityCompat.START);
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

    public void doVibrate(long time) {
        Vibrator vibrator = (Vibrator) getSystemService(VIBRATOR_SERVICE);
        vibrator.vibrate(time);
    }
}

