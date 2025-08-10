package org.example.authservice.dto;

import lombok.Data;

@Data
public class VendorRequestDto {
    private String id;
    private String name;
    private String description;
    private String address;
    private String phone;
    private String email;
}
