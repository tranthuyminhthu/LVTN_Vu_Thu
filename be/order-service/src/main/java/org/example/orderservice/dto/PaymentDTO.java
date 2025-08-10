package org.example.orderservice.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;

@Builder
@AllArgsConstructor
public class PaymentDTO {
    public String code;
    public String message;
    public String paymentUrl;
}
