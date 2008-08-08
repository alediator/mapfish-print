package org.mapfish.print.map.renderers;

import com.lowagie.text.pdf.PdfContentByte;
import org.mapfish.print.RenderingContext;
import org.mapfish.print.Transformer;

import java.io.IOException;
import java.net.URI;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public abstract class MapRenderer {
    private static final Map<Format, MapRenderer> renderers = new HashMap<Format, MapRenderer>();

    static {
        renderers.put(Format.BITMAP, new BitmapMapRenderer());
        renderers.put(Format.PDF, new PDFMapRenderer());
        renderers.put(Format.SVG, new SVGMapRenderer());
    }

    public static MapRenderer get(Format format) {
        return renderers.get(format);
    }

    public abstract void render(Transformer transformer, List<URI> urls, PdfContentByte dc, RenderingContext context, float opacity, int nbTilesHorizontal, float offsetX, float offsetY, long bitmapTileW, long bitmapTileH) throws IOException;

    public enum Format {
        BITMAP,
        PDF,
        SVG
    }
}
