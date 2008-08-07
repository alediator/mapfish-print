package org.mapfish.print.config.layout;

import com.lowagie.text.Document;
import org.mapfish.print.PDFUtils;
import org.mapfish.print.RenderingContext;
import org.mapfish.print.utils.PJsonObject;

public class MetaData {
    private String title;
    private String author;
    private String subject;
    private String keywords;
    private String creator;

    public void setTitle(String title) {
        this.title = title;
    }

    public void setAuthor(String author) {
        this.author = author;
    }

    public void setSubject(String subject) {
        this.subject = subject;
    }

    public void setKeywords(String keywords) {
        this.keywords = keywords;
    }

    public void setCreator(String creator) {
        this.creator = creator;
    }

    public void render(PJsonObject params, RenderingContext context) {
        final Document doc = context.getDocument();

        if (title != null) {
            doc.addTitle(PDFUtils.evalString(context, params, title));
        }

        if (author != null) {
            doc.addAuthor(PDFUtils.evalString(context, params, author));
        }

        if (subject != null) {
            doc.addSubject(PDFUtils.evalString(context, params, subject));
        }

        if (keywords != null) {
            doc.addKeywords(PDFUtils.evalString(context, params, keywords));
        }

        if (creator != null) {
            doc.addCreator(PDFUtils.evalString(context, params, creator));
        }
    }
}
