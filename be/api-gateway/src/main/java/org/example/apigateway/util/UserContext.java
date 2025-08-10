package org.example.apigateway.util;

import org.springframework.http.HttpHeaders;
import org.springframework.web.server.ServerWebExchange;

public class UserContext {
    public static final String USER_ID_HEADER = "X-User-ID";
    public static final String USER_EMAIL_HEADER = "X-User-Email";
    public static final String USER_NAME_HEADER = "X-User-Name";

    public static String getUserId(ServerWebExchange exchange) {
        return exchange.getRequest().getHeaders().getFirst(USER_ID_HEADER);
    }

    public static String getUserEmail(ServerWebExchange exchange) {
        return exchange.getRequest().getHeaders().getFirst(USER_EMAIL_HEADER);
    }

    public static String getUserName(ServerWebExchange exchange) {
        return exchange.getRequest().getHeaders().getFirst(USER_NAME_HEADER);
    }

    public static boolean hasUserInfo(ServerWebExchange exchange) {
        return getUserId(exchange) != null;
    }
} 