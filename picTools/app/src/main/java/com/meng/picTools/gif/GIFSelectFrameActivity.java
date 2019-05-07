package com.meng.picTools.gif;

import android.app.Activity;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.os.Environment;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;

import com.meng.picTools.LogTool;
import com.meng.picTools.MainActivity2;

import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;

public class GIFSelectFrameActivity extends Activity {

    private ArrayList<GIFFrame> selectedImages;
    private SelectFileAdapter selectFileAdapter;
    private ArrayList<File> fileList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        ListView listView = new ListView(this);
        setContentView(listView);
        selectedImages = MainActivity2.instence.gifCreatorFragment.selectedImages;
        fileList = new ArrayList<>(Arrays.asList(Environment.getExternalStorageDirectory().listFiles()));
        selectFileAdapter = new SelectFileAdapter(this, fileList);
        listView.setAdapter(selectFileAdapter);
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                File file = (File) parent.getItemAtPosition(position);
                if (file.isDirectory()) {
                    fileList = new ArrayList<>(Arrays.asList(file.listFiles()));
                    selectFileAdapter.notifyDataSetChanged();
                } else {
                    GIFFrame gifFrame = new GIFFrame();
                    gifFrame.bitmap = BitmapFactory.decodeFile(file.getAbsolutePath());
                    gifFrame.delay = MainActivity2.instence.gifCreatorFragment.mengEtFrameDelay.getInt();
                    gifFrame.filePath = file.getAbsolutePath();
                    selectedImages.add(gifFrame);
                    LogTool.t("add frame");
                }
            }
        });
    }

    View.OnClickListener onClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            setResult(Activity.RESULT_OK);
        }
    };
}
