package org.example.authservice.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

import java.util.List;
import java.util.Map;

@Data
public class UserDto {
    
    @JsonProperty("id")
    private String id;
    
    @JsonProperty("username")
    private String username;
    
    @JsonProperty("email")
    private String email;
    
    @JsonProperty("firstName")
    private String firstName;
    
    @JsonProperty("lastName")
    private String lastName;
    
    @JsonProperty("enabled")
    private Boolean enabled;
    
    @JsonProperty("emailVerified")
    private Boolean emailVerified;
    
    @JsonProperty("attributes")
    private Map<String, List<String>> attributes;
    
    @JsonProperty("groups")
    private List<String> groups;
    
    @JsonProperty("requiredActions")
    private List<String> requiredActions;
} 