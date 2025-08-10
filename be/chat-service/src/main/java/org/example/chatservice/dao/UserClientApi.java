package org.example.chatservice.dao;

import org.example.chatservice.dto.ParticipantInfoDto;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestHeader;

@FeignClient(name = "user-client-api", url = "${service.gateway-service}")
public interface UserClientApi {
    @GetMapping(value = "/api/profile/introspect", consumes = MediaType.APPLICATION_JSON_VALUE)
    ResponseEntity<?> introspect(@RequestHeader("Authorization") String authorizationHeader);
}
