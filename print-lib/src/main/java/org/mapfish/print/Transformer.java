package org.mapfish.print;

import com.lowagie.text.pdf.PdfContentByte;
import org.mapfish.print.utils.DistanceUnit;

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

    /**
     * angle in radian
     */
    private double rotation;

    public Transformer(float centerX, float centerY, int paperWidth, int paperHeight, int scale, int dpi, String unit, double rotation) {
        final DistanceUnit unitEnum = DistanceUnit.fromString(unit);
        if (unitEnum == null) {
            throw new RuntimeException("Unknown unit: '" + unit + "'");
        }
        pixelPerGeoUnit = (float) (unitEnum.convertTo(dpi, DistanceUnit.IN) / scale);

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
        this.rotation = rotation;
    }

    public float getGeoW() {
        return maxGeoX - minGeoX;
    }

    public float getGeoH() {
        return (maxGeoY - minGeoY);
    }

    public float getStraightBitmapW() {
        return getGeoW() * pixelPerGeoUnit;
    }

    public float getStraightBitmapH() {
        return getGeoH() * pixelPerGeoUnit;
    }

    public long getRotatedBitmapW() {
        double width = getStraightBitmapW();
        if (rotation != 0.0) {
            double height = getStraightBitmapH();
            width = Math.abs(width * Math.cos(rotation)) + Math.abs(height * Math.sin(rotation));
        }
        return Math.round(width);
    }

    public long getRotatedBitmapH() {
        double height = getStraightBitmapH();
        if (rotation != 0.0) {
            double width = getStraightBitmapW();
            height = Math.abs(height * Math.cos(rotation)) + Math.abs(width * Math.sin(rotation));
        }
        return Math.round(height);
    }

    public float getRotatedGeoW() {
        float width = getGeoW();
        if (rotation != 0.0) {
            float height = getGeoH();
            width = (float) (Math.abs(width * Math.cos(rotation)) + Math.abs(height * Math.sin(rotation)));
        }
        return width;
    }

    public float getRotatedGeoH() {
        float height = getGeoH();
        if (rotation != 0.0) {
            float width = getGeoW();
            height = (float) (Math.abs(height * Math.cos(rotation)) + Math.abs(width * Math.sin(rotation)));
        }
        return height;
    }

    public float getRotatedPaperW() {
        float width = getPaperW();
        if (rotation != 0.0) {
            float height = getPaperH();
            width = (float) (Math.abs(width * Math.cos(rotation)) + Math.abs(height * Math.sin(rotation)));
        }
        return width;
    }

    public float getRotatedPaperH() {
        float height = getPaperH();
        if (rotation != 0.0) {
            float width = getPaperW();
            height = (float) (Math.abs(height * Math.cos(rotation)) + Math.abs(width * Math.sin(rotation)));
        }
        return height;
    }

    public float getRotatedMinGeoX() {
        return minGeoX - (getRotatedGeoW() - getGeoW()) / 2.0F;
    }

    public float getRotatedMaxGeoX() {
        return maxGeoX + (getRotatedGeoW() - getGeoW()) / 2.0F;
    }

    public float getRotatedMinGeoY() {
        return minGeoY - (getRotatedGeoH() - getGeoH()) / 2.0F;
    }

    public float getRotatedMaxGeoY() {
        return maxGeoY + (getRotatedGeoH() - getGeoH()) / 2.0F;
    }

    public long getRotatedSvgW() {
        return getRotatedBitmapW() * svgFactor;
    }

    public long getRotatedSvgH() {
        return getRotatedBitmapH() * svgFactor;
    }

    public long getStraightSvgW() {
        return (long) (getStraightBitmapW() * svgFactor);
    }

    public long getStraightSvgH() {
        return (long) (getStraightBitmapH() * svgFactor);
    }

    public float getPaperW() {
        return paperWidth;
    }

    public float getPaperH() {
        return paperHeight;
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

    private AffineTransform getBaseTransform() {
        final AffineTransform result = AffineTransform.getTranslateInstance(paperPosX, paperPosY);
        if (rotation != 0.0F) {
            result.rotate(rotation, getPaperW() / 2, getPaperH() / 2);
            result.translate(-(getRotatedPaperW() - getPaperW()) / 2, -(getRotatedPaperH() - getPaperH()) / 2);
        }
        return result;
    }

    public AffineTransform getGeoTransform(boolean reverse) {
        final AffineTransform result = AffineTransform.getTranslateInstance(paperPosX, paperPosY);
        if (rotation != 0.0F) {
            result.rotate((reverse ? -1 : 1) * rotation, getPaperW() / 2, getPaperH() / 2);
        }
        result.scale(getPaperW() / getGeoW(), getPaperH() / getGeoH());
        result.translate(-minGeoX, -minGeoY);
        return result;
    }

    public AffineTransform getSvgTransform() {
        final AffineTransform result = getBaseTransform();
        result.scale(getPaperW() / getStraightSvgW(), getPaperH() / getStraightSvgH());
        return result;
    }

    public AffineTransform getPdfTransform() {
        final AffineTransform result = getBaseTransform();
        result.scale(getPaperW() / getStraightBitmapW(), getPaperH() / getStraightBitmapH());
        return result;
    }

    public AffineTransform getBitmapTransform() {
        return getPdfTransform();
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

    public double getRotation() {
        return rotation;
    }

    public void setClipping(PdfContentByte dc) {
        dc.rectangle(paperPosX, paperPosY, paperWidth, paperHeight);
        dc.clip();
        dc.newPath();
    }

    public void setRotation(double rotation) {
        this.rotation = rotation;
    }

    public float getResolution() {
        return 1 / pixelPerGeoUnit;
    }

    public void setResolution(float resolution) {
        this.pixelPerGeoUnit = 1 / resolution;
    }
}
