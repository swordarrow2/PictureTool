package com.meng.picTools.gif;

import android.content.res.ColorStateList;
import android.content.res.Configuration;
import android.graphics.BitmapFactory;
import android.media.ThumbnailUtils;
import android.os.Bundle;
import android.os.Environment;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.KeyEvent;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.FrameLayout;
import android.widget.ListView;
import android.widget.TextView;

import com.meng.picTools.LogTool;
import com.meng.picTools.MainActivity2;
import com.meng.picTools.R;
import com.meng.picTools.helpers.SharedPreferenceHelper;

import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class SelectFileActivity extends AppCompatActivity {

    private DrawerLayout mDrawerLayout;

    private ActionBarDrawerToggle toggle;
    private int theme = 0;
    private String rootPath;
    private List<String> pathList;
    private TextView curPathTextView;
    private ArrayList<GIFFrame> selectedImages;
    private ListView listView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main_activity);
        ((View) findViewById(R.id.right_drawer)).setVisibility(View.GONE);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        mDrawerLayout = (DrawerLayout) findViewById(R.id.drawer_layout);
        toggle = new ActionBarDrawerToggle(
                this, mDrawerLayout, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        mDrawerLayout.setDrawerListener(toggle);
        toggle.syncState();
        View mainView = getLayoutInflater().inflate(R.layout.activity_file_browser_acitivity, (FrameLayout) findViewById(R.id.fragment));
        listView = (ListView) mainView.findViewById(R.id.list);
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                File file = new File(pathList.get(i));
                if (file.isDirectory()) {
                    getFileDir(file.getPath());
                } else if (isPicture(file)) {
                    GIFFrame gifFrame = new GIFFrame();
                    gifFrame.thumb = ThumbnailUtils.extractThumbnail(BitmapFactory.decodeFile(file.getAbsolutePath()), 48, 48);
                    gifFrame.delay = MainActivity2.instence.gifCreatorFragment.mengEtFrameDelay.getInt();
                    gifFrame.filePath = file.getAbsolutePath();
                    selectedImages.add(gifFrame);
                    LogTool.t(file.getName() + "已选择");
                }
            }
        });
        listView.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {

            @Override
            public boolean onItemLongClick(AdapterView<?> p1, View p2, int p3, long p4) {
                for (String s : pathList) {
                    File file = new File(s);
                    if (isPicture(file)) {
                        GIFFrame gifFrame = new GIFFrame();
                        gifFrame.thumb = ThumbnailUtils.extractThumbnail(BitmapFactory.decodeFile(file.getAbsolutePath()), 48, 48);
                        gifFrame.delay = MainActivity2.instence.gifCreatorFragment.mengEtFrameDelay.getInt();
                        gifFrame.filePath = file.getAbsolutePath();
                        selectedImages.add(gifFrame);
                        LogTool.t("已选择本目录全部图片");
                    }
                }
                return true;
            }
        });
        curPathTextView = (TextView) findViewById(R.id.curPath);
        selectedImages = MainActivity2.instence.gifCreatorFragment.selectedImages;
        rootPath = Environment.getExternalStorageDirectory().toString();
        getFileDir(rootPath);

        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.getMenu().clear();
        navigationView.getMenu().add(2, 2, 2, "menu_1");//group id,item id,order id
        navigationView.setNavigationItemSelectedListener(new NavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(MenuItem item) {
                mDrawerLayout.closeDrawer(GravityCompat.START);
                switch (item.getItemId()) {
                    case R.id.pixiv_download:
                        break;
                    case R.id.exit:
                        break;
                }
                return true;
            }
        });
        ColorStateList csl;
        switch (theme) {
            case R.style.green:
                csl = getResources().getColorStateList(R.color.navigation_menu_item_color_green);
                break;
            case R.style.red:
                csl = getResources().getColorStateList(R.color.navigation_menu_item_color_red);
                break;
            case R.style.blue:
                csl = getResources().getColorStateList(R.color.navigation_menu_item_color_blue);
                break;
            case R.style.black:
                csl = getResources().getColorStateList(R.color.navigation_menu_item_color_black);
                break;
            case R.style.purple:
                csl = getResources().getColorStateList(R.color.navigation_menu_item_color_purple);
                break;
            default:
                csl = getResources().getColorStateList(R.color.navigation_menu_item_color_green);
                break;
        }
        navigationView.setItemTextColor(csl);
        navigationView.setItemIconTintList(csl);
        navigationView.setCheckedItem(R.id.first_page);
        mDrawerLayout.closeDrawer(GravityCompat.START);
        navigationView.getHeaderView(0).setVisibility(View.GONE);
    }

    private boolean isPicture(File file) {
        return file.getName().toLowerCase().endsWith(".jpg") ||
                file.getName().toLowerCase().endsWith(".png") ||
                file.getName().toLowerCase().endsWith(".bmp");
    }

    private void getFileDir(String filePath) {
        curPathTextView.setText(filePath);
        List<String> itemsList = new ArrayList<>();
        pathList = new ArrayList<>();
        File file = new File(filePath);
        File[] files = file.listFiles();
        Arrays.sort(files);
        if (!filePath.equals(rootPath)) {
            itemsList.add("b1");
            pathList.add(rootPath);
            itemsList.add("b2");
            pathList.add(file.getParent());
        }
        for (File f : files) {
            // if (checkSpecificFile(f)) {
            itemsList.add(f.getName());
            pathList.add(f.getPath());
            //   }
        }
        listView.setAdapter(new SelectFileAdapter(this, itemsList, pathList));
    }

    @Override
    protected void onPostCreate(Bundle savedInstanceState) {
        super.onPostCreate(savedInstanceState);
        toggle.syncState();
    }

    @Override
    public void setTheme(int resid) {
        switch (SharedPreferenceHelper.getValue("color", "芳")) {
            case "芳":
                super.setTheme(theme = R.style.green);
                break;
            case "红":
                super.setTheme(theme = R.style.red);
                break;
            case "黑":
                super.setTheme(theme = R.style.black);
                break;
            case "紫":
                super.setTheme(theme = R.style.purple);
                break;
            case "蓝":
                super.setTheme(theme = R.style.blue);
                break;
            default:
                super.setTheme(theme = R.style.green);
                break;
        }
    }

    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
        toggle.onConfigurationChanged(newConfig);
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK) {
            if (mDrawerLayout.isDrawerOpen(GravityCompat.START)) {
                finish();
            } else {
                mDrawerLayout.openDrawer(GravityCompat.START);
            }
            return true;
        }
        if (keyCode == KeyEvent.KEYCODE_MENU) {
            if (mDrawerLayout.isDrawerOpen(GravityCompat.START)) {
                mDrawerLayout.closeDrawer(GravityCompat.START);
            } else {
                mDrawerLayout.openDrawer(GravityCompat.START);
            }
            return true;
        }
        return super.onKeyDown(keyCode, event);
    }
}

