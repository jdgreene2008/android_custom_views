package com.jarvis.dragdropresearch.utils;

import android.graphics.Bitmap;

import java.util.List;
import java.util.concurrent.Executor;

class ImageLoadingCallbackMarshaller {

    private Executor mMainThreadExecutor;
    private ImageLoader.ImageLoadingCallbacks mImageLoadingCallbacks;

    ImageLoadingCallbackMarshaller(Executor executor,
            ImageLoader.ImageLoadingCallbacks callbacks) {
        mMainThreadExecutor = executor;
        mImageLoadingCallbacks = callbacks;
    }

    void postStatusUpdate(final ImageLoader.LoadingStatus status) {
        if (mImageLoadingCallbacks != null) {
            if (mMainThreadExecutor != null) {
                mMainThreadExecutor.execute(new Runnable() {
                    @Override
                    public void run() {
                        mImageLoadingCallbacks.onStatusUpdate(status);
                    }
                });
            }
        }
    }

    void postError(final String error) {
        if (mImageLoadingCallbacks != null) {
            if (mMainThreadExecutor != null) {
                mMainThreadExecutor.execute(new Runnable() {
                    @Override
                    public void run() {
                        mImageLoadingCallbacks.onError(error);
                    }
                });
            }
        }
    }

    void postImagesLoaded(final List<Bitmap> bitmaps) {
        if (mImageLoadingCallbacks != null) {
            if (mMainThreadExecutor != null) {
                mMainThreadExecutor.execute(new Runnable() {
                    @Override
                    public void run() {
                        mImageLoadingCallbacks.onComplete(bitmaps);
                    }
                });
            }
        }
    }

    void postImagesLoaded(final ImageCacheHelper imageCache) {
        if (mImageLoadingCallbacks != null) {
            if (mMainThreadExecutor != null) {
                mMainThreadExecutor.execute(new Runnable() {
                    @Override
                    public void run() {
                        mImageLoadingCallbacks.onComplete(imageCache);
                    }
                });
            }
        }
    }
}
