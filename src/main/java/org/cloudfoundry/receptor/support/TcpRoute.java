package org.cloudfoundry.receptor.support;

import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * @author Matt Stine
 */
public class TcpRoute implements Route {

    @JsonProperty("external_port")
    private int externalPort;

    @JsonProperty("container_port")
    private int containerPort;

    public TcpRoute() {
    }

    public TcpRoute(int externalPort, int port) {
        this.externalPort = externalPort;
        this.containerPort = port;
    }

    public int getExternalPort() {
        return externalPort;
    }

    public void setExternalPort(int externalPort) {
        this.externalPort = externalPort;
    }

    public int getContainerPort() {
        return containerPort;
    }

    public void setContainerPort(int containerPort) {
        this.containerPort = containerPort;
    }

    @Override
    public String toString() {
        return "TcpRoute [externalPort=" + externalPort + ", containerPort="
                + containerPort + "]";
    }
}
