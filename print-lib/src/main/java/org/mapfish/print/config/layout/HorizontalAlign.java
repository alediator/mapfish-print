package org.mapfish.print.config.layout;

import com.lowagie.text.Paragraph;

public enum HorizontalAlign {
    LEFT(Paragraph.ALIGN_LEFT),
    RIGHT(Paragraph.ALIGN_RIGHT),
    CENTER(Paragraph.ALIGN_CENTER),
    JUSTIFIED(Paragraph.ALIGN_JUSTIFIED);

    private final int code;

    HorizontalAlign(int code) {
        this.code = code;
    }

    public int getCode() {
        return code;
    }
}
