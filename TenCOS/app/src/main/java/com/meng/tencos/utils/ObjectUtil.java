package com.meng.tencos.utils;


import android.content.*;
import android.database.*;
import android.net.*;
import android.os.*;
import android.provider.*;
import com.meng.tencos.ui.*;
import com.tencent.cos.model.*;
import com.tencent.cos.utils.*;
import java.io.*;
import java.net.*;

/**
 * Created by Gu on 2017/8/1.
 */

public class ObjectUtil {

    /**
     * @return PutObjectRequest
     * @Listener IUploadTaskListener
     * 大文件分片上传 : >=20M的文件，需要使用分片上传，否则会出错
     */
    public static PutObjectRequest getUploadObjRequest(BizService bizService, String cosPath, String localPath) {
        /** PutObjectRequest 请求对象 */
        PutObjectRequest putObjectRequest = new PutObjectRequest();
        /** 设置Bucket */
        putObjectRequest.setBucket(bizService.bucket);
        /** 设置cosPath :远程路径*/
        putObjectRequest.setCosPath(cosPath);
        /** 设置srcPath: 本地文件的路径 */
        putObjectRequest.setSrcPath(localPath);
        /** 设置 insertOnly: 是否上传覆盖同名文件*/
        putObjectRequest.setInsertOnly("1");
        /** 设置sign: 签名，此处使用多次签名 */
        putObjectRequest.setSign(bizService.getSign());

        /** 设置sliceFlag: 是否开启分片上传 */
        putObjectRequest.setSliceFlag(true);
        /** 设置slice_size: 若使用分片上传，设置分片的大小 */
        putObjectRequest.setSlice_size(1024 * 1024);

        /** 设置sha: 是否上传文件时带上sha，一般带上sha*/
        putObjectRequest.setSha(SHA1Utils.getFileSha1(localPath));
        return putObjectRequest;
    }

    /**
     * @return GetObjectRequest
     * @Listener IDownloadTaskListener
     * 文件下载
     */
    public static GetObjectRequest getDownloadObjRequest(final String url, final String savePath) {
        /** GetObjectRequest 请求对象 */
        GetObjectRequest getObjectRequest = new GetObjectRequest(url, savePath);
        //若是设置了防盗链则需要多次有效签名；否则，不需要签名.
        getObjectRequest.setSign(null);
        return getObjectRequest;
    }

    /**
     * @return DeleteObjectRequest
     * @Listener ICmdTaskListener
     * 文件删除
     */
    public static DeleteObjectRequest getDeleteObjRequest(BizService bizService, String cosPath) {
        /** DeleteObjectRequest 请求对象 */
        DeleteObjectRequest deleteObjectRequest = new DeleteObjectRequest();
        /** 设置Bucket */
        deleteObjectRequest.setBucket(bizService.bucket);
        /** 设置cosPath :远程路径*/
        deleteObjectRequest.setCosPath(cosPath);
        /** 设置sign: 签名，此处使用单次签名 */
        deleteObjectRequest.setSign(bizService.getSignOnce(cosPath));
        return deleteObjectRequest;
    }

    /**
     * @return CopyObjectRequest
     * @Listener ICmdTaskListener
     * 文件复制
     */
    public static CopyObjectRequest getCopyObjRequest(BizService bizService, String cosPathSrc, String cosPathDest) {
        CopyObjectRequest copyObjectRequest = new CopyObjectRequest();
        copyObjectRequest.setBucket(bizService.bucket);
        copyObjectRequest.setCosPath(cosPathSrc);
        copyObjectRequest.setDestFileId(cosPathDest);
        copyObjectRequest.setToOverWrite(1);
        copyObjectRequest.setSign(bizService.getSignOnce(cosPathSrc));
        return copyObjectRequest;
    }

    /**
     * @return MoveObjectRequest
     * @Listener ICmdTaskListener
     * 文件移动
     */
    public static MoveObjectRequest getMoveObjRequest(BizService bizService, String cosPathSrc, String cosPathDest) {
        /** MoveObjectRequest 请求对象 */
        MoveObjectRequest moveObjectRequest = new MoveObjectRequest();
        /** 设置Bucket */
        moveObjectRequest.setBucket(bizService.bucket);
        /** 设置cosPath :远程源文件路径*/
        moveObjectRequest.setCosPath(cosPathSrc);
        /** 设置dest_fileId :远程目标文件路径*/
        moveObjectRequest.setDestFileId(cosPathDest);
        /** 设置to_Over_Write :是否覆盖 0：不覆盖 1：覆盖*/
        moveObjectRequest.setToOverWrite(1);
        /** 设置sign: 签名，此处使用单次签名 */
        moveObjectRequest.setSign(bizService.getSignOnce(cosPathSrc));
        return moveObjectRequest;
    }

    /**
     * @return GetObjectMetadataRequest
     * @Listener ICmdTaskListener
     * 指定文件或目录查询
     */
    public static GetObjectMetadataRequest getObjMetaRequest(BizService bizService, String cosPath) {
        /** GetObjectMetadataRequest 请求对象 */
        GetObjectMetadataRequest getObjectMetadataRequest = new GetObjectMetadataRequest();
        /** 设置Bucket */
        getObjectMetadataRequest.setBucket(bizService.bucket);
        /** 设置cosPath :远程路径*/
        getObjectMetadataRequest.setCosPath(cosPath);
        /** 设置sign: 签名，此处使用多次签名 */
        getObjectMetadataRequest.setSign(bizService.getSign());
        return getObjectMetadataRequest;
    }

    public static String getPath(final Context context, final Uri uri) {

        final boolean isKitKat = Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT;

        // DocumentProvider
        if (isKitKat && DocumentsContract.isDocumentUri(context, uri)) {
            // ExternalStorageProvider
            if (isExternalStorageDocument(uri)) {
                final String docId = DocumentsContract.getDocumentId(uri);
                final String[] split = docId.split(":");
                final String type = split[0];

                if ("primary".equalsIgnoreCase(type)) {
                    return Environment.getExternalStorageDirectory() + "/" + split[1];
                }
            }
            // DownloadsProvider
            else if (isDownloadsDocument(uri)) {

                final String id = DocumentsContract.getDocumentId(uri);
                final Uri contentUri = ContentUris.withAppendedId(
                        Uri.parse("content://downloads/public_downloads"), Long.valueOf(id));

                return getDataColumn(context, contentUri, null, null);
            }
            // MediaProvider
            else if (isMediaDocument(uri)) {
                final String docId = DocumentsContract.getDocumentId(uri);
                final String[] split = docId.split(":");
                final String type = split[0];

                Uri contentUri = null;
                if ("image".equals(type)) {
                    contentUri = MediaStore.Images.Media.EXTERNAL_CONTENT_URI;
                } else if ("video".equals(type)) {
                    contentUri = MediaStore.Video.Media.EXTERNAL_CONTENT_URI;
                } else if ("audio".equals(type)) {
                    contentUri = MediaStore.Audio.Media.EXTERNAL_CONTENT_URI;
                }

                final String selection = "_id=?";
                final String[] selectionArgs = new String[]{split[1]};

                return getDataColumn(context, contentUri, selection, selectionArgs);
            }
        }
        // MediaStore (and general)
        else if ("content".equalsIgnoreCase(uri.getScheme())) {

            // Return the remote address
            if (isGooglePhotosUri(uri))
                return uri.getLastPathSegment();

            return getDataColumn(context, uri, null, null);
        }
        // File
        else if ("file".equalsIgnoreCase(uri.getScheme())) {
            return uri.getPath();
        }

        return null;
    }

    public static String getDataColumn(Context context, Uri uri, String selection, String[] selectionArgs) {

        Cursor cursor = null;
        final String column = "_data";
        final String[] projection = {column};

        try {
            cursor = context.getContentResolver().query(uri, projection, selection, selectionArgs,
                    null);
            if (cursor != null && cursor.moveToFirst()) {
                final int index = cursor.getColumnIndexOrThrow(column);
                return cursor.getString(index);
            }
        } finally {
            if (cursor != null)
                cursor.close();
        }
        return null;
    }

    public static boolean isExternalStorageDocument(Uri uri) {
        return "com.android.externalstorage.documents".equals(uri.getAuthority());
    }

    public static boolean isDownloadsDocument(Uri uri) {
        return "com.android.providers.downloads.documents".equals(uri.getAuthority());
    }

    public static boolean isMediaDocument(Uri uri) {
        return "com.android.providers.media.documents".equals(uri.getAuthority());
    }

    public static boolean isGooglePhotosUri(Uri uri) {
        return "com.google.android.apps.photos.content".equals(uri.getAuthority());
    }


    public static void renameFile(String url, String savePath) {
        String str = url.substring(url.lastIndexOf("/") + 1);
        StringBuilder localUrl = new StringBuilder(savePath);
        StringBuilder destUrl = new StringBuilder(savePath);
        String fileName = null;
        try {
            fileName = URLDecoder.decode(str, "utf-8");
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
        localUrl.append(File.separator).append(str);
        destUrl.append(File.separator).append(fileName);
        File file = new File(localUrl.toString());
        File destFile = new File(destUrl.toString());
        file.renameTo(destFile);
    }

    public static String getCosPath(String url) {
        String strStart = "com/";
        if (url.indexOf(strStart) < 0) {
            return "";
        }
        return url.substring(url.indexOf(strStart) + strStart.length());
    }

}
