package org.mapfish.print.map.renderers;

import com.lowagie.text.pdf.PdfContentByte;
import com.lowagie.text.pdf.PdfGState;
import org.apache.batik.ext.awt.RenderingHintsKeyExt;
import org.apache.batik.transcoder.TranscoderInput;
import org.apache.batik.transcoder.print.PrintTranscoder;
import org.apache.log4j.Logger;
import org.apache.xerces.parsers.DOMParser;
import org.apache.xml.serialize.OutputFormat;
import org.apache.xml.serialize.XMLSerializer;
import org.mapfish.print.RenderingContext;
import org.mapfish.print.Transformer;
import org.w3c.dom.Document;
import org.xml.sax.InputSource;

import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMResult;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamSource;
import java.awt.*;
import java.awt.print.PageFormat;
import java.awt.print.Paper;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.URI;
import java.net.URL;

public class SVGMapRenderer extends MapRenderer {
    public static final Logger LOGGER = Logger.getLogger(SVGMapRenderer.class);

    private static final Document svgZoomOut;

    static {
        DOMParser parser = new DOMParser();
        try {
            final InputStream stream = SVGMapRenderer.class.getResourceAsStream("/org/mapfish/print/map/renderers/svgZoomOut.xsl");
            final InputSource inputSource = new InputSource(stream);
            inputSource.setSystemId(".");
            parser.parse(inputSource);

            svgZoomOut = parser.getDocument();

            stream.close();

        } catch (Exception e) {
            throw new RuntimeException("Cannot parse the SVG transformation XSLT", e);
        }
    }

    public void render(Transformer transformer, URI url, PdfContentByte dc, RenderingContext context, float opacity) throws IOException {
        dc.saveState();
        try {
            transformer.setSvgTransform(dc);

            if (opacity < 1.0) {
                PdfGState gs = new PdfGState();
                gs.setFillOpacity(opacity);
                gs.setStrokeOpacity(opacity);
                //gs.setBlendMode(PdfGState.BM_SOFTLIGHT);
                dc.setGState(gs);
            }

            Graphics2D g2 = dc.createGraphics(transformer.getSvgW(), transformer.getSvgH());

            //avoid a warning from Batik
            System.setProperty("org.apache.batik.warn_destination", "false");
            g2.setRenderingHint(RenderingHintsKeyExt.KEY_TRANSCODING, RenderingHintsKeyExt.VALUE_TRANSCODING_PRINTING);
            g2.setRenderingHint(RenderingHintsKeyExt.KEY_AVOID_TILE_PAINTING, RenderingHintsKeyExt.VALUE_AVOID_TILE_PAINTING_ON);

            final TranscoderInput ti = getTranscoderInput(url.toURL(), transformer, context);
            if (ti != null) {
                PrintTranscoder pt = new PrintTranscoder();
                pt.transcode(ti, null);
                Paper paper = new Paper();
                paper.setSize(transformer.getSvgW(), transformer.getSvgH());
                paper.setImageableArea(0, 0, transformer.getSvgW(), transformer.getSvgH());
                PageFormat pf = new PageFormat();
                pf.setPaper(paper);
                pt.print(g2, pf, 0);
            }

            g2.dispose();
        } finally {
            dc.restoreState();
        }

    }

    private TranscoderInput getTranscoderInput(URL url, Transformer transformer, RenderingContext context) {
        if (svgZoomOut != null) {
            final Document doc;
            try {
                DOMResult transformedSvg = new DOMResult();
                final TransformerFactory factory = TransformerFactory.newInstance();
                javax.xml.transform.Transformer xslt = factory.newTransformer(new DOMSource(svgZoomOut));
                //TODO: may want a different zoom factor in function of the layer and the type (symbol, line or font)
                xslt.setParameter("zoomFactor", transformer.getSvgFactor());
                final InputStream inputStream = url.openStream();
                xslt.transform(new StreamSource(inputStream), transformedSvg);
                doc = (Document) transformedSvg.getNode();

                if (LOGGER.isDebugEnabled()) {
                    printDom(doc);
                }

                inputStream.close();

                return new TranscoderInput(doc);

            } catch (Exception e) {
                context.addError(e);
                return null;
            }
        } else {
            return new TranscoderInput(url.toString());
        }
    }

    /**
     * Just for debugging XML.
     */
    public static void printDom(Document doc) throws IOException {
        OutputFormat format = new OutputFormat(doc);
        format.setLineWidth(65);
        format.setIndenting(true);
        format.setIndent(2);

        OutputStream out = new ByteArrayOutputStream();

        XMLSerializer serializer = new XMLSerializer(out, format);
        serializer.serialize(doc);

        LOGGER.debug(out.toString());
        out.close();
    }

}
