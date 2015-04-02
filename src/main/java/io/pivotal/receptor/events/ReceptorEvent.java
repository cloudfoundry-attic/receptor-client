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

package io.pivotal.receptor.events;

import java.util.Map;

/**
 * @author Mark Fisher
 */
public abstract class ReceptorEvent<D> {

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
		return this.getClass().getSimpleName();
	}

	public Map<String, D> getData() {
		return data;
	}

	public void setData(Map<String, D> data) {
		this.data = data;
	}

	@Override
	public String toString() {
		return "Event [id=" + id + ", type=" + type + ", data=" + data + "]";
	}
}
