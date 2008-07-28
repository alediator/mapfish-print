package org.mapfish.print.scalebar;

import com.lowagie.text.Font;
import com.lowagie.text.pdf.PdfContentByte;
import org.mapfish.print.config.layout.ScalebarBlock;

import java.util.List;

/**
 * Draw a simple line with ticks.
 */

public class LineScalebarDrawer extends ScalebarDrawer {
    public LineScalebarDrawer(ScalebarBlock block, List<Label> labels, int barSize,
                              int labelDistance, int subIntervals, float intervalWidth, Font pdfFont,
                              float leftLabelMargin, float rightLabelMargin, float maxLabelWidth,
                              float maxLabelHeight) {
        super(block, labels, barSize, labelDistance, subIntervals, intervalWidth,
                pdfFont, leftLabelMargin, rightLabelMargin, maxLabelWidth, maxLabelHeight);
    }

    protected void drawBar(PdfContentByte dc) {
        dc.moveTo(0, 0);
        dc.lineTo(0, barSize);
        dc.lineTo(intervalWidth * block.getIntervals(), barSize);
        dc.lineTo(intervalWidth * block.getIntervals(), 0);
        for (int i = 0; i < block.getIntervals(); ++i) {
            float pos = i * intervalWidth;
            if (i > 0) {
                dc.moveTo(pos, 0);
                dc.lineTo(pos, barSize);
            }
            for (int j = 1; j < subIntervals; ++j) {
                pos += intervalWidth / subIntervals;
                dc.moveTo(pos, barSize);
                dc.lineTo(pos, barSize / 2);
            }
        }
        dc.stroke();
    }
}
