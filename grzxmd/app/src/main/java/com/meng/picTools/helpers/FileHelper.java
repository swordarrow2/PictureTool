package com.meng.picTools.helpers;

import android.content.Context;
import android.content.Intent;
import android.graphics.*;
import android.net.Uri;
import android.os.*;

import java.io.*;
import java.util.*;

public class FileHelper {
    private static final String exDir = Environment.getExternalStorageDirectory().getAbsolutePath() + "/Pictures/picTool/";
    private static Context context;

    public static void init(Context context) {
        FileHelper.context = context;
    }

    public static File getFolder(FileType type) {
        File f = new File(exDir + type.toString());
        if (!f.exists()) f.mkdirs();
        return f;
    }

    public static String getFileAbsPath(String name, FileType type) {
        return getFolder(type) + "/" + name;
    }

    public static String getFileAbsPath(FileType type) {
        return getFolder(type) + "/" + (SharedPreferenceHelper.getBoolean("useTimeStamp") ? String.valueOf(System.currentTimeMillis() / 1000) : new Date().toString()) + ".png";
    }


    public static String getPreDownloadJsonPath() {
        return Environment.getExternalStorageDirectory().getAbsolutePath() + "/pixivLike.json";
    }

    public static String saveBitmap(Bitmap bmp, FileType t) {
        String fileAbsPath = getFolder(t).getAbsolutePath() +"/"+ (SharedPreferenceHelper.getBoolean("useTimeStamp") ? String.valueOf(System.currentTimeMillis() / 1000) : new Date().toString()) + ".png";
        File f = new File(fileAbsPath);
        try {
            f.createNewFile();
            FileOutputStream fOut = new FileOutputStream(f);
            bmp.compress(Bitmap.CompressFormat.PNG, 100, fOut);
            fOut.flush();
            fOut.close();
            context.sendBroadcast(new Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE, Uri.fromFile(f)));
        } catch (IOException e) {
            return null;
        }
        return f.getAbsolutePath();
    }

}
