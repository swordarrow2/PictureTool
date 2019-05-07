package com.meng.picTools.gif;

import android.app.*;
import android.graphics.*;
import android.media.*;
import android.os.*;
import android.view.*;
import android.widget.*;
import com.meng.picTools.*;
import java.io.*;
import java.util.*;

public class GIFSelectFrameActivity extends Activity {

    private ArrayList<GIFFrame> selectedImages;
    private SelectFileAdapter selectFileAdapter;
    private File[] fileList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        final ListView listView = new ListView(this);
        setContentView(listView);
        selectedImages = MainActivity2.instence.gifCreatorFragment.selectedImages;
        fileList = Environment.getExternalStorageDirectory().listFiles();
		Arrays.sort(fileList);
        selectFileAdapter = new SelectFileAdapter(this, fileList);
        listView.setAdapter(selectFileAdapter);
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
			  @Override
			  public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
				  File file = (File) parent.getItemAtPosition(position);
				  if (file.isDirectory()) {
					  fileList = file.listFiles();
					  Arrays.sort(fileList);
					  selectFileAdapter.notifyDataSetChanged();
					  selectFileAdapter = new SelectFileAdapter(GIFSelectFrameActivity.this, fileList);
					  listView.setAdapter(selectFileAdapter);
					} else if (file.getName().toLowerCase().endsWith(".jpg") ||
							   file.getName().toLowerCase().endsWith(".png") ||
							   file.getName().toLowerCase().endsWith(".bmp")) {
					  GIFFrame gifFrame = new GIFFrame();
					  gifFrame.thumb = ThumbnailUtils.extractThumbnail(BitmapFactory.decodeFile(file.getAbsolutePath()), 48, 48);
					  gifFrame.delay = MainActivity2.instence.gifCreatorFragment.mengEtFrameDelay.getInt();
					  gifFrame.filePath = file.getAbsolutePath();
					  selectedImages.add(gifFrame);
					  LogTool.t("add frame");			
					}
				}
			});
	  }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        super.onCreateOptionsMenu(menu);
        MenuItem add = menu.add(1, 1, 1, "完成");
        add.setShowAsAction(MenuItem.SHOW_AS_ACTION_ALWAYS);
        return true;
	  }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getTitle().equals("完成")) {
			setResult(Activity.RESULT_OK);
			finish();
		  }
        return true;
	  }

  }
