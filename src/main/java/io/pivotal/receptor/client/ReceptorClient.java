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
		this.baseUrl = "http://" + receptorHost + "/v1";
		this.restTemplate = restTemplate;
	}

	public void createLongRunningProcess(DesiredLRPCreateRequest process) {
		restTemplate.postForEntity("{baseUrl}/desired_lrps", process, null, baseUrl);
	}

	public void destroyLongRunningProcess(String guid) {
		restTemplate.delete("{baseUrl}/desired_lrps/{guid}", baseUrl, guid);
	}

	public List<ActualLRPResponse> findAllLongRunningProcesses() {
		return restTemplate.exchange("{baseUrl}/actual_lrps", HttpMethod.GET, null, ACTUAL_LRP_RESPONSE_LIST_TYPE, baseUrl).getBody();		
	}

	public List<ActualLRPResponse> findLongRunningProcesses(String guid) {
		return restTemplate.exchange("{baseUrl}/actual_lrps/{guid}", HttpMethod.GET, null, ACTUAL_LRP_RESPONSE_LIST_TYPE, baseUrl, guid).getBody();
	}
}
