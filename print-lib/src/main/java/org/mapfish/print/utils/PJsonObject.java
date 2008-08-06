package org.mapfish.print.utils;

import org.json.JSONArray;
import org.json.JSONObject;
import org.mapfish.print.JsonMissingException;

import java.util.Iterator;

/**
 * Wrapper around the {@link org.json.JSONObject} class to have a better
 * error managment.
 */
public class PJsonObject extends PJsonElement {
    private final JSONObject obj;

    public PJsonObject(JSONObject obj, String contextName) {
        this(null, obj, contextName);
    }

    public PJsonObject(PJsonElement parent, JSONObject obj, String contextName) {
        super(parent, contextName);
        this.obj = obj;
    }

    public String optString(String key) {
        return optString(key, null);
    }

    public String optString(String key, String defaultValue) {
        return obj.optString(key, defaultValue);
    }

    public String getString(String key) {
        String result = obj.optString(key, null);
        if (result == null) {
            throw new JsonMissingException(this, key);
        }
        return result;
    }

    public int getInt(String key) {
        Integer result = obj.optInt(key, Integer.MIN_VALUE);
        if (result == Integer.MIN_VALUE) {
            throw new JsonMissingException(this, key);
        }
        return result;
    }

    public Integer optInt(String key) {
        final int result = obj.optInt(key, Integer.MIN_VALUE);
        return result == Integer.MIN_VALUE ? null : result;
    }

    public int optInt(String key, int defaultValue) {
        return obj.optInt(key, defaultValue);
    }

    public double getDouble(String key) {
        double result = obj.optDouble(key, Double.NaN);
        if (Double.isNaN(result)) {
            throw new JsonMissingException(this, key);
        }
        return result;
    }

    public float getFloat(String key) {
        return (float) getDouble(key);
    }

    public Float optFloat(String key) {
        double result = obj.optDouble(key, Double.NaN);
        if (Double.isNaN(result)) {
            return null;
        }
        return (float) result;
    }

    public Float optFloat(String key, float defaultValue) {
        return (float) obj.optDouble(key, defaultValue);
    }

    public PJsonObject optJSONObject(String key) {
        final JSONObject val = obj.optJSONObject(key);
        return val != null ? new PJsonObject(this, val, key) : null;
    }

    public PJsonObject getJSONObject(String key) {
        final JSONObject val = obj.optJSONObject(key);
        if (val == null) {
            throw new JsonMissingException(this, key);
        }
        return new PJsonObject(this, val, key);
    }

    public PJsonArray getJSONArray(String key) {
        final JSONArray val = obj.optJSONArray(key);
        if (val == null) {
            throw new JsonMissingException(this, key);
        }
        return new PJsonArray(this, val, key);
    }

    public PJsonArray optJSONArray(String key) {
        final JSONArray val = obj.optJSONArray(key);
        if (val == null) {
            return null;
        }
        return new PJsonArray(this, val, key);
    }

    public Iterator<String> keys() {
        return obj.keys();
    }

    public int size() {
        return obj.length();
    }

    public boolean equals(Object obj) {
        if (obj == this) {
            return true;
        }
        if (obj instanceof PJsonObject) {
            PJsonObject other = (PJsonObject) obj;
            if (size() != other.size()) {
                return false;
            }
            final Iterator<String> iterator = keys();
            while (iterator.hasNext()) {
                String key = iterator.next();
                if (!other.getString(key).equals(getString(key))) {
                    return false;
                }
            }
            return true;
        } else {
            return false;
        }
    }

    /**
     * @deprecated Only for tests!
     */
    public JSONObject getObj() {
        return obj;
    }
}
