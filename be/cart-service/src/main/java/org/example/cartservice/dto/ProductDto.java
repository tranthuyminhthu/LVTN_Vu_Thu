package org.example.cartservice.dto;

import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@NoArgsConstructor
public class ProductDto {
    private Long id;
    private String name;
    private String description;
    private Double price;
    private Double rating;
    private String status;
    private List<String> images;
    private VendorInfoDto vendorInfo;

    @Data
    public static class VendorInfoDto {
        private String id;
        private String name;
        private String image;
    }

}

