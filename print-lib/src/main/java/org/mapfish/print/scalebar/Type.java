package org.mapfish.print.scalebar;

/**
 * Type of scale bar to render.
 */
public enum Type {
    /**
     * A simple line with ticks.
     */
    LINE,

    /**
     * A bar with alternating black and white zones marking the sub-intervals.
     */
    BAR,

    /**
     * A bar with alternating black and white zones marking the sub-intervals.
     * Intervals have small additional ticks.
     */
    BAR_SUB
}
