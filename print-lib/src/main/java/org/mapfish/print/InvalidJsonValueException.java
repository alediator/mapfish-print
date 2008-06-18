package org.mapfish.print;

import org.mapfish.print.utils.PJsonElement;

public class InvalidJsonValueException extends PrintException {
    public InvalidJsonValueException(PJsonElement element, String key, Object value) {
        this(element, key, value, null);
    }

    public InvalidJsonValueException(PJsonElement element, String key, Object value, Throwable e) {
        super(element.getPath(key) + " has an invalid value: " + value.toString(), e);
    }

    public String toString() {
        if (getCause() != null) {
            return super.toString() + " (" + getCause().getMessage() + ")";
        } else {
            return super.toString();
        }
    }
}
