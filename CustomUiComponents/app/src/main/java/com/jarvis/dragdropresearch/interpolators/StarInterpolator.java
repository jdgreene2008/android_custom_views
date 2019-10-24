package com.jarvis.dragdropresearch.interpolators;

import android.graphics.PointF;
import android.util.Log;

import com.jarvis.dragdropresearch.math.Line;
import com.jarvis.dragdropresearch.math.LineUtils;

import androidx.annotation.VisibleForTesting;

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
    private TriangleInterpolator mBottomRightTriangleInterpolator;
    private TriangleInterpolator mBottomLeftTriangleInterpolator;

    // Key Points
    private PointF mBottomRightLineMidpoint;
    private PointF mBottomLeftLineMidpoint;
    private PointF mCenterPolygonPeakPoint;
    private PointF mIntersectionLeftSideBottomLeftSide;
    private PointF mIntersectionRightSideBottomRightSide;

    // Points where the bottom left and right triangles intersect the X axis.
    private PointF mBottomRightLineBisectorXAxisIntercept;
    private PointF mBottomLeftLineBisectorXAxisIntercept;

    private DrawingDescriptor mDrawingDescriptor;

    private StarInterpolator(int maxValue, float width, float height) {
        super(maxValue);
        mWidth = width;
        mHeight = height;
    }

    @Override
    public void updateValue(int value) {
        super.updateValue(value);
        mTopTriangleInterpolator.updateValue(value);
        mLeftTriangleInterpolator.updateValue(value);
        mBottomLeftTriangleInterpolator.updateValue(value);
        mRightTriangleInterpolator.updateValue(value);
        mBottomRightTriangleInterpolator.updateValue(value);
    }

    @VisibleForTesting
    public Line getBottomRightBisector() {
        return mBottomRightBisector;
    }

    @VisibleForTesting
    public Line getBottomLeftBisector() {
        return mBottomLeftBisector;
    }

    @VisibleForTesting
    public PointF getBottomRightLineMidpoint() {
        return mBottomRightLineMidpoint;
    }

    @VisibleForTesting
    public PointF getBottomLeftLineMidpoint() {
        return mBottomLeftLineMidpoint;
    }

    @VisibleForTesting
    public TriangleInterpolator getTopTriangleInterpolator() {
        return mTopTriangleInterpolator;
    }

    @VisibleForTesting
    public TriangleInterpolator getRightTriangleInterpolator() {
        return mRightTriangleInterpolator;
    }

    @VisibleForTesting
    public TriangleInterpolator getLeftTriangleInterpolator() {
        return mLeftTriangleInterpolator;
    }

    @VisibleForTesting
    public TriangleInterpolator getBottomRightTriangleInterpolator() {
        return mBottomRightTriangleInterpolator;
    }

    @VisibleForTesting
    public TriangleInterpolator getBottomLeftTriangleInterpolator() {
        return mBottomLeftTriangleInterpolator;
    }

    @VisibleForTesting
    public PointF getBottomRightLineBisectorXAxisIntercept() {
        return mBottomRightLineBisectorXAxisIntercept;
    }

    @VisibleForTesting
    public PointF getBottomLeftLineBisectorXAxisIntercept() {
        return mBottomLeftLineBisectorXAxisIntercept;
    }

    private void constructStarMetrics() {
        float ratioWidthToHeight = mWidth / mHeight;
        mDrawingDescriptor = new DrawingDescriptor();

        PointF center = new PointF(mWidth / 2f, mHeight / 2f);

        // Dimensions of the top,left and right sides of the square of the inner pentagon.
        float centerPolygonHeight = mHeight / 4f;
        float centerPolygonWidth = centerPolygonHeight * ratioWidthToHeight;

        float topLineYIntercept = center.y + centerPolygonHeight / 2f;
        float leftLineXIntercept = center.x - centerPolygonWidth / 2f;
        float rightLineXIntercept = center.x + centerPolygonWidth / 2f;

        float centerPolygonPeakHeight = centerPolygonHeight + centerPolygonHeight / 4f;

        createCenterPolygonLines(center, centerPolygonHeight, centerPolygonPeakHeight,
                topLineYIntercept, leftLineXIntercept, rightLineXIntercept);

        createBottomLineBisectors();

        createTriangleMetrics(mCenterPolygonPeakPoint, centerPolygonHeight,
                mIntersectionRightSideBottomRightSide, mIntersectionLeftSideBottomLeftSide);
    }

    private void createCenterPolygonLines(PointF center, float centerPolygonHeight,
            float centerPolygonPeakHeight, float topLineYIntercept, float leftLineXIntercept,
            float rightLineXIntercept) {
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
        mCenterPolygonPeakPoint =
                new PointF(center.x, mTopLine.getYIntercept() - centerPolygonPeakHeight);

        // Build bottom right line;
        mIntersectionRightSideBottomRightSide = new PointF(mRightSideLine.getXIntercept(),
                mTopLine.getYIntercept() - centerPolygonHeight);
        mBottomRightLine = LineUtils.createLineFromTwoPoints(mCenterPolygonPeakPoint,
                mIntersectionRightSideBottomRightSide);
        if (mBottomRightLine != null) {
            mBottomRightLineMidpoint = mBottomRightLine
                    .getMidpoint(mCenterPolygonPeakPoint, mIntersectionRightSideBottomRightSide);
        } else {
            Log.d(TAG, "Error constructing star. Bottom right line is null");
            return;
        }

        // Build bottom left line;
        mIntersectionLeftSideBottomLeftSide = new PointF(mLeftSideLine.getXIntercept(),
                mTopLine.getYIntercept() - centerPolygonHeight);
        mBottomLeftLine = LineUtils.createLineFromTwoPoints(mCenterPolygonPeakPoint,
                mIntersectionLeftSideBottomLeftSide);
        if (mBottomLeftLine != null) {
            mBottomLeftLineMidpoint = mBottomLeftLine
                    .getMidpoint(mCenterPolygonPeakPoint, mIntersectionLeftSideBottomLeftSide);
        } else {
            Log.d(TAG, "Error constructing star. Bottom left line is null");
        }
    }

    private void createBottomLineBisectors() {
        if (mBottomLeftLine.getSlope() == null || mBottomRightLine.getSlope() == null) {
            Log.d(TAG, "Error constructing star. Bottom lines slope is null.");
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
    }

    private void createTriangleMetrics(PointF centerPolygonPeakPoint, float centerPolygonHeight,
            PointF intersectionRightSideBottomRightSide,
            PointF intersectionLeftSideBottomLeftSide) {

        //1 . Top triangle
        float topTriangleBase = mRightSideLine.getXIntercept() - mLeftSideLine.getXIntercept();
        float topTriangleAltitude = mHeight - mTopLine.getYIntercept();

        mTopTriangleInterpolator =
                new TriangleInterpolator(getMaxValue(), topTriangleAltitude, topTriangleBase);
        mDrawingDescriptor.setTopTrianglePeak(new PointF(centerPolygonPeakPoint.x,
                mTopLine.getYIntercept() + topTriangleAltitude));

        //2. Left and right triangles
        float leftTriangleBase = centerPolygonHeight;
        float rightTriangleBase = leftTriangleBase;
        float leftTriangleAltitude = mLeftSideLine.getXIntercept();
        float rightTriangleAltitude = leftTriangleAltitude;

        mLeftTriangleInterpolator =
                new TriangleInterpolator(getMaxValue(), leftTriangleAltitude, leftTriangleBase);
        mRightTriangleInterpolator =
                new TriangleInterpolator(getMaxValue(), rightTriangleAltitude, rightTriangleBase);
        mDrawingDescriptor
                .setLeftTrianglePeak(
                        new PointF(0, mTopLine.getYIntercept() - leftTriangleBase / 2));
        mDrawingDescriptor
                .setRightTrianglePeak(
                        new PointF(mRightSideLine.getXIntercept() + rightTriangleAltitude,
                                mTopLine.getYIntercept() - rightTriangleBase / 2));

        //3. Bottom triangles.
        float bottomRightTriangleBase = LineUtils.getDistanceBetweenPoints(centerPolygonPeakPoint,
                intersectionRightSideBottomRightSide);
        float bottomLeftTriangleBase = LineUtils.getDistanceBetweenPoints(centerPolygonPeakPoint,
                intersectionLeftSideBottomLeftSide);
        float bottomRightTriangleAltitude = LineUtils
                .getDistanceBetweenPoints(mBottomRightLineMidpoint,
                        mBottomRightLineBisectorXAxisIntercept);
        float bottomLeftTriangleAltitude = LineUtils
                .getDistanceBetweenPoints(mBottomLeftLineMidpoint,
                        mBottomLeftLineBisectorXAxisIntercept);
        mDrawingDescriptor.setBottomLeftTrianglePeak(mBottomLeftLineBisectorXAxisIntercept);
        mDrawingDescriptor.setBottomRightTrianglePeak(mBottomRightLineBisectorXAxisIntercept);

        mBottomRightTriangleInterpolator =
                new TriangleInterpolator(getMaxValue(), bottomRightTriangleAltitude,
                        bottomRightTriangleBase);
        mBottomLeftTriangleInterpolator =
                new TriangleInterpolator(getMaxValue(), bottomLeftTriangleAltitude,
                        bottomLeftTriangleBase);

        mDrawingDescriptor
                .setBottomLeftLineBisectorXAxisIntercept(mBottomLeftLineBisectorXAxisIntercept);
        mDrawingDescriptor.setBottomLeftLineMidpoint(mBottomLeftLineMidpoint);
        mDrawingDescriptor
                .setBottomRightLineBisectorXAxisIntercept(mBottomRightLineBisectorXAxisIntercept);
        mDrawingDescriptor.setBottomRightLineMidpoint(mBottomRightLineMidpoint);
    }

    /**
     * @return {@link DrawingDescriptor} containing the coordinates for drawing the star triangles.
     */
    public DrawingDescriptor getDrawingDescriptor() {
        calculateTopTriangleDrawing();
        calculateRightTriangleDrawing();
        calculateLeftTriangleDrawing();
        calculateBottomLeftTriangleDrawing();
        calculateBottomRightTriangleDrawing();
        return mDrawingDescriptor;
    }

    private void calculateTopTriangleDrawing() {
        float[] interpolatedValues = mTopTriangleInterpolator.getInterpolatedValues();
        PointF peakVertex = mDrawingDescriptor.getTopTrianglePeak();

        float halfBase = interpolatedValues[TriangleInterpolator.INTERPOLATION_VALUES_BASE] / 2;
        float altitude = interpolatedValues[TriangleInterpolator.INTERPOLATION_VALUES_ALTITUDE];
        mDrawingDescriptor.setTopTriangleLeftVertex(
                new PointF(peakVertex.x - halfBase, peakVertex.y - altitude));
        mDrawingDescriptor.setTopTriangleRightVertex(
                new PointF(peakVertex.x + halfBase, peakVertex.y - altitude));
    }

    private void calculateLeftTriangleDrawing() {
        float[] interpolatedValues = mLeftTriangleInterpolator.getInterpolatedValues();
        PointF peakVertex = mDrawingDescriptor.getLeftTrianglePeak();

        float halfBase = interpolatedValues[TriangleInterpolator.INTERPOLATION_VALUES_BASE] / 2;
        float altitude = interpolatedValues[TriangleInterpolator.INTERPOLATION_VALUES_ALTITUDE];
        mDrawingDescriptor.setLeftTriangleTopVertex(
                new PointF(altitude, peakVertex.y + halfBase));
        mDrawingDescriptor.setLeftTriangleBottomVertex(
                new PointF(altitude, peakVertex.y - halfBase));
    }

    private void calculateRightTriangleDrawing() {
        float[] interpolatedValues = mRightTriangleInterpolator.getInterpolatedValues();
        PointF peakVertex = mDrawingDescriptor.getRightTrianglePeak();

        float halfBase = interpolatedValues[TriangleInterpolator.INTERPOLATION_VALUES_BASE] / 2;
        float altitude = interpolatedValues[TriangleInterpolator.INTERPOLATION_VALUES_ALTITUDE];
        mDrawingDescriptor.setRightTriangleTopVertex(
                new PointF(peakVertex.x - altitude, peakVertex.y + halfBase));
        mDrawingDescriptor.setRightTriangleBottomVertex(
                new PointF(peakVertex.x - altitude, peakVertex.y - halfBase));
    }

    private void calculateBottomLeftTriangleDrawing() {
        float[] interpolatedValues = mBottomLeftTriangleInterpolator.getInterpolatedValues();
        PointF peakVertex = mDrawingDescriptor.getBottomLeftTrianglePeak();

        float halfBase = interpolatedValues[TriangleInterpolator.INTERPOLATION_VALUES_BASE] / 2;
        float altitude = interpolatedValues[TriangleInterpolator.INTERPOLATION_VALUES_ALTITUDE];

        // Calculate the point distance of altitude away from the peak.
        Line bottomLeftLineXAxisBisector =
                LineUtils.createLineFromSlopeAndPoint(mBottomLeftBisectorSlope, peakVertex);
        PointF pointOnBisector =
                bottomLeftLineXAxisBisector.getPointAtDistance(peakVertex, altitude);
        if (pointOnBisector == null) {
            Log.d(TAG, "calculateBottomLeftTriangleDrawing() : pointOnBisector is null");
            return;
        }

        Line baseLine =
                LineUtils.createLineFromSlopeAndPoint(mBottomLeftLine.getSlope(), pointOnBisector);
        mDrawingDescriptor.setBottomLeftTriangleUpperLeftVertex(
                baseLine.getPointAtDistance(pointOnBisector, -1f * halfBase));
        mDrawingDescriptor.setBottomLeftTriangleUpperRightVertex(
                baseLine.getPointAtDistance(pointOnBisector, halfBase));
    }

    private void calculateBottomRightTriangleDrawing() {
        float[] interpolatedValues = mBottomRightTriangleInterpolator.getInterpolatedValues();
        PointF peakVertex = mDrawingDescriptor.getBottomRightTrianglePeak();

        float halfBase = interpolatedValues[TriangleInterpolator.INTERPOLATION_VALUES_BASE] / 2;
        float altitude = interpolatedValues[TriangleInterpolator.INTERPOLATION_VALUES_ALTITUDE];

        // Calculate the point distance of altitude away from the peak.
        Line bottomRightLineXAxisBisector =
                LineUtils.createLineFromSlopeAndPoint(mBottomRightBisectorSlope, peakVertex);
        PointF pointOnBisector =
                bottomRightLineXAxisBisector.getPointAtDistance(peakVertex, -1f * altitude);
        if (pointOnBisector == null) {
            Log.d(TAG, "calculateBottomRightTriangleDrawing() : pointOnBisector is null");
            return;
        }

        Line baseLine =
                LineUtils.createLineFromSlopeAndPoint(mBottomRightLine.getSlope(), pointOnBisector);
        mDrawingDescriptor.setBottomRightTriangleUpperLeftVertex(
                baseLine.getPointAtDistance(pointOnBisector, -1f * halfBase));
        mDrawingDescriptor.setBottomRightTriangleUpperRightVertex(
                baseLine.getPointAtDistance(pointOnBisector, halfBase));
    }

    /**
     * Contains the metrics needed to draw this star in the coordinate plane.
     */
    public static class DrawingDescriptor {
        // Bottom Right Triangle
        private PointF mBottomRightTriangleUpperRightVertex;
        private PointF mBottomRightTriangleUpperLeftVertex;
        private PointF mBottomRightTrianglePeak;

        // Bottom Left Triangle
        private PointF mBottomLeftTriangleUpperLeftVertex;
        private PointF mBottomLeftTriangleUpperRightVertex;
        private PointF mBottomLeftTrianglePeak;

        // Top Triangle
        private PointF mTopTriangleLeftVertex;
        private PointF mTopTriangleRightVertex;
        private PointF mTopTrianglePeak;

        // Left Triangle
        private PointF mLeftTriangleTopVertex;
        private PointF mLeftTriangleBottomVertex;
        private PointF mLeftTrianglePeak;

        // Right Triangle
        private PointF mRightTriangleTopVertex;
        private PointF mRightTriangleBottomVertex;
        private PointF mRightTrianglePeak;

        private PointF mBottomLeftLineMidpoint;
        private PointF mBottomRightLineMidpoint;
        private PointF mBottomRightLineBisectorXAxisIntercept;
        private PointF mBottomLeftLineBisectorXAxisIntercept;
        private PointF mCenterPolygonPeakPoint;
        private PointF mIntersectionLeftSideBottomLeftSide;
        private PointF mIntersectionRightSideBottomRightSide;

        public PointF getBottomRightTriangleUpperRightVertex() {
            return mBottomRightTriangleUpperRightVertex;
        }

        public PointF getBottomRightTriangleUpperLeftVertex() {
            return mBottomRightTriangleUpperLeftVertex;
        }

        public PointF getBottomLeftTriangleUpperLeftVertex() {
            return mBottomLeftTriangleUpperLeftVertex;
        }

        public PointF getBottomLeftTriangleUpperRightVertex() {
            return mBottomLeftTriangleUpperRightVertex;
        }

        public PointF getBottomRightTrianglePeak() {
            return mBottomRightTrianglePeak;
        }

        public PointF getBottomLeftTrianglePeak() {
            return mBottomLeftTrianglePeak;
        }


        private void setBottomLeftLineMidpoint(PointF bottomLeftLineMidpoint) {
            mBottomLeftLineMidpoint = bottomLeftLineMidpoint;
        }

        private void setBottomRightLineMidpoint(PointF bottomRightLineMidpoint) {
            mBottomRightLineMidpoint = bottomRightLineMidpoint;
        }

        private void setBottomRightLineBisectorXAxisIntercept(
                PointF bottomRightLineBisectorXAxisIntercept) {
            mBottomRightLineBisectorXAxisIntercept = bottomRightLineBisectorXAxisIntercept;
        }

        private void setBottomLeftLineBisectorXAxisIntercept(
                PointF bottomLeftLineBisectorXAxisIntercept) {
            mBottomLeftLineBisectorXAxisIntercept = bottomLeftLineBisectorXAxisIntercept;
        }

        private void setBottomRightTriangleUpperRightVertex(
                PointF bottomRightTriangleUpperRightVertex) {
            mBottomRightTriangleUpperRightVertex = bottomRightTriangleUpperRightVertex;
        }

        private void setBottomRightTriangleUpperLeftVertex(
                PointF bottomRightTriangleUpperLeftVertex) {
            mBottomRightTriangleUpperLeftVertex = bottomRightTriangleUpperLeftVertex;
        }

        private void setBottomLeftTriangleUpperLeftVertex(
                PointF bottomLeftTriangleUpperLeftVertex) {
            mBottomLeftTriangleUpperLeftVertex = bottomLeftTriangleUpperLeftVertex;
        }

        private void setBottomLeftTriangleUpperRightVertex(
                PointF bottomLeftTriangleUpperRightVertex) {
            mBottomLeftTriangleUpperRightVertex = bottomLeftTriangleUpperRightVertex;
        }

        private void setBottomRightTrianglePeak(
                PointF bottomRightTrianglePeak) {
            mBottomRightTrianglePeak = bottomRightTrianglePeak;
        }

        private void setBottomLeftTrianglePeak(PointF bottomLeftTrianglePeak) {
            mBottomLeftTrianglePeak = bottomLeftTrianglePeak;
        }

        public PointF getTopTriangleLeftVertex() {
            return mTopTriangleLeftVertex;
        }

        private void setTopTriangleLeftVertex(PointF topTriangleLeftVertex) {
            mTopTriangleLeftVertex = topTriangleLeftVertex;
        }

        public PointF getTopTriangleRightVertex() {
            return mTopTriangleRightVertex;
        }

        private void setTopTriangleRightVertex(PointF topTriangleRightVertex) {
            mTopTriangleRightVertex = topTriangleRightVertex;
        }

        public PointF getTopTrianglePeak() {
            return mTopTrianglePeak;
        }

        private void setTopTrianglePeak(PointF topTrianglePeak) {
            mTopTrianglePeak = topTrianglePeak;
        }

        public PointF getLeftTriangleTopVertex() {
            return mLeftTriangleTopVertex;
        }

        private void setLeftTriangleTopVertex(PointF leftTriangleTopVertex) {
            mLeftTriangleTopVertex = leftTriangleTopVertex;
        }

        public PointF getLeftTriangleBottomVertex() {
            return mLeftTriangleBottomVertex;
        }

        private void setLeftTriangleBottomVertex(PointF leftTriangleBottomVertex) {
            mLeftTriangleBottomVertex = leftTriangleBottomVertex;
        }

        public PointF getLeftTrianglePeak() {
            return mLeftTrianglePeak;
        }

        private void setLeftTrianglePeak(PointF leftTrianglePeak) {
            mLeftTrianglePeak = leftTrianglePeak;
        }

        public PointF getRightTriangleTopVertex() {
            return mRightTriangleTopVertex;
        }

        private void setRightTriangleTopVertex(PointF rightTriangleTopVertex) {
            mRightTriangleTopVertex = rightTriangleTopVertex;
        }

        public PointF getRightTriangleBottomVertex() {
            return mRightTriangleBottomVertex;
        }

        private void setRightTriangleBottomVertex(PointF rightTriangleBottomVertex) {
            mRightTriangleBottomVertex = rightTriangleBottomVertex;
        }

        public PointF getRightTrianglePeak() {
            return mRightTrianglePeak;
        }

        private void setRightTrianglePeak(PointF rightTrianglePeak) {
            mRightTrianglePeak = rightTrianglePeak;
        }
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
