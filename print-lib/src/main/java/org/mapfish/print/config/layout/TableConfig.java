package org.mapfish.print.config.layout;

import com.lowagie.text.pdf.PdfPCell;

public class TableConfig extends BorderConfig {
    private Exceptions cells;

    public void apply(PdfPCell cell, int row, int col, int nbRows, int nbCols) {
        if (cells != null) {
            for (int i = 0; i < cells.size(); i++) {
                CellException cellException = cells.get(i);
                if (cellException.matches(row, col)) {
                    cellException.apply(cell);
                }
            }
        }

        if (row == 0) {
            if (borderWidthTop != null)
                cell.setBorderWidthTop(borderWidthTop.floatValue());
            if (borderColorTop != null) cell.setBorderColorTop(borderColorTop);
        }

        if (col == 0) {
            if (borderWidthLeft != null)
                cell.setBorderWidthLeft(borderWidthLeft.floatValue());
            if (borderColorLeft != null)
                cell.setBorderColorLeft(borderColorLeft);
        }

        if (row == nbRows - 1) {
            if (borderWidthBottom != null)
                cell.setBorderWidthBottom(borderWidthBottom.floatValue());
            if (borderColorBottom != null)
                cell.setBorderColorBottom(borderColorBottom);
        }

        if (col == nbCols - 1) {
            if (borderWidthRight != null)
                cell.setBorderWidthRight(borderWidthRight.floatValue());
            if (borderColorRight != null)
                cell.setBorderColorRight(borderColorRight);
        }

    }

    public void setCells(Exceptions cells) {
        this.cells = cells;
    }
}
