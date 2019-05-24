package com.meng.picTools.sauceNao;

import android.app.*;
import android.graphics.Bitmap;
import android.view.*;
import android.widget.*;

import com.meng.picTools.MainActivity;
import com.meng.picTools.R;

import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
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
            convertView = activity.getLayoutInflater().inflate(R.layout.saucenao_result_list, null);
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
        Bitmap bitmap = MainActivity.instence.sauceNaoMain.hashMap.get(result.mThumbnail);
        if (bitmap == null) {
            MainActivity.instence.sauceNaoMain.threadPool.execute(new DownloadThumbnailRunnable(activity, holder.thumbnail, result.mThumbnail));
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




