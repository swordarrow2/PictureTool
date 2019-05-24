package com.meng.picTools.sauceNao;

import android.app.*;
import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.*;
import android.support.annotation.*;
import android.view.*;
import android.view.animation.*;
import android.widget.*;

import com.meng.picTools.*;
import com.meng.picTools.helpers.ContentHelper;
import com.meng.picTools.helpers.SharedPreferenceHelper;
import com.meng.picTools.lib.MaterialDesign.*;
import com.meng.picTools.qrCode.creator.LogoQRCreator;

import org.jsoup.Connection;
import org.jsoup.Jsoup;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.Date;
import java.util.HashMap;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import android.widget.AdapterView.*;

public class SauceNaoMain extends Fragment {
    private FloatingActionButton mFabSelect;
    private ListView listView;
    public HashMap<String, Bitmap> hashMap = new HashMap<>();
    public ExecutorService threadPool;
    public String uploadBmpAbsPath;
    public boolean running = false;
    AlertDialog alertDialog;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.saucenao_main, container, false);
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        mFabSelect = (FloatingActionButton) view.findViewById(R.id.fab_select);
        listView = (ListView) view.findViewById(R.id.list);
        threadPool = Executors.newFixedThreadPool(Integer.parseInt(SharedPreferenceHelper.getValue("threads", "3")));
        mFabSelect.setOnClickListener(onClickListener);
        mFabSelect.hide(false);
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                mFabSelect.show(true);
                mFabSelect.setShowAnimation(AnimationUtils.loadAnimation(getActivity(), R.anim.show_from_bottom));
                mFabSelect.setHideAnimation(AnimationUtils.loadAnimation(getActivity(), R.anim.hide_to_bottom));
            }
        }, 450);
        listView.setOnItemClickListener(new OnItemClickListener() {

            @Override
            public void onItemClick(AdapterView<?> p1, View p2, int p3, long p4) {
                final PicResults.Result result = (PicResults.Result) p1.getItemAtPosition(p3);
                ListView urlSelect = new ListView(getActivity());
                urlSelect.setAdapter(new ArrayAdapter<String>(getActivity(), android.R.layout.simple_list_item_1, result.mExtUrls));
                urlSelect.setOnItemClickListener(new OnItemClickListener() {

                    @Override
                    public void onItemClick(final AdapterView<?> p1, View p2, final int p3, long p4) {
                        String url = (String) p1.getItemAtPosition(p3);
                        LogTool.t(url);
                        if (url.contains("illust_id")) {
                            MainActivity.instence.showPixivDownloadFragment(true);
                            MainActivity.instence.pixivDownloadMainFragment.editTextURL.setText(result.mExtUrls.get(1));
                        } else {
                            ClipboardManager clipboardManager = (ClipboardManager) getActivity().getSystemService(Context.CLIPBOARD_SERVICE);
                            ClipData clipData = ClipData.newPlainText("text", url);
                            clipboardManager.setPrimaryClip(clipData);
                            LogTool.t("已复制到剪贴板");
                        }
                        alertDialog.dismiss();
                    }
                });
                alertDialog = new AlertDialog.Builder(getActivity())
                        .setView(urlSelect)
                        .setTitle("这是一个标题")
                        .setNegativeButton("我好了", null).show();
            }
        });
    }

    View.OnClickListener onClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View view) {
            switch (view.getId()) {
                case R.id.fab_select:
                    if (running) return;
                    running = true;
                    mFabSelect.setShowProgressBackground(true);
                    mFabSelect.setIndeterminate(true);
                    MainActivity.instence.selectImage(SauceNaoMain.this);
                    break;
            }
        }
    };

    private void cropPhoto(Uri uri) {
        Intent intent = new Intent("com.android.camera.action.CROP");
        intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
        intent.addFlags(Intent.FLAG_GRANT_WRITE_URI_PERMISSION);
        intent.setDataAndType(uri, "image/*");
        intent.putExtra("crop", "true");
        intent.putExtra("outputX", 300);
        intent.putExtra("outputY", 300);
        intent.putExtra("return-data", true);
        startActivityForResult(intent, MainActivity.instence.CROP_REQUEST_CODE);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (data != null && resultCode == Activity.RESULT_OK) {
            //      if (requestCode == MainActivity.instence.SELECT_FILE_REQUEST_CODE) {
            //  String path = ContentHelper.absolutePathFromUri(getActivity().getApplicationContext(),
            //        cropPhoto(data.getData());
            //	  } else
            if (requestCode == MainActivity.instence.SELECT_FILE_REQUEST_CODE) {
				/*    Bundle bundle = data.getExtras();
				 if (bundle != null) {
				 Bitmap bmp = bundle.getParcelable("data");
				 if (bmp == null) {
				 mFabSelect.hideProgress();
				 LogTool.e("选择图片出错");
				 running = false;
				 }
				 */
                uploadBmpAbsPath = ContentHelper.absolutePathFromUri(getActivity(), data.getData());//= Environment.getExternalStorageDirectory().getAbsolutePath() + "/Pictures/picTool/search_tmp.png";
				/*	File f = new File(uploadBmpAbsPath);
				 try {
				 f.createNewFile();
				 FileOutputStream fOut = new FileOutputStream(f);
				 bmp.compress(Bitmap.CompressFormat.PNG, 100, fOut);
				 fOut.flush();
				 fOut.close();
				 //  getActivity().sendBroadcast(new Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE, Uri.fromFile(f)));
				 } catch (IOException e) {
				 mFabSelect.hideProgress();
				 running = false;
				 LogTool.e("裁剪图片出错");
				 }
				 LogTool.t("图片添加成功");
				 */
                mFabSelect.setImageResource(R.drawable.ic_progress);
                new Thread(new Runnable() {
                    @Override
                    public void run() {
                        FileInputStream fInputStream;
                        try {
                            fInputStream = new FileInputStream(uploadBmpAbsPath);
                            Connection.Response response = Jsoup.connect("https://saucenao.com/search.php?db=" + 999)
                                    .timeout(60000).data("file", "image.png", fInputStream).method(Connection.Method.POST).execute();
                            if (response.statusCode() != 200) {
                                running = false;
                                LogTool.e("发生错误" + response.statusCode());
                                return;
                            }
                            PicResults mResults = new PicResults(Jsoup.parse(response.body()));
                            final ResultAdapter resultAdapter = new ResultAdapter(getActivity(), mResults.getResults());
                            getActivity().runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    mFabSelect.setImageResource(R.drawable.fab_add);
                                    listView.setAdapter(resultAdapter);
                                    mFabSelect.hideProgress();
                                    running = false;
                                }
                            });
                        } catch (Exception e) {
                            LogTool.e(e.toString());
                        }
                    }
                }).start();
            } else {
                mFabSelect.hideProgress();
                mFabSelect.setImageResource(R.drawable.ic_progress);
                LogTool.t("取消了选择图片");
                running = false;
            }
            //	  }
        } else if (resultCode == Activity.RESULT_CANCELED) {
            mFabSelect.hideProgress();
            mFabSelect.setImageResource(R.drawable.ic_progress);
            running = false;
            Toast.makeText(getActivity().getApplicationContext(), "取消选择图片", Toast.LENGTH_SHORT).show();
        } else {
            MainActivity.instence.selectImage(this);
        }
        super.onActivityResult(requestCode, resultCode, data);
    }

}
