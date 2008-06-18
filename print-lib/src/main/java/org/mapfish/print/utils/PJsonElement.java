package org.mapfish.print.utils;

/**
 * Common parent class for the JSON wrappers.
 */
public abstract class PJsonElement {
    private final PJsonElement parent;
    private final String contextName;

    protected PJsonElement(PJsonElement parent, String contextName) {
        this.parent = parent;
        this.contextName = contextName;
    }

    /**
     * Gets the string representation of the path to the current JSON element.
     */
    public String getPath(String key) {
        StringBuilder result = new StringBuilder();
        getPath(result);
        result.append(".");
        result.append(getPathElement(key));
        return result.toString();
    }

    protected void getPath(StringBuilder result) {
        if (parent != null) {
            parent.getPath(result);
            if (!(parent instanceof PJsonArray)) {
                result.append(".");
            }
        }
        result.append(getPathElement(contextName));
    }

    private static String getPathElement(String val) {
        if (val.contains(" ")) {
            return "'" + val + "'";
        } else {
            return val;
        }
    }


    public PJsonElement getParent() {
        return parent;
    }
}
