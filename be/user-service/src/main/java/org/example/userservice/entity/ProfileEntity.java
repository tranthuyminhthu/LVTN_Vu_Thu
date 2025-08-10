package org.example.userservice.entity;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.FieldDefaults;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.MongoId;

import java.time.LocalDate;
import java.util.List;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
@Document("profile")
public class ProfileEntity {
    @Id
    String userId;
    String phone;
    String email;
    String username;
    String fullName;
    String type;
    String image;
    Integer height;
    Double weight;
    String gender;
    String shopName;
    List<String> role;
    LocalDate dob;
}
