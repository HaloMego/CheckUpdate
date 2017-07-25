package com.ume.update.network;

import android.content.Context;
import android.util.Log;

import com.ume.update.AppUpdateService;
import com.ume.update.model.ApkInfo;
import com.ume.update.model.UpdateConstrant;
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

    public static void downloadApk(final Context context, final AppUpdateService service, String downUrl) {

        AppRestClient.newInstance().mAPIService.downLoadFile(downUrl).enqueue(new Callback<ResponseBody>() {

            @Override
            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {

                if (response.isSuccessful()) {
                    MyRunnable runnable = new MyRunnable(context, service, response);
                    ThreadPoolManager.getInstance().executor(runnable);
                } else {
                    if (service != null) {
                        service.onUpdate(UpdateConstrant.FLAG_DOWNLOAD_ERROR, ApkInfo.APK_SAVE_NAME);
                    }
                }

            }

            @Override
            public void onFailure(Call<ResponseBody> call, Throwable t) {
                Log.d(TAG, "onFailure: ", t);
                if (service != null) {
                    service.onUpdate(UpdateConstrant.FLAG_DOWNLOAD_ERROR, ApkInfo.APK_SAVE_NAME);
                }


            }
        });


    }

    private static class MyRunnable implements Runnable {

        private Context mContext;
        private int mPercentage;
        private int mFlag;
        private AppUpdateService mUpdateService;

        private Response<ResponseBody> mResponse;

        public MyRunnable(Context context, AppUpdateService updateService, Response<ResponseBody> response) {
            mContext = context;
            mUpdateService = updateService;
            mResponse = response;
        }

        @Override
        public void run() {

            if (Utils.checkFileExistence(mContext, ApkInfo.APK_SAVE_NAME)) {
                Utils.deleteApkFile(mContext, ApkInfo.APK_SAVE_NAME);
            }
            if (Utils.isEnoughFreeSpace()) {
                try {
                    File futureStudioIconFile = new File(mContext.getExternalFilesDir(ApkInfo.APK_SAVE_DIR), ApkInfo.APK_SAVE_NAME);
                    InputStream inputStream = null;
                    OutputStream outputStream = null;
                    try {
                        byte[] fileReader = new byte[4096];
                        long fileSize = mResponse.body().contentLength();
                        long fileSizeDownloaded = 0;
                        inputStream = mResponse.body().byteStream();
                        outputStream = new FileOutputStream(futureStudioIconFile);
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
                        mFlag = UpdateConstrant.FLAG_DOWNLOAD_SUCCESS;
                        outputStream.flush();
                    } catch (IOException e) {
                        e.printStackTrace();
                        mFlag = UpdateConstrant.FLAG_DOWNLOAD_ERROR;
                    } finally {
                        if (inputStream != null) {
                            inputStream.close();
                        }

                        if (outputStream != null) {
                            outputStream.close();
                        }
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                    mFlag = UpdateConstrant.FLAG_DOWNLOAD_ERROR;
                }
            } else {
                mFlag = UpdateConstrant.FLAG_NO_ENOUGH_SPACE;
            }
            mUpdateService.onUpdate(mFlag, ApkInfo.APK_SAVE_NAME);
            Log.i(TAG, "Download succeeded");
        }
    }
}


