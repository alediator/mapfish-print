package org.mapfish.print;

import com.lowagie.text.BadElementException;
import com.lowagie.text.Chunk;
import com.lowagie.text.Document;
import com.lowagie.text.DocumentException;
import com.lowagie.text.Font;
import com.lowagie.text.Rectangle;
import com.lowagie.text.pdf.PdfAnnotation;
import com.lowagie.text.pdf.PdfContentByte;
import com.lowagie.text.pdf.PdfImportedPage;
import com.lowagie.text.pdf.PdfPageEventHelper;
import com.lowagie.text.pdf.PdfReader;
import com.lowagie.text.pdf.PdfWriter;
import org.apache.log4j.Logger;
import org.mapfish.print.config.layout.HeaderFooter;
import org.mapfish.print.utils.PJsonObject;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Listen to events from the PDF document in order to render the
 * custom {@link com.lowagie.text.Chunk}s.
 */
public class PDFCustomBlocks extends PdfPageEventHelper {
    public static final Logger LOGGER = Logger.getLogger(PDFCustomBlocks.class);

    private int ids = 0;

    private final Map<String, DrawerHolder> drawers = new HashMap<String, DrawerHolder>();
    private DrawerHolder last = null;
    private final PdfWriter writer;
    private final RenderingContext context;
    private HeaderFooter header;
    private PJsonObject headerParams;
    private HeaderFooter footer;
    private PJsonObject footerParams;
    private String backgroundPdf;
    private final List<Exception> errors = new ArrayList<Exception>();

    /**
     * cache of background PDF pages
     */
    private final Map<String, PdfImportedPage> backgroundPdfs = new HashMap<String, PdfImportedPage>();

    /**
     * block for rendering the totalpage number.
     */
    private TotalPageNum totalPageNum = null;

    public PDFCustomBlocks(PdfWriter writer, RenderingContext context) {
        this.writer = writer;
        this.context = context;
        writer.setPageEvent(this);
    }

    /**
     * The callback called for every chunk having a generic tag set.
     */
    public void onGenericTag(PdfWriter writer, Document doc, Rectangle rectangle, String s) {
        super.onGenericTag(writer, doc, rectangle, s);
        DrawerHolder holder = drawers.remove(s);
        if (holder != null) {
            final PdfContentByte dc = writer.getDirectContent();
            holder.chunkDrawer.render(rectangle, dc);
            for (int i = 0; i < holder.others.size(); i++) {
                AbsoluteDrawer absoluteDrawer = holder.others.get(i);
                try {
                    absoluteDrawer.render(dc);
                } catch (DocumentException e) {
                    addError(e);
                }
            }
            if (holder == last) {
                last = null;
            }
        }
    }

    public void onStartPage(PdfWriter writer, Document document) {
        super.onStartPage(writer, document);

        final PdfContentByte dc = writer.getDirectContent();
        addBackground(writer, document, dc);
    }

    public void onEndPage(PdfWriter writer, Document document) {
        final PdfContentByte dc = writer.getDirectContent();
        addHeader(document, dc);
        addFooter(document, dc);
        addErrors(writer);
        super.onEndPage(writer, document);
    }

    public void onCloseDocument(PdfWriter writer, Document document) {
        if (totalPageNum != null) {
            totalPageNum.render(writer);
        }
        super.onCloseDocument(writer, document);
    }

    private void addBackground(PdfWriter writer, Document document, PdfContentByte dc) {
        if (backgroundPdf != null) {
            try {
                PdfImportedPage page = backgroundPdfs.get(backgroundPdf);
                if (page == null) {
                    PdfReader reader = new PdfReader(backgroundPdf);
                    page = writer.getImportedPage(reader, 1);
                    backgroundPdfs.put(backgroundPdf, page);
                }
                final Rectangle pageSize = document.getPageSize();
                final boolean rotate = (page.getWidth() < page.getHeight()) ^ (pageSize.getWidth() < pageSize.getHeight());
                if (rotate) {
                    dc.addTemplate(page, 0, -1, 1, 0, 0, pageSize.getHeight());
                } else {
                    dc.addTemplate(page, 0, 0);
                }
            } catch (IOException e) {
                addError(e);
            }
        }
    }

    private void addHeader(Document document, PdfContentByte dc) {
        if (header != null) {
            Rectangle rectangle = new Rectangle(document.left(), document.top(),
                    document.right(), document.top() + header.getHeight());
            header.render(rectangle, dc, headerParams, context);
        }
    }

    private void addFooter(Document document, PdfContentByte dc) {
        if (footer != null) {
            Rectangle rectangle = new Rectangle(document.left(), document.bottom() - footer.getHeight(), document.right(), document.bottom());
            footer.render(rectangle, dc, footerParams, context);
        }
    }

    private void addErrors(PdfWriter writer) {
        if (errors.size() > 0) {
            StringBuilder errorTxt = new StringBuilder();
            for (int i = 0; i < errors.size(); i++) {
                Exception exception = errors.get(i);
                errorTxt.append(exception).append("\n");
            }
            errors.clear();

            final Rectangle rect = new Rectangle(20f, 40f, 40f, 60f);

            final PdfAnnotation annotation = PdfAnnotation.createText(writer, rect, "Error", errorTxt.toString(), false, "Note");
            writer.addAnnotation(annotation);

            if (LOGGER.isDebugEnabled()) {
                LOGGER.debug("Added an annotation for errors");
            }
        }
    }

    /**
     * Link a custom drawer and a chunk.
     */
    public void addChunkDrawer(Chunk chunk, ChunkDrawer chunkDrawer) {
        int id = ids++;
        final String idTxt = "map" + Integer.toString(id);
        DrawerHolder holder = new DrawerHolder(chunkDrawer);
        drawers.put(idTxt, holder);
        chunk.setGenericTag(idTxt);
        last = holder;
    }

    /**
     * Schedule a absolute block.
     */
    public void addAbsoluteDrawer(AbsoluteDrawer chunkDrawer) throws DocumentException {
        if (last != null) {
            last.others.add(chunkDrawer);
        } else {
            //no chunk drawer is scheduled. We can draw it right away.
            chunkDrawer.render(writer.getDirectContent());
        }
    }

    public void setHeader(HeaderFooter header, PJsonObject params) {
        this.header = header;
        this.headerParams = params;
    }

    public void setFooter(HeaderFooter footer, PJsonObject params) {
        this.footer = footer;
        this.footerParams = params;
    }

    public void setBackgroundPdf(String backgroundPdf) {
        this.backgroundPdf = backgroundPdf;
    }

    public void addError(Exception e) {
        errors.add(e);
        LOGGER.warn("Error while adding a PDF element", e);
    }

    public Chunk getOrCreateTotalPagesBlock(Font font) throws BadElementException {
        if (totalPageNum == null) {
            totalPageNum = new TotalPageNum(writer, font);
        }

        return totalPageNum.createPlaceHolder(this);
    }

    /**
     * Base class for the chunk drawers
     */
    public static interface ChunkDrawer {
        void render(Rectangle rectangle, PdfContentByte dc);
    }

    /**
     * Base class for the absolute drawers
     */
    public static interface AbsoluteDrawer {
        void render(PdfContentByte dc) throws DocumentException;
    }

    private static class DrawerHolder {
        private final ChunkDrawer chunkDrawer;
        private final List<AbsoluteDrawer> others = new ArrayList<AbsoluteDrawer>();

        public DrawerHolder(ChunkDrawer chunkDrawer) {
            this.chunkDrawer = chunkDrawer;
        }
    }
}
