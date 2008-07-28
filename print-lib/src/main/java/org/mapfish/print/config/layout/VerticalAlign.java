package org.mapfish.print.config.layout;

import com.lowagie.text.Paragraph;

public enum VerticalAlign {
    TOP(Paragraph.ALIGN_TOP),
    MIDDLE(Paragraph.ALIGN_MIDDLE),
    BOTTOM(Paragraph.ALIGN_BOTTOM);

    private final int code;

    VerticalAlign(int code) {
        this.code = code;
    }

    public int getCode() {
        return code;
    }
}
