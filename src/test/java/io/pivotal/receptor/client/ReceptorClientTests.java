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

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import io.pivotal.receptor.commands.CellResponse;
import io.pivotal.receptor.commands.DesiredLRPCreateRequest;
import io.pivotal.receptor.commands.TaskCreateRequest;

import java.util.List;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.junit.Test;

import com.fasterxml.jackson.databind.ObjectMapper;

/**
 * @author Mark Fisher
 */
public class ReceptorClientTests {

	private static final Log logger = LogFactory.getLog(ReceptorClientTests.class);

	private static final String APP_DOCKER_PATH = "docker:///cloudfoundry/lattice-app";

	private static final ObjectMapper objectMapper = new ObjectMapper();

	private final ReceptorClient client = new ReceptorClient();

	@Test
	public void createAndDeleteLRP() throws Exception {
		DesiredLRPCreateRequest request = new DesiredLRPCreateRequest();
		request.setProcessGuid("test-app");
		request.setRootfs(APP_DOCKER_PATH);
		request.runAction().setPath("/lattice-app");
		request.addRoute(8080, new String[] {"test-app.192.168.11.11.xip.io", "test-app-8080.192.168.11.11.xip.io"});
		logger.info("creating LRP: " + objectMapper.writeValueAsString(request));
		client.createDesiredLRP(request);
		assertEquals("test-app", client.getDesiredLRPs().get(0).getProcessGuid());
		assertEquals("test-app", client.getActualLRPs().get(0).getProcessGuid());
		client.deleteDesiredLRP("test-app");
		for (int i = 0; i < 1000; i++) {
			Thread.sleep(100);
			if (0 == client.getActualLRPsByProcessGuid("test-app").size()) {
				break;
			}
		}
		assertEquals(0, client.getDesiredLRPs().size());
		assertEquals(0, client.getActualLRPs().size());
	}

	@Test
	public void createAndDeleteTask() throws Exception {
		TaskCreateRequest request = new TaskCreateRequest();
		request.setTaskGuid("test-task");
		request.setRootfs("docker:///cloudfoundry/lucid64");
		request.runAction.setPath("/bin/sh");
		request.runAction.setArgs(new String[] {"-c", "exit 1"});
		request.runAction.setDir("/tmp");
		assertEquals(0, client.getTasks().size());
		logger.info("creating Task: " + objectMapper.writeValueAsString(request));
		client.createTask(request);
		assertEquals(1, client.getTasks().size());
		assertEquals("/bin/sh", client.getTask("test-task").getAction().get("run").getPath());
		for (int i = 0; i < 1000; i++) {
			String state = client.getTask("test-task").getState();
			if ("COMPLETED".equals(state)) {
				break;
			}
			Thread.sleep(100);
		}
		assertEquals("COMPLETED", client.getTask("test-task").getState());
		client.deleteTask("test-task");
		assertEquals(0, client.getTasks().size());
	}

	@Test
	public void createAndCancelTask() throws Exception {
		TaskCreateRequest request = new TaskCreateRequest();
		request.setTaskGuid("test-task");
		request.setRootfs("docker:///cloudfoundry/lucid64");
		request.runAction.setPath("/bin/sh");
		request.runAction.setArgs(new String[] {"-c", "sleep 3;exit 1"});
		request.runAction.setDir("/tmp");
		assertEquals(0, client.getTasks().size());
		logger.info("creating Task: " + objectMapper.writeValueAsString(request));
		client.createTask(request);
		assertEquals(1, client.getTasks().size());
		assertEquals("/bin/sh", client.getTask("test-task").getAction().get("run").getPath());
		boolean cancelled = false;
		for (int i = 0; i < 100; i++) {
			String state = client.getTask("test-task").getState();
			if ("RUNNING".equals(state)) {
				client.cancelTask("test-task");
				cancelled = true;
				break;
			}
			Thread.sleep(100);
		}
		assertTrue(cancelled);
		assertEquals("COMPLETED", client.getTask("test-task").getState());
		client.deleteTask("test-task");
		assertEquals(0, client.getTasks().size());
	}

	@Test
	public void getDomains() {
		String[] domains = client.getDomains();
		assertEquals(1, domains.length);
		assertEquals("lattice", domains[0]);
	}

	@Test
	public void getCells() {
		List<CellResponse> cells = client.getCells();
		assertEquals(1, cells.size());
		assertEquals("lattice-cell-01", cells.get(0).getCellId());
	}
}
