package org.mapfish.print.config;

import org.mapfish.print.PrintTestCase;

import java.awt.*;

public class ColorWrapperTest extends PrintTestCase {
    public ColorWrapperTest(String name) {
        super(name);
    }

    public void testHexa() {
        ColorWrapper wrapper = new ColorWrapper(Color.class);

        wrapper.setObject("#1256A8");
        assertEquals(new Color(0x12, 0x56, 0xA8), wrapper.getObject());

        wrapper.setObject("#1256b8");
        assertEquals(new Color(0x12, 0x56, 0xb8), wrapper.getObject());
    }

    public void testHexaAlpha() {
        ColorWrapper wrapper = new ColorWrapper(Color.class);

        wrapper.setObject("#1256b823");
        assertEquals(new Color(0x12, 0x56, 0xb8, 0x23), wrapper.getObject());

    }

    public void testText() {
        ColorWrapper wrapper = new ColorWrapper(Color.class);

        wrapper.setObject("white");
        assertEquals(Color.white, wrapper.getObject());

        wrapper.setObject("Red");
        assertEquals(Color.red, wrapper.getObject());

        wrapper.setObject("LIGHT_GRAY");
        assertEquals(Color.lightGray, wrapper.getObject());

        wrapper.setObject("BLACK");
        assertEquals(Color.black, wrapper.getObject());

        wrapper.setObject("light gray");
        assertEquals(Color.lightGray, wrapper.getObject());
    }
}
