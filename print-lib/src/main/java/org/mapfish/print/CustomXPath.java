package org.mapfish.print;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

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
            result.append(factorValue(val, factor));
        }

        return result.toString();
    }


    private static Pattern NUMBER_UNIT = Pattern.compile("^(\\d*(\\.\\d*)?)(.*)$");

    public String factorValue(String valTxt, int factor) {
        Matcher matcher = NUMBER_UNIT.matcher(valTxt);
        if (matcher.matches()) {
            final String s = matcher.group(1);
            String txt = String.valueOf(Float.parseFloat(s) * factor);
            if (txt.endsWith(".0")) {
                txt = txt.substring(0, txt.length() - 2);
            }
            return txt + matcher.group(3);
        } else {
            throw new NumberFormatException("Cannot parse [" + valTxt + "]");
        }
    }
}
