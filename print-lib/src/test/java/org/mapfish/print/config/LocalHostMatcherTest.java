package org.mapfish.print.config;

import org.mapfish.print.PrintTestCase;

import java.net.Inet4Address;
import java.net.InetAddress;
import java.net.MalformedURLException;
import java.net.NetworkInterface;
import java.net.SocketException;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.UnknownHostException;
import java.util.Enumeration;

public class LocalHostMatcherTest extends PrintTestCase {
    public LocalHostMatcherTest(String name) {
        super(name);
    }

    public void testAllIpV4() throws UnknownHostException, SocketException, URISyntaxException, MalformedURLException {
        LocalHostMatcher matcher = new LocalHostMatcher();

        Enumeration<NetworkInterface> ifaces = NetworkInterface.getNetworkInterfaces();
        while (ifaces.hasMoreElements()) {
            NetworkInterface networkInterface = ifaces.nextElement();

            Enumeration<InetAddress> addrs = networkInterface.getInetAddresses();
            while (addrs.hasMoreElements()) {
                InetAddress inetAddress = addrs.nextElement();
                if (inetAddress instanceof Inet4Address) {
                    final URI uri = new URI("http://" + inetAddress.getCanonicalHostName() + "/cgi-bin/mapserv");
                    assertTrue(matcher.validate(uri));
                }
            }
        }

        assertFalse(matcher.validate(new URI("http://www.google.com/")));
    }
}
