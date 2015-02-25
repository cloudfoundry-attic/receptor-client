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

import io.pivotal.receptor.commands.DesiredLRPCreateRequest;

import java.net.Inet4Address;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.junit.Test;

import com.fasterxml.jackson.databind.ObjectMapper;

/**
 * @author Mark Fisher
 */
public class ReceptorClientTests {

	private static final Log logger = LogFactory.getLog(ReceptorClientTests.class);

	private static final ObjectMapper objectMapper = new ObjectMapper();

	@Test
	public void findAll() {
		ReceptorClient client = new ReceptorClient();
		client.findAllLongRunningProcesses();
	}

	@Test
	public void ticktock() throws Exception {
		ReceptorClient client = new ReceptorClient();

		try {
			client.destroyLongRunningProcess("xd-source");
		}
		catch (Exception e) {
			// not running, ignore
		}
		try {
			client.destroyLongRunningProcess("xd-sink");
		}
		catch (Exception e) {
			// not running, ignore
		}

		String host = Inet4Address.getLocalHost().getHostAddress();
		String dockerPath = "docker:///pperalta/xd";
		String jarPath = "/opt/xd/lib/xolpoc-0.0.1-SNAPSHOT.jar";

		DesiredLRPCreateRequest sink = new DesiredLRPCreateRequest();
		sink.setProcessGuid("xd-sink");
		sink.setRootfs(dockerPath);
		sink.runAction.setPath("java");
		sink.runAction.addArg("-Dmodule=ticktock.sink.log.1");
		sink.runAction.addArg("-Dspring.redis.host=" + host);
		sink.runAction.addArg("-Dserver.port=9999");
		sink.runAction.addArg("-jar");
		sink.runAction.addArg(jarPath);
		sink.addRoute(8080, new String[] {"xd-sink.192.168.11.11.xip.io", "xd-sink-8080.192.168.11.11.xip.io"});
		logger.info("creating LRP for sink: " + objectMapper.writeValueAsString(sink));
		client.createLongRunningProcess(sink);

		DesiredLRPCreateRequest source = new DesiredLRPCreateRequest();
		source.setProcessGuid("xd-source");
		source.setRootfs(dockerPath);
		source.runAction.setPath("java");
		source.runAction.addArg("-Dmodule=ticktock.source.time.0");
		source.runAction.addArg("-Dspring.redis.host=" + host);
		source.runAction.addArg("-Dserver.port=8888");
		source.runAction.addArg("-jar");
		source.runAction.addArg(jarPath);
		source.addRoute(8080, new String[] {"xd-source.192.168.11.11.xip.io", "xd-source-8080.192.168.11.11.xip.io"});
		logger.info("creating LRP for source: " + objectMapper.writeValueAsString(source));
		client.createLongRunningProcess(source);
	}
}
