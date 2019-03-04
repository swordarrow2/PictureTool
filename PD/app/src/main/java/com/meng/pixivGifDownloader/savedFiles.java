package com.meng.pixivGifDownloader;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.Toast;

import java.io.File;
import java.lang.reflect.Array;
import java.util.Arrays;

/**
 * Created by Administrator on 2018/4/14.
 */

public class savedFiles extends AppCompatActivity {
    ListView listView;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.saved_files);
        listView= (ListView) findViewById(R.id.saved_files_list);
        File zipFod =new File(MainActivity.zipFolder);
        if (!zipFod.exists()){
            Toast.makeText(this,"空路径",Toast.LENGTH_SHORT).show();
            try {
                zipFod.mkdirs();
            }catch (Exception e){
                Toast.makeText(this,e.toString(),Toast.LENGTH_SHORT).show();
            }
        }else {
            String[] filesName = new File(MainActivity.zipFolder).list();
            Arrays.sort(filesName);
            ListAdapter adapter = new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1, filesName);
            listView.setAdapter(adapter);
            listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                @Override
                public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                    Intent intent = new Intent(savedFiles.this, playLayout.class);
                    intent.putExtra(Data.intentKeys.fileName, MainActivity.zipFolder + File.separator + adapterView.getItemAtPosition(i).toString());
                    startActivity(intent);
                }
            });}

    }
}
