package com.jarvis.dragdropresearch.interpolators;

import android.graphics.PointF;

import com.jarvis.dragdropresearch.math.Line;

import org.junit.Before;
import org.junit.Test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

public class StarInterpolatorTest {
    private static final int INTERPOLATION_MAX_VALUE = 800;

    private StarInterpolator mInterpolator;

    @Before
    public void setUp() {
        StarInterpolator.Builder builder = new StarInterpolator.Builder(INTERPOLATION_MAX_VALUE);
        builder.setWidth(500)
                .setHeight(500);
        mInterpolator = builder.build();
    }

    @Test
    public void test_leftAndRightTriangleInterpolatorsEquality() {

        assertNotNull(mInterpolator);

        TriangleInterpolator leftTriangleInterpolator = mInterpolator.getLeftTriangleInterpolator();
        TriangleInterpolator rightTriangleInterpolator =
                mInterpolator.getRightTriangleInterpolator();

        assertNotNull(leftTriangleInterpolator);
        assertNotNull(rightTriangleInterpolator);

        assertEquals(leftTriangleInterpolator.getAltitude(),
                rightTriangleInterpolator.getAltitude(), 0);
        assertEquals(leftTriangleInterpolator.getBase(),
                rightTriangleInterpolator.getBase(), 0);
    }

    @Test
    public void test_bottomLeftAndBottomRightTriangleInterpolatorsEquality() {
        assertNotNull(mInterpolator);

        TriangleInterpolator bottomLeftTriangleInterpolator =
                mInterpolator.getBottomLeftTriangleInterpolator();
        TriangleInterpolator bottomRightTriangleInterpolator =
                mInterpolator.getBottomRightTriangleInterpolator();

        assertNotNull(bottomLeftTriangleInterpolator);
        assertNotNull(bottomRightTriangleInterpolator);

        assertEquals(bottomLeftTriangleInterpolator.getAltitude(),
                bottomRightTriangleInterpolator.getAltitude(), 0);
        assertEquals(bottomLeftTriangleInterpolator.getBase(),
                bottomRightTriangleInterpolator.getBase(), 0);
    }

    @Test
    public void test_bottomLeftAndBottomRightLineMidpoints() {
        assertNotNull(mInterpolator);

        PointF bottomLeftLineMidpoint = mInterpolator.getBottomLeftLineMidpoint();
        PointF bottomRightLineMidpoint = mInterpolator.getBottomRightLineMidpoint();

        assertNotNull(bottomLeftLineMidpoint);
        assertNotNull(bottomRightLineMidpoint);

        assertEquals(bottomLeftLineMidpoint.y, bottomRightLineMidpoint.y, 0);
    }

    @Test
    public void test_bottomLeftAndBottomRightLineBisectors() {
        assertNotNull(mInterpolator);

        Line bottomLeftBisector = mInterpolator.getBottomLeftBisector();
        Line bottomRightBisector = mInterpolator.getBottomRightBisector();

        assertNotNull(bottomLeftBisector);
        assertNotNull(bottomRightBisector);

        PointF bottomLeftLineBisectorXAxisIntercept =
                mInterpolator.getBottomLeftLineBisectorXAxisIntercept();
        PointF bottomRightLineBisectorXAxisIntercept =
                mInterpolator.getBottomRightLineBisectorXAxisIntercept();

        assertNotNull(bottomLeftLineBisectorXAxisIntercept);
        assertNotNull(bottomRightLineBisectorXAxisIntercept);
    }

    @Test
    public void test_interpolationUpdatesDrawingDescriptor() {
        assertNotNull(mInterpolator);
        mInterpolator.updateValue(INTERPOLATION_MAX_VALUE / 2);

        TriangleInterpolator topTriangleInterpolator = mInterpolator.getTopTriangleInterpolator();
        TriangleInterpolator rightTriangleInterpolator =
                mInterpolator.getRightTriangleInterpolator();
        TriangleInterpolator leftTriangleInterpolator = mInterpolator.getLeftTriangleInterpolator();
        TriangleInterpolator bottomRightTriangleInterpolator =
                mInterpolator.getBottomRightTriangleInterpolator();
        TriangleInterpolator bottomLeftTriangleInterpolator = mInterpolator.getBottomLeftTriangleInterpolator();

        assertNotNull(topTriangleInterpolator);
        assertNotNull(rightTriangleInterpolator);
        assertNotNull(leftTriangleInterpolator);
        assertNotNull(bottomLeftTriangleInterpolator);
        assertNotNull(bottomRightTriangleInterpolator);

        assertEquals(topTriangleInterpolator.getValue(),INTERPOLATION_MAX_VALUE/2);
        assertEquals(rightTriangleInterpolator.getValue(), INTERPOLATION_MAX_VALUE / 2);
        assertEquals(leftTriangleInterpolator.getValue(), INTERPOLATION_MAX_VALUE / 2);
        assertEquals(bottomRightTriangleInterpolator.getValue(), INTERPOLATION_MAX_VALUE / 2);
        assertEquals(bottomLeftTriangleInterpolator.getValue(), INTERPOLATION_MAX_VALUE / 2);

        assertNotNull(mInterpolator.getDrawingDescriptor());
    }
}
