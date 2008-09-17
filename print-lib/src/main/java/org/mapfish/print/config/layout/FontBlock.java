/*
 * Copyright (C) 2008  Camptocamp
 *
 * This file is part of MapFish Server
 *
 * MapFish Server is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Lesser General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * MapFish Server is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public License
 * along with MapFish Server.  If not, see <http://www.gnu.org/licenses/>.
 */

package org.mapfish.print.config.layout;

import org.mapfish.print.RenderingContext;
import org.mapfish.print.utils.PJsonObject;
import com.lowagie.text.Paragraph;
import com.lowagie.text.DocumentException;
import com.lowagie.text.Font;
import com.lowagie.text.FontFactory;
import com.lowagie.text.pdf.BaseFont;

public abstract class FontBlock extends Block {
    private String font = "Helvetica";
    protected Double fontSize = null;
    private String fontEncoding = BaseFont.WINANSI;

    protected double spacingAfter = 0;

    public void setFont(String font) {
        this.font = font;
    }

    public void setFontSize(Double fontSize) {
        this.fontSize = fontSize;
    }

    public void setFontSize(double fontSize) {
        this.fontSize = fontSize;
    }

    public String getFont() {
        return font;
    }

    public double getFontSize() {
        if (fontSize != null) {
            return fontSize;
        } else {
            return 12.0;
        }
    }

    public void setFontEncoding(String fontEncoding) {
        this.fontEncoding = fontEncoding;
    }

    protected Font getPdfFont() {
        return FontFactory.getFont(font, fontEncoding, (float) getFontSize());
    }

    public void setSpacingAfter(double spacingAfter) {
        this.spacingAfter = spacingAfter;
    }
}