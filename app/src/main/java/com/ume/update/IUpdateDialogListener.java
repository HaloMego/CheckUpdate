package com.ume.update;

/**
 * Created by WannaDie on 2017/7/14.
 */

public interface IUpdateDialogListener {
    void onShow();
    void onProgress(int percentage);
    void onDismiss();
}
