package com.meng.picTools.pixivGifDownloader;

import android.graphics.Bitmap;
import android.util.Log;

import com.meng.picTools.MainActivity;
import com.meng.picTools.R;
import com.meng.picTools.qrtools.log;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Arrays;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;

public class UnzipThread extends Thread {
    private File zipFile;
    public String zipName;
    private int filesCountNow = 0;
    private int filesCount = 0;
    public boolean isUnzipSuccess = false;
    private File frameFileFolder;

    public UnzipThread(File zipFile) {
        this.zipFile = zipFile;
        zipName = zipFile.getAbsolutePath().substring(zipFile.getAbsolutePath().lastIndexOf("/") + 1, zipFile.getAbsolutePath().lastIndexOf("."));	
    }

    public int getFilesCount() {
        return filesCount;
    }

    public int getFilesCountNow() {
        return filesCountNow;
    }

    public File getFrameFileFolder() {
        return frameFileFolder;
    }

    @Override
    public void run() {
        try {
            byte[] buffer = new byte[1024];
            frameFileFolder = new File(MainActivity.instence.getTmpFolder() + zipName);
            if (!frameFileFolder.exists()) {
                frameFileFolder.mkdirs();
            }
            File nomedia = new File(frameFileFolder.getParent() + File.separator + ".nomedia");
            if (!nomedia.exists()) {
                try {
                    nomedia.createNewFile();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
            filesCount = countFilesInZip(zipFile);
            ZipInputStream zis = new ZipInputStream(new FileInputStream(zipFile));
            ZipEntry ze = zis.getNextEntry();
            while (ze != null) {
                filesCountNow++;
                String fileName = ze.getName();
                File frameFile = new File(frameFileFolder.getAbsolutePath() + File.separator + fileName);
                FileOutputStream nfos = new FileOutputStream(frameFile);
                int len;
                while ((len = zis.read(buffer)) > 0) {
                    nfos.write(buffer, 0, len);
                }
                nfos.close();
                ze = zis.getNextEntry();
            }
            isUnzipSuccess = true;
        } catch (Exception e) {
            log.t(e.toString());
        }
    }

    private int countFilesInZip(File zipFile) {
        int filesCount = 0;
        try {
            ZipInputStream zis = new ZipInputStream(new FileInputStream(zipFile));
            ZipEntry ze = zis.getNextEntry();
            while (ze != null) {
                filesCount++;
                ze = zis.getNextEntry();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return filesCount;
    }

}
