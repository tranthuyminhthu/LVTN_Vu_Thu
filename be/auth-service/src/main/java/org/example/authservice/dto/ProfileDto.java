package org.example.authservice.dto;

import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.util.List;

@Data
@Builder
public class ProfileDto {
    private String userId;
    private String email;
    private String username;
    private String fullName;
    private String type;
    private String image;
    private Integer height;
    private Double weight;
    private String gender;
    private String shopName;
    private List<String> role;
    private LocalDate dob;
}
