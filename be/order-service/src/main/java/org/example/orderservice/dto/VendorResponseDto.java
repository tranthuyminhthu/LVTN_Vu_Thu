package org.example.orderservice.dto;

import lombok.Data;
import java.time.LocalDateTime;

@Data
public class VendorResponseDto {
    private String id;
    private Integer shopId;
    private String name;
    private String description;
    private String address;
    private String phone;
    private String email;
    private String status;
    private String image;
    private String wardName;
    private String districtName;
    private String provinceName;
    private Integer districtId;
    private String wardId;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
} 
