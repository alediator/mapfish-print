package org.mapfish.print;

/**
 * Class implementing some custom XPath functions.
 */
public class CustomXPath {
    /**
     * Takes a string with integers separated by ',' and returns this same
     * string but with the integers multiplied by the factor.
     */
    public String factorArray(String valsTxt, int factor) {
        String[] vals = valsTxt.split("[,]\\s*");

        StringBuilder result = new StringBuilder();
        for (int i = 0; i < vals.length; ++i) {
            String val = vals[i];
            if (i > 0) {
                result.append(",");
            }
            result.append(Integer.parseInt(val) * factor);
        }

        return result.toString();
    }
}
