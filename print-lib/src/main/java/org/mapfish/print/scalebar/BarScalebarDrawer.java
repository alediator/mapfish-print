package org.mapfish.print.scalebar;

import com.lowagie.text.Font;
import com.lowagie.text.pdf.PdfContentByte;
import org.mapfish.print.config.layout.ScalebarBlock;
import org.mapfish.print.PDFCustomBlocks;

import java.awt.*;
import java.util.List;

/**
 * Draw a bar with alternating black and white zones marking the sub-intervals.
 */
public class BarScalebarDrawer extends ScalebarDrawer {
    public BarScalebarDrawer(PDFCustomBlocks customBlocks, ScalebarBlock block, List<Label> labels, int barSize,
                             int labelDistance, int subIntervals, float intervalWidth, Font pdfFont,
                             float leftLabelMargin, float rightLabelMargin, float maxLabelWidth, float maxLabelHeight
    ) {
        super(customBlocks, block, labels, barSize, labelDistance, subIntervals, intervalWidth,
                pdfFont, leftLabelMargin, rightLabelMargin, maxLabelWidth, maxLabelHeight);
    }

    protected void drawBar(PdfContentByte dc) {
        float subIntervalWidth = intervalWidth / subIntervals;
        for (int i = 0; i < block.getIntervals() * subIntervals; ++i) {
            float pos = i * subIntervalWidth;
            final Color color = i % 2 == 0 ? block.getBarBgColor() : block.getColor();
            if (color != null) {
                dc.setColorFill(color);
                dc.rectangle(pos, 0, subIntervalWidth, barSize);
                dc.fill();
            }
        }

        dc.rectangle(0, 0, intervalWidth * block.getIntervals(), barSize);
        dc.stroke();
    }
}
