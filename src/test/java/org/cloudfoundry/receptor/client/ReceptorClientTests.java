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

import static org.junit.Assert.assertArrayEquals;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.mockito.Matchers.eq;
import static org.mockito.Matchers.isNull;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoMoreInteractions;
import static org.mockito.Mockito.when;

import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import org.cloudfoundry.receptor.actions.RunAction;
import org.cloudfoundry.receptor.client.ReceptorClient;
import org.cloudfoundry.receptor.commands.ActualLRPResponse;
import org.cloudfoundry.receptor.commands.CellResponse;
import org.cloudfoundry.receptor.commands.DesiredLRPCreateRequest;
import org.cloudfoundry.receptor.commands.DesiredLRPResponse;
import org.cloudfoundry.receptor.commands.DesiredLRPUpdateRequest;
import org.cloudfoundry.receptor.commands.TaskCreateRequest;
import org.cloudfoundry.receptor.commands.TaskResponse;
import org.junit.Before;
import org.junit.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.client.RestOperations;

/**
 * @author Michael Minella
 */
@SuppressWarnings({"rawtypes", "unchecked"})
public class ReceptorClientTests {

	private static final String APP_DOCKER_PATH = "docker:///cloudfoundry/lattice-app";

	public static final String BASE_URL = "http://localhost/v1";

	@Mock
	private RestOperations restTemplate;

	private ReceptorClient receptorClient;

	@Before
	public void setUp() {
		MockitoAnnotations.initMocks(this);

		this.receptorClient = new ReceptorClient("localhost", restTemplate);
	}

	@Test
	public void testCreateDesiredLRP() throws Exception {
		ArgumentCaptor<DesiredLRPCreateRequest> requestCaptor = ArgumentCaptor.forClass(DesiredLRPCreateRequest.class);

		DesiredLRPCreateRequest request = getDesiredLRPCreateRequest();

		receptorClient.createDesiredLRP(request);

		verify(restTemplate).postForEntity(eq("{baseUrl}/desired_lrps"), requestCaptor.capture(), (Class) isNull(), eq(BASE_URL));
		verifyNoMoreInteractions(restTemplate);

		DesiredLRPCreateRequest receivedRequest = (DesiredLRPCreateRequest) requestCaptor.getValue();
		assertDesiredLRPRequest(receivedRequest);
	}

	@Test
	public void testGetDesiredLRP() {
		ResponseEntity<DesiredLRPResponse> entity = new ResponseEntity(getDesiredLRPResponse(), HttpStatus.OK);

		when(restTemplate.exchange("{baseUrl}/desired_lrps/{processGuid}", HttpMethod.GET, null, DesiredLRPResponse.class, BASE_URL, "123")).thenReturn(entity);

		DesiredLRPResponse response = receptorClient.getDesiredLRP("123");
		assertEquals(response.getProcessGuid(), "123");
		assertEquals(response.getRootfs(), APP_DOCKER_PATH);
		assertEquals(response.runAction().getPath(), "/lattice-app");
	}

	@Test
	public void testGetDesiredLRPs() {
		ResponseEntity<List<DesiredLRPResponse>> entity = new ResponseEntity(Collections.singletonList(getDesiredLRPResponse()), HttpStatus.OK);

		when(restTemplate.exchange("{baseUrl}/desired_lrps", HttpMethod.GET, null, ReceptorClient.DESIRED_LRP_RESPONSE_LIST_TYPE, BASE_URL)).thenReturn(entity);

		DesiredLRPResponse response = receptorClient.getDesiredLRPs().get(0);
		assertEquals(response.getProcessGuid(), "123");
		assertEquals(response.getRootfs(), APP_DOCKER_PATH);
		assertEquals(response.runAction().getPath(), "/lattice-app");
	}

	@Test
	public void testGetDesiredLRPsByDomain() {
		ResponseEntity<List<DesiredLRPResponse>> entity = new ResponseEntity(Collections.singletonList(getDesiredLRPResponse()), HttpStatus.OK);

		when(restTemplate.exchange("{baseUrl}/desired_lrps?domain={domain}", HttpMethod.GET, null, ReceptorClient.DESIRED_LRP_RESPONSE_LIST_TYPE, BASE_URL, "foo")).thenReturn(entity);

		DesiredLRPResponse response = receptorClient.getDesiredLRPsByDomain("foo").get(0);
		assertEquals(response.getProcessGuid(), "123");
		assertEquals(response.getRootfs(), APP_DOCKER_PATH);
		assertEquals(response.runAction().getPath(), "/lattice-app");
	}

	@Test
	public void testUpdateDesiredLRP() {
		String processGuid = UUID.randomUUID().toString();
		DesiredLRPUpdateRequest request = new DesiredLRPUpdateRequest();

		receptorClient.updateDesiredLRP(processGuid, request);

		verify(restTemplate).put("{baseUrl}/desired_lrps/{processGuid}", request, BASE_URL, processGuid);
		verifyNoMoreInteractions(restTemplate);
	}

	@Test
	public void testDeleteDesiredLRP() {
		String processGuid = UUID.randomUUID().toString();

		receptorClient.deleteDesiredLRP(processGuid);

		verify(restTemplate).delete("{baseUrl}/desired_lrps/{processGuid}", BASE_URL, processGuid);
		verifyNoMoreInteractions(restTemplate);
	}

	@Test
	public void testGetActualLRPs() {
		ResponseEntity<List<ActualLRPResponse>> entity = new ResponseEntity(Collections.singletonList(getActualLRPResponse()), HttpStatus.OK);
		when(restTemplate.exchange("{baseUrl}/actual_lrps", HttpMethod.GET, null, ReceptorClient.ACTUAL_LRP_RESPONSE_LIST_TYPE, BASE_URL)).thenReturn(entity);

		List<ActualLRPResponse> responses = receptorClient.getActualLRPs();

		assertEquals(1, responses.size());

		ActualLRPResponse response = responses.get(0);
		assertActuaLRPResponse(response);
	}

	@Test
	public void testGetActualLRPsByDomain() {
		ResponseEntity<List<ActualLRPResponse>> entity = new ResponseEntity(Collections.singletonList(getActualLRPResponse()), HttpStatus.OK);
		when(restTemplate.exchange("{baseUrl}/actual_lrps?domain={domain}", HttpMethod.GET, null, ReceptorClient.ACTUAL_LRP_RESPONSE_LIST_TYPE, BASE_URL, "foo")).thenReturn(entity);

		List<ActualLRPResponse> responses = receptorClient.getActualLRPsByDomain("foo");

		assertEquals(1, responses.size());

		ActualLRPResponse response = responses.get(0);
		assertActuaLRPResponse(response);
	}

	@Test
	public void testGetActualLRPsByProcessGuid() {
		ResponseEntity<List<ActualLRPResponse>> entity = new ResponseEntity(Collections.singletonList(getActualLRPResponse()), HttpStatus.OK);
		String guid = UUID.randomUUID().toString();
		when(restTemplate.exchange("{baseUrl}/actual_lrps/{processGuid}", HttpMethod.GET, null, ReceptorClient.ACTUAL_LRP_RESPONSE_LIST_TYPE, BASE_URL, guid)).thenReturn(entity);

		List<ActualLRPResponse> responses = receptorClient.getActualLRPsByProcessGuid(guid);

		assertEquals(1, responses.size());

		ActualLRPResponse response = responses.get(0);
		assertActuaLRPResponse(response);
	}

	@Test
	public void testGetActualLRPByProcessGuidAndIndex() {
		ResponseEntity<ActualLRPResponse> entity = new ResponseEntity(getActualLRPResponse(), HttpStatus.OK);
		String guid = UUID.randomUUID().toString();
		when(restTemplate.exchange("{baseUrl}/actual_lrps/{processGuid}/index/{index}", HttpMethod.GET, null, ActualLRPResponse.class, BASE_URL, guid, 5)).thenReturn(entity);

		ActualLRPResponse response = receptorClient.getActualLRPByProcessGuidAndIndex(guid, 5);

		assertActuaLRPResponse(response);
	}

	@Test
	public void testKillActualLRPByProcessGuidAndIndex() {
		String guid = UUID.randomUUID().toString();

		receptorClient.killActualLRPByProcessGuidAndIndex(guid, 5);

		verify(restTemplate).delete("{baseUrl}/actual_lrps/{processGuid}/index/{index}", BASE_URL, guid, 5);
		verifyNoMoreInteractions(restTemplate);
	}

	@Test
	public void testCreateTask() {
		Map<String, RunAction> action = new HashMap<>();
		RunAction runAction = new RunAction();
		runAction.setPath("java");
		runAction.setArgs(new String[] {"-jar", "/app.jar"});
		runAction.setDir("/");
		action.put("run", runAction);

		TaskCreateRequest request = new TaskCreateRequest();
		String taskGuid = UUID.randomUUID().toString();
		request.setTaskGuid(taskGuid);
		request.setDomain("foo");
		request.setRootfs("docker:///foo#bar");
		request.setMemoryMb(512);
		request.setPrivileged(false);
		request.setLogGuid(request.getTaskGuid());
		request.setAction(action);
		TaskCreateRequest.EgressRule rule = new TaskCreateRequest.EgressRule();
		rule.setProtocol("all");
		rule.setDestinations(new String[] {"127.0.0.1"});
		TaskCreateRequest.PortRange portRange = new TaskCreateRequest.PortRange();
		portRange.setStart(123);
		portRange.setEnd(456);
		rule.setPortRange(portRange);
		request.setEgressRules(new TaskCreateRequest.EgressRule[] {rule});

		ArgumentCaptor<TaskCreateRequest> capturedRequest =
				ArgumentCaptor.forClass(TaskCreateRequest.class);

		receptorClient.createTask(request);

		verify(restTemplate).postForEntity(eq("{baseUrl}/tasks"), capturedRequest.capture(), (Class) isNull(), eq(BASE_URL));
		verifyNoMoreInteractions(restTemplate);

		TaskCreateRequest resultingRequest = capturedRequest.getValue();

		assertEquals(taskGuid, resultingRequest.getTaskGuid());
		assertEquals("foo", resultingRequest.getDomain());
		assertEquals("docker:///foo#bar", resultingRequest.getRootfs());
		assertEquals(512, resultingRequest.getMemoryMb());
		assertFalse(resultingRequest.isPrivileged());
		assertEquals(taskGuid, resultingRequest.getLogGuid());
		RunAction resultingAction = resultingRequest.getAction().get("run");
		assertEquals("java", resultingAction.getPath());
		assertEquals("/", resultingAction.getDir());
		assertArrayEquals(new String[] {"-jar", "/app.jar"}, resultingAction.getArgs());
		assertEquals("all", resultingRequest.getEgressRules()[0].getProtocol());
		assertArrayEquals(new String[] {"127.0.0.1"}, resultingRequest.getEgressRules()[0].getDestinations());
		assertEquals(123, resultingRequest.getEgressRules()[0].getPortRange().getStart());
		assertEquals(456, resultingRequest.getEgressRules()[0].getPortRange().getEnd());
	}

	@Test
	public void testGetTasks() {
		TaskResponse taskResponse = getTaskResponse();

		ResponseEntity<List<TaskResponse>> entity = new ResponseEntity(Collections.singletonList(taskResponse), HttpStatus.OK);

		when(restTemplate.exchange("{baseUrl}/tasks", HttpMethod.GET, null, ReceptorClient.TASK_RESPONSE_LIST_TYPE, BASE_URL)).thenReturn(entity);

		List<TaskResponse> tasks = receptorClient.getTasks();

		assertEquals(1, tasks.size());
		TaskResponse response = tasks.get(0);
		assertTaskResponse(taskResponse.getTaskGuid(), taskResponse.getLogGuid(), response);
	}

	@Test
	public void testGetTasksByDomain() {
		TaskResponse taskResponse = getTaskResponse();

		ResponseEntity<List<TaskResponse>> entity = new ResponseEntity(Collections.singletonList(taskResponse), HttpStatus.OK);

		when(restTemplate.exchange("{baseUrl}/tasks?domain={domain}", HttpMethod.GET, null, ReceptorClient.TASK_RESPONSE_LIST_TYPE, BASE_URL, "foo")).thenReturn(entity);

		List<TaskResponse> tasks = receptorClient.getTasksByDomain("foo");

		assertEquals(1, tasks.size());
		TaskResponse response = tasks.get(0);
		assertTaskResponse(taskResponse.getTaskGuid(), taskResponse.getLogGuid(), response);
	}

	@Test
	public void testGetTask() {
		TaskResponse taskResponse = getTaskResponse();

		ResponseEntity<TaskResponse> entity = new ResponseEntity(taskResponse, HttpStatus.OK);

		when(restTemplate.exchange("{baseUrl}/tasks/{taskGuid}", HttpMethod.GET, null, TaskResponse.class, BASE_URL, "guid")).thenReturn(entity);

		TaskResponse task = receptorClient.getTask("guid");

		assertTaskResponse(taskResponse.getTaskGuid(), taskResponse.getLogGuid(), task);
	}

	@Test
	public void testDeleteTask() {
		receptorClient.deleteTask("guid");

		verify(restTemplate).delete("{baseUrl}/tasks/{taskGuid}", BASE_URL, "guid");
		verifyNoMoreInteractions(restTemplate);
	}

	@Test
	public void testCancelTask() {
		receptorClient.cancelTask("guid");

		verify(restTemplate).postForEntity("{baseUrl}/tasks/{taskGuid}/cancel", "", null, BASE_URL, "guid");
		verifyNoMoreInteractions(restTemplate);
	}

	@Test
	public void testGetCells() {
		CellResponse cellResponse = new CellResponse();
		cellResponse.setCellId("id");
		cellResponse.setZone("zone");
		CellResponse.Capacity capacity = new CellResponse.Capacity();
		capacity.setContainers(1);
		capacity.setDiskMb(512);
		capacity.setMemoryMb(1024);
		cellResponse.setCapacity(capacity);

		ResponseEntity<List<CellResponse>> entity = new ResponseEntity(Collections.singletonList(cellResponse), HttpStatus.OK);

		when(restTemplate.exchange("{baseUrl}/cells", HttpMethod.GET, null, ReceptorClient.CELL_RESPONSE_LIST_TYPE, BASE_URL)).thenReturn(entity);

		List<CellResponse> cells = receptorClient.getCells();

		assertEquals(1, cells.size());
		CellResponse response = cells.get(0);
		assertEquals("id", response.getCellId());
		assertEquals("zone", response.getZone());
		CellResponse.Capacity responseCapacity = response.getCapacity();
		assertEquals(1, responseCapacity.getContainers());
		assertEquals(512, responseCapacity.getDiskMb());
		assertEquals(1024, responseCapacity.getMemoryMb());
	}

	@Test
	public void testUpsertDomain() {
		ArgumentCaptor<HttpEntity> httpEntityCaptor = ArgumentCaptor.forClass(HttpEntity.class);

		receptorClient.upsertDomain("foo", 123);

		verify(restTemplate).put(eq("{baseUrl}/domains/{domain}"), httpEntityCaptor.capture(), eq(BASE_URL), eq("foo"));

		HttpEntity capturedEntity = (HttpEntity) httpEntityCaptor.getValue();
		assertEquals("max-age=123", capturedEntity.getHeaders().getCacheControl());
	}

	@Test
	public void testGetDomains() {
		ResponseEntity<String[]> entity = new ResponseEntity(new String[] {"foo", "bar"}, HttpStatus.OK);

		when(restTemplate.exchange("{baseUrl}/domains", HttpMethod.GET, null, String[].class, BASE_URL)).thenReturn(entity);

		String [] domains = receptorClient.getDomains();

		assertEquals("foo", domains[0]);
		assertEquals("bar", domains[1]);
	}

	private void assertTaskResponse(String taskGuid, String logGuid, TaskResponse response) {
		assertEquals(taskGuid, response.getTaskGuid());
		assertEquals("/out.txt", response.getResultFile());
		assertEquals("COMPLETED", response.getResult());
		assertEquals(logGuid, response.getLogGuid());
		assertEquals("cell", response.getCellId());
		assertFalse(response.isFailed());
		assertEquals("COMPLETED", response.getState());
	}

	private void assertDesiredLRPRequest(DesiredLRPCreateRequest receivedRequest) {
		assertEquals(receivedRequest.getProcessGuid(), "test-app");
		assertEquals(receivedRequest.getRootfs(), APP_DOCKER_PATH);
		assertEquals(receivedRequest.runAction().getPath(), "/lattice-app");
	}

	private DesiredLRPCreateRequest getDesiredLRPCreateRequest() {
		DesiredLRPCreateRequest request = new DesiredLRPCreateRequest();
		request.setProcessGuid("test-app");
		request.setRootfs(APP_DOCKER_PATH);
		request.runAction().setPath("/lattice-app");
		request.addRoute(8080, "test-app.192.168.11.11.xip.io", "test-app-8080.192.168.11.11.xip.io");
		return request;
	}

	private DesiredLRPResponse getDesiredLRPResponse() {
		DesiredLRPResponse response = new DesiredLRPResponse();
		response.setProcessGuid("123");
		response.setRootfs(APP_DOCKER_PATH);
		response.runAction().setPath("/lattice-app");
		response.addRoute(8080, "test-app.192.168.11.11.xip.io", "test-app-8080.192.168.11.11.xip.io");
		return response;
	}

	private void assertActuaLRPResponse(ActualLRPResponse response) {
		assertEquals("123", response.getProcessGuid());
		assertEquals("foo", response.getAddress());
		assertEquals("456", response.getCellId());
		assertEquals(0, response.getCrashCount());
		assertEquals("bar", response.getDomain());
		assertFalse(response.isEvacuating());
		assertEquals(2, response.getIndex());
		assertEquals("guid", response.getInstanceGuid());
	}

	private ActualLRPResponse getActualLRPResponse() {
		ActualLRPResponse response = new ActualLRPResponse();
		response.setProcessGuid("123");
		response.setAddress("foo");
		response.setCellId("456");
		response.setCrashCount(0);
		response.setDomain("bar");
		response.setEvacuating(false);
		response.setIndex(2);
		response.setInstanceGuid("guid");
		response.setSince(new Date().getTime());

		return response;
	}

	private TaskResponse getTaskResponse() {
		TaskResponse response = new TaskResponse();
		String taskGuid = UUID.randomUUID().toString();
		response.setTaskGuid(taskGuid);
		response.setResultFile("/out.txt");
		response.setResult("COMPLETED");
		response.setLogGuid(taskGuid);
		response.setCellId("cell");
		response.setFailed(false);
		response.setState("COMPLETED");

		return response;
	}
}
