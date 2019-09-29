package com.jarvis.dragdropresearch.utils;

import android.graphics.Bitmap;
import android.util.LruCache;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

/**
 * LRU Cache wrapper for managing {@link Bitmap} entries.
 */
public class ImageCacheHelper {

    private static ImageCacheHelper sInstance;
    private LruCache<String, Bitmap> mImageCache;

    private ImageCacheHelper() {

    }

    public static ImageCacheHelper getInstance() {
        synchronized (ImageCacheHelper.class) {
            if (sInstance == null) {
                synchronized (ImageCacheHelper.class) {
                    sInstance = new ImageCacheHelper();
                }
            }
        }

        return sInstance;
    }

    public void initCache() {
        if (mImageCache != null) {
            mImageCache.evictAll();
            mImageCache = null;
        }

        // Get max available VM memory, exceeding this amount will throw an
        // OutOfMemory exception. Stored in kilobytes as LruCache takes an
        // int in its constructor.
        final int maxMemory = (int)(Runtime.getRuntime().maxMemory() / 1024);

        // Use 1/8th of the available memory for this memory cache.
        final int cacheSize = maxMemory / 8;

        mImageCache = new LruCache<String, Bitmap>(cacheSize) {
            @Override
            protected int sizeOf(String key, Bitmap bitmap) {
                // The cache size will be measured in kilobytes rather than
                // number of items.
                return bitmap.getByteCount() / 1024;
            }
        };
    }

    public void addEntry(String key, Bitmap bitmap) {
        if (mImageCache == null) {
            throw new IllegalStateException(
                    "Memory cache is not initialized.ImageCacheHelper.initCache() must be called");
        }

        if (getBitmapFromMemCache(key) == null) {
            mImageCache.put(key, bitmap);
        }
    }

    @Nullable
    public Bitmap getBitmapFromMemCache(String key) {
        if (mImageCache == null) return null;

        return mImageCache.get(key);
    }

    public void removeEntry(@NonNull String key) {
        if (mImageCache != null) {
            mImageCache.remove(key);
        }
    }
}
