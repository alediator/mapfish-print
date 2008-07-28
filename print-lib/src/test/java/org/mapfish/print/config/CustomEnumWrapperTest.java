package org.mapfish.print.config;

import org.mapfish.print.PrintTestCase;
import org.mapfish.print.scalebar.Type;

public class CustomEnumWrapperTest extends PrintTestCase {
    public CustomEnumWrapperTest(String name) {
        super(name);
    }

    public void testSetObject() {
        CustomEnumWrapper wrapper = new CustomEnumWrapper(Type.class);
        wrapper.setObject("bar");
        assertSame(Type.BAR, wrapper.getObject());

        wrapper.setObject("Line");
        assertSame(Type.LINE, wrapper.getObject());

        wrapper.setObject("bar sUB");
        assertSame(Type.BAR_SUB, wrapper.getObject());

        wrapper.setObject("LINE");
        assertSame(Type.LINE, wrapper.getObject());

        wrapper.setObject("BAR_SUB");
        assertSame(Type.BAR_SUB, wrapper.getObject());
    }
}
