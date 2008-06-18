package org.ho.yaml;

import org.apache.log4j.Logger;
import org.ho.yaml.exception.PropertyAccessException;
import org.ho.yaml.wrapper.DefaultBeanWrapper;

import java.beans.PropertyDescriptor;
import java.lang.reflect.Method;

/**
 * Fix a few bug in the original class.
 * See https://sourceforge.net/tracker/index.php?func=detail&aid=1954096&group_id=153924&atid=789717
 */
public class CustomBeanWrapper extends DefaultBeanWrapper {
    public static final Logger LOGGER = Logger.getLogger(CustomBeanWrapper.class);

    public CustomBeanWrapper(Class<?> type) {
        super(type);
    }

    public void setProperty(String name, Object value) throws PropertyAccessException {
        try {
            PropertyDescriptor prop = ReflectionUtil.getPropertyDescriptor(type, name);
            if (prop == null) {
                LOGGER.warn(type.getSimpleName() + ": unknown field '" + name + "' with value '" + value + "'");
                PropertyDescriptor prop2 = ReflectionUtil.getPropertyDescriptor(type, name);
                return;
            }
            if (config.isPropertyAccessibleForDecoding(prop)) {
                Method wm = prop.getWriteMethod();
                wm.setAccessible(true);
                wm.invoke(getObject(), new Object[]{value});
                return;
            }

        } catch (Exception e) {
            LOGGER.warn(type.getSimpleName() + ": Error while writing '" + value + "' to " + name + ": " + e);
        }
        /*try {
            Field field = type.getDeclaredField(name);
            if (config.isFieldAccessibleForDecoding(field)) {
                field.setAccessible(true);
                field.set(getObject(), value);
            }
            return;
        } catch (Exception e) {
        }*/
    }
}
