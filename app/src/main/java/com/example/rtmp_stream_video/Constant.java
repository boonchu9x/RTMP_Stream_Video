package com.example.rtmp_stream_video;

import android.os.Environment;

import java.io.File;
import java.io.FileOutputStream;

public class Constant {
    public static final int RECORS_AUDIO = 100;
    public static final int CAMERA = 101;
    public static final int WRITE_STORAGE = 102;

    public static int[] intPermission = {Constant.RECORS_AUDIO, Constant.CAMERA, Constant.WRITE_STORAGE};

    public static String URL_SERVER_LIVESTREAM = "rtmp://e24a83.entrypoint.cloud.wowza.com/app-d61c/";
    public static String SERVER_NAME = "b62de36d";
    public static String PREFIX_FILE_PATH = "/Stream Video";
    public static int RETRY_COUNT = 10;
    public static String AUTH = "client57564";
    public static String PASS = "f3aa809a";
    public static String FOLDER_PATH = Environment.getExternalStorageDirectory().getAbsolutePath() + Constant.PREFIX_FILE_PATH;
    public static int DELAY = 2000;
    public static int DELAY_START_LIVE = 1000;
    public static String URL_WATCH_SERVER_LIVE = Constant.URL_SERVER_LIVESTREAM + SERVER_NAME;

    public static void saveFile(String fileName){
        String root = Environment.getExternalStorageDirectory().getAbsolutePath();
        File myDir = new File(root, Constant.PREFIX_FILE_PATH);
        if (!myDir.exists()) {
            myDir.mkdirs();
        }
        File file = new File(myDir, fileName);
        if (file.exists()) {
            file.delete();
        }
        try {
            file.createNewFile(); // if file already exists will do nothing
            FileOutputStream out = new FileOutputStream(file);
            out.flush();
            out.close();

        } catch (Exception e) {
            e.printStackTrace();
        }

    }
}
