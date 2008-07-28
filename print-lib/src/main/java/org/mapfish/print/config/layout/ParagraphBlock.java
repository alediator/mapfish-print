package org.mapfish.print.config.layout;

import com.lowagie.text.DocumentException;
import com.lowagie.text.Paragraph;
import org.mapfish.print.RenderingContext;
import org.mapfish.print.utils.PJsonObject;

public abstract class ParagraphBlock extends Block {
    private int spacingAfter = 0;

    public void render(PJsonObject params, PdfElement target, final RenderingContext context) throws DocumentException {
        Paragraph paragraph = new Paragraph();

        fillParagraph(context, params, paragraph);
        paragraph.setAlignment(getAlign().getCode());
        paragraph.setSpacingAfter(spacingAfter);
        target.add(paragraph);
    }

    /**
     * Virtual method that will fill the paragraph.
     */
    protected abstract void fillParagraph(RenderingContext context, PJsonObject params, Paragraph paragraph) throws DocumentException;

    public void setSpacingAfter(int spacingAfter) {
        this.spacingAfter = spacingAfter;
    }

}