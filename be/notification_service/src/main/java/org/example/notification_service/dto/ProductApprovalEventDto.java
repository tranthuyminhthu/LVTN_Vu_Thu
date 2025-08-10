package org.example.notification_service.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ProductApprovalEventDto {
    private String eventType;
    private List<String> productIds;
    private String approvedBy;
    private LocalDateTime approvedAt;
    private String message;
    private String vendorId;
    private String vendorName;
} 