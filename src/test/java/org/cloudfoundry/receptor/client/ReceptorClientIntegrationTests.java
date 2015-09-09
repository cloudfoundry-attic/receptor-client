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

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import java.util.List;
import java.util.UUID;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.cloudfoundry.receptor.client.test.IntegrationTest;
import org.cloudfoundry.receptor.commands.ActualLRPResponse;
import org.cloudfoundry.receptor.commands.CellResponse;
import org.cloudfoundry.receptor.commands.DesiredLRPCreateRequest;
import org.cloudfoundry.receptor.commands.DesiredLRPResponse;
import org.cloudfoundry.receptor.commands.TaskCreateRequest;
import org.cloudfoundry.receptor.support.EnvironmentVariable;
import org.junit.Test;
import org.junit.experimental.categories.Category;

/**
 * @author Mark Fisher
 * @author Matt Stine
 */
@Category(IntegrationTest.class)
public class ReceptorClientIntegrationTests {

	private static final Log logger = LogFactory.getLog(ReceptorClientIntegrationTests.class);

	private static final String APP_DOCKER_PATH = "docker:///cloudfoundry/lattice-app";

	private static final String MYSQL_DOCKER_PATH = "docker:///mysql";

	private static final ObjectMapper objectMapper = new ObjectMapper();

	private final ReceptorOperations client = new ReceptorClient();

	@Test
	public void createAndDeleteLRP() throws Exception {
		int desiredLrpCountAtStart = client.getDesiredLRPs().size();
		int actualLrpCountAtStart = client.getActualLRPs().size();
		DesiredLRPCreateRequest request = new DesiredLRPCreateRequest();
		String processGuid = UUID.randomUUID().toString();
		request.setProcessGuid(processGuid);
		request.setRootfs(APP_DOCKER_PATH);
		request.runAction().setPath("/lattice-app");
		request.addHttpRoute(8080, "test-app.192.168.11.11.xip.io", "test-app-8080.192.168.11.11.xip.io");
		request.setDomain("yomamma");
		logger.info("creating LRP: " + objectMapper.writeValueAsString(request));
		client.createDesiredLRP(request);

		boolean desiredLrpExists = false;
		for (DesiredLRPResponse desiredLRPResponse : client.getDesiredLRPs()) {
			if(desiredLRPResponse.getProcessGuid().equals(processGuid)) {
				desiredLrpExists = true;
				break;
			}
		}

		assertTrue(desiredLrpExists);

		boolean actualLrpExists = false;
		for (ActualLRPResponse actualLRPResponse : client.getActualLRPs()) {
			if(actualLRPResponse.getProcessGuid().equals(processGuid)) {
				actualLrpExists = true;
				break;
			}
		}

		assertTrue(actualLrpExists);

		client.deleteDesiredLRP(processGuid);
		for (int i = 0; i < 1000; i++) {
			Thread.sleep(100);
			if (0 == client.getActualLRPsByProcessGuid(processGuid).size()) {
				break;
			}
		}
		assertEquals(desiredLrpCountAtStart, client.getDesiredLRPs().size());
		assertEquals(actualLrpCountAtStart, client.getActualLRPs().size());
	}

	@Test
	public void createAndDeleteLRPWithTcpRoute() throws Exception {
		int desiredLrpCountAtStart = client.getDesiredLRPs().size();
		int actualLrpCountAtStart = client.getActualLRPs().size();
		DesiredLRPCreateRequest request = new DesiredLRPCreateRequest();
		String processGuid = UUID.randomUUID().toString();
		request.setProcessGuid(processGuid);
		request.setRootfs(MYSQL_DOCKER_PATH);
		request.setPorts(new int[]{3306});
		request.setEnv(new EnvironmentVariable[]{new EnvironmentVariable("MYSQL_ROOT_PASSWORD", "somesecret")});
		request.runAction().setPath("/entrypoint.sh");
		request.runAction().addArg("mysqld");
		request.runAction().setUser("root");
		request.addTcpRoute(3306, 3306);
		request.setPriviliged(true);
		request.setMemoryMb(512);
		request.setDomain("yomamma");
		logger.info("creating LRP: " + objectMapper.writeValueAsString(request));
		client.createDesiredLRP(request);

		boolean desiredLrpExists = false;
		for (DesiredLRPResponse desiredLRPResponse : client.getDesiredLRPs()) {
			if(desiredLRPResponse.getProcessGuid().equals(processGuid)) {
				desiredLrpExists = true;
				break;
			}
		}

		assertTrue(desiredLrpExists);

		boolean actualLrpExists = false;
		for (ActualLRPResponse actualLRPResponse : client.getActualLRPs()) {
			if(actualLRPResponse.getProcessGuid().equals(processGuid)) {
				actualLrpExists = true;
				break;
			}
		}

		assertTrue(actualLrpExists);

		client.deleteDesiredLRP(processGuid);
		for (int i = 0; i < 1000; i++) {
			Thread.sleep(100);
			if (0 == client.getActualLRPsByProcessGuid(processGuid).size()) {
				break;
			}
		}
		assertEquals(desiredLrpCountAtStart, client.getDesiredLRPs().size());
		assertEquals(actualLrpCountAtStart, client.getActualLRPs().size());
	}

	@Test
	public void createAndDeleteTask() throws Exception {
		int tasksAtStart = client.getTasks().size();
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
		assertEquals(tasksAtStart, client.getTasks().size());
	}

	@Test
	public void createAndCancelTask() throws Exception {
		int tasksAtStart = client.getTasks().size();
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
		assertEquals(tasksAtStart, client.getTasks().size());
	}

	@Test
	public void getDomains() {
		client.upsertDomain("test-domain", 10);
		String[] domains = client.getDomains();
		assertNotNull(domains);
	}

	@Test
	public void getCells() {
		List<CellResponse> cells = client.getCells();
		assertEquals(1, cells.size());
		assertEquals("cell-01", cells.get(0).getCellId());
	}
}
