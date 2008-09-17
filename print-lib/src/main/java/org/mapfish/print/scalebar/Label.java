package org.mapfish.print.scalebar;

import com.lowagie.text.pdf.BaseFont;

/**
 * Position, size and content of a label
 */
public class Label {
    /**
     * Position of the label, relative to the first tick of the bar.
     */
    public final float paperOffset;

    public String label;

    /**
     * size of the text in the axis of the bar.
     */
    public final float width;

    /**
     * size of the text in the perpendicular axis of the bar
     */
    public final float height;

    public Label(float paperOffset, String label, BaseFont font, double fontSize, boolean rotated) {
        this.paperOffset = paperOffset;
        this.label = label;
        final float textWidth = font.getWidthPoint(label, (float) fontSize);
        final float textHeight = font.getAscentPoint(label, (float) fontSize) - font.getDescentPoint(label, (float) fontSize);
        if (rotated) {
            this.height = textWidth;
            this.width = textHeight;
        } else {
            this.width = textWidth;
            this.height = textHeight;
        }
    }
}
