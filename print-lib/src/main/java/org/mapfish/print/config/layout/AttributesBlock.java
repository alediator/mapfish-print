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

    public void render(PJsonObject params, PdfElement target, RenderingContext context) throws DocumentException {
        PJsonArray data = params.getJSONArray(source);
        if (data.size() == 0) {
            return;
        }
        PJsonObject firstLine = data.getJSONObject(0);

        final PdfPTable table = new PdfPTable(firstLine.size());
        table.setWidthPercentage(100f);

        Iterator<String> cols = firstLine.keys();
        while (cols.hasNext()) {
            String name = cols.next();
            ColumnDef colDef = columnDefs.get(name);
            if (colDef == null) {
                throw new InvalidJsonValueException(firstLine, "key", name);
            }
            table.addCell(colDef.createHeaderPdfCell(params, context));
        }
        table.setHeaderRows(1);

        for (int rowNum = 0; rowNum < data.size(); ++rowNum) {
            PJsonObject row = data.getJSONObject(rowNum);
            cols = firstLine.keys();
            while (cols.hasNext()) {
                String name = cols.next();
                ColumnDef colDef = columnDefs.get(name);
                if (colDef == null) {
                    throw new InvalidJsonValueException(firstLine, "key", name);
                }
                table.addCell(colDef.createContentPdfCell(row, context));
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
}
