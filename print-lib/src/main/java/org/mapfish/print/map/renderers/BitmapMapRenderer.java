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
        dc.saveState();
        try {
            final Image map = Image.getInstance(url.toURL());

            float rotation = (float) transformer.getRotation();
            final float h = transformer.getPaperH();
            final float w = transformer.getPaperW();
            final float x = transformer.getPaperPosX();
            final float y = transformer.getPaperPosY();
            if (rotation != 0.0) {
                map.setRotation(rotation);
                float dx = (float) Math.abs(h * Math.sin(rotation) * Math.cos(rotation));
                float dy = (float) Math.abs(w * Math.sin(rotation) * Math.cos(rotation));
                float rw = (float) (Math.abs(w * Math.cos(rotation)) + Math.abs(h * Math.sin(rotation)));
                float rh = (float) (Math.abs(h * Math.cos(rotation)) + Math.abs(w * Math.sin(rotation)));
                map.scaleAbsolute(rw, rh);
                map.setAbsolutePosition(x - dx, y - dy);
                transformer.setClipping(dc);
            } else {
                map.scaleAbsolute(w, h);
                map.setAbsolutePosition(x, y);
            }

            if (opacity < 1.0) {
                final byte byteOpacity = (byte) (Math.round(opacity * 256.0F));
                byte gradient[] = new byte[]{byteOpacity};
                Image smask = Image.getInstance(1, 1, 1, 8, gradient);
                smask.makeMask();
                map.setImageMask(smask);
            }
            dc.addImage(map);
        } catch (DocumentException e) {
            context.addError(e);
        } finally {
            dc.restoreState();
        }
    }
}
