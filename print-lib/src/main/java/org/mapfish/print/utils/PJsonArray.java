package org.mapfish.print.utils;

import org.json.JSONArray;
import org.json.JSONObject;
import org.mapfish.print.JsonMissingException;

/**
 * Wrapper around the {@link org.json.JSONArray} class to have a better
 * error managment.
 */
public class PJsonArray extends PJsonElement {
    private final JSONArray array;

    public PJsonArray(PJsonElement parent, JSONArray array, String contextName) {
        super(parent, contextName);
        this.array = array;
    }

    public int size() {
        return array.length();
    }

    public PJsonObject getJSONObject(int i) {
        JSONObject val = array.optJSONObject(i);
        final String context = "[" + i + "]";
        if (val == null) {
            throw new JsonMissingException(this, context);
        }
        return new PJsonObject(this, val, context);
    }

    public PJsonArray getJSONArray(int i) {
        JSONArray val = array.optJSONArray(i);
        final String context = "[" + i + "]";
        if (val == null) {
            throw new JsonMissingException(this, context);
        }
        return new PJsonArray(this, val, context);
    }

    public int getInt(int i) {
        int val = array.optInt(i, Integer.MIN_VALUE);
        if (val == Integer.MIN_VALUE) {
            throw new JsonMissingException(this, "[" + i + "]");
        }
        return val;
    }

    public float getFloat(int i) {
        double val = array.optDouble(i, Double.MAX_VALUE);
        if (val == Double.MAX_VALUE) {
            throw new JsonMissingException(this, "[" + i + "]");
        }
        return (float) val;
    }

    public String getString(int i) {
        String val = array.optString(i, null);
        if (val == null) {
            throw new JsonMissingException(this, "[" + i + "]");
        }
        return val;
    }
}
