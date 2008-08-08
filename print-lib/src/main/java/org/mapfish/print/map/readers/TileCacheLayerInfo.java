package org.mapfish.print.map.readers;

import org.mapfish.print.InvalidValueException;
import org.mapfish.print.utils.PJsonArray;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Holds the information we need to manage a tilecache layer. 
 */
public class TileCacheLayerInfo {
    private static final Pattern FORMAT_REGEXP = Pattern.compile("^[^/]+/([^/]+)$");
    private static final Pattern RESOLUTIONS_REGEXP = Pattern.compile("\\s+");
    private final int width;
    private final int height;
    private final float[] resolutions;
    private final float minX;
    private final float minY;
    private String extension;

    public TileCacheLayerInfo(String resolutions, int width, int height, float minX, float minY, String format) {
        String[] resolutionsTxt = RESOLUTIONS_REGEXP.split(resolutions);
        this.resolutions = new float[resolutionsTxt.length];
        for (int i = 0; i < resolutionsTxt.length; ++i) {
            this.resolutions[i] = Float.parseFloat(resolutionsTxt[i]);

        }
        this.width = width;
        this.height = height;
        this.minX = minX;
        this.minY = minY;

        Matcher formatMatcher = FORMAT_REGEXP.matcher(format);
        if (formatMatcher.matches()) {
            extension = formatMatcher.group(1).toLowerCase();
            if (extension.equals("jpg")) {
                extension = "jpeg";
            }
        } else {
            throw new InvalidValueException("format", format);
        }
    }

    public TileCacheLayerInfo(PJsonArray resolutions, int width, int height, float minX, float minY, String extension) {
        this.resolutions = new float[resolutions.size()];
        for (int i = 0; i < resolutions.size(); ++i) {
            this.resolutions[i] = resolutions.getFloat(i);

        }
        this.width = width;
        this.height = height;
        this.minX = minX;
        this.minY = minY;

        this.extension = extension;
    }

    public int getWidth() {
        return width;
    }

    public int getHeight() {
        return height;
    }

    public ResolutionInfo getNearestResolution(float targetResolution) {
        int pos = resolutions.length - 1;
        float result = resolutions[pos];
        for (int i = resolutions.length - 1; i >= 0; --i) {
            float cur = resolutions[i];
            if (cur > result && cur <= targetResolution) {
                result = cur;
                pos = i;
            }
        }
        return new ResolutionInfo(pos, result);
    }

    public float[] getResolutions() {
        return resolutions;
    }

    public String getExtension() {
        return extension;
    }

    public static class ResolutionInfo {
        public final int index;
        public final float value;

        public ResolutionInfo(int index, float value) {
            this.index = index;
            this.value = value;
        }

        public boolean equals(Object o) {
            if (this == o) {
                return true;
            }
            if (o == null || getClass() != o.getClass()) {
                return false;
            }

            ResolutionInfo that = (ResolutionInfo) o;
            return index == that.index && Float.compare(that.value, value) == 0;

        }

        public String toString() {
            final StringBuilder sb = new StringBuilder();
            sb.append("ResolutionInfo");
            sb.append("{index=").append(index);
            sb.append(", result=").append(value);
            sb.append('}');
            return sb.toString();
        }
    }

    public float getMinX() {
        return minX;
    }

    public float getMinY() {
        return minY;
    }

    public String toString() {
        final StringBuilder sb = new StringBuilder();
        sb.append("TileCacheLayerInfo");
        sb.append("{width=").append(width);
        sb.append(", height=").append(height);
        sb.append(", minX=").append(minX);
        sb.append(", minY=").append(minY);
        sb.append(", extension='").append(extension).append('\'');
        sb.append(", resolutions=").append(resolutions == null ? "null" : "");
        for (int i = 0; resolutions != null && i < resolutions.length; ++i) {
            sb.append(i == 0 ? "" : ", ").append(resolutions[i]);
        }
        sb.append('}');
        return sb.toString();
    }
}
