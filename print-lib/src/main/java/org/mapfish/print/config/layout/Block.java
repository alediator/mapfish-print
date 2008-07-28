package org.mapfish.print.config.layout;

import com.lowagie.text.DocumentException;
import org.mapfish.print.RenderingContext;
import org.mapfish.print.utils.PJsonObject;

import java.awt.*;

/**
 * Base class for blocks that can be found in "items" arrays.
 */
public abstract class Block {
    private HorizontalAlign align = HorizontalAlign.LEFT;
    private VerticalAlign vertAlign = VerticalAlign.MIDDLE;
    private Color backgroundColor = null;

    public Block() {

    }

    /**
     * Called when the block is rendered.
     */
    public abstract void render(PJsonObject params, PdfElement target, RenderingContext context) throws DocumentException;

    public MapBlock getMap() {
        return null;
    }

    public interface PdfElement {
        void add(com.lowagie.text.Element element) throws DocumentException;
    }

    public void setAlign(HorizontalAlign align) {
        this.align = align;
    }

    public void setVertAlign(VerticalAlign vertAlign) {
        this.vertAlign = vertAlign;
    }

    public Color getBackgroundColor() {
        return backgroundColor;
    }

    public void setBackgroundColor(Color backgroundColor) {
        this.backgroundColor = backgroundColor;
    }

    public HorizontalAlign getAlign() {
        return align;
    }

    public VerticalAlign getVertAlign() {
        return vertAlign;
    }

}
