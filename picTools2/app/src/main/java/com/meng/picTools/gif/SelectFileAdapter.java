package com.meng.picTools.gif;

import android.content.*;
import android.graphics.*;
import android.media.*;
import android.view.*;
import android.widget.*;

import com.meng.picTools.*;

import java.io.*;
import java.util.*;

public class SelectFileAdapter extends BaseAdapter {
    private LayoutInflater mInflater;
    private Bitmap mIcon1;
    private Bitmap mIcon2;
    private Bitmap mIcon3;
    private Bitmap mIcon4;
    private ArrayList<String> items;
    private ArrayList<String> paths;
    private Bitmap[] bmpList;

    public SelectFileAdapter(Context context, ArrayList<String> items, ArrayList<String> paths) {
        mInflater = LayoutInflater.from(context);
        this.items = items;
        this.paths = paths;
        bmpList = new Bitmap[this.paths.size()];
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
        File file = new File(paths.get(position));
        switch (items.get(position)) {
            case "b1":
                holder.text.setText("返回根目录..");
                holder.icon.setImageBitmap(mIcon1);
                break;
            case "b2":
                holder.text.setText("返回上一层..");
                holder.icon.setImageBitmap(mIcon2);
                break;
            default:
                holder.text.setText(file.getName());
                if (file.isDirectory()) {
                    holder.icon.setImageBitmap(mIcon3);
                } else if (isPicture(file)) {
                    if (bmpList[position] == null) {
                        bmpList[position] = ThumbnailUtils.extractThumbnail(BitmapFactory.decodeFile(file.getAbsolutePath()), 48, 48);
                    }
                    holder.icon.setImageBitmap(bmpList[position]);
                } else {
                    holder.icon.setImageBitmap(mIcon4);
                }
                break;
        }
        return convertView;
    }

    private class ViewHolder {
        TextView text;
        ImageView icon;
    }

    private boolean isPicture(File file) {
        return file.getName().toLowerCase().endsWith(".jpg") ||
                file.getName().toLowerCase().endsWith(".png") ||
                file.getName().toLowerCase().endsWith(".bmp");
    }
}
