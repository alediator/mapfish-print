/*
 * Copyright (C) 2008 Camptocamp
 *
 * This file is part of MapFish Server
 *
 * MapFish Server is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Lesser General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * MapFish Server is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public License
 * along with MapFish Server.  If not, see <http://www.gnu.org/licenses/>.
 */

package org.mapfish.print.map.renderers.vector;

import org.mapfish.geo.MfGeo;
import org.mapfish.geo.MfFeatureCollection;
import org.mapfish.geo.MfFeature;
import org.mapfish.geo.MfGeometry;
import org.mapfish.print.RenderingContext;
import com.lowagie.text.pdf.PdfContentByte;

import java.util.Map;
import java.util.HashMap;
import java.awt.geom.AffineTransform;

/**
 * iText renderer for MF geoJSON features.
 */
public abstract class FeaturesRenderer<T extends MfGeo> {
    private static final Map<Class<?>, FeaturesRenderer<?>> RENDERERS = new HashMap<Class<?>, FeaturesRenderer<?>>();

    static {
        RENDERERS.put(StyledMfFeature.class, new FeatureRenderer());
        RENDERERS.put(MfFeatureCollection.class, new FeatureCollectionRenderer());
        RENDERERS.put(MfGeometry.class, new GeometryRenderer());
    }

    @SuppressWarnings({"RawUseOfParameterizedType", "unchecked"})
    public static void render(RenderingContext context, PdfContentByte dc, AffineTransform transform, MfGeo geo) {
        FeaturesRenderer renderer = RENDERERS.get(geo.getClass());
        if (renderer == null) {
            throw new RuntimeException("Rendering of " + geo.getClass().getName() + " not supported");
        }
        renderer.renderImpl(context, dc, transform, geo);
    }

    protected abstract void renderImpl(RenderingContext context, PdfContentByte dc, AffineTransform transform, T geo);

    private static class FeatureRenderer extends FeaturesRenderer<StyledMfFeature> {
        protected void renderImpl(RenderingContext context, PdfContentByte dc, AffineTransform transform, StyledMfFeature geo) {
            final MfGeometry theGeom = geo.getMfGeometry();
            if (theGeom != null && geo.isDisplayed()) {
                GeometriesRenderer.render(context, dc, transform, geo.getStyle(), theGeom.getInternalGeometry());
            }
        }
    }

    private static class FeatureCollectionRenderer extends FeaturesRenderer<MfFeatureCollection> {
        protected void renderImpl(RenderingContext context, PdfContentByte dc, AffineTransform transform, MfFeatureCollection geo) {
            for (MfFeature cur : geo.getCollection()) {
                render(context, dc, transform, cur);
            }
        }
    }

    private static class GeometryRenderer extends FeaturesRenderer<MfGeometry> {
        protected void renderImpl(RenderingContext context, PdfContentByte dc, AffineTransform transform, MfGeometry geo) {
            GeometriesRenderer.render(context, dc, transform, null, geo.getInternalGeometry());
        }
    }
}
