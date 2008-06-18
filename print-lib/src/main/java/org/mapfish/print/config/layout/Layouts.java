package org.mapfish.print.config.layout;

import org.ho.yaml.wrapper.DefaultMapWrapper;

import java.util.HashMap;

/**
 * Just to make sure the values of the hash have the good type.
 */
public class Layouts extends HashMap<String, Layout> {
    public static class Wrapper extends DefaultMapWrapper {
        public Wrapper(Class<Layout> type) {
            super(type);
        }

        public Class<Layout> getExpectedType(Object key) {
            return Layout.class;
        }
    }
}
