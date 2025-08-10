package org.example.userservice.dao;

import feign.QueryMap;
import org.example.userservice.dto.TokenExchangeParamDto;
import org.example.userservice.dto.TokenExchangeResponseDto;
import org.example.userservice.dto.UserCreationParamDto;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;

@FeignClient(name = "identity-client", url = "${idp.url}")
public interface IdentityClient {

    @PostMapping(value = "/realms/kltn/protocol/openid-connect/token", consumes = MediaType.APPLICATION_FORM_URLENCODED_VALUE)
    TokenExchangeResponseDto exchangeToken(@QueryMap TokenExchangeParamDto tokenExchangeParamDto);

    @PostMapping(value = "/admin/realms/kltn/users", consumes = MediaType.APPLICATION_JSON_VALUE)
    ResponseEntity<?> createUser(@RequestBody UserCreationParamDto userCreationParamDto, @RequestHeader("Authorization") String authorizationHeader);

    @PostMapping(value = "/realms/kltn/protocol/openid-connect/token", consumes = MediaType.APPLICATION_FORM_URLENCODED_VALUE)
    TokenExchangeResponseDto login(@QueryMap TokenExchangeParamDto tokeLnExchangeParamDto);
}
