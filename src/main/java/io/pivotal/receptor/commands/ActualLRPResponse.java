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

import com.fasterxml.jackson.annotation.JsonProperty;

import io.pivotal.receptor.support.ModificationTag;
import io.pivotal.receptor.support.Port;

import java.util.Arrays;

/**
 * @author Matt Stine
 * @author Mark Fisher
 */
public class ActualLRPResponse {

	@JsonProperty("process_guid")
	private String processGuid;

	@JsonProperty("instance_guid")
	private String instanceGuid;

	@JsonProperty("cell_id")
	private String cellId;

	private String domain;

	private int index;

	private String address;

	private Port[] ports;

	private String state;

	private long since;

	@JsonProperty("crash_count")
	private int crashCount;

	private boolean evacuating;

	@JsonProperty("modification_tag")
	private ModificationTag modificationTag;

	public String getProcessGuid() {
		return processGuid;
	}

	public void setProcessGuid(String processGuid) {
		this.processGuid = processGuid;
	}

	public String getInstanceGuid() {
		return instanceGuid;
	}

	public void setInstanceGuid(String instanceGuid) {
		this.instanceGuid = instanceGuid;
	}

	public String getCellId() {
		return cellId;
	}

	public void setCellId(String cellId) {
		this.cellId = cellId;
	}

	public String getDomain() {
		return domain;
	}

	public void setDomain(String domain) {
		this.domain = domain;
	}

	public int getIndex() {
		return index;
	}

	public void setIndex(int index) {
		this.index = index;
	}

	public String getAddress() {
		return address;
	}

	public void setAddress(String address) {
		this.address = address;
	}

	public Port[] getPorts() {
		return ports;
	}

	public void setPorts(Port[] ports) {
		this.ports = ports;
	}

	public String getState() {
		return state;
	}

	public void setState(String state) {
		this.state = state;
	}

	public long getSince() {
		return since;
	}

	public void setSince(long since) {
		this.since = since;
	}

	public int getCrashCount() {
		return crashCount;
	}

	public void setCrashCount(int crashCount) {
		this.crashCount = crashCount;
	}

	public boolean isEvacuating() {
		return evacuating;
	}

	public void setEvacuating(boolean evacuating) {
		this.evacuating = evacuating;
	}

	public ModificationTag getModificationTag() {
		return modificationTag;
	}

	public void setModificationTag(ModificationTag modificationTag) {
		this.modificationTag = modificationTag;
	}

	@Override
	public String toString() {
		return "ActualLRPResponse{" + "processGuid='" + processGuid + '\''
				+ ", instanceGuid='" + instanceGuid + '\'' + ", cellId='"
				+ cellId + '\'' + ", domain='" + domain + '\'' + ", index="
				+ index + ", address='" + address + '\'' + ", ports="
				+ Arrays.toString(ports) + ", state='" + state + '\''
				+ ", since=" + since + '}';
	}
}
