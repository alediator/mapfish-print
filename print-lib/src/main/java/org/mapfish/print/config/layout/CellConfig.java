package org.mapfish.print.config.layout;

import com.lowagie.text.pdf.PdfPCell;

import java.awt.*;

public class CellConfig extends BorderConfig {

    private Double paddingLeft = null;
    private Double paddingRight = null;
    private Double paddingTop = null;
    private Double paddingBottom = null;

    private Color backgroundColor = null;

    private HorizontalAlign align = null;
    private VerticalAlign vertAlign = null;

    protected void apply(PdfPCell cell) {
        if (paddingLeft != null) cell.setPaddingLeft(paddingLeft.floatValue());
        if (paddingRight != null)
            cell.setPaddingRight(paddingRight.floatValue());
        if (paddingTop != null) cell.setPaddingTop(paddingTop.floatValue());
        if (paddingBottom != null)
            cell.setPaddingBottom(paddingBottom.floatValue());

        if (borderWidthLeft != null)
            cell.setBorderWidthLeft(borderWidthLeft.floatValue());
        if (borderWidthRight != null)
            cell.setBorderWidthRight(borderWidthRight.floatValue());
        if (borderWidthTop != null)
            cell.setBorderWidthTop(borderWidthTop.floatValue());
        if (borderWidthBottom != null)
            cell.setBorderWidthBottom(borderWidthBottom.floatValue());

        if (borderColorLeft != null) cell.setBorderColorLeft(borderColorLeft);
        if (borderColorRight != null)
            cell.setBorderColorRight(borderColorRight);
        if (borderColorTop != null) cell.setBorderColorTop(borderColorTop);
        if (borderColorBottom != null)
            cell.setBorderColorBottom(borderColorBottom);

        if (backgroundColor != null) cell.setBackgroundColor(backgroundColor);

        if (align != null) cell.setHorizontalAlignment(align.getCode());
        if (vertAlign != null) cell.setVerticalAlignment(vertAlign.getCode());

    }

    public void setPadding(double padding) {
        setPaddingLeft(padding);
        setPaddingRight(padding);
        setPaddingTop(padding);
        setPaddingBottom(padding);
    }

    public void setPaddingLeft(double paddingLeft) {
        this.paddingLeft = paddingLeft;
    }

    public void setPaddingRight(double paddingRight) {
        this.paddingRight = paddingRight;
    }

    public void setPaddingTop(double paddingTop) {
        this.paddingTop = paddingTop;
    }

    public void setPaddingBottom(double paddingBottom) {
        this.paddingBottom = paddingBottom;
    }

    public void setBackgroundColor(Color backgroundColor) {
        this.backgroundColor = backgroundColor;
    }

    public void setAlign(HorizontalAlign align) {
        this.align = align;
    }

    public void setVertAlign(VerticalAlign vertAlign) {
        this.vertAlign = vertAlign;
    }
}
