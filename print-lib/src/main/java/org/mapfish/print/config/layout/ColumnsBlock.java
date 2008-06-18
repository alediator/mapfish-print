package org.mapfish.print.config.layout;

import com.lowagie.text.DocumentException;
import com.lowagie.text.pdf.PdfContentByte;
import com.lowagie.text.pdf.PdfPTable;
import org.mapfish.print.PDFCustomBlocks;
import org.mapfish.print.PDFUtils;
import org.mapfish.print.RenderingContext;
import org.mapfish.print.utils.PJsonObject;

import java.util.ArrayList;

public class ColumnsBlock extends Block {
    private ArrayList<Block> items = new ArrayList<Block>();
    private int[] widths = null;
    private float spacingAfter = 0.0f;
    private int absoluteX = Integer.MIN_VALUE;
    private int absoluteY = Integer.MIN_VALUE;
    private int width = Integer.MIN_VALUE;

    public void render(PJsonObject params, PdfElement target, RenderingContext context) throws DocumentException {

        final PdfPTable table = PDFUtils.buildTable(items, params, context);

        if (widths != null) {
            table.setWidths(widths);
        }

        if (isAbsolute()) {
            table.setTotalWidth(width);
            context.getCustomBlocks().addAbsoluteDrawer(new PDFCustomBlocks.AbsoluteDrawer() {
                public void render(PdfContentByte dc) {
                    table.writeSelectedRows(0, -1, absoluteX, absoluteY, dc);
                }
            });
        } else {
            table.setSpacingAfter(spacingAfter);
            target.add(table);
        }
    }

    public void setItems(ArrayList<Block> items) {
        this.items = items;
    }

    public void setWidths(int[] widths) {
        this.widths = widths;
    }

    public void setSpacingAfter(float spacingAfter) {
        this.spacingAfter = spacingAfter;
    }

    public void setAbsoluteX(int absoluteX) {
        this.absoluteX = absoluteX;
    }

    public void setAbsoluteY(int absoluteY) {
        this.absoluteY = absoluteY;
    }

    public void setWidth(int width) {
        this.width = width;
    }

    protected boolean isAbsolute() {
        return absoluteX != Integer.MIN_VALUE &&
                absoluteY != Integer.MIN_VALUE &&
                width != Integer.MIN_VALUE;
    }

    public MapBlock getMap() {
        for (Block item : items) {
            MapBlock result = item.getMap();
            if (result != null) {
                return result;
            }
        }
        return null;
    }
}