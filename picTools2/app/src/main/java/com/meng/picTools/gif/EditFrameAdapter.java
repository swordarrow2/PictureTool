package com.meng.picTools.gif;

import android.app.*;
import android.view.*;
import android.widget.*;
import com.meng.picTools.*;
import java.io.*;
import java.util.*;

public class EditFrameAdapter extends BaseAdapter {
    private Activity context;
    private ArrayList<GIFFrame> frames;
	private boolean showAll;

    public EditFrameAdapter(Activity context, ArrayList<GIFFrame> infos,boolean showFullPath) {
        this.context = context;
        this.frames = infos;
		showAll=showFullPath;
	  }

    public int getCount() {
        return frames.size();
	  }

    public Object getItem(int position) {
        return frames.get(position);
	  }

    public long getItemId(int position) {
        return frames.get(position).hashCode();
	  }

    public View getView(final int position, View convertView, ViewGroup parent) {

        final ViewHolder holder;
        if (convertView == null) {
            convertView = context.getLayoutInflater().inflate(R.layout.list_item_image_with_two_text, null);
            holder = new ViewHolder();
            holder.imageView = (ImageView) convertView.findViewById(R.id.imageView_info);
            holder.tvFileName = (TextView) convertView.findViewById(R.id.textView_bilibiliUid);
            holder.tvDelay = (TextView) convertView.findViewById(R.id.textView_bilibiliLiveId);
            convertView.setTag(holder);
		  } else {
            holder = (ViewHolder) convertView.getTag();
		  }
        GIFFrame gifFrame = frames.get(position);
		if(showAll){
        holder.tvFileName.setText(gifFrame.filePath);
		}else{
			holder.tvFileName.setText(new File(gifFrame.filePath).getName());
		}
        holder.tvDelay.setText("delay:" + gifFrame.delay + "ms");
        holder.imageView.setImageBitmap(gifFrame.thumb);

        return convertView;
	  }

    private final class ViewHolder {
        private ImageView imageView;
        private TextView tvFileName;
        private TextView tvDelay;
	  }
  }
