package com.jarvis.dragdropresearch.funwithshapes;

import android.graphics.RectF;

/**
 * Descripes the arc that makes up a part of a spiral path.
 */
public class SpiralArcDescriptor {

    private RectF mBounds;

    private float mLeft;
    private float mTop;
    private float mBottom;
    private float mRight;

    private float mStartAngle;
    private float mSweepAngle;

    public SpiralArcDescriptor(RectF bounds, float startAngle, float sweepAngle) {
        mBounds = bounds;
        mStartAngle = startAngle;
        mSweepAngle = sweepAngle;
    }

    public SpiralArcDescriptor(float left, float top, float bottom, float right, float startAngle,
            float sweepAngle) {
        mLeft = left;
        mTop = top;
        mBottom = bottom;
        mRight = right;
        mStartAngle = startAngle;
        mSweepAngle = sweepAngle;
    }

    public RectF getBounds() {
        return mBounds;
    }

    public void setBounds(RectF bounds) {
        mBounds = bounds;
    }

    public float getLeft() {
        return mLeft;
    }

    public void setLeft(float left) {
        mLeft = left;
    }

    public float getTop() {
        return mTop;
    }

    public void setTop(float top) {
        mTop = top;
    }

    public float getBottom() {
        return mBottom;
    }

    public void setBottom(float bottom) {
        mBottom = bottom;
    }

    public float getRight() {
        return mRight;
    }

    public void setRight(float right) {
        mRight = right;
    }

    public float getStartAngle() {
        return mStartAngle;
    }

    public void setStartAngle(float startAngle) {
        mStartAngle = startAngle;
    }

    public float getSweepAngle() {
        return mSweepAngle;
    }

    public void setSweepAngle(float sweepAngle) {
        mSweepAngle = sweepAngle;
    }
}
