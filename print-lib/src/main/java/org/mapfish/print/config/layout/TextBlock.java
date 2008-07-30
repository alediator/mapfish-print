package org.mapfish.print.config.layout;

import com.lowagie.text.DocumentException;
import com.lowagie.text.Font;
import com.lowagie.text.FontFactory;
import com.lowagie.text.Paragraph;
import com.lowagie.text.Phrase;
import com.lowagie.text.pdf.BaseFont;
import org.mapfish.print.PDFUtils;
import org.mapfish.print.RenderingContext;
import org.mapfish.print.utils.PJsonObject;

public class TextBlock extends ParagraphBlock {
    private String font = "Helvetica";
    private float fontSize = 12;
    private String fontEncoding = BaseFont.WINANSI;
    private String text = "";

    protected void fillParagraph(RenderingContext context, PJsonObject params, Paragraph paragraph) throws DocumentException {
        final Font pdfFont;
        pdfFont = FontFactory.getFont(font, fontEncoding, fontSize);
        paragraph.setFont(pdfFont);

        final Phrase text = PDFUtils.renderString(context, params, this.text, pdfFont);
        paragraph.add(text);
    }

    public void setFont(String font) {
        this.font = font;
    }

    public void setText(String text) {
        this.text = text;
    }

    public void setFontSize(float fontSize) {
        this.fontSize = fontSize;
    }

    public String getFont() {
        return font;
    }

    public float getFontSize() {
        return fontSize;
    }

    public void setFontEncoding(String fontEncoding) {
        this.fontEncoding = fontEncoding;
    }

    public String getText() {
        return text;
    }
}
