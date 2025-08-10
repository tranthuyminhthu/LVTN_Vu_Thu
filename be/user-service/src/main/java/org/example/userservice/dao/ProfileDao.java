package org.example.userservice.dao;

import org.example.userservice.entity.ProfileEntity;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface ProfileDao extends MongoRepository<ProfileEntity, String> {
    Optional<ProfileEntity> findByUsername(String username);
}
