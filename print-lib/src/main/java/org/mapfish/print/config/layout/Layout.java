package org.mapfish.print.config.layout;

import com.lowagie.text.DocumentException;
import com.lowagie.text.Rectangle;
import org.json.JSONException;
import org.json.JSONWriter;
import org.mapfish.print.RenderingContext;
import org.mapfish.print.utils.PJsonArray;
import org.mapfish.print.utils.PJsonObject;

/**
 * Config and logic for one layout instance.
 */
public class Layout {
    private TitlePage titlePage;

    private MainPage mainPage;

    public void render(PJsonObject params, RenderingContext context) throws DocumentException {
        if (titlePage != null) {
            titlePage.render(params, context);
        }

        if (mainPage != null) {
            PJsonArray pages = params.getJSONArray("pages");
            for (int i = 0; i < pages.size(); ++i) {
                final PJsonObject cur = pages.getJSONObject(i);
                mainPage.render(cur, context);
            }
        }
    }

    public TitlePage getTitlePage() {
        return titlePage;
    }

    public void setTitlePage(TitlePage titlePage) {
        this.titlePage = titlePage;
    }

    public MainPage getMainPage() {
        return mainPage;
    }

    public void setMainPage(MainPage mainPage) {
        this.mainPage = mainPage;
    }

    public Rectangle getFirstPageSize() {
        if (titlePage != null) {
            return titlePage.getPageSizeRect();
        } else {
            return mainPage.getPageSizeRect();
        }
    }

    public void printClientConfig(JSONWriter json) throws JSONException {
        mainPage.printClientConfig(json);
    }
}
