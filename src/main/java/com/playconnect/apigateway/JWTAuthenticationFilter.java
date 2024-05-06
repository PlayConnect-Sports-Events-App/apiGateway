package com.playconnect.apigateway;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cloud.gateway.filter.GatewayFilter;
import org.springframework.cloud.gateway.filter.factory.AbstractGatewayFilterFactory;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;
import java.util.List;


@Component
public class JWTAuthenticationFilter extends AbstractGatewayFilterFactory<JWTAuthenticationFilter.Config> {

    @Autowired
    private RouteValidator routeValidator;
    @Autowired
    private JWTService jwtService;

    public JWTAuthenticationFilter() {
        super(Config.class);
    }

    @Override
    public GatewayFilter apply(Config config) {
        return (exchange, chain) -> {
            if (routeValidator.isSecured.test(exchange.getRequest())) {
                List<String> authHeaders = exchange.getRequest().getHeaders().getOrEmpty("Authorization");
                if (authHeaders.isEmpty()) {
                    System.out.println("Authorization header is missing");
                    exchange.getResponse().setStatusCode(HttpStatus.UNAUTHORIZED);
                    return exchange.getResponse().setComplete();
                }
                String authHeader = authHeaders.get(0);
                System.out.println("Authorization header: " + authHeader);
                if (authHeader != null && authHeader.startsWith("Bearer ")) {
                    String token = authHeader.substring(7);
                    if (jwtService.isTokenValid(token)) {
                        System.out.println("Token is valid");
                        return chain.filter(exchange);
                    } else {
                        System.out.println("Token is invalid");
                    }
                }
                else{
                    System.out.println("Authorization header format is incorrect");
                }
                exchange.getResponse().setStatusCode(HttpStatus.UNAUTHORIZED);
                return exchange.getResponse().setComplete();
            }
            return chain.filter(exchange);
        };
    }

    public static class Config {
    }
}

