package com.jarvis.dragdropresearch.views;

import com.jarvis.dragdropresearch.interpolators.ColorInterpolator;
import com.jarvis.dragdropresearch.scrollingpictures.domain.FlashImage;

public class ImageFlashPage extends ScrollPage {

  private FlashImage mImage;
  private ColorInterpolator mBackgroundColorInterpolator;

    public FlashImage getImage() {
        return mImage;
    }

    public void setImage(FlashImage image) {
        mImage = image;
    }

    public ColorInterpolator getBackgroundColorInterpolator() {
        return mBackgroundColorInterpolator;
    }

    public void setBackgroundColorInterpolator(
            ColorInterpolator backgroundColorInterpolator) {
        mBackgroundColorInterpolator = backgroundColorInterpolator;
    }
}
