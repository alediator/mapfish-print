package org.mapfish.print.config.layout;

import com.lowagie.text.Chunk;
import com.lowagie.text.DocumentException;
import com.lowagie.text.Image;
import com.lowagie.text.Paragraph;
import org.mapfish.print.InvalidValueException;
import org.mapfish.print.PDFUtils;
import org.mapfish.print.RenderingContext;
import org.mapfish.print.utils.PJsonObject;

import java.io.IOException;

/**
 * Configuration and logic to add an image block.
 */
public class ImageBlock extends ParagraphBlock {
    private String url = "";
    private int maxWidth = 0;
    private int maxHeight = 0;

    protected void fillParagraph(RenderingContext context, PJsonObject params, Paragraph paragraph) throws DocumentException {
        final Image image;
        final String url = PDFUtils.evalString(context, params, this.url);
        try {
            image = Image.getInstance(url);
        } catch (IOException e) {
            throw new InvalidValueException("url", url, e);
        }

        if (maxWidth != 0.0) {
            image.scaleToFit(maxWidth, maxHeight);
        }

        Chunk chunk = new Chunk(image, 0f, 0f, true);
        paragraph.add(chunk);
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