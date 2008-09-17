package org.mapfish.print.config.layout;

import com.lowagie.text.DocumentException;
import com.lowagie.text.pdf.PdfPTable;
import org.mapfish.print.InvalidJsonValueException;
import org.mapfish.print.RenderingContext;
import org.mapfish.print.utils.PJsonArray;
import org.mapfish.print.utils.PJsonObject;

import java.util.Iterator;

public class AttributesBlock extends Block {
    private String source;
    private ColumnDefs columnDefs = new ColumnDefs();

    private TableConfig tableConfig = null;

    public void render(PJsonObject params, PdfElement target, RenderingContext context) throws DocumentException {
        PJsonObject sourceJson = params.optJSONObject(source);
        if (sourceJson == null) {
            sourceJson = context.getGlobalParams().optJSONObject(source);
        }
        if (sourceJson == null || sourceJson.size() == 0) {
            return;
        }
        PJsonArray data = sourceJson.optJSONArray("data");
        PJsonArray firstLine = sourceJson.getJSONArray("columns");

        final PdfPTable table = new PdfPTable(firstLine.size());
        table.setWidthPercentage(100f);

        int nbCols = firstLine.size();
        int nbRows = data.size() + 1;
        for (int colNum = 0; colNum < firstLine.size(); ++colNum) {
            String name = firstLine.getString(colNum);
            ColumnDef colDef = columnDefs.get(name);
            if (colDef != null) {
                table.addCell(colDef.createHeaderPdfCell(params, context, colNum, nbRows, nbCols, tableConfig));
            } else {
                //noinspection ThrowableInstanceNeverThrown
                context.addError(new InvalidJsonValueException(firstLine, name, "Unknown column"));
            }
        }
        table.setHeaderRows(1);

        for (int rowNum = 0; rowNum < data.size(); ++rowNum) {
            PJsonObject row = data.getJSONObject(rowNum);
            for (int colNum = 0; colNum < firstLine.size(); ++colNum) {
                String name = firstLine.getString(colNum);
                ColumnDef colDef = columnDefs.get(name);
                if (colDef != null) {
                    table.addCell(colDef.createContentPdfCell(row, context, rowNum + 1, colNum, nbRows, nbCols, tableConfig));
                }
            }
        }
        target.add(table);
    }

    public void setSource(String source) {
        this.source = source;
    }

    public void setColumnDefs(ColumnDefs columnDefs) {
        this.columnDefs = columnDefs;
    }

    public void setTableConfig(TableConfig tableConfig) {
        this.tableConfig = tableConfig;
    }
}
