package org.mapfish.print.config.layout;

import org.ho.yaml.wrapper.DefaultCollectionWrapper;

import java.util.ArrayList;

/**
 * Just to make sure the values of the hash have the good type.
 */
@SuppressWarnings({"RawUseOfParameterizedType"})
public class Exceptions extends ArrayList<CellException> {
    public static class Wrapper extends DefaultCollectionWrapper {
        public Wrapper(Class type) {
            super(type);
        }

        public Class componentType() {
            return CellException.class;
        }

        public boolean isTyped() {
            return true;
        }
    }
}