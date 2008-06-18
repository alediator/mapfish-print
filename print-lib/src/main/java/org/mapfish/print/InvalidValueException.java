package org.mapfish.print;

public class InvalidValueException extends PrintException {
    public InvalidValueException(String name, String value) {
        this(name, value, null);
    }

    public InvalidValueException(String name, String value, Throwable e) {
        super(name + " has an invalid value: " + value, e);
    }
}
