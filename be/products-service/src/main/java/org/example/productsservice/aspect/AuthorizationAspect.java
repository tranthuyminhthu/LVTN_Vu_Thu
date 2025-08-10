package org.example.productsservice.aspect;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Before;
import org.example.productsservice.annotation.RequireRole;
import org.example.productsservice.util.UserContext;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ResponseStatusException;

@Aspect
@Component
@Slf4j
@RequiredArgsConstructor
public class AuthorizationAspect {

    private final UserContext userContext;

    @Before("@annotation(requireRole)")
    public void checkRole(JoinPoint joinPoint, RequireRole requireRole) {
        String[] requiredRoles = requireRole.value();
        boolean requireAll = requireRole.requireAll();

        if (userContext.getRoles() == null || userContext.getRoles().isEmpty()) {
            log.warn("User has no roles assigned");
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "Access denied: No roles assigned");
        }

        boolean hasAccess;
        if (requireAll) {
            hasAccess = userContext.hasAllRoles(requiredRoles);
        } else {
            hasAccess = userContext.hasAnyRole(requiredRoles);
        }

        if (!hasAccess) {
            log.warn("User {} with roles {} does not have required roles: {}", 
                    userContext.getUserId(), userContext.getRoles(), String.join(",", requiredRoles));
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, 
                    "Access denied: Required roles: " + String.join(",", requiredRoles));
        }

        log.debug("User {} with roles {} has access to {}", 
                userContext.getUserId(), userContext.getRoles(), joinPoint.getSignature().getName());
    }
} 