package com.meng.tencos.ui;

import android.*;
import android.app.*;
import android.content.*;
import android.content.pm.*;
import android.net.*;
import android.os.*;
import android.support.v4.app.*;
import android.support.v4.content.*;
import android.support.v7.app.*;
import android.text.*;
import android.util.*;
import android.view.*;
import android.widget.*;
import com.meng.tencos.*;
import com.meng.tencos.adapter.*;
import com.meng.tencos.bean.*;
import com.meng.tencos.utils.*;
import com.tencent.cos.model.*;
import com.tencent.cos.task.listener.*;
import com.tencent.cos.utils.*;
import java.io.*;
import java.net.*;
import java.util.*;

import android.support.v7.app.AlertDialog;
import com.meng.tencos.R;

public class MainList extends android.app.Fragment implements ICmdTaskListener, IDownloadTaskListener, IUploadTaskListener, View.OnClickListener, AdapterView.OnItemClickListener {

    private static final int REQUEST_EXTERNAL_STORAGE = 1;
    private static String[] PERMISSIONS_STORAGE = {Manifest.permission.READ_EXTERNAL_STORAGE, Manifest.permission.WRITE_EXTERNAL_STORAGE, Manifest.permission.READ_PHONE_STATE, Manifest.permission.MOUNT_UNMOUNT_FILESYSTEMS};

    private BizService bizService;
    private ListView lv_files;
    private List<FileItem> files = new ArrayList<FileItem>();
    private List<FileItem> move_files = new ArrayList<FileItem>();
    private FileItemAdapter adapter;
    private Toast mToast;
    final String savePath = Environment.getExternalStorageDirectory().getAbsolutePath() + File.separator + "test_download";
    public String currentPath = "/";
    private LinearLayout ll_upload, ll_create, ll_download, ll_delete, ll_more, ll_bottom, ll_move;
    private final int OPEN_FILE_CODE = 1;
    private ProgressBar pb;
    private TextView tv_empty, move_cancel, move_create, move, copy;

	
	@Override
    public View onCreateView(LayoutInflater inflater,ViewGroup container,Bundle savedInstanceState){
        return inflater.inflate(R.layout.activity_main,container,false);
    }

    @Override
    public void onViewCreated(View view,Bundle savedInstanceState){
        super.onViewCreated(view,savedInstanceState);
        checkPermission();
        initView(view);
        initData();
    }

    private void initData() {
        listDir(currentPath);
    }

    private void initView(View v) {
        bizService = BizService.getInstance();
        mToast = Toast.makeText(getActivity(), "", Toast.LENGTH_SHORT);

        lv_files = (ListView)v. findViewById(R.id.lv_files);
        adapter = new FileItemAdapter(getActivity(), files);
        lv_files.setAdapter(adapter);
        lv_files.setOnItemClickListener(this);
        ll_create = (LinearLayout)v. findViewById(R.id.create);
        ll_create.setOnClickListener(this);
        ll_upload = (LinearLayout) v.findViewById(R.id.upload);
        ll_upload.setOnClickListener(this);
        ll_delete = (LinearLayout)v. findViewById(R.id.delete);
        ll_delete.setOnClickListener(this);
        ll_download = (LinearLayout)v. findViewById(R.id.download);
        ll_download.setOnClickListener(this);
        ll_more = (LinearLayout)v. findViewById(R.id.more);
        ll_more.setOnClickListener(this);
        ll_move = (LinearLayout)v. findViewById(R.id.ll_move);
        ll_bottom = (LinearLayout)v. findViewById(R.id.ll_bottom);
        pb = (ProgressBar)v. findViewById(R.id.pb);
        tv_empty = (TextView) v.findViewById(R.id.tv_empty);
        move_cancel = (TextView) v.findViewById(R.id.move_cancel);
        move_cancel.setOnClickListener(this);
        move_create = (TextView)v. findViewById(R.id.move_create);
        move_create.setOnClickListener(this);
        move = (TextView) v.findViewById(R.id.move);
        move.setOnClickListener(this);
        copy = (TextView)v. findViewById(R.id.copy);
        copy.setOnClickListener(this);

    }

    @Override
    public void onProgress(COSRequest cosRequest, long currentSize, long totalSize) {
        final int progress = (int) ((100.00 * currentSize) / totalSize);
        getActivity().runOnUiThread(new Runnable() {
            @Override
            public void run() {
                pb.setVisibility(View.VISIBLE);
                pb.setProgress(progress);
                if (progress == 100) {
                    pb.setVisibility(View.GONE);
                }
            }
        });

    }

    @Override
    public void onCancel(COSRequest cosRequest, COSResult cosResult) {
        String result = "code =" + cosResult.code + "; msg =" + cosResult.msg;
        Log.w("XIAO", result);
    }

    @Override
    public void onSuccess(final COSRequest cosRequest, final COSResult cosResult) {
        getActivity(). runOnUiThread(new Runnable() {
            @Override
            public void run() {
                if (pb.getVisibility() == View.VISIBLE) {
                    pb.setVisibility(View.GONE);
                }
                String simpleName = cosRequest.getClass().getSimpleName();
                if (simpleName.equals("ListDirRequest")) {
                    if (!files.isEmpty())
                        files.clear();
                    DirUtil.getData(cosResult, files);
                    if (files.size() == 0) {
                        tv_empty.setVisibility(View.VISIBLE);
                    } else {
                        tv_empty.setVisibility(View.GONE);
                        adapter.notifyDataSetChanged();
                    }
                }
                if (simpleName.equals("DeleteObjectRequest") || simpleName.equals("RemoveEmptyDirRequest") || simpleName.equals("CreateDirRequest") || simpleName.equals("PutObjectRequest") || simpleName.equals("MoveObjectRequest") || simpleName.equals("CopyObjectRequest")) {
                    listDir(currentPath);
                }
                if (simpleName.equals("GetObjectRequest")) {
                    ObjectUtil.renameFile(cosRequest.getDownloadUrl(), savePath);
                    showTip("下载成功");
                }
            }
        });
    }

    @Override
    public void onFailed(COSRequest cosRequest, COSResult cosResult) {
        String str;
        switch (cosResult.code) {
            case -197:
                str = "文件不存在或文件夹非空";
                break;
            default:
                str = cosResult.msg;
                break;
        }
        showTip(str);
        String result = "code =" + cosResult.code + "; msg =" + cosResult.msg;
        Log.w("XIAO", result);
    }


    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.create:
                showDialog();
                break;
            case R.id.move_create:
                showDialog();
                break;
            case R.id.upload:
                onAdd();
                break;
            case R.id.download:
                boolean hasFile = false;
                for (FileItem item : files) {
                    if (item.isChecked() && item.getType() == 0) {
                        hasFile = true;
                    }
                }
                if (!hasFile) {
                    showTip("请选择待下载的文件");
                    return;
                }
                onDownload();
                break;
            case R.id.delete:
                boolean hasChecked = false;
                for (FileItem item : files) {
                    if (item.isChecked()) {
                        hasChecked = true;
                    }
                }
                if (!hasChecked) {
                    showTip("请选择待删除的文件");
                    return;
                }
                onDelete();
                break;
            case R.id.more:
                for (FileItem item : files) {
                    if (item.isChecked() && item.getType() == 0) {
                        move_files.add(item);
                    }
                }
                if (move_files.size() > 0) {
                    ll_bottom.setVisibility(View.GONE);
                    ll_move.setVisibility(View.VISIBLE);
                } else {
                    showTip("请选择待操作的文件");
                }
                break;
            case R.id.move_cancel:
                move_files.clear();
                ll_bottom.setVisibility(View.VISIBLE);
                ll_move.setVisibility(View.GONE);
                break;
            case R.id.move:
                moveObject();
                break;
            case R.id.copy:
                copyObject();
                break;
            default:
                break;
        }
    }

    private void copyObject() {
        CopyObjectRequest request;
        for (FileItem item : move_files) {
            try {
                String url = ObjectUtil.getCosPath(item.getDownloadUrl());
                String cosPathSrc = URLDecoder.decode(url, "utf-8");
                String cosPathDest = currentPath.concat(item.getFileName());
                request = ObjectUtil.getCopyObjRequest(bizService, cosPathSrc, cosPathDest);
                request.setListener(this);
                bizService.cosClient.copyObjectAsyn(request);
            } catch (UnsupportedEncodingException e) {
                e.printStackTrace();
            }
        }
        move_files.clear();
        ll_bottom.setVisibility(View.VISIBLE);
        ll_move.setVisibility(View.GONE);
    }


    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        if (files.get(position).getType() == 1) {
            currentPath = currentPath.concat(files.get(position).getFileName()).concat(File.separator);
            listDir(currentPath);
        }
    }

    private void onAdd() {
        Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
        intent.setType("*/*");//无类型限制
        intent.addCategory(Intent.CATEGORY_OPENABLE);
        try {
            startActivityForResult(intent, OPEN_FILE_CODE);
        } catch (android.content.ActivityNotFoundException ex) {
            showTip("亲，木有文件管理器啊-_-!!");
        }
    }

    private void onCreateDir(final String dirName) {
        CreateDirRequest request = DirUtil.getCreateDirRequest(bizService, currentPath.concat(dirName));
        request.setListener(this);
        bizService.cosClient.createDirAsyn(request);
    }

    private void onDelete() {
        for (FileItem item : files) {
            if (item.isChecked()) {
                if (item.getType() == 0) {
                    try {
                        String url = ObjectUtil.getCosPath(item.getDownloadUrl());
                        String decode = URLDecoder.decode(url, "utf-8");
                        DeleteObjectRequest request = ObjectUtil.getDeleteObjRequest(bizService, decode);
                        request.setListener(this);
                        bizService.cosClient.deleteObjectAsyn(request);
                    } catch (UnsupportedEncodingException e) {
                        e.printStackTrace();
                    }
                } else {
                    String url = currentPath.concat(item.getFileName()).concat(File.separator).replaceFirst("/", "");
                    RemoveEmptyDirRequest request = DirUtil.getRemoveDirRequest(bizService, url);
                    request.setListener(this);
                    bizService.cosClient.removeEmptyDir(request);
                }
            }
        }
    }

    private void onDownload() {
        GetObjectRequest getObjectRequest;
        for (FileItem item : files) {
            if (item.isChecked() && item.getType() == 0) {
                getObjectRequest = ObjectUtil.getDownloadObjRequest(item.getDownloadUrl(), savePath);
                getObjectRequest.setListener(this);
                bizService.cosClient.getObjectAsyn(getObjectRequest);
            }
        }
    }

    private void listDir(final String dirName) {
        //前缀查询的字符串,为空表示不进行精确查询
        final String prefix = "";
        ListDirRequest dirRequest = DirUtil.getListDirRequest(bizService, dirName, prefix);
        dirRequest.setListener(this);
        bizService.cosClient.listDirAsyn(dirRequest);
    }


    private void showTip(final String str) {
        mToast.setText(str);
        mToast.show();
    }

    public boolean onBackPressed() {
        if (currentPath.equals("/") || currentPath.equals(""))
           return false;
        currentPath = currentPath.substring(0, currentPath.lastIndexOf("/", currentPath.length() - 2) + 1);
        listDir(currentPath);
		return true;
    }


    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (resultCode != Activity.RESULT_OK || data == null) {
            return;
        }
        switch (requestCode) {
            case OPEN_FILE_CODE:
                Uri uri = data.getData();
                upload(ObjectUtil.getPath(getActivity(), uri));
                break;
            default:
                break;
        }
    }

    private void upload(String path) {
        if (TextUtils.isEmpty(path)) {
            showTip("请选择文件");
            return;
        }
        String filename = FileUtils.getFileName(path);
        final String cosPath = currentPath.concat(filename); //cos 上的路径
        PutObjectRequest request = ObjectUtil.getUploadObjRequest(bizService, cosPath, path);
        request.setListener(this);
        bizService.cosClient.putObjectAsyn(request);
    }

    private void showDialog() {
        final EditText et = new EditText(getActivity());

        new AlertDialog.Builder(getActivity()).setTitle("请输入目录名:")
                .setIcon(android.R.drawable.ic_dialog_info)
                .setView(et)
                .setPositiveButton("确定", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        onCreateDir(et.getText().toString());
                    }
                })
                .setNegativeButton("取消", null)
                .show();
    }

    private void moveObject() {
        MoveObjectRequest request;
        for (FileItem item : move_files) {
            try {
                String url = ObjectUtil.getCosPath(item.getDownloadUrl());
                String cosPathSrc = URLDecoder.decode(url, "utf-8");
                String cosPathDest = currentPath.concat(item.getFileName());
                request = ObjectUtil.getMoveObjRequest(bizService, cosPathSrc, cosPathDest);
                request.setListener(this);
                bizService.cosClient.moveObjcetAsyn(request);
            } catch (UnsupportedEncodingException e) {
                e.printStackTrace();
            }
        }
        move_files.clear();
        ll_bottom.setVisibility(View.VISIBLE);
        ll_move.setVisibility(View.GONE);
    }

    private void checkPermission() {
        if (ContextCompat.checkSelfPermission(getActivity(), Manifest.permission.MOUNT_UNMOUNT_FILESYSTEMS) != PackageManager.PERMISSION_GRANTED ||
                ContextCompat.checkSelfPermission(getActivity(), Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED ||
			ContextCompat.checkSelfPermission(getActivity(), Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED ||
			ContextCompat.checkSelfPermission(getActivity(), Manifest.permission.READ_PHONE_STATE) != PackageManager.PERMISSION_GRANTED) {
            //进入到这里代表没有权限.
            if (ActivityCompat.shouldShowRequestPermissionRationale(getActivity(), Manifest.permission.CALL_PHONE)) {
                //已经禁止提示了
                showTip("请在设置中打开应用所需权限");
            } else {
                ActivityCompat.requestPermissions(getActivity(), PERMISSIONS_STORAGE, REQUEST_EXTERNAL_STORAGE);
            }
        }
    }

}
