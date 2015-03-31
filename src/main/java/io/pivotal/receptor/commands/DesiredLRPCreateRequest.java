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

import io.pivotal.receptor.actions.Action;
import io.pivotal.receptor.actions.RunAction;
import io.pivotal.receptor.support.EnvironmentVariable;
import io.pivotal.receptor.support.Route;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

import org.springframework.util.ObjectUtils;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;

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

	private Map<String, Route[]> routes = new HashMap<String, Route[]>();

	private EnvironmentVariable[] env = new EnvironmentVariable[] {
		new EnvironmentVariable("PORT", "8080")
	};

	@JsonProperty("cpu_weight")
	private int cpuWeight;

	@JsonProperty("disk_mb")
	private int diskMb = 1024;

	@JsonProperty("memory_mb")
	private int memoryMb = 128;

	private boolean privileged;

	@JsonProperty("no_monitor")
	private boolean noMonitor = false;

	@JsonProperty("log_guid")
	private String logGuid;

	@JsonProperty("metrics_guid")
	private String metricsGuid;

	@JsonProperty("log_source")
	private String logSource = "APP";

	@JsonInclude(Include.NON_EMPTY)
	@JsonDeserialize(using = ActionMapSerializer.class)
	private Map<String, Action> setup = new HashMap<String, Action>();

	@JsonDeserialize(using = ActionMapSerializer.class)
	private Map<String, Action> action = new HashMap<String, Action>();

	@JsonInclude(Include.NON_EMPTY)
	@JsonDeserialize(using = ActionMapSerializer.class)
	private Map<String, Action> monitor = new HashMap<String, Action>();

	@JsonProperty("start_timeout")
	private int startTimeout;

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

	public int getCpuWeight() {
		return cpuWeight;
	}

	public void setCpuWeight(int cpuWeight) {
		this.cpuWeight = cpuWeight;
	}

	public boolean isPrivileged() {
		return privileged;
	}

	public void setPriviliged(boolean priviliged) {
		this.privileged = priviliged;
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

	public String getMetricsGuid() {
		return metricsGuid;
	}

	public void setMetricsGuid(String metricsGuid) {
		this.metricsGuid = metricsGuid;
	}

	public Map<String, Action> getSetup() {
		return setup;
	}

	public void setSetup(Map<String, Action> setup) {
		this.setup = setup;
	}

	public Map<String, Action> getAction() {
		return action;
	}

	public void setAction(Map<String, Action> action) {
		this.action = action;
	}

	@JsonIgnore
	public RunAction runAction() {
		action.putIfAbsent("run", new RunAction());
		return (RunAction) action.get("run");
	}

	public Map<String, Action> getMonitor() {
		return monitor;
	}

	public void setMonitor(Map<String, Action> monitor) {
		this.monitor = monitor;
	}

	public int getStartTimeout() {
		return startTimeout;
	}

	public void setStartTimeout(int startTimeout) {
		this.startTimeout = startTimeout;
	}

	@Override
	public String toString() {
		return "DesiredLRPCreateRequest [processGuid=" + processGuid + ", domain=" + domain + ", rootfs=" + rootfs
				+ ", instances=" + instances + ", stack=" + stack + ", ports=" + Arrays.toString(ports) + ", routes="
				+ routes + ", env=" + Arrays.toString(env) + ", cpuWeight=" + cpuWeight + ", diskMb=" + diskMb
				+ ", memoryMb=" + memoryMb + ", privileged=" + privileged + ", noMonitor=" + noMonitor + ", logGuid="
				+ logGuid + ", metricsGuid=" + metricsGuid + ", logSource=" + logSource + ", setup=" + setup
				+ ", action=" + action + ", monitor=" + monitor + ", startTimeout=" + startTimeout + "]";
	}

}
