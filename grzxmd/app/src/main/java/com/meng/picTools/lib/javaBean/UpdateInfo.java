package com.meng.picTools.lib.javaBean;

import android.content.Context;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;

import com.meng.picTools.LogTool;

public class UpdateInfo {

    public boolean error = false;
    private int[] newVersion;
    private int[] nowVersion;
    private String newVersionLink;

    public boolean newFunction = false;
    public boolean optimize = false;
    public boolean bugFix = false;

    public UpdateInfo(Context context) {
        try {
            PackageInfo packageInfo = context.getPackageManager().getPackageInfo(context.getPackageName(), 0);
            String[] nowVersionStr = packageInfo.versionName.split("\\.");
            nowVersion = new int[]{Integer.parseInt(nowVersionStr[0]), Integer.parseInt(nowVersionStr[1]), Integer.parseInt(nowVersionStr[2])};
        } catch (Exception e) {
            LogTool.e("检查更新出错");
            e.printStackTrace();
            error = true;
        }
    }

    public boolean setNewVersionLink(String newVersionLink) {
        try {
            this.newVersionLink = newVersionLink;
            String[] newVersionStr = newVersionLink.substring(newVersionLink.lastIndexOf("/") + 1).split("\\.");
            newVersion = new int[]{Integer.parseInt(newVersionStr[0]), Integer.parseInt(newVersionStr[1]), Integer.parseInt(newVersionStr[2])};
            newFunction = newVersion[0] > nowVersion[0];
            optimize = newVersion[1] > nowVersion[1];
            bugFix = newVersion[2] > nowVersion[2];
            return true;
        } catch (Exception e) {
            LogTool.e(e.toString());
            return false;
        }
    }

    public String getNewVersionLink() {
        return newVersionLink;
    }

    public String getVersionName() {
        return newVersionLink.substring(newVersionLink.lastIndexOf("/") + 1);
    }

    public String getUpdateNote() {
        StringBuilder stringBuilder = new StringBuilder("本次更新的内容:\n");
        if (newFunction) stringBuilder.append("添加新功能\n");
        if (optimize) stringBuilder.append("性能优化\n");
        if (bugFix) stringBuilder.append("修理bug\n");
        stringBuilder.append("现在要更新吗");
        return stringBuilder.toString();
    }

}
