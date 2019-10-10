package com.jarvis.dragdropresearch.views;

/**
 * Describes a page of content that is used in conjunction with {@link AbsCustomScrollingView}.
 */
public class ScrollPage {

    private int mXPosition;

    private int mYPosition;

    private int mHeight;

    private int mWidth;

    private boolean mIsVisible;

    private boolean mIsScrolledToTop;

    public int getXPosition() {
        return mXPosition;
    }

    /**
     * Set's the x position of the page relative to the parent {@link AbsCustomScrollingView}
     * that is hosting it.
     *
     * @param xPosition
     */
    public void setXPosition(int xPosition) {
        mXPosition = xPosition;
    }

    public int getYPosition() {
        return mYPosition;
    }

    /**
     * Set's the y position of the page relative to the parent {@link AbsCustomScrollingView}
     * that is hosting it.
     *
     * @param yPosition
     */
    public void setYPosition(int yPosition) {
        mYPosition = yPosition;
    }

    public int getHeight() {
        return mHeight;
    }

    public void setHeight(int height) {
        mHeight = height;
    }

    public int getWidth() {
        return mWidth;
    }

    public void setWidth(int width) {
        mWidth = width;
    }

    /**
     * Returns true when the top of this page is visible in the {@link AbsCustomScrollingView}.
     */
    public boolean isVisible() {
        return mIsVisible;
    }

    public void setVisible(boolean visible) {
        mIsVisible = visible;
    }

    /**
     * @return True when this page has reached the upper limit of the visible portion of the {@link
     * AbsCustomScrollingView hosting it.}
     */
    public boolean isScrolledToTop() {
        return mIsScrolledToTop;
    }

    public void setScrolledToTop(boolean scrolledToTop) {
        mIsScrolledToTop = scrolledToTop;
    }
}
