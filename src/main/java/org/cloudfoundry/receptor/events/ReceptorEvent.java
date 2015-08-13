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

package org.cloudfoundry.receptor.events;

import java.util.Map;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

/**
 * @author Mark Fisher
 */
public abstract class ReceptorEvent<D> {

	private static final ObjectMapper objectMapper = new ObjectMapper();

	private final int id;

	private final String type;

	private Map<String, D> data;

	public ReceptorEvent(int id, String type) {
		this.id = id;
		this.type = type;
	}

	public int getId() {
		return id;
	}

	public String getType() {
		return type;
	}

	public Map<String, D> getData() {
		return data;
	}

	public void setData(Map<String, D> data) {
		this.data = data;
	}

	@Override
	public String toString() {
		StringBuilder builder = new StringBuilder();
		builder.append(String.format("id: %s%n", id));
		builder.append(String.format("event: %s%n", type));
		String dataString;
		try {
			dataString = objectMapper.writeValueAsString(data);
		}
		catch (JsonProcessingException e) {
			dataString = (data != null ? data.toString() : null);
		}
		builder.append(String.format("data: %s%n", dataString));
		return builder.toString();
	}
}
