package com.ume.update.listener;

/**
 * Created by AlphaGo on 2017/7/13.
 */

public interface IUpdateDownloadListener {
    void onUpdate(int flag, String fileName);

    void onProgress(int downloadPercentage);
}
