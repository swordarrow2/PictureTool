package com.meng.picTools.gif;

import android.app.*;
import android.content.*;
import android.graphics.*;
import android.net.*;
import android.os.*;
import android.view.*;
import android.widget.*;
import android.widget.AdapterView.*;
import com.meng.picTools.*;
import com.meng.picTools.lib.*;
import com.meng.picTools.lib.mengViews.*;
import java.io.*;
import java.util.*;

/**
 * gif生成
 */

public class GIFCreator extends Fragment {

    public MengEditText mengEtFrameDelay;
    private ProgressBar progressBar;
	private Button btnEncode;
    public ArrayList<GIFFrame> selectedImages = new ArrayList<>();
    public EditFrameAdapter editFrameAdapter;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.gif_creator, container, false);
	  }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        mengEtFrameDelay = (MengEditText) view.findViewById(R.id.gif_creator_delay);
        ListView listView = (ListView) view.findViewById(R.id.gif_creator_list);
        progressBar = (ProgressBar) view.findViewById(R.id.gif_creator_progress_bar);
        ((Button) view.findViewById(R.id.gif_creator_add)).setOnClickListener(listenerBtnClick);
		btnEncode = (Button) view.findViewById(R.id.gif_creator_finish);
        btnEncode.setOnClickListener(listenerBtnClick);
        editFrameAdapter = new EditFrameAdapter(getActivity(), selectedImages);
        listView.setAdapter(editFrameAdapter);
        listView.setOnItemClickListener(new OnItemClickListener() {
			  @Override
			  public void onItemClick(final AdapterView<?> parent, View v, int position, long id) {
				  final EditText editTextName = new EditText(getActivity());
				  final GIFFrame personInfo = (GIFFrame) parent.getItemAtPosition(position);
				  editTextName.setText(personInfo.delay + "");
				  new AlertDialog.Builder(getActivity())
					.setView(editTextName)
					.setTitle("设置帧延时(ms)")
					.setPositiveButton("确定", new DialogInterface.OnClickListener() {
						@Override
						public void onClick(DialogInterface dialogInterface, int which) {
							personInfo.delay = Integer.parseInt(editTextName.getText().toString());
							((EditFrameAdapter) parent.getAdapter()).notifyDataSetChanged();
						  }
					  }).setNegativeButton("取消", null).show();
				}
			});
		listView.setOnItemLongClickListener(new OnItemLongClickListener() {

			  @Override
			  public boolean onItemLongClick(final AdapterView<?> adapterView, View view, final int position, long id) {

				  new AlertDialog.Builder(getActivity())
					.setTitle("确定删除吗")
					.setPositiveButton("确定", new DialogInterface.OnClickListener() {
						@Override
						public void onClick(DialogInterface dialogInterface, int which) {
							selectedImages.remove(position);
							((BaseAdapter) adapterView.getAdapter()).notifyDataSetChanged();
							btnEncode.setEnabled(selectedImages.size() > 0);
						  }
					  }).setNegativeButton("取消", null).show();
				  return true;
				}
			});
	  }
    View.OnClickListener listenerBtnClick = new View.OnClickListener() {

        @Override
        public void onClick(View v) {
            switch (v.getId()) {
                case R.id.gif_creator_add:
				  Intent intent = new Intent(getActivity(), GIFSelectFrameActivity.class);
				  startActivityForResult(intent, 9961);
				  break;
                case R.id.gif_creator_finish:
				  LogTool.t("开始生成gif");
				  new Thread(new Runnable() {

                        @Override
                        public void run() {
                            try {
                                String filePath = MainActivity.instence.getGifPath();
                                ByteArrayOutputStream baos = new ByteArrayOutputStream();
                                AnimatedGifEncoder localAnimatedGifEncoder = new AnimatedGifEncoder();
                                localAnimatedGifEncoder.start(baos);//start
                                localAnimatedGifEncoder.setRepeat(0);//设置生成gif的开始播放时间。0为立即开始播放
                                int currentFile = 1;
                                for (GIFFrame gifFrame : selectedImages) {
                                    localAnimatedGifEncoder.setDelay(gifFrame.delay);
                                    localAnimatedGifEncoder.addFrame(BitmapFactory.decodeFile(gifFrame.filePath));
                                    float pro = ((float) currentFile) / selectedImages.size() * 100;
                                    setProgress((int) pro);
                                    ++currentFile;
								  }
                                localAnimatedGifEncoder.finish();
                                try {
                                    FileOutputStream fos = new FileOutputStream(filePath);
                                    baos.writeTo(fos);
                                    baos.flush();
                                    fos.flush();
                                    baos.close();
                                    fos.close();
								  } catch (IOException e) {
                                    LogTool.e("gif异常" + e.toString());
								  }
                                getActivity().getApplicationContext().sendBroadcast(new Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE, Uri.fromFile(new File(filePath))));
                                LogTool.t("完成 : " + filePath);
							  } catch (Exception e) {
                                LogTool.e(e);
							  }
						  }
					  }).start();
				  break;
			  }
		  }
	  };

    private void setProgress(final int progress) {
        getActivity().runOnUiThread(new Runnable() {
			  @Override
			  public void run() {
				  if (progress == 100) {
					  progressBar.setVisibility(View.GONE);
					  LogTool.t("完成");
					} else {
					  if (progressBar.getVisibility() == View.GONE) {
						  progressBar.setVisibility(View.VISIBLE);
						}
					  progressBar.setProgress(progress);
					}
				}
			});
	  }

	@Override
	public void onResume() {
		editFrameAdapter.notifyDataSetChanged();
		super.onResume();
	  }


    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == 9961 && resultCode == Activity.RESULT_OK) {
            LogTool.t("add frame ok");
		  }
		btnEncode.setEnabled(selectedImages.size() > 0);
        super.onActivityResult(requestCode, resultCode, data);
	  }

  }
