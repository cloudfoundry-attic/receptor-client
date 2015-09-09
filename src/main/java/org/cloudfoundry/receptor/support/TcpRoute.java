/*
 * Copyright 2015 the original author or authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

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
