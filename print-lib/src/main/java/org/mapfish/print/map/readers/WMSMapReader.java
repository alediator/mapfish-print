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

public class WMSMapReader extends HTTPMapReader {
    private final List<String> layers = new ArrayList<String>();

    private final String format;

    private final List<String> styles = new ArrayList<String>();

    private WMSMapReader(String layer, String style, RenderingContext context, PJsonObject params) {
        super(context, params);
        layers.add(layer);
        styles.add(style);
        format = params.getString("format");
    }

    protected MapRenderer.Format addQueryParams(Map<String, List<String>> result, Transformer transformer, String srs, boolean first) {
        URIUtils.addParamOverride(result, "FORMAT", format);
        final MapRenderer.Format type;
        if (format.equals("image/svg+xml")) {
            URIUtils.addParamOverride(result, "WIDTH", Integer.toString(transformer.getSvgW()));
            URIUtils.addParamOverride(result, "HEIGHT", Integer.toString(transformer.getSvgH()));
            type = MapRenderer.Format.SVG;
        } else {
            URIUtils.addParamOverride(result, "WIDTH", Integer.toString(transformer.getBitmapW()));
            URIUtils.addParamOverride(result, "HEIGHT", Integer.toString(transformer.getBitmapH()));
            type = format.equals("application/x-pdf") ? MapRenderer.Format.PDF : MapRenderer.Format.BITMAP;
        }
        URIUtils.addParamOverride(result, "LAYERS", StringUtils.join(layers, ","));
        URIUtils.addParamOverride(result, "SRS", srs);
        URIUtils.addParamOverride(result, "SERVICE", "WMS");
        URIUtils.addParamOverride(result, "REQUEST", "GetMap");
        //URIUtils.addParamOverride(result, "EXCEPTIONS", "application/vnd.ogc.se_inimage");
        URIUtils.addParamOverride(result, "VERSION", "1.1.1");
        URIUtils.addParamOverride(result, "BBOX", String.format("%f,%f,%f,%f", transformer.minGeoX, transformer.minGeoY, transformer.maxGeoX, transformer.maxGeoY));
        if (!first) {
            URIUtils.addParamOverride(result, "TRANSPARENT", "true");
        }
        URIUtils.addParamOverride(result, "STYLES", StringUtils.join(styles, ","));
        return type;
    }

    protected static void create(List<MapReader> target, RenderingContext context, PJsonObject params) {
        PJsonArray layers = params.getJSONArray("layers");
        PJsonArray styles = params.optJSONArray("styles");
        for (int i = 0; i < layers.size(); i++) {
            String layer = layers.getString(i);
            String style = "";
            if (styles != null && i < styles.size()) {
                style = styles.getString(i);
            }
            target.add(new WMSMapReader(layer, style, context, params));
        }
    }

    public boolean testMerge(MapReader other) {
        if (canMerge(other)) {
            WMSMapReader wms = (WMSMapReader) other;
            layers.addAll(wms.layers);
            styles.addAll(wms.styles);
            return true;
        } else {
            return false;
        }
    }

    public boolean canMerge(MapReader other) {
        if (!super.canMerge(other)) {
            return false;
        }

        if (other instanceof WMSMapReader) {
            WMSMapReader wms = (WMSMapReader) other;
            return format.equals(wms.format);
        } else {
            return false;
        }
    }
}
