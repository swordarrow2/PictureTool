package com.browser.blue.filebrowser;

import android.app.*;
import android.graphics.*;
import android.media.*;
import android.os.*;
import android.support.annotation.*;
import android.util.*;
import android.view.*;
import android.widget.*;
import com.meng.picTools.*;
import com.meng.picTools.gif.*;
import java.io.*;
import java.util.*;

/**
 * Created by blue on 2016/10/23.
 */

public class FileBrowserActivity extends ListActivity {
    private static final String TAG = FileBrowserActivity.class.getSimpleName() + "--->";
    private String rootPath;
    private List<String> pathList;
    private List<String> itemsList;
    private TextView curPathTextView;
	private ArrayList<GIFFrame> selectedImages;
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_file_browser_acitivity);
        	
		curPathTextView = (TextView) findViewById(R.id.curPath);
		selectedImages = MainActivity2.instence.gifCreatorFragment.selectedImages;
		rootPath = getRootPath();     
		getFileDir(rootPath);
	  }

 
    private void getFileDir(String filePath) {
        curPathTextView.setText(filePath);
        itemsList = new ArrayList<>();
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
        if (files == null) {
            Toast.makeText(this, "所选SD卡为空！", Toast.LENGTH_SHORT).show();
            finish();
            return;
		  }
        for (int i = 0; i < files.length; i++) {
            File f = files[i];
			// if (checkSpecificFile(f)) {
			itemsList.add(f.getName());
			pathList.add(f.getPath());
			//   }
		  }
        setListAdapter(new MyAdapter(this, itemsList, pathList));
	  }

    public boolean checkSpecificFile(File file) {
        String fileNameString = file.getName();
        String endNameString = fileNameString.substring(
		  fileNameString.lastIndexOf(".") + 1, fileNameString.length())
		  .toLowerCase();
        Log.d(TAG, "checkShapeFile: " + endNameString);
        if (file.isDirectory()) {
            return true;
		  }
        if (endNameString.equals("txt")) {
            return true;
		  } else {
            return false;
		  }
	  }

    private String getRootPath() {
		return Environment.getExternalStorageDirectory().toString();
	  }

    @Override
    protected void onListItemClick(ListView l, View v, int position, long id) {
        File file = new File(pathList.get(position));
        if (file.isDirectory()) {
            getFileDir(file.getPath());
		  } else if (file.getName().toLowerCase().endsWith(".jpg") ||
					 file.getName().toLowerCase().endsWith(".png") ||
					 file.getName().toLowerCase().endsWith(".bmp")) {
			GIFFrame gifFrame = new GIFFrame();
			gifFrame.thumb = ThumbnailUtils.extractThumbnail(BitmapFactory.decodeFile(file.getAbsolutePath()), 48, 48);
			gifFrame.delay = MainActivity2.instence.gifCreatorFragment.mengEtFrameDelay.getInt();
			gifFrame.filePath = file.getAbsolutePath();
			selectedImages.add(gifFrame);
			LogTool.t("已选择");			
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

    public boolean checkSDcard() {
        String sdStutusString = Environment.getExternalStorageState();
        if (sdStutusString.equals(Environment.MEDIA_MOUNTED)) {
            return true;
		  } else {
            return false;
		  }
	  }
  }
