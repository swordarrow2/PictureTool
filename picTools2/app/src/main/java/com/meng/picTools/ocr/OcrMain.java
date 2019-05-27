package com.meng.picTools.ocr;

import android.app.*;
import android.content.*;
import android.os.*;
import android.view.*;
import android.view.animation.*;
import android.widget.*;
import android.widget.AdapterView.*;
import com.google.gson.*;
import com.meng.picTools.*;
import com.meng.picTools.helpers.*;
import com.meng.picTools.lib.MaterialDesign.*;
import java.io.*;
import java.util.*;
import org.json.*;

public class OcrMain extends Fragment {
    public static final String APP_ID = "10173140";
    public static final String SECRET_ID = "AKIDRmqfEXsNxHOFBrrpx2rVzDG3arCPs2Uh";
    public static final String SECRET_KEY = "71hGdBXfZIG1wWSLNI2YtCJrz62rIe8t";
    public static final String USER_ID = "2856986197"; // qq号
    public Youtu faceYoutu = new Youtu(APP_ID, SECRET_ID, SECRET_KEY, Youtu.API_YOUTU_END_POINT, USER_ID);
    private FloatingActionButton mFabSelect;
	private ListView listView;
    public boolean running = false;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.saucenao_main, container, false);
	  }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        mFabSelect = (FloatingActionButton) view.findViewById(R.id.fab_select);
		listView = (ListView) view.findViewById(R.id.list);
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
			  public void onItemClick(final AdapterView<?> p1, View p2, final int p3, long p4) {
				  String url = (String) p1.getItemAtPosition(p3);
				  ClipboardManager clipboardManager = (ClipboardManager) getActivity().getSystemService(Context.CLIPBOARD_SERVICE);
				  ClipData clipData = ClipData.newPlainText("text", url);
				  clipboardManager.setPrimaryClip(clipData);
				  LogTool.t("已复制到剪贴板");					  
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
				  MainActivity.instence.selectImage(OcrMain.this);
				  break;
			  }
		  }
	  };

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (data.getData() != null && resultCode == Activity.RESULT_OK) {
            if (requestCode == MainActivity.instence.SELECT_FILE_REQUEST_CODE) {
                //    uploadBmpAbsPath = ContentHelper.absolutePathFromUri(getActivity(), data.getData());//= Environment.getExternalStorageDirectory().getAbsolutePath() + "/Pictures/picTool/search_tmp.png";
                final String path = ContentHelper.absolutePathFromUri(getActivity(), data.getData());
                if (path == null) {
					running = false;
                    LogTool.e("select pic error");
                    return;
				  }
                new Thread(new Runnable() {
					  @Override
					  public void run() {
						  JSONObject response = null;
						  File image = new File(path);
						  try {
							  response = faceYoutu.GeneralOcr(image.getPath());
							} catch (Exception e) {
							  e.printStackTrace();
							}
						  OcrJavaBean ocrJavaBean = new Gson().fromJson(response.toString(), OcrJavaBean.class);
						  ArrayList<OcrJavaBean.Items> items = ocrJavaBean.items;
						  final ArrayList<String> strings=new ArrayList<>();
						  for (OcrJavaBean.Items s : items) {
							  strings.add(s.itemstring);
							}
						  getActivity().runOnUiThread(new Runnable() {
								@Override
								public void run() {
									listView.setAdapter(new ArrayAdapter<String>(getActivity(), android.R.layout.simple_list_item_1, strings));
									mFabSelect.setImageResource(R.drawable.fab_add);
									mFabSelect.hideProgress();
									running = false;
								  }
							  });
						}
					}).start();
			  }
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
