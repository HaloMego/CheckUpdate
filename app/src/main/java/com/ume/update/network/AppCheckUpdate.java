package com.ume.update.network;

import android.app.Activity;
import android.util.Log;

import com.google.gson.Gson;
import com.ume.update.AppUpdateManager;
import com.ume.update.model.ApkInfo;

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
        AppRestClient.newInstance().mAPIService.getAPKInfoFromTencent().enqueue(new Callback<String>() {
            @Override
            public void onResponse(Call<String> call, Response<String> response) {
                if (response.isSuccessful()) {
                    final String body = response.body();
                    assert body != null;
                    int beginIndex = body.lastIndexOf("{");
                    int endIndex = body.lastIndexOf("}");
                    info = body.substring(beginIndex, endIndex + 1);
                    Gson gson = new Gson();
                    ApkInfo apkInfo = gson.fromJson(info, ApkInfo.class);
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


                    manager.onSuccess(apkInfo, activity);
                }
            }

            @Override
            public void onFailure(Call<String> call, Throwable t) {
                manager.onFailure();

            }
        });
    }

    public static void getApkInfoFromApkure(final Activity activity, final AppUpdateManager manager) {
        AppRestClient.newInstance().mAPIService.getAPKInfoFromPure().enqueue(new Callback<String>() {
            @Override
            public void onResponse(Call<String> call, Response<String> response) {
                if (response.isSuccessful()) {
                    String body = response.body();
                    assert body != null;
                    int i = body.indexOf("fileSize");
                    int j = body.indexOf("content", i);
                    int beginI = j + 9;
                    int k = body.indexOf(">", j);
                    int engk = k - 3;
                    String size = body.substring(beginI, engk);
                    ApkInfo apkInfo = new ApkInfo();
                    apkInfo.setSize(size);
                    int l = body.lastIndexOf("版本");
                    int m = body.indexOf("nbsp", l);
                    int beiginM = m + 5;

                    int n = body.indexOf("dd", m);
                    int endN = n - 2;
                    String version = body.substring(beiginM, endN);
                    apkInfo.setSize(version);
                    int o = body.indexOf("change-info");
                    int p = body.indexOf("con", o);
                    int beginO = p + 5;
                    int q = body.indexOf("div", p);
                    int endQ = q - 2;
                    String feature = body.substring(beginO, endQ);
                    apkInfo.setFeature(feature);
                    int r = body.indexOf("qr-info");
                    int s = body.indexOf("href", r);
                    int beiginR = s + 6;
                    int t = body.indexOf("rel", r);
                    int endT = t - 1;
                    String downUrl = body.substring(beiginR, endT);
                    apkInfo.setDownUrl(downUrl);
                    Log.i(TAG, "onResponse: " + downUrl);


                    manager.onSuccess(apkInfo, activity);


                }
            }

            @Override
            public void onFailure(Call<String> call, Throwable t) {
                manager.onFailure();
                Log.d(TAG, "onFailure: ", t);

            }
        });
    }
}
