package org.mapfish.print.map.readers;

import com.lowagie.text.pdf.PdfContentByte;
import org.mapfish.print.InvalidJsonValueException;
import org.mapfish.print.RenderingContext;
import org.mapfish.print.Transformer;
import org.mapfish.print.utils.PJsonObject;

import java.util.List;

public abstract class MapReader {
    protected final float opacity;

    public MapReader(PJsonObject params) {
        opacity = params.optFloat("opacity", 1.0F);
    }

    public abstract void render(Transformer transformer, PdfContentByte dc, String srs, boolean first);

    public static void create(List<MapReader> target, String type, RenderingContext context, PJsonObject params) {
        if ("WMS".equalsIgnoreCase(type)) {
            WMSMapReader.create(target, context, params);
        } else if ("MapServer".equalsIgnoreCase(type)) {
            MapServerMapReader.create(target, context, params);
        } else {
            throw new InvalidJsonValueException(params, "type", type);
        }
    }

    public abstract boolean testMerge(MapReader other);

    public boolean canMerge(MapReader other) {
        return opacity == other.opacity;
    }
}
