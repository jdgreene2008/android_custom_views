package com.jarvis.dragdropresearch.views;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.util.AttributeSet;
import android.util.Log;

import com.jarvis.dragdropresearch.R;
import com.jarvis.dragdropresearch.interpolators.AlphaInterpolator;
import com.jarvis.dragdropresearch.interpolators.ColorInterpolator;
import com.jarvis.dragdropresearch.scrollingpictures.domain.FlashImage;
import com.jarvis.dragdropresearch.utils.ImageCacheHelper;
import com.jarvis.dragdropresearch.utils.ImageLoader;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

/**
 * Pictures flash in and out of view as the user scrolls.
 */
public class ImageFlashView extends AbsCustomScrollingView<ImageFlashPage> {
    private static final String TAG = ImageFlashView.class.getName();
    private static final int PAGE_COUNT = 3;
    private static final int[] IMAGE_POOL =
            new int[] {R.drawable.ic_pizza_pie_large, R.drawable.ic_pizza_cheese_multi,
                    R.drawable.ic_pizza_jalapeno, R.drawable.ic_pizza_pepperoni,
                    R.drawable.ic_pizza_pie_xlarge, R.drawable.ic_pizza_pie_medium};
    private static final int[] COLORS_BACKGROUNDS =
            new int[] {Color.MAGENTA, Color.CYAN, Color.LTGRAY, Color.BLUE, Color.RED};
    /**
     * Image page padding in pixels.
     */
    private static final int PADDING = 50;

    private int mMaxImageWidth;
    private int mMaxImageHeight;

    private ImageCacheHelper mImageCache;

    public ImageFlashView(@NonNull Context context) {
        super(context);
    }

    public ImageFlashView(@NonNull Context context,
            @Nullable AttributeSet attrs) {
        super(context, attrs);
    }

    public ImageFlashView(@NonNull Context context, @Nullable AttributeSet attrs,
            int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    @Override
    protected void initializePages() {
        setInitializedPages(true);
        computeImageBounds();
        setupPages();
        prepareImages();
    }

    private void computeImageBounds() {
        mMaxImageHeight = getMeasuredHeight() - 2 * PADDING;
        mMaxImageWidth = getMeasuredWidth() - 2 * PADDING;
    }

    private void setupPages() {
        mPages = new ArrayList<>(PAGE_COUNT);

        // Assumes fixed-size pages for now.
        final Random random = new Random(System.currentTimeMillis());
        int startPosition = 0;
        for (int i = 1; i <= PAGE_COUNT; i++) {
            ImageFlashPage page = new ImageFlashPage();
            page.setHeight(getMeasuredHeight() - (getPaddingTop() + getPaddingBottom()));
            page.setWidth(getMeasuredWidth() - (getPaddingStart() + getPaddingEnd()));
            page.setXPosition(getPaddingStart());

            if (startPosition == 0) {
                startPosition = i * (getMeasuredHeight() - getPaddingBottom());
            } else {
                startPosition += page.getHeight();
            }
            page.setYPosition(startPosition);

            mPages.add(page);

            ColorInterpolator interpolator = new ColorInterpolator(page.getHeight());
            interpolator.setColor(COLORS_BACKGROUNDS[i]);
            page.setBackgroundColorInterpolator(interpolator);
        }

        setContentHeight(
                (getMeasuredHeight() + getPaddingTop() + getPaddingBottom()) * (PAGE_COUNT + 1));
    }

    private void prepareImages() {

        ImageCacheHelper cache = ImageCacheHelper.getInstance();
        cache.initCache();
        ImageLoader loader = ImageLoader.getInstance();

        ImageLoader.Configuration configuration = new ImageLoader.Configuration();
        configuration.setCache(cache);
        configuration.setMaxImageHeight(mMaxImageHeight);
        configuration.setMaxImageWidth(mMaxImageWidth);

        final int[] images = getRandomImages();
        for (int i = 0; i < PAGE_COUNT; i++) {
            FlashImage image = new FlashImage();
            image.setAlphaInterpolator(new AlphaInterpolator(getMeasuredHeight(), 0));
            image.setImageResId(images[i]);
            mPages.get(i).setImage(image);
        }

        loader.loadImages(getContext(), new ImageLoader.ImageLoadingCallbacks() {
            @Override
            public void onStatusUpdate(ImageLoader.LoadingStatus status) {
                Log.d(TAG, "onStatusUpdated() called :: " + status);
            }

            @Override
            public void onComplete(@Nullable ImageCacheHelper cache) {
                Log.d(TAG, "Image Loading Complete");
                mImageCache = cache;
                if (mImageCache != null) {
                    for (ImageFlashPage page : mPages) {
                        FlashImage image = page.getImage();
                        Bitmap bm =
                                mImageCache.getBitmapFromMemCache(
                                        String.valueOf(image.getImageResId()));
                        if (bm != null) {
                            image.setImageAvailable(true);

                            int imgHeight = bm.getHeight();
                            int imgWidth = bm.getWidth();

                            int imgTop = (page.getHeight() / 2 - imgHeight / 2) + getPaddingTop();
                            int imgLeft = (page.getWidth() / 2 - imgWidth / 2) + getPaddingStart();

                            image.setXOffset(imgLeft);
                            image.setYOffset(imgTop);
                        }
                    }
                }
            }

            @Override
            public void onComplete(@Nullable List<Bitmap> images) {
                // Unused
            }

            @Override
            public void onError(String error) {
                Log.d(TAG, "onError() called with error :: " + error);
            }
        }, configuration, images);
    }

    private int[] getRandomImages() {
        Random random = new Random(System.currentTimeMillis());
        int[] images = new int[PAGE_COUNT];

        for (int i = 0; i < images.length; i++) {
            images[i] = IMAGE_POOL[random.nextInt(IMAGE_POOL.length)];
        }

        return images;
    }

    @Override
    protected void onDraw(Canvas canvas) {
        drawPages(canvas);
        super.onDraw(canvas);
    }

    private void drawPages(Canvas canvas) {
        if (mPages != null) {
            for (ImageFlashPage page : mPages) {
                drawBackground(canvas, page);
                drawPageImage(page, canvas);
            }
        }
    }

    private void drawPageImage(ImageFlashPage page, Canvas canvas) {
        if (!page.isVisible()) {
            return;
        }

        FlashImage image = page.getImage();
        if (!image.isImageAvailable()) return;

        if (page.getYPosition() + image.getYOffset() >
                (getMeasuredHeight() + getScrollY() + getPaddingTop())) {
            // Picture has not come into view. Do not draw.
            return;
        }

        final Bitmap bm = mImageCache.getBitmapFromMemCache(String.valueOf(image.getImageResId()));
        if (bm == null) return;

        Paint paint = new Paint();

        AlphaInterpolator interpolator = image.getAlphaInterpolator();
        interpolator.updateValue(getContentBoundsBottom() - page.getYPosition());
        paint.setAlpha(interpolator.getInterpolatedAlpha());

        if (getContentBoundsTop() >= (page.getYPosition() + image.getYOffset())) {
            // Means we've scrolled the current page to the top of the visible part of the image.
            canvas.drawBitmap(bm,
                    page.getXPosition() + image.getXOffset(),
                    getContentBoundsTop() + image.getYOffset(), paint);
        } else {
            canvas.drawBitmap(bm,
                    page.getXPosition() + image.getXOffset(),
                    page.getYPosition() + +image.getYOffset() + getPaddingTop(), paint);
        }
    }

    private void drawBackground(Canvas canvas, ImageFlashPage page) {
        if (page.isVisible()) {
            ColorInterpolator interpolator = page.getBackgroundColorInterpolator();
            interpolator
                    .updateValue((getContentBoundsBottom() - page.getYPosition()));
            drawShadedBackground(canvas, interpolator, page);
        }
    }
}
