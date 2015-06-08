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

package org.cloudfoundry.receptor.client;

import java.util.List;

import org.cloudfoundry.receptor.commands.ActualLRPResponse;
import org.cloudfoundry.receptor.commands.CellResponse;
import org.cloudfoundry.receptor.commands.DesiredLRPCreateRequest;
import org.cloudfoundry.receptor.commands.DesiredLRPResponse;
import org.cloudfoundry.receptor.commands.DesiredLRPUpdateRequest;
import org.cloudfoundry.receptor.commands.TaskCreateRequest;
import org.cloudfoundry.receptor.commands.TaskResponse;
import org.cloudfoundry.receptor.events.EventListener;
import org.cloudfoundry.receptor.events.ReceptorEvent;

/**
 * Definition of available operations for interacting with the Receptor API.
 *
 * @author Mark Fisher
 */
public interface ReceptorOperations {

	void createDesiredLRP(DesiredLRPCreateRequest request);

	DesiredLRPResponse getDesiredLRP(String processGuid);

	List<DesiredLRPResponse> getDesiredLRPs();

	List<DesiredLRPResponse> getDesiredLRPsByDomain(String domain);

	void updateDesiredLRP(String processGuid, DesiredLRPUpdateRequest request);

	void deleteDesiredLRP(String processGuid);

	List<ActualLRPResponse> getActualLRPs();

	List<ActualLRPResponse> getActualLRPsByDomain(String domain);

	List<ActualLRPResponse> getActualLRPsByProcessGuid(String processGuid);

	ActualLRPResponse getActualLRPByProcessGuidAndIndex(String processGuid, int index);

	void killActualLRPByProcessGuidAndIndex(String processGuid, int index);

	void createTask(TaskCreateRequest request);

	List<TaskResponse> getTasks();

	List<TaskResponse> getTasksByDomain(String domain);

	TaskResponse getTask(String taskGuid);

	void deleteTask(String taskGuid);

	void cancelTask(String taskGuid);

	List<CellResponse> getCells();

	/**
	 * Mark a domain as fresh for a number of seconds. A value of 0 indicates never expire.
	 * If a non-zero value is provided, the domain will expire after the number of
	 * seconds specified, unless this request is repeated before that time elapses.
	 *
	 * @param domain name of domain to keep fresh
	 * @param ttl number of seconds to keep fresh, or 0 to never expire
	 */
	void upsertDomain(String domain, int ttl);

	String[] getDomains();

	/**
	 * Add an {@link EventListener} to be invoked when a {@link ReceptorEvent} occurs.
	 *
	 * @param listener the listener to invoke
	 */
	<E extends ReceptorEvent<?>> void subscribeToEvents(EventListener<E> listener);

}
