package org.mapfish.print;

import com.lowagie.text.*;
import com.lowagie.text.pdf.PdfPCell;
import com.lowagie.text.pdf.PdfPTable;
import org.mapfish.print.config.layout.Block;
import org.mapfish.print.config.layout.MapBlock;
import org.mapfish.print.utils.PJsonObject;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.net.URI;
import java.io.IOException;

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
                if (varName.equals("pageTot")) {
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

    private static Pattern FORMAT_PATTERN = Pattern.compile("^format\\s+(%[-+# 0,(]*\\d*(\\.\\d*)?(d))\\s+(.*)$");

    private static String getContextValue(RenderingContext context, PJsonObject params, String key) {
        Matcher matcher;
        if (key.equals("pageNum")) {
            return Integer.toString(context.getWriter().getPageNumber());
        } else if (key.equals("now")) {
            return new Date().toString();
        } else if (key.startsWith("now ")) {
            return formatTime(context, key);
        } else if ((matcher = FORMAT_PATTERN.matcher(key)) != null && matcher.matches()) {
            return format(context, params, matcher);
        } else if (key.equals("configDir")) {
            return context.getConfigDir();
        }

        String result = context.getGlobalParams().optString(key);
        if (result == null) {
            result = params.getString(key);
        }
        return result;
    }

    private static String format(RenderingContext context, PJsonObject params, Matcher matcher) {
        final String valueTxt = getContextValue(context, params, matcher.group(4));
        final Object value;
        switch (matcher.group(3).charAt(0)) {
            case 'd':
            case 'o':
            case 'x':
            case 'X':
                value = Long.valueOf(valueTxt);
                break;
            case 'e':
            case 'E':
            case 'f':
            case 'g':
            case 'G':
            case 'a':
            case 'A':
                value = Double.valueOf(valueTxt);
                break;
            default:
                value = valueTxt;
        }
        try {
            return String.format(matcher.group(1), value);
        } catch (RuntimeException e) {
            // gracefuly fallback to the standard format
            context.addError(e);
            return valueTxt;
        }
    }

    private static String formatTime(RenderingContext context, String key) {
        try {
            SimpleDateFormat format = new SimpleDateFormat(key.substring(4));
            return format.format(new Date());
        } catch (IllegalArgumentException e) {
            // gracefuly fallback to the standard format
            context.addError(e);
            return new Date().toString();
        }
    }

    public static PdfPTable buildTable(ArrayList<Block> items, PJsonObject params, RenderingContext context, int nbColumns) throws DocumentException {
        final PdfPTable table = new PdfPTable(nbColumns > 0 ? nbColumns : items.size());
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
                cell[0].setHorizontalAlignment(block.getAlign().getCode());
                cell[0].setVerticalAlignment(block.getVertAlign().getCode());
                if (!(block instanceof MapBlock) && block.getBackgroundColor() != null) {
                    cell[0].setBackgroundColor(block.getBackgroundColor());
                }
            }
        }, context);
        return cell[0];
    }

    public static void addImage(float maxWidth, float maxHeight, Paragraph target, URI url, float rotation) throws BadElementException {
        final Image image;
        try {
            image = Image.getInstance(url.toString());
        } catch (IOException e) {
            throw new InvalidValueException("url", url.toString(), e);
        }

        if (maxWidth != 0.0f || maxHeight != 0.0f) {
            image.scaleToFit(maxWidth != 0.0f ? maxWidth : Integer.MAX_VALUE, maxHeight != 0.0f ? maxHeight : Integer.MAX_VALUE);
        }

        if (rotation != 0.0F) {
            image.setRotation(rotation);
        }

        Chunk chunk = new Chunk(image, 0f, 0f, true);
        target.add(chunk);
    }
}
