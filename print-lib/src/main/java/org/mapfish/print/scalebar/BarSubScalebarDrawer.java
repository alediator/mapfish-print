package org.mapfish.print.scalebar;

import com.lowagie.text.Font;
import com.lowagie.text.pdf.PdfContentByte;
import org.mapfish.print.config.layout.ScalebarBlock;

import java.util.List;

/**
 * Draw a bar with alternating black and white zones marking the sub-intervals.
 * Intervals have small additional ticks.
 */
public class BarSubScalebarDrawer extends BarScalebarDrawer {
    public BarSubScalebarDrawer(ScalebarBlock block, List<Label> labels, int barSize,
                                int labelDistance, int subIntervals, float intervalWidth, Font pdfFont,
                                float leftLabelMargin, float rightLabelMargin, float maxLabelWidth,
                                float maxLabelHeight) {
        super(block, labels, barSize, labelDistance, subIntervals, intervalWidth,
                pdfFont, leftLabelMargin, rightLabelMargin, maxLabelWidth, maxLabelHeight);
    }

    protected void drawBar(PdfContentByte dc) {
        super.drawBar(dc);

        if (subIntervals > 1) {
            for (int i = 0; i <= block.getIntervals(); ++i) {
                if (labels.get(i).label != null) {
                    float pos = i * intervalWidth;
                    dc.moveTo(pos, 0);
                    dc.lineTo(pos, -block.getLineWidth() * 1.5f);
                    dc.stroke();
                }
            }
        }
    }
}