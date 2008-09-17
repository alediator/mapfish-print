package org.mapfish.print;

import com.lowagie.text.*;
import com.lowagie.text.pdf.BaseFont;
import com.lowagie.text.pdf.PdfContentByte;
import com.lowagie.text.pdf.PdfTemplate;
import com.lowagie.text.pdf.PdfWriter;

/**
 * A class for handling the tricky task of displaying the total number of pages
 * on each page of the document.
 */
public class TotalPageNum {
    private static final String SAMPLE_VALUE = "999";

    private PdfTemplate totalPageNum;
    private final BaseFont totalPageNumFont;
    private final float totalPageNumFontSize;
    private PdfContentByte dc;

    public TotalPageNum(PdfWriter writer, Font font) {
        dc = writer.getDirectContent();
        totalPageNumFont = font.getCalculatedBaseFont(false);
        totalPageNumFontSize = font.getSize();
    }

    public Chunk createPlaceHolder() throws BadElementException {
        float width = totalPageNumFont.getWidthPoint(SAMPLE_VALUE, totalPageNumFontSize);
        float height = totalPageNumFont.getAscentPoint(SAMPLE_VALUE, totalPageNumFontSize) -
                totalPageNumFont.getDescentPoint(SAMPLE_VALUE, totalPageNumFontSize);
        if (totalPageNum == null) {
            totalPageNum = dc.createTemplate(width, height);
        }

        Image image = Image.getInstance(totalPageNum);
        return new Chunk(image, 0, 0, true);

    }

    public void render(PdfWriter writer) {
        totalPageNum.beginText();
        totalPageNum.setFontAndSize(totalPageNumFont, totalPageNumFontSize);
        totalPageNum.setTextMatrix(0, 0);
        totalPageNum.showText(String.valueOf(writer.getPageNumber() - 1));
        totalPageNum.endText();
    }
}
