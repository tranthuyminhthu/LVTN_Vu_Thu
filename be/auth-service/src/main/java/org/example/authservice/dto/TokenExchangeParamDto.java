package org.example.authservice.dto;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class TokenExchangeParamDto {
    private String grant_type;
    private String client_id;
    private String client_secret;
    private String username;
    private String password;
    private String scope;
    private String refresh_token;
}
