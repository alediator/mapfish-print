package org.mapfish.print.config;

import java.net.InetAddress;
import java.net.NetworkInterface;
import java.net.SocketException;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.Enumeration;

public class LocalHostMatcher extends InetHostMatcher {

    protected byte[][] getAuthorizedIPs(InetAddress mask) throws UnknownHostException, SocketException {
        if (authorizedIPs == null) {
            InetAddress[] result;
            Enumeration<NetworkInterface> ifaces = NetworkInterface.getNetworkInterfaces();
            ArrayList<InetAddress> addresses = new ArrayList<InetAddress>();
            while (ifaces.hasMoreElements()) {
                NetworkInterface networkInterface = ifaces.nextElement();
                Enumeration<InetAddress> addrs = networkInterface.getInetAddresses();
                while (addrs.hasMoreElements()) {
                    addresses.add(addrs.nextElement());
                }
            }
            result = addresses.toArray(new InetAddress[addresses.size()]);

            buildMaskedAuthorizedIPs(result);
        }
        return authorizedIPs;
    }

    public String toString() {
        final StringBuilder sb = new StringBuilder();
        sb.append("LocalHostMatcher");
        sb.append("{");
        if (port >= 0) {
            sb.append("port=").append(port);
        }
        if (pathRegex != null) {
            sb.append(", pathRegexp=").append(pathRegex);
        }
        sb.append('}');
        return sb.toString();
    }

    protected InetAddress getMaskAddress() throws UnknownHostException {
        return null;
    }

    @SuppressWarnings({"EmptyMethod"})
    public void setDummy(boolean dummy) {
        //YAML parser always need some content
    }
}
