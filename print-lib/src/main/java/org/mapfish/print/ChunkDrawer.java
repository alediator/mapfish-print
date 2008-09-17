package org.mapfish.print;

import com.lowagie.text.pdf.PdfPTableEvent;
import com.lowagie.text.pdf.PdfPTable;
import com.lowagie.text.pdf.PdfContentByte;
import com.lowagie.text.Rectangle;
import com.lowagie.text.DocumentException;

import java.util.List;
import java.util.ArrayList;

/**
     * Base class for the chunk drawers
 */
public abstract class ChunkDrawer implements PdfPTableEvent {
    private final List<PDFCustomBlocks.AbsoluteDrawer> others = new ArrayList<PDFCustomBlocks.AbsoluteDrawer>();
    private final PDFCustomBlocks customBlocks;

    public ChunkDrawer(PDFCustomBlocks customBlocks) {
        this.customBlocks = customBlocks;
    }

    public void tableLayout(PdfPTable table, float widths[][], float heights[], int headerRows, int rowStart, PdfContentByte[] canvases) {
        PdfContentByte dc = canvases[PdfPTable.LINECANVAS];
        Rectangle rect = new Rectangle(widths[0][0], heights[1], widths[0][1], heights[0]);
        render(rect, dc);
    }

    public final void render(Rectangle rectangle, PdfContentByte dc) {
        customBlocks.blockRendered(this);

        renderImpl(rectangle, dc);
        for (int i = 0; i < others.size(); i++) {
            PDFCustomBlocks.AbsoluteDrawer absoluteDrawer = others.get(i);
            try {
                absoluteDrawer.render(dc);
            } catch (DocumentException e) {
                customBlocks.addError(e);
            }
        }
    }

    public abstract void renderImpl(Rectangle rectangle, PdfContentByte dc);

    public void addAbsoluteDrawer(PDFCustomBlocks.AbsoluteDrawer chunkDrawer) {
        others.add(chunkDrawer);
    }
}
