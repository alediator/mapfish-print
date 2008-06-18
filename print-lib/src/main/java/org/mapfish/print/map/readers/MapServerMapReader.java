package org.mapfish.print.map.readers;

import org.mapfish.print.RenderingContext;
import org.mapfish.print.Transformer;
import org.mapfish.print.map.renderers.MapRenderer;
import org.mapfish.print.utils.PJsonArray;
import org.mapfish.print.utils.PJsonObject;
import org.pvalsecc.misc.StringUtils;
import org.pvalsecc.misc.URIUtils;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class MapServerMapReader extends HTTPMapReader {
    private final List<String> layers = new ArrayList<String>();

    private final String format;

    private MapServerMapReader(String layer, RenderingContext context, PJsonObject params) {
        super(context, params);
        layers.add(layer);
        format = params.getString("format");
    }

    protected MapRenderer.Format addQueryParams(Map<String, List<String>> result, Transformer transformer, String srs, boolean first) {
        final MapRenderer.Format type;
        final int w;
        final int h;
        if (format.equals("image/svg+xml")) {
            URIUtils.addParamOverride(result, "map_imagetype", "svg");
            w = transformer.getSvgW();
            h = transformer.getSvgH();
            type = MapRenderer.Format.SVG;
        } else if (format.equals("application/x-pdf")) {
            URIUtils.addParamOverride(result, "MAP_IMAGETYPE", "pdf");
            w = transformer.getBitmapW();
            h = transformer.getBitmapH();
            type = MapRenderer.Format.PDF;

        } else {
            URIUtils.addParamOverride(result, "MAP_IMAGETYPE", "png");
            w = transformer.getBitmapW();
            h = transformer.getBitmapH();
            type = MapRenderer.Format.BITMAP;
        }
        URIUtils.addParamOverride(result, "MODE", "map");
        URIUtils.addParamOverride(result, "LAYERS", StringUtils.join(layers, " "));
        //URIUtils.addParamOverride(result, "SRS", srs);
        URIUtils.addParamOverride(result, "MAP_SIZE", String.format("%d %d", w, h));
        URIUtils.addParamOverride(result, "MAPEXT", String.format("%f %f %f %f", transformer.minGeoX, transformer.minGeoY, transformer.maxGeoX, transformer.maxGeoY));
        if (!first) {
            URIUtils.addParamOverride(result, "TRANSPARENT", "true");
        }
        return type;
    }

    protected static void create(List<MapReader> target, RenderingContext context, PJsonObject params) {
        PJsonArray layers = params.getJSONArray("layers");
        for (int i = 0; i < layers.size(); i++) {
            String layer = layers.getString(i);
            target.add(new MapServerMapReader(layer, context, params));
        }
    }

    public boolean testMerge(MapReader other) {
        if (canMerge(other)) {
            MapServerMapReader wms = (MapServerMapReader) other;
            layers.addAll(wms.layers);
            return true;
        }
        return false;
    }

    public boolean canMerge(MapReader other) {
        if (!super.canMerge(other)) {
            return false;
        }

        if (other instanceof MapServerMapReader) {
            MapServerMapReader wms = (MapServerMapReader) other;
            return format.equals(wms.format);
        } else {
            return false;
        }
    }
}