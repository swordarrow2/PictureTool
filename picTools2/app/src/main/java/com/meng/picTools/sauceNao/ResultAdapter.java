package com.meng.picTools.sauceNao;

import android.app.*;
import android.graphics.*;
import android.view.*;
import android.widget.*;
import com.meng.picTools.*;
import com.meng.picTools.sauceNao.*;
import java.util.*;


public class ResultAdapter extends BaseAdapter {
    private Activity activity;
    private ArrayList<PicResults.Result> resultArrayList;

    public ResultAdapter(Activity context, ArrayList<PicResults.Result> resultArrayList) {
        this.activity = context;
        this.resultArrayList = resultArrayList;
    }

    public int getCount() {
        return resultArrayList.size();
    }

    public Object getItem(int position) {
        return resultArrayList.get(position);
    }

    public long getItemId(int position) {
        return resultArrayList.get(position).hashCode();
    }

    public View getView(final int position, View convertView, ViewGroup parent) {

        final ViewHolder holder;
        if (convertView == null) {
            convertView = activity.getLayoutInflater().inflate(R.layout.list_item_saucenao_result, null);
            holder = new ViewHolder();
            holder.thumbnail = (ImageView) convertView.findViewById(R.id.thumbnail);
            holder.title = (TextView) convertView.findViewById(R.id.title);
            holder.similarity = (TextView) convertView.findViewById(R.id.similarity);
            holder.metadata = (TextView) convertView.findViewById(R.id.metadata);
            convertView.setTag(holder);
        } else {
            holder = (ViewHolder) convertView.getTag();
        }
        PicResults.Result result = resultArrayList.get(position);
        holder.title.setText(result.mTitle);
        holder.similarity.setText(result.mSimilarity);
        holder.metadata.setText(result.mColumns.get(0));
        Bitmap bitmap = MainActivity2.instence.sauceNaoMain.hashMap.get(result.mThumbnail);
        if (bitmap == null) {
            MainActivity2.instence.sauceNaoMain.threadPool.execute(new DownloadThumbnailRunnable(activity, holder.thumbnail, result.mThumbnail));
        } else {
            holder.thumbnail.setImageBitmap(bitmap);
        }
        return convertView;
    }

    private class ViewHolder {
        private ImageView thumbnail;
        private TextView title;
        private TextView similarity;
        private TextView metadata;
    }
}




