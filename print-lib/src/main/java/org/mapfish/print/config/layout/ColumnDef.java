package org.mapfish.print.config.layout;

import com.lowagie.text.DocumentException;
import com.lowagie.text.pdf.PdfPCell;
import org.mapfish.print.PDFUtils;
import org.mapfish.print.RenderingContext;
import org.mapfish.print.utils.PJsonObject;

public class ColumnDef {
    private Block header;
    private Block cell;

    public void setHeader(Block header) {
        this.header = header;
    }

    public void setCell(Block cell) {
        this.cell = cell;
    }

    public PdfPCell createHeaderPdfCell(PJsonObject params, RenderingContext context, int col, int nbRows, int nbCols, TableConfig config) throws DocumentException {
        return PDFUtils.createCell(params, context, header, 0, col, nbRows, nbCols, config);
    }

    public PdfPCell createContentPdfCell(PJsonObject params, RenderingContext context, int row, int col, int nbRows, int nbCols, TableConfig config) throws DocumentException {
        return PDFUtils.createCell(params, context, cell, row, col, nbRows, nbCols, config);
    }
}
