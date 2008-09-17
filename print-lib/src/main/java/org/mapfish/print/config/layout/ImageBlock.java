package org.mapfish.print.config.layout;

import com.lowagie.text.DocumentException;
import com.lowagie.text.Rectangle;
import com.lowagie.text.pdf.PdfContentByte;
import org.apache.batik.ext.awt.RenderingHintsKeyExt;
import org.apache.batik.transcoder.TranscoderInput;
import org.apache.batik.transcoder.print.PrintTranscoder;
import org.mapfish.print.*;
import org.mapfish.print.utils.PJsonObject;

import java.awt.*;
import java.awt.geom.AffineTransform;
import java.awt.print.PageFormat;
import java.awt.print.Paper;
import java.net.URI;
import java.net.URISyntaxException;

/**
 * Configuration and logic to add an image block.
 */
public class ImageBlock extends Block {
    private String url = "";
    private double maxWidth = 0.0;
    private double maxHeight = 0.0;
    private String rotation = "0";
    private double spacingAfter = 0;

    public void render(PJsonObject params, PdfElement target, RenderingContext context) throws DocumentException {
        final URI url;
        try {
            final String urlTxt = PDFUtils.evalString(context, params, this.url);
            url = new URI(urlTxt);
        } catch (URISyntaxException e) {
            throw new InvalidValueException("url", this.url, e);
        }
        if (url.getPath().endsWith(".svg")) {
            drawSVG(context, params, target, url);
        } else {
            target.add(PDFUtils.createImage(maxWidth, maxHeight, url, getRotationRadian(context, params)));
        }
    }

    private float getRotationRadian(RenderingContext context, PJsonObject params) {
        return (float) (Float.parseFloat(PDFUtils.evalString(context, params, this.rotation)) * Math.PI / 180.0F);
    }

    private void drawSVG(RenderingContext context, PJsonObject params, PdfElement paragraph, URI url) throws DocumentException {
        final TranscoderInput ti = new TranscoderInput(url.toString());
        final PrintTranscoder pt = new PrintTranscoder();
        pt.addTranscodingHint(PrintTranscoder.KEY_SCALE_TO_PAGE, Boolean.TRUE);
        pt.transcode(ti, null);

        final Paper paper = new Paper();
        paper.setSize(maxWidth, maxHeight);
        paper.setImageableArea(0, 0, maxWidth, maxHeight);
        final float rotation = getRotationRadian(context, params);

        final PageFormat pf = new PageFormat();
        pf.setPaper(paper);

        final SvgDrawer drawer = new SvgDrawer(context.getCustomBlocks(), rotation, pt, pf);

        //register a drawer that will do the job once the position of the map is known
        paragraph.add(PDFUtils.createPlaceholderTable(maxWidth, maxHeight, spacingAfter, drawer, align, context.getCustomBlocks()));
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public void setMaxWidth(double maxWidth) {
        this.maxWidth = maxWidth;
    }

    public void setMaxHeight(double maxHeight) {
        this.maxHeight = maxHeight;
    }

    public void setRotation(String rotation) {
        this.rotation = rotation;
    }

    public void setSpacingAfter(double spacingAfter) {
        this.spacingAfter = spacingAfter;
    }

    private class SvgDrawer extends ChunkDrawer {
        private final float rotation;
        private final PrintTranscoder pt;
        private final PageFormat pf;

        public SvgDrawer(PDFCustomBlocks customBlocks, float rotation, PrintTranscoder pt, PageFormat pf) {
            super(customBlocks);
            this.rotation = rotation;
            this.pt = pt;
            this.pf = pf;
        }

        public void renderImpl(Rectangle rectangle, PdfContentByte dc) {
            dc.saveState();
            Graphics2D g2 = null;
            try {
                final AffineTransform t = AffineTransform.getTranslateInstance(rectangle.getLeft(), rectangle.getBottom());
                if (rotation != 0.0F) {
                    t.rotate(rotation, maxWidth / 2.0, maxHeight / 2.0);
                }
                dc.transform(t);
                g2 = dc.createGraphics((float) maxWidth, (float) maxHeight);

                //avoid a warning from Batik
                System.setProperty("org.apache.batik.warn_destination", "false");
                g2.setRenderingHint(RenderingHintsKeyExt.KEY_TRANSCODING, RenderingHintsKeyExt.VALUE_TRANSCODING_PRINTING);
                g2.setRenderingHint(RenderingHintsKeyExt.KEY_AVOID_TILE_PAINTING, RenderingHintsKeyExt.VALUE_AVOID_TILE_PAINTING_ON);

                pt.print(g2, pf, 0);
            } finally {
                if (g2 != null) {
                    g2.dispose();
                }
                dc.restoreState();
            }
        }

    }
}