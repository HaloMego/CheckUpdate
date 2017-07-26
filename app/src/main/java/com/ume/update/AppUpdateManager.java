package com.ume.update;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.ComponentName;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.IBinder;
import android.text.TextUtils;

import com.ume.update.model.ApkInfo;
import com.ume.update.model.UpdateConstrant;
import com.ume.update.network.AppCheckUpdate;
import com.ume.update.utils.LogUtils;
import com.ume.update.utils.ToastUtils;
import com.ume.update.utils.Utils;


public class AppUpdateManager implements IUpdateCheckListener {

    private static AppUpdateManager instance;
    private Context mApplication;
    private ServiceConnection updateServiceConnection = null;

    private ProgressDialog progressDialog = null;

    private AppUpdateService mUpdateService;


    private AppUpdateManager() {

        initServiceConnection();

    }

    public synchronized static AppUpdateManager getInstance() {
        if (instance == null) {
            instance = new AppUpdateManager();
        }
        return instance;
    }


    private void initProgressDialog(Activity activity) {
        if (progressDialog == null) {
            progressDialog = new ProgressDialog(activity);
            progressDialog.setProgressStyle(ProgressDialog.STYLE_HORIZONTAL);
            progressDialog.setTitle(activity.getString(R.string.update_app));
            progressDialog.setMax(100);
            progressDialog.setCancelable(true);
            progressDialog.setCanceledOnTouchOutside(false);
            progressDialog.setOnCancelListener(new DialogInterface.OnCancelListener() {
                @Override
                public void onCancel(DialogInterface dialog) {
                    ToastUtils.showShort(mApplication, R.string.tip_cancelupdate);
                    unBindService();
                }
            });
        }
    }


    public void unBindService() {
        LogUtils.i("service", "unbind service");
        Intent intent = new Intent(mApplication, AppUpdateService.class);
        mApplication.unbindService(updateServiceConnection);
        mApplication.stopService(intent);
        mUpdateService = null;
        mApplication = null;
    }


    private void initServiceConnection() {
        updateServiceConnection = new ServiceConnection() {

            @Override
            public void onServiceConnected(ComponentName name, IBinder service) {
                AppUpdateService.AppUpdateBinder service1 = (AppUpdateService.AppUpdateBinder) service;
                mUpdateService = service1.getService();
                mUpdateService.downloadStart();

            }

            @Override
            public void onServiceDisconnected(ComponentName name) {
                mUpdateService = null;

            }
        };
    }

    public void checkUpdate(Activity activity) {
        mApplication = activity.getApplicationContext();
        AppCheckUpdate.getApkInfoFromApkure(activity, this);
    }

    private void bindUpdateService(String url) {
        Intent intent = new Intent(mApplication, AppUpdateService.class);
        intent.putExtra(UpdateConstrant.DOWNLOAD_URL, url);
        mApplication.bindService(intent, updateServiceConnection, Context.BIND_AUTO_CREATE);
    }

    @Override
    public void onSuccess(ApkInfo apkInfo, Activity activity) {
        String versionName = apkInfo.getVersionName();
        String downUrl = apkInfo.getDownUrl();
        int currentVersionCode = Utils.getAppVersionCode(mApplication);

        if (!TextUtils.isEmpty(versionName)) {
            if (versionName.equals(String.valueOf(currentVersionCode))) {
                ToastUtils.showShort(mApplication, "版本一样");
            } else {
                UpdateDialogActiviity.showDialogActivity(activity,apkInfo);
            }

        }

    }

    @Override
    public void onFailure() {

    }


}
