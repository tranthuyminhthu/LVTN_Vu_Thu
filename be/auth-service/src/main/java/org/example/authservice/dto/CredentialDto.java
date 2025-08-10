package org.example.authservice.dto;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class CredentialDto {
    private String type;
    private String value;
    private Boolean temporary;
}
