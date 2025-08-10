package org.example.userservice.dto;

import lombok.Data;

import java.time.LocalDate;

@Data
public class ProfileRequestDto {
    private String phone;
    private String fullName;
    private String image;
    private Integer height;
    private Double weight;
    private String gender;
    private LocalDate dob;
}
