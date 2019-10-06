package com.jarvis.dragdropresearch.utils;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.os.Build;

import androidx.annotation.DrawableRes;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Executor;

/**
 * Asynchronous image loader that loads images on a worker thread.
 * The {@link ImageLoadingCallbacks} can be used to monitor the status of the image loading process.
 */
public class ImageLoader {

    private static ImageLoader sInstance;

    private ImageLoadingCallbackMarshaller mMarshaller;
    private Executor mWorkerThread;

    private ImageLoader() {
        mWorkerThread = ImageLoadingExecutors.getInstance().getWorkerThread();
    }

    public static ImageLoader getInstance() {
        synchronized (ImageLoader.class) {
            if (sInstance == null) {
                synchronized (ImageLoader.class) {
                    sInstance = new ImageLoader();
                }
            }
        }

        return sInstance;
    }

    /**
     * Load the bitmaps for a list of image resources.
     *
     * @param config {@link Configuration} specifying the options for loading the images. If {@link Configuration#mCache}
     * is set to a non-null value, the images will be set loaded into the {@link ImageCacheHelper} LruCache
     * with the key for each image set to the string value of the image resource id.
     * @param imageResourcesIds {@link DrawableRes} ids representing the images to load.
     */
    public void loadImages(@NonNull final Context context,
            @NonNull final ImageLoadingCallbacks callbacks,
            @NonNull final Configuration config,
            @DrawableRes final int[] imageResourcesIds) {
        mMarshaller = new ImageLoadingCallbackMarshaller(
                ImageLoadingExecutors.getInstance().getMainThread(), callbacks);
        mWorkerThread.execute(new Runnable() {
            @Override
            public void run() {
                processImages(context.getApplicationContext(), config, imageResourcesIds);
            }
        });
    }

    private void processImages(@NonNull Context context, @NonNull Configuration config,
            int[] imageResourcesIds) {
        mMarshaller.postStatusUpdate(LoadingStatus.STARTED);

        List<Bitmap> images = new ArrayList<>();
        for (int i = 0; i < imageResourcesIds.length; i++) {
            int imgResId = imageResourcesIds[i];

            BitmapFactory.Options options = getImageSpecs(context, imgResId);

            options.inSampleSize =
                    calculateInSampleSize(options, config.mMaxImageWidth, config.mMaxImageHeight);

            BitmapDrawable resourceDrawable;
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                resourceDrawable = (BitmapDrawable)context.getDrawable(imgResId);
            } else {
                resourceDrawable = (BitmapDrawable)context.getResources().getDrawable(imgResId);
            }
            if (resourceDrawable == null || resourceDrawable.getBitmap() == null) continue;

            Bitmap output = resourceDrawable.getBitmap();
            if (output == null) {
                mMarshaller.postError("Could not decode bitmap with id :: " + imgResId);
                continue;
            }

            // TODO: Figure out how to correctly load a scaled down version of bitmap from InputStream.
            // It is currently throwing a null pointer exception.

            if (config.mCache != null) {
                ImageCacheHelper cache = config.mCache;
                cache.addEntry(String.valueOf(imageResourcesIds), output);
            } else {
                images.add(output);
            }
        }

        if (config.mCache != null) {
            mMarshaller.postImagesLoaded(config.mCache);
        } else {
            mMarshaller.postImagesLoaded(images);
        }

        mMarshaller.postStatusUpdate(LoadingStatus.COMPLETED);
    }

    private BitmapFactory.Options getImageSpecs(@NonNull Context context,
            @DrawableRes int imageResId) {
        BitmapFactory.Options options = new BitmapFactory.Options();
        options.inJustDecodeBounds = true;
        BitmapFactory.decodeResource(context.getResources(), imageResId, options);
        return options;
    }

    private int calculateInSampleSize(
            BitmapFactory.Options options, int reqWidth, int reqHeight) {
        // Raw height and width of image
        final int height = options.outHeight;
        final int width = options.outWidth;
        int inSampleSize = 1;

        if (height > reqHeight || width > reqWidth) {

            final int halfHeight = height / 2;
            final int halfWidth = width / 2;

            // Calculate the largest inSampleSize value that is a power of 2 and keeps both
            // height and width larger than the requested height and width.
            while ((halfHeight / inSampleSize) >= reqHeight
                    && (halfWidth / inSampleSize) >= reqWidth) {
                inSampleSize *= 2;
            }
        }

        return inSampleSize;
    }

    private InputStream getBitmapInputStream(Bitmap bitmap) {
        ByteArrayOutputStream bos = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.PNG, 0 /*ignored for PNG*/, bos);
        byte[] bitmapdata = bos.toByteArray();
        return new ByteArrayInputStream(bitmapdata);
    }

    /**
     * Specifies the configuration to use when loading images.
     */
    public static class Configuration {

        private ImageCacheHelper mCache;
        private int mMaxImageHeight = -1;
        private int mMaxImageWidth = -1;

        public ImageCacheHelper getCache() {
            return mCache;
        }

        /**
         * Set the cache that the image bitmaps will be loaded into. If null the {@link Bitmap}s will
         * be loaded into a {@link List<Bitmap>}.
         *
         * @param cache
         */
        public void setCache(ImageCacheHelper cache) {
            mCache = cache;
        }

        public int getMaxImageHeight() {
            return mMaxImageHeight;
        }

        public void setMaxImageHeight(int maxImageHeight) {
            mMaxImageHeight = maxImageHeight;
        }

        public int getMaxImageWidth() {
            return mMaxImageWidth;
        }

        public void setMaxImageWidth(int maxImageWidth) {
            mMaxImageWidth = maxImageWidth;
        }
    }

    public enum LoadingStatus {
        STARTED,
        STOPPED,
        COMPLETED
    }

    public interface ImageLoadingCallbacks {

        /**
         * Callback invoked on the UI thread when certain states are reached in the image loading
         * process. See {@link LoadingStatus} for a list of the different states.
         *
         * @param status {@link LoadingStatus} representing the current state of the image loading
         * process.
         */
        void onStatusUpdate(LoadingStatus status);

        /**
         * Called with a link to the {@link ImageCacheHelper} when loading
         * by invoking the {@link ImageLoader#loadImages(Context, ImageLoadingCallbacks, Configuration, int[])}
         * {@link Configuration#mCache} set to a non-null value.
         * This method is invoked on the UI thread when used with the {@link ImageLoader}
         *
         * @param cache {@link ImageCacheHelper} containing the cache for the images that were
         * loaded.
         */
        void onComplete(@Nullable ImageCacheHelper cache);

        /**
         * Called with a link to the {@link ImageCacheHelper} when loading
         * by invoking the {@link ImageLoader#loadImages(Context, ImageLoadingCallbacks, Configuration, int[])}
         * {@link Configuration#mCache} set to null.
         * This method is invoked on the UI thread when used with the {@link ImageLoader}
         *
         * @param images {@link List<Bitmap>} containing the list of bitmaps that were
         * loaded.
         */
        void onComplete(@Nullable List<Bitmap> images);

        void onError(String error);
    }
}
