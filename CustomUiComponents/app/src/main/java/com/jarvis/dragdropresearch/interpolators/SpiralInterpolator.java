package com.jarvis.dragdropresearch.interpolators;

import java.util.ArrayList;
import java.util.List;

public class SpiralInterpolator extends Interpolator {
    /**
     * Represents the number of 180-degree arc segments in this spiral
     * when counting clockwise starting at the 3-o'clock position.
     */
    public static final int DEFAULT_SEGMENT_COUNT = 40;

    private int mSegmentMax = DEFAULT_SEGMENT_COUNT;

    private float mMaxSegmentHeight;
    private float mMaxSegmentWidth;

    private float mSegmentHeightWidthRatio;

    private float mSegmentWidthFactor;
    private float mSegmentHeightFactor;

    private List<SpiralSegment> mSegments = new ArrayList<>();

    public SpiralInterpolator(int maxValue, int maxSegmentCount, float maxSegmentHeight,
            float maxSegmentWidth) {
        super(maxValue);
        mMaxSegmentWidth = maxSegmentWidth;
        mMaxSegmentHeight = maxSegmentHeight;

        mSegmentHeightWidthRatio = mMaxSegmentHeight / mMaxSegmentWidth;

        // TODO: Move this code to builder.
        mSegmentWidthFactor = mMaxSegmentWidth / maxSegmentCount;
        mSegmentHeightFactor = mSegmentHeightWidthRatio * mSegmentWidthFactor;
    }

    public SpiralInterpolator(int maxValue, int maxSegmentHeight, int maxSegmentWidth) {
        this(maxValue, DEFAULT_SEGMENT_COUNT, maxSegmentHeight, maxSegmentWidth);
    }

    public int getSegmentMax() {
        return mSegmentMax;
    }

    public void setSegmentMax(int segmentMax) {
        mSegmentMax = segmentMax;
    }

    public float getMaxSegmentHeight() {
        return mMaxSegmentHeight;
    }

    public void setMaxSegmentHeight(float maxSegmentHeight) {
        mMaxSegmentHeight = maxSegmentHeight;
    }

    public float getMaxSegmentWidth() {
        return mMaxSegmentWidth;
    }

    public void setMaxSegmentWidth(float maxSegmentWidth) {
        mMaxSegmentWidth = maxSegmentWidth;
    }

    public List<SpiralSegment> getSegments() {
        float interpolatedWidth = getInterpolation() * mMaxSegmentWidth;
        int segmentCount = (int)(interpolatedWidth / mSegmentWidthFactor);

        int finalSegmentCount = 0;

        // Ensure that width or height are not out of bounds of the max.
        while (finalSegmentCount <= segmentCount &&
                finalSegmentCount * mSegmentHeightFactor <= mMaxSegmentHeight) {
            finalSegmentCount++;
        }

        mSegments.clear();
        for (int i = 0; i < finalSegmentCount; i++) {
            SpiralSegment segment = new SpiralSegment(i % 2 == 0 ? SpiralSegment.Type.BOTTOM :
                    SpiralSegment.Type.TOP);
            segment.setWidth((i + 1) * mSegmentWidthFactor);
            segment.setHeight((i + 1) * mSegmentHeightFactor);
            mSegments.add(segment);
        }

        return mSegments;
    }

    // TODO: Create a Builder to handle the construction.
}
