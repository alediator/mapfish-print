package org.mapfish.print.scalebar;

import com.lowagie.text.Font;
import com.lowagie.text.pdf.PdfContentByte;
import org.mapfish.print.config.layout.ScalebarBlock;
import org.mapfish.print.PDFCustomBlocks;

import java.util.List;

/**
 * Draw a bar with alternating black and white zones marking the sub-intervals.
 * Intervals have small additional ticks.
 */
public class BarSubScalebarDrawer extends BarScalebarDrawer {
    public BarSubScalebarDrawer(PDFCustomBlocks customBlocks, ScalebarBlock block, List<Label> labels, int barSize,
                                int labelDistance, int subIntervals, float intervalWidth, Font pdfFont,
                                float leftLabelMargin, float rightLabelMargin, float maxLabelWidth,
                                float maxLabelHeight) {
        super(customBlocks, block, labels, barSize, labelDistance, subIntervals, intervalWidth,
                pdfFont, leftLabelMargin, rightLabelMargin, maxLabelWidth, maxLabelHeight);
    }

    protected void drawBar(PdfContentByte dc) {
        super.drawBar(dc);

        for (int i = 0; i <= block.getIntervals(); ++i) {
            if (labels.get(i).label != null) {
                float pos = i * intervalWidth;
                dc.moveTo(pos, 0);
                dc.lineTo(pos, (float) -block.getLineWidth() * 1.5f);
                dc.stroke();
            }
        }
    }
}