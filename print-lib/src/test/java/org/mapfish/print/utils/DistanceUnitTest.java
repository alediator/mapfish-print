package org.mapfish.print.utils;

import org.mapfish.print.PrintTestCase;

public class DistanceUnitTest extends PrintTestCase {
    public DistanceUnitTest(String name) {
        super(name);
    }

    public void testString() {
        assertEquals("m", DistanceUnit.fromString("Meter").toString());
        assertEquals("m", DistanceUnit.fromString("meter").toString());
        assertEquals("m", DistanceUnit.fromString("meterS").toString());
        assertEquals("m", DistanceUnit.fromString("m").toString());
        assertEquals("°", DistanceUnit.fromString("degree").toString());
    }

    public void testConvert() {
        assertEquals(25.4 / 1000.0, DistanceUnit.IN.convertTo(1.0, DistanceUnit.M));
        assertEquals(25.4, DistanceUnit.IN.convertTo(1.0, DistanceUnit.MM));
        assertEquals(1000.0, DistanceUnit.M.convertTo(1.0, DistanceUnit.MM));
        assertEquals(1 / 12.0, DistanceUnit.IN.convertTo(1.0, DistanceUnit.FT));
        assertEquals(12.0, DistanceUnit.FT.convertTo(1.0, DistanceUnit.IN));
    }

    public void testGroup() {
        DistanceUnit[] metrics = DistanceUnit.MM.getAllUnits();
        assertEquals(4, metrics.length);
        assertSame(DistanceUnit.MM, metrics[0]);
        assertSame(DistanceUnit.CM, metrics[1]);
        assertSame(DistanceUnit.M, metrics[2]);
        assertSame(DistanceUnit.KM, metrics[3]);
    }

    public void testBestUnit() {
        assertEquals(DistanceUnit.M, DistanceUnit.getBestUnit(1000.0, DistanceUnit.MM));
        assertEquals(DistanceUnit.CM, DistanceUnit.getBestUnit(999.9, DistanceUnit.MM));
        assertEquals(DistanceUnit.KM, DistanceUnit.getBestUnit(1e12, DistanceUnit.M));
        assertEquals(DistanceUnit.MM, DistanceUnit.getBestUnit(1e-12, DistanceUnit.M));
    }
}
