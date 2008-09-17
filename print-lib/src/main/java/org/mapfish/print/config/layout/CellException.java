package org.mapfish.print.config.layout;

import java.util.regex.Pattern;
import java.util.regex.Matcher;

public class CellException extends CellConfig {
    private CoordMatcher row;
    private CoordMatcher col;

    public boolean matches(int row, int col) {
        return (this.row == null || this.row.matches(row)) &&
                (this.col == null || this.col.matches(col));
    }

    public void setRow(String row) {
        this.row = createMatcher(row);
    }

    public void setCol(String col) {
        this.col = createMatcher(col);
    }

    private static final Pattern SIMPLE = Pattern.compile("^\\d+$");
    private static final Pattern RANGE = Pattern.compile("^(\\d+)-(\\d+)$");

    private CoordMatcher createMatcher(String value) {
        Matcher simple = SIMPLE.matcher(value);
        if (simple.matches()) {
            return new IntCoordMatcher(Integer.parseInt(value));
        } else {
            Matcher range = RANGE.matcher(value);
            if (range.matches()) {
                return new IntCoordMatcher(Integer.parseInt(range.group(1)), Integer.parseInt(range.group(2)));
            } else {
                return new RegExpMatcher(value);
            }
        }
    }

    public static interface CoordMatcher {
        public boolean matches(int pos);
    }

    public static class IntCoordMatcher implements CoordMatcher {
        private int min;
        private int max;

        public IntCoordMatcher(int min, int max) {
            this.min = min;
            this.max = max;
        }

        public IntCoordMatcher(int value) {
            this.min = value;
            this.max = value;
        }

        public boolean matches(int pos) {
            return pos >= min && pos <= max;
        }

        public String toString() {
            return "IntCoordMatcher{" +
                    "min=" + min +
                    ", max=" + max +
                    '}';
        }
    }

    public static class RegExpMatcher implements CoordMatcher {
        private Pattern regexp;

        public RegExpMatcher(String value) {
            regexp = Pattern.compile(value);
        }

        public boolean matches(int pos) {
            return regexp.matcher(Integer.toString(pos)).matches();
        }
    }
}
