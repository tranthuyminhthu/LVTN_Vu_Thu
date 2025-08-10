package org.example.userservice.dto;

import lombok.Data;
import org.example.userservice.entity.VendorEntity;
import java.util.List;

@Data
public class VendorListResponseDto {
    private List<VendorEntity> vendors;
    private int page;
    private int size;
    private long totalElements;
    private int totalPages;
} 