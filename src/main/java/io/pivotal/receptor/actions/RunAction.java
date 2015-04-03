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

package io.pivotal.receptor.actions;

import io.pivotal.receptor.support.EnvironmentVariable;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * @author Mark Fisher
 */
public class RunAction implements Action {

	private String path;

	private String dir;

	private final ArrayList<String> args = new ArrayList<String>();

	private EnvironmentVariable[] env;

	@JsonProperty("resource_limits")
	private Map<String, String> resourceLimits = new HashMap<String,String>();

	private boolean privileged;

	@JsonProperty("log_source")
	private String logSource;

	public String getPath() {
		return path;
	}

	public void setPath(String path) {
		this.path = path;
	}

	public String getDir() {
		return dir;
	}

	public void setDir(String dir) {
		this.dir = dir;
	}

	public String[] getArgs() {
		return args.toArray(new String[args.size()]);
	}

	public void setArgs(String[] args) {
		this.args.clear();
		this.args.addAll(Arrays.asList(args));
	}

	public void addArg(String arg) {
		this.args.add(arg);
	}

	public EnvironmentVariable[] getEnv() {
		return env;
	}

	public void setEnv(EnvironmentVariable[] env) {
		this.env = env;
	}

	public Map<String, String> getResourceLimits() {
		return resourceLimits;
	}

	public void setResourceLimits(Map<String, String> resourceLimits) {
		this.resourceLimits = resourceLimits;
	}

	public boolean isPrivileged() {
		return privileged;
	}

	public void setPrivileged(boolean privileged) {
		this.privileged = privileged;
	}

	public String getLogSource() {
		return logSource;
	}

	public void setLogSource(String logSource) {
		this.logSource = logSource;
	}

	@Override
	public String toString() {
		return "RunAction [path=" + path + ", args=" + args + ", dir=" + dir + ", resourceLimits=" + resourceLimits
				+ ", env=" + Arrays.toString(env) + ", logSource=" + logSource + "]";
	}
}
