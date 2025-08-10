package org.example.userservice.dto;

import lombok.Builder;
import lombok.Data;

import java.util.List;

@Data
@Builder
public class UserCreationParamDto {
    private String username;
    private Boolean enabled;
    private String email;
    private Boolean emailVerified;
    private String firstName;
    private String lastName;
    private List<CredentialDto> credentials;
}
