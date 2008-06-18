package org.mapfish.print.config;

import org.apache.log4j.Logger;

import java.net.InetAddress;
import java.net.MalformedURLException;
import java.net.SocketException;
import java.net.URI;
import java.net.UnknownHostException;

public abstract class InetHostMatcher extends HostMatcher {
    public static final Logger LOGGER = Logger.getLogger(InetHostMatcher.class);

    protected byte[][] authorizedIPs = null;

    public boolean validate(URI uri) throws UnknownHostException, SocketException, MalformedURLException {
        final InetAddress maskAddress = getMaskAddress();
        final InetAddress[] requestedIPs = InetAddress.getAllByName(uri.getHost());
        for (int i = 0; i < requestedIPs.length; ++i) {
            InetAddress requestedIP = requestedIPs[i];
            if (!isInAuthorized(requestedIP, maskAddress)) {
                return false;
            }
        }
        return super.validate(uri);
    }

    private boolean isInAuthorized(InetAddress requestedIP, InetAddress mask) throws UnknownHostException, SocketException {
        byte[] rBytes = mask(requestedIP, mask);
        final byte[][] authorizedIPs = getAuthorizedIPs(mask);
        for (int i = 0; i < authorizedIPs.length; ++i) {
            byte[] authorizedIP = authorizedIPs[i];
            if (compareIP(rBytes, authorizedIP)) {
                return true;
            }
        }
        return false;
    }

    private boolean compareIP(byte[] rBytes, byte[] authorizedIP) {
        if (rBytes.length != authorizedIP.length) {
            return false;
        }
        for (int j = 0; j < authorizedIP.length; ++j) {
            byte bA = authorizedIP[j];
            byte bR = rBytes[j];
            if (bA != bR) {
                return false;
            }
        }
        return true;
    }

    private byte[] mask(InetAddress address, InetAddress mask) {
        byte[] aBytes = address.getAddress();
        if (mask != null) {
            byte[] mBytes = mask.getAddress();
            if (aBytes.length != mBytes.length) {
                LOGGER.warn("Cannot mask address [" + address + "] with :" + mask);
                return aBytes;
            } else {
                final byte[] result = new byte[aBytes.length];
                for (int i = 0; i < result.length; ++i) {
                    result[i] = (byte) (aBytes[i] & mBytes[i]);
                }
                return result;
            }
        } else {
            return aBytes;
        }
    }

    protected abstract InetAddress getMaskAddress() throws UnknownHostException;

    protected void buildMaskedAuthorizedIPs(InetAddress[] ips) throws UnknownHostException {
        final InetAddress maskAddress = getMaskAddress();
        authorizedIPs = new byte[ips.length][];
        for (int i = 0; i < ips.length; ++i) {
            authorizedIPs[i] = mask(ips[i], maskAddress);
        }
    }

    protected abstract byte[][] getAuthorizedIPs(InetAddress mask) throws UnknownHostException, SocketException;
}