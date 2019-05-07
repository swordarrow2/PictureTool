package com.meng.picTools.gif;

import android.content.*;
import android.graphics.*;
import android.view.*;
import android.widget.*;
import com.meng.picTools.*;
import java.io.*;
import java.util.*;

public class QQNotReplyAdapter extends BaseAdapter {
    private Context context;
    private ArrayList<Long> notReplyList;

    public QQNotReplyAdapter(Context context, ArrayList<Long> notReplyList) {
        this.context = context;
        this.notReplyList = notReplyList;
    }

    public int getCount() {
        return notReplyList.size();
    }

    public Object getItem(int position) {
        return notReplyList.get(position);
    }

    public long getItemId(int position) {
        return notReplyList.get(position).hashCode();
    }

    public View getView(final int position, View convertView, ViewGroup parent) {

        final ViewHolder holder;
        if (convertView == null) {
            convertView = ((MainActivity2) context).getLayoutInflater().inflate(R.layout.list_item_image_text_switch, null);
            holder = new ViewHolder();
            holder.groupNumber = (TextView) convertView.findViewById(R.id.group_reply_list_itemTextView);
            holder.imageView = (ImageView) convertView.findViewById(R.id.group_reply_list_itemImageView);
            holder.replySwitch = (Switch) convertView.findViewById(R.id.group_reply_list_itemSwitch);
            holder.replySwitch.setEnabled(false);
            holder.replySwitch.setChecked(false);
            convertView.setTag(holder);
        } else {
            holder = (ViewHolder) convertView.getTag();
        }
        final long qqNotReply = notReplyList.get(position);
        holder.groupNumber.setText(String.valueOf(qqNotReply));
        File imageFile = new File(MainActivity.mainDic + "user/" + qqNotReply + ".jpg");
        if (imageFile.exists()) {
            holder.imageView.setImageBitmap(BitmapFactory.decodeFile(imageFile.getAbsolutePath()));
        } else {
            if (MainActivity.onWifi) {
                new DownloadImageThread(context, holder.imageView, qqNotReply, HeadType.QQUser).start();
            } else {
                holder.imageView.setImageResource(R.drawable.stat_sys_download_anim0);
                holder.imageView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        new DownloadImageThread(context, holder.imageView, qqNotReply, HeadType.QQUser).start();
                    }
                });
            }
        }
        return convertView;
    }

    private final class ViewHolder {
        private ImageView imageView;
        private TextView groupNumber;
        private Switch replySwitch;
    }
}
