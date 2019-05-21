package com.meng.picTools.gif;

import android.app.*;
import android.content.*;
import android.graphics.*;
import android.net.*;
import android.os.*;
import android.view.*;
import android.widget.*;
import android.widget.AdapterView.*;

import com.github.clans.fab.FloatingActionButton;
import com.meng.picTools.*;
import com.meng.picTools.helpers.FileHelper;
import com.meng.picTools.helpers.FileType;
import com.meng.picTools.lib.*;
import com.meng.picTools.lib.mengViews.*;

import java.io.*;
import java.util.*;

import android.support.v7.app.AlertDialog;

public class GIFCreator extends Fragment {

    public MengEditText mengEtFrameDelay;
    public ArrayList<GIFFrame> selectedImages = new ArrayList<>();
    public EditFrameAdapter editFrameAdapter;
    public FloatingActionButton fabAdd;
    public FloatingActionButton fabEncode;

    private int mPreviousVisibleItem;
    private boolean encoding = false;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.gif_creator, container, false);
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        mengEtFrameDelay = (MengEditText) view.findViewById(R.id.gif_creator_delay);
        fabAdd = (FloatingActionButton) view.findViewById(R.id.fab_add);
        fabEncode = (FloatingActionButton) view.findViewById(R.id.fab_encode);
        ListView listView = (ListView) view.findViewById(R.id.gif_creator_list);
        fabAdd.setOnClickListener(listenerBtnClick);
        fabEncode.setOnClickListener(listenerBtnClick);
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
                                editFrameAdapter.notifyDataSetChanged();
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
                                editFrameAdapter.notifyDataSetChanged();
                            }
                        }).setNegativeButton("取消", null).show();
                return true;
            }
        });
        listView.setOnScrollListener(new AbsListView.OnScrollListener() {
            @Override
            public void onScrollStateChanged(AbsListView view, int scrollState) {
            }

            @Override
            public void onScroll(AbsListView view, int firstVisibleItem, int visibleItemCount, int totalItemCount) {
                if (firstVisibleItem > mPreviousVisibleItem) {
                    fabAdd.hide(true);
                    fabEncode.hide(true);
                } else if (firstVisibleItem < mPreviousVisibleItem) {
                    fabAdd.show(true);
                    fabEncode.show(true);
                }
                mPreviousVisibleItem = firstVisibleItem;
            }
        });
    }

    View.OnClickListener listenerBtnClick = new View.OnClickListener() {

        @Override
        public void onClick(View v) {
            switch (v.getId()) {
                case R.id.fab_add:
                    Intent intent = new Intent(getActivity(), GIFSelectFrameActivity.class);
                    startActivityForResult(intent, 9961);
                    break;
                case R.id.fab_encode:
                    if (encoding) return;
                    encoding = true;
                    LogTool.t("开始生成gif");
                    new Thread(new Runnable() {

                        @Override
                        public void run() {
                            try {
                                fabEncode.setMax(selectedImages.size());
                                String filePath = FileHelper.getFileAbsPath(FileType.gif);
                                ByteArrayOutputStream baos = new ByteArrayOutputStream();
                                getActivity().runOnUiThread(new Runnable() {
                                    @Override
                                    public void run() {
                                        fabEncode.setShowProgressBackground(true);
                                        fabEncode.setIndeterminate(false);
                                    }
                                });
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
                            encoding = false;
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
                fabEncode.setProgress(progress, true);
                if (progress == 100) {
                    LogTool.t("完成");
                    fabEncode.hideProgress();
                } else {
				  fabEncode.setIndeterminate(false);
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
        super.onActivityResult(requestCode, resultCode, data);
    }

}
