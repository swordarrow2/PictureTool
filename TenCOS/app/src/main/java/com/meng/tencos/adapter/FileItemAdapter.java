package com.meng.tencos.adapter;


import android.content.*;
import android.view.*;
import android.widget.*;
import com.meng.tencos.*;
import com.meng.tencos.bean.*;
import java.util.*;


public class FileItemAdapter extends BaseAdapter{

    private List<FileItem> files;
    private LayoutInflater inflater;

    public FileItemAdapter(Context context,List<FileItem> files){
        this.inflater=LayoutInflater.from(context);
        this.files=files;
    }

    @Override
    public int getCount(){
        return files.size();
    }

    @Override
    public Object getItem(int position){
        return files.get(position);
    }

    @Override
    public long getItemId(int position){
        return position;
    }

    @Override
    public View getView(final int position,View convertView,ViewGroup parent){
        ViewHolder holder = null;
        if(convertView==null){
            convertView=inflater.inflate(R.layout.file_item,parent,false);
            holder=new ViewHolder();
            holder.iv_fileIcon=(ImageView) convertView.findViewById(R.id.iv_file_icon);
            holder.tv_fileName=(TextView) convertView.findViewById(R.id.tv_file_name);
            holder.tv_fileMsg=(TextView) convertView.findViewById(R.id.tv_file_msg);
            holder.checkBox=(CheckBox) convertView.findViewById(R.id.checkbox);
            convertView.setTag(holder);
        }else{
            holder=(ViewHolder) convertView.getTag();
        }
        final FileItem item = files.get(position);
        holder.iv_fileIcon.setImageResource(item.getFileIconRes());
        holder.tv_fileName.setText(item.getFileName());
        if(item.getFileTime().length()!=0){
            holder.tv_fileMsg.setVisibility(View.VISIBLE);
            holder.tv_fileMsg.setText(item.getFileTime());
        }else{
            holder.tv_fileMsg.setVisibility(View.GONE);
        }
        if(item.isChecked()){
            holder.checkBox.setChecked(true);
        }else{
            holder.checkBox.setChecked(false);
        }
        holder.checkBox.setOnClickListener(new View.OnClickListener() {
				@Override
				public void onClick(View v){
					boolean isCheck = item.isChecked();
					files.get(position).setChecked(!isCheck);
				}
			});
        return convertView;
    }


    private class ViewHolder{
        ImageView iv_fileIcon;
        TextView tv_fileName;
        TextView tv_fileMsg;
        CheckBox checkBox;
    }
}
