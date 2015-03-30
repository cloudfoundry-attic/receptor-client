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

/**
 * @author Mark Fisher
 */
public class CellResponse {

	@JsonProperty("cell_id")
	private String cellId;

	private String zone;

	private Capacity capacity;

	public String getCellId() {
		return cellId;
	}

	public void setCellId(String cellId) {
		this.cellId = cellId;
	}

	public String getZone() {
		return zone;
	}

	public void setZone(String zone) {
		this.zone = zone;
	}

	public Capacity getCapacity() {
		return capacity;
	}

	public void setCapacity(Capacity capacity) {
		this.capacity = capacity;
	}

	@Override
	public String toString() {
		return "CellResponse [cellId=" + cellId + ", zone=" + zone + ", capacity=" + capacity + "]";
	}

	public static class Capacity {

		@JsonProperty("memory_mb")
		private int memoryMb;

		@JsonProperty("disk_mb")
		private int diskMb;

		private int containers;

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

		public int getContainers() {
			return containers;
		}

		public void setContainers(int containers) {
			this.containers = containers;
		}

		@Override
		public String toString() {
			return "Capacity [memoryMb=" + memoryMb + ", diskMb=" + diskMb + ", containers=" + containers + "]";
		}
	}
}
