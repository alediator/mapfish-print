package org.mapfish.print.config.layout;

import org.ho.yaml.wrapper.DefaultMapWrapper;

import java.util.HashMap;

/**
 * Just to make sure the values of the hash have the good type.
 */
public class ColumnDefs extends HashMap<String, ColumnDef> {
    public static class Wrapper extends DefaultMapWrapper {
        public Wrapper(Class<ColumnDef> type) {
            super(type);
        }

        public Class<ColumnDef> getExpectedType(Object key) {
            return ColumnDef.class;
        }
    }
}
