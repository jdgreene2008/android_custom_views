package com.jarvis.dragdropresearch.views;

import android.content.Context;
import android.graphics.Canvas;
import android.util.AttributeSet;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import java.util.ArrayList;

/**
 * Pictures flash in and out of view as the user scrolls.
 */
public class ImageFlashView extends CustomScrollingView<ImagePage> {
    /**
     * Maximum number of images to cache.
     */
    private static final int IMAGE_CACHE_SIZE = 5;

    /**
     * Image page padding in pixels.
     */
    private static final int IMAGE_PAGE_PADDING = 20;

    private int mMaxImageWidth;
    private int mMaxImageHeight;

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
    }

    private void computeImageBounds() {
        mMaxImageHeight = getMeasuredHeight() - 2 * IMAGE_PAGE_PADDING;
        mMaxImageWidth = getMeasuredWidth() - 2 * IMAGE_PAGE_PADDING;
    }

    private void setupPages() {
        final int pageCount = 3;
        mPages = new ArrayList<>(pageCount);

        // Assumes fixed-size pages for now.
        for (int i = 1; i <= pageCount; i++) {
            ImagePage page = new ImagePage();
            page.setHeight(getMeasuredHeight());
            page.setWidth(getMeasuredWidth());
            page.setXPosition(0);
            page.setYPosition(i * getMeasuredHeight());
            mPages.add(page);
        }

        // TODO: Setup image dimensions:
    }

    @Override
    protected void onDraw(Canvas canvas) {
        drawPages(canvas);
        super.onDraw(canvas);
    }

    private void drawPages(Canvas canvas) {
        if (mPages != null) {
            // Draw image pages.
        }
    }
}
