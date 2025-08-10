package org.example.orderservice.dao;

import org.example.orderservice.dto.OrderGHNRequestDto;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;

import java.util.List;
import java.util.Map;

@FeignClient(name = "ghn-client", url = "${service.ghn}")
public interface GHNClient {

    @PostMapping(value = "/v2/shipping-order/create", consumes = "application/json")
    ResponseEntity<Map<Object, Object>> createOrder(@RequestBody OrderGHNRequestDto orderGHNRequestDto, @RequestHeader("Token") String token,
                                  @RequestHeader("ShopId") Integer shopId);
}
