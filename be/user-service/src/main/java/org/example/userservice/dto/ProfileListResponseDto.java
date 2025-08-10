package org.example.userservice.dto;

import lombok.Data;
import org.example.userservice.entity.ProfileEntity;
import java.util.List;

@Data
public class ProfileListResponseDto {
    private List<ProfileEntity> profiles;
    private int page;
    private int size;
    private long totalElements;
    private int totalPages;
} 