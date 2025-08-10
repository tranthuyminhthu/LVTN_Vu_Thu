package org.example.userservice.entity;

import lombok.Data;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.time.LocalDateTime;

@Data
@Document("vendors")
public class VendorEntity {
    @Id
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
