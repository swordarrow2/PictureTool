package com.browser.blue.filebrowser;

import android.content.*;
import android.graphics.*;
import android.media.*;
import android.view.*;
import android.widget.*;
import com.meng.picTools.*;
import java.io.*;
import java.util.*;

public class MyAdapter extends BaseAdapter {
    private LayoutInflater mInflater;
    private Bitmap mIcon1;
    private Bitmap mIcon2;
    private Bitmap mIcon3;
    private Bitmap mIcon4;
    private List<String> items;
    private List<String> paths;
	private Bitmap[] bmpList;
    public MyAdapter(Context context, List<String> it, List<String> pa) {
        mInflater = LayoutInflater.from(context);
        items = it;
        paths = pa;
		bmpList = new Bitmap[paths.size()];
        mIcon1 = BitmapFactory.decodeResource(context.getResources(), R.drawable.icon_back);
        mIcon2 = BitmapFactory.decodeResource(context.getResources(), R.drawable.icon_back02);
        mIcon3 = BitmapFactory.decodeResource(context.getResources(), R.drawable.icon_fodler);
        mIcon4 = BitmapFactory.decodeResource(context.getResources(), R.drawable.icon_file);
	  }
    public int getCount() {
        return items.size();
	  }
    public Object getItem(int position) {
        return items.get(position);
	  }
    public long getItemId(int position) {
        return position;
	  }
    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder holder;
        if (convertView == null) {
            convertView = mInflater.inflate(R.layout.file_item, null);
            holder = new ViewHolder();
            holder.text = (TextView) convertView.findViewById(R.id.text);
            holder.icon = (ImageView) convertView.findViewById(R.id.icon);
            convertView.setTag(holder);
		  } else {
            holder = (ViewHolder) convertView.getTag();
		  }
        File f = new File(paths.get(position).toString());
        if (items.get(position).toString().equals("b1")) {
            holder.text.setText("返回根目录..");
            holder.icon.setImageBitmap(mIcon1);
		  } else if (items.get(position).toString().equals("b2")) {
            holder.text.setText("返回上一层..");
            holder.icon.setImageBitmap(mIcon2);
		  } else {
            holder.text.setText(f.getName());
            if (f.isDirectory()) {
                holder.icon.setImageBitmap(mIcon3);
			  } else {
                holder.icon.setImageBitmap(mIcon4);
				if (f.getName().toLowerCase().endsWith(".jpg") ||
					f.getName().toLowerCase().endsWith(".png") ||
					f.getName().toLowerCase().endsWith(".bmp")) {
					if (bmpList[position] == null) {
						bmpList[position] = ThumbnailUtils.extractThumbnail(BitmapFactory.decodeFile(f.getAbsolutePath()), 48, 48);
					  }
					holder.icon.setImageBitmap(bmpList[position]);
				  }
			  }	
		  }
        return convertView;
	  }
    private class ViewHolder {
        TextView text;
        ImageView icon;
	  }
  }