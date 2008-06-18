package org.mapfish.print;

import com.lowagie.text.*;
import com.lowagie.text.Image;
import com.lowagie.text.pdf.PdfPCell;
import com.lowagie.text.pdf.PdfPTable;
import org.mapfish.print.config.layout.Block;
import org.mapfish.print.utils.PJsonObject;

import java.awt.*;
import java.util.ArrayList;
import java.util.Date;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class PDFUtils {
    public static Image createEmptyImage(float width, float height) throws BadElementException {
        Image background = Image.getInstance(1, 1, 1, 1, new byte[]{0}, new int[]{0, 0});
        background.scaleAbsolute(width, height);
        return background;
    }

    private static final Pattern VAR_REGEXP = Pattern.compile("\\$\\{([^}]+)\\}");

    public static Phrase renderString(RenderingContext context, PJsonObject params, String val, com.lowagie.text.Font font) throws BadElementException {
        Phrase result = new Phrase();
        while (true) {
            Matcher matcher = VAR_REGEXP.matcher(val);
            if (matcher.find()) {
                result.add(val.substring(0, matcher.start()));
                final String value;
                final String varName = matcher.group(1);
                if(varName.equals("pageTot")) {
                    result.add(context.getCustomBlocks().getOrCreateTotalPagesBlock(font));
                } else {
                    value = getContextValue(context, params, varName);
                    result.add(value);
                }
                val = val.substring(matcher.end());
            } else {
                break;
            }
        }
        result.add(val);
        return result;
    }

    public static String evalString(RenderingContext context, PJsonObject params, String val) {
        if (val == null) {
            return null;
        }
        StringBuilder result = new StringBuilder();
        while (true) {
            Matcher matcher = VAR_REGEXP.matcher(val);
            if (matcher.find()) {
                result.append(val.substring(0, matcher.start()));
                result.append(getContextValue(context, params, matcher.group(1)));
                val = val.substring(matcher.end());
            } else {
                break;
            }
        }
        result.append(val);
        return result.toString();
    }

    private static String getContextValue(RenderingContext context, PJsonObject params, String key) {
        //TODO: add support for pageTot (tricky)
        if (key.equals("pageNum")) {
            return Integer.toString(context.getWriter().getPageNumber());
        } else if (key.equals("now")) {
            return new Date().toString();
        } else if (key.equals("configDir")) {
            return context.getConfigDir();
        }

        String result = context.getGlobalParams().optString(key);
        if (result == null) {
            result = params.getString(key);
        }
        return result;
    }

    public static PdfPTable buildTable(ArrayList<Block> items, PJsonObject params, RenderingContext context) throws DocumentException {
        final PdfPTable table = new PdfPTable(items.size());
        table.setWidthPercentage(100f);

        for (int i = 0; i < items.size(); i++) {
            final Block block = items.get(i);
            final PdfPCell cell = createCell(params, context, block);
            table.addCell(cell);
        }
        return table;
    }

    public static PdfPCell createCell(PJsonObject params, RenderingContext context, final Block block) throws DocumentException {
        final PdfPCell[] cell = new PdfPCell[1];
        block.render(params, new Block.PdfElement() {
            public void add(Element element) throws DocumentException {
                if (element instanceof PdfPTable) {
                    cell[0] = new PdfPCell((PdfPTable) element);
                } else {
                    final Phrase phrase = new Phrase();
                    phrase.add(element);
                    cell[0] = new PdfPCell(phrase);
                }
                cell[0].setBorder(PdfPCell.NO_BORDER);
                cell[0].setHorizontalAlignment(block.getAlignInt());
                cell[0].setVerticalAlignment(block.getVAlignInt());
                if (block.getBackgroundColor() != null) {
                    cell[0].setBackgroundColor(convertColor("backgroundColor", block.getBackgroundColor()));
                }
            }
        }, context);
        return cell[0];
    }


    private static final Pattern COLOR_RGB = Pattern.compile("#([\\dABCDEF]{2})([\\dABCDEF]{2})([\\dABCDEF]{2})", Pattern.CASE_INSENSITIVE);

    public static Color convertColor(String name, String color) {
        Matcher rgb = COLOR_RGB.matcher(color);
        if (rgb.matches()) {
            return new Color(Integer.valueOf(rgb.group(1), 16),
                    Integer.valueOf(rgb.group(2), 16),
                    Integer.valueOf(rgb.group(3), 16));
        }

        throw new InvalidValueException(name, color);
    }
}
