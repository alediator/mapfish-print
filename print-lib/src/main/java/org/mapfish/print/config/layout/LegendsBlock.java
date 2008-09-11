package org.mapfish.print.config.layout;

import org.mapfish.print.RenderingContext;
import org.mapfish.print.PDFUtils;
import org.mapfish.print.utils.PJsonObject;
import org.mapfish.print.utils.PJsonArray;
import com.lowagie.text.*;
import com.lowagie.text.pdf.PdfPTable;
import com.lowagie.text.pdf.PdfPCell;
import com.lowagie.text.pdf.BaseFont;

import java.net.URI;

public class LegendsBlock extends Block {
    private float maxIconWidth = 8f;
    private float maxIconHeight = 8f;
    private float classIndentation = 20f;
    private float layerSpace = 5f;
    private float classSpace = 2f;

    private String layerFont = "Helvetica";
    protected float layerFontSize = 10f;
    private String classFont = "Helvetica";
    protected float classFontSize = 8f;
    private String fontEncoding = BaseFont.WINANSI;

    public void render(PJsonObject params, PdfElement target, RenderingContext context) throws DocumentException {
        PdfPTable table = new PdfPTable(1);
        table.setWidthPercentage(100f);

        Font layerPdfFont = getLayerPdfFont();
        Font classPdfFont = getClassPdfFont();

        PJsonArray legends = context.getGlobalParams().optJSONArray("legends");
        if (legends != null && legends.size() > 0) {
            for (int i = 0; i < legends.size(); ++i) {
                PJsonObject layer = legends.getJSONObject(i);
                final PdfPCell cell = createLine(0f, layer, layerPdfFont);
                if (i > 0) {
                    cell.setPaddingTop(layerSpace);
                }
                table.addCell(cell);

                PJsonArray classes = layer.getJSONArray("classes");
                for (int j = 0; j < classes.size(); ++j) {
                    PJsonObject clazz = classes.getJSONObject(j);
                    final PdfPCell classCell = createLine(classIndentation, clazz, classPdfFont);
                    classCell.setPaddingTop(classSpace);
                    table.addCell(classCell);
                }
            }
        }

        target.add(table);
    }

    private PdfPCell createLine(float indent, PJsonObject node, Font pdfFont) throws BadElementException {
        final String name = node.getString("name");
        final String icon = node.optString("icon");

        final Paragraph result = new Paragraph();
        result.setFont(pdfFont);
        if (icon != null) {
            PDFUtils.addImage(maxIconWidth, maxIconHeight, result, URI.create(icon), 0.0f);
            result.add(" ");
        }
        result.add(name);

        final PdfPCell cell = new PdfPCell(result);
        cell.setBorder(PdfPCell.NO_BORDER);
        cell.setPadding(0f);
        cell.setPaddingLeft(indent);

        if (getBackgroundColor() != null) {
            cell.setBackgroundColor(getBackgroundColor());
        }

        return cell;
    }

    public void setMaxIconWidth(float maxIconWidth) {
        this.maxIconWidth = maxIconWidth;
    }

    public void setMaxIconHeight(float maxIconHeight) {
        this.maxIconHeight = maxIconHeight;
    }

    public void setClassIndentation(float classIndentation) {
        this.classIndentation = classIndentation;
    }

    public void setClassFont(String classFont) {
        this.classFont = classFont;
    }

    public void setClassFontSize(float classFontSize) {
        this.classFontSize = classFontSize;
    }

    public String getClassFont() {
        return classFont;
    }

    protected Font getLayerPdfFont() {
        return FontFactory.getFont(layerFont, fontEncoding, layerFontSize);
    }

    protected Font getClassPdfFont() {
        return FontFactory.getFont(classFont, fontEncoding, classFontSize);
    }

    public void setLayerSpace(float layerSpace) {
        this.layerSpace = layerSpace;
    }

    public void setClassSpace(float classSpace) {
        this.classSpace = classSpace;
    }

    public void setLayerFont(String layerFont) {
        this.layerFont = layerFont;
    }

    public void setLayerFontSize(float layerFontSize) {
        this.layerFontSize = layerFontSize;
    }

    public void setFontEncoding(String fontEncoding) {
        this.fontEncoding = fontEncoding;
    }
}
