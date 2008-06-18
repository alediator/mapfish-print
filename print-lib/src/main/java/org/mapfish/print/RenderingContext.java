package org.mapfish.print;

import com.lowagie.text.Document;
import com.lowagie.text.pdf.PdfContentByte;
import com.lowagie.text.pdf.PdfWriter;
import org.mapfish.print.config.Config;
import org.mapfish.print.config.layout.Layout;
import org.mapfish.print.utils.PJsonObject;

/**
 * Holds some "per rendering request" information.
 */
public class RenderingContext {
    private final Document document;
    private final PdfWriter writer;
    private final Config config;
    private final PJsonObject globalParams;
    private final String configDir;
    private final PDFCustomBlocks customBlocks;
    private final Layout layout;

    public RenderingContext(Document document, PdfWriter writer, Config config, PJsonObject globalParams, String configDir, Layout layout) {
        this.document = document;
        this.writer = writer;
        this.config = config;
        this.globalParams = globalParams;
        this.configDir = configDir;
        this.layout = layout;
        customBlocks = new PDFCustomBlocks(writer, this);
    }

    public PDFCustomBlocks getCustomBlocks() {
        return customBlocks;
    }

    public Document getDocument() {
        return document;
    }

    public Config getConfig() {
        return config;
    }

    public PdfWriter getWriter() {
        return writer;
    }

    public PdfContentByte getDirectContent() {
        return writer.getDirectContent();
    }

    public PJsonObject getGlobalParams() {
        return globalParams;
    }

    public String getConfigDir() {
        return configDir;
    }

    public Layout getLayout() {
        return layout;
    }

    public void addError(Exception e) {
        customBlocks.addError(e);
    }
}
