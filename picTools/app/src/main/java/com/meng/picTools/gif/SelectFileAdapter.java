package com.meng.picTools.gif;

import android.app.Activity;
import android.graphics.*;
import android.view.*;
import android.widget.*;

import com.meng.picTools.*;

import java.io.*;
import java.util.*;

public class SelectFileAdapter extends BaseAdapter {
    private Activity context;
    private ArrayList<File> fileList;

    public SelectFileAdapter(Activity context, ArrayList<File> fileArrayList) {
        this.context = context;
        this.fileList = fileArrayList;
    }

    public int getCount() {
        return fileList.size();
    }

    public Object getItem(int position) {
        return fileList.get(position);
    }

    public long getItemId(int position) {
        return fileList.get(position).hashCode();
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
        File qqNotReply = fileList.get(position);
        holder.fileName.setText(qqNotReply.getName());
        try {
            holder.imageView.setImageBitmap(BitmapFactory.decodeFile(qqNotReply.getAbsolutePath()));
        } catch (Exception e) {

        }
        return convertView;
    }

    private final class ViewHolder {
        private ImageView imageView;
        private TextView fileName;
    }
}
