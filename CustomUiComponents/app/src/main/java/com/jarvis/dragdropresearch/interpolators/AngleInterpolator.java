package com.jarvis.dragdropresearch.interpolators;

import android.util.Pair;

import java.util.ArrayList;
import java.util.List;

public class AngleInterpolator extends Interpolator {

    private float mMaxAngle;
    private DrawingDescriptor mDrawingDescriptor = new DrawingDescriptor();
    private int mMaxComponents = 1;

    public AngleInterpolator(int maxValue) {
        super(maxValue);
    }

    public AngleInterpolator(int maxValue, float maxAngle, int maxComponents) {
        super(maxValue);
        mMaxAngle = maxAngle;
        mMaxComponents = maxComponents;
    }

    public float getMaxAngle() {
        return mMaxAngle;
    }

    public void setMaxAngle(float maxAngle) {
        mMaxAngle = maxAngle;
    }

    public float getInterpolatedAngle() {
        return getInterpolation() * mMaxAngle;
    }

    public void setMaxComponents(int maxComponents) {
        mMaxComponents = maxComponents;
    }

    /**
     * @return {@link DrawingDescriptor} containing the data needed to draw the arc associated
     * with the current interpolated angle.
     */
    public DrawingDescriptor getDrawingDescriptor() {
        computeDrawingMetrics();
        return mDrawingDescriptor;
    }

    private void computeDrawingMetrics() {
        List<Pair<Float, Float>> drawingAngles = new ArrayList<>();
        if (mMaxComponents <= 1) {
            drawingAngles.add(new Pair<>(0f, getInterpolatedAngle()));
        } else {// Number of degrees that each component will take up in the arc.
            float angleFactor = 360.0f / mMaxComponents;

            // Total number of components that makeup the current interpolated angle.
            int componentCount = (int)Math.floor(getInterpolatedAngle() / angleFactor);

            // Left-over degrees after dividing the interpolated angle by the angle factor.
            float componentModulus = getInterpolatedAngle() % angleFactor;

            float currentStartAngle = 0;

            for (int i = 0; i < componentCount; i++) {
                drawingAngles.add(new Pair<>(currentStartAngle, angleFactor));
                currentStartAngle += angleFactor;
            }
            if (componentModulus != 0) {
                drawingAngles.add(new Pair<>(currentStartAngle, componentModulus));
            }
        }

        mDrawingDescriptor.setDrawingAngles(drawingAngles);
    }

    /**
     * Contains the data needed to draw this angle as an arc.
     */
    public static class DrawingDescriptor {

        private List<Pair<Float, Float>> drawingAngles;

        /**
         * @return {@link List<Pair<Float,Float>> describing the start/sweep angles that make up
         * the current angle. The first element in the pair is the start angle. The second is the sweep angle.
         */
        public List<Pair<Float, Float>> getDrawingAngles() {
            return drawingAngles;
        }

        private void setDrawingAngles(
                List<Pair<Float, Float>> drawingAngles) {
            this.drawingAngles = drawingAngles;
        }
    }
}
