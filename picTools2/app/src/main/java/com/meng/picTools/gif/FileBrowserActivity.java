package com.meng.picTools.gif;

import android.app.*;
import android.graphics.*;
import android.media.*;
import android.os.*;
import android.support.annotation.*;
import android.view.*;
import android.widget.*;

import com.meng.picTools.*;

import java.io.*;
import java.util.*;

public class FileBrowserActivity extends Activity {
    private String rootPath;
    private ArrayList<String> pathList;
    private TextView curPathTextView;
    private ArrayList<GIFFrame> selectedImages;
    private ListView listView;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_file_browser_acitivity);
        listView = (ListView) findViewById(R.id.list);
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

		/*  {
		 Intent data = new Intent(FileBrowserActivity.this, MainActivity.class);
		 Bundle bundle = new Bundle();
		 bundle.putString("file", file.getPath());
		 data.putExtras(bundle);
		 setResult(2, data);
		 finish();
		 }*/
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

    //  public boolean checkSpecificFile(File file) {
    //       String fileNameString = file.getName();
    //      String endNameString = fileNameString.substring(
    //              fileNameString.lastIndexOf(".") + 1, fileNameString.length())
    //               .toLowerCase();
    //      return file.isDirectory() || endNameString.equals("txt");
    //  }

    //  public boolean checkSDcard() {
    //      return Environment.getExternalStorageState().equals(Environment.MEDIA_MOUNTED);
    //  }
}
