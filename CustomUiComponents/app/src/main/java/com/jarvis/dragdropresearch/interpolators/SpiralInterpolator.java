package com.jarvis.dragdropresearch.interpolators;

import com.jarvis.dragdropresearch.funwithshapes.SpiralSegment;

import java.util.ArrayList;
import java.util.List;

public class SpiralInterpolator extends Interpolator {

    private float mMaxSegmentHeight;
    private float mMaxSegmentWidth;

    private float mSegmentHeightWidthRatio;

    private float mSegmentWidthFactor;
    private float mSegmentHeightFactor;

    private boolean mAllowMulticoloredSegments;

    private List<SpiralSegment> mSegments = new ArrayList<>();

    public SpiralInterpolator(int maxValue, float maxSegmentHeight,
            float maxSegmentWidth, int maxSegmentCount) {
        super(maxValue);
        mMaxSegmentWidth = maxSegmentWidth;
        mMaxSegmentHeight = maxSegmentHeight;

        mSegmentHeightWidthRatio = mMaxSegmentHeight / mMaxSegmentWidth;

        // TODO: Move this code to builder.
        final int finalSegmentCount = maxSegmentCount < 1 ? 1 : maxSegmentCount;
        mSegmentWidthFactor = mMaxSegmentWidth / finalSegmentCount;
        mSegmentHeightFactor = mSegmentHeightWidthRatio * mSegmentWidthFactor;
    }

    public SpiralInterpolator(int maxValue, int maxSegmentHeight, int maxSegmentWidth) {
        this(maxValue, maxSegmentHeight, maxSegmentWidth, 1);
    }

    public List<SpiralSegment> getSegments() {
        float interpolatedWidth = getInterpolation() * mMaxSegmentWidth;
        int segmentCount = (int)(interpolatedWidth / mSegmentWidthFactor);

        int finalSegmentCount = 0;

        // Ensure that width or height are not out of bounds of the max.
        while (finalSegmentCount <= segmentCount &&
                finalSegmentCount * mSegmentHeightFactor <= mMaxSegmentHeight
                && finalSegmentCount * mSegmentWidthFactor < mMaxSegmentWidth) {
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
