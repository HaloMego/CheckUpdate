package com.ume.update.component;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Binder;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.support.annotation.Nullable;
import android.support.v4.content.FileProvider;
import android.support.v7.app.NotificationCompat;
import android.util.Log;
import android.widget.RemoteViews;

import com.ume.update.AppUpdateManager;
import com.ume.update.BuildConfig;
import com.ume.update.MainActivity;
import com.ume.update.R;
import com.ume.update.listener.IUpdateDownloadListener;
import com.ume.update.model.ApkInfo;
import com.ume.update.model.UpdateConstant;
import com.ume.update.network.UpdateDomestic;
import com.ume.update.utils.Utils;

import java.io.File;
import java.lang.ref.WeakReference;

import static com.ume.update.model.UpdateConstant.KEY_DOWNLOAD_RESULT;
import static com.ume.update.model.UpdateConstant.KEY_FILENAME;
import static com.ume.update.model.UpdateConstant.KEY_PERCENT;
import static com.ume.update.model.UpdateConstant.MSG_DOWNLOAD_PROGRESS;
import static com.ume.update.model.UpdateConstant.MSG_DOWNLOAD_RESULT;
import static com.ume.update.model.UpdateConstant.NOTIFICATION_ID;


public class AppUpdateService extends Service implements IUpdateDownloadListener {
    private static final String TAG = "AppUpdateService";
    private final AppUpdateBinder mBinder = new AppUpdateBinder();

    private Context mContext;

    private NotificationCompat.Builder mBuilder;

    private NotificationManager mNotificationManager = null;

    private Notification mNotification = null;

    private RemoteViews contentView;

    private MyHandler mHandler;
    private ApkInfo mApkInfo;

    public static boolean openDownloadFile(Context context, File target) {
        target = new File(context.getExternalFilesDir(ApkInfo.APK_SAVE_DIR), ApkInfo.APK_SAVE_NAME);
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

    @Override
    public void onCreate() {
        super.onCreate();
        mContext = getApplicationContext();
        mHandler = new MyHandler(this);
        initNotification();
    }

    private void updateProgress(Message msg) {
        int percentage = msg.getData().getInt(UpdateConstant.KEY_PERCENT);
        if (UpdateConstant.isNotificationCanceled) {
            if (percentage == 100) {
                openDownloadFile(mContext, null);
            }
            return;
        }

        if (contentView != null && !UpdateConstant.isNotificationCanceled) {
            contentView.setTextViewText(R.id.notification_update_progress_text, percentage + "%");
            contentView.setProgressBar(R.id.notification_update_progress_bar, 100, percentage, false);
            mNotificationManager.notify(UpdateConstant.NOTIFICATION_ID, mNotification);
        }


    }

    private void handleDownloadResult(Message msg) {
        String version = msg.getData().getString(UpdateConstant.KEY_FILENAME);
        int downloadResult = msg.getData().getInt(UpdateConstant.KEY_DOWNLOAD_RESULT);
        switch (downloadResult) {
            case UpdateConstant.FLAG_CANCEL_UPDATE:
                showNotification("取消下载", "已取消下载", new Intent(mContext, MainActivity.class));

                break;
            case UpdateConstant.FLAG_DOWNLOAD_ERROR:
                showNotification("更新出错", "更新出错，请稍后再试", new Intent(mContext, MainActivity.class));

                break;
            case UpdateConstant.FLAG_NO_ENOUGH_SPACE:
                showNotification("下载出错，", "存储空间不足", new Intent(mContext, MainActivity.class));

                break;
            case UpdateConstant.FLAG_DOWNLOAD_SUCCESS:
                Utils.openDownloadFile(mContext, version);
                break;
        }


    }

    private void setNotification(String ticker, String title,
                                 String text, PendingIntent intent) {

        if (mNotification != null) {
            if (mBuilder != null) {
                mBuilder.setTicker(ticker);
                mBuilder.setContentTitle(title);
                mBuilder.setContentText(text);
                mBuilder.setAutoCancel(true);
                mBuilder.setContent(null);
                mBuilder.setContentIntent(intent);
                mNotification = mBuilder.build();
            }

        }
    }

    private void showNotification(String ticker, String message, Intent intent) {
        PendingIntent contentIntent = PendingIntent.getActivity(mContext, NOTIFICATION_ID, intent,
                PendingIntent.FLAG_UPDATE_CURRENT);
        setNotification(ticker, ticker, message, contentIntent);
        mNotificationManager.notify(NOTIFICATION_ID, mNotification);
    }

    private void initNotification() {
        mBuilder = new NotificationCompat.Builder(this);
        mBuilder.setContentTitle("提示");
        mBuilder.setContentText("正在下载APk");
        mBuilder.setSmallIcon(R.mipmap.ic_launcher_round);

        mNotificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
        contentView = new RemoteViews(getPackageName(), R.layout.notification_update);
        contentView.setTextViewText(R.id.notification_update_progress_text, "0%");
        contentView.setImageViewResource(R.id.notification_update_image, R.mipmap.ic_launcher);
        mBuilder.setCustomContentView(contentView);

        Intent closeIntent = new Intent(UpdateConstant.ACTION_UPDATE_CANCEL);
        PendingIntent closePendingIntent = PendingIntent.getService(this, 0, closeIntent, PendingIntent.FLAG_UPDATE_CURRENT);
        contentView.setOnClickPendingIntent(R.id.ib_update_close, closePendingIntent);

        Intent intent = new Intent(UpdateConstant.ACTION_UPDATE_DEFAULT);
        PendingIntent contentIntent = PendingIntent.getService(this, 0, intent, PendingIntent.FLAG_UPDATE_CURRENT);
        mBuilder.setContentIntent(contentIntent);
        mNotification = mBuilder.build();


    }

    public void notifyMessage() {
        if (mNotification != null && mNotificationManager != null) {
            mNotificationManager.notify(NOTIFICATION_ID, mNotification);
        }
    }

    public void cancelNotification() {
        mNotificationManager.cancel(NOTIFICATION_ID);
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        getData(intent);
        return mBinder;
    }

    private void getData(Intent intent) {
        mApkInfo = intent.getParcelableExtra(UpdateConstant.KEY_EXTRA_APK_INFO);
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        String action = intent.getAction();
        if (action.equals(UpdateConstant.ACTION_UPDATE_DEFAULT)) {
            Log.i(TAG, "onStartCommand: " + "ACTION_UPDATE_DEFAULT");
        }
        if (action.equals(UpdateConstant.ACTION_UPDATE_CANCEL)) {
            Log.i(TAG, "onStartCommand: " + "ACTION_UPDATE_CANCEL");
            UpdateConstant.isNotificationCanceled = true;
            mNotificationManager.cancel(UpdateConstant.NOTIFICATION_ID);
        }
        return START_STICKY;
    }

    public void downloadStart() {
        UpdateDomestic.downloadApk(mContext, AppUpdateService.this, mApkInfo);
    }

    private void sendDownloadResult(int flag, String version) {
        Message msg = Message.obtain();
        msg.what = UpdateConstant.MSG_DOWNLOAD_RESULT;
        Bundle bundle = new Bundle();
        bundle.putInt(KEY_DOWNLOAD_RESULT, flag);
        bundle.putString(KEY_FILENAME, version);
        msg.setData(bundle);
        mHandler.sendMessage(msg);
    }

    @Override
    public void onUpdate(int flag, String version) {
        sendDownloadResult(flag, version);
    }

    @Override
    public void onProgress(int downloadPercentage) {
        Message msg = new Message();
        msg.what = UpdateConstant.MSG_DOWNLOAD_PROGRESS;
        Bundle bundle = new Bundle();
        bundle.putInt(KEY_PERCENT, downloadPercentage);
        msg.setData(bundle);
        mHandler.sendMessage(msg);
    }

    private static class MyHandler extends Handler {

        private WeakReference<AppUpdateService> mReference;

        public MyHandler(AppUpdateService mService) {
            this.mReference = new WeakReference<>(mService);
        }

        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            if (mReference.get() != null) {
                switch (msg.what) {
                    case MSG_DOWNLOAD_PROGRESS:
                        mReference.get().updateProgress(msg);
                        break;
                    case MSG_DOWNLOAD_RESULT:
                        AppUpdateService updateService = mReference.get();
                        updateService.handleDownloadResult(msg);
                        updateService.cancelNotification();
                        AppUpdateManager.getManager().unBindService();
                        break;
                }
            }
        }
    }

    public class AppUpdateBinder extends Binder {
        public AppUpdateService getService() {
            return AppUpdateService.this;
        }
    }

}
