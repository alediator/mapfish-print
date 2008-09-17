package org.mapfish.print.config.layout;

import org.mapfish.print.RenderingContext;
import org.mapfish.print.utils.PJsonObject;
import com.lowagie.text.Paragraph;
import com.lowagie.text.DocumentException;
import com.lowagie.text.Font;
import com.lowagie.text.FontFactory;
import com.lowagie.text.pdf.BaseFont;

public abstract class FontBlock extends Block {
    private String font = "Helvetica";
    protected Double fontSize = null;
    private String fontEncoding = BaseFont.WINANSI;

    protected double spacingAfter = 0;

    public void setFont(String font) {
        this.font = font;
    }

    public void setFontSize(Double fontSize) {
        this.fontSize = fontSize;
    }

    public void setFontSize(double fontSize) {
        this.fontSize = fontSize;
    }

    public String getFont() {
        return font;
    }

    public double getFontSize() {
        if (fontSize != null) {
            return fontSize;
        } else {
            return 12.0;
        }
    }

    public void setFontEncoding(String fontEncoding) {
        this.fontEncoding = fontEncoding;
    }

    protected Font getPdfFont() {
        return FontFactory.getFont(font, fontEncoding, (float) getFontSize());
    }

    public void setSpacingAfter(double spacingAfter) {
        this.spacingAfter = spacingAfter;
    }
}