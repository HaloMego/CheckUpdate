package com.ume.update.utils;

import android.content.Context;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.PackageManager.NameNotFoundException;
import android.os.Environment;
import android.os.StatFs;

import com.ume.update.model.ApkInfo;

import java.io.File;

public class Utils {

    private static final String TAG = "Donald";


    public static final int FREE_SD_SPACE_NEEDED_TO_CACHE = 50;


    public static boolean checkFileExistence(Context context, String fileName) {
        File file = new File(context.getExternalFilesDir(ApkInfo.APK_SAVE_DIR), fileName);
        return file.exists();
    }


    public static boolean isEnoughFreeSpace() {

        boolean isEnough = false;

        StatFs stat = new StatFs(Environment.getExternalStorageDirectory()
                .getPath());

        double sdFreeMB = ((double) stat.getAvailableBlocks() * (double) stat
                .getBlockSize()) / 1024 / 1024;

        if (sdFreeMB > FREE_SD_SPACE_NEEDED_TO_CACHE) {
            isEnough = true;
        }

        return isEnough;

    }


    public static void deleteApkFile(Context context, String name) {
        File file = new File(context.getExternalFilesDir(ApkInfo.APK_SAVE_DIR), name);
        if (file.exists()) {
            file.delete();
        }
    }


    public static String getAppVersionName(Context context) {
        try {
            return context.getPackageManager().getPackageInfo(context.getPackageName(), 0).versionName;
        } catch (NameNotFoundException e) {
            e.printStackTrace();
        }
        return "";
    }

    public static int getAppVersionCode(Context context) {
        try {
            return context.getPackageManager().getPackageInfo(context.getPackageName(), 0).versionCode;
        } catch (NameNotFoundException e) {
            e.printStackTrace();
        }
        return 0;
    }

    public static int getApkVersionCode(Context context, String filePath) {
        PackageManager manager = context.getPackageManager();
        PackageInfo packageInfo = manager.getPackageArchiveInfo(filePath, PackageManager.GET_ACTIVITIES);
        if (packageInfo != null) {
            return packageInfo.versionCode;
        }
        return 0;
    }

    public static String getApkVersionName(Context context, String filePath) {
        PackageManager manager = context.getPackageManager();
        PackageInfo packageInfo = manager.getPackageArchiveInfo(filePath, PackageManager.GET_ACTIVITIES);
        if (packageInfo != null) {
            return packageInfo.versionName;
        }
        return "";
    }

    public static String getApkPackageName(Context context, String filePath) {
        PackageManager manager = context.getPackageManager();
        PackageInfo packageInfo = manager.getPackageArchiveInfo(filePath, PackageManager.GET_ACTIVITIES);
        if (packageInfo != null) {
            return packageInfo.packageName;
        }
        return "";

    }


    public static String getApkShareUserId(Context context, String filePath) {
        PackageManager manager = context.getPackageManager();
        PackageInfo packageInfo = manager.getPackageArchiveInfo(filePath, PackageManager.GET_ACTIVITIES);
        if (packageInfo != null) {
            return packageInfo.sharedUserId;
        }
        return "";

    }

}
