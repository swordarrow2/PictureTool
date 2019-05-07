package com.meng.picTools.gif;

import android.app.Activity;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;

import com.meng.picTools.MainActivity2;

import java.util.ArrayList;

public class GIFSelectFrame extends Activity {

    ArrayList<GIFFrame> selected;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        ListView listView = new ListView(this);
        setContentView(listView);
        selected = MainActivity2.instence.gifCreatorFragment.selected;
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                selected.add((GIFFrame) parent.getItemAtPosition(position));
            }
        });
    }
}
