package com.jarvis.dragdropresearch.math;

/**
 * Top level class representing an algebraic equation of some sort,
 * such as the equation for a line, etc.
 */
public abstract class Equation {

    /**
     * @return String desribing the formula that
     * this {@link Equation} corresponds to.
     */
    abstract String getDescription();
}
