package com.jarvis.dragdropresearch.scrollingpictures.domain;

import com.jarvis.dragdropresearch.interpolators.AlphaInterpolator;
import com.jarvis.dragdropresearch.views.ImagePage;

import androidx.annotation.DrawableRes;

public class FlashImage {

    @DrawableRes
    private int mImageResId;

    private boolean mImageAvailable;

    private int mXOffset;

    private int mYOffset;

    private AlphaInterpolator mAlphaInterpolator;

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

    /**
     * @return {@link AlphaInterpolator} that will manage the visibility of the image.
     */
    public AlphaInterpolator getAlphaInterpolator() {
        return mAlphaInterpolator;
    }

    /**
     * @param alphaInterpolator Set as the {@link AlphaInterpolator} that will manage
     * the visibility of the image.
     */
    public void setAlphaInterpolator(
            AlphaInterpolator alphaInterpolator) {
        mAlphaInterpolator = alphaInterpolator;
    }

    /**
     * @return True if the image is loaded.
     */
    public boolean isImageAvailable() {
        return mImageAvailable;
    }

    /**
     * @param imageAvailable Should be set to true when the image is available. Available
     * means that the image has been loaded into memory or on disk and can be retrieved.
     */
    public void setImageAvailable(boolean imageAvailable) {
        mImageAvailable = imageAvailable;
    }
}
