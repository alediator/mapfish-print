package org.mapfish.print.config;

import org.ho.yaml.wrapper.EnumWrapper;

/**
 * Yaml wrapper for allowing more elbow room around enum parsing:
 * <ul>
 * <li>case insensitive
 * <li>allow spaces instead of "_"s
 * </ul>
 */
public class CustomEnumWrapper extends EnumWrapper {
    public CustomEnumWrapper(Class type) {
        super(type);
    }

    public void setObject(Object obj) {
        if (obj instanceof String) {
            super.setObject((((String) obj).toUpperCase().replaceAll(" ", "_")));
        } else {
            super.setObject(obj);
        }
    }
}
