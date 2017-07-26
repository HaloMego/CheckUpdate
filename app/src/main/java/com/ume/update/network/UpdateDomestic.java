package com.ume.update.network;

import android.content.Context;
import android.util.Log;

import com.ume.update.component.AppUpdateService;
import com.ume.update.model.ApkInfo;
import com.ume.update.model.UpdateConstant;
import com.ume.update.utils.LogUtils;
import com.ume.update.utils.Utils;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

/**
 * Created by WannaDie on 2017/7/12.
 */

public class UpdateDomestic {
    private static final String TAG = "Donald";
    private static File tempFile;

    public static void downloadApk(final Context context, final AppUpdateService service, final ApkInfo apkInfo) {

        AppRestClient.newInstance().mAPIService.downLoadFile(apkInfo.getDownUrl()).enqueue(new Callback<ResponseBody>() {

            @Override
            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {

                if (response.isSuccessful()) {
                    MyRunnable runnable = new MyRunnable(context, service, response, apkInfo);
                    ThreadPoolManager.getInstance().executor(runnable);
                } else {
                    if (service != null) {
                        service.onUpdate(UpdateConstant.FLAG_DOWNLOAD_ERROR, ApkInfo.APK_SAVE_NAME);
                    }
                }
            }

            @Override
            public void onFailure(Call<ResponseBody> call, Throwable t) {
                Log.d(TAG, "onFailure: ", t);
                if (service != null) {
                    service.onUpdate(UpdateConstant.FLAG_DOWNLOAD_ERROR, ApkInfo.APK_SAVE_NAME);
                }
            }
        });


    }

    private static class MyRunnable implements Runnable {

        private Context mContext;
        private int mPercentage;
        private int mFlag;
        private AppUpdateService mUpdateService;
        private ApkInfo mApkInfo;
        private Response<ResponseBody> mResponse;

        public MyRunnable(Context context, AppUpdateService updateService, Response<ResponseBody> response, ApkInfo apkInfo) {
            mApkInfo = apkInfo;
            mContext = context;
            mUpdateService = updateService;
            mResponse = response;
        }

        @Override
        public void run() {
            if (Utils.isEnoughFreeSpace()) {
                InputStream inputStream = null;
                OutputStream outputStream = null;
                try {
                    mUpdateService.onProgress(0);
                    tempFile = new File(mContext.getExternalFilesDir(ApkInfo.APK_SAVE_DIR), mApkInfo.getVersionName() + ".temp");
                    byte[] fileReader = new byte[1024 * 500];
                    long fileSize = mResponse.body().contentLength();
                    long fileSizeDownloaded = 0;
                    inputStream = mResponse.body().byteStream();
                    outputStream = new FileOutputStream(tempFile);
                    while (true) {
                        int read = inputStream.read(fileReader);
                        if (read == -1) {
                            break;
                        }
                        outputStream.write(fileReader, 0, read);
                        fileSizeDownloaded += read;
                        float tempPercent = mPercentage;
                        mPercentage = (int) ((float) fileSizeDownloaded / fileSize * 100);
                        if (mPercentage != tempPercent && mUpdateService != null) {
                            mUpdateService.onProgress(mPercentage);
                            LogUtils.d(TAG, "onResponse: onProgress " + mPercentage);
                        }
                    }
                    outputStream.flush();
                    mFlag = UpdateConstant.FLAG_DOWNLOAD_SUCCESS;
                    Utils.renameFile(mContext, mApkInfo, tempFile);
                } catch (IOException e) {
                    e.printStackTrace();
                    mFlag = UpdateConstant.FLAG_DOWNLOAD_ERROR;
                } finally {
                    Utils.deleteApkFile(mContext, mApkInfo.getVersionName() + ".temp");
                    if (inputStream != null) {
                        try {
                            inputStream.close();
                        } catch (IOException ignored) {
                        }
                    }
                    if (outputStream != null) {
                        try {
                            outputStream.close();
                        } catch (IOException ignored) {
                        }
                    }
                }
            } else {
                mFlag = UpdateConstant.FLAG_NO_ENOUGH_SPACE;
            }
            mUpdateService.onUpdate(mFlag, mApkInfo.getVersionName());
        }


    }
}


