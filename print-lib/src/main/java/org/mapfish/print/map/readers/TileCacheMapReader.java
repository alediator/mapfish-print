package org.mapfish.print.map.readers;

import org.mapfish.print.RenderingContext;
import org.mapfish.print.Transformer;
import org.mapfish.print.map.renderers.MapRenderer;
import org.mapfish.print.utils.PJsonArray;
import org.mapfish.print.utils.PJsonObject;

import java.io.UnsupportedEncodingException;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.List;
import java.util.Map;

/**
 * Support for the protocol using directly the content of a TileCache directory.
 */
public class TileCacheMapReader extends TileableMapReader {
    private final String layer;

    private TileCacheMapReader(RenderingContext context, PJsonObject params) {
        super(context, params);
        this.layer = params.getString("layer");
        PJsonArray maxExtent = params.getJSONArray("maxExtent");
        PJsonArray tileSize = params.getJSONArray("tileSize");
        tileCacheLayerInfo = new TileCacheLayerInfo(params.getJSONArray("resolutions"), tileSize.getInt(0), tileSize.getInt(1), maxExtent.getFloat(0), maxExtent.getFloat(1), maxExtent.getFloat(2), maxExtent.getFloat(3), params.getString("extension"));
    }

    protected MapRenderer.Format getFormat() {
        return MapRenderer.Format.BITMAP;
    }

    protected void addCommonQueryParams(Map<String, List<String>> result, Transformer transformer, String srs, boolean first) {
    }

    protected URI getTileUri(URI commonUri, Transformer transformer, float minGeoX, float minGeoY, float maxGeoX, float maxGeoY, long w, long h) throws URISyntaxException, UnsupportedEncodingException {
        float targetResolution = transformer.getGeoW() / transformer.getStraightBitmapW();
        TileCacheLayerInfo.ResolutionInfo resolution = tileCacheLayerInfo.getNearestResolution(targetResolution);

        int tileX = Math.round((minGeoX - tileCacheLayerInfo.getMinX()) / (resolution.value * w));
        int tileY = Math.round((minGeoY - tileCacheLayerInfo.getMinY()) / (resolution.value * h));

        StringBuilder path = new StringBuilder();
        if (!commonUri.getPath().endsWith("/")) {
            path.append('/');
        }
        path.append(layer);
        path.append('/').append(String.format("%02d", resolution.index));
        path.append('/').append(String.format("%03d", (tileX / 1000000) % 1000));
        path.append('/').append(String.format("%03d", (tileX / 1000) % 1000));
        path.append('/').append(String.format("%03d", tileX % 1000));
        path.append('/').append(String.format("%03d", (tileY / 1000000) % 1000));
        path.append('/').append(String.format("%03d", (tileY / 1000) % 1000));
        path.append('/').append(String.format("%03d", tileY % 1000));
        path.append('.').append(tileCacheLayerInfo.getExtension());

        return new URI(commonUri.getScheme(), commonUri.getUserInfo(), commonUri.getHost(), commonUri.getPort(), commonUri.getPath() + path, commonUri.getQuery(), commonUri.getFragment());
    }

    protected static void create(List<MapReader> target, RenderingContext context, PJsonObject params) {
        target.add(new TileCacheMapReader(context, params));
    }

    public boolean testMerge(MapReader other) {
        return false;
    }

    public boolean canMerge(MapReader other) {
        return false;
    }
}