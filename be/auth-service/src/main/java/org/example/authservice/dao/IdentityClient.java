package org.example.authservice.dao;

import feign.QueryMap;
import org.example.authservice.dto.*;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@FeignClient(name = "identity-client", url = "${idp.url}")
public interface IdentityClient {

    @PostMapping(value = "/realms/kltn/protocol/openid-connect/token", consumes = MediaType.APPLICATION_FORM_URLENCODED_VALUE)
    TokenExchangeResponseDto exchangeToken(@QueryMap TokenExchangeParamDto tokenExchangeParamDto);

    @PostMapping(value = "/admin/realms/kltn/users", consumes = MediaType.APPLICATION_JSON_VALUE)
    ResponseEntity<?> createUser(@RequestBody UserCreationParamDto userCreationParamDto, @RequestHeader("Authorization") String authorizationHeader);

    @PostMapping(value = "/realms/kltn/protocol/openid-connect/token", consumes = MediaType.APPLICATION_FORM_URLENCODED_VALUE)
    TokenExchangeResponseDto login(@QueryMap TokenExchangeParamDto tokenExchangeParamDto);

    @GetMapping(value = "/admin/realms/kltn/roles", produces = MediaType.APPLICATION_JSON_VALUE)
    ResponseEntity<List<RoleResponseDto>> getRoles(@RequestHeader("Authorization") String authorizationHeader);

    @PostMapping(value = "/admin/realms/kltn/users/{userId}/role-mappings/realm", produces = MediaType.APPLICATION_JSON_VALUE)
    ResponseEntity<List<RoleResponseDto>> assignRole(@RequestHeader("Authorization") String authorizationHeader,
            @RequestBody List<RoleResponseDto> roleResponseDto, @PathVariable String userId);

    @DeleteMapping(value = "/admin/realms/kltn/users/{userId}")
    ResponseEntity<?> deleteUser(@RequestHeader("Authorization") String authorizationHeader, @PathVariable String userId);

    @GetMapping(value = "/admin/realms/kltn/users", produces = MediaType.APPLICATION_JSON_VALUE)
    ResponseEntity<List<UserDto>> getUserByEmail(@RequestHeader("Authorization") String authorizationHeader, @RequestParam("email") String email);

    @PutMapping(value = "/admin/realms/kltn/users/{userId}/reset-password", consumes = MediaType.APPLICATION_JSON_VALUE)
    ResponseEntity<?> updatePassword(@RequestHeader("Authorization") String authorizationHeader, 
                                   @PathVariable String userId,
                                   @RequestParam("currentPassword") String currentPassword,
                                   @RequestParam("newPassword") String newPassword);

    @PostMapping(value = "/admin/realms/kltn/users/reset-password", consumes = MediaType.APPLICATION_JSON_VALUE)
    ResponseEntity<?> resetPassword(@RequestHeader("Authorization") String authorizationHeader,
                                  @RequestParam("token") String token,
                                  @RequestParam("newPassword") String newPassword);

    @PutMapping(value = "/admin/realms/kltn/users/{userId}/reset-password", consumes = MediaType.APPLICATION_JSON_VALUE)
    ResponseEntity<?> adminResetPassword(@RequestHeader("Authorization") String authorizationHeader,
                                       @PathVariable String userId,
                                       @RequestBody Map<String, String> passwordData);
}
