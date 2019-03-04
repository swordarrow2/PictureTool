package com.meng.pixivGifDownloader;

public class Data {
    static class preferenceKeys {
        public static String mainPreferenceName = "pre";
        public static String downloadBigPicture = "bigpicture";
        public static String autoInput = "autoInput";
        public static String zipPath = "zipPath";
        public static String deleteZipAfterMakeGif = "deleteZipAfterMakeGif";
        public static String gifScale = "option";
        public static String gifPath = "gifPath";
        public static String useJava = "useJava";
        public static String tmpPath = "tmpPath";
        public static String cleanTmpOnStopWatch = "cleantemp";
        public static String cleanTmpFilesNow = "cleannow";
        public static String cookievalue = "cookievalue";
    }

    static class intentKeys {
        public static String url = "url";
        public static String result = "result";
        public static String fileName = "fileName";
    }

    static class status {
        public static String success = "success";
    }
}
