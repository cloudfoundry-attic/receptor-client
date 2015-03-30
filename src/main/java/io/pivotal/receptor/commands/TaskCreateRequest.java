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

import java.util.Arrays;
import java.util.Collections;
import java.util.Map;

import io.pivotal.receptor.actions.RunAction;
import io.pivotal.receptor.support.EnvironmentVariable;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * @author Mark Fisher
 */
public class TaskCreateRequest {

	@JsonProperty("task_guid")
	private String taskGuid;

	private String domain = "lattice";

	private String stack = "lucid64";

	private String rootfs;

	private EnvironmentVariable[] env = new EnvironmentVariable[] {};

	@JsonProperty("cpu_weight")
	private int cpuWeight;

	@JsonProperty("disk_mb")
	private int diskMb;

	@JsonProperty("memory_mb")
	private int memoryMb;

	private boolean privileged;

	@JsonIgnore
	public RunAction runAction = new RunAction();

	@JsonProperty("result_file")
	private String resultFile;

	@JsonProperty("completion_callback_url")
	private String completionCallbackUrl;

	@JsonProperty("log_guid")
	private String logGuid;

	@JsonProperty("log_source")
	private String logSource;

	private String annotation;

	@JsonProperty("egress_rules")
	private EgressRule[] egressRules = new EgressRule[] {};

	public String getTaskGuid() {
		return taskGuid;
	}

	public void setTaskGuid(String taskGuid) {
		this.taskGuid = taskGuid;
	}

	public String getDomain() {
		return domain;
	}

	public void setDomain(String domain) {
		this.domain = domain;
	}

	public String getStack() {
		return stack;
	}

	public void setStack(String stack) {
		this.stack = stack;
	}

	public String getRootfs() {
		return rootfs;
	}

	public void setRootfs(String rootfs) {
		this.rootfs = rootfs;
	}

	public EnvironmentVariable[] getEnv() {
		return env;
	}

	public void setEnv(EnvironmentVariable[] env) {
		this.env = env;
	}

	public int getCpuWeight() {
		return cpuWeight;
	}

	public void setCpuWeight(int cpuWeight) {
		this.cpuWeight = cpuWeight;
	}

	public int getDiskMb() {
		return diskMb;
	}

	public void setDiskMb(int diskMb) {
		this.diskMb = diskMb;
	}

	public int getMemoryMb() {
		return memoryMb;
	}

	public void setMemoryMb(int memoryMb) {
		this.memoryMb = memoryMb;
	}

	public boolean isPrivileged() {
		return privileged;
	}

	public void setPrivileged(boolean privileged) {
		this.privileged = privileged;
	}

	public Map<String, RunAction> getAction() {
		return Collections.singletonMap("run",  runAction);
	}

	public void setAction(Map<String, RunAction> action) {
		this.runAction = action.get("run");
	}

	public String getResultFile() {
		return resultFile;
	}

	public void setResultFile(String resultFile) {
		this.resultFile = resultFile;
	}

	public String getCompletionCallbackUrl() {
		return completionCallbackUrl;
	}

	public void setCompletionCallbackUrl(String completionCallbackUrl) {
		this.completionCallbackUrl = completionCallbackUrl;
	}

	public String getLogGuid() {
		return logGuid;
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

	public String getAnnotation() {
		return annotation;
	}

	public void setAnnotation(String annotation) {
		this.annotation = annotation;
	}

	public EgressRule[] getEgressRules() {
		return egressRules;
	}

	public void setEgressRules(EgressRule[] egressRules) {
		this.egressRules = egressRules;
	}

	@Override
	public String toString() {
		return "TaskCreateRequest [taskGuid=" + taskGuid + ", domain=" + domain + ", stack=" + stack + ", rootfs="
				+ rootfs + ", env=" + Arrays.toString(env) + ", cpuWeight=" + cpuWeight + ", diskMb=" + diskMb
				+ ", memoryMb=" + memoryMb + ", privileged=" + privileged + ", runAction=" + runAction
				+ ", resultFile=" + resultFile + ", completionCallbackUrl=" + completionCallbackUrl + ", logGuid="
				+ logGuid + ", logSource=" + logSource + ", annotation=" + annotation + ", egressRules="
				+ Arrays.toString(egressRules) + "]";
	}

	public static class EgressRule {

		private String protocol;

		private String[] destinations = new String[] {};

		private PortRange portRange;

		public String getProtocol() {
			return protocol;
		}

		public void setProtocol(String protocol) {
			this.protocol = protocol;
		}

		public String[] getDestinations() {
			return destinations;
		}

		public void setDestinations(String[] destinations) {
			this.destinations = destinations;
		}

		public PortRange getPortRange() {
			return portRange;
		}

		public void setPortRange(PortRange portRange) {
			this.portRange = portRange;
		}

		@Override
		public String toString() {
			return "EgressRule [protocol=" + protocol + ", destinations=" + Arrays.toString(destinations)
					+ ", portRange=" + portRange + "]";
		}
	}

	public static class PortRange {

		private int start;

		private int end;

		public int getStart() {
			return start;
		}

		public void setStart(int start) {
			this.start = start;
		}

		public int getEnd() {
			return end;
		}

		public void setEnd(int end) {
			this.end = end;
		}

		@Override
		public String toString() {
			return "PortRange [start=" + start + ", end=" + end + "]";
		}
	}
}
