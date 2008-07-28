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

    protected void fillParagraph(RenderingContext context, PJsonObject params, Paragraph paragraph) throws DocumentException {
        final URI url;
        try {
            url = new URI(PDFUtils.evalString(context, params, this.url));
        } catch (URISyntaxException e) {
            throw new InvalidValueException("url", this.url, e);
        }
        if (url.getPath().endsWith(".svg")) {
            drawSVG(paragraph, context, url);
        } else {
            drawImage(paragraph, url);
        }
    }

    private void drawImage(Paragraph paragraph, URI url) throws BadElementException {
        final Image image;
        try {
            image = Image.getInstance(url.toString());
        } catch (IOException e) {
            throw new InvalidValueException("url", url.toString(), e);
        }

        if (maxWidth != 0.0f || maxHeight != 0.0f) {
            image.scaleToFit(maxWidth != 0.0f ? maxWidth : Integer.MAX_VALUE, maxHeight != 0.0f ? maxHeight : Integer.MAX_VALUE);
        }

        Chunk chunk = new Chunk(image, 0f, 0f, true);
        paragraph.add(chunk);
    }

    private void drawSVG(Paragraph paragraph, RenderingContext context, URI url) throws BadElementException {
        final TranscoderInput ti = new TranscoderInput(url.toString());
        final PrintTranscoder pt = new PrintTranscoder();
        pt.addTranscodingHint(PrintTranscoder.KEY_SCALE_TO_PAGE, Boolean.TRUE);
        pt.transcode(ti, null);

        final Paper paper = new Paper();
        paper.setSize(maxWidth, maxHeight);
        paper.setImageableArea(0, 0, maxWidth, maxHeight);
        Image background = PDFUtils.createEmptyImage(maxWidth, maxHeight);

        final PageFormat pf = new PageFormat();
        pf.setPaper(paper);

        //register a drawer that will do the job once the position of the image is known
        Chunk mapChunk = new Chunk(background, 0f, 0f, true);
        context.getCustomBlocks().addChunkDrawer(mapChunk, new PDFCustomBlocks.ChunkDrawer() {
            public void render(Rectangle rectangle, PdfContentByte dc) {
                dc.saveState();
                Graphics2D g2 = null;
                try {
                    dc.transform(AffineTransform.getTranslateInstance(rectangle.getLeft(), rectangle.getBottom()));
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
}