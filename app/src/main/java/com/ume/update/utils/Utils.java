package com.ume.update.utils;

import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.PackageManager.NameNotFoundException;
import android.net.Uri;
import android.os.Build;
import android.os.Environment;
import android.os.StatFs;
import android.support.annotation.NonNull;
import android.support.v4.content.FileProvider;

import com.ume.update.BuildConfig;
import com.ume.update.model.ApkInfo;

import java.io.File;

public class Utils {

    public static final int FREE_SD_SPACE_NEEDED_TO_CACHE = 50;
    private static final String TAG = "Donald";

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

    public static boolean deleteApk(Context context, String version) {
        File file = new File(context.getExternalFilesDir(ApkInfo.APK_SAVE_DIR), version + ".apk");
        return file.delete();
    }

    public static File getApkPath(Context context, String version) {
        return new File(context.getExternalFilesDir(ApkInfo.APK_SAVE_DIR), version + ".apk");

    }

    public static boolean checkFile(Context context, String version) {
        File file = new File(context.getExternalFilesDir(ApkInfo.APK_SAVE_DIR), version + ".apk");
        return file.exists();
    }

    public static boolean renameFile(Context context, ApkInfo apkInfo, File file) {
        File apkFile = new File(context.getExternalFilesDir(ApkInfo.APK_SAVE_DIR), apkInfo.getVersionName() + ".apk");
        return file.renameTo(apkFile);
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


    public static boolean openDownloadFile(Context context, @NonNull String version) {
        File target = Utils.getApkPath(context, version);
        String mimeType = "application/vnd.android.package-archive";
        Intent intent = new Intent(Intent.ACTION_VIEW);
        Uri uri;
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION | Intent.FLAG_GRANT_WRITE_URI_PERMISSION);
            uri = FileProvider.getUriForFile(context, BuildConfig.APPLICATION_ID + ".fileprovider", target);
            context.grantUriPermission(context.getPackageName(), uri,
                    Intent.FLAG_GRANT_WRITE_URI_PERMISSION);
        } else {
            uri = Uri.fromFile(target);
        }

        intent.setDataAndType(uri, mimeType);
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);

        try {
            context.startActivity(intent);
            return true;
        } catch (Exception ex) {
            ex.printStackTrace();
        }
        return false;
    }

}
