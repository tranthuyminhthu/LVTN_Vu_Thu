package org.example.userservice.util;

import org.springframework.http.HttpHeaders;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import jakarta.servlet.http.HttpServletRequest;

public class UserContext {
    public static final String USER_ID_HEADER = "X-User-ID";
    public static final String USER_EMAIL_HEADER = "X-User-Email";
    public static final String USER_NAME_HEADER = "X-User-Name";

    public static String getCurrentUserId() {
        HttpServletRequest request = getCurrentRequest();
        if (request != null) {
            return request.getHeader(USER_ID_HEADER);
        }
        return null;
    }

    public static String getCurrentUserEmail() {
        HttpServletRequest request = getCurrentRequest();
        if (request != null) {
            return request.getHeader(USER_EMAIL_HEADER);
        }
        return null;
    }

    public static String getCurrentUserName() {
        HttpServletRequest request = getCurrentRequest();
        if (request != null) {
            return request.getHeader(USER_NAME_HEADER);
        }
        return null;
    }

    public static boolean hasUserInfo() {
        return getCurrentUserId() != null;
    }

    private static HttpServletRequest getCurrentRequest() {
        try {
            ServletRequestAttributes attributes = (ServletRequestAttributes) RequestContextHolder.getRequestAttributes();
            return attributes != null ? attributes.getRequest() : null;
        } catch (Exception e) {
            return null;
        }
    }
} 