package org.mapfish.print.config.layout;

import com.lowagie.text.DocumentException;
import org.json.JSONException;
import org.json.JSONWriter;
import org.mapfish.print.InvalidJsonValueException;
import org.mapfish.print.RenderingContext;
import org.mapfish.print.utils.PJsonObject;

public class MainPage extends Page {
    private boolean rotation = false;

    public void setRotation(boolean rotation) {
        this.rotation = rotation;
    }

    public void printClientConfig(JSONWriter json) throws JSONException {
        MapBlock map = getMap();
        if (map != null) {
            json.key("map");
            map.printClientConfig(json);
        }

        json.key("rotation").value(rotation);
    }

    /**
     * Called for each map requested by the client.
     */
    public void render(PJsonObject params, RenderingContext context) throws DocumentException {
        //validate the rotation parameter
        final float rotation = params.optFloat("rotation", 0.0F);
        if (rotation != 0.0F && !this.rotation) {
            throw new InvalidJsonValueException(params, "rotation", rotation);
        }

        super.render(params, context);
    }

    public MapBlock getMap() {
        MapBlock result = null;
        for (int i = 0; i < items.size() && result == null; i++) {
            Block block = items.get(i);
            result = block.getMap();
        }
        return result;
    }
}
