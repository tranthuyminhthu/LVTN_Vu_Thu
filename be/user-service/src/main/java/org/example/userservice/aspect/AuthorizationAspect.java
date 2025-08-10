package org.example.userservice.aspect;

import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Before;
import org.example.userservice.annotation.RequireRole;
import org.example.userservice.util.UserContext;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ResponseStatusException;

@Aspect
@Component
@Slf4j
public class AuthorizationAspect {

    @Before("@annotation(requireRole)")
    public void checkRole(JoinPoint joinPoint, RequireRole requireRole) {
        String[] requiredRoles = requireRole.value();
        boolean requireAll = requireRole.requireAll();

        if (UserContext.getRoles() == null || UserContext.getRoles().isEmpty()) {
            log.warn("User has no roles assigned");
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "Access denied: No roles assigned");
        }

        boolean hasAccess;
        if (requireAll) {
            hasAccess = UserContext.hasAllRoles(requiredRoles);
        } else {
            hasAccess = UserContext.hasAnyRole(requiredRoles);
        }

        if (!hasAccess) {
            log.warn("User {} with roles {} does not have required roles: {}", 
                    UserContext.getUserId(), UserContext.getRoles(), String.join(",", requiredRoles));
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, 
                    "Access denied: Required roles: " + String.join(",", requiredRoles));
        }

        log.debug("User {} with roles {} has access to {}", 
                UserContext.getUserId(), UserContext.getRoles(), joinPoint.getSignature().getName());
    }
}
