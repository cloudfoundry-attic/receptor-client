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
import io.pivotal.receptor.events.EventDispatcher;
import io.pivotal.receptor.events.EventListener;
import io.pivotal.receptor.events.ReceptorEvent;

import java.util.List;

import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.client.ClientHttpRequestFactory;
import org.springframework.web.client.RestTemplate;

/**
 * Client for the Receptor API exposed by Diego.
 * For more info, see: <a href="https://github.com/cloudfoundry-incubator/receptor/blob/master/doc/README.md">https://github.com/cloudfoundry-incubator/receptor/blob/master/doc/README.md</a>
 *
 * @author Mark Fisher
 * @author Matt Stine
 */
public class ReceptorClient implements ReceptorOperations {

	private static final String DEFAULT_RECEPTOR_HOST = "receptor.192.168.11.11.xip.io";

	private static final ParameterizedTypeReference<List<DesiredLRPResponse>> DESIRED_LRP_RESPONSE_LIST_TYPE = new ParameterizedTypeReference<List<DesiredLRPResponse>>(){};

	private static final ParameterizedTypeReference<List<ActualLRPResponse>> ACTUAL_LRP_RESPONSE_LIST_TYPE = new ParameterizedTypeReference<List<ActualLRPResponse>>(){};

	private static final ParameterizedTypeReference<List<TaskResponse>> TASK_RESPONSE_LIST_TYPE = new ParameterizedTypeReference<List<TaskResponse>>(){};

	private static final ParameterizedTypeReference<List<CellResponse>> CELL_RESPONSE_LIST_TYPE = new ParameterizedTypeReference<List<CellResponse>>(){};

	private final String baseUrl;

	private final RestTemplate restTemplate;

	private final EventDispatcher eventDispatcher;

	public ReceptorClient() {
		this(DEFAULT_RECEPTOR_HOST);
	}

	public ReceptorClient(String receptorHost) {
		this(receptorHost, new RestTemplate());
	}

	public ReceptorClient(String receptorHost, ClientHttpRequestFactory factory) {
		this(receptorHost, new RestTemplate(factory));
	}

	private ReceptorClient(String receptorHost, RestTemplate restTemplate) {
		this.baseUrl = (receptorHost.contains("://") ? receptorHost : "http://" + receptorHost) + "/v1";
		this.restTemplate = restTemplate;
		this.eventDispatcher = new EventDispatcher(String.format("%s/events", baseUrl));
	}

	/* (non-Javadoc)
	 * @see io.pivotal.receptor.client.ReceptorOperations#createDesiredLRP(io.pivotal.receptor.commands.DesiredLRPCreateRequest)
	 */
	@Override
	public void createDesiredLRP(DesiredLRPCreateRequest request) {
		restTemplate.postForEntity("{baseUrl}/desired_lrps", request, null, baseUrl);
	}

	/* (non-Javadoc)
	 * @see io.pivotal.receptor.client.ReceptorOperations#getDesiredLRP(java.lang.String)
	 */
	@Override
	public DesiredLRPResponse getDesiredLRP(String processGuid) {
		return restTemplate.exchange("{baseUrl}/desired_lrps/{processGuid}", HttpMethod.GET, null, DesiredLRPResponse.class, baseUrl, processGuid).getBody();
	}

	/* (non-Javadoc)
	 * @see io.pivotal.receptor.client.ReceptorOperations#getDesiredLRPs()
	 */
	@Override
	public List<DesiredLRPResponse> getDesiredLRPs() {
		return restTemplate.exchange("{baseUrl}/desired_lrps", HttpMethod.GET, null, DESIRED_LRP_RESPONSE_LIST_TYPE, baseUrl).getBody();
	}

	/* (non-Javadoc)
	 * @see io.pivotal.receptor.client.ReceptorOperations#getDesiredLRPsByDomain(java.lang.String)
	 */
	@Override
	public List<DesiredLRPResponse> getDesiredLRPsByDomain(String domain) {
		return restTemplate.exchange("{baseUrl}/desired_lrps?domain={domain}", HttpMethod.GET, null, DESIRED_LRP_RESPONSE_LIST_TYPE, baseUrl, domain).getBody();
	}

	/* (non-Javadoc)
	 * @see io.pivotal.receptor.client.ReceptorOperations#updateDesiredLRP(java.lang.String, io.pivotal.receptor.commands.DesiredLRPUpdateRequest)
	 */
	@Override
	public void updateDesiredLRP(String processGuid, DesiredLRPUpdateRequest request) {
		restTemplate.put("{baseUrl}/desired_lrps/{processGuid}", request, baseUrl, processGuid);
	}

	/* (non-Javadoc)
	 * @see io.pivotal.receptor.client.ReceptorOperations#deleteDesiredLRP(java.lang.String)
	 */
	@Override
	public void deleteDesiredLRP(String processGuid) {
		restTemplate.delete("{baseUrl}/desired_lrps/{processGuid}", baseUrl, processGuid);
	}

	/* (non-Javadoc)
	 * @see io.pivotal.receptor.client.ReceptorOperations#getActualLRPs()
	 */
	@Override
	public List<ActualLRPResponse> getActualLRPs() {
		return restTemplate.exchange("{baseUrl}/actual_lrps", HttpMethod.GET, null, ACTUAL_LRP_RESPONSE_LIST_TYPE, baseUrl).getBody();		
	}

	/* (non-Javadoc)
	 * @see io.pivotal.receptor.client.ReceptorOperations#getActualLRPsByDomain(java.lang.String)
	 */
	@Override
	public List<ActualLRPResponse> getActualLRPsByDomain(String domain) {
		return restTemplate.exchange("{baseUrl}/actual_lrps?domain={domain}", HttpMethod.GET, null, ACTUAL_LRP_RESPONSE_LIST_TYPE, baseUrl, domain).getBody();
	}

	/* (non-Javadoc)
	 * @see io.pivotal.receptor.client.ReceptorOperations#getActualLRPsByProcessGuid(java.lang.String)
	 */
	@Override
	public List<ActualLRPResponse> getActualLRPsByProcessGuid(String processGuid) {
		return restTemplate.exchange("{baseUrl}/actual_lrps/{processGuid}", HttpMethod.GET, null, ACTUAL_LRP_RESPONSE_LIST_TYPE, baseUrl, processGuid).getBody();
	}

	/* (non-Javadoc)
	 * @see io.pivotal.receptor.client.ReceptorOperations#getActualLRPByProcessGuidAndIndex(java.lang.String, int)
	 */
	@Override
	public ActualLRPResponse getActualLRPByProcessGuidAndIndex(String processGuid, int index) {
		return restTemplate.exchange("{baseUrl}/actual_lrps/{processGuid}/index/{index}", HttpMethod.GET, null, ActualLRPResponse.class, baseUrl, processGuid, index).getBody();
	}

	/* (non-Javadoc)
	 * @see io.pivotal.receptor.client.ReceptorOperations#killActualLRPByProcessGuidAndIndex(java.lang.String, int)
	 */
	@Override
	public void killActualLRPByProcessGuidAndIndex(String processGuid, int index) {
		restTemplate.delete("{baseUrl}/actual_lrps/{processGuid}/index/{index}", baseUrl, processGuid, index);
	}

	/* (non-Javadoc)
	 * @see io.pivotal.receptor.client.ReceptorOperations#createTask(io.pivotal.receptor.commands.TaskCreateRequest)
	 */
	@Override
	public void createTask(TaskCreateRequest request) {
		restTemplate.postForEntity("{baseUrl}/tasks", request, null, baseUrl);
	}

	/* (non-Javadoc)
	 * @see io.pivotal.receptor.client.ReceptorOperations#getTasks()
	 */
	@Override
	public List<TaskResponse> getTasks() {
		return restTemplate.exchange("{baseUrl}/tasks", HttpMethod.GET, null, TASK_RESPONSE_LIST_TYPE, baseUrl).getBody();
	}

	/* (non-Javadoc)
	 * @see io.pivotal.receptor.client.ReceptorOperations#getTasksByDomain(java.lang.String)
	 */
	@Override
	public List<TaskResponse> getTasksByDomain(String domain) {
		return restTemplate.exchange("{baseUrl}/tasks?domain={domain}", HttpMethod.GET, null, TASK_RESPONSE_LIST_TYPE, baseUrl, domain).getBody();
	}

	/* (non-Javadoc)
	 * @see io.pivotal.receptor.client.ReceptorOperations#getTask(java.lang.String)
	 */
	@Override
	public TaskResponse getTask(String taskGuid) {
		return restTemplate.exchange("{baseUrl}/tasks/{taskGuid}", HttpMethod.GET, null, TaskResponse.class, baseUrl, taskGuid).getBody();
	}

	/* (non-Javadoc)
	 * @see io.pivotal.receptor.client.ReceptorOperations#deleteTask(java.lang.String)
	 */
	@Override
	public void deleteTask(String taskGuid) {
		restTemplate.delete("{baseUrl}/tasks/{taskGuid}", baseUrl, taskGuid);
	}

	/* (non-Javadoc)
	 * @see io.pivotal.receptor.client.ReceptorOperations#cancelTask(java.lang.String)
	 */
	@Override
	public void cancelTask(String taskGuid) {
		restTemplate.postForEntity("{baseUrl}/tasks/{taskGuid}/cancel", "", null, baseUrl, taskGuid);
	}

	/* (non-Javadoc)
	 * @see io.pivotal.receptor.client.ReceptorOperations#getCells()
	 */
	@Override
	public List<CellResponse> getCells() {
		return restTemplate.exchange("{baseUrl}/cells", HttpMethod.GET, null, CELL_RESPONSE_LIST_TYPE, baseUrl).getBody();
	}

	/* (non-Javadoc)
	 * @see io.pivotal.receptor.client.ReceptorOperations#upsertDomain(java.lang.String, int)
	 */
	@Override
	public void upsertDomain(String domain, int ttl) {
		HttpHeaders headers = new HttpHeaders();
		if (ttl != 0) {
			headers.setCacheControl(String.format("max-age=%d", ttl));
		}		
		HttpEntity<String> request = new HttpEntity<String>(headers);
		restTemplate.put("{baseUrl}/domains/{domain}", request, baseUrl, domain);
	}

	/* (non-Javadoc)
	 * @see io.pivotal.receptor.client.ReceptorOperations#getDomains()
	 */
	@Override
	public String[] getDomains() {
		return restTemplate.exchange("{baseUrl}/domains", HttpMethod.GET, null, String[].class, baseUrl).getBody();
	}

	/* (non-Javadoc)
	 * @see io.pivotal.receptor.client.ReceptorOperations#subscribeToEvents(io.pivotal.receptor.events.EventListener)
	 */
	@Override
	public <E extends ReceptorEvent<?>> void subscribeToEvents(EventListener<E> listener) {
		eventDispatcher.addListener(listener);
	}
}
