package org.mapfish.print;

import com.lowagie.text.Document;
import com.lowagie.text.DocumentException;
import com.lowagie.text.pdf.PdfWriter;
import org.json.JSONException;
import org.json.JSONObject;
import org.json.JSONWriter;
import org.mapfish.print.config.Config;
import org.mapfish.print.config.layout.Layout;
import org.mapfish.print.utils.PJsonObject;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.OutputStream;

/**
 * The main class for printing maps. Will parse the spec, create the PDF
 * document and generate if
 */
public class MapPrinter {
    /**
     * The parsed configuration file.
     */
    private final Config config;
    private String configDir;

    public MapPrinter(File config) throws FileNotFoundException {
        this.config = Config.fromYaml(config);
        configDir = config.getParent();
    }

    public void print(String spec, OutputStream outFile) throws DocumentException {
        final PJsonObject jsonSpec = parseSpec(spec);

        final String layoutName = jsonSpec.getString("layout");
        Layout layout = config.getLayout(layoutName);
        if (layout == null) {
            throw new RuntimeException("Unknown layout '" + layoutName + "'");
        }

        Document doc = new Document(layout.getFirstPageSize());
        PdfWriter writer = PdfWriter.getInstance(doc, outFile);
        writer.setFullCompression();
        RenderingContext context = new RenderingContext(doc, writer, config, jsonSpec, configDir, layout);

        layout.render(jsonSpec, context);

        doc.close();
        writer.close();
    }

    private PJsonObject parseSpec(String spec) {
        final JSONObject jsonSpec;
        try {
            jsonSpec = new JSONObject(spec);
        } catch (JSONException e) {
            throw new RuntimeException("Cannot parse the spec file", e);
        }
        return new PJsonObject(jsonSpec, "spec");
    }

    public void printClientConfig(JSONWriter json) throws JSONException {
        config.printClientConfig(json);
    }
}
