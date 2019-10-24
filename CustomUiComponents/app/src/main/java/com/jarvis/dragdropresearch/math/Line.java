package com.jarvis.dragdropresearch.math;

import android.graphics.PointF;

import androidx.annotation.Nullable;

public class Line extends Equation {
    private static final String TAG = Line.class.getName();
    private static final float PRECISION = 0.01f;
    private static final String DESCRIPTION =
            "Represents the slope-intercept form of an equation for a line.";

    private Float mSlope;
    private Float mOrthogonalLineSlope;
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
            return Math.abs(point.y - mYIntercept) <= PRECISION;
        } else if (mType == Type.VERTICAL) {
            return Math.abs(point.x - mXIntercept) <= PRECISION;
        } else {
            return Math.abs(point.x - ((point.y - mYIntercept) / mSlope)) <= PRECISION;
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
     * Get slope of orthogonal line.
     *
     * @return {@link Float} containing the slope of a line that runs perpendicular to this line.
     */
    @Nullable
    public Float getOrthogonalLineSlope() {
        return mOrthogonalLineSlope;
    }

    @Nullable
    public PointF getPointAtDistance(PointF src, float distance) {
        if (src == null || !containsPoint(src)) return null;
        PointF unitVector = getUnitVector();
        return new PointF(src.x + distance * unitVector.x, src.y + distance * unitVector.y);
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

    private void calculateOrthogonalLineSlope() {
        if (mType == Type.HORIZONTAL) return;
        if (mType == Type.VERTICAL) {
            mOrthogonalLineSlope = 0f;
        } else {
            mOrthogonalLineSlope = -1 * (float)Math.pow(mSlope, -1);
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
            Line line;
            if (mType == Type.VERTICAL) {
                line = new Line(null, null, mXIntercept, Type.VERTICAL);
            } else if (mType == Type.HORIZONTAL ||
                    mSlope == 0) {
                line = new Line(0f, mYIntercept, null, Type.HORIZONTAL);
            } else {
                if (mYIntercept == null) mYIntercept = 0f;
                line = new Line(mSlope, mYIntercept, mXIntercept, Type.DIAGONAL);
            }

            line.calculateOrthogonalLineSlope();
            return line;
        }
    }
}
