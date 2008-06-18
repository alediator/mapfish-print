package org.mapfish.print.map.renderers;

import com.lowagie.text.DocumentException;
import com.lowagie.text.Image;
import com.lowagie.text.pdf.PdfContentByte;
import org.mapfish.print.RenderingContext;
import org.mapfish.print.Transformer;

import java.io.IOException;
import java.net.URI;

public class BitmapMapRenderer extends MapRenderer {
    public void render(Transformer transformer, URI url, PdfContentByte dc, RenderingContext context, float opacity) throws IOException {
        try {
            final Image map = Image.getInstance(url.toURL());
            map.scaleAbsolute(transformer.getPaperW(), transformer.getPaperH());
            map.setAbsolutePosition(transformer.getPaperPosX(), transformer.getPaperPosY());

            if(opacity<1.0) {
                final byte byteOpacity = (byte) (Math.round(opacity * 256.0F));
                byte gradient[] = new byte[] {byteOpacity};
                Image smask = Image.getInstance(1, 1, 1, 8, gradient);
                smask.makeMask();
                map.setImageMask(smask);
            }
            dc.addImage(map);
        } catch (DocumentException e) {
            context.addError(e);
        }
    }
}
