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
        Line line = LineUtils.createLineFromTwoPoints(a, a);
        assertNull(line);
    }

    @Test
    public void test_initLineWithTwoPoints_positiveSlope() {
        // Line y = x
        Line line = LineUtils.createLineFromTwoPoints(new PointF(1, 1), new PointF(2, 2));

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
        line = LineUtils.createLineFromTwoPoints(new PointF(3, 0), new PointF(10, 14));

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
        Line line = LineUtils.createLineFromSlopeAndPoint(-1f, new PointF(-2, 2));

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
        line = LineUtils.createLineFromTwoPoints(new PointF(0, 5), new PointF(2, 3));
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
        Line line = LineUtils.createLineFromSlopeAndPoint(1f, new PointF(2, 2));

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
        line = LineUtils.createLineFromSlopeAndPoint(2f, new PointF(10, 14));

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
        Line line = LineUtils.createLineFromSlopeAndPoint(-1f, new PointF(-2, 2));

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
        line = LineUtils.createLineFromSlopeAndPoint(-1f, new PointF(2, 3));
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

        Line line1 = LineUtils.createLineFromSlopeAndPoint(3f, new PointF(0, -3));
        Line line2 = LineUtils.createLineFromSlopeAndPoint(2.3f, new PointF(0, 4));

        PointF intersection = LineUtils.getPointOfIntersection(line1, line2);
        assertNotNull(intersection);
        assertEquals(10, Math.round(intersection.x));
        assertEquals(27, Math.round(intersection.y));

        /*
        Test:
        Line 1 = 2x + 3
        Line 2 = -0.5x + 7
         */

        line1 = LineUtils.createLineFromSlopeAndPoint(2f, new PointF(0, 3));
        line2 = LineUtils.createLineFromSlopeAndPoint(-0.5f, new PointF(0, 7));

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

        assertTrue(line.containsPoint(new PointF(200, 5)));
        assertTrue(line.containsPoint(new PointF(400, 5)));

        PointF midpoint = line.getMidpoint(new PointF(200, 5), new PointF(400, 5));
        assertNotNull(midpoint);
        assertEquals(300, midpoint.x, 0);
        assertEquals(5, midpoint.y, 0);

        midpoint = line.getMidpoint(new PointF(200, 5), new PointF(400, 6));
        assertNull(midpoint);

        // Intersection with diagonal line:
        // Test intersection of y = 5 with x = y; Intersection should be (5,5);
        Line line2 = LineUtils.createLineFromSlopeAndPoint(1f, new PointF(1, 1));
        assertEquals(line2.getType(), Line.Type.DIAGONAL);

        PointF intersection = LineUtils.getPointOfIntersection(line, line2);
        assertNotNull(intersection);
        assertEquals(5, intersection.x, 0);
        assertEquals(5, intersection.y, 0);

        // Intersection with vertical line x = 5. Intersection should be (5,5)
        line2 = LineUtils.createLineFromSlopeAndPoint(null, new PointF(5, 20));
        assertNotNull(line2);
        assertEquals(line2.getType(), Line.Type.VERTICAL);
        assertNull(line2.getSlope());

        intersection = LineUtils.getPointOfIntersection(line, line2);
        assertNotNull(intersection);
    }

    @Test
    public void test_verticalLine() {
        Line.Builder builder = new Line.Builder(Line.Type.VERTICAL);

        // Line x = 5;
        builder.setXIntercept(5F);
        Line line = builder.build();

        assertNotNull(line);
        assertEquals(line.getType(), Line.Type.VERTICAL);
        assertNull(line.getSlope());
        assertNull(line.getYIntercept());
        assertEquals(5f, line.getXIntercept(), 0);

        assertTrue(line.containsPoint(new PointF(5, 200)));
        assertTrue(line.containsPoint(new PointF(5, 400)));

        PointF midpoint = line.getMidpoint(new PointF(5, 200), new PointF(5, 400));
        assertNotNull(midpoint);
        assertEquals(300, midpoint.y, 0);
        assertEquals(5, midpoint.x, 0);

        // Intersection with diagonal line:
        // Test intersection of y = 5 with x = y; Intersection should be (5,5);
        Line line2 = LineUtils.createLineFromSlopeAndPoint(1f, new PointF(1, 1));
        assertEquals(line2.getType(), Line.Type.DIAGONAL);

        PointF intersection = LineUtils.getPointOfIntersection(line, line2);
        assertNotNull(intersection);
        assertEquals(5, intersection.x, 0);
        assertEquals(5, intersection.y, 0);

        // Intersection with horizontal line y = 5. Intersection should be (5,5)
        line2 = LineUtils.createLineFromSlopeAndPoint(0f, new PointF(5, 5));
        assertNotNull(line2);
        assertEquals(line2.getType(), Line.Type.HORIZONTAL);
        assertNotNull(line2.getSlope());
        assertEquals(0f, line2.getSlope(), 0f);

        intersection = LineUtils.getPointOfIntersection(line, line2);
        assertNotNull(intersection);
        assertEquals(5, intersection.x, 0);
        assertEquals(5, intersection.y, 0);
    }

    @Test
    public void test_unitVector() {
        // Horizontal line
        Line.Builder builder = new Line.Builder(Line.Type.HORIZONTAL);
        builder.setYIntercept(10f);
        Line line = builder.build();
        assertNotNull(line);
        assertEquals(line.getType(), Line.Type.HORIZONTAL);
        assertNotNull(line.getSlope());
        assertEquals(line.getSlope(), 0f, 0);

        PointF unitVector = line.getUnitVector();
        assertEquals(unitVector, new PointF(1.0f, 0f));

        // Vertical line
        builder = new Line.Builder(Line.Type.VERTICAL);
        builder.setXIntercept(10f);
        line = builder.build();
        assertNotNull(line);
        assertEquals(line.getType(), Line.Type.VERTICAL);
        assertNull(line.getSlope());

        unitVector = line.getUnitVector();
        assertEquals(unitVector, new PointF(0f, 1.0f));

        // Line x = y. Expected unit vector = (1/sqrt(2),1/sqrt(2))
        builder = new Line.Builder(Line.Type.DIAGONAL);
        builder.setYIntercept(0f)
                .setSlope(1f);
        line = builder.build();
        assertNotNull(line);
        assertEquals(line.getType(), Line.Type.DIAGONAL);
        assertNotNull(line.getSlope());

        PointF expectedValue = new PointF();
        expectedValue.x = (float)Math.pow(Math.sqrt(2), -1);
        expectedValue.y = (float)Math.pow(Math.sqrt(2), -1);

        unitVector = line.getUnitVector();

        assertEquals(expectedValue.x, unitVector.x, 0.01);
        assertEquals(expectedValue.y, unitVector.y, 0.01);

        // Line y = 2x. Expected unit vector (1/sqrt(5),2/sqrt(5))
        builder = new Line.Builder(Line.Type.DIAGONAL);
        builder.setYIntercept(0f)
                .setSlope(2f);
        line = builder.build();
        assertNotNull(line);
        assertEquals(line.getType(), Line.Type.DIAGONAL);
        assertNotNull(line.getSlope());

        expectedValue = new PointF();
        expectedValue.x = (float)Math.pow(Math.sqrt(5), -1);
        expectedValue.y = (float)((float)2 * (Math.pow(Math.sqrt(5), -1)));

        unitVector = line.getUnitVector();

        assertEquals(expectedValue.x, unitVector.x, 0.01);
        assertEquals(expectedValue.y, unitVector.y, 0.01);
    }

    @Test
    public void test_slopeOrthogonalLine() {
        Line line;
        Line.Builder builder;

        // vertical line: (x = 5) Slope of orthogonal line should be 0.
        builder = new Line.Builder(Line.Type.VERTICAL);
        builder.setXIntercept(5f);
        line = builder.build();

        Float orthogonalLineSlope = line.getOrthogonalLineSlope();
        assertNotNull(orthogonalLineSlope);
        assertEquals(0, orthogonalLineSlope, 0);

        // horizontal line: (y = 5). Slope of orthogonal line should be undefined.
        builder = new Line.Builder(Line.Type.HORIZONTAL);
        line = builder.build();
        assertNull(line.getOrthogonalLineSlope());

        // diagonal line: (y = 2x). Expected slope:  -1/2
        builder = new Line.Builder(Line.Type.DIAGONAL);
        builder.setSlope(2f);
        line = builder.build();
        assertNotNull(line.getOrthogonalLineSlope());
        assertEquals(-0.5f, line.getOrthogonalLineSlope(), 0);

        // diagonal line: (y = -2x). Expected slope:  1/2
        builder = new Line.Builder(Line.Type.DIAGONAL);
        builder.setSlope(-2f);
        line = builder.build();
        assertNotNull(line.getOrthogonalLineSlope());
        assertEquals(0.5f, line.getOrthogonalLineSlope(), 0);

        // diagonal line: (y = 4/3x). Expected slope:  -0.75
        builder = new Line.Builder(Line.Type.DIAGONAL);
        builder.setSlope(4f/3);
        line = builder.build();
        assertNotNull(line.getOrthogonalLineSlope());
        assertEquals(-(3f/4), line.getOrthogonalLineSlope(), 0);
    }
}
