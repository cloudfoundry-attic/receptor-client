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

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import org.springframework.core.GenericTypeResolver;
import org.springframework.http.HttpMethod;
import org.springframework.http.client.ClientHttpRequest;
import org.springframework.http.client.ClientHttpResponse;
import org.springframework.util.Assert;
import org.springframework.util.StringUtils;
import org.springframework.web.client.RequestCallback;
import org.springframework.web.client.ResponseExtractor;
import org.springframework.web.client.RestTemplate;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;

/**
 * @author Mark Fisher
 *
 * @param <E> type of event for this subscription
 */
public class EventSubscription<E extends ReceptorEvent<?>> implements Runnable {

	private static final Log logger = LogFactory.getLog(EventSubscription.class);

	private final String url;

	private final EventListener<E> listener;

	private final RestTemplate restTemplate = new RestTemplate();

	public EventSubscription(String url, EventListener<E> listener) {
		Assert.hasText(url, "URL is required");
		Assert.notNull(listener, "EventListener must not be null");
		this.url = url;
		this.listener = listener;
	}

	@Override
	public void run() {
		while (true) {
			try {
				readStream();
			}
			catch (Exception e) {
				throw new IllegalStateException("Exception while reading event stream.", e);
			}
		}
	}

	private void readStream() {
		restTemplate.execute(url, HttpMethod.GET,
				new RequestCallback() {

					@Override
					public void doWithRequest(ClientHttpRequest request) throws IOException {
					}
				},
				new ResponseExtractor<String>() {

					@Override
					@SuppressWarnings("unchecked")
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
									ReceptorEvent<?> event = ((TypedEventBuilder<?>) builder).setData(line.split(":", 2)[1].trim());
									if (supportsEventType(listener, event)) {
										listener.onEvent((E) event);
									}
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
				});
	}


	private static boolean supportsEventType(EventListener<?> listener, ReceptorEvent<?> event) {
		Class<?> declaredEventType = GenericTypeResolver.resolveTypeArgument(listener.getClass(), EventListener.class);
		return (declaredEventType == null || declaredEventType.isAssignableFrom(event.getClass()));
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
		<B extends TypedEventBuilder<?>> B setType(String type) {
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

	private abstract static class TypedEventBuilder<T extends ReceptorEvent<?>> extends EventBuilder {
	
		static final ObjectMapper mapper = new ObjectMapper();
	
		private TypedEventBuilder(int id) {
			super(id);
		}
	
		T setData(String data) {
			T event = createEvent(id);
			try {
				event.setData(mapper.readValue(data, getTypeReference()));
			}
			catch (IOException e) {
				logger.warn("failed to map event data", e);
			}
			return event;
		}

		abstract T createEvent(int id);

		abstract TypeReference<?> getTypeReference();
	}

	private static class DesiredLRPCreatedEventBuilder extends TypedEventBuilder<DesiredLRPCreatedEvent> {
	
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

	private static class DesiredLRPChangedEventBuilder extends TypedEventBuilder<DesiredLRPChangedEvent> {
	
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

	private static class DesiredLRPRemovedEventBuilder extends TypedEventBuilder<DesiredLRPRemovedEvent> {
	
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
	
	private static class ActualLRPCreatedEventBuilder extends TypedEventBuilder<ActualLRPCreatedEvent> {
	
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
	
	private static class ActualLRPChangedEventBuilder extends TypedEventBuilder<ActualLRPChangedEvent> {
	
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
	
	private static class ActualLRPRemovedEventBuilder extends TypedEventBuilder<ActualLRPRemovedEvent> {
	
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
