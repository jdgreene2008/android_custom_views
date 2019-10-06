package com.jarvis.dragdropresearch.utils;

import android.os.Handler;
import android.os.Looper;

import androidx.annotation.NonNull;

import java.util.concurrent.Executor;
import java.util.concurrent.Executors;

class ImageLoadingExecutors {

    private static ImageLoadingExecutors sInstance = new ImageLoadingExecutors();

    private final Executor mWorkerThread;
    private final Executor mMainThread;

    public static ImageLoadingExecutors getInstance() {
        return sInstance;
    }

    private ImageLoadingExecutors() {
        mWorkerThread = Executors.newSingleThreadExecutor();
        mMainThread = new MainThreadExecutor();
    }

    Executor getMainThread() {
        return mMainThread;
    }

    Executor getWorkerThread() {
        return mWorkerThread;
    }

    private static class MainThreadExecutor implements Executor {

        private final Handler mHandler;

        MainThreadExecutor() {
            mHandler = new Handler(Looper.getMainLooper());
        }

        @Override
        public void execute(@NonNull Runnable command) {
            mHandler.post(command);
        }
    }
}
