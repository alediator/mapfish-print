package org.mapfish.print.config;

import java.net.MalformedURLException;
import java.net.SocketException;
import java.net.URI;
import java.net.UnknownHostException;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Used to validate the access to a map service host
 */
public abstract class HostMatcher {
    protected int port = -1;
    protected String pathRegex = null;

    public boolean validate(URI uri) throws UnknownHostException, SocketException, MalformedURLException {
        int uriPort = uri.getPort();
        if (uriPort < 0) {
            uriPort = uri.toURL().getDefaultPort();
        }
        if (port > 0 && uriPort != port) {
            return false;
        }

        if (pathRegex != null) {
            Matcher matcher = Pattern.compile(pathRegex).matcher(uri.getPath());
            if (!matcher.matches()) {
                return false;
            }
        }
        return true;
    }

    public void setPort(int port) {
        this.port = port;
    }

    public void setPathRegex(String pathRegex) {
        this.pathRegex = pathRegex;
    }

    public abstract String toString();
}
