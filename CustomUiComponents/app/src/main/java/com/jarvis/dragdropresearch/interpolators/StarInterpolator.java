package com.jarvis.dragdropresearch.interpolators;

import android.graphics.PointF;
import android.util.Log;

import com.jarvis.dragdropresearch.math.Line;
import com.jarvis.dragdropresearch.math.LineUtils;

public class StarInterpolator extends Interpolator {
    private static final String TAG = StarInterpolator.class.getName();

    private float mWidth;
    private float mHeight;

    // Sides
    private Line mTopLine;
    private Line mLeftSideLine;
    private Line mRightSideLine;
    private Line mBottomLeftLine;
    private Line mBottomRightLine;

    // Lines that bisect the bottom left and right sides. These lines determine where
    // the bottom left and right triangles intersect the x-axis.
    private Line mBottomRightBisector;
    private Line mBottomLeftBisector;
    private Float mBottomRightBisectorSlope;
    private Float mBottomLeftBisectorSlope;

    // Interpolators
    private TriangleInterpolator mTopTriangleInterpolator;
    private TriangleInterpolator mRightTriangleInterpolator;
    private TriangleInterpolator mLeftTriangleInterpolator;
    private TriangleInterpolator mBottomTrianglesInterpolator;

    // Key Points
    private PointF mBottomRightLineMidpoint;
    private PointF mBottomLeftLineMidpoint;

    // Points where the bottom left and right triangles intersect the X axis.
    private PointF mBottomRightLineBisectorXAxisIntercept;
    private PointF mBottomLeftLineBisectorXAxisIntercept;

    private StarInterpolator(int maxValue, float width, float height) {
        super(maxValue);
        mWidth = width;
        mHeight = height;
    }

    private void constructStarMetrics() {
        float ratioWidthToHeight = mWidth / mHeight;

        PointF center = new PointF(mWidth / 2, mHeight / 2);

        // Dimensions of the top,left and right sides of the square of the inner pentagon.
        float centerPolygonHeight = mHeight / 4;
        float centerPolygonWidth = centerPolygonHeight * ratioWidthToHeight;

        float topLineYIntercept = center.y + centerPolygonHeight / 2;
        float leftLineXIntercept = center.x - centerPolygonWidth / 2;
        float rightLineXIntercept = center.x + centerPolygonWidth / 2;

        // Build top line
        Line.Builder builder = new Line.Builder(Line.Type.HORIZONTAL);
        builder.setYIntercept(topLineYIntercept);
        mTopLine = builder.build();

        // Build left line
        builder = new Line.Builder(Line.Type.VERTICAL);
        builder.setXIntercept(leftLineXIntercept);
        mLeftSideLine = builder.build();

        // Build right side line
        builder = new Line.Builder(Line.Type.VERTICAL);
        builder.setXIntercept(rightLineXIntercept);
        mRightSideLine = builder.build();

        // Build Bottom Lines
        // Total height of the inner pentagon
        float centerPolygonPeakHeight = centerPolygonHeight + centerPolygonHeight / 4;
        PointF centerPolygonPeakPoint =
                new PointF(center.x, mTopLine.getYIntercept() - centerPolygonPeakHeight);

        // Build bottom right line;
        PointF intersectionRightSideBottomRightSide = new PointF(mRightSideLine.getXIntercept(),
                mTopLine.getYIntercept() - centerPolygonHeight);
        mBottomRightLine = LineUtils.createLineFromTwoPoints(centerPolygonPeakPoint,
                intersectionRightSideBottomRightSide);
        if (mBottomRightLine != null) {
            mBottomRightLineMidpoint = mBottomRightLine
                    .getMidpoint(centerPolygonPeakPoint, intersectionRightSideBottomRightSide);
        } else {
            Log.d(TAG, "Error constructing star. Bottom right line is null");
            return;
        }

        // Build bottom left line;
        PointF intersectionLeftSideBottomLeftSide = new PointF(mLeftSideLine.getXIntercept(),
                mTopLine.getYIntercept() - centerPolygonHeight);
        mBottomLeftLine = LineUtils.createLineFromTwoPoints(centerPolygonPeakPoint,
                intersectionLeftSideBottomLeftSide);
        if (mBottomLeftLine != null) {
            mBottomLeftLineMidpoint = mBottomLeftLine
                    .getMidpoint(centerPolygonPeakPoint, intersectionLeftSideBottomLeftSide);
        } else {
            Log.d(TAG, "Error constructing star. Bottom left line is null");
            return;
        }

        // Create bottom and left side line bisectors.
        if (mBottomLeftLine.getSlope() == null || mBottomRightLine.getSlope() == null) {
            Log.d(TAG, "Error constructing star. Bottom lines slope is null.");
            return;
        } else {
            mBottomRightBisectorSlope = mBottomRightLine.getOrthogonalLineSlope();
            mBottomLeftBisectorSlope = mBottomLeftLine.getOrthogonalLineSlope();

            if (mBottomRightBisectorSlope == null) {
                Log.d(TAG, "Error constructing star. Bottom right line bisector slope is null.");
                return;
            } else if (mBottomLeftBisectorSlope == null) {
                Log.d(TAG, "Error constructing star. Bottom left line bisector is null.");
                return;
            }

            // Define line for bottom right bisector and left bisector
            mBottomRightBisector = LineUtils.createLineFromSlopeAndPoint(mBottomRightBisectorSlope,
                    mBottomRightLineMidpoint);
            mBottomLeftBisector = LineUtils
                    .createLineFromSlopeAndPoint(mBottomLeftBisectorSlope, mBottomLeftLineMidpoint);

            // Determine where bisectors intersect the x-axis
            Line xAxis = LineUtils.createLineFromSlopeAndPoint(0f, new PointF(0, 0));
            mBottomRightLineBisectorXAxisIntercept =
                    LineUtils.getPointOfIntersection(xAxis, mBottomRightBisector);
            mBottomLeftLineBisectorXAxisIntercept =
                    LineUtils.getPointOfIntersection(xAxis, mBottomLeftBisector);
        }

        // Determine triangle metrics.

        //1 . Top triangle
        float topTriangleBase = mRightSideLine.getXIntercept() - mLeftSideLine.getXIntercept();
        float topTriangleAltitude = mHeight - mTopLine.getYIntercept();
        mTopTriangleInterpolator =
                new TriangleInterpolator(getMaxValue(), topTriangleAltitude, topTriangleBase);

        //2. Left and right triangles
        float leftTriangleBase = mTopLine.getYIntercept() - centerPolygonHeight;
        float rightTriangleBase = leftTriangleBase;
        float leftTriangleAltitude = mWidth - mBottomRightLine.getXIntercept();
        float rightTriangleAltitude = leftTriangleAltitude;
        mLeftTriangleInterpolator =
                new TriangleInterpolator(getMaxValue(), leftTriangleAltitude, leftTriangleBase);
        mLeftTriangleInterpolator =
                new TriangleInterpolator(getMaxValue(), rightTriangleAltitude, rightTriangleBase);

        //3. Bottom sides.
        // TODO: Get dimensions for bottom left and right triangles.

    }

    public static class Builder {
        private int mMaxValue;
        private float mWidth;
        private float mHeight;

        public Builder(int maxValue) {
            mMaxValue = maxValue;
        }

        public Builder setWidth(float width) {
            mWidth = width;
            return this;
        }

        public Builder setHeight(float height) {
            mHeight = height;
            return this;
        }

        public StarInterpolator build() {
            StarInterpolator interpolator = new StarInterpolator(mMaxValue, mWidth, mHeight);
            interpolator.constructStarMetrics();
            return interpolator;
        }
    }
}
