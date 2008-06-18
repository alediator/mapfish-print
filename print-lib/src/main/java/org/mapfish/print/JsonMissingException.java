package org.mapfish.print;

import org.mapfish.print.utils.PJsonElement;

public class JsonMissingException extends PrintException {
    public JsonMissingException(PJsonElement pJsonObject, String key) {
        super("attribute [" + pJsonObject.getPath(key) + "] missing");
    }
}
