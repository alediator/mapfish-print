package org.mapfish.print;

import com.lowagie.text.Document;
import com.lowagie.text.DocumentException;
import com.lowagie.text.FontFactory;
import com.lowagie.text.pdf.PdfStream;
import com.lowagie.text.pdf.PdfWriter;
import org.json.JSONException;
import org.json.JSONObject;
import org.json.JSONWriter;
import org.mapfish.print.config.Config;
import org.mapfish.print.config.layout.Layout;
import org.mapfish.print.utils.PJsonObject;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.OutputStream;
import java.util.TreeSet;

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
        if (configDir == null) {
            try {
                configDir = new File(".").getCanonicalPath();
            } catch (IOException e) {
                configDir = ".";
            }
        }
        initFonts();
    }

    private void initFonts() {
        //we don't do that since it takes ages and that would hurt the perfs for
        //the python controller:
        //FontFactory.registerDirectories();

        FontFactory.defaultEmbedding=true;
        
        final TreeSet<String> fontPaths = config.getFonts();
        if (fontPaths != null) {
            for (String fontPath : fontPaths) {
                File fontFile=new File(fontPath);
                if(fontFile.isDirectory()) {
                    FontFactory.registerDirectory(fontPath, true);
                } else {
                    FontFactory.register(fontPath);
                }
            }
        }
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
        writer.setPdfVersion(PdfWriter.PDF_VERSION_1_5);
        writer.setCompressionLevel(PdfStream.BEST_COMPRESSION);
        RenderingContext context = new RenderingContext(doc, writer, config, jsonSpec, configDir, layout);

        layout.render(jsonSpec, context);

        doc.close();
        writer.close();
    }

    public static PJsonObject parseSpec(String spec) {
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
