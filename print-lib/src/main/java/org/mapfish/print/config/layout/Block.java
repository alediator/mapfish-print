package org.mapfish.print.config.layout;

import com.lowagie.text.DocumentException;
import com.lowagie.text.Paragraph;
import org.mapfish.print.InvalidValueException;
import org.mapfish.print.RenderingContext;
import org.mapfish.print.utils.PJsonObject;

/**
 * Base class for blocks that can be found in "items" arrays.
 */
public abstract class Block {
    private String align = "left";
    private String vertAlign = "middle";
    private String backgroundColor = null;

    public Block() {

    }

    public abstract void render(PJsonObject params, PdfElement target, RenderingContext context) throws DocumentException;

    public String getBackgroundColor() {
        return backgroundColor;
    }

    public MapBlock getMap() {
        return null;
    }

    public interface PdfElement {
        void add(com.lowagie.text.Element element) throws DocumentException;
    }

    public void setAlign(String align) {
        this.align = align;
    }

    public void setVertAlign(String vAlign) {
        this.vertAlign = vAlign;
    }

    public int getAlignInt() {
        if ("center".equalsIgnoreCase(align)) {
            return Paragraph.ALIGN_CENTER;
        } else if ("justified".equalsIgnoreCase(align)) {
            return Paragraph.ALIGN_JUSTIFIED;
        } else if ("left".equalsIgnoreCase(align)) {
            return Paragraph.ALIGN_LEFT;
        } else if ("right".equalsIgnoreCase(align)) {
            return Paragraph.ALIGN_RIGHT;
        }
        throw new InvalidValueException("align", align);
    }

    public int getVAlignInt() {
        if ("top".equalsIgnoreCase(vertAlign)) {
            return Paragraph.ALIGN_TOP;
        } else if ("middle".equalsIgnoreCase(vertAlign)) {
            return Paragraph.ALIGN_MIDDLE;
        } else if ("bottom".equalsIgnoreCase(vertAlign)) {
            return Paragraph.ALIGN_BOTTOM;
        }
        throw new InvalidValueException("valign", vertAlign);
    }

    public void setBackgroundColor(String backgroundColor) {
        this.backgroundColor = backgroundColor;
    }
}
