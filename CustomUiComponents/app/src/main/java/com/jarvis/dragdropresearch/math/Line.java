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
     * Return an equation for a {@link Line} when the slope is provided and one point is known.
     *
     * @param slope
     * @param point
     */
    public static Line initLineWithSlopeAndPoint(float slope, PointF point) {
        float x = point.x;
        float y = point.y;
        float yIntercept;

        if (slope == 0) {
            return new Line(slope, point.y, 0f, Type.HORIZONTAL);
        } else if (x == 0) {
            // If x is 0, then we know what the y-intercept is without calculating.

            return new Line(slope, y, null, Type.DIAGONAL);
        } else {
            yIntercept = y - slope * x;
        }

        return new Line(slope, yIntercept, null, Type.DIAGONAL);
    }

    @Nullable
    public static Line initLineWithTwoPoints(PointF pointA, PointF pointB) {
        if (pointA.x == pointB.x && pointA.y == pointB.y) {
            return null;
        } else if (pointA.x == pointB.x && pointA.y != pointB.y) {
            // Vertical line.
            return new Line(null, 0f, pointA.x, Type.VERTICAL);
        } else if (pointA.x != pointB.x && pointA.y == pointB.y) {
            // Horizontal line.
            return new Line(0f, pointA.y, 0f, Type.HORIZONTAL);
        }

        float slope = (pointA.y - pointB.y) / (float)(pointA.x - pointB.x);
        float yIntercept;

        if (pointA.x == 0) {
            yIntercept = pointA.y;
        } else if (pointB.x == 0) {
            yIntercept = pointB.y;
        } else {
            yIntercept = pointB.y - slope * pointB.x;
        }

        return new Line(slope, yIntercept, null, Type.DIAGONAL);
    }

    @Nullable
    public static PointF getPointOfIntersection(Line line1, Line line2) {
        if (line1 == null || line2 == null) {
            return null;
        } else if (line1.getType() != Type.DIAGONAL &&
                line1.getType() == line2.getType()) {
            // Case where the two lines are parallel and non-diagonal

            return null;
        } else if (line1.getSlope() != null
                && line2.getSlope() != null
                && line1.getSlope().equals(line2.getSlope())) {
            // Case where the two lines are parallel and diagonal.

            return null;
        } else if (line1.getType() == Type.VERTICAL
                && line2.getType() == Type.HORIZONTAL) {
            // X-value for point of intersection is where line 1 crosses the X-axis.
            // Y-value is where line 2 crosses the Y axis.

            return new PointF(line1.getXIntercept(), line2.getYIntercept());
        } else if (line2.getType() == Type.VERTICAL
                && line1.getType() == Type.HORIZONTAL) {
            // X-value for point of intersection is where line 2 crosses the X-axis.
            // Y-value is where line 1 crosses the Y axis.

            return new PointF(line2.getXIntercept(), line1.getYIntercept());
        } else if (line1.getType() == Type.HORIZONTAL) {
            // Case where line 1 is horizontal and line 2 is diagonal.

            float yInterceptLine1 = line1.getYIntercept();
            Float xLine2 = line2.getX(yInterceptLine1);
            if (xLine2 != null) {
                return new PointF(xLine2, yInterceptLine1);
            } else {
                return null;
            }
        } else if (line2.getType() == Type.HORIZONTAL) {
            // Case where line 2 is horizontal and line 1 is diagonal.

            float yInterceptLine2 = line2.getYIntercept();
            Float xLine1 = line1.getX(yInterceptLine2);
            if (xLine1 != null) {
                return new PointF(xLine1, yInterceptLine2);
            } else {
                return null;
            }
        } else if (line1.getType() == Type.VERTICAL) {
            // Case where line 1 is horizontal and line 2 is diagonal.

            float xInterceptLine1 = line1.getXIntercept();
            Float yLine2 = line2.getY(xInterceptLine1);
            if (yLine2 != null) {
                return new PointF(xInterceptLine1, yLine2);
            } else {
                return null;
            }
        } else if (line2.getType() == Type.VERTICAL) {
            // Case where line 2 is vertical and line 1 is diagonal.

            float xInterceptLine2 = line2.getXIntercept();
            Float yLine1 = line1.getY(xInterceptLine2);
            if (yLine1 != null) {
                return new PointF(xInterceptLine2, yLine1);
            } else {
                return null;
            }
        } else {
            if (line2.getSlope() == null || line1.getSlope() == null) return null;

            float intersectionX = (line1.getYIntercept() - line2.getYIntercept()) /
                    (-1 * line1.getSlope() + line2.getSlope());
            float intersectionY = line1.getYIntercept() + line1.getSlope() *
                    ((line1.getYIntercept() - line2.getYIntercept()) /
                            (-1 * line1.getSlope() + line2.getSlope()));
            return new PointF(intersectionX, intersectionY);
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

        public void setSlope(Float slope) {
            mSlope = slope;
        }

        public void setYIntercept(Float YIntercept) {
            mYIntercept = YIntercept;
        }

        public void setXIntercept(Float XIntercept) {
            mXIntercept = XIntercept;
        }

        public Line build() {
            if (mType == Type.VERTICAL) {
                return new Line(null, null, mXIntercept, Type.VERTICAL);
            } else if (mType == Type.HORIZONTAL ||
                    mSlope == 0) {
                return new Line(null, mYIntercept, null, Type.HORIZONTAL);
            } else {
                if (mYIntercept == null) mYIntercept = 0f;
                return new Line(mSlope, mYIntercept, mXIntercept, Type.DIAGONAL);
            }
        }
    }
}
