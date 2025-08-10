package org.example.authservice.dto;

import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

@Data
@NoArgsConstructor
public class RegistrationDto {
    private String email;
    private String username;
    private String password;
    private Boolean isVendor;
}
