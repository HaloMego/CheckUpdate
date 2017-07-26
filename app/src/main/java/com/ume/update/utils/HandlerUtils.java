package com.ume.update.utils;

import android.os.Handler;
import android.os.Looper;

import java.util.concurrent.FutureTask;

public class HandlerUtils {
    private static final Object sLock = new Object();
    private static final Handler sHandler = new Handler(Looper.getMainLooper());

    public static Handler getMainHandler() {
        synchronized (sLock) {
            return sHandler;
        }
    }

    public static void assertOnUiThread() {
        assert ensureOnMainThread();
    }

    public static boolean ensureOnMainThread() {
        return getMainHandler().getLooper() == Looper.myLooper();
    }

    public static void runOnMainThread(Runnable runnable) {
        if (ensureOnMainThread()) {
            runnable.run();
        } else {
            FutureTask futuretask = new FutureTask(runnable, null);
            getMainHandler().post(futuretask);
            try {
                futuretask.get();
            } catch (Exception e) {
                throw new RuntimeException("Exception occured while waiting for runnable", e);
            }
        }
    }

    public static void postOnMainThread(Runnable runnable) {
        getMainHandler().post(runnable);
    }

    public static void postOnMainThreadDelay(Runnable runnable, long delayMillis) {
        getMainHandler().postDelayed(runnable, delayMillis);
    }

    public static void RunOnMainThread(FutureTask task) {
        if (ensureOnMainThread()) {
            task.run();
        } else {
            postOnMainThread(task);
        }
    }

    public static void postOnMainThread(FutureTask task) {
        getMainHandler().post(task);
    }

    public static void postOnMainThreadDelay(FutureTask task, long delayMillis) {
        getMainHandler().postDelayed(task, delayMillis);
    }
}
