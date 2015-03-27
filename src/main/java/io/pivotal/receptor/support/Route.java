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

package io.pivotal.receptor.support;

/**
 * @author Mark Fisher
 */
public class Route {

	private String[] hostnames;

	private int port;

	@SuppressWarnings("unused")
	private Route() {}

	public Route(int port, String... hostnames) {
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
}
