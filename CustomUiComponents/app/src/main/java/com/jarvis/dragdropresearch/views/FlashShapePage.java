package com.jarvis.dragdropresearch.views;

import com.jarvis.dragdropresearch.funwithshapes.FlashShape;
import com.jarvis.dragdropresearch.interpolators.ColorInterpolator;

/**
 * Hosts various shapes.
 * See {@link FlashShape.Type}
 */
public class FlashShapePage extends ScrollPage {

    private FlashShape mFlashShape;

    private ColorInterpolator mBackgroundColorInterpolator;

    public FlashShape getFlashShape() {
        return mFlashShape;
    }

    public void setFlashShape(FlashShape flashShape) {
        mFlashShape = flashShape;
    }

    public ColorInterpolator getBackgroundColorInterpolator() {
        return mBackgroundColorInterpolator;
    }

    public void setBackgroundColorInterpolator(
            ColorInterpolator backgroundColorInterpolator) {
        mBackgroundColorInterpolator = backgroundColorInterpolator;
    }
}
