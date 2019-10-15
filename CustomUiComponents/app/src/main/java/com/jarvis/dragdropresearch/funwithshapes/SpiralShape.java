package com.jarvis.dragdropresearch.funwithshapes;

import android.graphics.Color;

import com.jarvis.dragdropresearch.interpolators.SpiralInterpolator;

import java.util.Random;

public class SpiralShape extends FlashShape {
    private static final int[] SEGMENT_COLOR_POOL_DEFAULT =
            new int[] {Color.RED,Color.BLUE};

    private SpiralInterpolator mSpiralInterpolator;
    private int[] mSegmentColors;
    private int[] mSegmentColorPool = SEGMENT_COLOR_POOL_DEFAULT;

    public SpiralShape() {
        super(Type.SPIRAL);
    }

    public SpiralInterpolator getSpiralInterpolator() {
        return mSpiralInterpolator;
    }

    public void setSpiralInterpolator(
            SpiralInterpolator spiralInterpolator) {
        mSpiralInterpolator = spiralInterpolator;
    }

    public int[] getSegmentColors() {
        return mSegmentColors;
    }

    /**
     * @param segmentColors Array of colors to be used for coloring each segment. The segments will be
     * colored in order of segmentColors[segmentIndex % segmentColors.length] to account for the
     * total segment count being greater than the number of segment colors.
     */
    public void setSegmentColors(int[] segmentColors) {
        mSegmentColors = segmentColors;
    }

    public int[] getSegmentColorPool() {
        return mSegmentColorPool;
    }

    /**
     * @param segmentColorPool Array of colors to be used in conjunction with
     * {@link SpiralShape#generateRandomSegmentColors()}. If null or the length is
     * equal to 0, the {@link SpiralShape#SEGMENT_COLOR_POOL_DEFAULT} will be used.
     */
    public void setSegmentColorPool(int[] segmentColorPool) {
        if (segmentColorPool != null && segmentColorPool.length > 0) {
            mSegmentColorPool = segmentColorPool;
        } else {
            mSegmentColorPool = SEGMENT_COLOR_POOL_DEFAULT;
        }
    }

    /**
     * Generate a random array of colors of size {@link SpiralInterpolator#MAX_SEGMENT_COUNT}.
     * This array will be used to color the segments, with array[i] coloring segment[i].
     */
    public void generateRandomSegmentColors() {
        mSegmentColors = new int[SpiralInterpolator.MAX_SEGMENT_COUNT];

        final Random random = new Random(System.currentTimeMillis());
        for (int i = 0; i < mSegmentColors.length; i++) {
            mSegmentColors[i] = mSegmentColorPool[random.nextInt(300) %
                    mSegmentColorPool.length];
        }
    }
}
