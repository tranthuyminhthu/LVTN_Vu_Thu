package org.example.chatservice.dao;

import org.example.chatservice.dto.ParticipantInfoDto;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

@FeignClient(name = "user-client", url = "${service.user-service}")
public interface UserClient {
    @GetMapping(value = "/internal/profile/{userId}", consumes = MediaType.APPLICATION_JSON_VALUE)
    ParticipantInfoDto getUserInfo(@PathVariable("userId") String userId);
}
