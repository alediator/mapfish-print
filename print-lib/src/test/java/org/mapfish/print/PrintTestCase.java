package org.mapfish.print;

import junit.framework.TestCase;
import org.apache.log4j.BasicConfigurator;
import org.apache.log4j.ConsoleAppender;
import org.apache.log4j.Level;
import org.apache.log4j.Logger;
import org.apache.log4j.PatternLayout;

public abstract class PrintTestCase extends TestCase {
    public PrintTestCase(String name) {
        super(name);
    }

    protected void setUp() throws Exception {
        super.setUp();
        BasicConfigurator.configure(new ConsoleAppender(
                new PatternLayout("%d{HH:mm:ss.SSS} [%t] %-5p %30.30c - %m%n")));
        Logger.getRootLogger().setLevel(Level.DEBUG);

    }

    protected void tearDown() throws Exception {
        BasicConfigurator.resetConfiguration();
        super.tearDown();
    }
}
