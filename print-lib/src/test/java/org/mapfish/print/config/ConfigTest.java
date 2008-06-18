package org.mapfish.print.config;

import org.mapfish.print.PrintTestCase;

import java.io.File;
import java.io.FileNotFoundException;

public class ConfigTest extends PrintTestCase {
    public ConfigTest(String name) {
        super(name);
    }

    public void testParse() throws FileNotFoundException {
        Config config = Config.fromYaml(new File("../print-standalone/samples/config.yaml"));
    }
}
