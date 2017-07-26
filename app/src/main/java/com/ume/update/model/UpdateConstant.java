package com.ume.update.model;

/**
 * Created by AlphaGo on 2017/7/25.
 */

public class UpdateConstant {
    public static final int FLAG_DOWNLOAD_ERROR = 0x01;

    public static final int FLAG_NO_ENOUGH_SPACE = 0x03;

    public static final String DOWNLOAD_URL = "DOWNLOAD_URL";

    public static final String KEY_PERCENT = "KEY_PERCENT";

    public static final int NOTIFICATION_ID = 0x100;

    public static final int FLAG_DOWNLOAD_SUCCESS = 0x02;

    public static final int FLAG_CANCEL_UPDATE = 0x04;

    public static final String KEY_FILENAME = "KEY_FILENAME";

    public static final String KEY_DOWNLOAD_RESULT = "KEY_DOWNLOAD_RESULT";

    public static final int MSG_DOWNLOAD_RESULT = 0x110;

    public static final int MSG_DOWNLOAD_PROGRESS = 0x111;

    public static final String ACTION_UPDATE_DEFAULT = "update.intent.action.DEFAULT";
    public static final String ACTION_UPDATE_CANCEL = "update.intent.action.CANCEL";
    public static  boolean  isNotificationCanceled;
    public static final String KEY_EXTRA_APK_INFO = "KEY_EXTRA_APK_INFO";
}
