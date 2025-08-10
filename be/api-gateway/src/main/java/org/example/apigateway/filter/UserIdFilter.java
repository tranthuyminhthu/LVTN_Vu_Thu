package org.example.apigateway.filter;

import org.springframework.cloud.gateway.filter.GatewayFilterChain;
import org.springframework.cloud.gateway.filter.GlobalFilter;
import org.springframework.core.Ordered;
import org.springframework.http.HttpHeaders;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.ReactiveSecurityContextHolder;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;

import java.util.List;
import java.util.stream.Collectors;

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
                    // Trích xuất thông tin từ JWT token
                    String userId = extractUserIdFromJwt(jwt);
                    List<String> roles = extractRolesFromJwt(jwt);
                    String email = jwt.getClaimAsString("email");
                    String username = jwt.getClaimAsString("preferred_username");
                    
                    // Thêm thông tin vào header
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

    private String extractUserIdFromJwt(Jwt jwt) {
        // Thử các claim khác nhau để lấy userId
        String userId = jwt.getClaimAsString("sub");
        if (userId == null) {
            userId = jwt.getClaimAsString("user_id");
        }
        if (userId == null) {
            userId = jwt.getClaimAsString("id");
        }
        if (userId == null) {
            // Fallback: sử dụng subject nếu không có userId cụ thể
            userId = jwt.getSubject();
        }
        return userId;
    }

    private List<String> extractRolesFromJwt(Jwt jwt) {
        // Keycloak thường lưu roles trong claim "realm_access.roles" hoặc "resource_access.{client-id}.roles"
        List<String> roles = jwt.getClaimAsStringList("realm_access.roles");
        
        if (roles == null || roles.isEmpty()) {
            // Thử lấy từ resource_access
            try {
                Object resourceAccess = jwt.getClaim("resource_access");
                if (resourceAccess != null) {
                    // Có thể cần parse JSON để lấy roles từ resource_access
                    // Đây là cách đơn giản, bạn có thể cần implement phức tạp hơn
                    roles = List.of("USER"); // Default role
                }
            } catch (Exception e) {
                // Fallback to default role
                roles = List.of("USER");
            }
        }
        
        // Nếu vẫn không có roles, sử dụng default
        if (roles == null || roles.isEmpty()) {
            roles = List.of("USER");
        }
        
        return roles;
    }

    @Override
    public int getOrder() {
        // Đảm bảo filter này chạy sau authentication nhưng trước routing
        return Ordered.HIGHEST_PRECEDENCE + 100;
    }
} 