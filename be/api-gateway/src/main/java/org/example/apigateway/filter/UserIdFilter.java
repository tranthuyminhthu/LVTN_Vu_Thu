package org.example.apigateway.filter;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.nimbusds.jose.shaded.gson.internal.LinkedTreeMap;
import org.springframework.cloud.gateway.filter.GatewayFilterChain;
import org.springframework.cloud.gateway.filter.GlobalFilter;
import org.springframework.core.Ordered;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.ReactiveSecurityContextHolder;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;

import java.util.List;

@Component
public class UserIdFilter implements GlobalFilter, Ordered {

    @Override
    public Mono<Void> filter(ServerWebExchange exchange, GatewayFilterChain chain) {
        return ReactiveSecurityContextHolder.getContext()
                .map(SecurityContext::getAuthentication)
                .filter(Authentication::isAuthenticated)
                .filter(auth -> auth.getPrincipal() instanceof Jwt)
                .map(auth -> (Jwt) auth.getPrincipal())
                .map(jwt -> {
                    String userId = jwt.getClaimAsString("sub");
                    List<String> roles = extractRolesFromJwt(jwt);
                    String email = jwt.getClaimAsString("email");
                    String username = jwt.getClaimAsString("preferred_username");
                    
                    System.out.println("Current user roles: " + roles);
                    
                    ServerHttpRequest request = exchange.getRequest();
                    ServerHttpRequest mutatedRequest = request.mutate()
                            .header("X-User-ID", userId)
                            .header("X-User-Email", email)
                            .header("X-User-Name", username)
                            .header("X-User-Roles", String.join(",", roles))
                            .build();
                    
                    return exchange.mutate().request(mutatedRequest).build();
                })
                .defaultIfEmpty(exchange)
                .flatMap(chain::filter);
    }

    private List<String> extractRolesFromJwt(Jwt jwt) {
        try {
            LinkedTreeMap<String, List<String>> realmAccess = jwt.getClaim("realm_access");
            return realmAccess != null && realmAccess.containsKey("roles")
                    ? realmAccess.get("roles")
                    : List.of("USER");
        } catch (Exception e) {
            System.out.println("Error extracting roles from realm_access: " + e.getMessage());
        }
        return List.of("USER");
    }

    @Override
    public int getOrder() {
        return Ordered.HIGHEST_PRECEDENCE + 100;
    }
} 
