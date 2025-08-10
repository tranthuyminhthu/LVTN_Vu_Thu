package org.example.orderservice.dto;

import lombok.Data;

import java.util.List;

@Data
public class CartRequestPayDto {
    private List<Long> cartDetailEntities;
    private Integer userId;
    private String paymentMethod;
    private List<Long> amenityEntities;
}
