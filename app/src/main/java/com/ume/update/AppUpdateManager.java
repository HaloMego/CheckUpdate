package com.ume.update;

import android.app.Activity;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.IBinder;
import android.text.TextUtils;
import android.widget.Toast;

import com.ume.update.component.AppUpdateService;
import com.ume.update.component.UpdateDialogActivity;
import com.ume.update.listener.IUpdateCheckListener;
import com.ume.update.model.ApkInfo;
import com.ume.update.model.UpdateConstant;
import com.ume.update.network.AppCheckUpdate;
import com.ume.update.utils.LogUtils;
import com.ume.update.utils.Utils;


public class AppUpdateManager implements IUpdateCheckListener {

    private static AppUpdateManager mManager;
    private Context mApplication;
    private ServiceConnection updateServiceConnection = null;
    private AppUpdateService mUpdateService;

    private AppUpdateManager() {
        initServiceConnection();
    }

    public synchronized static AppUpdateManager getManager() {
        if (mManager == null) {
            mManager = new AppUpdateManager();
        }
        return mManager;
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
        AppCheckUpdate.getApkInfoFromApkUre(activity, this);
    }

    public void bindUpdateService(ApkInfo apkInfo) {
        Intent intent = new Intent(mApplication, AppUpdateService.class);
        intent.putExtra(UpdateConstant.KEY_EXTRA_APK_INFO, apkInfo);
        mApplication.bindService(intent, updateServiceConnection, Context.BIND_AUTO_CREATE);
    }

    @Override
    public void onSuccess(ApkInfo apkInfo, Activity activity) {
        String versionName = apkInfo.getVersionName();
        String currentVersionCode = Utils.getAppVersionName(mApplication);
        if (!TextUtils.isEmpty(versionName)) {
            if (versionName.equals(String.valueOf(currentVersionCode))) {
                Toast.makeText(mApplication, "已经是最新版本", Toast.LENGTH_SHORT).show();
            } else {
                UpdateDialogActivity.showDialogActivity(activity, apkInfo);
            }

        }

    }

    @Override
    public void onFailure() {

    }


}
