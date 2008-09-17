package org.mapfish.print.config.layout;

import java.awt.*;

public class BorderConfig {
    protected Double borderWidthLeft = null;
    protected Double borderWidthRight = null;
    protected Double borderWidthTop = null;
    protected Double borderWidthBottom = null;
    protected Color borderColorLeft = null;
    protected Color borderColorRight = null;
    protected Color borderColorTop = null;
    protected Color borderColorBottom = null;

    public void setBorderColor(Color color) {
        setBorderColorLeft(color);
        setBorderColorRight(color);
        setBorderColorTop(color);
        setBorderColorBottom(color);
    }

    public void setBorderWidth(double border) {
        setBorderWidthLeft(border);
        setBorderWidthRight(border);
        setBorderWidthTop(border);
        setBorderWidthBottom(border);
    }

    public void setBorderWidthLeft(double borderWidthLeft) {
        this.borderWidthLeft = borderWidthLeft;
    }

    public void setBorderWidthRight(double borderWidthRight) {
        this.borderWidthRight = borderWidthRight;
    }

    public void setBorderWidthTop(double borderWidthTop) {
        this.borderWidthTop = borderWidthTop;
    }

    public void setBorderWidthBottom(double borderWidthBottom) {
        this.borderWidthBottom = borderWidthBottom;
    }

    public void setBorderColorLeft(Color borderColorLeft) {
        this.borderColorLeft = borderColorLeft;
    }

    public void setBorderColorRight(Color borderColorRight) {
        this.borderColorRight = borderColorRight;
    }

    public void setBorderColorTop(Color borderColorTop) {
        this.borderColorTop = borderColorTop;
    }

    public void setBorderColorBottom(Color borderColorBottom) {
        this.borderColorBottom = borderColorBottom;
    }
}
