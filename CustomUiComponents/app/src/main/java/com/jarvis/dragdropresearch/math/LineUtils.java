package com.jarvis.dragdropresearch.math;

import android.graphics.PointF;

import androidx.annotation.Nullable;

public final class LineUtils {

    private LineUtils() {

    }

    /**
     * Return the shortest distance between two points on a line.
     */
    public static float getDistanceBetweenPoints(PointF a, PointF b) {
        return (float)Math.sqrt(Math.pow(b.x - a.x, 2) + Math.pow(b.y - a.y, 2));
    }

    /**
     * Return an equation for a {@link Line} when the slope is provided and one point is known.
     *
     * @param slope Slope. If null, the line will be considered a vertical line with x-intercept
     * equal to the x value of the point passed in.
     * @param point {@link PointF} defining a point on the line. If slope is 0 or null, the line
     * will be treated as {@link Line.Type#HORIZONTAL} AND {@link Line.Type#VERTICAL} respectively.
     * For vertical lines, the Y coordinate of the point is ignored. For horizontal lines, the x coordinate
     * is ignored.
     */
    public static Line createLineFromSlopeAndPoint(@Nullable Float slope, PointF point) {
        float x = point.x;
        float y = point.y;
        float yIntercept;

        if (slope == null) {
            Line.Builder builder = new Line.Builder(Line.Type.VERTICAL);
            builder.setXIntercept(point.x);
            return builder.build();
        }
        if (slope == 0) {
            Line.Builder builder = new Line.Builder(Line.Type.HORIZONTAL);
            builder.setYIntercept(point.y)
                    .setSlope(slope);
            return builder.build();
        } else if (x == 0) {
            // If x is 0, then we know what the y-intercept is without calculating.
            Line.Builder builder = new Line.Builder(Line.Type.DIAGONAL);
            builder.setYIntercept(point.y)
                    .setSlope(slope);
            return builder.build();
        } else {
            yIntercept = y - slope * x;

            Line.Builder builder = new Line.Builder(Line.Type.DIAGONAL);
            builder.setYIntercept(yIntercept)
                    .setSlope(slope);
            return builder.build();
        }
    }

    @Nullable
    public static Line createLineFromTwoPoints(PointF pointA, PointF pointB) {
        if (pointA.x == pointB.x && pointA.y == pointB.y) {
            return null;
        } else if (pointA.x == pointB.x && pointA.y != pointB.y) {
            // Vertical line.

            Line.Builder builder = new Line.Builder(Line.Type.VERTICAL);
            builder.setXIntercept(pointA.x);
            return builder.build();
        } else if (pointA.x != pointB.x && pointA.y == pointB.y) {
            // Horizontal line.

            Line.Builder builder = new Line.Builder(Line.Type.HORIZONTAL);
            builder.setYIntercept(pointA.y);
            return builder.build();
        }

        float slope = (pointA.y - pointB.y) / (pointA.x - pointB.x);
        float yIntercept;

        if (pointA.x == 0) {
            yIntercept = pointA.y;
        } else if (pointB.x == 0) {
            yIntercept = pointB.y;
        } else {
            yIntercept = pointB.y - slope * pointB.x;
        }

        Line.Builder builder = new Line.Builder(Line.Type.DIAGONAL);
        builder.setYIntercept(yIntercept)
                .setSlope(slope);
        return builder.build();
    }

    @Nullable
    public static PointF getPointOfIntersection(Line line1, Line line2) {
        if (line1 == null || line2 == null) {
            return null;
        } else if (line1.getType() != Line.Type.DIAGONAL &&
                line1.getType() == line2.getType()) {
            // Case where the two lines are parallel and non-diagonal

            return null;
        } else if (line1.getSlope() != null
                && line2.getSlope() != null
                && line1.getSlope().equals(line2.getSlope())) {
            // Case where the two lines are parallel and diagonal.

            return null;
        } else if (line1.getType() == Line.Type.VERTICAL
                && line2.getType() == Line.Type.HORIZONTAL) {
            // X-value for point of intersection is where line 1 crosses the X-axis.
            // Y-value is where line 2 crosses the Y axis.

            return new PointF(line1.getXIntercept(), line2.getYIntercept());
        } else if (line2.getType() == Line.Type.VERTICAL
                && line1.getType() == Line.Type.HORIZONTAL) {
            // X-value for point of intersection is where line 2 crosses the X-axis.
            // Y-value is where line 1 crosses the Y axis.

            return new PointF(line2.getXIntercept(), line1.getYIntercept());
        } else if (line1.getType() == Line.Type.HORIZONTAL) {
            // Case where line 1 is horizontal and line 2 is diagonal.

            float yInterceptLine1 = line1.getYIntercept();
            Float xLine2 = line2.getX(yInterceptLine1);
            if (xLine2 != null) {
                return new PointF(xLine2, yInterceptLine1);
            } else {
                return null;
            }
        } else if (line2.getType() == Line.Type.HORIZONTAL) {
            // Case where line 2 is horizontal and line 1 is diagonal.

            float yInterceptLine2 = line2.getYIntercept();
            Float xLine1 = line1.getX(yInterceptLine2);
            if (xLine1 != null) {
                return new PointF(xLine1, yInterceptLine2);
            } else {
                return null;
            }
        } else if (line1.getType() == Line.Type.VERTICAL) {
            // Case where line 1 is horizontal and line 2 is diagonal.

            float xInterceptLine1 = line1.getXIntercept();
            Float yLine2 = line2.getY(xInterceptLine1);
            if (yLine2 != null) {
                return new PointF(xInterceptLine1, yLine2);
            } else {
                return null;
            }
        } else if (line2.getType() == Line.Type.VERTICAL) {
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
}
