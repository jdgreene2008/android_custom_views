package com.jarvis.dragdropresearch.math;

import android.graphics.PointF;
import android.util.Log;

import androidx.annotation.Nullable;

public class Line extends Equation {
    private static final String TAG = Line.class.getName();
    private static final String DESCRIPTION =
            "Represents the slope-intercept form of an equation for a line.";

    private float mSlope;
    private float mYIntercept;

    /**
     * Create an equation for a {@link Line} in y-intercept form with the given slope and y-intercept.
     *
     * @param slope
     * @param yIntercept
     */
    public Line(float slope, float yIntercept) {
        mSlope = slope;
        mYIntercept = yIntercept;
    }

    /**
     * Get the y-coordinate of the point on the line with the provided x-coordinate.
     *
     * @param x
     *
     * @return Y-coordinate.
     */
    public float getY(float x) {
        return mSlope * x + mYIntercept;
    }

    /**
     * Get the x-coordinate of the point on the line with the provided y-coordinate.
     *
     * @param y
     *
     * @return X-coordinate.
     */
    public float getX(float y) {
        return (y - mYIntercept) / mSlope;
    }

    public boolean containsPoint(PointF point) {
        return point.x == (point.y - mYIntercept) / mSlope;
    }

    /**
     * @return X-coordinate for point where the line crosses the X-axis. Y is 0 at this point.
     */
    public float getXIntercept() {
        return -1 * mYIntercept / mSlope;
    }

    public float getSlope() {
        return mSlope;
    }

    public float getYIntercept() {
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

        if (x == 0) {
            // If x is 0, then we know what the y-intercept is without calculating.
            return new Line(slope, y);
        } else {
            yIntercept = y - slope * x;
        }

        return new Line(slope, yIntercept);
    }

    @Nullable
    public static Line initLineWithTwoPoints(PointF pointA, PointF pointB) {
        if (pointA.x == pointB.x || pointB.y == pointA.y) {
            Log.e(TAG,
                    "Both points share a common X or common Y. Corresponding values should be distinct.");
            return null;
        }

        float slope = (float)(pointA.y - pointB.y) / (float)(pointA.x - pointB.x);
        float yIntercept;

        if (pointA.x == 0) {
            yIntercept = pointA.y;
        } else if (pointB.x == 0) {
            yIntercept = pointB.y;
        } else {
            yIntercept = pointB.y - slope * pointB.x;
        }

        return new Line(slope, yIntercept);
    }

    @Nullable
    public static PointF getPointOfIntersection(Line line1, Line line2) {
        if (line1 == null || line2 == null) {
            return null;
        } else if (line1.getSlope() == line2.getSlope()) {
            return null;
        } else {
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
}
