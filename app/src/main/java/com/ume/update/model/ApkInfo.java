package com.ume.update.model;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * Created by WannaDie on 2017/7/12.
 */

public class ApkInfo implements Parcelable {
    public static final String APK_SAVE_NAME="APP.apk";
    public static final String APK_SAVE_DIR="app";
    private String versionName;
    private String downUrl;
    private String apkName;
    private String feature;
    private String size;

    public String getVersionName() {
        return versionName;
    }

    public void setVersionName(String versionName) {
        this.versionName = versionName;
    }

    public String getDownUrl() {
        return downUrl;
    }

    public void setDownUrl(String downUrl) {
        this.downUrl = downUrl;
    }

    public String getApkName() {
        return apkName;
    }

    public void setApkName(String apkName) {
        this.apkName = apkName;
    }

    public String getFeature() {
        return feature;
    }

    public void setFeature(String feature) {
        this.feature = feature;
    }

    public String getSize() {
        return size;
    }

    public void setSize(String size) {
        this.size = size;
    }

    @Override
    public String toString() {
        return "ApkInfo{" +
                "versionName='" + versionName + '\'' +
                ", downUrl='" + downUrl + '\'' +
                ", apkName='" + apkName + '\'' +
                ", feature='" + feature + '\'' +
                ", size='" + size + '\'' +
                '}';
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {

    }
}
