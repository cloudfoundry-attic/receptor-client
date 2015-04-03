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
import io.pivotal.receptor.commands.DesiredLRPResponse;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.LineNumberReader;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.CopyOnWriteArraySet;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import org.springframework.core.GenericTypeResolver;
import org.springframework.http.HttpMethod;
import org.springframework.http.client.ClientHttpRequest;
import org.springframework.http.client.ClientHttpResponse;
import org.springframework.scheduling.concurrent.CustomizableThreadFactory;
import org.springframework.util.Assert;
import org.springframework.util.StringUtils;
import org.springframework.web.client.RequestCallback;
import org.springframework.web.client.ResponseExtractor;
import org.springframework.web.client.RestTemplate;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;

/**
 * @author Mark Fisher
 */
public class EventDispatcher implements Runnable {

	private static final Log logger = LogFactory.getLog(EventDispatcher.class);

	private final String url;

	private final Executor backgroundExecutor;

	private final Executor dispatchingExecutor;

	private final Set<EventListener<?>> listeners = new CopyOnWriteArraySet<EventListener<?>>();

	private final RestTemplate restTemplate = new RestTemplate();

	public EventDispatcher(String url) {
		Assert.hasText(url, "URL is required");
		this.url = url;
		this.backgroundExecutor = Executors.newSingleThreadExecutor(new CustomizableThreadFactory("receptor-event-subscriber-"));
		this.dispatchingExecutor = Executors.newCachedThreadPool(new CustomizableThreadFactory("receptor-event-dispatcher-"));
	}

	public void addListener(EventListener<?> listener) {
		if (listeners.isEmpty()) {
			backgroundExecutor.execute(this);
		}
		this.listeners.add(listener);
	}

	@Override
	public void run() {
		RequestCallback requestCallback = new NoOpRequestCallback();
		ResponseExtractor<?> responseExtractor = new EventResponseExtractor();
		while (true) {
			try {
				restTemplate.execute(url, HttpMethod.GET, requestCallback, responseExtractor);
			}
			catch (Exception e) {
				throw new IllegalStateException("Exception while reading event stream.", e);
			}
		}
	}

	@SuppressWarnings({ "rawtypes", "unchecked" })
	private void dispatchEvent(final ReceptorEvent<?> event) {
		for (final EventListener listener : listeners) {
			if (supportsEventType(listener, event)) {
				dispatchingExecutor.execute(new Runnable() {
					
					@Override
					public void run() {
						listener.onEvent(event);						
					}
				});
			}
		}
	}

	private static boolean supportsEventType(EventListener<?> listener, ReceptorEvent<?> event) {
		Class<?> declaredEventType = GenericTypeResolver.resolveTypeArgument(listener.getClass(), EventListener.class);
		return (declaredEventType == null || declaredEventType.isAssignableFrom(event.getClass()));
	}

	private static class NoOpRequestCallback implements RequestCallback {

		@Override
		public void doWithRequest(ClientHttpRequest request) throws IOException {
		}
	}

	private class EventResponseExtractor implements ResponseExtractor<String> {

		@Override
		public String extractData(ClientHttpResponse response) throws IOException {
			InputStream inputStream = response.getBody();
			LineNumberReader reader = null;
			try {
				EventBuilder builder = null;
				reader = new LineNumberReader(new InputStreamReader(inputStream));
				for (String line = reader.readLine(); line != null; line = reader.readLine()) {
					if (line.startsWith("id:")) {
						builder = EventBuilder.setId(Integer.parseInt(line.split(":")[1].trim()));
					}
					else if (line.startsWith("event:")) {
						builder = builder.setType(line.split(":", 2)[1].trim());
					}
					else if (line.startsWith("data:") && builder != null) {
						ReceptorEvent<?> event = ((TypedEventBuilder<?, ?>) builder).setData(line.split(":", 2)[1].trim());
						dispatchEvent(event);
						event = null;
					}
					if (!StringUtils.hasText(line)) {
						break;
					}
				}
			}
			finally {
				if (reader != null) {
					reader.close();
				}
			}
			return null;
		}
	}

	private static class EventBuilder {

		final int id;

		private EventBuilder(int id) {
			this.id = id;
		}

		static EventBuilder setId(int id) {
			return new EventBuilder(id);
		}

		@SuppressWarnings("unchecked")
		<B extends TypedEventBuilder<?, ?>> B setType(String type) {
			switch (type) {
			case DesiredLRPCreatedEvent.TYPE:
				return (B) new DesiredLRPCreatedEventBuilder(id);
			case DesiredLRPChangedEvent.TYPE:
				return (B) new DesiredLRPChangedEventBuilder(id);
			case DesiredLRPRemovedEvent.TYPE:
				return (B) new DesiredLRPRemovedEventBuilder(id);
			case ActualLRPCreatedEvent.TYPE:
				return (B) new ActualLRPCreatedEventBuilder(id);
			case ActualLRPChangedEvent.TYPE:
				return (B) new ActualLRPChangedEventBuilder(id);
			case ActualLRPRemovedEvent.TYPE:
				return (B) new ActualLRPRemovedEventBuilder(id);
			default:
				logger.warn("unsupported event type: " + type);
				return null;
			}
		}
	}

	private abstract static class TypedEventBuilder<E extends ReceptorEvent<D>, D> extends EventBuilder {
	
		static final ObjectMapper mapper = new ObjectMapper();
	
		private TypedEventBuilder(int id) {
			super(id);
		}
	
		@SuppressWarnings("unchecked")
		E setData(String data) {
			E event = createEvent(id);
			try {
				event.setData((Map<String, D>) mapper.readValue(data, getTypeReference()));
			}
			catch (IOException e) {
				logger.warn("failed to map event data", e);
			}
			return event;
		}

		abstract E createEvent(int id);

		abstract TypeReference<?> getTypeReference();
	}

	private static class DesiredLRPCreatedEventBuilder extends TypedEventBuilder<DesiredLRPCreatedEvent, DesiredLRPResponse> {
	
		private DesiredLRPCreatedEventBuilder(int id) {
			super(id);
		}
	
		@Override
		DesiredLRPCreatedEvent createEvent(int id) {
			return new DesiredLRPCreatedEvent(id);
		}

		@Override
		TypeReference<?> getTypeReference() {
			return new TypeReference<Map<String, DesiredLRPResponse>>() {};
		}
	}

	private static class DesiredLRPChangedEventBuilder extends TypedEventBuilder<DesiredLRPChangedEvent, DesiredLRPResponse> {
	
		private DesiredLRPChangedEventBuilder(int id) {
			super(id);
		}
		@Override
		DesiredLRPChangedEvent createEvent(int id) {
			return new DesiredLRPChangedEvent(id);
		}

		@Override
		TypeReference<?> getTypeReference() {
			return new TypeReference<Map<String, DesiredLRPResponse>>() {};
		}
	}

	private static class DesiredLRPRemovedEventBuilder extends TypedEventBuilder<DesiredLRPRemovedEvent, DesiredLRPResponse> {
	
		private DesiredLRPRemovedEventBuilder(int id) {
			super(id);
		}
	
		@Override
		DesiredLRPRemovedEvent createEvent(int id) {
			return new DesiredLRPRemovedEvent(id);
		}

		@Override
		TypeReference<?> getTypeReference() {
			return new TypeReference<Map<String, DesiredLRPResponse>>() {};
		}
	}
	
	private static class ActualLRPCreatedEventBuilder extends TypedEventBuilder<ActualLRPCreatedEvent, ActualLRPResponse> {
	
		private ActualLRPCreatedEventBuilder(int id) {
			super(id);
		}

		@Override
		ActualLRPCreatedEvent createEvent(int id) {
			return new ActualLRPCreatedEvent(id);
		}

		@Override
		TypeReference<?> getTypeReference() {
			return new TypeReference<Map<String, ActualLRPResponse>>() {};
		}
	}
	
	private static class ActualLRPChangedEventBuilder extends TypedEventBuilder<ActualLRPChangedEvent, ActualLRPResponse> {
	
		private ActualLRPChangedEventBuilder(int id) {
			super(id);
		}
	
		@Override
		ActualLRPChangedEvent createEvent(int id) {
			return new ActualLRPChangedEvent(id);
		}

		@Override
		TypeReference<?> getTypeReference() {
			return new TypeReference<Map<String, ActualLRPResponse>>() {};
		}
	}
	
	private static class ActualLRPRemovedEventBuilder extends TypedEventBuilder<ActualLRPRemovedEvent, ActualLRPResponse> {
	
		private ActualLRPRemovedEventBuilder(int id) {
			super(id);
		}
	
		@Override
		ActualLRPRemovedEvent createEvent(int id) {
			return new ActualLRPRemovedEvent(id);
		}

		@Override
		TypeReference<?> getTypeReference() {
			return new TypeReference<Map<String, ActualLRPResponse>>() {};
		}
	}
}
