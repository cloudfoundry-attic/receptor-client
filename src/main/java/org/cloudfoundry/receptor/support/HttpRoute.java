package org.cloudfoundry.receptor.support;

import java.util.Arrays;

/**
 * @author Mark Fisher
 * @author Matt Stine
 */
public class HttpRoute implements Route {
    private String[] hostnames;
    private int port;

    public HttpRoute() {
    }

    public HttpRoute(int port, String... hostnames) {
        this.port = port;
        this.hostnames = hostnames;
    }

    public String[] getHostnames() {
        return hostnames;
    }

    public void setHostnames(String... hostnames) {
        this.hostnames = hostnames;
    }

    public int getPort() {
        return port;
    }

    public void setPort(int port) {
        this.port = port;
    }

    @Override
    public String toString() {
        return "HttpRoute [hostnames=" + Arrays.toString(hostnames) + ", port="
                + port + "]";
    }
}
