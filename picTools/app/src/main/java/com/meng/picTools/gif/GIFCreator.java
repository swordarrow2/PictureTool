package com.meng.picTools.gif;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Fragment;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.ProgressBar;

import com.meng.picTools.MainActivity;
import com.meng.picTools.R;
import com.meng.picTools.LogTool;
import com.meng.picTools.lib.AnimatedGifEncoder;
import com.meng.picTools.lib.mengViews.MengEditText;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;

/**
 * gif生成
 */

public class GIFCreator extends Fragment {

    public MengEditText mengEtFrameDelay;
    private ProgressBar progressBar;
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
        ((Button) view.findViewById(R.id.gif_creator_finish)).setOnClickListener(listenerBtnClick);
        editFrameAdapter = new EditFrameAdapter(getActivity(), selectedImages);
        listView.setAdapter(editFrameAdapter);
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(final AdapterView<?> parent, View v, int position, long id) {
                final EditText editTextName = new EditText(getActivity());
                final GIFFrame personInfo = (GIFFrame) parent.getItemAtPosition(position);
                editTextName.setText(personInfo.delay);
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
    }

    View.OnClickListener listenerBtnClick = new View.OnClickListener() {

        @Override
        public void onClick(View v) {
            switch (v.getId()) {
                case R.id.gif_creator_add:
                    Intent intent = new Intent(getActivity(), GIFSelectFrameActivity.class);
                    getActivity().startActivityForResult(intent, 9961);
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
                                    localAnimatedGifEncoder.addFrame(gifFrame.bitmap);
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
                }
            }
        });
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == 9961 && resultCode == Activity.RESULT_OK && data.getData() != null) {
            LogTool.t("add frame ok");
        }
        super.onActivityResult(requestCode, resultCode, data);
    }

}
