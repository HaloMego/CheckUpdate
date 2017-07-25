package com.ume.update.network;

import java.util.concurrent.Executors;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;


public class ThreadPoolManager {

    private volatile static ThreadPool mThreadPool;

    public static ThreadPool getInstance() {
        if (mThreadPool == null) {
            synchronized (ThreadPoolManager.class) {
                if (mThreadPool == null) {
                    int CPU_COUNT = Runtime.getRuntime().availableProcessors();
                    int corePoolSize = CPU_COUNT + 1;
                    int maximumPoolSize = CPU_COUNT * 2 + 1;
                    long keepAliveTime = 1;
                    mThreadPool = new ThreadPool(corePoolSize, maximumPoolSize, keepAliveTime);
                }

            }
        }
        return mThreadPool;
    }

    public static class ThreadPool {
        private static ThreadPoolExecutor executor = null;

        private int corePoolSize;
        private int maximumPoolSize;
        private long keepAliveTime = 0;

        public ThreadPool(int corePoolSize, int maximumPoolSize, long keepAliveTime) {
            super();
            this.corePoolSize = corePoolSize;
            this.maximumPoolSize = maximumPoolSize;
            this.keepAliveTime = keepAliveTime;
        }

        public void executor(Runnable runnable) {
            if (runnable == null) {
                return;
            }

            if (executor == null || executor.isShutdown()) {
                executor = new ThreadPoolExecutor(
                        corePoolSize,
                        maximumPoolSize,
                        keepAliveTime,
                        TimeUnit.MILLISECONDS,
                        new LinkedBlockingQueue<Runnable>(),
                        Executors.defaultThreadFactory(),
                        new ThreadPoolExecutor.AbortPolicy());
            }
            executor.execute(runnable);

        }

        public void cancel(Runnable runnable) {
            if (runnable != null && !executor.isShutdown()) {
                executor.getQueue().remove(runnable);
            }
        }

    }

}
