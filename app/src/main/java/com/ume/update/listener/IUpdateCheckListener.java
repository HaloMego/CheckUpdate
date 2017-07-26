package com.ume.update.listener;


import android.app.Activity;

import com.ume.update.model.ApkInfo;

/**
 * Created by AlphaGo on 2017/7/13.
 */

public interface IUpdateCheckListener {

    void onSuccess(ApkInfo apkInfo, Activity activity);

    void onFailure();
}
