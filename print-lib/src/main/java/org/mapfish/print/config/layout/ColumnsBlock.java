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
    private double spacingAfter = 0.0f;
    private int absoluteX = Integer.MIN_VALUE;
    private int absoluteY = Integer.MIN_VALUE;
    private int width = Integer.MIN_VALUE;
    private int nbColumns = Integer.MIN_VALUE;
    private TableConfig config;

    public void render(final PJsonObject params, PdfElement target, final RenderingContext context) throws DocumentException {

        if (isAbsolute()) {
            context.getCustomBlocks().addAbsoluteDrawer(new PDFCustomBlocks.AbsoluteDrawer() {
                public void render(PdfContentByte dc) throws DocumentException {
                    final PdfPTable table = PDFUtils.buildTable(items, params, context, nbColumns, config);

                    table.setTotalWidth(width);
                    table.setLockedWidth(true);

                    if (widths != null) {
                        table.setWidths(widths);
                    }

                    table.writeSelectedRows(0, -1, absoluteX, absoluteY, dc);
                }
            });
        } else {
            final PdfPTable table = PDFUtils.buildTable(items, params, context, nbColumns, config);

            if (widths != null) {
                table.setWidths(widths);
            }

            table.setSpacingAfter((float) spacingAfter);
            target.add(table);
        }
    }

    public void setItems(ArrayList<Block> items) {
        this.items = items;
    }

    public void setWidths(int[] widths) {
        this.widths = widths;
    }

    public void setSpacingAfter(double spacingAfter) {
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

    public void setNbColumns(int nbColumns) {
        this.nbColumns = nbColumns;
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

    public void setConfig(TableConfig config) {
        this.config = config;
    }
}