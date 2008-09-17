package org.mapfish.print.config.layout;

import com.lowagie.text.DocumentException;
import com.lowagie.text.Rectangle;
import com.lowagie.text.pdf.PdfContentByte;
import com.lowagie.text.pdf.PdfPTable;
import org.mapfish.print.PDFUtils;
import org.mapfish.print.RenderingContext;
import org.mapfish.print.utils.PJsonObject;

import java.util.ArrayList;

/**
 * Config and logic to render a header or a footer.
 */
public class HeaderFooter {
    private int height = 0;
    private ArrayList<Block> items = new ArrayList<Block>();

    public void setHeight(int height) {
        this.height = height;
    }

    public void setItems(ArrayList<Block> items) {
        this.items = items;
    }

    public int getHeight() {
        return height;
    }

    public void render(final Rectangle rectangle, PdfContentByte dc, PJsonObject params, RenderingContext context) {
        try {
            final PdfPTable table = PDFUtils.buildTable(items, params, context, 1/*multiple items are arranged by lines*/, null);

            table.setTotalWidth(rectangle.getWidth());
            table.writeSelectedRows(0, -1, rectangle.getLeft(), rectangle.getTop(), dc);
        } catch (DocumentException e) {
            context.addError(e);
        }
    }
}
