package org.mapfish.print.config;

import org.mapfish.print.PrintTestCase;

import java.net.MalformedURLException;
import java.net.SocketException;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.UnknownHostException;

public class DnsHostMatcherTest extends PrintTestCase {
    public DnsHostMatcherTest(String name) {
        super(name);
    }

    public void testSimple() throws URISyntaxException, UnknownHostException, SocketException, MalformedURLException {
        DnsHostMatcher matcher = new DnsHostMatcher();
        matcher.setHost("www.example.com");

        assertTrue(matcher.validate(new URI("http://www.example.com:80/toto")));
        assertTrue(matcher.validate(new URI("http://www.example.com:8000/toto")));
        assertFalse(matcher.validate(new URI("http://www.microsoft.com:80/toto")));
    }
}
