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
import io.pivotal.receptor.commands.DesiredLRPCreateRequest;

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
		request.runAction.setPath("/lattice-app");
		request.addRoute(8080, new String[] {"test-app.192.168.11.11.xip.io", "test-app-8080.192.168.11.11.xip.io"});
		logger.info("creating LRP: " + objectMapper.writeValueAsString(request));
		client.createDesiredLRP(request);
		assertEquals("test-app", client.getDesiredLRPs().get(0).getProcessGuid());
		assertEquals("test-app", client.getActualLRPs().get(0).getProcessGuid());
		client.deleteDesiredLRP("test-app");
		for (int i = 0; i < 100; i++) {
			Thread.sleep(100);
			if (0 == client.getActualLRPsByProcessGuid("test-app").size()) {
				break;
			}
		}
		assertEquals(0, client.getDesiredLRPs().size());
		assertEquals(0, client.getActualLRPs().size());
	}

}
