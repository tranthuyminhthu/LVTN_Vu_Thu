package org.example.productsservice.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ProductApprovalEventDto {
    private String eventType = "PRODUCT_APPROVED";
    private List<String> productIds;
    private String approvedBy;
    private LocalDateTime approvedAt;
    private String message;
    private String vendorId; // ID của vendor sở hữu sản phẩm
    private String vendorName; // Tên vendor (có thể lấy từ profile service)
} 