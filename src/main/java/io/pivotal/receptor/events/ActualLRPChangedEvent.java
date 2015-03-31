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

package io.pivotal.receptor.events;

import io.pivotal.receptor.commands.ActualLRPResponse;

/**
 * @author Mark Fisher
 */
public class ActualLRPChangedEvent extends ReceptorEvent<ActualLRPResponse> {

	public static final String TYPE = "actual_lrp_changed";

	public ActualLRPChangedEvent(int id) {
		super(id, TYPE);
	}

	public ActualLRPResponse getActualLRPBefore() {
		return getData().get("actual_lrp_before");
	}

	public ActualLRPResponse getActualLRPAfter() {
		return getData().get("actual_lrp_after");
	}
}
