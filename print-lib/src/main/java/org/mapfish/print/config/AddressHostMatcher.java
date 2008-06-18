package org.mapfish.print.config;

import org.apache.log4j.Logger;

import java.net.InetAddress;
import java.net.SocketException;
import java.net.UnknownHostException;

public class AddressHostMatcher extends InetHostMatcher {
    public static final Logger LOGGER = Logger.getLogger(InetHostMatcher.class);

    private String ip = null;
    private String mask = null;

    private InetAddress maskAddress = null;

    protected byte[][] getAuthorizedIPs(InetAddress mask) throws UnknownHostException, SocketException {
        if (authorizedIPs == null) {
            InetAddress[] ips = InetAddress.getAllByName(ip);
            buildMaskedAuthorizedIPs(ips);
        }
        return authorizedIPs;
    }

    protected InetAddress getMaskAddress() throws UnknownHostException {
        if (maskAddress == null && mask != null) {
            maskAddress = InetAddress.getByName(mask);
        }
        return maskAddress;
    }

    public void setIp(String ip) {
        this.ip = ip;
    }

    public void setMask(String mask) {
        this.mask = mask;
    }


    public String toString() {
        final StringBuilder sb = new StringBuilder();
        sb.append("AddressHostMatcher");
        sb.append("{ip='").append(ip).append('\'');
        if (mask != null) {
            sb.append(", mask='").append(mask).append('\'');
        }
        if (port >= 0) {
            sb.append(", port=").append(port);
        }
        if (pathRegex != null) {
            sb.append(", pathRegexp=").append(pathRegex);
        }
        sb.append('}');
        return sb.toString();
    }
}