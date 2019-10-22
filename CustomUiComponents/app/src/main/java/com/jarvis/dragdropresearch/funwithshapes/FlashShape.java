package com.jarvis.dragdropresearch.funwithshapes;

import android.graphics.Color;

import com.jarvis.dragdropresearch.interpolators.AlphaInterpolator;
import com.jarvis.dragdropresearch.interpolators.ColorInterpolator;

import java.util.Random;

public abstract class FlashShape {
    private static final int[] COMPONENT_COLOR_POOL_DEFAULT =
            new int[] {Color.RED, Color.GREEN};

    private Type mType;

    private int mXOffset;

    private int mYOffset;

    private ColorInterpolator mColorInterpolator;

    private AlphaInterpolator mAlphaInterpolator;

    private boolean mAllowMulticoloredComponents;

    private int[] mComponentColors;

    private int[] mComponentColorPool = COMPONENT_COLOR_POOL_DEFAULT;

    FlashShape(Type type) {
        mType = type;
        mComponentColors = mComponentColorPool;
    }

    public Type getType() {
        return mType;
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
     * @return Maximum number of components that this shape can be composed of.
     */
    abstract public int getMaxComponents();

    public boolean allowMultiColoredComponents() {
        return mAllowMulticoloredComponents;
    }

    /**
     * @param allowMulticoloredComponents Set to true if individual segment colors should be respected.
     * If false,the default color used to draw the spiral will be used for all segments.
     */
    public void setAllowMulticoloredComponents(boolean allowMulticoloredComponents) {
        mAllowMulticoloredComponents = allowMulticoloredComponents;
    }

    /**
     * @param componentColorPool Array of colors to be used in conjunction with
     * {@link SpiralShape#generateRandomComponentColors()}. If null or the length is
     * equal to 0, the default color pool will be used.
     *
     * @see #COMPONENT_COLOR_POOL_DEFAULT
     */
    public void setComponentColorPool(int[] componentColorPool) {
        if (componentColorPool != null && componentColorPool.length > 0) {
            mComponentColorPool = componentColorPool;
        } else {
            mComponentColorPool = COMPONENT_COLOR_POOL_DEFAULT;
        }
    }

    /**
     * Generate a random array of colors of size.
     * This array will be used to color the segments, with array[i] coloring segment[i].
     */
    public void generateRandomComponentColors() {
        mComponentColors = new int[getMaxComponents()];

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
        RECTANGLE,
        SPIRAL,
        STAR,
        TRIANGLE
    }
}
