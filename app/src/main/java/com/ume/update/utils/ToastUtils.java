package com.ume.update.utils;

import android.content.Context;
import android.widget.Toast;

public class ToastUtils {

    private static Toast shortToast = null;

    public static void makeShortText(String msg, Context context) {
        if (context == null) {
            return;
        }

        if (shortToast == null) {
            shortToast = Toast.makeText(context, msg, Toast.LENGTH_SHORT);
        } else {
            shortToast.setText(msg);
        }
        shortToast.show();
    }


    private static Toast longToast = null;

    public static void makeLongText(String msg, Context context) {
        if (context == null) {
            return;
        }

        if (longToast == null) {
            longToast = Toast.makeText(context, msg, Toast.LENGTH_LONG);
        } else {
            longToast.setText(msg);
        }
        longToast.show();
    }


    public static void showShort(Context context, String msg) {
        makeShortText(msg, context);
    }

    public static void showShort(Context context, int id) {
        makeShortText(context.getResources().getString(id), context);
    }


}
