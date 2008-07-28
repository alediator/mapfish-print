package org.mapfish.print.scalebar;

/**
 * Specify a direction for the labels and the bar.
 */
public enum Direction {
    UP(0, 0.0),
    DOWN(0, 180),
    LEFT(1, -90),
    RIGHT(1, 90);

    private final int orientation;
    private final double angle;

    Direction(int orientation, double angle) {
        this.orientation = orientation;
        this.angle = angle;
    }

    public boolean isSameOrientation(Direction other) {
        return other.orientation == orientation;
    }

    public double getAngle() {
        return angle;
    }
}
