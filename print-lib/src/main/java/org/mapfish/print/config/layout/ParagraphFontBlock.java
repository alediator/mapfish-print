package org.mapfish.print.config.layout;

import org.mapfish.print.RenderingContext;
import org.mapfish.print.utils.PJsonObject;
import com.lowagie.text.Paragraph;
import com.lowagie.text.DocumentException;
import com.lowagie.text.Font;
import com.lowagie.text.FontFactory;
import com.lowagie.text.pdf.BaseFont;

public abstract class ParagraphFontBlock extends ParagraphBlock {
    private String font = "Helvetica";
    protected Float fontSize = null;
    private String fontEncoding = BaseFont.WINANSI;

    public void setFont(String font) {
        this.font = font;
    }

    public void setFontSize(float fontSize) {
        this.fontSize = fontSize;
    }

    public String getFont() {
        return font;
    }

    public float getFontSize() {
        if (fontSize != null) {
            return fontSize;
        } else {
            return 12;
        }
    }

    public void setFontEncoding(String fontEncoding) {
        this.fontEncoding = fontEncoding;
    }

    protected Font getPdfFont() {
        return FontFactory.getFont(font, fontEncoding, getFontSize());
    }

}
