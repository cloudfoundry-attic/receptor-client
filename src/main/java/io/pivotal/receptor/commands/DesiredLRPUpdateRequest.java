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

import io.pivotal.receptor.support.Route;

import java.util.HashMap;
import java.util.Map;

import org.springframework.util.ObjectUtils;

/**
 * @author Mark Fisher
 */
public class DesiredLRPUpdateRequest {

	private int instances = 1;

	private Map<String, Route[]> routes = new HashMap<String, Route[]>();

	private String annotation;

	public int getInstances() {
		return instances;
	}

	public void setInstances(int instances) {
		this.instances = instances;
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

	public void setAnnotation(String annotation) {
		this.annotation = annotation;
	}

	public String getAnnotation() {
		return annotation;
	}
}
