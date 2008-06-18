package org.mapfish.print;

import com.lowagie.text.pdf.PdfContentByte;

import java.awt.geom.AffineTransform;

public class Transformer implements Cloneable {
    private int svgFactor;
    public float minGeoX;
    public float minGeoY;
    public float maxGeoX;
    public float maxGeoY;
    private final int scale;
    private final int paperWidth;
    private final int paperHeight;
    private float pixelPerGeoUnit;
    private float paperPosX;
    private float paperPosY;

    public Transformer(float centerX, float centerY, int paperWidth, int paperHeight, int scale, int dpi, String units) {
        if ("meters".equalsIgnoreCase(units) || "m".equalsIgnoreCase(units)) {
            pixelPerGeoUnit = dpi / (25.4f / 1000f) / scale;
        } else if ("kilometers".equalsIgnoreCase(units)) {
            pixelPerGeoUnit = dpi / (25.4f / 1000f) / scale * 1000f;
        } else if ("inches".equalsIgnoreCase(units)) {
            pixelPerGeoUnit = dpi / scale;
        } else
        if ("dd".equalsIgnoreCase(units) || "degrees".equalsIgnoreCase(units)) {
            pixelPerGeoUnit = dpi / (25.4f / 1000f) / scale * 40041470.0f / 360.0f;
        } else {
            throw new RuntimeException("Unknown units: '" + units + "'");
        }

        float geoWidth = paperWidth * dpi / 72.0f / pixelPerGeoUnit;
        float geoHeight = paperHeight * dpi / 72.0f / pixelPerGeoUnit;

        //target at least 600DPI for the SVG precision
        svgFactor = Math.max((600 + dpi - 1) / dpi, 1);

        minGeoX = centerX - geoWidth / 2.0f;
        minGeoY = centerY - geoHeight / 2.0f;
        maxGeoX = minGeoX + geoWidth;
        maxGeoY = minGeoY + geoHeight;
        this.paperWidth = paperWidth;
        this.paperHeight = paperHeight;
        this.scale = scale;
    }

    public float getGeoW() {
        return maxGeoX - minGeoX;
    }

    public float getGeoH() {
        return (maxGeoY - minGeoY);
    }

    public int getBitmapW() {
        return Math.round(getGeoW() * pixelPerGeoUnit);
    }

    public int getBitmapH() {
        return Math.round(getGeoH() * pixelPerGeoUnit);
    }

    public int getSvgW() {
        return getBitmapW() * svgFactor;
    }

    public int getSvgH() {
        return getBitmapH() * svgFactor;
    }

    public float getPaperW() {
        return paperWidth;
    }

    public float getPaperH() {
        return paperHeight;
    }

    public void setGeoTransform(PdfContentByte dc) {
        AffineTransform transform = getGeoTransform();
        dc.transform(transform);

        dc.rectangle(minGeoX, minGeoY, getGeoW(), getGeoH());
        dc.clip();
        dc.newPath();
    }

    public AffineTransform getGeoTransform() {
        AffineTransform transform = new AffineTransform();

        final AffineTransform pageTranslate = AffineTransform.getTranslateInstance(paperPosX, paperPosY);
        transform.concatenate(pageTranslate);

        final AffineTransform scale = AffineTransform.getScaleInstance(
                getPaperW() / getGeoW(),
                getPaperH() / getGeoH());
        transform.concatenate(scale);

        final AffineTransform mapTranslate = AffineTransform.getTranslateInstance(-minGeoX, -minGeoY);
        transform.concatenate(mapTranslate);
        return transform;
    }

    public void setMapPos(float x, float y) {
        paperPosX = x;
        paperPosY = y;
    }

    public float getPaperPosX() {
        return paperPosX;
    }

    public float getPaperPosY() {
        return paperPosY;
    }

    public void setSvgTransform(PdfContentByte dc) {
        final AffineTransform pageTranslate = AffineTransform.getTranslateInstance(paperPosX, paperPosY);
        final AffineTransform scale = AffineTransform.getScaleInstance(
                getPaperW() / getSvgW(),
                getPaperH() / getSvgH());
        pageTranslate.concatenate(scale);
        dc.transform(pageTranslate);

        dc.rectangle(0, 0, getSvgW(), getSvgH());
        dc.clip();
        dc.newPath();
    }

    public int getScale() {
        return scale;
    }

    public void zoom(Transformer mainTransformer, float factor) {
        float destW = mainTransformer.getGeoW() / factor;
        float destH = mainTransformer.getGeoH() / factor;

        //fix aspect ratio
        if (destW / destH > getGeoW() / getGeoH()) {
            destH = getGeoH() * destW / getGeoW();
        } else {
            destW = getGeoW() * destH / getGeoH();
        }

        float cX = (minGeoX + maxGeoX) / 2.0f;
        float cY = (minGeoY + maxGeoY) / 2.0f;
        pixelPerGeoUnit = pixelPerGeoUnit * getGeoW() / destW;
        minGeoX = cX - destW / 2.0f;
        maxGeoX = cX + destW / 2.0f;
        minGeoY = cY - destH / 2.0f;
        maxGeoY = cY + destH / 2.0f;
    }

    @SuppressWarnings({"CloneDoesntDeclareCloneNotSupportedException"})
    public Transformer clone() {
        try {
            return (Transformer) super.clone();
        } catch (CloneNotSupportedException e) {
            throw new RuntimeException(e);
        }
    }

    public float getMinGeoX() {
        return minGeoX;
    }

    public float getMinGeoY() {
        return minGeoY;
    }

    public float getMaxGeoX() {
        return maxGeoX;
    }

    public float getMaxGeoY() {
        return maxGeoY;
    }

    public int getSvgFactor() {
        return svgFactor;
    }
}
