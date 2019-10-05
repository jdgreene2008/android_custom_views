package com.jarvis.dragdropresearch.scrollingpictures.domain;

import com.jarvis.dragdropresearch.views.ImagePage;

import androidx.annotation.DrawableRes;

public class FlashImage {

    @DrawableRes
    private int mImageResId;

    private int mXOffset;

    private int mYOffset;

    public int getImageResId() {
        return mImageResId;
    }

    public void setImageResId(@DrawableRes int imageResId) {
        mImageResId = imageResId;
    }

    /**
     * @return Distance in pixels from the left side of the {@link ImagePage}
     * hosting this image.
     */
    public int getXOffset() {
        return mXOffset;
    }

    /**
     * @param XOffset Distance in pixels from the left side of the {@link ImagePage}
     * hosting this image.
     */
    public void setXOffset(int XOffset) {
        mXOffset = XOffset;
    }

    /**
     * @return Distance in pixels from the top of the {@link ImagePage} hosting this image.
     */
    public int getYOffset() {
        return mYOffset;
    }

    /**
     * @param YOffset Distance in  pixels from the top of the {@link ImagePage} hosting this
     * image.
     */
    public void setYOffset(int YOffset) {
        mYOffset = YOffset;
    }
}
