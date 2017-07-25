package com.ume.update;

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
import android.widget.RemoteViews;

import com.ume.update.model.ApkInfo;
import com.ume.update.model.UpdateConstrant;
import com.ume.update.network.UpdateDomestic;
import com.ume.update.utils.ToastUtils;

import java.io.File;
import java.lang.ref.WeakReference;

import static com.ume.update.model.UpdateConstrant.DOWNLOAD_URL;
import static com.ume.update.model.UpdateConstrant.KEY_DOWNLOAD_RESULT;
import static com.ume.update.model.UpdateConstrant.KEY_FILENAME;
import static com.ume.update.model.UpdateConstrant.KEY_PERCENT;
import static com.ume.update.model.UpdateConstrant.MSG_DOWNLOAD_PROGRESS;
import static com.ume.update.model.UpdateConstrant.MSG_DOWNLOAD_RESULT;
import static com.ume.update.model.UpdateConstrant.NOTIFICATION_ID;


public class AppUpdateService extends Service implements IUpdateDownloadListener {

    private final AppUpdateBinder mBinder = new AppUpdateBinder();

    private String mDownloadUrl;

    private IUpdateDialogListener mUpdateDialogListener;

    private Context mContext;

    private NotificationCompat.Builder mBuilder;

    private NotificationManager mNotificationManager = null;

    private Notification mNotification = null;

    private RemoteViews contentView;

    private MyHandler mHandler;

    @Override
    public void onCreate() {
        super.onCreate();
        this.mContext = getApplicationContext();
        initNotification();
        mHandler = new MyHandler(this);
    }


    private void updateProgress(Message msg) {

        int percentage = msg.getData().getInt(UpdateConstrant.KEY_PERCENT);

        // TODO: 2017/7/14 应用处于前台，不显示通知

//        if (contentView != null && Utils.isAppOnForeground(mContext)) {

        if (contentView != null) {
            contentView.setTextViewText(R.id.notification_update_progress_text, percentage + "%");
            contentView.setProgressBar(R.id.notification_update_progress_bar, 100, percentage, false);
            mNotificationManager.notify(UpdateConstrant.NOTIFICATION_ID, mNotification);
        }


    }

    private void handleDownloadResult(Message msg) {
        String fileName = msg.getData().getString(UpdateConstrant.KEY_FILENAME);
        int downloadResult = msg.getData().getInt(UpdateConstrant.KEY_DOWNLOAD_RESULT);
        switch (downloadResult) {
            case UpdateConstrant.FLAG_CANCEL_UPDATE:
                ToastUtils.showShort(mContext, R.string.tip_cancelupdate);

                showNotification("取消下载", "已取消下载", new Intent(mContext, MainActivity.class));
                break;
            case UpdateConstrant.FLAG_DOWNLOAD_ERROR:
                ToastUtils.showShort(mContext, R.string.tip_update_error);
                showNotification("更新出错", "更新出错，请稍后再试", new Intent(mContext, MainActivity.class));

                break;
            case UpdateConstrant.FLAG_NO_ENOUGH_SPACE:
                ToastUtils.showShort(mContext, R.string.tip_no_enough_space);
                showNotification("下载出错，", "存储空间不足", new Intent(mContext, MainActivity.class));

                break;
            case UpdateConstrant.FLAG_DOWNLOAD_SUCCESS:
                openDownloadFile(mContext, null);
                break;
        }

//        mUpdateDialogListener.onDismiss();

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
        PendingIntent contentIntent = PendingIntent.getActivity(
                mContext, NOTIFICATION_ID, intent,
                PendingIntent.FLAG_UPDATE_CURRENT);
        setNotification(ticker, ticker, message, contentIntent);
        mNotificationManager.notify(
                NOTIFICATION_ID, mNotification);
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

        Intent intent = new Intent(this, MainActivity.class);
        PendingIntent contentIntent = PendingIntent.getActivity(this, 0, intent, PendingIntent.FLAG_UPDATE_CURRENT);
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
        mDownloadUrl = intent.getStringExtra(DOWNLOAD_URL);
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        return START_STICKY;
    }


    public void downloadStart() {
        UpdateDomestic.downloadApk(mContext, this, mDownloadUrl);
    }

    private void sendDownloadResult(int flag, String fileName) {
        Message msg = Message.obtain();
        msg.what = UpdateConstrant.MSG_DOWNLOAD_RESULT;
        Bundle bundle = new Bundle();
        bundle.putInt(KEY_DOWNLOAD_RESULT, flag);
        bundle.putString(KEY_FILENAME, fileName);
        msg.setData(bundle);
        mHandler.sendMessage(msg);
    }

    @Override
    public void onUpdate(int flag, String fileName) {
        sendDownloadResult(flag, fileName);
    }

    @Override
    public void onProgress(int downloadPercentage) {
        Message msg = new Message();
        msg.what = UpdateConstrant.MSG_DOWNLOAD_PROGRESS;
        Bundle bundle = new Bundle();
        bundle.putInt(KEY_PERCENT, downloadPercentage);
        msg.setData(bundle);
        mHandler.sendMessage(msg);
    }

    public boolean openDownloadFile(Context context, File target) {
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
                        mReference.get().handleDownloadResult(msg);
                        break;
                }
            }
        }
    }

    class AppUpdateBinder extends Binder {
        AppUpdateService getService() {
            return AppUpdateService.this;
        }
    }

}
