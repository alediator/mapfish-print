package org.mapfish.print;

public class TransformerTest extends PrintTestCase {
    public TransformerTest(String name) {
        super(name);
    }

    public void testStraight() {
        Transformer t = new Transformer(0, 0, 100, 70, 10, 2, "m", 0);
        assertEquals(100.0F, t.getPaperW());
        assertEquals(70.0F, t.getPaperH());

        final double geoW = 100.0F / 72.0F * 2.54 / 10.0F;
        final double geoH = 70.0F / 72.0F * 2.54 / 10.0F;
        assertEquals(geoW, t.getGeoW(), .000001);
        assertEquals(geoH, t.getGeoH(), .000001);
        assertEquals(geoW, t.getRotatedGeoW(), .000001);
        assertEquals(geoH, t.getRotatedGeoH(), .000001);

    }
}
