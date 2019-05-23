package com.meng.picTools.sauceNao;
	import android.app.*;
	import android.graphics.*;
	import android.view.*;
	import android.widget.*;
		import java.io.*;
	import java.util.*;



public class ResultAdapter extends BaseAdapter {
    private Activity activity;
    private ArrayList<PicResults.Result> loginInfoPeopleArrayList;

    public ResultAdapter(Activity context, ArrayList<PicResults.Result> groupReplies) {
        this.activity = context;
        this.loginInfoPeopleArrayList = groupReplies;
	  }

    public int getCount() {
        return loginInfoPeopleArrayList.size();
	  }

    public Object getItem(int position) {
        return loginInfoPeopleArrayList.get(position);
	  }

    public long getItemId(int position) {
        return loginInfoPeopleArrayList.get(position).hashCode();
	  }

    public View getView(final int position, View convertView, ViewGroup parent) {

        final ViewHolder holder;
        if (convertView == null) {
            convertView = activity.getLayoutInflater().inflate(R.layout.list_item_image_text_switch, null);
            holder = new ViewHolder();
            holder.tvName = (TextView) convertView.findViewById(R.id.group_reply_list_itemTextView);
            holder.aSwitch = (Switch) convertView.findViewById(R.id.group_reply_list_itemSwitch);
            holder.aSwitch.setVisibility(View.GONE);
            holder.ivHead = (ImageView) convertView.findViewById(R.id.group_reply_list_itemImageView);
            convertView.setTag(holder);
		  } else {
            holder = (ViewHolder) convertView.getTag();
		  }
        final LoginInfoPeople loginInfoPeople = loginInfoPeopleArrayList.get(position);
        holder.tvName.setText(loginInfoPeople.personInfo.data.name);
        File bilibiliImageFile = new File(MainActivity.instence.mainDic + "bilibili/" + loginInfoPeople.personInfo.data.mid + ".jpg");
        if (bilibiliImageFile.exists()) {
            holder.ivHead.setImageBitmap(BitmapFactory.decodeFile(bilibiliImageFile.getAbsolutePath()));
		  } else {
            if (MainActivity.onWifi) {
                MainActivity.instence.personInfoFragment.threadPool.execute(new DownloadImageRunnable(activity, holder.ivHead, String.valueOf(loginInfoPeople.personInfo.data.mid), HeadType.BilibiliUser));
			  } else {
                holder.ivHead.setImageResource(R.drawable.stat_sys_download_anim0);
                holder.ivHead.setOnClickListener(new View.OnClickListener() {
					  @Override
					  public void onClick(View v) {
						  MainActivity.instence.personInfoFragment.threadPool.execute(new DownloadImageRunnable(activity, holder.ivHead, String.valueOf(loginInfoPeople.personInfo.data.mid), HeadType.BilibiliUser));
						}
					});
			  }
		  }
        return convertView;
	  }

    private class ViewHolder {
        private ImageView ivHead;
        private TextView tvName;
        private Switch aSwitch;
	  }
  }




