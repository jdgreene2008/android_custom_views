package com.jarvis.dragdropresearch.math;

import android.graphics.PointF;

import org.junit.Test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;

public class LineTest {

    @Test
    public void test_initLineWithTwoPoints_samePoints_returnsInvalidLine() {
        PointF a = new PointF(1, 1);
        Line line = LineUtils.initLineWithTwoPoints(a, a);
        assertNull(line);
    }

    @Test
    public void test_initLineWithTwoPoints_positiveSlope() {
        // Line y = x
        Line line = LineUtils.initLineWithTwoPoints(new PointF(1, 1), new PointF(2, 2));

        assertNotNull(line);
        assertEquals(Line.Type.DIAGONAL, line.getType());

        assertEquals(0, line.getXIntercept(), 0.0);
        assertEquals(0, line.getYIntercept(), 0.0);
        assertNotNull(line.getSlope());
        assertEquals(1, line.getSlope(), 0.0);

        assertTrue(line.containsPoint(new PointF(1, 1)));
        assertTrue(line.containsPoint(new PointF(2, 2)));
        assertTrue(line.containsPoint(new PointF(5, 5)));

        assertNotNull(line.getMidpoint(new PointF(1, 1), new PointF(5, 5)));

        // Line y = 2x - 6;
        line = LineUtils.initLineWithTwoPoints(new PointF(3, 0), new PointF(10, 14));

        assertNotNull(line);
        assertEquals(Line.Type.DIAGONAL, line.getType());

        assertEquals(-6, line.getYIntercept(), 0);
        assertEquals(3, line.getXIntercept(), 0);
        assertNotNull(line.getSlope());
        assertEquals(2, line.getSlope(), 0.0);

        assertTrue(line.containsPoint(new PointF(3, 0)));
        assertTrue(line.containsPoint(new PointF(10, 14)));
        assertTrue(line.containsPoint(new PointF(0, -6)));

        assertNotNull(line.getMidpoint(new PointF(0, -6), new PointF(10, 14)));
    }

    @Test
    public void test_initLineWithTwoPoints_negativeSlope() {
        Line line = LineUtils.initLineWithSlopeAndPoint(-1f, new PointF(-2, 2));

        // Line y = -x;
        assertNotNull(line);
        assertEquals(Line.Type.DIAGONAL, line.getType());

        assertEquals(0, line.getXIntercept(), 0.0);
        assertEquals(0, line.getYIntercept(), 0.0);
        assertNotNull(line.getSlope());
        assertEquals(-1, line.getSlope(), 0.0);

        assertTrue(line.containsPoint(new PointF(-1, 1)));
        assertTrue(line.containsPoint(new PointF(-2, 2)));
        assertTrue(line.containsPoint(new PointF(1, -1)));
        assertTrue(line.containsPoint(new PointF(2, -2)));

        PointF midpoint = line.getMidpoint(new PointF(-1, 1), new PointF(1, -1));
        assertNotNull(midpoint);
        assertEquals(0, midpoint.x, 0);
        assertEquals(0, midpoint.y, 0);

        // Line y = -x + 5
        line = LineUtils.initLineWithTwoPoints(new PointF(0, 5), new PointF(2, 3));
        assertNotNull(line);
        assertEquals(Line.Type.DIAGONAL, line.getType());

        assertEquals(5, line.getXIntercept(), 0.0);
        assertEquals(5, line.getYIntercept(), 0.0);
        assertNotNull(line.getSlope());
        assertEquals(-1, line.getSlope(), 0.0);

        assertTrue(line.containsPoint(new PointF(0, 5)));
        assertTrue(line.containsPoint(new PointF(2, 3)));
        assertTrue(line.containsPoint(new PointF(1, 4)));
        assertTrue(line.containsPoint(new PointF(3, 2)));

        midpoint = line.getMidpoint(new PointF(1, 4), new PointF(3, 2));
        assertNotNull(midpoint);
        assertEquals(2, midpoint.x, 0);
        assertEquals(3, midpoint.y, 0);
    }

    @Test
    public void test_initLineWithSlopeAndPoint_positiveSlope() {

        // Line y = x
        Line line = LineUtils.initLineWithSlopeAndPoint(1f, new PointF(2, 2));

        assertNotNull(line);
        assertEquals(Line.Type.DIAGONAL, line.getType());

        assertEquals(0, line.getXIntercept(), 0.0);
        assertEquals(0, line.getYIntercept(), 0.0);
        assertNotNull(line.getSlope());
        assertEquals(1, line.getSlope(), 0.0);

        assertTrue(line.containsPoint(new PointF(1, 1)));
        assertTrue(line.containsPoint(new PointF(2, 2)));
        assertTrue(line.containsPoint(new PointF(5, 5)));

        assertNotNull(line.getMidpoint(new PointF(1, 1), new PointF(5, 5)));

        // Line y = 2x - 6;
        line = LineUtils.initLineWithSlopeAndPoint(2f, new PointF(10, 14));

        assertNotNull(line);
        assertEquals(Line.Type.DIAGONAL, line.getType());

        assertEquals(-6, line.getYIntercept(), 0);
        assertEquals(3, line.getXIntercept(), 0);
        assertNotNull(line.getSlope());
        assertEquals(2, line.getSlope(), 0.0);

        assertTrue(line.containsPoint(new PointF(3, 0)));
        assertTrue(line.containsPoint(new PointF(10, 14)));
        assertTrue(line.containsPoint(new PointF(0, -6)));

        assertNotNull(line.getMidpoint(new PointF(0, -6), new PointF(10, 14)));
    }

    @Test
    public void test_initLineWithSlopeAndPoint_negativeSlope() {
        Line line = LineUtils.initLineWithSlopeAndPoint(-1f, new PointF(-2, 2));

        // Line y = -x;
        assertNotNull(line);
        assertEquals(Line.Type.DIAGONAL, line.getType());

        assertEquals(0, line.getXIntercept(), 0.0);
        assertEquals(0, line.getYIntercept(), 0.0);
        assertNotNull(line.getSlope());
        assertEquals(-1, line.getSlope(), 0.0);

        assertTrue(line.containsPoint(new PointF(-1, 1)));
        assertTrue(line.containsPoint(new PointF(-2, 2)));
        assertTrue(line.containsPoint(new PointF(1, -1)));
        assertTrue(line.containsPoint(new PointF(2, -2)));

        PointF midpoint = line.getMidpoint(new PointF(-1, 1), new PointF(1, -1));
        assertNotNull(midpoint);
        assertEquals(0, midpoint.x, 0);
        assertEquals(0, midpoint.y, 0);

        // Line y = -x + 5
        line = LineUtils.initLineWithSlopeAndPoint(-1f, new PointF(2, 3));
        assertNotNull(line);
        assertEquals(Line.Type.DIAGONAL, line.getType());

        assertEquals(5, line.getXIntercept(), 0.0);
        assertEquals(5, line.getYIntercept(), 0.0);
        assertNotNull(line.getSlope());
        assertEquals(-1, line.getSlope(), 0.0);

        assertTrue(line.containsPoint(new PointF(0, 5)));
        assertTrue(line.containsPoint(new PointF(2, 3)));
        assertTrue(line.containsPoint(new PointF(1, 4)));
        assertTrue(line.containsPoint(new PointF(3, 2)));

        midpoint = line.getMidpoint(new PointF(1, 4), new PointF(3, 2));
        assertNotNull(midpoint);
        assertEquals(2, midpoint.x, 0);
        assertEquals(3, midpoint.y, 0);
    }

    @Test
    public void test_getPointOfIntersection() {
        /*
         Test:
        Line 1: y = 3x-3
        Line 2: y = 2.3x+4
         */

        Line line1 = LineUtils.initLineWithSlopeAndPoint(3f, new PointF(0, -3));
        Line line2 = LineUtils.initLineWithSlopeAndPoint(2.3f, new PointF(0, 4));

        PointF intersection = LineUtils.getPointOfIntersection(line1, line2);
        assertNotNull(intersection);
        assertEquals(10, Math.round(intersection.x));
        assertEquals(27, Math.round(intersection.y));

        /*
        Test:
        Line 1 = 2x + 3
        Line 2 = -0.5x + 7
         */

        line1 = LineUtils.initLineWithSlopeAndPoint(2f, new PointF(0, 3));
        line2 = LineUtils.initLineWithSlopeAndPoint(-0.5f, new PointF(0, 7));

        intersection = LineUtils.getPointOfIntersection(line1, line2);
        assertNotNull(intersection);
        assertEquals(1.6, intersection.x, 0.01);
        assertEquals(6.2, intersection.y, 0.01);
    }

    @Test
    public void test_horizontalLine() {
        Line.Builder builder = new Line.Builder(Line.Type.HORIZONTAL);

        // Line y = 5;
        builder.setYIntercept(5F);
        Line line = builder.build();

        assertNotNull(line);
        assertEquals(line.getType(), Line.Type.HORIZONTAL);
        assertNotNull(line.getSlope());
        assertEquals(line.getSlope(), 0, 0);
        assertNull(line.getXIntercept());
        assertEquals(5f, line.getYIntercept(), 0);

        assertTrue(line.containsPoint(new PointF(200,5)));
        assertTrue(line.containsPoint(new PointF(400,5)));

        PointF midpoint = line.getMidpoint(new PointF(200, 5), new PointF(400, 5));
        assertNotNull(midpoint);
        assertEquals(300,midpoint.x,0);
        assertEquals(5,midpoint.y,0);

        midpoint = line.getMidpoint(new PointF(200, 5), new PointF(400, 6));
        assertNull(midpoint);

        // Intersection with diagonal line:
        // Test intersection of y = 5 with x = y; Intersection should be (5,5);
        Line line2 = LineUtils.initLineWithSlopeAndPoint(1f,new PointF(1,1));
        assertEquals(line2.getType(), Line.Type.DIAGONAL);

        PointF intersection = LineUtils.getPointOfIntersection(line,line2);
        assertNotNull(intersection);
        assertEquals(5,intersection.x,0);
        assertEquals(5,intersection.y,0);

        // Intersection with vertical line x = 5. Intersection should be (5,5)
        line2 = LineUtils.initLineWithSlopeAndPoint(null,new PointF(5,20));
        assertNotNull(line2);
        assertEquals(line2.getType(), Line.Type.VERTICAL);
        assertNull(line2.getSlope());

        intersection = LineUtils.getPointOfIntersection(line,line2);
        assertNotNull(intersection);
    }
}
