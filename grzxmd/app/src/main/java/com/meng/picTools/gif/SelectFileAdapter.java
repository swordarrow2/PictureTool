package com.meng.picTools.gif;

import android.app.Activity;
import android.graphics.*;
import android.view.*;
import android.widget.*;

import com.meng.picTools.*;

import java.io.*;
import java.util.*;
import android.media.*;

public class SelectFileAdapter extends BaseAdapter {
    private Activity context;
    private File[] fileList;
	private Bitmap[] bmpList;

    public SelectFileAdapter(Activity context, File[] fileArrayList) {
        this.context = context;
        this.fileList = fileArrayList;
		bmpList = new Bitmap[fileList.length];
	  }

    public int getCount() {
        return fileList.length;
	  }

    public Object getItem(int position) {
        return fileList[position];
	  }

    public long getItemId(int position) {
        return fileList[position].hashCode();
	  }

    public View getView(final int position, View convertView, ViewGroup parent) {
        final ViewHolder holder;
        if (convertView == null) {
            convertView = context.getLayoutInflater().inflate(R.layout.list_item_select_image, null);
            holder = new ViewHolder();
            holder.fileName = (TextView) convertView.findViewById(R.id.select_file_adapter_file_name);
            holder.imageView = (ImageView) convertView.findViewById(R.id.select_file_adapter_imageview);
            convertView.setTag(holder);
		  } else {
            holder = (ViewHolder) convertView.getTag();
		  }
        File qqNotReply = fileList[position];
        holder.fileName.setText(qqNotReply.getName());
		if (bmpList[position] == null) {
			bmpList[position] = ThumbnailUtils.extractThumbnail(BitmapFactory.decodeFile(qqNotReply.getAbsolutePath()), 48, 48);
		  }
		holder.imageView.setImageBitmap(bmpList[position]);
        return convertView;
	  }

    private final class ViewHolder {
        private ImageView imageView;
        private TextView fileName;
	  }
  }
