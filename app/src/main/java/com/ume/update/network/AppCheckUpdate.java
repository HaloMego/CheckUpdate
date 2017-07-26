package com.ume.update.network;

import android.app.Activity;
import android.util.Log;

import com.google.gson.Gson;
import com.ume.update.AppUpdateManager;
import com.ume.update.model.ApkInfo;
import com.ume.update.model.UpdateConstant;
import com.ume.update.utils.LogUtils;

import java.util.Locale;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

/**
 * Created by AlphaGo on 2017/7/13.
 */

public class AppCheckUpdate {
    private static final String TAG = "Donald";
    private static String info;

    public static void getApkInfoFromTencent(final Activity activity, final AppUpdateManager manager) {
        String url = String.format(Locale.CHINA, UpdateConstant.SERVER_DOWNLOAD_TENCENT, activity.getPackageName());
        LogUtils.i("Donald", "Url is ...." + url);
        AppRestClient.newInstance().mAPIService.getAPKInfoFromTencent(url).enqueue(new Callback<String>() {
            @Override
            public void onResponse(Call<String> call, final Response<String> response) {
                ThreadPoolManager.getInstance().executor(new Runnable() {
                    @Override
                    public void run() {
                        boolean isSuccessful = false;
                        ApkInfo apkInfo = null;
                        if (response.isSuccessful()) {
                            try {
                                final String body = response.body();
                                assert body != null;
                                if(!body.contains("det-size")){
                                    throw new IllegalArgumentException();
                                }
                                int beginIndex = body.lastIndexOf("{");
                                int endIndex = body.lastIndexOf("}");
                                info = body.substring(beginIndex, endIndex + 1);
                                Gson gson = new Gson();
                                apkInfo = gson.fromJson(info, ApkInfo.class);
                                String downUrl = apkInfo.getDownUrl();

                                int begin = downUrl.indexOf("_", 1);
                                int end = downUrl.lastIndexOf(".apk");
                                String versionName = downUrl.substring(begin + 1, end - 3);
                                apkInfo.setVersionName(versionName);

                                int i = body.indexOf("det-size");
                                int j = body.indexOf("div", i);
                                int beginI = i + 10;
                                int endJ = j - 3;
                                String size = body.substring(beginI, endJ);
                                apkInfo.setSize(size);

                                int m = body.lastIndexOf("det-app-data-info");
                                int n = body.indexOf("div", m);
                                int beginM = m + 19;
                                int beginN = n - 2;
                                String content = body.substring(beginM, beginN);
                                apkInfo.setFeature(content);
                                Log.i(TAG, "onResponse: " + apkInfo);
                                isSuccessful = true;
                            } catch (Exception e) {
                                isSuccessful = false;
                            } finally {
                                if (isSuccessful) {
                                    manager.onSuccess(apkInfo, activity);
                                } else {
                                    manager.onFailure();
                                }
                            }
                        }
                    }
                });
            }

            @Override
            public void onFailure(Call<String> call, Throwable t) {
                manager.onFailure();

            }
        });
    }

    public static void getApkInfoFromApkUre(final Activity activity, final AppUpdateManager manager) {

        String url = String.format(Locale.CHINA, UpdateConstant.SERVER_DOWNLOAD_PURE, activity.getPackageName());
        LogUtils.i("Donald", "Url is ...." + url);
        AppRestClient.newInstance().mAPIService.getAPKInfoFromTencent(url).enqueue(new Callback<String>() {
            @Override
            public void onResponse(Call<String> call, final Response<String> response) {

                ThreadPoolManager.getInstance().executor(new Runnable() {
                    @Override
                    public void run() {
                        ApkInfo apkInfo = new ApkInfo();
                        boolean isSuccessful = false;
                        if (response.isSuccessful()) {
                            try {
                                String body = response.body();
                                assert body != null;
                                int i = body.indexOf("fileSize");
                                if (i == -1) {
                                    throw new IllegalArgumentException();
                                }
                                int j = body.indexOf("content", i);
                                int beginI = j + 9;
                                int k = body.indexOf(">", j);
                                int endK = k - 3;
                                String size = body.substring(beginI, endK);

                                apkInfo.setSize(size);
                                int l = body.lastIndexOf("版本");
                                int m = body.indexOf("nbsp", l);
                                int beginM = m + 5;

                                int n = body.indexOf("dd", m);
                                int endN = n - 2;
                                String version = body.substring(beginM, endN);
                                apkInfo.setVersionName(version);
                                int o = body.indexOf("change-info");
                                int p = body.indexOf("con", o);
                                int beginO = p + 5;
                                int q = body.indexOf("div", p);
                                int endQ = q - 2;
                                String feature = body.substring(beginO, endQ);
                                apkInfo.setFeature(feature);
                                int r = body.indexOf("qr-info");
                                int s = body.indexOf("href", r);
                                int beginR = s + 6;
                                int t = body.indexOf("rel", r);
                                int endT = t - 2;
                                String downUrl = body.substring(beginR, endT);
                                apkInfo.setDownUrl(downUrl);
                                Log.i(TAG, "onResponse: " + downUrl);
                                isSuccessful = true;
                            } catch (Exception ignored) {
                                isSuccessful = false;
                            } finally {
                                if (isSuccessful) {
                                    manager.onSuccess(apkInfo, activity);
                                } else {
                                    manager.onFailure();
                                }
                            }

                        }
                    }
                });
            }

            @Override
            public void onFailure(Call<String> call, Throwable t) {
                manager.onFailure();
                LogUtils.d(TAG, "onFailure: " + t.getMessage());

            }
        });
    }
}
