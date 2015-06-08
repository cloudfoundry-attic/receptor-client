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

package org.cloudfoundry.receptor.commands;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import org.cloudfoundry.receptor.actions.Action;
import org.cloudfoundry.receptor.actions.DownloadAction;
import org.cloudfoundry.receptor.actions.RunAction;
import org.cloudfoundry.receptor.actions.UploadAction;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.ObjectCodec;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonDeserializer;
import com.fasterxml.jackson.databind.JsonNode;

/**
 * @author Mark Fisher
 */
public class ActionMapSerializer extends JsonDeserializer<Map<String, Action>> {

	private static final String DOWNLOAD_KEY = "download";

	private static final String UPLOAD_KEY = "upload";

	private static final String RUN_KEY = "run";

	@Override
	public Map<String, Action> deserialize(JsonParser parser, DeserializationContext context) throws IOException {
		ObjectCodec codec = parser.getCodec();
		Map<String, Action> map = new HashMap<>();
		final JsonNode node = codec.readTree(parser);
		if (node.has(DOWNLOAD_KEY)) {
			map.put(DOWNLOAD_KEY, deserializeAction(node, DOWNLOAD_KEY, codec, DownloadAction.class));
		}
		if (node.has(UPLOAD_KEY)) {
			map.put(UPLOAD_KEY, deserializeAction(node, UPLOAD_KEY, codec, UploadAction.class)); 
		}
		if (node.has(RUN_KEY)) {
			map.put(RUN_KEY, deserializeAction(node, RUN_KEY, codec, RunAction.class));
		}
		return (map.size() == 0) ? null : map;
	}

	private Action deserializeAction(JsonNode node, String key, ObjectCodec codec, Class<? extends Action> type) throws IOException {
		return node.get(key).traverse(codec).readValueAs(type);
	}
}
