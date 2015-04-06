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

package io.pivotal.receptor.client;

import io.pivotal.receptor.commands.ActualLRPResponse;
import io.pivotal.receptor.commands.CellResponse;
import io.pivotal.receptor.commands.DesiredLRPCreateRequest;
import io.pivotal.receptor.commands.DesiredLRPResponse;
import io.pivotal.receptor.commands.DesiredLRPUpdateRequest;
import io.pivotal.receptor.commands.TaskCreateRequest;
import io.pivotal.receptor.commands.TaskResponse;
import io.pivotal.receptor.events.EventListener;
import io.pivotal.receptor.events.ReceptorEvent;

import java.util.List;

/**
 * @author Mark Fisher
 */
public interface ReceptorOperations {

	public abstract void createDesiredLRP(DesiredLRPCreateRequest request);

	public abstract DesiredLRPResponse getDesiredLRP(String processGuid);

	public abstract List<DesiredLRPResponse> getDesiredLRPs();

	public abstract List<DesiredLRPResponse> getDesiredLRPsByDomain(String domain);

	public abstract void updateDesiredLRP(String processGuid, DesiredLRPUpdateRequest request);

	public abstract void deleteDesiredLRP(String processGuid);

	public abstract List<ActualLRPResponse> getActualLRPs();

	public abstract List<ActualLRPResponse> getActualLRPsByDomain(String domain);

	public abstract List<ActualLRPResponse> getActualLRPsByProcessGuid(String processGuid);

	public abstract ActualLRPResponse getActualLRPByProcessGuidAndIndex(String processGuid, int index);

	public abstract void killActualLRPByProcessGuidAndIndex(String processGuid, int index);

	public abstract void createTask(TaskCreateRequest request);

	public abstract List<TaskResponse> getTasks();

	public abstract List<TaskResponse> getTasksByDomain(String domain);

	public abstract TaskResponse getTask(String taskGuid);

	public abstract void deleteTask(String taskGuid);

	public abstract void cancelTask(String taskGuid);

	public abstract List<CellResponse> getCells();

	/**
	 * Mark a domain as fresh for a number of seconds. A value of 0 indicates never expire.
	 * If a non-zero value is provided, the domain will expire after the number of
	 * seconds specified, unless this request is repeated before that time elapses.
	 *
	 * @param domain name of domain to keep fresh
	 * @param ttl number of seconds to keep fresh, or 0 to never expire
	 */
	public abstract void upsertDomain(String domain, int ttl);

	public abstract String[] getDomains();

	/**
	 * Add an {@link EventListener} to be invoked when a {@link ReceptorEvent} occurs.
	 *
	 * @param listener the listener to invoke
	 */
	public abstract <E extends ReceptorEvent<?>> void subscribeToEvents(EventListener<E> listener);

}
