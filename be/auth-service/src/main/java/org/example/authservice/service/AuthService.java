package org.example.authservice.service;

import feign.FeignException;
import lombok.RequiredArgsConstructor;
import org.example.authservice.dao.IdentityClient;
import org.example.authservice.dto.CredentialDto;
import org.example.authservice.dto.RegistrationDto;
import org.example.authservice.dto.TokenExchangeParamDto;
import org.example.authservice.dto.UserCreationParamDto;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class ProfileService {
    private final IdentityClient identityClient;

    @Value("${idp.client-id}")
    private String clientId;

    @Value("${idp.client-secret}")
    private String clientSecret;

    public String registerAccount(RegistrationDto registrationDto) {
        try {
            // create account in keycloak

            // exchange client token
            TokenExchangeParamDto tokenExchangeParam = TokenExchangeParamDto.builder()
                    .grant_type("client_credentials")
                    .client_id(clientId)
                    .client_secret(clientSecret)
                    .scope("openid")
                    .build();
            String token = identityClient.exchangeToken(tokenExchangeParam).getAccessToken();
            // Create user in keycloak
            ResponseEntity<?> response = identityClient.createUser(
                    UserCreationParamDto.builder()
                            .username(registrationDto.getUsername())
                            .email(registrationDto.getEmail())
                            .firstName("")
                            .lastName("")
                            .enabled(true)
                            .emailVerified(false)
                            .credentials(List.of(
                                    CredentialDto.builder()
                                            .type("password")
                                            .value(registrationDto.getPassword())
                                            .temporary(false)
                                            .build()
                            ))
                            .build(), "Bearer " + token
            );
            // Get userId from keycloak response
            String userId = extractUserId(response);
            // Create user profile in the application database
            return userId;
        } catch (FeignException e) {
            int status = e.status();
            switch (status) {
                case 400 -> throw new IllegalArgumentException("Invalid request data: " + e.getMessage());
                case 409 -> throw new IllegalStateException("User already exists: " + e.getMessage());
                case 500 -> throw new RuntimeException("Internal server error: " + e.getMessage());
                default -> throw new RuntimeException("Unexpected error occurred: " + e.getMessage());
            }
        }
    }

    private String extractUserId(ResponseEntity<?> response) {
        String location = response.getHeaders().getLocation().toString();
        List<String> locationParts = List.of(location.split("/"));
        return locationParts.getLast();
    }

    public LoginResponseDto login(LoginDto loginDto) {
        try {
            // Call Keycloak login endpoint
            TokenExchangeParamDto loginParam = TokenExchangeParamDto.builder()
                    .grant_type("password")
                    .client_id(clientId)
                    .client_secret(clientSecret)
                    .username(loginDto.getUsername())
                    .password(loginDto.getPassword())
                    .scope("openid")
                    .build();
            
            TokenExchangeResponseDto response = identityClient.login(loginParam);
            
            // Get user profile to return additional info
            ProfileEntity profile = profileDao.findByUsername(loginDto.getUsername())
                    .orElseThrow(() -> new IllegalArgumentException("User profile not found"));
            
            return LoginResponseDto.builder()
                    .accessToken(response.getAccessToken())
                    .refreshToken(response.getRefreshToken())
                    .tokenType(response.getTokenType())
                    .expiresIn(Long.parseLong(response.getExpiresIn()))
                    .userId(profile.getUserId())
                    .username(profile.getUsername())
                    .email(profile.getEmail())
                    .build();
                    
        } catch (FeignException e) {
            int status = e.status();
            switch (status) {
                case 400 -> throw new IllegalArgumentException("Invalid credentials: " + e.getMessage());
                case 401 -> throw new IllegalArgumentException("Invalid username or password");
                case 500 -> throw new RuntimeException("Internal server error: " + e.getMessage());
                default -> throw new RuntimeException("Unexpected error occurred: " + e.getMessage());
            }
        }
    }
}
