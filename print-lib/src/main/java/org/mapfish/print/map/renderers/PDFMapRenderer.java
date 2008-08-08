package org.mapfish.print.map.renderers;

import com.lowagie.text.pdf.PdfContentByte;
import com.lowagie.text.pdf.PdfGState;
import com.lowagie.text.pdf.PdfImportedPage;
import com.lowagie.text.pdf.PdfReader;
import org.apache.log4j.Logger;
import org.mapfish.print.InvalidValueException;
import org.mapfish.print.RenderingContext;
import org.mapfish.print.Transformer;

import java.io.IOException;
import java.net.URI;
import java.util.List;

public class PDFMapRenderer extends MapRenderer {
    public static final Logger LOGGER = Logger.getLogger(PDFMapRenderer.class);

    public void render(Transformer transformer, List<URI> uris, PdfContentByte dc, RenderingContext context, float opacity, int nbTilesHorizontal, float offsetX, float offsetY, long bitmapTileW, long bitmapTileH) throws IOException {
        if (uris.size() != 1) {
            //tiling not supported in PDF
            throw new InvalidValueException("format", "application/x-pdf");
        }
        final URI uri = uris.get(0);
        LOGGER.debug(uri);
        PdfReader reader = new PdfReader(uri.toURL());
        PdfImportedPage pdfMap = context.getWriter().getImportedPage(reader, 1);

        if (opacity < 1.0) {
            PdfGState gs = new PdfGState();
            gs.setFillOpacity(opacity);
            gs.setStrokeOpacity(opacity);
            //gs.setBlendMode(PdfGState.BM_SOFTLIGHT);
            pdfMap.setGState(gs);
        }

        dc.saveState();
        try {
            dc.transform(transformer.getPdfTransform());
            dc.addTemplate(pdfMap, 0, 0);
        } finally {
            dc.restoreState();
        }
    }
}
