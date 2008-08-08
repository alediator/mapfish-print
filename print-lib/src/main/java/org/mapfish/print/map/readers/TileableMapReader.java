package org.mapfish.print.map.readers;

import com.lowagie.text.pdf.PdfContentByte;
import org.mapfish.print.RenderingContext;
import org.mapfish.print.Transformer;
import org.mapfish.print.map.renderers.MapRenderer;
import org.mapfish.print.utils.PJsonObject;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.List;

public abstract class TileableMapReader extends HTTPMapReader {

    protected TileCacheLayerInfo tileCacheLayerInfo;

    protected TileableMapReader(RenderingContext context, PJsonObject params) {
        super(context, params);
    }

    protected void renderTiles(MapRenderer formater, Transformer transformer, URI commonUri, PdfContentByte dc) throws IOException, URISyntaxException {
        final List<URI> urls = new ArrayList<URI>(1);
        final float offsetX;
        final float offsetY;
        final long bitmapTileW;
        final long bitmapTileH;
        int nbTilesW = 0;

        float minGeoX = transformer.getRotatedMinGeoX();
        float minGeoY = transformer.getRotatedMinGeoY();
        float maxGeoX = transformer.getRotatedMaxGeoX();
        float maxGeoY = transformer.getRotatedMaxGeoY();

        if (tileCacheLayerInfo != null) {
            //tiled
            float targetResolution = transformer.getGeoW() / transformer.getStraightBitmapW();
            TileCacheLayerInfo.ResolutionInfo resolution = tileCacheLayerInfo.getNearestResolution(targetResolution);
            bitmapTileW = tileCacheLayerInfo.getWidth();
            bitmapTileH = tileCacheLayerInfo.getHeight();
            final float tileGeoWidth = resolution.value * bitmapTileW;
            final float tileGeoHeight = resolution.value * bitmapTileH;
            float tileMinGeoX = (float) (Math.floor((minGeoX - tileCacheLayerInfo.getMinX()) / tileGeoWidth) * tileGeoWidth) + tileCacheLayerInfo.getMinX();
            float tileMinGeoY = (float) (Math.floor((minGeoY - tileCacheLayerInfo.getMinY()) / tileGeoHeight) * tileGeoHeight) + tileCacheLayerInfo.getMinY();
            offsetX = (minGeoX - tileMinGeoX) / resolution.value;
            offsetY = (minGeoY - tileMinGeoY) / resolution.value;
            for (float geoY = tileMinGeoY; geoY < maxGeoY; geoY += tileGeoHeight) {
                nbTilesW = 0;
                for (float geoX = tileMinGeoX; geoX < maxGeoX; geoX += tileGeoWidth) {
                    nbTilesW++;
                    urls.add(getTileUri(commonUri, transformer, geoX, geoY, geoX + tileGeoWidth, geoY + tileGeoHeight, bitmapTileW, bitmapTileH));
                }
            }

        } else {
            //single tile
            nbTilesW = 1;
            offsetX = 0;
            offsetY = 0;
            bitmapTileW = transformer.getRotatedBitmapW();
            bitmapTileH = transformer.getRotatedBitmapH();
            urls.add(getTileUri(commonUri, transformer, minGeoX, minGeoY, maxGeoX, maxGeoY, bitmapTileW, bitmapTileH));
        }
        formater.render(transformer, urls, dc, context, opacity, nbTilesW, offsetX, offsetY, bitmapTileW, bitmapTileH);
    }

    /**
     * Adds the query parameters for the given tile.
     */
    protected abstract URI getTileUri(URI commonUri, Transformer transformer, float minGeoX, float minGeoY, float maxGeoX, float maxGeoY, long w, long h) throws URISyntaxException, UnsupportedEncodingException;
}
