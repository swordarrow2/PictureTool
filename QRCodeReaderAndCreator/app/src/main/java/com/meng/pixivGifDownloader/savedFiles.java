package com.meng.pixivGifDownloader;

import android.app.*;
import android.content.*;
import android.os.*;
import android.view.*;
import android.widget.*;
import com.meng.picTools.*;
import java.io.*;
import java.util.*;

public class savedFiles extends Activity {
    ListView listView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.saved_files);
        listView = (ListView) findViewById(R.id.saved_files_list);
        File zipFod = new File(PixivDownloadMain.zipFolder);
        if (!zipFod.exists()) {
            Toast.makeText(this, "空路径", Toast.LENGTH_SHORT).show();
            try {
                zipFod.mkdirs();
            } catch (Exception e) {
                Toast.makeText(this, e.toString(), Toast.LENGTH_SHORT).show();
            }
        } else {
            String[] filesName = new File(PixivDownloadMain.zipFolder).list();
            Arrays.sort(filesName);
            ListAdapter adapter = new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1, filesName);
            listView.setAdapter(adapter);
            listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                @Override
                public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                    Intent intent = new Intent(savedFiles.this, playLayout.class);
                    intent.putExtra(Data.intentKeys.fileName, PixivDownloadMain.zipFolder + File.separator + adapterView.getItemAtPosition(i).toString());
                    startActivity(intent);
                }
            });
        }

    }
}
