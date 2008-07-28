package org.mapfish.print.map.readers;

import com.lowagie.text.pdf.PdfContentByte;
import org.apache.log4j.Logger;
import org.mapfish.print.InvalidJsonValueException;
import org.mapfish.print.RenderingContext;
import org.mapfish.print.Transformer;
import org.mapfish.print.map.renderers.MapRenderer;
import org.mapfish.print.utils.PJsonObject;
import org.pvalsecc.misc.MatchAllSet;
import org.pvalsecc.misc.URIUtils;

import java.net.URI;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

public abstract class HTTPMapReader extends MapReader {
    public static final Logger LOGGER = Logger.getLogger(HTTPMapReader.class);

    private final RenderingContext context;
    protected final PJsonObject params;
    protected final URI baseUrl;
    private static final Set<String> OVERRIDE_ALL = new MatchAllSet<String>();

    protected HTTPMapReader(RenderingContext context, PJsonObject params) {
        super(params);
        this.context = context;
        this.params = params;
        try {
            baseUrl = new URI(params.getString("baseURL"));
        } catch (Exception e) {
            throw new InvalidJsonValueException(params, "baseURL", params.getString("baseURL"), e);
        }

        checkSecurity(params);
    }

    private void checkSecurity(PJsonObject params) {
        try {
            if (!context.getConfig().validateUri(baseUrl)) {
                throw new InvalidJsonValueException(params, "baseURL", baseUrl);
            }
        } catch (Exception e) {
            throw new InvalidJsonValueException(params, "baseURL", baseUrl, e);
        }
    }

    public void render(Transformer transformer, PdfContentByte dc, String srs, boolean first) {
        Map<String, List<String>> queryParams = new HashMap<String, List<String>>();

        try {
            PJsonObject customParams = params.optJSONObject("customParams");
            if (customParams != null) {
                final Iterator<String> customParamsIt = customParams.keys();
                while (customParamsIt.hasNext()) {
                    String key = customParamsIt.next();
                    URIUtils.addParam(queryParams, key, customParams.getString(key));
                }
            }

            MapRenderer.Format format = addQueryParams(queryParams, transformer, srs, first);

            final URI url = URIUtils.addParams(baseUrl, queryParams, OVERRIDE_ALL);
            LOGGER.debug(url);

            MapRenderer formater = MapRenderer.get(format);

            formater.render(transformer, url, dc, context, opacity);
        } catch (Exception e) {
            context.addError(e);
        }
    }

    protected abstract MapRenderer.Format addQueryParams(Map<String, List<String>> result, Transformer transformer, String srs, boolean first);

    public boolean canMerge(MapReader other) {
        if (!super.canMerge(other)) {
            return false;
        }

        if (other instanceof WMSMapReader) {
            WMSMapReader wms = (WMSMapReader) other;
            PJsonObject customParams = params.optJSONObject("customParams");
            PJsonObject customParamsOther = wms.params.optJSONObject("customParams");
            return baseUrl.equals(wms.baseUrl) && customParams.equals(customParamsOther);
        } else {
            return false;
        }
    }
}
