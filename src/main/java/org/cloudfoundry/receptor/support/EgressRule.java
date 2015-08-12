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

import java.util.Arrays;

import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * @author Mark Fisher
 */
public class EgressRule {

	private String protocol;

	private String[] destinations = new String[] {};

	private int[] ports = new int[] {};

	@JsonProperty("port_range")
	private PortRange portRange;

	@JsonProperty("icmp_info")
	private IcmpInfo icmpInfo;

	private boolean log;

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

	public int[] getPorts() {
		return ports;
	}

	public void setPorts(int[] ports) {
		this.ports = ports;
	}

	public PortRange getPortRange() {
		return portRange;
	}

	public void setPortRange(PortRange portRange) {
		this.portRange = portRange;
	}

	public IcmpInfo getIcmpInfo() {
		return icmpInfo;
	}

	public void setIcmpInfo(IcmpInfo icmpInfo) {
		this.icmpInfo = icmpInfo;
	}

	public boolean isLog() {
		return log;
	}

	public void setLog(boolean log) {
		this.log = log;
	}

	@Override
	public String toString() {
		return "EgressRule [protocol=" + protocol + ", destinations="
				+ Arrays.toString(destinations) + ", ports="
				+ Arrays.toString(ports) + ", portRange=" + portRange
				+ ", icmpInfo=" + icmpInfo + ", log=" + log + "]";
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

	public static class IcmpInfo {

		private int type;

		private int code;

		public int getType() {
			return type;
		}

		public void setType(int type) {
			this.type = type;
		}

		public int getCode() {
			return code;
		}

		public void setCode(int code) {
			this.code = code;
		}

		@Override
		public String toString() {
			return "IcmpInfo [type=" + type + ", code=" + code + "]";
		}
	}
}
