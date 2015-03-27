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
import io.pivotal.receptor.commands.DesiredLRPCreateRequest;
import io.pivotal.receptor.commands.DesiredLRPResponse;
import io.pivotal.receptor.commands.DesiredLRPUpdateRequest;

import java.util.List;

import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpMethod;
import org.springframework.http.client.ClientHttpRequestFactory;
import org.springframework.web.client.RestTemplate;

/**
 * @author Mark Fisher
 * @author Matt Stine
 */
public class ReceptorClient {

	private static final String DEFAULT_RECEPTOR_HOST = "receptor.192.168.11.11.xip.io";

	private static final ParameterizedTypeReference<List<DesiredLRPResponse>> DESIRED_LRP_RESPONSE_LIST_TYPE = new ParameterizedTypeReference<List<DesiredLRPResponse>>(){};

	private static final ParameterizedTypeReference<List<ActualLRPResponse>> ACTUAL_LRP_RESPONSE_LIST_TYPE = new ParameterizedTypeReference<List<ActualLRPResponse>>(){};

	private final String baseUrl;

	private final RestTemplate restTemplate;

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
	}

	public void createDesiredLRP(DesiredLRPCreateRequest request) {
		restTemplate.postForEntity("{baseUrl}/desired_lrps", request, null, baseUrl);
	}

	public DesiredLRPResponse getDesiredLRP(String processGuid) {
		return restTemplate.exchange("{baseUrl}/desired_lrps/{processGuid}", HttpMethod.GET, null, DesiredLRPResponse.class, baseUrl, processGuid).getBody();
	}

	public List<DesiredLRPResponse> getDesiredLRPs() {
		return restTemplate.exchange("{baseUrl}/desired_lrps", HttpMethod.GET, null, DESIRED_LRP_RESPONSE_LIST_TYPE, baseUrl).getBody();
	}

	public List<DesiredLRPResponse> getDesiredLRPsByDomain(String domain) {
		return restTemplate.exchange("{baseUrl}/desired_lrps?domain={domain}", HttpMethod.GET, null, DESIRED_LRP_RESPONSE_LIST_TYPE, baseUrl, domain).getBody();
	}

	public void updateDesiredLRP(String processGuid, DesiredLRPUpdateRequest request) {
		restTemplate.put("{baseUrl}/desired_lrps/{processGuid}", request, baseUrl, processGuid);
	}

	public void deleteDesiredLRP(String processGuid) {
		restTemplate.delete("{baseUrl}/desired_lrps/{processGuid}", baseUrl, processGuid);
	}

	public List<ActualLRPResponse> getActualLRPs() {
		return restTemplate.exchange("{baseUrl}/actual_lrps", HttpMethod.GET, null, ACTUAL_LRP_RESPONSE_LIST_TYPE, baseUrl).getBody();		
	}

	public List<ActualLRPResponse> getActualLRPsByDomain(String domain) {
		return restTemplate.exchange("{baseUrl}/actual_lrps?domain={domain}", HttpMethod.GET, null, ACTUAL_LRP_RESPONSE_LIST_TYPE, baseUrl, domain).getBody();
	}

	public List<ActualLRPResponse> getActualLRPsByProcessGuid(String processGuid) {
		return restTemplate.exchange("{baseUrl}/actual_lrps/{processGuid}", HttpMethod.GET, null, ACTUAL_LRP_RESPONSE_LIST_TYPE, baseUrl, processGuid).getBody();
	}

	public ActualLRPResponse getActualLRPByProcessGuidAndIndex(String processGuid, int index) {
		return restTemplate.exchange("{baseUrl}/actual_lrps/{processGuid}/index/{index}", HttpMethod.GET, null, ActualLRPResponse.class, baseUrl, processGuid, index).getBody();
	}

	public void killActualLRPByProcessGuidAndIndex(String processGuid, int index) {
		restTemplate.delete("{baseUrl}/actual_lrps/{processGuid}/index/{index}", baseUrl, processGuid, index);
	}
}
