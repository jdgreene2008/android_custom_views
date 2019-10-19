package com.jarvis.dragdropresearch.math;

import android.graphics.PointF;

import org.junit.Test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

public class LineTest {

    @Test
    public void test_PositiveSlope() {

        // Line y = x
        Line line = Line.getLineWithTwoPoints(new PointF(1, 1), new PointF(2, 2));

        assertNotNull(line);

        assertEquals(0, line.getXIntercept(), 0.0);
        assertEquals(0, line.getYIntercept(), 0.0);
        assertEquals(1, line.getSlope(), 0.0);

        assertTrue(line.containsPoint(new PointF(1, 1)));
        assertTrue(line.containsPoint(new PointF(2, 2)));
        assertTrue(line.containsPoint(new PointF(5, 5)));

        assertNotNull(line.getMidpoint(new PointF(1, 1), new PointF(5, 5)));

        // Line y = 2x - 6;
        line = Line.getLineWithTwoPoints(new PointF(3, 0), new PointF(10, 14));

        assertNotNull(line);

        assertEquals(-6, line.getYIntercept(), 0);
        assertEquals(3, line.getXIntercept(), 0);
        assertEquals(2, line.getSlope(), 0.0);

        assertTrue(line.containsPoint(new PointF(3, 0)));
        assertTrue(line.containsPoint(new PointF(10, 14)));
        assertTrue(line.containsPoint(new PointF(0, -6)));

        assertNotNull(line.getMidpoint(new PointF(0, -6), new PointF(10, 14)));
    }

    @Test
    public void test_NegativeSlope() {
        Line line = Line.getLineWithTwoPoints(new PointF(-1, 1), new PointF(-2, 2));

        assertNotNull(line);
        assertEquals(0, line.getXIntercept(), 0.0);
        assertEquals(0, line.getYIntercept(), 0.0);
        assertEquals(-1, line.getSlope(), 0.0);
        assertTrue(line.containsPoint(new PointF(-1, 1)));
        assertTrue(line.containsPoint(new PointF(-2, 2)));
        assertTrue(line.containsPoint(new PointF(1, -1)));
        assertTrue(line.containsPoint(new PointF(2, -2)));
    }
}
