package org.mapfish.print.config.layout;

import com.lowagie.text.DocumentException;
import com.lowagie.text.Font;
import com.lowagie.text.Paragraph;
import com.lowagie.text.Phrase;
import com.lowagie.text.pdf.BaseFont;
import org.mapfish.print.InvalidValueException;
import org.mapfish.print.PDFUtils;
import org.mapfish.print.RenderingContext;
import org.mapfish.print.utils.PJsonObject;

import java.io.IOException;

public class TextBlock extends ParagraphBlock {
    private String font = "Helvetica";
    private float fontSize = 12;
    private String text = "";

    protected void fillParagraph(RenderingContext context, PJsonObject params, Paragraph paragraph) throws DocumentException {
        final Font pdfFont;
        try {
            pdfFont = new Font(BaseFont.createFont(font, BaseFont.CP1252, BaseFont.EMBEDDED), fontSize);
            paragraph.setFont(pdfFont);
        } catch (IOException e) {
            throw new InvalidValueException("font", font, e);
        }

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

    public String getText() {
        return text;
    }
}
