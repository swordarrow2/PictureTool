package com.meng.picTools.gif;

import android.app.*;
import android.content.*;
import android.content.res.*;
import android.graphics.*;
import android.media.*;
import android.os.*;
import android.support.v4.view.*;
import android.support.v4.widget.*;
import android.support.v7.app.*;
import android.support.v7.widget.*;
import android.view.*;
import android.widget.*;
import com.meng.picTools.*;
import com.meng.picTools.helpers.*;
import java.io.*;
import java.util.*;

import android.app.AlertDialog;
import android.support.v7.widget.Toolbar;

public class SelectFileActivity extends AppCompatActivity {

    private DrawerLayout mDrawerLayout;

    private ActionBarDrawerToggle toggle;
    private int theme = 0;
    private String rootPath;
    private ArrayList<String> pathList;
    private TextView curPathTextView;
    private ArrayList<GIFFrame> selectedImages;
    private ListView listView;
    private ListView leftList;
	private EditFrameAdapter editFrameAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.select_file_layout);
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
                    editFrameAdapter.notifyDataSetChanged();
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
                    }
                    editFrameAdapter.notifyDataSetChanged();
                    LogTool.t("已选择本目录全部图片");
                }
                return true;
            }
        });
        curPathTextView = (TextView) findViewById(R.id.curPath);
        selectedImages = MainActivity2.instence.gifCreatorFragment.selectedImages;
        rootPath = Environment.getExternalStorageDirectory().toString();
        getFileDir(rootPath);

        leftList = (ListView) findViewById(R.id.nav_view);
		editFrameAdapter=new EditFrameAdapter(this, selectedImages,false);
        leftList.setAdapter(editFrameAdapter);
        leftList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
           //     mDrawerLayout.closeDrawer(GravityCompat.START);
            }
        });
		leftList.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {

			  @Override
			  public boolean onItemLongClick(AdapterView<?> p1, View p2, final int p3, long p4) {
				  new AlertDialog.Builder(SelectFileActivity.this)
					.setTitle("确定删除吗")
					.setPositiveButton("确定", new DialogInterface.OnClickListener() {
						@Override
						public void onClick(DialogInterface p1, int p2) {
							selectedImages.remove(p3);
							editFrameAdapter.notifyDataSetChanged();
						  }
					  }).setNegativeButton("取消", null).show();
				  return true;
				}
			});
        mDrawerLayout.closeDrawer(GravityCompat.START);
    }

    private boolean isPicture(File file) {
        return file.getName().toLowerCase().endsWith(".jpg") ||
                file.getName().toLowerCase().endsWith(".png") ||
                file.getName().toLowerCase().endsWith(".bmp");
    }

    private void getFileDir(String filePath) {
        curPathTextView.setText(filePath);
        ArrayList<String> itemsList = new ArrayList<>();
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

