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

public class TextBlock extends ParagraphFontBlock {
    private String text = "";

    protected void fillParagraph(RenderingContext context, PJsonObject params, Paragraph paragraph) throws DocumentException {
        final Font pdfFont;
        pdfFont = getPdfFont();
        paragraph.setFont(pdfFont);

        final Phrase text = PDFUtils.renderString(context, params, this.text, pdfFont);
        paragraph.add(text);
    }

    public void setText(String text) {
        this.text = text;
    }

    public String getText() {
        return text;
    }
}
