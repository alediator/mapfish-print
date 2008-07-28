package org.mapfish.print;

import com.lowagie.text.BadElementException;
import com.lowagie.text.Chunk;
import com.lowagie.text.Font;
import com.lowagie.text.Rectangle;
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

    private final PdfTemplate totalPageNum;
    private final BaseFont totalPageNumFont;
    private final float totalPageNumFontSize;

    public TotalPageNum(PdfWriter writer, Font font) {
        PdfContentByte dc = writer.getDirectContent();
        totalPageNum = dc.createTemplate(100, 100);
        totalPageNumFont = font.getCalculatedBaseFont(false);
        totalPageNumFontSize = font.getSize();
    }

    public Chunk createPlaceHolder(PDFCustomBlocks customBlocks) throws BadElementException {
        float width = totalPageNumFont.getWidthPoint(SAMPLE_VALUE, totalPageNumFontSize);
        float height = totalPageNumFont.getAscentPoint(SAMPLE_VALUE, totalPageNumFontSize) -
                totalPageNumFont.getDescentPoint(SAMPLE_VALUE, totalPageNumFontSize);

        Chunk result = new Chunk(PDFUtils.createEmptyImage(width, height), 0, 0, true);
        customBlocks.addChunkDrawer(result, new PDFCustomBlocks.ChunkDrawer() {
            public void render(Rectangle rectangle, PdfContentByte dc) {
                dc.addTemplate(totalPageNum, rectangle.getLeft(), rectangle.getBottom());
            }
        });
        return result;

    }

    public void render(PdfWriter writer) {
        totalPageNum.beginText();
        totalPageNum.setFontAndSize(totalPageNumFont, totalPageNumFontSize);
        totalPageNum.setTextMatrix(0, 0);
        totalPageNum.showText(String.valueOf(writer.getPageNumber() - 1));
        totalPageNum.endText();
    }
}
