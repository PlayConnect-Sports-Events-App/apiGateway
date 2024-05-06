package com.playconnect.apigateway;

import org.springframework.http.HttpMethod;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import java.util.function.Predicate;

@Component
public class RouteValidator {
    // Map of endpoints and methods that require authentication
    private static final Map<String, Set<HttpMethod>> securedEndpoints = new HashMap<>();

    // Initialize the map, adding the endpoints and methods that require authentication
    static {
        // Event endpoints
        securedEndpoints.put("/api/event/{id}", Set.of(HttpMethod.PUT, HttpMethod.DELETE));
        securedEndpoints.put("/api/event", Set.of(HttpMethod.POST));
        securedEndpoints.put("/api/event/join", Set.of(HttpMethod.POST));
        securedEndpoints.put("/api/event/leave", Set.of(HttpMethod.POST));
        securedEndpoints.put("/api/event/user/{id}", Set.of(HttpMethod.GET));

        // Comment endpoints
        securedEndpoints.put("/api/comment", Set.of(HttpMethod.POST));
        securedEndpoints.put("/api/comment/{id}", Set.of(HttpMethod.DELETE));
    }

    public Predicate<ServerHttpRequest> isSecured = request ->
            securedEndpoints.entrySet().stream()
                    .anyMatch(entry -> request.getURI().getPath().matches("^" + entry.getKey().replace("{id}", "\\d+") + "$")
                            && entry.getValue().contains(request.getMethod()));
}
