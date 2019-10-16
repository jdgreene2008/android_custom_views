package com.jarvis.dragdropresearch.funwithshapes;

/**
 * Represents a 180-degree segment of a spiral.
 */
public class SpiralSegment {
    public static final int SEGMENT_COLOR_DEFAULT = -1;
    private float mHeight;
    private float mWidth;
    private Type mType;

    public SpiralSegment(Type type) {
        mType = type;
    }

    public float getHeight() {
        return mHeight;
    }

    /**
     * @param height Represents the height of the rectangle that defines the bounds
     * for the oval of this segment.
     */
    public void setHeight(float height) {
        mHeight = height;
    }

    public float getWidth() {
        return mWidth;
    }

    /**
     * @param width Represents the width of the rectangle that defines the bounds
     * for the oval of this segment.
     */
    public void setWidth(float width) {
        mWidth = width;
    }

    public Type getType() {
        return mType;
    }

    public void setType(Type type) {
        mType = type;
    }

    public enum Type {
        TOP,
        BOTTOM
    }
}
