package org.mapfish.print.map.renderers;

import com.lowagie.text.pdf.PdfContentByte;
import com.lowagie.text.pdf.PdfGState;
import com.lowagie.text.pdf.PdfImportedPage;
import com.lowagie.text.pdf.PdfReader;
import org.apache.log4j.Logger;
import org.mapfish.print.RenderingContext;
import org.mapfish.print.Transformer;

import java.io.IOException;
import java.net.URI;

public class PDFMapRenderer extends MapRenderer {
    public static final Logger LOGGER = Logger.getLogger(PDFMapRenderer.class);

    public void render(Transformer transformer, URI url, PdfContentByte dc, RenderingContext context, float opacity) throws IOException {
        PdfReader reader = new PdfReader(url.toURL());
        PdfImportedPage pdfMap = context.getWriter().getImportedPage(reader, 1);

        if (opacity < 1.0) {
            PdfGState gs = new PdfGState();
            gs.setFillOpacity(opacity);
            gs.setStrokeOpacity(opacity);
            //gs.setBlendMode(PdfGState.BM_SOFTLIGHT);
            pdfMap.setGState(gs);
        }

        dc.addTemplate(pdfMap,
                transformer.getPaperW() / transformer.getBitmapW(), 0,
                0, transformer.getPaperW() / transformer.getBitmapW(),
                transformer.getPaperPosX(), transformer.getPaperPosY());

    }
}
