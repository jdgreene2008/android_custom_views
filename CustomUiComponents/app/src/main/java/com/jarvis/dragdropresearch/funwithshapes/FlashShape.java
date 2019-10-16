package com.jarvis.dragdropresearch.funwithshapes;

import android.graphics.Color;

import com.jarvis.dragdropresearch.interpolators.AlphaInterpolator;
import com.jarvis.dragdropresearch.interpolators.ColorInterpolator;
import com.jarvis.dragdropresearch.interpolators.SpiralInterpolator;

import java.util.Random;

public class FlashShape {
    private static final int[] COMPONENT_COLOR_POOL_DEFAULT =
            new int[] {Color.RED, Color.GREEN};

    private Type mType;

    private int mXOffset;

    private int mYOffset;

    private ColorInterpolator mColorInterpolator;

    private AlphaInterpolator mAlphaInterpolator;

    protected int[] mComponentColors;

    private int[] mComponentColorPool;

    public FlashShape(Type type) {
        mType = type;
    }

    public Type getType() {
        return mType;
    }

    public void setType(Type type) {
        mType = type;
    }

    public int getXOffset() {
        return mXOffset;
    }

    public void setXOffset(int XOffset) {
        mXOffset = XOffset;
    }

    public int getYOffset() {
        return mYOffset;
    }

    public void setYOffset(int YOffset) {
        mYOffset = YOffset;
    }

    public int[] getComponentColors() {
        return mComponentColors;
    }

    /**
     * @param componentColors Array of colors to be used for coloring each segment. The segments will be
     * colored in order of componentColors[segmentIndex % componentColors.length] to account for the
     * total segment count being greater than the number of segment colors.
     */
    public void setComponentColors(int[] componentColors) {
        mComponentColors = componentColors;
    }

    public int[] getComponentColorPool() {
        return mComponentColorPool;
    }

    /**
     * @param componentColorPool Array of colors to be used in conjunction with
     * {@link SpiralShape#generateRandomSegmentColors()}. If null or the length is
     * equal to 0, the default color pool will be used.
     * @see  #COMPONENT_COLOR_POOL_DEFAULT
     */
    public void setComponentColorPool(int[] componentColorPool) {
        if (componentColorPool != null && componentColorPool.length > 0) {
            mComponentColorPool = componentColorPool;
        } else {
            mComponentColorPool = COMPONENT_COLOR_POOL_DEFAULT;
        }
    }

    /**
     * Generate a random array of colors of size {@link SpiralInterpolator#MAX_SEGMENT_COUNT}.
     * This array will be used to color the segments, with array[i] coloring segment[i].
     */
    public void generateRandomSegmentColors() {
        mComponentColors = new int[SpiralInterpolator.MAX_SEGMENT_COUNT];

        final Random random = new Random(System.currentTimeMillis());
        for (int i = 0; i < mComponentColors.length; i++) {
            mComponentColors[i] = mComponentColorPool[random.nextInt(300) %
                    mComponentColorPool.length];
        }
    }

    public ColorInterpolator getColorInterpolator() {
        return mColorInterpolator;
    }

    public void setColorInterpolator(
            ColorInterpolator colorInterpolator) {
        mColorInterpolator = colorInterpolator;
    }

    public AlphaInterpolator getAlphaInterpolator() {
        return mAlphaInterpolator;
    }

    public void setAlphaInterpolator(
            AlphaInterpolator alphaInterpolator) {
        mAlphaInterpolator = alphaInterpolator;
    }

    public enum Type {
        ARC,
        SPIRAL,
        RECTANGLE,
        TRIANGLE
    }
}
