package com.meng.tencos.utils;


import android.text.*;
import com.meng.tencos.*;
import com.meng.tencos.bean.*;
import com.meng.tencos.ui.*;
import com.tencent.cos.model.*;
import java.text.*;
import java.util.*;
import org.json.*;

/**
 * Created by Gu on 2017/8/1.
 */

public class DirUtil {

    /**
     * @return CreateDirRequest
     * @Listener ICmdTaskListener
     * 创建目录
     */
    public static CreateDirRequest getCreateDirRequest(BizService bizService, String cosPath) {
        /** CreateDirRequest 请求对象 */
        CreateDirRequest createDirRequest = new CreateDirRequest();
        /** 设置Bucket */
        createDirRequest.setBucket(bizService.bucket);
        /** 设置cosPath :远程路径*/
        createDirRequest.setCosPath(cosPath);
        /** 设置sign: 签名，此处使用多次签名 */
        createDirRequest.setSign(bizService.getSign());
        return createDirRequest;
    }

    /**
     * @return RemoveEmptyDirRequest
     * @Listener ICmdTaskListener
     * 删除空目录
     */
    public static RemoveEmptyDirRequest getRemoveDirRequest(BizService bizService, String cosPath) {
        /** RemoveEmptyDirRequest 请求对象，只能删除空文件夹，其他无效 */
        RemoveEmptyDirRequest removeEmptyDirRequest = new RemoveEmptyDirRequest();
        /** 设置Bucket */
        removeEmptyDirRequest.setBucket(bizService.bucket);
        /** 设置cosPath :远程路径*/
        removeEmptyDirRequest.setCosPath(cosPath);
        /** 设置sign: 签名，此处使用单次签名 */
        removeEmptyDirRequest.setSign(bizService.getSignOnce(cosPath));
        return removeEmptyDirRequest;
    }

    /**
     * @return ListDirRequest
     * @Listener ICmdTaskListener
     * 查询目录列表
     */
    public static ListDirRequest getListDirRequest(BizService bizService, String cosPath, String prefix) {
        /** ListDirRequest 请求对象 */
        ListDirRequest listDirRequest = new ListDirRequest();
        /** 设置Bucket */
        listDirRequest.setBucket(bizService.bucket);
        /** 设置cosPath :远程路径*/
        listDirRequest.setCosPath(cosPath);
        /** 设置num :预查询的目录数*/
        listDirRequest.setNum(100);
        /** 设置content: 透传字段，首次拉取必须清空。拉取下一页，需要将前一页返回值中的context透传到参数中*/
        listDirRequest.setContent("");
        /** 设置sign: 签名，此处使用多次签名 */
        listDirRequest.setSign(bizService.getSign());
        /** 设置 prefix: 前缀查询的字符串,开启前缀查询 */
        if (!TextUtils.isEmpty(prefix) && prefix != null) {
            listDirRequest.setPrefix(prefix);
        }
        return listDirRequest;
    }

    public static void getData(COSResult cosResult, List<FileItem> files) {
        ListDirResult listObjectResult = (ListDirResult) cosResult;
        if (listObjectResult.infos != null && listObjectResult.infos.size() > 0) {
            int length = listObjectResult.infos.size();
            String str;
            JSONObject jsonObject;
            for (int i = 0; i < length; i++) {
                str = listObjectResult.infos.get(i);
                try {
                    jsonObject = new JSONObject(str);
                    String time = getTime(jsonObject.optString("ctime"));
                    //创建的时间戳
                    long stamp = Long.parseLong(jsonObject.optString("ctime"));
                    //文件名
                    String fileName = jsonObject.optString("name");
                    //文件后缀
                    String suffix = fileName.substring(fileName.lastIndexOf(".") + 1);
                    //文件下载路径
                    String downloadUrl = jsonObject.optString("source_url");
                    int resId, type;
                    if (jsonObject.has("sha")) {
                        //是文件
                        if (suffix.equals("mp3") || suffix.equals("wav") || suffix.equals("ape") || suffix.equals("flac")) {
                            resId = R.mipmap.music;
                        } else if (suffix.equals("avi") || suffix.equals("mkv") || suffix.equals("mp4") || suffix.equals("rmvb")) {
                            resId = R.mipmap.mkv;
                        } else if (suffix.equals("txt")) {
                            resId = R.mipmap.txt;
                        } else if (suffix.equals("apk")) {
                            resId = R.mipmap.apk;
                        } else if (suffix.equals("pdf")) {
                            resId = R.mipmap.pdf;
                        } else if (suffix.equals("rar")) {
                            resId = R.mipmap.rar;
                        } else if (suffix.equals("zip")) {
                            resId = R.mipmap.zip;
                        } else {
                            resId = R.mipmap.epub;
                        }
                        type = 0;
                    } else {
                        //是文件夹
                        resId = R.mipmap.folder;
                        fileName = fileName.replaceAll("/", "");
                        type = 1;
                    }

                    files.add(new FileItem(resId, fileName, time, stamp, type,downloadUrl));
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
            if (files.size() >= 2)
                Collections.sort(files, new FileComparator());//通过重写Comparator的实现类
        }
    }

    /**
     * 调用此方法输入所要转换的时间戳输入例如（1402733340）输出（"2014-06-14  16:09:00"）
     */
    private static String getTime(String time) {
        SimpleDateFormat sdr = new SimpleDateFormat("yyyy-MM-dd HH:mm");
        @SuppressWarnings("unused")
        long lcc = Long.valueOf(time);
        int i = Integer.parseInt(time);
        String times = sdr.format(new Date(i * 1000L));
        return times;
    }

}
