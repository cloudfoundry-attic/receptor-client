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

package io.pivotal.receptor.commands;

import io.pivotal.receptor.actions.RunAction;
import io.pivotal.receptor.support.EnvironmentVariable;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

import org.springframework.util.ObjectUtils;

import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * @author Mark Fisher
 */
public class DesiredLRPCreateRequest {

	@JsonProperty("process_guid")
	private String processGuid;

	private String domain = "lattice";

	private String rootfs;

	private int instances = 1;

	private String stack = "lucid64";

	private int[] ports = new int[] { 8080 };

	private Map<String, Route[]> routes = new HashMap<String, DesiredLRPCreateRequest.Route[]>();

	private EnvironmentVariable[] env = new EnvironmentVariable[] {
		new EnvironmentVariable("PORT", "8080")
	};

	@JsonProperty("memory_mb")
	private int memoryMb = 128;

	@JsonProperty("disk_mb")
	private int diskMb = 1024;

	@JsonProperty("no_monitor")
	private boolean noMonitor = false;

	@JsonProperty("log_guid")
	private String logGuid;

	@JsonProperty("log_source")
	private String logSource = "APP";

	public final RunAction runAction = new RunAction();

	private final Map<String,RunAction> action = Collections.singletonMap("run", runAction);

	public String getProcessGuid() {
		return processGuid;
	}

	public void setProcessGuid(String processGuid) {
		this.processGuid = processGuid;
	}

	public String getDomain() {
		return domain;
	}

	public void setDomain(String domain) {
		this.domain = domain;
	}

	public String getRootfs() {
		return rootfs;
	}

	public void setRootfs(String rootfs) {
		this.rootfs = rootfs;
	}

	public int getInstances() {
		return instances;
	}

	public void setInstances(int instances) {
		this.instances = instances;
	}

	public String getStack() {
		return stack;
	}

	public void setStack(String stack) {
		this.stack = stack;
	}

	public int[] getPorts() {
		return ports;
	}

	public void setPorts(int[] ports) {
		this.ports = ports;
	}

	public Map<String, Route[]> getRoutes() {
		return routes;
	}

	public void setRoutes(Map<String, Route[]> routes) {
		this.routes = routes;
	}

	public void addRoute(int port, String... hostnames) {
		this.routes.put("cf-router", ObjectUtils.addObjectToArray(this.routes.get("cf-router"), new Route(port, hostnames)));
	}

	public void setEnv(EnvironmentVariable[] env) {
		this.env = env;
	}

	public EnvironmentVariable[] getEnv() {
		return env;
	}

	public int getMemoryMb() {
		return memoryMb;
	}

	public void setMemoryMb(int memoryMb) {
		this.memoryMb = memoryMb;
	}

	public int getDiskMb() {
		return diskMb;
	}

	public void setDiskMb(int diskMb) {
		this.diskMb = diskMb;
	}

	public boolean isNoMonitor() {
		return noMonitor;
	}

	public void setNoMonitor(boolean noMonitor) {
		this.noMonitor = noMonitor;
	}

	public String getLogGuid() {
		return (logGuid == null ? processGuid : logGuid);
	}

	public void setLogGuid(String logGuid) {
		this.logGuid = logGuid;
	}

	public String getLogSource() {
		return logSource;
	}

	public void setLogSource(String logSource) {
		this.logSource = logSource;
	}

	public Map<String,RunAction> getAction() {
		return action;
	}

	@SuppressWarnings("unused")
	private static class Route {

		private String[] hostnames;

		private int port;

		private Route() {}

		private Route(int port, String... hostnames) {
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
}
