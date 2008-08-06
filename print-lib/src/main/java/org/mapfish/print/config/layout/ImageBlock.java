package org.mapfish.print.config.layout;

import com.lowagie.text.BadElementException;
import com.lowagie.text.Chunk;
import com.lowagie.text.DocumentException;
import com.lowagie.text.Image;
import com.lowagie.text.Paragraph;
import com.lowagie.text.Rectangle;
import com.lowagie.text.pdf.PdfContentByte;
import org.apache.batik.ext.awt.RenderingHintsKeyExt;
import org.apache.batik.transcoder.TranscoderInput;
import org.apache.batik.transcoder.print.PrintTranscoder;
import org.mapfish.print.InvalidValueException;
import org.mapfish.print.PDFCustomBlocks;
import org.mapfish.print.PDFUtils;
import org.mapfish.print.RenderingContext;
import org.mapfish.print.utils.PJsonObject;

import java.awt.*;
import java.awt.geom.AffineTransform;
import java.awt.print.PageFormat;
import java.awt.print.Paper;
import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;

/**
 * Configuration and logic to add an image block.
 */
public class ImageBlock extends ParagraphBlock {
    private String url = "";
    private float maxWidth = 0.0f;
    private float maxHeight = 0.0f;
    private String rotation = "0";

    protected void fillParagraph(RenderingContext context, PJsonObject params, Paragraph paragraph) throws DocumentException {
        final URI url;
        try {
            final String urlTxt = PDFUtils.evalString(context, params, this.url);
            url = new URI(urlTxt);
        } catch (URISyntaxException e) {
            throw new InvalidValueException("url", this.url, e);
        }
        if (url.getPath().endsWith(".svg")) {
            drawSVG(context, params, paragraph, url);
        } else {
            drawImage(context, params, paragraph, url);
        }
    }

    private void drawImage(RenderingContext context, PJsonObject params, Paragraph paragraph, URI url) throws BadElementException {
        final Image image;
        try {
            image = Image.getInstance(url.toString());
        } catch (IOException e) {
            throw new InvalidValueException("url", url.toString(), e);
        }

        if (maxWidth != 0.0f || maxHeight != 0.0f) {
            image.scaleToFit(maxWidth != 0.0f ? maxWidth : Integer.MAX_VALUE, maxHeight != 0.0f ? maxHeight : Integer.MAX_VALUE);
        }

        final float rotation = getRotationRadian(context, params);
        if (rotation != 0.0F) {
            image.setRotation(rotation);
        }

        Chunk chunk = new Chunk(image, 0f, 0f, true);
        paragraph.add(chunk);
    }

    private float getRotationRadian(RenderingContext context, PJsonObject params) {
        return (float) (Float.parseFloat(PDFUtils.evalString(context, params, this.rotation)) * Math.PI / 180.0F);
    }

    private void drawSVG(RenderingContext context, PJsonObject params, Paragraph paragraph, URI url) throws BadElementException {
        final TranscoderInput ti = new TranscoderInput(url.toString());
        final PrintTranscoder pt = new PrintTranscoder();
        pt.addTranscodingHint(PrintTranscoder.KEY_SCALE_TO_PAGE, Boolean.TRUE);
        pt.transcode(ti, null);

        final Paper paper = new Paper();
        paper.setSize(maxWidth, maxHeight);
        paper.setImageableArea(0, 0, maxWidth, maxHeight);
        Image background = PDFUtils.createEmptyImage(maxWidth, maxHeight);
        final float rotation = getRotationRadian(context, params);

        final PageFormat pf = new PageFormat();
        pf.setPaper(paper);

        //register a drawer that will do the job once the position of the image is known
        Chunk mapChunk = new Chunk(background, 0f, 0f, true);
        context.getCustomBlocks().addChunkDrawer(mapChunk, new PDFCustomBlocks.ChunkDrawer() {
            public void render(Rectangle rectangle, PdfContentByte dc) {
                dc.saveState();
                Graphics2D g2 = null;
                try {
                    final AffineTransform t = AffineTransform.getTranslateInstance(rectangle.getLeft(), rectangle.getBottom());
                    if (rotation != 0.0F) {
                        t.rotate(rotation, maxWidth / 2.0, maxHeight / 2.0);
                    }
                    dc.transform(t);
                    g2 = dc.createGraphics(maxWidth, maxHeight);

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
        });
        paragraph.add(mapChunk);
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public void setMaxWidth(int maxWidth) {
        this.maxWidth = maxWidth;
    }

    public void setMaxHeight(int maxHeight) {
        this.maxHeight = maxHeight;
    }

    public void setRotation(String rotation) {
        this.rotation = rotation;
    }
}