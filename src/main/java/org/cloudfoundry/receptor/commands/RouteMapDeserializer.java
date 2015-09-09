package org.cloudfoundry.receptor.commands;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.ObjectCodec;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonDeserializer;
import com.fasterxml.jackson.databind.JsonNode;
import org.cloudfoundry.receptor.support.HttpRoute;
import org.cloudfoundry.receptor.support.Route;
import org.cloudfoundry.receptor.support.TcpRoute;

import java.io.IOException;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

/**
 * @author Matt Stine
 */
public class RouteMapDeserializer extends JsonDeserializer<Map<String, Route[]>> {
    @Override
    public Map<String, Route[]> deserialize(JsonParser parser, DeserializationContext context) throws IOException, JsonProcessingException {
        ObjectCodec codec = parser.getCodec();
        Map<String, Route[]> map = new HashMap<>();
        final JsonNode node = codec.readTree(parser);

        if (node.has("cf-router")) {
            deserializeRouteArray("cf-router", codec, map, node, HttpRoute.class);
        }

        if (node.has("tcp-router")) {
            deserializeRouteArray("tcp-router", codec, map, node, TcpRoute.class);
        }

        return (map.size() == 0) ? null : map;
    }

    private void deserializeRouteArray(String basePath, ObjectCodec codec, Map<String, Route[]> map, JsonNode node, Class<? extends Route> type) throws IOException {
        final JsonNode baseNode = node.path(basePath);

        Route[] routes = new Route[baseNode.size()];
        final Iterator<JsonNode> routeNodes = baseNode.elements();

        int i = 0;
        while (routeNodes.hasNext()) {
            JsonNode route = routeNodes.next();
            routes[i] = route.traverse(codec).readValueAs(type);
            i++;
        }

        map.put(basePath, routes);
    }

}
