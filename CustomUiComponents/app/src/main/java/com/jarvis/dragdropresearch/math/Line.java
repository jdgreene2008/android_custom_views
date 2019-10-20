package com.jarvis.dragdropresearch.math;

import android.graphics.PointF;

import androidx.annotation.Nullable;

public class Line extends Equation {
    private static final String TAG = Line.class.getName();
    private static final String DESCRIPTION =
            "Represents the slope-intercept form of an equation for a line.";

    private Float mSlope;
    private Float mYIntercept;
    private Float mXIntercept;

    private Type mType;

    /**
     * Create an equation for a {@link Line} in y-intercept form with the given slope and y-intercept.
     *
     * @param slope
     * @param yIntercept
     */
    private Line(@Nullable Float slope, Float yIntercept, Float xIntercept, Type type) {
        mSlope = slope;
        mYIntercept = yIntercept;
        mXIntercept = xIntercept;
        mType = type;
    }

    /**
     * Get the y-coordinate of the point on the line with the provided x-coordinate.
     *
     * @param x
     *
     * @return Y-coordinate.
     */
    public Float getY(float x) {
        if (mSlope == null || mSlope == 0) return mYIntercept;
        return mSlope * x + mYIntercept;
    }

    /**
     * Get the x-coordinate of the point on the line with the provided y-coordinate.
     *
     * @param y
     *
     * @return X-coordinate.
     */
    @Nullable
    public Float getX(float y) {
        if (mSlope == null || mSlope == 0) return null;
        return (y - mYIntercept) / mSlope;
    }

    public boolean containsPoint(PointF point) {
        if (mType == Type.HORIZONTAL) {
            return point.y == mYIntercept;
        } else if (mType == Type.VERTICAL) {
            return point.x == mXIntercept;
        } else {
            return point.x == (point.y - mYIntercept) / mSlope;
        }
    }

    public Type getType() {
        return mType;
    }

    /**
     * @return X-coordinate for point where the line crosses the X-axis. Y is 0 at this point.
     */
    public Float getXIntercept() {
        if (mType == Type.VERTICAL) {
            return mXIntercept;
        } else if (mType == Type.HORIZONTAL) {
            return null;
        } else {
            return -1 * mYIntercept / mSlope;
        }
    }

    @Nullable
    public Float getSlope() {
        return mSlope;
    }

    public Float getYIntercept() {
        return mYIntercept;
    }

    /**
     * @return {@link PointF) representing the midpoint between two points on this line.
     */
    @Nullable
    public PointF getMidpoint(PointF a, PointF b) {
        if (!containsPoint(a) || !containsPoint(b)) {
            return null;
        }

        return new PointF((a.x + b.x) / 2, (a.y + b.y) / 2);
    }

    /**
     * @return {@link PointF} containing the x and y values for the unit vector for this line.
     */
    public PointF getUnitVector() {
        if (mType == Type.VERTICAL) {
            return new PointF(0, 1);
        } else if (mType == Type.HORIZONTAL) {
            return new PointF(1, 0);
        } else {
            // Pick two random points on the line and determine the unit vector.

            float x1 = 1;
            float y1 = getY(x1);

            float x2 = 6;
            float y2 = getY(x2);

            PointF differenceVector = new PointF(x2 - x1, y2 - y1);
            float magnitude = (float)Math
                    .sqrt(Math.pow(differenceVector.x, 2) + Math.pow(differenceVector.y, 2));
            return new PointF(differenceVector.x / magnitude, differenceVector.y / magnitude);
        }
    }

    @Override
    String getDescription() {
        return DESCRIPTION;
    }

    public enum Type {
        DIAGONAL,
        VERTICAL,
        HORIZONTAL
    }

    public static class Builder {
        private Float mSlope;
        private Float mYIntercept;
        private Float mXIntercept;

        private Type mType;

        public Builder(Type type) {
            mType = type;
        }

        public Builder setSlope(Float slope) {
            mSlope = slope;
            return this;
        }

        public Builder setYIntercept(Float YIntercept) {
            mYIntercept = YIntercept;
            return this;
        }

        public Builder setXIntercept(Float XIntercept) {
            mXIntercept = XIntercept;
            return this;
        }

        public Line build() {
            if (mType == Type.VERTICAL) {
                return new Line(null, null, mXIntercept, Type.VERTICAL);
            } else if (mType == Type.HORIZONTAL ||
                    mSlope == 0) {
                return new Line(0f, mYIntercept, null, Type.HORIZONTAL);
            } else {
                if (mYIntercept == null) mYIntercept = 0f;
                return new Line(mSlope, mYIntercept, mXIntercept, Type.DIAGONAL);
            }
        }
    }
}
