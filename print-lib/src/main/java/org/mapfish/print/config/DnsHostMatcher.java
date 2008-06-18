package org.mapfish.print.config;

import java.net.MalformedURLException;
import java.net.SocketException;
import java.net.URI;
import java.net.UnknownHostException;

public class DnsHostMatcher extends HostMatcher {
    private String host = null;

    /**
     * Check the given URI to see if it matches.
     *
     * @return True if it matches.
     */
    public boolean validate(URI uri) throws UnknownHostException, SocketException, MalformedURLException {
        if (!uri.getHost().equals(host)) {
            return false;
        }
        return super.validate(uri);
    }


    public String toString() {
        final StringBuilder sb = new StringBuilder();
        sb.append("DnsHostMatcher");
        sb.append("{host='").append(host).append('\'');
        if (port >= 0) {
            sb.append(", port=").append(port);
        }
        if (pathRegex != null) {
            sb.append(", pathRegexp=").append(pathRegex);
        }
        sb.append('}');
        return sb.toString();
    }

    public void setHost(String host) {
        this.host = host;
    }
}